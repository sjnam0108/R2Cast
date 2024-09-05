package kr.co.r2cast.controllers.eco;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.EcoMessageManager;
import kr.co.r2cast.models.ExcelDownloadView;
import kr.co.r2cast.models.Message;
import kr.co.r2cast.models.MessageManager;
import kr.co.r2cast.models.ModelManager;
import kr.co.r2cast.models.eco.Rvm;
import kr.co.r2cast.models.eco.RvmTrx;
import kr.co.r2cast.models.eco.service.RvmService;
import kr.co.r2cast.models.eco.service.TrxService;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.service.SiteService;
import kr.co.r2cast.utils.Util;
import kr.co.r2cast.viewmodels.eco.RvmTrxDailyResult;
import kr.co.r2cast.viewmodels.eco.RvmTrxDailySummaryResult;

/**
 * 수거일지 컨트롤러
 */
@Controller("eco-daily-collect-controller")
@RequestMapping(value="/eco/dailycollect")
public class DailyCollectController {
	private static final Logger logger = LoggerFactory.getLogger(RecPeriodController.class);

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
	 * 수거일지 페이지
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
    	model.addAttribute("pageTitle", "수거일지");
    	
    	
        return "eco/dailycollect";
    }
    
	/**
	 * 수거일지 요약 정보 읽기 액션
	 */
	@RequestMapping(value = "/readDailyResult", method = RequestMethod.POST)
    public @ResponseBody RvmTrxDailySummaryResult readRvmTrxResult(@RequestBody DataSourceRequest request,
    		Locale locale, HttpSession session) {
		
		Site site = siteService.getSite(Util.getSessionSiteId(session));
		
    	Date date = Util.parseZuluTime(request.getReqStrValue1());
    	
    	if (date == null || site == null) {
        	throw new ServerOperationForbiddenException(
        			msgMgr.message("common.server.msg.wrongParamError", locale));
    	}
    	
    	date = Util.removeTimeOfDate(date);
    	
    	
    	int rvmCount = 0;
    	int receiptCount = 0;
    	int receiptAmout = 0;
    	int emptiesCount = 0;
    	int emptiesTypeCount = 0;
    	
    	List<Object[]> trxData = trxService.getDailyRvmTrxListBySiteIdOpDates(site.getId(), date, date);
    	if (trxData != null && trxData.size() == 1) {
    		receiptCount = ((Long)trxData.get(0)[1]).intValue();
    		receiptAmout = ((Long)trxData.get(0)[2]).intValue();
    		rvmCount = ((Long)trxData.get(0)[3]).intValue();
    		
    		List<Object[]> trxItemData = trxService.getDailyRvmTrxItemListBySiteIdOpDates(site.getId(), date, date);
    		if (trxItemData != null && trxItemData.size() == 1) {
    			emptiesCount = ((Long)trxItemData.get(0)[1]).intValue();
    			emptiesTypeCount = ((Long)trxItemData.get(0)[2]).intValue();
    		}
    	}
    	
    	return new RvmTrxDailySummaryResult(date, rvmCount, receiptCount, receiptAmout, 
    			emptiesCount, emptiesTypeCount);
    }

	/**
	 * 읽기 액션
	 */
	@RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody List<RvmTrxDailyResult> read(@RequestBody DataSourceRequest request, 
    		Locale locale, HttpSession session) {
		
		Site site = siteService.getSite(Util.getSessionSiteId(session));
		
    	Date date = Util.parseZuluTime(request.getReqStrValue1());
    	
    	if (date == null || site == null) {
        	throw new ServerOperationForbiddenException(
        			msgMgr.message("common.server.msg.wrongParamError", locale));
    	}
    	
    	return getTrxData(site.getId(), date);
    }
    
	/**
	 * 수거 자료 획득
	 */
	private List<RvmTrxDailyResult> getTrxData(int siteId, Date date) {
		
    	HashMap<String, RvmTrxDailyResult> rvmHm = new HashMap<String, RvmTrxDailyResult>();
    	List<Rvm> rvmList = rvmService.getRvmListBySiteId(siteId, true);
    	for (Rvm rvm : rvmList) {
    		rvmHm.put("R" + rvm.getId(), new RvmTrxDailyResult(rvm));
    	}
    	
    	List<Object[]> trxData = trxService.getDailyRvmTrxListBySiteIdOpDate(siteId, date);
    	for(Object[] obj : trxData) {
    		String key = "R" + ((Integer)obj[0]).intValue();
    		if (rvmHm.containsKey(key)) {
    			RvmTrxDailyResult row = rvmHm.get(key);
    			row.setReceiptCount(((Long)obj[1]).intValue());
    			row.setReceiptAmount(((Long)obj[2]).intValue());
    			
    			RvmTrx rvmTrx = trxService.getRvmTrx(((Integer)obj[3]).intValue());
    			if (rvmTrx != null) {
    				row.setLastReceiptNo(rvmTrx.getReceiptNo());
    			}
    		}
    	}
    	
    	List<Object[]> trxItemData = trxService.getDailyRvmTrxItemListBySiteIdOpDate(siteId, date);
    	for(Object[] obj : trxItemData) {
    		String key = "R" + ((Integer)obj[0]).intValue();
    		if (rvmHm.containsKey(key)) {
    			RvmTrxDailyResult row = rvmHm.get(key);
    			row.setEmptiesCount(((Long)obj[1]).intValue());
    			row.setEmptiesTypeCount(((Long)obj[2]).intValue());
    		}
    	}
    	
    	
    	List<RvmTrxDailyResult> list = new ArrayList<RvmTrxDailyResult>(rvmHm.values());
    	Collections.sort(list, new Comparator<RvmTrxDailyResult>() {
        	public int compare(RvmTrxDailyResult item1, RvmTrxDailyResult item2) {
        		if(item1.getReceiptCount() == item2.getReceiptCount()) {
        			return item1.getRvmName().compareTo(item2.getRvmName());
        		} else {
        			return Integer.compare(item2.getReceiptCount(), item1.getReceiptCount());
        		}
        	}
    	});
    	

		return list;
	}
    
	/**
	 * 엑셀 다운로드 액션
	 */
    @RequestMapping(value = "/excel", method = RequestMethod.POST)
    public @ResponseBody String excel(@RequestBody DataSourceRequest request, 
    		HttpServletRequest req, HttpServletResponse res, Locale locale, HttpSession session) {
		
		Site site = siteService.getSite(Util.getSessionSiteId(session));
		
    	Date date = Util.parseZuluTime(request.getReqStrValue1());
    	
    	if (date == null || site == null) {
        	throw new ServerOperationForbiddenException(
        			msgMgr.message("common.server.msg.wrongParamError", locale));
    	}

    	ArrayList<ArrayList<String>> excelData = new ArrayList<ArrayList<String>>();
    	
    	try {
    		List<RvmTrxDailyResult> list = getTrxData(site.getId(), date);
        	
        	ArrayList<String> excelRow = new ArrayList<String>();
        	excelRow.add("id");
        	excelRow.add("RVM명");
        	excelRow.add("영수증 건수");
        	excelRow.add("수거 용기수");
        	excelRow.add("영수증 총액");
        	excelRow.add("마지막 영수증");
        	excelRow.add("유형수");
        	
        	
        	excelData.add(excelRow);
        	
        	for(RvmTrxDailyResult row : list) {
        		excelRow = new ArrayList<String>();
        		
        		excelRow.add(String.valueOf(row.getId()));
        		excelRow.add(row.getRvmName());
        		excelRow.add(String.valueOf(row.getReceiptCount()));
        		excelRow.add(String.valueOf(row.getEmptiesCount()));
        		excelRow.add(String.valueOf(row.getReceiptAmount()));
        		excelRow.add(String.valueOf(row.getLastReceiptNo()));
        		excelRow.add(String.valueOf(row.getEmptiesTypeCount()));
            	
            	excelData.add(excelRow);
        	}
    	} catch (Exception e) {
    		logger.error("excel", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    	
    	ExcelDownloadView.makeExcelData("DailyCollectionData", excelData, req, res);
    	
    	return "OK";
    }
}
