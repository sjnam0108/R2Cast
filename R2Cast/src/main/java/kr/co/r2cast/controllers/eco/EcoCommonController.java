package kr.co.r2cast.controllers.eco;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import kr.co.r2cast.exceptions.ServerOperationForbiddenException;
import kr.co.r2cast.info.EcoGlobalInfo;
import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceRequest.FilterDescriptor;
import kr.co.r2cast.models.DataSourceRequest.SortDescriptor;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.EcoMessageManager;
import kr.co.r2cast.models.Message;
import kr.co.r2cast.models.MessageManager;
import kr.co.r2cast.models.ModelManager;
import kr.co.r2cast.models.UploadTransitionModel;
import kr.co.r2cast.models.eco.EcoComparator;
import kr.co.r2cast.models.eco.EcoFormRequest;
import kr.co.r2cast.models.eco.MonTask;
import kr.co.r2cast.models.eco.RecStatusStat;
import kr.co.r2cast.models.eco.Rvm;
import kr.co.r2cast.models.eco.RvmGroup;
import kr.co.r2cast.models.eco.RvmGroupRvm;
import kr.co.r2cast.models.eco.RvmLastReport;
import kr.co.r2cast.models.eco.RvmStatusLine;
import kr.co.r2cast.models.eco.service.MonitoringService;
import kr.co.r2cast.models.eco.service.RvmService;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.service.SiteService;
import kr.co.r2cast.utils.SolUtil;
import kr.co.r2cast.utils.Util;
import kr.co.r2cast.viewmodels.DropDownListItem;
import kr.co.r2cast.viewmodels.eco.ContentFileTransData;
import kr.co.r2cast.viewmodels.eco.RunningTimeItem;
import kr.co.r2cast.viewmodels.eco.RvmGroupItem;
import kr.co.r2cast.viewmodels.eco.RvmItem;
import kr.co.r2cast.viewmodels.eco.RvmOverviewData;
import kr.co.r2cast.viewmodels.eco.RvmServiceResult;

/**
 * DSG 공통 컨트롤러
 */
@Controller("eco-common-controller")
@RequestMapping(value="/eco/common")
public class EcoCommonController {
	private static final Logger logger = LoggerFactory.getLogger(EcoCommonController.class);

    @Autowired 
    private RvmService rvmService;

    @Autowired 
    private SiteService siteService;

    @Autowired 
    private MonitoringService monService;

	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private EcoMessageManager solMsgMgr;
    
	@SuppressWarnings("unused")
	@Autowired
	private ModelManager modelMgr;
	
	/**
	 * 공통 읽기 액션 - Kendo AutoComplete 용 RVM 정보
	 */
    @RequestMapping(value = "/readACRvms", method = RequestMethod.POST)
    public @ResponseBody List<RvmItem> readAutoComplRvms(@RequestBody DataSourceRequest request, 
    		HttpSession session) {
    	
    	int siteId = Util.getSessionSiteId(session);
    	
		ArrayList<RvmItem> list = new ArrayList<RvmItem>();

		FilterDescriptor filter = request.getFilter();
		List<FilterDescriptor> filters = filter.getFilters();
		String userInput = "";
		if (filters.size() > 0) {
			userInput = Util.parseString((String) filters.get(0).getValue());
		}

		List<Rvm> rvmList = rvmService.getRvmListBySiteIdRvmName(siteId, true, userInput);
		
		if (rvmList.size() <= 50) {
    		for(Rvm rvm : rvmList) {
    			list.add(new RvmItem(rvm.getId(), rvm.getRvmName()));
    		}
    		
    		Collections.sort(list, EcoComparator.RvmItemRvmNameComparator);
		}

    	return list;
    }
    
