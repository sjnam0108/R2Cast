package kr.co.r2cast.controllers;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import kr.co.r2cast.info.EcoGlobalInfo;
import kr.co.r2cast.info.GlobalInfo;
import kr.co.r2cast.models.CustomComparator;
import kr.co.r2cast.models.eco.MonTask;
import kr.co.r2cast.models.eco.Rvm;
import kr.co.r2cast.models.eco.RvmTrx;
import kr.co.r2cast.models.eco.RvmTrxItem;
import kr.co.r2cast.models.eco.service.MonitoringService;
import kr.co.r2cast.models.eco.service.OptService;
import kr.co.r2cast.models.eco.service.RvmService;
import kr.co.r2cast.models.eco.service.TrxService;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.service.SiteService;
import kr.co.r2cast.utils.SolUtil;
import kr.co.r2cast.utils.Util;
import kr.co.r2cast.utils.WebSocketUtil;

@Component
public class EcoStartupHouseKeeper implements ApplicationListener<ContextRefreshedEvent> {
	private static final Logger logger = LoggerFactory.getLogger(EcoStartupHouseKeeper.class);

    @Autowired 
    private MonitoringService monService;

	@Autowired 
    private RvmService rvmService;

	@Autowired 
    private SiteService siteService;

    @Autowired 
    private OptService optService;
    
    @Autowired
    private TrxService trxService;

//    @Autowired 
//    private NocService nocService;

//    @Autowired 
//    private DsgService dsgService;

	private static Timer bgJob1Timer;
	private static Timer bgJob2Timer;
	private static Timer bgJob3Timer;
	private static Timer bgJob4Timer;

	private static Timer bgWolJobTimer;
	private static Timer bgStatusCalcTimer;
	
	private static Timer bgCleaningAutomationHelpTimer;

	private static Timer bgCustomJobTimer;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		String appId = event.getApplicationContext().getId();
		logger.info("Enter onApplicationEvent() - id=" + appId);
		
		if (!appId.equals("org.springframework.web.context.WebApplicationContext:/" + GlobalInfo.AppId)) {
			return;
		}
		

		boolean bgJob1Enabled = Util.getBooleanFileProperty("backgroundJob.enabled");
		
		boolean bgMainServerMode = Util.getBooleanFileProperty("backgroundJob.mainServerMode");

		
		//
		// 역할: 원시 트랜잭션 파일 to 데이터베이스 임포트
		// 주기: 구동 즉시부터 5 분 단위
		// 
		if (bgJob1Enabled && bgJob1Timer == null) {
			bgJob1Timer = new Timer();
			bgJob1Timer.scheduleAtFixedRate(new TimerTask() {
		    	   public void run()
		 	       {
		    	      doBackgroundJob();
			       }
		        }, 0, 5 * (60 * 1000));
		}

