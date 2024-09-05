package kr.co.r2cast.controllers.eco;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import kr.co.r2cast.info.EcoGlobalInfo;
import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.EcoMessageManager;
import kr.co.r2cast.models.Message;
import kr.co.r2cast.models.MessageManager;
import kr.co.r2cast.models.ModelManager;
import kr.co.r2cast.models.eco.RecDailySummary;
import kr.co.r2cast.models.eco.RecStatusStat;
import kr.co.r2cast.models.eco.Rvm;
import kr.co.r2cast.models.eco.service.MonitoringService;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.service.SiteService;
import kr.co.r2cast.utils.Util;
import kr.co.r2cast.viewmodels.eco.RunningTimeItem;
import kr.co.r2cast.viewmodels.eco.RvmServiceResult;
import kr.co.r2cast.viewmodels.eco.RvmServiceStatusItem;
import kr.co.r2cast.viewmodels.eco.RvmServiceSummaryResult;


/**
 * 모니터링 현황판 컨트롤러
 */
@Controller("eco-mon-dashboard-controller")
@RequestMapping(value="/eco/mondashboard")
public class MonDashboardController {
	private static final Logger logger = LoggerFactory.getLogger(MonDashboardController.class);

	@Autowired 
    private MonitoringService monService;

    @Autowired 
    private SiteService siteService;

	
    @Autowired
	private MessageManager msgMgr;

	@Autowired
	private EcoMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;

	
	/**
	 * 모니터링 현황판 페이지
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
    	model.addAttribute("pageTitle", "모니터링 현황판");

    	
    	model.addAttribute("isViewSwitcherMode", true);
    	

    	return "eco/mondashboard";
    }
    
	/**
	 * 상태에 따른 로케일 상태 문자열 획득
	 */
    private String getLocaleStatus(char c, Locale locale) {
		switch (c) {
		case '2':
			return "STB 꺼짐";
		case '3':
			return "RVM 꺼짐";
		case '4':
			return "장애 보고";
		case '5':
			return "투입 금지";
		case '6':
			return "정상 동작";
		case '0':
			return "미확인";
		}
		
		return "해당없음";
    }
    
	/**
	 * 모니터링 현황판의 요약 정보 읽기 액션
	 */
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/readServiceResult", method = RequestMethod.POST)
    public @ResponseBody RvmServiceResult readRvmServiceResult(@RequestBody DataSourceRequest request,
    		Locale locale, HttpSession session) {
    	
    	try {
    		RvmServiceResult result = new RvmServiceResult();

			DecimalFormat dFormat = new DecimalFormat("##,##0");
			DecimalFormat pctFormat = new DecimalFormat("##0.0");
			DecimalFormat avgFormat = new DecimalFormat("#,##0.0");
    		
			int status6 = 0;
			int status5 = 0;
			int status4 = 0;
			int status3 = 0;
			int status2 = 0;
			int status0 = 0;
			
			int totalCount = 0;
			long runningTime = 0;
			float avgRunningMins = -1;

			if (monService.isSiteLevelViewMode(session)) {
    			int siteId = Util.getSessionSiteId(session);
    			
    			RecStatusStat nowStat = EcoGlobalInfo.SiteStatusMap.get(siteId);
    			if (nowStat != null && nowStat.isValid()) {
    				status6 = nowStat.getStatus6();
    				status5 = nowStat.getStatus5();
    				status4 = nowStat.getStatus4();
    				status3 = nowStat.getStatus3();
    				status2 = nowStat.getStatus2();
    				status0 = nowStat.getStatus0();
    				
    				totalCount = nowStat.getTotalCount();
    				avgRunningMins = nowStat.getAvgRunningMins();
    			}
    		}
			
			if (avgRunningMins == -1) {
	    		DataSourceResult dsResult = monService.getMonitoringRvmList(request, session, true, true, true);
	    		List<Rvm> rvmList = (List<Rvm>)dsResult.getData();
				
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
						status2 ++;
						break;
					case "0":
						status0 ++;
						break;
					}
				}
				
				totalCount = rvmList.size();
			}
			
			result.setWorkingCount(dFormat.format(status6));
			result.setStoreClosedCount(dFormat.format(status5));
			result.setFailureReportedCount(dFormat.format(status4));
			result.setRvmOffCount(dFormat.format(status3));
			result.setStbOffCount(dFormat.format(status2));
			result.setNoShowCount(dFormat.format(status0));
			
