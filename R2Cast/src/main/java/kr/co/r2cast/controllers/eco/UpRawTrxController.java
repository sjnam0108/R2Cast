package kr.co.r2cast.controllers.eco;


import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.co.r2cast.exceptions.ServerOperationForbiddenException;
import kr.co.r2cast.info.EcoGlobalInfo;
import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.EcoMessageManager;
import kr.co.r2cast.models.Message;
import kr.co.r2cast.models.MessageManager;
import kr.co.r2cast.models.ModelManager;
import kr.co.r2cast.models.eco.MonTask;
import kr.co.r2cast.models.eco.Rvm;
import kr.co.r2cast.models.eco.RvmTrx;
import kr.co.r2cast.models.eco.RvmTrxItem;
import kr.co.r2cast.models.eco.service.MonitoringService;
import kr.co.r2cast.models.eco.service.RvmService;
import kr.co.r2cast.models.eco.service.TrxService;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.service.SiteService;
import kr.co.r2cast.utils.SolUtil;
import kr.co.r2cast.utils.Util;
import kr.co.r2cast.viewmodels.eco.UploadFileItem;

/**
 * 원시 트랜잭션 파일 컨트롤러
 */
@Controller("eco-up-raw-trx-controller")
@RequestMapping(value="/eco/uprawtrx")
public class UpRawTrxController {
	private static final Logger logger = LoggerFactory.getLogger(UpRawTrxController.class);

    @Autowired 
    private SiteService siteService;

    @Autowired 
    private RvmService rvmService;
    
    @Autowired
    private TrxService trxService;
    
    @Autowired
    private MonitoringService monService;

	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private EcoMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
    
	/**
	 * 원시 트랜잭션 파일 페이지
	 */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String index(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	modelMgr.addMainMenuModel(model, locale, session, request);
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {
					
    			});

    	// 페이지 제목
    	model.addAttribute("pageTitle", "원시 트랜잭션 파일");

    	String lastImportDt = Util.toSimpleString(EcoGlobalInfo.LastTrxImportDt);
    	model.addAttribute("lastImportDt", Util.isNotValid(lastImportDt) ? "N/A" : lastImportDt);

    	
    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
    	
