package kr.co.r2cast.controllers.eco;

import java.text.DecimalFormat;
import java.util.ArrayList;
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
import kr.co.r2cast.models.eco.service.RvmService;
import kr.co.r2cast.models.eco.service.TrxService;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.service.SiteService;
import kr.co.r2cast.utils.Util;
import kr.co.r2cast.viewmodels.eco.RvmTrxSummayResult;

/**
 * 기간 차트 컨트롤러
 */
@Controller("eco-rec-period-controller")
@RequestMapping(value="/eco/recperiod")
public class RecPeriodController {
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
	 * 기간 차트 페이지
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
    	model.addAttribute("pageTitle", "기간 차트");
    	
    	
        return "eco/recperiod";
    }
    
	/**
	 * 기간 차트 정보 읽기 액션
	 */
	@RequestMapping(value = "/readTrxResult", method = RequestMethod.POST)
    public @ResponseBody RvmTrxSummayResult readRvmTrxResult(@RequestBody DataSourceRequest request,
    		Locale locale, HttpSession session) {
		
		Site site = siteService.getSite(Util.getSessionSiteId(session));
		
    	Date date = Util.parseZuluTime(request.getReqStrValue1());
    	int rvmId = -1;
    	
    	String rvmName = request.getReqStrValue2();
    	if (Util.isValid(rvmName)) {
    		Rvm rvm = rvmService.getRvm(site, rvmName);
    		if (rvm != null) {
    			rvmId = rvm.getId();
    		}
    	}
    	
    	if (date == null || site == null) {
        	throw new ServerOperationForbiddenException(
        			msgMgr.message("common.server.msg.wrongParamError", locale));
    	}

    	try {
    		return getTrxData(site.getId(), date, rvmId, session);
    	} catch (Exception e) {
    		logger.error("readStbServiceResult", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
	/**
	 * 수거 자료 획득
	 */
    private RvmTrxSummayResult getTrxData(int siteId, Date date, int rvmId, HttpSession session) {
    	
    	RvmTrxSummayResult result = new RvmTrxSummayResult(date);
    	int maxRvmCount = 0;
		
		if (rvmId != -1) {
			result.setRvmCompareMode(true);
		}
		
		HashMap<String, Long> receiptCountHM = new HashMap<String, Long>();
		HashMap<String, Long> receiptAmountHM = new HashMap<String, Long>();
		HashMap<String, Long> rvmCountHM = new HashMap<String, Long>();
		HashMap<String, Long> emptiesCountHM = new HashMap<String, Long>();
		
		List<Object[]> trxSummaryList = trxService.getDailyRvmTrxListBySiteIdOpDates(siteId, result.getFromDate(), result.getToDate());
		for (Object[] obj : trxSummaryList) {
			String opDate = Util.toSimpleString((Date) obj[0], "yyyyMMdd");
			
			receiptCountHM.put(opDate, (Long) obj[1]);
			receiptAmountHM.put(opDate,  (Long) obj[2]);
			rvmCountHM.put(opDate,  (Long) obj[3]);
		}
		
		List<Object[]> trxItemSummaryList = trxService.getDailyRvmTrxItemListBySiteIdOpDates(siteId, result.getFromDate(), result.getToDate());
		for (Object[] obj : trxItemSummaryList) {
			emptiesCountHM.put(Util.toSimpleString((Date) obj[0], "yyyyMMdd"), (Long) obj[1]);
		}
		
		
		HashMap<String, Long> rvmReceiptCountHM = new HashMap<String, Long>();
		
		if (result.isRvmCompareMode()) {
			trxSummaryList = trxService.getDailyRvmTrxListByRvmIdOpDates(rvmId, result.getFromDate(), result.getToDate());
			for (Object[] obj : trxSummaryList) {
				rvmReceiptCountHM.put(Util.toSimpleString((Date) obj[0], "yyyyMMdd"), (Long) obj[1]);
			}
		}

		
		for(Date stateDate : result.getDates()) {
			String opDate = Util.toSimpleString(stateDate, "yyyyMMdd");
			int receiptCount = 0;
			int emptiesCount = 0;
			double receiptAvgCount = 0d;
			double emptiesAvgCount = 0d;
			int rvmReceiptCount = 0;
			if (receiptCountHM.containsKey(opDate) && receiptAmountHM.containsKey(opDate) && rvmCountHM.containsKey(opDate) 
					&& emptiesCountHM.containsKey(opDate)) {
				long rvmCount = rvmCountHM.get(opDate);
				if (rvmCount > 0) {
					receiptCount = receiptCountHM.get(opDate).intValue();
					emptiesCount = emptiesCountHM.get(opDate).intValue();
					
					receiptAvgCount = Math.round((double)receiptCount / rvmCount * 10d) / 10d;
					emptiesAvgCount = Math.round((double)emptiesCount / rvmCount * 10d) / 10d;
					
					if (rvmCount > maxRvmCount) {
						maxRvmCount = (int)rvmCount;
					}
				}
				
				if (result.isRvmCompareMode() && rvmReceiptCountHM.containsKey(opDate)) {
					rvmReceiptCount = rvmReceiptCountHM.get(opDate).intValue();
				}
			}
			
			result.setValues(stateDate, receiptAvgCount, emptiesAvgCount, receiptCount, emptiesCount);
			result.setRvmValues(stateDate, rvmReceiptCount);
		}
		
		result.setMaxRvmCount(maxRvmCount);
    	
    	return result;
    }
    
	/**
	 * 엑셀 다운로드 액션
	 */
    @RequestMapping(value = "/excel", method = RequestMethod.POST)
    public @ResponseBody String excel(@RequestBody DataSourceRequest request, 
    		HttpServletRequest req, HttpServletResponse res, Locale locale, HttpSession session) {
		
		Site site = siteService.getSite(Util.getSessionSiteId(session));
		
    	Date date = Util.parseZuluTime(request.getReqStrValue1());
    	int rvmId = -1;
    	
    	String rvmName = request.getReqStrValue2();
    	if (Util.isValid(rvmName)) {
    		Rvm rvm = rvmService.getRvm(site, rvmName);
    		if (rvm != null) {
    			rvmId = rvm.getId();
    		}
    	}
    	
    	if (date == null) {
        	throw new ServerOperationForbiddenException(
        			msgMgr.message("common.server.msg.wrongParamError", locale));
    	}

    	ArrayList<ArrayList<String>> excelData = new ArrayList<ArrayList<String>>();
    	
    	try {
    		RvmTrxSummayResult result = getTrxData(site.getId(), date, rvmId, session);
        	
        	ArrayList<String> excelRow = new ArrayList<String>();
        	excelRow.add("날짜");
        	excelRow.add("수거 용기수");
        	excelRow.add("평균 수거 용기수");
        	excelRow.add("영수증 건수");
        	excelRow.add("평균 영수증 건수");
        	
        	if (result.isRvmCompareMode()) {
            	excelRow.add("대상 기기 영수증 건수");
        	}
        	
        	
        	excelData.add(excelRow);
        	
        	DecimalFormat df = new DecimalFormat("#,##0.0");
        	
        	for(Date dt : result.getDates()) {
        		excelRow = new ArrayList<String>();
        		int idx = result.getDates().indexOf(dt);
        		
            	excelRow.add(Util.toDateString(dt));
            	excelRow.add(df.format(result.getEmptiesCounts().get(idx)));
            	excelRow.add(df.format(result.getEmptiesAvgCounts().get(idx)));
            	excelRow.add(df.format(result.getReceiptCounts().get(idx)));
            	excelRow.add(df.format(result.getReceiptAvgCounts().get(idx)));

            	if (result.isRvmCompareMode()) {
                	excelRow.add(String.valueOf(result.getRvmReceiptCounts().get(idx)));
            	}
            	
            	excelData.add(excelRow);
        	}
    	} catch (Exception e) {
    		logger.error("excel", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    	
    	ExcelDownloadView.makeExcelData("RecPeriodData", excelData, req, res);
    	
    	return "OK";
    }
    
}