			result.setRealTotalCount(totalCount);
    		result.setTotalCount(dFormat.format(totalCount));

    		if (status6 > 0) {
    			result.getRunningTimeItems().add(
    					new RunningTimeItem(getLocaleStatus('6', locale) + ": " + status6, "6", status6));
    		}
    		
    		if (status5 > 0) {
    			result.getRunningTimeItems().add(
    					new RunningTimeItem(getLocaleStatus('5', locale) + ": " + status5, "5", status5));
    		}
    		
    		if (status4 > 0) {
    			result.getRunningTimeItems().add(
    					new RunningTimeItem(getLocaleStatus('4', locale) + ": " + status4, "4", status4));
    		}
    		
    		if (status3 > 0) {
    			result.getRunningTimeItems().add(
    					new RunningTimeItem(getLocaleStatus('3', locale) + ": " + status3, "3", status3));
    		}
    		
    		if (status2 > 0) {
    			result.getRunningTimeItems().add(
    					new RunningTimeItem(getLocaleStatus('2', locale) + ": " + status2, "2", status2));
    		}
    		
    		if (status0 > 0) {
    			result.getRunningTimeItems().add(
    					new RunningTimeItem(getLocaleStatus('0', locale) + ": " + status0, "0", status0));
    		}
    		
    		if (result.getRunningTimeItems().size() == 0) {
    			result.getRunningTimeItems().add(
    					new RunningTimeItem(getLocaleStatus('9', locale), "0", 1));
    		}
    		
			int todayWorkingCount = status6 + status5 + status4 + status3 + status2;
			
			result.setRealWorkingCount(status6);
			result.setRealTodayWorkingCount(todayWorkingCount);
			result.setRealNoShowCount(status0);
    		
    		if (totalCount > 0) {
    			result.setTodayWorkingPct(pctFormat.format((float)todayWorkingCount * 100f / (float)totalCount));
    			result.setWorkingPct(pctFormat.format((float)status6 * 100f / (float)totalCount));
    			result.setTodayWorkingCount(dFormat.format(todayWorkingCount));
    			
    			if (todayWorkingCount > 0) {
    				if (avgRunningMins == -1) {
            			result.setAvgRunningTime(avgFormat.format((float)runningTime / (float)todayWorkingCount));
    				} else {
            			result.setAvgRunningTime(avgFormat.format(avgRunningMins));
    				}
    			}
    		}

    		RvmServiceSummaryResult rvmServiceData = getServiceData(session);
    		
    		result.setDates(rvmServiceData.getDates());
    		result.setRunningMins(rvmServiceData.getRunningMins());
    		result.setRvmCounts(rvmServiceData.getRvmCounts());
    		
    		ArrayList<RvmServiceStatusItem> list = new ArrayList<RvmServiceStatusItem>();
    		list.add(new RvmServiceStatusItem(getLocaleStatus('6', locale), "bg-blue", status6, totalCount));
    		list.add(new RvmServiceStatusItem(getLocaleStatus('5', locale), "bg-green", status5, totalCount));
    		list.add(new RvmServiceStatusItem(getLocaleStatus('4', locale), "bg-yellow", status4, totalCount));
    		list.add(new RvmServiceStatusItem(getLocaleStatus('3', locale), "bg-orange", status3, totalCount));
    		list.add(new RvmServiceStatusItem(getLocaleStatus('2', locale), "bg-red", status2, totalCount));
    		list.add(new RvmServiceStatusItem(getLocaleStatus('0', locale), "bg-gray", status0, totalCount));
    		
			Collections.sort(list, new Comparator<RvmServiceStatusItem>() {
				@Override
				public int compare(RvmServiceStatusItem item1, RvmServiceStatusItem item2) {
		    		return Integer.compare(item2.getCount(), item1.getCount());
				}
			});
			

