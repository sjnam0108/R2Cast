package kr.co.r2cast.controllers.eco;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import kr.co.r2cast.models.EcoMessageManager;
import kr.co.r2cast.models.Message;
import kr.co.r2cast.models.MessageManager;
import kr.co.r2cast.models.ModelManager;
import kr.co.r2cast.models.eco.Rvm;
import kr.co.r2cast.models.eco.RvmTrx;
import kr.co.r2cast.models.eco.RvmTrxItem;
import kr.co.r2cast.models.eco.service.RvmService;
import kr.co.r2cast.models.eco.service.TrxService;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.service.SiteService;
import kr.co.r2cast.utils.SolUtil;
import kr.co.r2cast.utils.Util;
import kr.co.r2cast.viewmodels.eco.JsonCosmoData;
import kr.co.r2cast.viewmodels.eco.JsonCosmoRoot;

/**
 * 사이트 수준 작업 컨트롤러
 */
@Controller("eco-currsite-task-controller")
@RequestMapping(value="/eco/currsitetask")
public class CurrSiteTaskController {
	private static final Logger logger = LoggerFactory.getLogger(CurrSiteTaskController.class);

    @Autowired 
    private TrxService trxService;

    @Autowired 
    private RvmService rvmService;
    
    @Autowired 
    private SiteService siteService;

    
    @Autowired
	private MessageManager msgMgr;

