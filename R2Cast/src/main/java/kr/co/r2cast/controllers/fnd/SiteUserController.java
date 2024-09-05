package kr.co.r2cast.controllers.fnd;

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
import kr.co.r2cast.models.Message;
import kr.co.r2cast.models.MessageManager;
import kr.co.r2cast.models.ModelManager;
import kr.co.r2cast.models.DataSourceRequest.SortDescriptor;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.SiteUser;
import kr.co.r2cast.models.fnd.User;
import kr.co.r2cast.models.fnd.service.SiteService;
import kr.co.r2cast.models.fnd.service.UserService;
import kr.co.r2cast.utils.Util;

/**
 * 사이트 사용자 컨트롤러
 */
@Controller("fnd-site-user-controller")
@RequestMapping(value="/fnd/siteuser")
public class SiteUserController {
	private static final Logger logger = LoggerFactory.getLogger(SiteUserController.class);

    @Autowired 
    private UserService userService;

    @Autowired 
    private SiteService siteService;
    
	@Autowired
	private MessageManager msgMgr;
    
	@Autowired
	private ModelManager modelMgr;
    
	/**
	 * 사이트 사용자 페이지
	 */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String index(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	modelMgr.addMainMenuModel(model, locale, session, request);
    	msgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {
					new Message("pageTitle", "siteuser.title"),
    				new Message("title_username", "siteuser.username"),
    				new Message("title_familiarName", "siteuser.familiarName"),
    				new Message("title_shortName", "siteuser.shortName"),
    				new Message("title_siteName", "siteuser.siteName"),
    				new Message("label_site", "siteuser.site"),
    				new Message("label_user", "siteuser.user"),
    			});

    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
         
    	
        return "fnd/siteuser";
    }
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request) {
    	try {
            return siteService.getSiteUserList(request);
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
    	ArrayList<Object> siteIds = (ArrayList<Object>) model.get("siteIds");
    	ArrayList<Object> userIds = (ArrayList<Object>) model.get("userIds");
    	
		int cnt = 0;

		if (siteIds.size() > 0 && userIds.size() > 0) {
    		try {
        		for(Object siteObj : siteIds) {
        			Site site = siteService.getSite((int) siteObj);
        			
        			for(Object userObj : userIds) {
        				User user = userService.getUser((int) userObj);
        				
            			if (!siteService.isRegisteredSiteUser(site.getId(), user.getId())) {
            				siteService.saveOrUpdate(new SiteUser(site, user, session));
            				cnt ++;
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
    public @ResponseBody List<SiteUser> destroy(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<SiteUser> siteUsers = new ArrayList<SiteUser>();

    	for (Object id : objs) {
    		SiteUser siteUser = new SiteUser();
    		
    		siteUser.setId((int)id);
    		
    		siteUsers.add(siteUser);
    	}
    	
    	try {
        	siteService.deleteSiteUsers(siteUsers);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return siteUsers;
    }
    
	/**
	 * 읽기 액션 - 사이트 정보
	 */
    @RequestMapping(value = "/readSites", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readSites(@RequestBody DataSourceRequest request) {
    	try {
    		SortDescriptor sort = new SortDescriptor();
    		sort.setDir("asc");
    		sort.setField("shortName");
    		
    		ArrayList<SortDescriptor> list = new ArrayList<SortDescriptor>();
    		list.add(sort);
    		
    		request.setSort(list);
    		
            return siteService.getSiteList(request);
    	} catch (Exception e) {
    		logger.error("readSites", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
	/**
	 * 읽기 액션 - 사용자 정보
	 */
    @RequestMapping(value = "/readUsers", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readUsers(@RequestBody DataSourceRequest request) {
    	try {
    		SortDescriptor sort = new SortDescriptor();
    		sort.setDir("asc");
    		sort.setField("familiarName");
    		
    		ArrayList<SortDescriptor> list = new ArrayList<SortDescriptor>();
    		list.add(sort);
    		
    		request.setSort(list);
    		
            return userService.getUserList(request);
    	} catch (Exception e) {
    		logger.error("readUsers", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
}
