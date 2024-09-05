package kr.co.r2cast.controllers.eco;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
//import kr.co.r2cast.models.DsgMessageManager;
import kr.co.r2cast.models.Message;
import kr.co.r2cast.models.MessageManager;
import kr.co.r2cast.models.ModelManager;
import kr.co.r2cast.models.eco.RtnSchdTask;
import kr.co.r2cast.models.eco.RtnSchdTaskRvm;
import kr.co.r2cast.models.eco.Rvm;
import kr.co.r2cast.models.eco.service.MonitoringService;
import kr.co.r2cast.models.eco.service.RvmService;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.service.SiteService;
import kr.co.r2cast.utils.SolUtil;
import kr.co.r2cast.utils.Util;

/**
 * 예약된 기기 작업 컨트롤러
 */
@Controller("eco-rtn-schd-task-controller")
@RequestMapping(value="/eco/rtnschdtask")
public class RtnSchdTaskController {
	private static final Logger logger = LoggerFactory.getLogger(RtnSchdTaskController.class);

    @Autowired 
    private MonitoringService monService;
    
    @Autowired 
    private SiteService siteService;

    @Autowired 
    private RvmService rvmService;
    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private EcoMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	/**
	 * 예약된 기기 작업 페이지
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
    	model.addAttribute("pageTitle", "예약된 기기 작업");
    	
    	
        return "eco/rtnschdtask";
    }
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request, 
    		Locale locale, HttpSession session) {
    	try {
    		DataSourceResult result = monService.getRtnSchdTaskList(request);
    		
        	Site site = siteService.getSite(Util.getSessionSiteId(session));
        	if (site != null) {
        		for(Object obj : result.getData()) {
        			RtnSchdTask rtnSchdTask = (RtnSchdTask)obj;
        			
        			rtnSchdTask.setUnivCommand(SolUtil.getUniversalCommandName(
        					rtnSchdTask.getCommand(), locale));
        			rtnSchdTask.setUnivAutoCancel(SolUtil.getUniversalAutoCancelTime(
        					rtnSchdTask.getAutoCancelMins(), locale));
        			rtnSchdTask.setRvmCount(monService.getRtnSchdTaskRvmCountBySiteIdRtnSchdTaskId(
        					site.getId(), rtnSchdTask.getId()));
        		}
        	}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("readError");
    	}
    }
    
	/**
	 * 추가/변경 액션
	 */
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
    public @ResponseBody String edit(@RequestBody Map<String, Object> model, Locale locale, 
    		HttpSession session) {
    	Site site = siteService.getSite(Util.getSessionSiteId(session));
    	
    	Integer id  = (Integer)model.get("id");
    	Integer autoCancelMins  = (Integer)model.get("autoCancelMins");

    	String taskName = (String)model.get("taskName");
    	String command = (String)model.get("command");
    	String published = (String)model.get("published");

    	if (site == null || id == null || Util.isNotValid(taskName) || Util.isNotValid(command) ||
    			autoCancelMins == null || Util.isNotValid(published)) {
    		throw new ServerOperationForbiddenException(
    				msgMgr.message("common.server.msg.wrongParamError", locale));
    	}
		
    	String monTime = (String)model.get("monTime");
    	String tueTime = (String)model.get("tueTime");
    	String wedTime = (String)model.get("wedTime");
    	String thuTime = (String)model.get("thuTime");
    	String friTime = (String)model.get("friTime");
    	String satTime = (String)model.get("satTime");
    	String sunTime = (String)model.get("sunTime");
    	
    	RtnSchdTask target = null;
    	if (id > 0) {
    		target = monService.getRtnSchdTask(id);
    		if (target == null) {
        		throw new ServerOperationForbiddenException(
        				msgMgr.message("common.server.msg.wrongParamError", locale));
    		}
    		
    		target.setTaskName(taskName);
    		target.setCommand(command);
    		
    		target.setMonTime(monTime);
    		target.setTueTime(tueTime);
    		target.setWedTime(wedTime);
    		target.setThuTime(thuTime);
    		target.setFriTime(friTime);
    		target.setSatTime(satTime);
    		target.setSunTime(sunTime);
    		
    		target.setAutoCancelMins(autoCancelMins);
    		target.setPublished(published);
    		
    		target.touchWho(session);
    	} else {
    		target = new RtnSchdTask(site, taskName, command, monTime, tueTime, wedTime,
    				thuTime, friTime, satTime, sunTime, autoCancelMins, published, session);
    	}
    	
		try {
			monService.saveOrUpdate(target);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("edit", dive);
        	throw new ServerOperationForbiddenException("동일한 작업명의 자료가 이미 등록되어 있습니다.");
        } catch (ConstraintViolationException cve) {
    		logger.error("edit", cve);
        	throw new ServerOperationForbiddenException("동일한 작업명의 자료가 이미 등록되어 있습니다.");
		} catch (Exception e) {
    		logger.error("edit", e);
    		throw new ServerOperationForbiddenException("SaveError");
    	}

		
    	ArrayList<Object> rvmGroupIds = (ArrayList<Object>) model.get("rvmGroupIds");
    	ArrayList<Object> rvmIds = (ArrayList<Object>) model.get("rvmIds");
		
    	String toAllRvm = (String)model.get("toAllRvm");
    	
		ArrayList<Integer> oldRvmIds = new ArrayList<Integer>();
		
    	List<Integer> newRvmIds = monService.getDestRvmIds(Util.isValid(toAllRvm) && toAllRvm.equals("Y"), 
    			rvmGroupIds, rvmIds, site.getId(), -1);
    	
    	
    	
    	List<RtnSchdTaskRvm> oldRvms = monService.getRtnSchdTaskRvmListBySiteIdRtnSchdTaskId(
    			site.getId(), target.getId());
    	ArrayList<RtnSchdTaskRvm> delItems = new ArrayList<RtnSchdTaskRvm>();

    	for(RtnSchdTaskRvm rtnSchdTaskRvm : oldRvms) {
    		oldRvmIds.add(rtnSchdTaskRvm.getRvm().getId());
    		
    		if (!newRvmIds.contains(rtnSchdTaskRvm.getRvm().getId())) {
    			RtnSchdTaskRvm tmp = new RtnSchdTaskRvm();
    			tmp.setId(rtnSchdTaskRvm.getId());

    			delItems.add(tmp);
    		}
    	}

		try {
			for(int rvmId : newRvmIds) {
	    		Rvm rvm = rvmService.getRvm(rvmId);
	    		
	    		if (!oldRvmIds.contains(rvmId)) {
					monService.saveOrUpdate(new RtnSchdTaskRvm(site, target, rvm, session));
	    		}
	    	}

			monService.deleteRtnSchdTaskRvms(delItems);
		} catch (Exception e) {
    		logger.error("edit", e);
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
    	
    	List<RtnSchdTask> rtnSchdTasks = new ArrayList<RtnSchdTask>();

    	for (Object id : objs) {
    		RtnSchdTask rtnSchdTask = new RtnSchdTask();
    		
    		rtnSchdTask.setId((int)id);
    		
    		rtnSchdTasks.add(rtnSchdTask);
    	}
    	
    	try {
        	monService.deleteRtnSchdTasks(rtnSchdTasks);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "OK";
    }
}