	/**
	 * 공통 읽기 액션 - RVM 그룹 정보(DataSource Result 형식)
	 */
    @RequestMapping(value = "/readDSRRvmGroups", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readDSRRVMGroups(@RequestBody DataSourceRequest request, 
    		HttpSession session) {
    	try {
    		FilterDescriptor filter = new FilterDescriptor();
    		filter.setField("site.id");
    		filter.setOperator("eq");
    		filter.setValue(Util.getSessionSiteId(session));
    		
    		FilterDescriptor outer = new FilterDescriptor();
    		outer.setLogic("and");
    		outer.getFilters().add(filter);
    		
    		request.setFilter(outer);
    		
    		SortDescriptor sort = new SortDescriptor();
    		sort.setDir("asc");
    		sort.setField("rvmGroupName");
    		
    		ArrayList<SortDescriptor> list = new ArrayList<SortDescriptor>();
    		list.add(sort);
    		
    		request.setSort(list);
    		
            return rvmService.getRvmGroupList(request);
    	} catch (Exception e) {
    		logger.error("readDSRRVMGroups", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
	/**
	 * 공통 읽기 액션 - 접근 가능한 RVM 정보
	 */
    @RequestMapping(value = "/readAccRvms", method = RequestMethod.POST)
    public @ResponseBody List<RvmItem> readAccessibleRvms(@RequestBody DataSourceRequest request, 
    		HttpSession session) {
		ArrayList<RvmItem> retList = new ArrayList<RvmItem>();

    	List<Integer> ids = monService.getSiteIdAndRvmGroupId(session);
    	if (ids != null) {
    		int siteId = ids.get(0);
    		int rvmGroupId = ids.get(1);
    		
    		ArrayList<Integer> rvmIds = new ArrayList<Integer>();
    		if (rvmGroupId > 0) {
    			List<RvmGroupRvm> rvmGroupRvms = 
    					rvmService.getRvmGroupRvmListBySiteIdRvmGroupId(siteId, rvmGroupId, true);
    			for (RvmGroupRvm rvmGroupRvm : rvmGroupRvms) {
    				rvmIds.add(rvmGroupRvm.getRvm().getId());
    			}
    		}
    		
        	try {
        		FilterDescriptor filter = new FilterDescriptor();
        		filter.setField("site.id");
        		filter.setOperator("eq");
        		filter.setValue(Util.getSessionSiteId(session));
        		
        		FilterDescriptor outer = new FilterDescriptor();
        		outer.setLogic("and");
        		outer.getFilters().add(filter);
        		
        		request.setFilter(outer);
        		
        		SortDescriptor sort = new SortDescriptor();
        		sort.setDir("asc");
        		sort.setField("rvmName");
        		
        		ArrayList<SortDescriptor> list = new ArrayList<SortDescriptor>();
        		list.add(sort);
        		
        		request.setSort(list);
        		
        		DataSourceResult result = rvmService.getRvmList(request);
        		
        		Date today = new Date();
        		for(Object obj : result.getData()) {
        			Rvm rvm = (Rvm) obj;
        			
        			if (rvmGroupId > 0 && !rvmIds.contains(rvm.getId())) {
        				continue;
        			}
        			
        			// STB가 유효할 경우에만 추가
        			Date endDate = rvm.getEffectiveEndDate() == null ? 
        					SolUtil.getMaxTaskDate() : rvm.getEffectiveEndDate();
        			if (endDate.compareTo(today) > 0) {
            			retList.add(new RvmItem(rvm.getId(), rvm.getRvmName()));
        			}
        		}
        		
        		Collections.sort(retList, EcoComparator.RvmItemRvmNameComparator);
        	} catch (Exception e) {
        		logger.error("readAccessibleRvms", e);
        		throw new ServerOperationForbiddenException("ReadError");
        	}
    	}
    	
    	return retList;
    }
    
	/**
	 * 공통 읽기 액션 - 접근 가능한 RVM 그룹 정보
	 */
    @RequestMapping(value = "/readAccRvmGroups", method = RequestMethod.POST)
    public @ResponseBody List<RvmGroupItem> readAccessibleRvmGroups(@RequestBody DataSourceRequest request, 
    		Locale locale, HttpSession session) {
		ArrayList<RvmGroupItem> retList = new ArrayList<RvmGroupItem>();

    	List<Integer> ids = monService.getSiteIdAndRvmGroupId(session);
    	if (ids != null) {
    		int rvmGroupId = ids.get(1);
    		
    		ArrayList<Integer> rvmGroupIds = new ArrayList<Integer>();
    		if (rvmGroupId > 0) {
    			rvmGroupIds.add(rvmGroupId);
    		}
    		
        	try {
        		FilterDescriptor filter = new FilterDescriptor();
        		filter.setField("site.id");
        		filter.setOperator("eq");
        		filter.setValue(Util.getSessionSiteId(session));
        		
        		FilterDescriptor outer = new FilterDescriptor();
        		outer.setLogic("and");
        		outer.getFilters().add(filter);
        		
        		request.setFilter(outer);
        		
        		SortDescriptor sort = new SortDescriptor();
        		sort.setDir("asc");
        		sort.setField("rvmGroupName");
        		
        		ArrayList<SortDescriptor> list = new ArrayList<SortDescriptor>();
        		list.add(sort);
        		
        		request.setSort(list);

        		DataSourceResult result = rvmService.getRvmGroupList(request);
        		
        		for(Object obj : result.getData()) {
        			RvmGroup rvmGroup = (RvmGroup) obj;
        			
        			if (rvmGroupId > 0 && !rvmGroupIds.contains(rvmGroup.getId())) {
        				continue;
        			}
        			
        			retList.add(new RvmGroupItem(rvmGroup.getId(), rvmGroup.getRvmGroupName(), 
        					rvmGroup.getCategory()));
        		}
        	} catch (Exception e) {
        		logger.error("readAccessibleRvmGroups", e);
        		throw new ServerOperationForbiddenException("ReadError");
        	}
    	}
    	
    	return retList;
    }
    
    
	/**
	 * 운영 태그 변경 액션
	 */
	@RequestMapping(value = "/updateOpTag", method = RequestMethod.POST)
    public @ResponseBody String updateOpTag(@RequestBody Map<String, Object> model, Locale locale, 
    		HttpSession session) {
		Rvm rvm = rvmService.getRvm((Integer)model.get("rvmId"));
		if (rvm != null) {
			RvmLastReport rvmLastReport = rvm.getRvmLastReport();
			if (rvmLastReport != null) {
				String opTag = (String)model.get("opTag");
				
				if (Util.isNotValid(opTag)) {
					opTag = "";
				}
				
				rvmLastReport.setOpTag(opTag);
				
				// 의도적인 Who 컬럼 미변경
				//stbLastReport.touchWho(session);
				
				try {
					monService.saveOrUpdate(rvmLastReport);
				} catch (Exception e) {
					logger.error("updateOpTag", e);
				}
			}
		}
    	
    	return "OK";
    }
//    
//	/**
//	 * 공통 읽기 액션 - 플레이어 ID
//	 */
//    @RequestMapping(value = "/readPlayerIDs", method = RequestMethod.POST)
//    public @ResponseBody List<BasPlayerId> readPlayerIDs(@RequestBody DataSourceRequest request, 
//    		HttpSession session) {
//		Site site = siteService.getSite(Util.getSessionSiteId(session));
//		ArrayList<BasPlayerId> list = new ArrayList<BasPlayerId>();
//
//    	try {
//        	DataSourceResult result = basService.getPlayerIdList(request, session, true);
//        	if (result.getData().size() <= 100) {
//        		for(Object obj : result.getData()) {
//        			BasPlayerId basPlayer = (BasPlayerId) obj;
//        			
//        			if (stbService.getStbByPlayerCode(site, basPlayer.getPlayerCode()) == null) {
//        				list.add(new BasPlayerId(site, basPlayer.getPlayerCode(), null));
//        			}
//        		}
//        		
//    			Collections.sort(list, DsgComparator.BasPlayerIdPlayerCodeComparator);
//        	}
//    	} catch (Exception e) {
//    		logger.error("readPlayerIDs", e);
//    		throw new ServerOperationForbiddenException("ReadError");
//    	}
//
//    	return list;
//    }
//    
//	/**
//	 * 공통 읽기 액션 - STB 그룹 DropDownList
//	 */
//    @RequestMapping(value = "/readStbGroups", method = RequestMethod.POST)
//    public @ResponseBody List<DropDownListItem> readStbGroups(@RequestBody Map<String, Object> model,
//    		Locale locale, HttpSession session) {
//		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
//		
//		Site site = siteService.getSite(Util.getSessionSiteId(session));
//		
//		// '값선택' 텍스트를 js에서 처리함
//		//list.add(new DropDownListItem("W", msgMgr.message("dropdown.selectValue", locale), "-1"));
//		
//		if (site != null) {
//			List<StbGroup> stbGroups = stbService.getStbGroupListBySiteId(site.getId());
//    		
//    		Collections.sort(stbGroups, DsgComparator.StbGroupStbGroupNameComparator);
//
//    		for (StbGroup stbGroup : stbGroups) {
//    			list.add(new DropDownListItem("far fa-folder",
//    					stbGroup.getStbGroupName(), String.valueOf(stbGroup.getId())));
//    		}
//    		
//    		Boolean includeCurrSiteItem = (Boolean) model.get("includeCurrSiteItem");
//            
//            if (includeCurrSiteItem != null && includeCurrSiteItem.booleanValue()) {
//            	list.add(0, new DropDownListItem("fas fa-globe", "[" + msgMgr.message(
//            			"viewswitcher.currentSite", locale) + "]", "-1"));
//            }
//		}
//		
//		return list;
//    }
//
	/**
	 * DataFeed - 화재경보기 설정 액션
	 */
    @RequestMapping(value = "/datafeed/chgfirealarm", method = RequestMethod.POST)
    public @ResponseBody String changSwitch(@RequestBody Map<String, Object> model, Locale locale, 
    		HttpSession session) {
		Site site = siteService.getSite(Util.getSessionSiteId(session));
		if (site == null) {
    		throw new ServerOperationForbiddenException(
    				msgMgr.message("common.server.msg.wrongParamError", locale));
		}
		
		String type = (String)model.get("alertType");
		String value = (String)model.get("value");
		
		if (Util.isNotValid(type) || Util.isNotValid(value)) {
    		throw new ServerOperationForbiddenException(
    				msgMgr.message("common.server.msg.wrongParamError", locale));
		}
		
		if (!type.equals("D") && !type.equals("E")) {
    		throw new ServerOperationForbiddenException(
    				msgMgr.message("common.server.msg.wrongParamError", locale));
		}
		
		EcoGlobalInfo.FireAlarmType = type;
		EcoGlobalInfo.FireAlarmActive = Util.parseBoolean(value, false);
		
        return "OK";
    }
	
	/**
	 * RVM 개요 모달
	 */
    @RequestMapping(value = "/rvmoverview", method = {RequestMethod.GET, RequestMethod.POST })
    public String rvmOverview(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	String id = request.getParameter("id");
    	Date date = Util.setMaxTimeOfDate(Util.parseDate(request.getParameter("date")));

    	if (Util.isNotValid(id)) {
    		id = "-1";
    	}

    	String dateStr = date == null ? Util.toDateString(null) : Util.toDateString(date);
    	
    	ArrayList<String> svcDateList = new ArrayList<String>();
    	List<RvmStatusLine> statusList = monService.getRvmStatusLineListByRvmId(Util.parseInt(id, -1));
    	for(RvmStatusLine statusLine : statusList) {
			String dateNumber = String.format("%0$tQ", statusLine.getStateDate());
			
			if (!svcDateList.contains(dateNumber)) {
				svcDateList.add(dateNumber);
			}
    	}
    	
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {
					
    				new Message("value_rvmId", id),
    				new Message("value_date", dateStr),
    			});

    	model.addAttribute("dates", svcDateList);
    	
        return "eco/modal/rvmoverview";
    }
    
	/**
	 * RVM 개요 모달 - 모든 요약 정보 읽기 액션
	 */
    @RequestMapping(value = "/readRvmOverview", method = RequestMethod.POST)
    public @ResponseBody RvmOverviewData readRvmOverviewData(@RequestBody EcoFormRequest form, 
    		Locale locale, HttpSession session) {

    	RvmOverviewData ret = new RvmOverviewData();
    	
    	Integer rvmId = form.getRvmId();
    	Date stateDate = Util.removeTimeOfDate(Util.parseZuluTime(form.getStateDate()));
    	
    	ret.setService(getService(rvmId, stateDate, locale));
    	ret.setRvm(rvmService.getRvm(rvmId));
    	
    	return ret;
    }
    
	/**
	 * RVM 개요 모달 - 상태 차트 정보 획득
	 */
    private RvmServiceResult getService(Integer rvmId, Date stateDate, Locale locale) {
    	try {
    		RvmServiceResult result = new RvmServiceResult();
    		
    		result.setStateDate(Util.toDateString(stateDate));
    		
    		Rvm rvm = rvmService.getRvm(rvmId);
    		if (rvm != null) {
    			result.setRvmId(rvm.getId());
    			result.setRvmName(rvm.getRvmName());
        		
        		RvmStatusLine statusLine = monService.getRvmStatusLineByStateDateRvmId(stateDate, rvmId);
        		

        		if (statusLine != null && statusLine.getStatusLine().length() == 1440) {
        			char prevChar = '9';  // 의미없는 숫자 '9'
        			String hhmm = "00:00";
        			int prevIndex = -1;
        			
        			int status6 = 0;
        			int status5 = 0;
        			int status4 = 0;
        			int status3 = 0;
        			int status2 = 0;
        			
        			String line = statusLine.getStatusLine();
        			if (stateDate.compareTo(Util.removeTimeOfDate(new Date())) == 0) {
        				line = SolUtil.getTodayStbStatusLine(line);
        			}
        			
        			for (int i = 0; i < 1440; i ++) {
        				char aChar = line.charAt(i);
        				
        				switch (aChar) {
        				case '2':
        					status2 ++;
        					break;
        				case '3':
        					status3 ++;
        					break;
        				case '4':
        					status4 ++;
        					break;
        				case '5':
        					status5 ++;
        					break;
        				case '6':
        					status6 ++;
        					break;
        				case '9':
        					break;
        				default:
        					status2 ++;
        					aChar = '2';
        					
        					break;
        				}
        				
        				if (prevChar != aChar) {
        					if (prevChar != '9') {
        		    			result.getRunningTimeItems().add(
        		    					new RunningTimeItem(getLocaleStatus(prevChar, locale) + "(" + hhmm + 
        		    							(i - prevIndex == 1 ? "" : " - " + Util.getHHmm(i - 1)) + ")", 
        		    							String.valueOf(prevChar), i - prevIndex));
        					}
        					
        					prevChar = aChar;
        					prevIndex = i;
        					hhmm = Util.getHHmm(i);
        				}
        			}
	    			result.getRunningTimeItems().add(
	    					new RunningTimeItem(getLocaleStatus(prevChar, locale) + "(" + hhmm + 
	    							(1440 - prevIndex == 1 ? "" : " - " + Util.getHHmm(1439)) + ")", 
	    							String.valueOf(prevChar), 1440 - prevIndex));

        			DecimalFormat dFormat = new DecimalFormat("##,##0");
        			
        			result.setWorkingCount(dFormat.format(status6));
        			result.setStoreClosedCount(dFormat.format(status5));
        			result.setFailureReportedCount(dFormat.format(status4));
        			result.setRvmOffCount(dFormat.format(status3));
        			result.setStbOffCount(dFormat.format(status2));
        			result.setTotalCount(dFormat.format(
        					status3 + status4 + status5 + status6));
        		}
    		}
    		
    		if (result.getRunningTimeItems().size() == 0) {
    			result.getRunningTimeItems().add(
    					new RunningTimeItem(getLocaleStatus('9', locale), "0", 1440));
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("getStbService", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
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
	 * 공통 읽기 액션 - RVM 모델 DropDownList
	 */
    @RequestMapping(value = "/readRvmModels", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readRvmModels(Locale locale, 
    		HttpSession session) {
    	
		ArrayList<DropDownListItem> retList = new ArrayList<DropDownListItem>();
    	
		boolean etcExists = false;
		List<Object[]> modelCntList = rvmService .getValidRvmModelCountListBySiteId(Util.getSessionSiteId(session));
		for (Object[] obj : modelCntList) {
			String m = (String) obj[0];
			if (Util.isNotValid(m)) {
				continue;
			}
			
    		if (!etcExists && m.equals("etc")) {
    			etcExists = true;
    		}
    		
			retList.add(new DropDownListItem(m, m));
		}

		Collections.sort(retList, new Comparator<DropDownListItem>() {
			public int compare(DropDownListItem i1, DropDownListItem i2) {
				return i1.getText().toLowerCase().compareTo(i2.getText().toLowerCase());
			}
		});
    	
    	if (!etcExists) {
    		retList.add(new DropDownListItem("etc", "etc"));
    	}
    	
    	return retList;
    }
    
	/**
	 * RVM 개요 모달 - 상태 차트 정보 획득
	 */
    private ContentFileTransData getTransfer(Integer rvmId, Locale locale) {
    	
    	ContentFileTransData data = null;
    	
    	// 컨텐츠 파일 전송 현황 자료 획득
    	long latestSchdLength = 0L, latestSchdTransLength = 0L;
    	long totalSchdLength = 0L, totalSchdTransLength = 0L;
    	long totalOthrLength = 0L, totalOthrTransLength = 0L;
    	
//		List<Object[]> latestSchdData = schdService.getLatestDplySchdCondFileTransSumByRvmId(rvmId);
//		for (Object[] obj : latestSchdData) {
//			String str = (String) obj[0];
//			
//			if (str.equals("Y")) {
//				latestSchdTransLength = (Long) obj[1];
//			}
//			
//			if (str.equals("Y") || str.equals("N")) {
//				latestSchdLength += (Long) obj[1];
//			}
//		}

//		List<Object[]> schdData = schdService.getDplySchdCondFileTransSumByRvmId(rvmId);
//		for (Object[] obj : schdData) {
//			String str = (String) obj[0];
//			
//			if (str.equals("Y")) {
//				totalSchdTransLength = (Long) obj[1];
//			}
//			
//			if (str.equals("Y") || str.equals("N")) {
//				totalSchdLength += (Long) obj[1];
//			}
//		}

//		List<Object[]> othrData = schdService.getDplyOthrCondFileTransSumByStbId(rvmId);
//		for (Object[] obj : othrData) {
//			String str = (String) obj[0];
//			
//			if (str.equals("Y")) {
//				totalOthrTransLength = (Long) obj[1];
//			}
//			
//			if (str.equals("Y") || str.equals("N")) {
//				totalOthrLength += (Long) obj[1];
//			}
//		}
    	
//    	data = new ContentFileTransData(schdService.getLatestDplySchdCondScheduleNameByRvmId(rvmId),
//    			latestSchdLength, latestSchdTransLength, totalSchdLength, totalSchdTransLength,
//    			totalOthrLength, totalOthrTransLength,
//    			msgMgr.message("stboverview.completed", locale),
//    			msgMgr.message("stboverview.left", locale),
//    			msgMgr.message("stboverview.na", locale));
    	
    	data.calcValues();

    	return data;
    }


	/**
	 * RVM 상태바의 간단한 상태 요약 정보 읽기 액션
	 */
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/readStatusFeed", method = RequestMethod.POST)
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
	 * 읽기 액션 - 기기 서비스유형
	 */
     @RequestMapping(value = "/readRvmServiceTypes", method = RequestMethod.POST)
     public @ResponseBody List<DropDownListItem> readRvmServiceTypes(Locale locale) {

     	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
 		
 		list.add(new DropDownListItem("fa-regular fa-monitor-waveform fa-fw text-blue", "집중 감시", "M"));
 		list.add(new DropDownListItem("fa-regular fa-lightbulb-on fa-fw text-blue", "서비스", "S"));
 		list.add(new DropDownListItem("fa-regular fa-truck fa-fw", "설치", "I"));
 		list.add(new DropDownListItem("fa-regular fa-vial fa-fw", "테스트", "T"));
 		list.add(new DropDownListItem("fa-regular fa-bullhorn fa-fw", "홍보", "P"));
 		list.add(new DropDownListItem("fa-regular fa-pause-circle fa-fw text-danger", "임시중단", "C"));
 		list.add(new DropDownListItem("fa-regular fa-truck fa-fw text-danger", "철거", "R"));
 		
 		return list;
     }
     
 	/**
	 * 읽기 액션 - 기기 상태유형
	 */
     @RequestMapping(value = "/readRvmStatusTypes", method = RequestMethod.POST)
     public @ResponseBody List<DropDownListItem> readRvmStatusTypes(Locale locale) {
 		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
 		
 		list.add(new DropDownListItem("flag-18 sts-18-6", "정상 동작", "6"));
 		list.add(new DropDownListItem("flag-18 sts-18-5", "투입 금지", "5"));
 		list.add(new DropDownListItem("flag-18 sts-18-4", "장애 보고", "4"));
 		list.add(new DropDownListItem("flag-18 sts-18-3", "RVM 꺼짐", "3"));
 		list.add(new DropDownListItem("flag-18 sts-18-2", "STB 꺼짐", "2"));
 		list.add(new DropDownListItem("flag-18 sts-18-0", "미확인", "0"));
 		
 		return list;
     }
    
    /**
     * 파일 다운로드 액션
     */
	@RequestMapping(value = "/download", method = RequestMethod.GET)
    public ModelAndView download(HttpServletRequest request,
    		HttpServletResponse response) {

    	String siteId = request.getParameter("siteId");
    	String type = request.getParameter("type");
    	String file = request.getParameter("file");
    	String code = Util.parseString(request.getParameter("code"));
    	String prefix = Util.parseString(request.getParameter("prefix"));

    	File target = null;
    	
    	try {
        	if (file != null && !file.isEmpty()) {
        		if (type != null && !type.isEmpty()) {
        			try {
        				if(type.equals("TRX")) {
            					String rootDir = SolUtil.getPhysicalRoot("Trx");
            					
            					target = new File(rootDir + file);
            					
            					if (!target.exists() || !target.isFile()) {
            						target = null;
            				}
	            		} else if (type.equals("DL")) {
		        			String logRootDir = SolUtil.getPhysicalRoot("Debug");
        					
        					target = new File(logRootDir + file);
        					
        					if (!target.exists() || !target.isFile()) {
        						target = null;
        					}
	            		} else if (type.equals("SETUP")) {
		        			String rootDir = SolUtil.getPhysicalRoot("Setup");
        					
        					target = new File(rootDir + file);
        					
        					if (!target.exists() || !target.isFile()) {
        						target = null;
        					}
	            		}
        			} catch (Exception e) {
        				logger.error("download", e);
        			}
        		}
        	}
        	
        	if (target == null) {
        		throw new ServerOperationForbiddenException("OperationError");
        	}

            response.setContentType("application/octet-stream;charset=UTF-8");
            
            if (target.length() < Integer.MAX_VALUE) {
                response.setContentLength((int)target.length());
            }

            String userAgent = request.getHeader("User-Agent");
            
            String targetName = (Util.isValid(prefix) ? prefix + "_" : "") + target.getName();
        	if (userAgent.indexOf("MSIE 5.5") > -1) { // MS IE 5.5 이하
        	    response.setHeader("Content-Disposition", "filename=\""
        		    + URLEncoder.encode(targetName, "UTF-8") + "\";");
        	} else if (userAgent.indexOf("MSIE") > -1) { // MS IE (보통은 6.x 이상 가정)
        	    response.setHeader("Content-Disposition", "attachment; filename=\""
        		    + URLEncoder.encode(targetName, "UTF-8") + "\";");
        	} else {
        	    response.setHeader("Content-Disposition", "attachment; filename=\""
        		    + new String(targetName.getBytes("UTF-8"), "latin1") + "\";");
        	}
        	
            FileCopyUtils.copy(new FileInputStream(target), response.getOutputStream());
    	} catch (Exception e) {
			logger.error("download", e);
			throw new ServerOperationForbiddenException("OperationError");
    	}
 
        return null;
    }
	
	/**
	 * 모듈 전용 파일 업로드 페이지
	 */
    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String upload(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {

    	Site site = siteService.getSite(Util.getSessionSiteId(session));
    	String type = Util.parseString(request.getParameter("type"));
    	String code = Util.parseString(request.getParameter("code"));
    	
    	UploadTransitionModel uploadModel = new UploadTransitionModel();
    	
    	try {
        	if (site != null && Util.isValid(type)) {
        		uploadModel.setSiteId(site.getId());
        		uploadModel.setType(type);
        		uploadModel.setCode(code);
        		uploadModel.setSaveUrl("/dsg/common/uploadsave");
        		
        		if (type.equals("SZ")) {
        			uploadModel.setAllowedExtensions("[\".scz\"]");
        		} else if (type.equals("CONTENT")) {
        			uploadModel.setAllowedExtensions("[\".jpg\", \".png\", \".mp4\"]");
        		}
        	}
    	} catch (Exception e) {
    		logger.error("upload", e);
    	}

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {
					new Message("label_cancel", "upload.cancel"),
					new Message("label_dropFilesHere", "upload.dropFilesHere"),
					new Message("label_headerStatusUploaded", "upload.headerStatusUploaded"),
					new Message("label_headerStatusUploading", "upload.headerStatusUploading"),
					new Message("label_remove", "upload.remove"),
					new Message("label_retry", "upload.retry"),
					new Message("label_select", "upload.select"),
					new Message("label_uploadSelectedFiles", "upload.uploadSelectedFiles"),
					new Message("label_clearSelectedFiles", "upload.clearSelectedFiles"),
					new Message("label_invalidFileExtension", "upload.invalidFileExtension"),
    			});

    	model.addAttribute("uploadModel", uploadModel);
    	
        return "dsg/modal/dsgupload";
    }
    
    /**
     * 모듈 전용 파일 업로드 저장 액션
     */
    @RequestMapping(value = "/uploadsave", method = RequestMethod.POST)
    public @ResponseBody String save(@RequestParam List<MultipartFile> files,
    		@RequestParam int siteId, @RequestParam String type, @RequestParam String code, HttpSession session) {
    	try {
    		if (Util.isValid(type)) {
        		Site site = siteService.getSite(siteId);

        		if (site != null) {
            		if (type.equals("SETUP")) {		// 설치파일: wizard
            			String typeRootDir = SolUtil.getPhysicalRoot("Setup");
            			
            	        for (MultipartFile file : files) {
            	        	if (!file.isEmpty()) {
            	        		File uploadedFile = new File(typeRootDir + "/" + file.getOriginalFilename());
            	        		FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(uploadedFile));
            	        	}
            	        }
        			} else if (type.equals("FILE")) {		// 일반 파일 업로드: ecocommon
        				// 일반 파일 업로드의 경우 code로 서버 path root를 전달한다
            	        for (MultipartFile file : files) {
            	        	if (!file.isEmpty()) {
            	        		File uploadedFile = new File(code + "/" + file.getOriginalFilename());
            	        		FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(uploadedFile));
            	        	}
            	        }
        			}
        		}
    		}
    	} catch (Exception e) {
    		logger.error("uploadsave", e);
    	}
        
        // Return an empty string to signify success
        return "";
    }
//	/**
//	 * 기기 파일 전송 상태
//	 */
//    @RequestMapping(value = "/stbtransstatus", method = RequestMethod.GET)
//    public String stbTransStatus(Model model, Locale locale, HttpSession session,
//    		HttpServletRequest request) {
//    	
//    	String condId = request.getParameter("condId");
//    	String type = request.getParameter("type");
//
//    	solMsgMgr.addCommonMessages(model, locale, session, request);
//    	
//    	msgMgr.addViewMessages(model, locale,
//    			new Message[] {
//    				new Message("title_status", "stbtransstatus.status"),
//    				new Message("title_stbName", "stbtransstatus.stbName"),
//    				new Message("title_totalLength", "stbtransstatus.totalLength"),
//    				new Message("title_transLength", "stbtransstatus.transLength"),
//    				new Message("title_transRatio", "stbtransstatus.transRatio"),
//    				new Message("value_condId", condId),
//    				new Message("value_type", type),
//    			});
//    	
//        return "dsg/modal/stbtransstatus";
//    }
//    
//	/**
//	 * 읽기 액션 - 기기 파일 전송 상태
//	 */
//    @RequestMapping(value = "/stbtransstatus/readData", method = RequestMethod.POST)
//    public @ResponseBody ArrayList<StbTransferStatusItem> readStbTransStatus(@RequestBody DataSourceRequest request, 
//    		HttpSession session, Locale locale) {
//    	
//    	ArrayList<StbTransferStatusItem> list = new ArrayList<StbTransferStatusItem>();
//    	Integer condId = request.getAdminCondId();
//    	String type = request.getAdminType();
//    	
//    	if (Util.isValid(type)) {
//    		if (type.equals("S")) {
//    	    	DplySchdCond dplySchdCond = schdService.getDplySchdCond(condId);
//    	    	
//    	    	if (dplySchdCond == null) {
//    	    		throw new ServerOperationForbiddenException(msgMgr.message("common.server.msg.wrongParamError", locale));
//    	    	} else {
//    	    		try {
//    	        		List<DplySchdCondFile> condFiles = schdService.getDplySchdCondFileListByDplySchdCondId(
//    	        				dplySchdCond.getId());
//    	        		HashMap<Integer, StbTransferStatusItem> map = new HashMap<Integer, StbTransferStatusItem>();
//    	        		
//    	        		for(DplySchdCondFile condFile : condFiles) {
//    	        			int stbId = condFile.getStb().getId();
//    	        			long length = condFile.getDplySchdFile().getFileLength();
//    	        			
//    	        			if (!map.containsKey(stbId)) {
//    	    					StbStatusLine stbStatusLine = monService.getStbStatusLineByStateDateStbId(
//    	    		        			Util.removeTimeOfDate(new Date()), stbId);
//    	    					String lastStatus = "0";
//
//    	    					if (stbStatusLine != null) {
//    	    						if (SolUtil.isDateDuring1Minute(stbStatusLine.getWhoLastUpdateDate())) {
//    	    							lastStatus = stbStatusLine.getLastStatus();
//    	    						} else {
//    	    							lastStatus = "2";
//    	    						}
//    	    					}
//    	        				
//    	        				map.put(stbId, new StbTransferStatusItem(stbId, lastStatus, condFile.getStb().getStbName()));
//    	        			}
//    	        			
//    	        			StbTransferStatusItem tmp = map.get(stbId);
//    	        			tmp.setTotalFileLength(tmp.getTotalFileLength() + length);
//    	        			
//    	        			if (condFile.getTransferred().equals("Y")) {
//    	        				tmp.setTransferredLength(tmp.getTransferredLength() + length);
//    	        			}
//    	        			
//    	        			list = new ArrayList<StbTransferStatusItem>(map.values());
//    	        		}
//    	    		} catch (Exception e) {
//    	        		logger.error("readStbTransStatus", e);
//    	        		throw new ServerOperationForbiddenException("ReadError");
//    	    		}
//    	    	}
//    		} else if (type.equals("O")) {
//    	    	DplyOthrCond dplyOthrCond = schdService.getDplyOthrCond(condId);
//    	    	
//    	    	if (dplyOthrCond == null) {
//    	    		throw new ServerOperationForbiddenException(msgMgr.message("common.server.msg.wrongParamError", locale));
//    	    	} else {
//    	    		try {
//    	        		List<DplyOthrCondFile> condFiles = schdService.getDplyOthrCondFileListByDplyOthrCondId(
//    	        				dplyOthrCond.getId());
//    	        		HashMap<Integer, StbTransferStatusItem> map = new HashMap<Integer, StbTransferStatusItem>();
//    	        		
//    	        		for(DplyOthrCondFile condFile : condFiles) {
//    	        			int stbId = condFile.getStb().getId();
//    	        			long length = condFile.getDplyOthrFile().getFileLength();
//    	        			
//    	        			if (!map.containsKey(stbId)) {
//    	    					StbStatusLine stbStatusLine = monService.getStbStatusLineByStateDateStbId(
//    	    		        			Util.removeTimeOfDate(new Date()), stbId);
//    	    					String lastStatus = "0";
//
//    	    					if (stbStatusLine != null) {
//    	    						if (SolUtil.isDateDuring1Minute(stbStatusLine.getWhoLastUpdateDate())) {
//    	    							lastStatus = stbStatusLine.getLastStatus();
//    	    						} else {
//    	    							lastStatus = "2";
//    	    						}
//    	    					}
//    	        				
//    	        				map.put(stbId, new StbTransferStatusItem(stbId, lastStatus, condFile.getStb().getStbName()));
//    	        			}
//    	        			
//    	        			StbTransferStatusItem tmp = map.get(stbId);
//    	        			tmp.setTotalFileLength(tmp.getTotalFileLength() + length);
//    	        			
//    	        			if (condFile.getTransferred().equals("Y")) {
//    	        				tmp.setTransferredLength(tmp.getTransferredLength() + length);
//    	        			}
//    	        			
//    	        			list = new ArrayList<StbTransferStatusItem>(map.values());
//    	        		}
//    	    		} catch (Exception e) {
//    	        		logger.error("readStbTransStatus", e);
//    	        		throw new ServerOperationForbiddenException("ReadError");
//    	    		}
//    	    	}
//    		}
//    	}
//    	
//    	return list;
//    }
//	
//	/**
//	 * STB 지도 페이지
//	 */
//    @RequestMapping(value = "/map", method = RequestMethod.GET)
//    public String mapStb(Model model, Locale locale, HttpSession session,
//    		HttpServletRequest request) {
//    	
//    	msgMgr.addViewMessages(model, locale,
//    			new Message[] {
//    				new Message("label_schedule", "monmapview.schedule"),
//    				new Message("label_lastReportTime", "monmapview.lastReportTime"),
//    				new Message("label_runningTime", "monmapview.runningTime"),
//    				new Message("label_mins", "monmapview.mins"),
//    			});
//    	
//        return "dsg/popup/map";
//    }
	
	/**
	 * RVM 현재 상태 출력 popover 페이지
	 */
    @RequestMapping(value = "/rvmstatus", method = { RequestMethod.GET, RequestMethod.POST })
    public String rvmStatus(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	String rvmIdStr = request.getParameter("rvmId");
    	int rvmId = Util.parseInt(rvmIdStr, -1);
    	
    	ArrayList<Integer> rvmIds = new ArrayList<Integer>();
    	rvmIds.add(rvmId);

    	monService.recalcMonitoringValues(rvmIds);
    	
    	String lastStatus = "0";
    	String statusTrain = "";
    	
    	Rvm rvm = rvmService.getRvm(rvmId);
    	if (rvm != null) {
    		String tmp = rvm.getLastStatus();
    		if (Util.isValid(tmp) && (tmp.equals("2") || tmp.equals("3") || tmp.equals("4") || 
    				tmp.equals("5") || tmp.equals("6"))) {
    			lastStatus = tmp;
    		}
    		
			RvmStatusLine rvmStatusLine = monService.getRvmStatusLineByStateDateRvmId(
        			Util.removeTimeOfDate(new Date()), rvm.getId());
			if (rvmStatusLine != null) {
				Calendar cal = Calendar.getInstance();
				int totalMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
				
				if (totalMinutes >= 0 && totalMinutes < 1440) {
					if (totalMinutes < 9) {
						statusTrain = rvmStatusLine.getStatusLine().substring(0, totalMinutes + 1);
					} else {
						statusTrain = rvmStatusLine.getStatusLine().substring(totalMinutes - 9, totalMinutes + 1);
					}
				}
			}
    	}
		
		if (statusTrain.length() < 10) {
			statusTrain = "0000000000".substring(0, 10 - statusTrain.length()) + statusTrain;
		}
		
    	msgMgr.addViewMessages(model, locale,
    			new Message[] {
    				new Message("value_lastStatus", lastStatus),
    				new Message("value_status0", statusTrain.substring(0, 1)),
    				new Message("value_status1", statusTrain.substring(1, 2)),
    				new Message("value_status2", statusTrain.substring(2, 3)),
    				new Message("value_status3", statusTrain.substring(3, 4)),
    				new Message("value_status4", statusTrain.substring(4, 5)),
    				new Message("value_status5", statusTrain.substring(5, 6)),
    				new Message("value_status6", statusTrain.substring(6, 7)),
    				new Message("value_status7", statusTrain.substring(7, 8)),
    				new Message("value_status8", statusTrain.substring(8, 9)),
    				new Message("value_status9", statusTrain.substring(9)),
    				new Message("value_lastStatusTip", SolUtil.getLocalizedStatusMsgString(lastStatus, locale)),
    				new Message("value_status0Tip", SolUtil.getLocalizedStatusMsgString(statusTrain.substring(0, 1), locale)),
    				new Message("value_status1Tip", SolUtil.getLocalizedStatusMsgString(statusTrain.substring(1, 2), locale)),
    				new Message("value_status2Tip", SolUtil.getLocalizedStatusMsgString(statusTrain.substring(2, 3), locale)),
    				new Message("value_status3Tip", SolUtil.getLocalizedStatusMsgString(statusTrain.substring(3, 4), locale)),
    				new Message("value_status4Tip", SolUtil.getLocalizedStatusMsgString(statusTrain.substring(4, 5), locale)),
    				new Message("value_status5Tip", SolUtil.getLocalizedStatusMsgString(statusTrain.substring(5, 6), locale)),
    				new Message("value_status6Tip", SolUtil.getLocalizedStatusMsgString(statusTrain.substring(6, 7), locale)),
    				new Message("value_status7Tip", SolUtil.getLocalizedStatusMsgString(statusTrain.substring(7, 8), locale)),
    				new Message("value_status8Tip", SolUtil.getLocalizedStatusMsgString(statusTrain.substring(8, 9), locale)),
    				new Message("value_status9Tip", SolUtil.getLocalizedStatusMsgString(statusTrain.substring(9), locale)),
    			});
    	
    	return "eco/popover/rvmstatus";
    }
	
	/**
	 * RVM 최근 작업 페이지
	 */
    @RequestMapping(value = "/recenttask", method = RequestMethod.GET)
    public String recentTask(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	solMsgMgr.addCommonMessages(model, locale, session, request);
    	
    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});
    	
        return "eco/modal/recenttask";
    }
	
	/**
	 * 읽기 액션 - RVM 최근 작업
	 */
    @RequestMapping(value = "/readRecentTasks", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readRecentTasks(@RequestBody DataSourceRequest request, 
    		HttpSession session, Locale locale) {
    	try {
    		DataSourceResult result = monService.getMonTaskList(request, session, 5);
    		
    		for(Object obj : result.getData()) {
    			MonTask monTask = (MonTask)obj;
    			
    			monTask.setUnivCommand(SolUtil.getUniversalCommandName(
    					monTask.getCommand(), locale));
    			
    			if (monTask.getStatus().equals("2") || monTask.getStatus().equals("3") || 
					monTask.getStatus().equals("S") || monTask.getStatus().equals("P") ||
					monTask.getStatus().equals("F") || monTask.getStatus().equals("C")) {
    				monTask.setRemainingSecs("");
    			} else {
    				Date lastReportTime = monService.getRvmLastReportLastUpdateDateByRvmId(
    						monTask.getRvm().getId());
    				
    				if (lastReportTime != null) {
    					Calendar cal = Calendar.getInstance();
    					cal.setTime(new Date());
    					
    					int currentSecs = cal.get(Calendar.SECOND);
    					
    					cal.setTime(lastReportTime);
    					int lastReportSec = cal.get(Calendar.SECOND);
    					
    					if (lastReportSec - currentSecs < 1) {
    						monTask.setRemainingSecs(String.valueOf(lastReportSec - currentSecs + 60));
    					} else {
    						monTask.setRemainingSecs(String.valueOf(lastReportSec - currentSecs));
    					}
    				}
    			}
    			
    			monTask.setFlagCode(SolUtil.getRCCommandStatusFlagCode(monTask.getStatus()));
    			monTask.setStatusTip(SolUtil.getRCCommandStatusTip(monTask.getStatus(), locale));
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("readRecentTasks", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
//	/**
//	 * 공통 읽기 액션 - FTP 서버 정보(DataSource Result 형식)
//	 */
//    @RequestMapping(value = "/readDSRFtpServers", method = RequestMethod.POST)
//    public @ResponseBody DataSourceResult readDSRFtpServers(@RequestBody DataSourceRequest request, 
//    		HttpSession session) {
//    	try {
//    		FilterDescriptor filter = new FilterDescriptor();
//    		filter.setField("site.id");
//    		filter.setOperator("eq");
//    		filter.setValue(Util.getSessionSiteId(session));
//    		
//    		FilterDescriptor outer = new FilterDescriptor();
//    		outer.setLogic("and");
//    		outer.getFilters().add(filter);
//    		
//    		request.setFilter(outer);
//    		
//    		SortDescriptor sort = new SortDescriptor();
//    		sort.setDir("asc");
//    		sort.setField("ftpServerName");
//    		
//    		ArrayList<SortDescriptor> list = new ArrayList<SortDescriptor>();
//    		list.add(sort);
//    		
//    		request.setSort(list);
//    		
//            return stbService.getFtpServerList(request);
//    	} catch (Exception e) {
//    		logger.error("readDSRFtpServers", e);
//    		throw new ServerOperationForbiddenException("ReadError");
//    	}
//    }
//    
//	/**
//	 * 공통 읽기 액션 - 접근 가능한 운영 태그 정보
//	 */
//    @RequestMapping(value = "/readAccOpTags", method = RequestMethod.POST)
//    public @ResponseBody List<OpTagItem> readAccessibleOpTags(@RequestBody DataSourceRequest request, 
//    		Locale locale, HttpSession session) {
//		ArrayList<OpTagItem> retList = new ArrayList<OpTagItem>();
//		
//    	try {
//    		FilterDescriptor filter = new FilterDescriptor();
//    		filter.setField("site.id");
//    		filter.setOperator("eq");
//    		filter.setValue(Util.getSessionSiteId(session));
//    		
//    		FilterDescriptor outer = new FilterDescriptor();
//    		outer.setLogic("and");
//    		outer.getFilters().add(filter);
//    		
//    		request.setFilter(outer);
//    		
//    		SortDescriptor sort = new SortDescriptor();
//    		sort.setDir("asc");
//    		sort.setField("description");
//    		
//    		ArrayList<SortDescriptor> list = new ArrayList<SortDescriptor>();
//    		list.add(sort);
//    		
//    		request.setSort(list);
//
//    		DataSourceResult result = basService.getTagList(request);
//    		
//    		for(Object obj : result.getData()) {
//    			BasTag tag = (BasTag) obj;
//    			
//    			if (!tag.getTagType().equals("O")) {
//    				continue;
//    			}
//    			
//    			retList.add(new OpTagItem(tag.getId(), tag.getUkid(), 
//    					tag.getColor(), tag.getDescription()));
//    		}
//    	} catch (Exception e) {
//    		logger.error("readAccessibleOpTags", e);
//    		throw new ServerOperationForbiddenException("ReadError");
//    	}
//    	
//    	return retList;
//    }
//
//	/**
//	 * 읽기 액션 - 기타 묶음 컨텐츠 중복
//	 */
//    @RequestMapping(value = "/stbserverschedule/readData", method = RequestMethod.POST)
//    public @ResponseBody List<ServerContentItem> readServerSchedule(@RequestBody DataSourceRequest request, 
//    		HttpSession session, Locale locale) {
//    	
//    	ArrayList<ServerContentItem> retList = new ArrayList<ServerContentItem>();
//    	
//    	Integer stbId = request.getReqIntValue1();
//		String schedName = "";
//    	if (stbId != null) {
//    		Stb stb = stbService.getStb(stbId);
//    		if (stb != null) {
//    			StbLastReport lastReport = stb.getStbLastReport();
//    			if (lastReport != null) {
//    				schedName = lastReport.getScheduleName();
//    				if (Util.isValid(schedName)) {
//    					schedName = Util.removeTrailingChar(schedName, "(*)");
//    				}
//    			}
//    		}
//    	}
//    	
//		int siteId = Util.getSessionSiteId(session);
//		Site site = siteService.getSite(siteId);
//		
//    	if (Util.isValid(schedName) && site != null) {
//    		List<Sheet> sheetList = ctntService.getSheetListBySiteId(siteId);
//    		
//    		Collections.sort(sheetList, new Comparator<Sheet>() {
//				public int compare(Sheet s1, Sheet s2) {
//					return s1.getName().toLowerCase().compareTo(s2.getName().toLowerCase());
//				}
//    	    });
//    		
//    		int dispIdx = -1;
//    		for(Sheet sheet : sheetList) {
//    			if (sheet.getName().indexOf("#") > -1) {
//    				for(int i = 1; i <= 12; i++) {
//    					if (sheet.getName().replace("#", String.valueOf(i)).equals(schedName)) {
//    						dispIdx = i;
//    						break;
//    					}
//    				}
//    			} else if (sheet.getName().equals(schedName)) {
//    				dispIdx = 1;
//    			}
//    			
//    			if (dispIdx > 0) {
//    	        	
//    	        	// 사이트 내 모든 컨텐츠에 대한 hashmap 생성
//    	    		List<Content> ctntList = ctntService.getContentListBySiteId(siteId);
//    	    		HashMap<String, Content> ctntMap = new HashMap<String, Content>();
//    	    		for(Content content : ctntList) {
//    	    			ctntMap.put("C" + content.getId(), content);
//    	    		}
//    	    		
//    				List<SheetContent> sheetCtntList = ctntService.getSheetContentListBySheetId(sheet.getId());
//    				for (SheetContent sheetContent : sheetCtntList) {
//    					if (sheetContent.getDispIndex() == dispIdx && ctntMap.containsKey("C" + sheetContent.getContentId())) {
//    						Content c = ctntMap.get("C" + sheetContent.getContentId());
//    						if (c != null) {
//    							retList.add(new ServerContentItem(c.getName(), Util.isValid(c.getShortName()) ? c.getShortName() : c.getName(), 
//    									c.getThumbFilename(), c.getDuration(), site.getShortName()));
//    						}
//    					}
//    				}
//    				
//    				break;
//    			}
//    		}
//    	}
//    	
//    	return retList;
//    }
}