		//
		// 역할: 사이트 수준 기기 상태별 운행 자료 계산
		// 주기: 구동 1분 후부터 1 분 단위
		// 
		if (bgMainServerMode && bgStatusCalcTimer == null) {
			bgStatusCalcTimer = new Timer();
			bgStatusCalcTimer.scheduleAtFixedRate(new TimerTask() {
		    	   public void run()
		 	       {
		    		   doBackgroundJobStatusCalc();
			       }
		        }, 1 * (60 * 1000), 1 * 60 * 1000);
		}
	}
	
	public void doBackgroundJob() {
		int fileCounts = Util.getIntFileProperty("backgroundJob.procCountPerOneCyle");
		
		if (fileCounts < 1 || fileCounts >= 100) {
			fileCounts = 100;
		}

		doRawTrxFileTask(fileCounts);
	}
	
	public void doBackgroundJobStatusCalc() {
		
		try {
			
	        List<Site> allSites = siteService.getSiteList();
	        ArrayList<Rvm> status0Rvms = new ArrayList<Rvm>();
	        ArrayList<Rvm> status2Rvms = new ArrayList<Rvm>();
	        
	        for(Site site : allSites) {
	        	String autoRvmStatus = optService.getSiteOption(site.getId(), "auto.rvmStatus");
	        	
	        	status0Rvms.clear();
	        	status2Rvms.clear();
	        	
	        	if (Util.isValid(autoRvmStatus) && autoRvmStatus.equals("Y")) {
	        		Date stateDate = Util.removeSecTimeOfDate(new Date());
	        		
	    			List<Rvm> rvmList = monService.getMonValCalcedSiteRvmList(site.getId(), true, true);
	        		
	    			int status6 = 0;
	    			int status5 = 0;
	    			int status4 = 0;
	    			int status3 = 0;
	    			int status2 = 0;
	    			int status0 = 0;
	    			
	    			long runningTime = 0;
	    			
	    			for(Rvm rvm : rvmList) {
	    				runningTime += (long)rvm.getRunningMinCount();

	    				switch (rvm.getLastStatus()) {
	    				case "6":
	    					status6 ++;
	    					break;
	    				case "5":
	    					status5 ++;
	    					break;
	    				case "4":
	    					status4 ++;
	    					break;
	    				case "3":
	    					status3 ++;
	    					break;
	    				case "2":
	    					status2Rvms.add(rvm);
	    					status2 ++;
	    					break;
	    				case "0":
	    					status0Rvms.add(rvm);
	    					status0 ++;
	    					break;
	    				}
	    			}
	    			
	    			monService.saveOrUpdateRecStatusStat(site, stateDate, status6, status5, 
	    					status4, status3, status2, status0, runningTime);
	    			monService.saveOrUpdateRecDailySummary(site, stateDate, 
	    					(status6 + status5 + status4 + status3 + status2),
	    					(status6 + status5 + status4 + status3 + status2 + status0),
	    					runningTime, "N");
	    			
	    			WebSocketUtil.setSiteRvmStatus(site.getId(), status6, status5, status4, 
	    					status3, status2, status0);
	        	}
	        }
	        
    	} catch (Exception e) {
    		logger.info("doBackgroundJobStatusCalc", e);
    	}
	}
	
	private int doRawTrxFileTask(int count) {
		// return value:
		//     -1: 오류, 0: 패스, 1: 성공
		
		if (count < 1) {
			return 0;
		}
		
		int cnt = 0;
		
		logger.info("doRawTrxFileTask called with count " + count);
		
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
				
				// files sorting
				List<File> fileList = Arrays.asList(files);
				Collections.sort(fileList, CustomComparator.FileAdTrackFileComparator);
				

				//
				// 업로드 파일 형식: {siteShortName}_{rvmId}_Trx_{트랜잭션번호:000000x}_{대상일:yyyyMMdd}_{시간:HHmmss}.xml
				// 형식의 예: r2cast_3_Trx_0000202_20221019_134555.xml
				//
				for(File file : fileList) {
					if (file.isDirectory()) {
						continue;
					}
					
					String siteID = file.getName().substring(0, file.getName().indexOf("_"));
					Site site = siteService.getSite(siteID);
					if (site == null) {
						continue;
					}
					
					String prefix = site.getShortName() +"_";
					if (file.getName().startsWith(prefix)) {
						List<String> tokens = Util.tokenizeValidStr(Util.removeFileExt(file.getName().substring(prefix.length())), "_");
						if (tokens.size() == 5 && tokens.get(1).equals("Trx") && tokens.get(3).length() == 8) {
							Rvm rvm = rvmService.getRvm(Util.parseInt(tokens.get(0)));
							
							if (rvm != null ) {
								if (importRawTrxFile(rvm, file, Util.parseDate(tokens.get(3)))) {
						        	EcoGlobalInfo.LastTrxImportDt = new Date();
						        	logger.info("importRawTrx - success: " + file.getName());
						        	cnt ++;
						        	if (cnt >= count) {
						        		return 1;
						        	}
								} else {
						        	logger.info("importRawTrx - failed: " + file.getName());
								}
							}
							
						} else {
				        	logger.info("Auto RawTrx Import - wrong file: " + file.getName());
						}
					} else {
			        	logger.info("Auto RawTrx Import - wrong file: " + file.getName());
					}
				}
			}
		} catch (Exception e) {
			logger.error("doRawTrxFileTask", e);
			return -1;
		}
		
		return 0;
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
			if (itemList.size() == 0) {
				return false;
			}
			
			
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
