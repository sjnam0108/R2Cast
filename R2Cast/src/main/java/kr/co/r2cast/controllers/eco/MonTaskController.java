package kr.co.r2cast.controllers.eco;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
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
import kr.co.r2cast.models.CustomComparator;
import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.EcoMessageManager;
import kr.co.r2cast.models.Message;
import kr.co.r2cast.models.MessageManager;
import kr.co.r2cast.models.ModelManager;
import kr.co.r2cast.models.eco.MonTask;
import kr.co.r2cast.models.eco.Rvm;
import kr.co.r2cast.models.eco.service.MonitoringService;
import kr.co.r2cast.models.eco.service.RvmService;
import kr.co.r2cast.models.fnd.Site; 
import kr.co.r2cast.models.fnd.service.SiteService;
import kr.co.r2cast.utils.SolUtil;
import kr.co.r2cast.utils.Util;
import kr.co.r2cast.viewmodels.DropDownListItem;

/**
 * 기기 작업 컨트롤러
 */
@Controller("eco-mon-task-controller")
@RequestMapping(value="/eco/montask")
public class MonTaskController {
	private static final Logger logger = LoggerFactory.getLogger(MonTaskController.class);

    @Autowired 
    private MonitoringService monService;

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
	 * 기기 작업 페이지
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
    	model.addAttribute("pageTitle", "기기 작업");
    	
    	
    	model.addAttribute("isViewSwitcherMode", true);

    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
    	
        return "eco/montask";
    }
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request, 
    		HttpSession session, Locale locale) {
    	try {
    		DataSourceResult result = monService.getMonTaskList(request, session);
    		
    		for(Object obj : result.getData()) {
    			MonTask monTask = (MonTask)obj;
    			
    			monTask.setUnivCommand(SolUtil.getUniversalCommandName(
    					monTask.getCommand(), locale));
    			
    			monTask.setFlagCode(SolUtil.getRCCommandStatusFlagCode(monTask.getStatus()));
    			monTask.setStatusTip(SolUtil.getRCCommandStatusTip(monTask.getStatus(), locale));
    		}
    		return result;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
	/**
	 * 읽기 액션 - 명령 정보
	 */
    @RequestMapping(value = "/readCommands", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readCommands(Locale locale) {
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		ArrayList<String> commandList = new ArrayList<String>(
			Arrays.asList("Reboot.bbmc", "RestartAgent.bbmc", "DeleteTrxFile.bbmc",
					"UpdateAgent.bbmc", "UploadDebugFile.bbmc", "UploadTrxFile.bbmc" )
		);
		
		for (String cmd : commandList) {
			list.add(new DropDownListItem(SolUtil.getUniversalCommandName(cmd, locale), cmd));
		}
		
		Collections.sort(list, CustomComparator.DropDownListItemTextComparator);
		
		return list;
    }
    
	/**
	 * 읽기 액션 - 작업 상태 유형 정보
	 */
    @RequestMapping(value = "/readStatusTypes", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readStatusTypes(Locale locale) {
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-asterisk text-muted fa-fw", "등록", "1"));
		list.add(new DropDownListItem("fa-regular fa-hourglass-half fa-fw", "통지", "2"));
		list.add(new DropDownListItem("fa-regular fa-flag text-blue fa-fw", "성공", "S"));
		list.add(new DropDownListItem("fa-regular fa-flag text-success fa-fw", "성공(수락)", "P"));
		list.add(new DropDownListItem("fa-regular fa-hand-paper text-danger fa-fw", "실패", "F"));
		list.add(new DropDownListItem("fa-regular fa-trash-can text-info fa-fw", "자동 취소", "C"));
		
		return list;
    }
    
	/**
	 * 읽기 액션 - 저장 전 대상 기기 수 획득
	 */
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/readDestRvmCnt", method = RequestMethod.POST)
    public @ResponseBody Integer readDestRvmCnt(@RequestBody Map<String, Object> model, Locale locale, 
    		HttpSession session) {
    	ArrayList<Object> rvmGroupIds = (ArrayList<Object>) model.get("rvmGroupIds");
    	ArrayList<Object> rvmIds = (ArrayList<Object>) model.get("rvmIds");
		
    	String toAllRvm = (String)model.get("toAllRvm");
    	
    	Site site = siteService.getSite(Util.getSessionSiteId(session));
    	List<Integer> ids = monService.getSiteIdAndRvmGroupId(session);
    	
    	if (ids != null && site != null) {
    		int rvmGroupId = ids.get(1);
    		
        	List<Integer> retIds = monService.getDestRvmIds(Util.isValid(toAllRvm) && toAllRvm.equals("Y"), 
        			rvmGroupIds, rvmIds, site.getId(), rvmGroupId);
        	
        	return retIds.size();
    	}
    	
    	return 0;
    }
    
	/**
	 * 추가 액션
	 */
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody String create(@RequestBody Map<String, Object> model, Locale locale, 
    		HttpSession session) {
    	String command = (String)model.get("command");
    	String execTime = (String)model.get("execTime");

    	Date destDate = Util.parseZuluTime((String)model.get("destDate"));
    	Date cancelDate = Util.parseZuluTime((String)model.get("cancelDate"));
		
    	if (destDate == null) {
    		destDate = new Date();
    	}
    	
    	ArrayList<Object> rvmGroupIds = (ArrayList<Object>) model.get("rvmGroupIds");
    	ArrayList<Object> rvmIds = (ArrayList<Object>) model.get("rvmIds");
		
    	String toAllRvm = (String)model.get("toAllRvm");
    	
    	Site site = siteService.getSite(Util.getSessionSiteId(session));
    	List<Integer> ids = monService.getSiteIdAndRvmGroupId(session);
    	
    	List<Integer> retIds = new ArrayList<Integer>();
    	
    	if (ids != null && site != null) {
    		int rvmGroupId = ids.get(1);
    		
        	retIds = monService.getDestRvmIds(Util.isValid(toAllRvm) && toAllRvm.equals("Y"), 
        			rvmGroupIds, rvmIds, site.getId(), rvmGroupId);
    	}
    	
    	if (Util.isNotValid(command) || retIds.size() == 0 || cancelDate == null) {
    		throw new ServerOperationForbiddenException(
    				msgMgr.message("common.server.msg.wrongParamError", locale));
    	}
    	
		try {
	    	for (int i : retIds) {
	    		Rvm rvm = rvmService.getRvm(i);
	    		if (rvm != null) {
	    			monService.saveOrUpdate(new MonTask(rvm.getSite(), rvm, command, "", execTime, "1", 
	    					destDate, cancelDate, null, null, session));
	    			
	    			monService.checkRvmRemoteControlTypeAndLastReportTime(rvm, command);
	    		}
	    	}
		} catch (Exception e) {
    		logger.error("create", e);
    		throw new ServerOperationForbiddenException("SaveError");
    	}
    	
    	return "OK";
    }
    
	/**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model,
    		Locale locale) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<MonTask> monTasks = new ArrayList<MonTask>();

    	for (Object id : objs) {
    		MonTask monTask = new MonTask();
    		
    		monTask.setId((int)id);
    		
    		monTasks.add(monTask);
    	}
    	
    	try {
        	monService.deleteMonTasks(monTasks);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "OK";
    }
}