	@Autowired
	private EcoMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 사이트 수준 작업 페이지
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
    	model.addAttribute("pageTitle", "사이트 수준 작업");

    	
    	model.addAttribute("initReportCosmoDate", Util.toDateString(Util.addDays(Util.removeTimeOfDate(new Date()), -1)));
    	
    	
        return "eco/currsitetask";
    }
	
	/**
	 * 작업 액션: COSMO 서버에 결과 보고(one-site task)
	 */
    @RequestMapping(value = "/reportCosmo", method = RequestMethod.POST)
    public @ResponseBody String reportCosmo(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
		Site site = siteService.getSite(Util.getSessionSiteId(session));
    	Date date = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("date")));
		
		if (site == null || date == null) {
    		throw new ServerOperationForbiddenException(
    				msgMgr.message("common.server.msg.wrongParamError", locale));
		}

		
    	Rvm dstRvm = null;
    	String rvmName = Util.parseString((String)model.get("rvmName"));
    	if (Util.isValid(rvmName)) {
    		dstRvm = rvmService.getRvm(site, rvmName);
    	}
    	
    	List<RvmTrx> list = null;
    	if (dstRvm == null) {
    		// 날짜만 제공된 경우: 해당일의 모든 RVM(결과 보고 == c1) 대상
    		list = trxService.getRvmTrxListBySiteIdOpDate(site.getId(), date);
    	} else {
    		// 대상 RVM의 해당일 자료 대상(RVM의 결과 보고 == c1인 경우에만 진행)
    		list = trxService.getRvmTrxListBySiteIdOpDateRvmId(site.getId(), date, dstRvm.getId());
    	}
    	
    	if (list == null) {
    		// 발생될 수 없는 오류이나, 방어 차원에서
    		throw new ServerOperationForbiddenException(
    				msgMgr.message("common.server.msg.wrongParamError", locale));
    	}
    	
		
		// Cosmo에서 정한 병당 단가 자료 확인
		//   자료 형식) 40:211|50:311|70:131|100:231|130:331|350:431
		HashMap<String, String> cosmoPriceHm = new HashMap<String, String>();
		
		List<String> prices = Util.tokenizeValidStr(Util.getFileProperty("cosmo.priceCodes"));
		for(String price : prices) {
			List<String> pair = Util.tokenizeValidStr(price, ":");
			if (pair.size() == 2) {
				if (!cosmoPriceHm.containsKey("P" + pair.get(0))) {
					cosmoPriceHm.put("P" + pair.get(0), pair.get(1));
				}
			}
		}
    	if (cosmoPriceHm.size() == 0) {
    		throw new ServerOperationForbiddenException("Cosmo 보고를 위한 병 단가 설정이 필요합니다.");
    	}
    	
    	String cosmoServerUrl = Util.getFileProperty("url.cosmo");
    	if (Util.isNotValid(cosmoServerUrl)) {
    		throw new ServerOperationForbiddenException("Cosmo 서버 URL 설정이 필요합니다.");
    	}
    	
    	
    	// 정렬 순서 확인:
    	//   > 동일 날짜 내, rvmId, TrxNo ASC 로 Trx sort
    	//   > 동일 Trx 내, 반환금액 ASC로 신규 TrxItem(반환금액별 grouping) sort
    	//
		Collections.sort(list, new Comparator<RvmTrx>() {
	    	public int compare(RvmTrx trx1, RvmTrx trx2) {
	    		if (trx1.getRvm().getId() == trx2.getRvm().getId()) {
		    		return Integer.compare(trx1.getTrxNo(), trx2.getTrxNo());
	    		} else {
		    		return Integer.compare(trx1.getRvm().getId(), trx2.getRvm().getId());
	    		}
	    	}
	    });
		
		
		//int seq = 0;
		try {
			ArrayList<JsonCosmoData> realCosmoList = new ArrayList<JsonCosmoData>();
			
			for(RvmTrx trx : list) {
				
				if (Util.isNotValid(trx.getRvm().getResultType()) || !trx.getRvm().getResultType().equals("c1")) {
					continue;
				}
				
				List<RvmTrxItem> itemList = trxService.getRvmTrxItemListByTrxId(trx.getSite().getId(), trx.getId());
				//logger.info("seq#: " + (seq++) + ", Trx id: " + trx.getId() + ", Trx No: " + trx.getTrxNo() + ", init size: " + itemList.size());

				// Cosmo 가격 항목에 대해 미리 개체를 모두 생성한 후, iteration 중에 qty를 증가
				List<JsonCosmoData> cosmoList = getJsonCosmoList(trx, cosmoPriceHm);
				for(RvmTrxItem trxItem : itemList) {
					for(JsonCosmoData jObj : cosmoList) {
						if (jObj.getAmount() == trxItem.getAmount()) {
							jObj.setQty(jObj.getQty() + 1);
						}
					}
					//logger.info("     - " + trxItem.getAmount());
				}
				
				int siblingSeq = 0;
				for(JsonCosmoData jObj : cosmoList) {
					if (jObj.getQty() > 0) {
						jObj.setSibling_seq(++siblingSeq);
						jObj.setDeposit_value(jObj.getAmount() * jObj.getQty());
						
						realCosmoList.add(jObj);
					}
				}
				
			}
			
			JsonCosmoRoot jObj = new JsonCosmoRoot();
			jObj.setData(realCosmoList);

			
			String filename = site.getShortName() + "_" + Util.toSimpleString(date, "yyyyMMdd");
			if (dstRvm != null) {
				filename += "_" + dstRvm.getRvmName();
			}
			String jsonPathFile = SolUtil.getPhysicalRoot("Cosmo") + "/" + filename + "_" + Util.toSimpleString(new Date(), "yyyyMMdd_HHmmss") + ".json";
			
			String jsonStr = Util.getObjectToJson(jObj, true);
			
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(jsonPathFile), "UTF-8"));
            
            output.write(jsonStr);
            output.close();
			
            int respCode = SolUtil.sendStreamToServer(cosmoServerUrl, "application/json; charset=UTF-8", jsonStr);
            if (respCode != 200) {
        		throw new ServerOperationForbiddenException("OperationError");
            }

		} catch (Exception e) {
    		logger.error("reportCosmo", e);
    		throw new ServerOperationForbiddenException("OperationError");
		}

        return "OK";
    }
    
    private List<JsonCosmoData> getJsonCosmoList(RvmTrx trx, HashMap<String, String> cosmoPriceHm) {
    	
    	ArrayList<JsonCosmoData> list = new ArrayList<JsonCosmoData>();
    	
    	List<String> keys = new ArrayList<String>(cosmoPriceHm.keySet());
    	for(String key : keys) {
    		list.add(new JsonCosmoData(trx, cosmoPriceHm.get(key), Util.parseInt(key.substring(1))));
    	}
		Collections.sort(list, new Comparator<JsonCosmoData>() {
	    	public int compare(JsonCosmoData obj1, JsonCosmoData obj2) {
	    		return Integer.compare(obj1.getAmount(), obj2.getAmount());
	    	}
	    });
    	
    	return list;
    }

}
