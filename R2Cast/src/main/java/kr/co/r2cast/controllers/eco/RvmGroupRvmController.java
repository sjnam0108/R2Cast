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
import kr.co.r2cast.models.eco.Rvm;
import kr.co.r2cast.models.eco.RvmGroup;
import kr.co.r2cast.models.eco.RvmGroupRvm;
import kr.co.r2cast.models.eco.service.RvmService;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.service.SiteService;
import kr.co.r2cast.utils.Util;

/**
 * RVM 그룹 RVM 컨트롤러
 */
@Controller("eco-rvm-group-rvm-controller")
@RequestMapping(value="/eco/rvmgrouprvm")
public class RvmGroupRvmController {
	private static final Logger logger = LoggerFactory.getLogger(RvmGroupRvmController.class);

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
	 * RVM 그룹 RVM 페이지
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
    	model.addAttribute("pageTitle", "RVM 그룹 RVM");

    	
    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
    	
        return "eco/rvmgrouprvm";
    }
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request) {
    	try {
            return rvmService.getRvmGroupRvmList(request);
    	} catch (RuntimeException re) {
    		logger.error("read", re);
    		throw new ServerOperationForbiddenException("ReadError");
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
	/**
	 * 추가 액션(자료 저장 포함)
	 */
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody String create(@RequestBody Map<String, Object> model, Locale locale, 
    		HttpSession session) {
    	ArrayList<Object> rvmGroupIds = (ArrayList<Object>) model.get("rvmGroupIds");
    	ArrayList<Object> rvmIds = (ArrayList<Object>) model.get("rvmIds");
    	int siteId = Util.getSessionSiteId(session);
    	
		int cnt = 0;

		if (rvmGroupIds.size() > 0 && rvmIds.size() > 0 && siteId > 0) {
    		try {
    			Site site = siteService.getSite(siteId);
    			
    			if (site != null) {
            		for(Object rvmGroupObj : rvmGroupIds) {
            			RvmGroup rvmGroup = rvmService.getRvmGroup((int) rvmGroupObj);
            			
            			for(Object rvmObj : rvmIds) {
            				Rvm rvm = rvmService.getRvm((int) rvmObj);
            				
                			if (!rvmService.isRegisteredRvmGroupRvm(siteId, rvmGroup.getId(), rvm.getId())) {
                				rvmService.saveOrUpdate(new RvmGroupRvm(site, rvmGroup, rvm, session));
                				cnt ++;
                			}
            			}
            		}
    			}
    		} catch (Exception e) {
        		logger.error("create", e);
        		throw new ServerOperationForbiddenException("SaveError");
        	}
    	} else {
    		throw new ServerOperationForbiddenException(msgMgr.message("common.server.msg.wrongParamError", locale));
    	}
    	
		if (cnt == 0) {
			return msgMgr.message("common.server.msg.operationNotRequired", locale);
		}
		
    	return msgMgr.message("common.server.msg.saveSuccessWithCount", new Object[] {cnt}, locale);
    }

	/**
	 * 변경 액션
	 */
    // 해당 없음

	/**
	 * 추가 / 변경 시의 자료 저장
	 */
    // 해당 없음
    
	/**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<RvmGroupRvm> rvmGroupRvms = new ArrayList<RvmGroupRvm>();

    	for (Object id : objs) {
    		RvmGroupRvm rvmGroupRvm = new RvmGroupRvm();
    		
    		rvmGroupRvm.setId((int)id);
    		
    		rvmGroupRvms.add(rvmGroupRvm);
    	}
    	
    	try {
        	rvmService.deleteRvmGroupRvms(rvmGroupRvms);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "OK";
    }
}