        return "eco/uprawtrx";
    }
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody List<UploadFileItem> read(@RequestBody DataSourceRequest request, 
    		HttpSession session, Locale locale) {
    	
    	Site site = siteService.getSite(Util.getSessionSiteId(session));
    	
    	ArrayList<UploadFileItem> list = new ArrayList<UploadFileItem>();
		if (site == null) {
    		throw new ServerOperationForbiddenException(msgMgr.message("common.server.msg.wrongParamError", locale));
		} else {
	    	try {
				String typeRootDir = SolUtil.getPhysicalRoot("Trx");
				File dir = new File(typeRootDir);
				final String extName = SolUtil.getFileSearchPattern("Trx");
				if (dir.exists() && dir.isDirectory()) {
					File[] files = dir.listFiles(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String name) {
							return extName.equals("*") ||  name.toLowerCase().endsWith(extName);
						}
					});
					String prefix = site.getShortName() +"_"; 
					//
					// 업로드 파일 형식: {siteShortName}_{rvmId}_Trx_{트랜잭션번호:000000x}_{대상일:yyyyMMdd}_{시간:HHmmss}.xml
					// 형식의 예: r2cast_3_Trx_0000202_20221019_134555.xml
					//
					for(File file : files) {
						if (file.getName().startsWith(prefix)) {
							List<String> tokens = Util.tokenizeValidStr(Util.removeFileExt(file.getName().substring(prefix.length())), "_");
							if (tokens.size() == 5 && tokens.get(1).equals("Trx") && tokens.get(3).length() == 8) {
								Rvm rvm = rvmService.getRvm(Util.parseInt(tokens.get(0)));
								
								if (rvm != null ) {	
									list.add(new UploadFileItem(file.getName(), file.length(), file.lastModified(), 
											rvm.getRvmName(), Util.delimitDateStr(tokens.get(3)),
											rvm.getId()));
								}
							}
						}
					}
				}
	    	} catch (Exception e) {
	    		logger.error("read", e);
	    		throw new ServerOperationForbiddenException("readError");
	    	}
		}
		
		return list;
    }
    
	/**
	 * 삭제 액션
	 */	
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model, Locale locale) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
		try {
			String rootDir = SolUtil.getPhysicalRoot("Trx");
			
	    	for (Object pathName : objs) {
	    		File file = new File(rootDir + ((String) pathName));
	
	    		if (file.exists() && file.isFile()) {
	    			file.delete();
	    		}
	    	}
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "OK";
    }
    
	/**
	 * 임포트 액션
	 */
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public @ResponseBody String importRawTrx(@RequestBody Map<String, Object> model, Locale locale,HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
		try {
			boolean error = false;
			String rootDir = SolUtil.getPhysicalRoot("Trx");
	    	for (Object pathName : objs) {
	    		File file = new File(rootDir + ((String) pathName));
				
				//
				// 업로드 파일 형식: {siteShortName}_{rvmId}_Trx_{트랜잭션번호:000000x}_{대상일:yyyyMMdd}_{시간:HHmmss}.xml
				// 형식의 예: r2cast_3_Trx_0000202_20221019_134555.xml
				//
				if (file.isDirectory()) {
					continue;
				}
				
		    	Site site = siteService.getSite(Util.getSessionSiteId(session));
				if (site == null) {
					continue;
				}
				
				String prefix = site.getShortName() +"_";
				String remain = file.getName().substring(prefix.length()).replace(".xml", "");
				if (remain.length() > 9 && remain.indexOf("_") > 0) {
					String rvmIdStr = remain.substring(0, remain.indexOf("_") );
					String dateStr = remain.substring(14,remain.indexOf("_") + 21);
					
					if (Util.isIntNumber(rvmIdStr)) {
						Rvm rvm = rvmService.getRvm(Integer.parseInt(rvmIdStr));
						if (rvm != null) {
							Date opDate = Util.parseDate(dateStr);
							
							if (importRawTrxFile(rvm, file, opDate)) {
					        	EcoGlobalInfo.LastTrxImportDt = new Date();
					        	logger.info("importRawTrx - success: " + file.getName());
							} else {
					        	logger.info("importRawTrx - failed: " + file.getName());
					        	error = true;
							}
						}
					}
				} else {
		        	logger.info("importRawTrx - wrong file: " + file.getName());
				}
	    	}
	    	
	    	if (error) {
	    		throw new ServerOperationForbiddenException("OperationError");
	    	}
    	} catch (Exception e) {
    		logger.error("importRawTrx", e);
    		throw new ServerOperationForbiddenException("OperationError");
    	}

        return "OK";
    }
    
	@SuppressWarnings("unchecked")
	private boolean importRawTrxFile(Rvm rvm, File file, Date opDate) {

		String logStep = "";
		
		try {
			if (rvm == null || file == null || !file.exists() ||
					!file.isFile()) {
				return false;
			}
			
			String trxContent = Util.getFileContent(file.getAbsolutePath());
			if (Util.isNotValid(trxContent)) {
    			try {
					String errPath = SolUtil.getPhysicalRoot("TrxError");
					Util.checkDirAndCopyFile(file, errPath);
					file.delete();
    			} catch (Exception e2) { }

				return false;
			}
			
			// Step 0. 전달 인자 검증 완료
			logStep += "0";
			
			Document document  = null;
			
			try {
				document  = DocumentHelper.parseText(trxContent);
			} catch (Exception e1) {
	    		logger.error("importRawTrxFile(Wrong file format)", e1);
	    		logger.error("logStep: " + logStep);
	    		
    			try {
					String errPath = SolUtil.getPhysicalRoot("TrxError");
					Util.checkDirAndCopyFile(file, errPath);
					file.delete();
    			} catch (Exception e2) { }

    			return false;
			}
			
			
			Node rootNode = document.selectSingleNode("/Data");
			if (rootNode == null) {
				return false;
			}

			
			// Step 1. 유효 자료 검증 - Trx (1)
			logStep += ".1";
			
			Node dateTimeNode = rootNode.selectSingleNode("DateTime");
			Node trxNoNode = rootNode.selectSingleNode("TransactionNo");
			Node receiptNoNode = rootNode.selectSingleNode("ReceiptNo");
			Node overallAmountNode = rootNode.selectSingleNode("OverallAmount");
			Node barcodesNode = rootNode.selectSingleNode("Barcodes");
			
			if (dateTimeNode == null || trxNoNode == null || receiptNoNode == null ||
					overallAmountNode == null || barcodesNode == null) {
				
				return false;
			}

			
			// Step 2. 유효 자료 검증 - Trx (2)
			logStep += ".2";
			
			Date dt = null;
			try {
				dt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(dateTimeNode.getText());
			} catch (Exception e3) { }
			
			int trxNo = Util.parseInt(trxNoNode.getText());
			int receiptNo = Util.parseInt(receiptNoNode.getText());
			int overallAmount = Util.parseInt(overallAmountNode.getText());
			
			String barcodes = barcodesNode.getText();

			if (dt == null || trxNo < 0 || receiptNo < 0 || overallAmount < 0) {
				return false;
			}
			
			List<Node> itemList = rootNode.selectNodes("Items/Item");
			// size() == 0인 자료도 등록시킴
			//if (itemList.size() == 0) {
			//	return false;
			//}
			
			
			// Step 3. Trx 기 자료 삭제 및 Trx 신규 생성
			logStep += ".3";
			
			RvmTrx rvmTrx = trxService.getRvmTrx(rvm, dt);
			if (rvmTrx != null) {
				trxService.deleteRvmTrx(rvmTrx);
			}
			
			rvmTrx = new RvmTrx(rvm, dt, trxNo, receiptNo, overallAmount, barcodes);
			trxService.saveOrUpdate(rvmTrx);
			
			
			// Step 4. TrxItem 생성
			logStep += ".4";
			
			for(Node node : itemList) {
				
				Node groupIdNode = node.selectSingleNode("GroupId");
				Node countNode = node.selectSingleNode("Count");
				Node amountNode = node.selectSingleNode("Amount");
				Node vatNode = node.selectSingleNode("Vat");
				Node typeNode = node.selectSingleNode("Type");
				Node barcodeNode = node.selectSingleNode("Barcode");
				Node timeNode = node.selectSingleNode("Time");
				Node emptiesTypeNode = node.selectSingleNode("EmptiesType");
				
				if (groupIdNode == null || countNode == null || amountNode == null || vatNode == null ||
						typeNode == null || barcodeNode == null || timeNode == null || emptiesTypeNode == null) {
					// 무효 자료는 패스
					continue;
				}
				
				int groupId = Util.parseInt(groupIdNode.getText());
				int count = Util.parseInt(countNode.getText());
				int amount = Util.parseInt(amountNode.getText());
				int vat = Util.parseInt(vatNode.getText());
				
				String type = Util.parseString(typeNode.getText());
				String barcode = Util.parseString(barcodeNode.getText());
				String time = Util.parseString(timeNode.getText());
				String emptiesType = Util.parseString(emptiesTypeNode.getText());
				
				if (groupId < 0 || count < 0 || amount < 0 || vat < 0) {
					// 무효 자료는 패스
					continue;
				}
				
				trxService.saveOrUpdate(new RvmTrxItem(rvmTrx, groupId, count, amount, vat, 
						type, barcode, time, emptiesType));
			}
			
			
			// Step 5. 임포트 성공한 자료 백업
			logStep += ".5";
			
			String backupPath = SolUtil.getPhysicalRoot("TrxBackup");
			Util.checkDirAndCopyFile(file, backupPath);
			file.delete();
			
			
			// Step 6. 기기에 존재하는 원시 트랜잭션 파일 삭제 명령 등록
			logStep += ".6";
			
			String command = "DeleteTrxFile.bbmc";
			String params = "file=" + file.getName().substring(file.getName().indexOf("Trx"));
			
			monService.saveOrUpdate(new MonTask(rvm.getSite(), rvm, command, 
					params, null, "1", new Date(), SolUtil.getMaxTaskDate(), null, null, null));
			
			monService.checkRvmRemoteControlTypeAndLastReportTime(rvm, command);
			
			
			// Step 999. 마무리
			logStep += ".End";
			
			return true;
			
    	} catch (Exception e) {
    		logger.error("importRawTrxFile", e);
    		logger.error("logStep: " + logStep);
    	}
		
		return false;
	}
	
}