			result.setStatusItemNo1(list.get(0));
			result.setStatusItemNo2(list.get(1));
			result.setStatusItemNo3(list.get(2));
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("readRvmServiceResult", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
	/**
	 * 모니터링 현황판의 간단한 요약 정보 읽기 액션
	 */
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/readServiceSimpleResult", method = RequestMethod.POST)
    public @ResponseBody RvmServiceResult readRvmServiceSimpleResult(@RequestBody DataSourceRequest request,
    		Locale locale, HttpSession session) {
    	try {
    		RvmServiceResult result = new RvmServiceResult();

			DecimalFormat dFormat = new DecimalFormat("##,##0");
			
    		if (monService.isSiteLevelViewMode(session)) {
    			int siteId = Util.getSessionSiteId(session);
    			
    			RecStatusStat nowStat = EcoGlobalInfo.SiteStatusMap.get(siteId);
    			if (nowStat != null && nowStat.isValid()) {
    				result.setWorkingCount(dFormat.format(nowStat.getStatus6()));
    				result.setStoreClosedCount(dFormat.format(nowStat.getStatus5()));
    				result.setFailureReportedCount(dFormat.format(nowStat.getStatus4()));
    				result.setRvmOffCount(dFormat.format(nowStat.getStatus3()));
    				result.setStbOffCount(dFormat.format(nowStat.getStatus2()));
    				result.setNoShowCount(dFormat.format(nowStat.getStatus0()));
    				
    	    		return result;
    			}
    		}
    		
    		DataSourceResult dsResult = monService.getMonitoringRvmList(request, session, true, true, true);
    		List<Rvm> rvmList = (List<Rvm>)dsResult.getData();
    		
			int status6 = 0;
			int status5 = 0;
			int status4 = 0;
			int status3 = 0;
			int status2 = 0;
			int status0 = 0;
			
			for(Rvm rvm : rvmList) {
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
					status2 ++;
					break;
				case "0":
					status0 ++;
					break;
				}
			}
			
			result.setWorkingCount(dFormat.format(status6));
			result.setStoreClosedCount(dFormat.format(status5));
			result.setFailureReportedCount(dFormat.format(status4));
			result.setRvmOffCount(dFormat.format(status3));
			result.setStbOffCount(dFormat.format(status2));
			result.setNoShowCount(dFormat.format(status0));
			
    		return result;
    	} catch (Exception e) {
    		logger.error("readRvmServiceSimpleResult", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
	/**
	 * 운행 자료 획득
	 */
    private RvmServiceSummaryResult getServiceData(HttpSession session) {
    	Date date = Util.removeTimeOfDate(new Date());
    	RvmServiceSummaryResult result = new RvmServiceSummaryResult(date);
		
    	int summaryRvmCount = monService.getMonitoringViewRvmCount(session, true);
		result.setSummaryRvmCount(summaryRvmCount);
		
		Site site = siteService.getSite(Util.getSessionSiteId(session));

		for(Date stateDate : result.getDates()) {
			boolean fromDB = false;
			boolean isSiteLevelViewMode = monService.isSiteLevelViewMode(session);
			if (isSiteLevelViewMode) {
				RecDailySummary dailySummary = monService.getRecDailySummary(site, stateDate);
				
				if (dailySummary != null && (dailySummary.getCalculated().equals("Y") ||
						(dailySummary.getCalculated().equals("N") && stateDate.compareTo(date) == 0))) {
					result.setValues(stateDate, dailySummary.getRvmCount(), dailySummary.getAvgRunningMins());
					fromDB = true;
				}
			}
			
			if (!fromDB) {
				List<Object[]> list = monService.getServiceRecordRunningMinCountListByStateDate(
						stateDate, session);
				int rvmCount = 0;
				long runningMins = 0l;
				double avgRunningMins = 0;
				
				if (list.size() > 0) {
		    		for(Object[] row : list) {
		    			rvmCount++;
		    			runningMins += (Integer)row[2];
		    		}
		    		
		    		if (rvmCount != 0) {
		    			avgRunningMins = Math.round((double)runningMins / (double)rvmCount * 10d) / 10d;
		    			result.setValues(stateDate, rvmCount, avgRunningMins);
		    			
		    			if (isSiteLevelViewMode && stateDate.compareTo(date) != 0) {
		        			monService.saveOrUpdateRecDailySummary(site, stateDate, rvmCount,
		        					summaryRvmCount, runningMins, "Y");
		    			}
		    		}
				}
			}
		}
    	
    	return result;
    }
	
}
