package kr.co.r2cast.controllers.eco;

import java.util.ArrayList;
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
import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.EcoMessageManager;
import kr.co.r2cast.models.Message;
import kr.co.r2cast.models.MessageManager;
import kr.co.r2cast.models.ModelManager;
import kr.co.r2cast.models.eco.MonEventReport;
import kr.co.r2cast.models.eco.service.MonitoringService;
import kr.co.r2cast.utils.Util;
import kr.co.r2cast.viewmodels.DropDownListItem;

/**
 * 장비 이벤트 보고 컨트롤러
 */
@Controller("eco-mon-event-report-controller")
@RequestMapping(value="/eco/moneventreport")
public class MonEventReportController {
	private static final Logger logger = LoggerFactory.getLogger(MonEventReportController.class);

    @Autowired 
    private MonitoringService monService;

    @Autowired
	private MessageManager msgMgr;

	@Autowired
	private EcoMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	/**
	 * 장비 이벤트 보고 페이지
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
    	model.addAttribute("pageTitle", "장비 이벤트 보고");

    	
    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
    	
        return "eco/moneventreport";
    }
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request, 
    		Locale locale) {
    	try {
    		return monService.getMonEventReportList(request);
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("readError");
    	}
    }
    
	/**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model,
    		Locale locale) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<MonEventReport> eventReports = new ArrayList<MonEventReport>();

    	for (Object id : objs) {
    		MonEventReport eventReport = new MonEventReport();
    		
    		eventReport.setId((int)id);
    		
    		eventReports.add(eventReport);
    	}
    	
    	try {
    		monService.deleteMonEventReports(eventReports);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "OK";
    }
    
	/**
	 * 읽기 액션 - 범주 정보
	 */
    @RequestMapping(value = "/readCategories", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readCategories(Locale locale) {
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-solid fa-circle fa-fw text-red", "빨간색", "R"));
		list.add(new DropDownListItem("fa-solid fa-circle fa-fw text-orange", "주황색", "O"));
		list.add(new DropDownListItem("fa-solid fa-circle fa-fw text-yellow", "노란색", "Y"));
		list.add(new DropDownListItem("fa-solid fa-circle fa-fw text-green", "초록색", "G"));
		list.add(new DropDownListItem("fa-solid fa-circle fa-fw text-blue", "파란색", "B"));
		list.add(new DropDownListItem("fa-solid fa-circle fa-fw text-purple", "보라색", "P"));
		
		return list;
    }
    
	/**
	 * 읽기 액션 - 보고 유형 정보
	 */
    @RequestMapping(value = "/readReportTypes", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readReportTypes(Locale locale) {
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-circle-exclamation fa-fw text-info", "정보", "I"));
		list.add(new DropDownListItem("fa-regular fa-siren-on fa-fw text-yellow", "경고", "W"));
		list.add(new DropDownListItem("fa-regular fa-circle-xmark fa-fw text-red", "오류", "E"));
		
		return list;
    }
    
	/**
	 * 읽기 액션 - 장비 유형 정보
	 */
    @RequestMapping(value = "/readEquipTypes", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readEquipTypes(Locale locale) {
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-server fa-fw", "Backup Server", "B"));
		list.add(new DropDownListItem("fa-regular fa-server fa-fw", "DB Server", "D"));
		list.add(new DropDownListItem("fa-regular fa-server fa-fw", "FTP Server", "F"));
		list.add(new DropDownListItem("fa-regular fa-album fa-fw", "RVM", "P"));
		list.add(new DropDownListItem("fa-regular fa-server fa-fw", "WAS", "W"));
		
		return list;
    }
}
