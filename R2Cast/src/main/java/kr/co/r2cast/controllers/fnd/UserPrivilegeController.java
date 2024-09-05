package kr.co.r2cast.controllers.fnd;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import kr.co.r2cast.exceptions.ServerOperationForbiddenException;
import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.Message;
import kr.co.r2cast.models.MessageManager;
import kr.co.r2cast.models.ModelManager;
import kr.co.r2cast.models.DataSourceRequest.SortDescriptor;
import kr.co.r2cast.models.fnd.Menu;
import kr.co.r2cast.models.fnd.Privilege;
import kr.co.r2cast.models.fnd.Role;
import kr.co.r2cast.models.fnd.RolePrivilege;
import kr.co.r2cast.models.fnd.User;
import kr.co.r2cast.models.fnd.UserPrivilege;
import kr.co.r2cast.models.fnd.service.PrivilegeService;
import kr.co.r2cast.models.fnd.service.UserService;
import kr.co.r2cast.utils.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 사용자 권한 컨트롤러
 */
@Controller("fnd-user-priv-controller")
@RequestMapping(value="/fnd/userpriv")
public class UserPrivilegeController {
	private static final Logger logger = LoggerFactory.getLogger(UserPrivilegeController.class);

    @Autowired 
    private UserService userService;

    @Autowired 
    private PrivilegeService privService;
    
	@Autowired
	private MessageManager msgMgr;
    
	@Autowired
	private ModelManager modelMgr;
    
	/**
	 * 사용자 권한 페이지
	 */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String index(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	modelMgr.addMainMenuModel(model, locale, session, request);
    	msgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {
					new Message("pageTitle", "userprivilege.title"),
    				new Message("title_username", "userprivilege.username"),
    				new Message("title_familiarName", "userprivilege.familiarName"),
    				new Message("title_privilege", "userprivilege.privilege"),
    				new Message("title_ukid", "userprivilege.ukid"),
    				new Message("label_role", "userprivilege.role"),
    				new Message("label_privilege", "userprivilege.privilege"),
    				new Message("label_user", "userprivilege.user"),
    			});

    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
        

    	
        return "fnd/userpriv";
    }
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request) {
    	try {
            return privService.getUserPrivilegeList(request);
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
    	ArrayList<Object> userIds = (ArrayList<Object>) model.get("userIds");
    	ArrayList<Object> roleIds = (ArrayList<Object>) model.get("roleIds");
    	ArrayList<Object> privIds = (ArrayList<Object>) model.get("privIds");
    	
		int cnt = 0;

		if (roleIds.size() + privIds.size() > 0 && userIds.size() > 0) {
    		try {
    			for(Object userObj : userIds) {
    				User user = userService.getUser((int) userObj);

    				for(Object privObj : privIds) {
    					Privilege priv = privService.getPrivilege((int) privObj);
    					
    					if (!privService.isRegisteredUserPrivilege(user.getId(), priv.getId())) {
    						privService.saveOrUpdate(new UserPrivilege(user, priv, session));
    						cnt ++;
    					}
    				}
    				
    				for(Object roleObj : roleIds) {
    					Role role = privService.getRole((int) roleObj);
    					if (role != null) {
    						if (role.getUkid().startsWith("menu.")) {
    							Menu parent = privService.getMenu(role.getUkid().substring(5));
    							if (parent != null) {
        							List<String> childrenList = privService.getAllMenuChildrenById(parent.getId());
        							
        							for (String str : childrenList) {
        								Privilege priv = privService.getPrivilege("menu." + str);
                						
                						if (!privService.isRegisteredUserPrivilege(user.getId(), priv.getId())) {
                    						privService.saveOrUpdate(new UserPrivilege(user, priv, session));
                    						cnt ++;
                						}
        							}
    							}
    						} else {
            					List<RolePrivilege> rolePrivList = privService.getRolePrivilegeListByRoleId((int) roleObj);
            					for(RolePrivilege rolePriv : rolePrivList) {
            						Privilege priv = rolePriv.getPrivilege();
            						
            						if (!privService.isRegisteredUserPrivilege(user.getId(), priv.getId())) {
                						privService.saveOrUpdate(new UserPrivilege(user, priv, session));
                						cnt ++;
            						}
            					}
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
    public @ResponseBody List<UserPrivilege> destroy(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<UserPrivilege> userPrivileges = new ArrayList<UserPrivilege>();

    	for (Object id : objs) {
    		UserPrivilege userPrivilege = new UserPrivilege();
    		
    		userPrivilege.setId((int)id);
    		
    		userPrivileges.add(userPrivilege);
    	}
    	
    	try {
        	privService.deleteUserPrivileges(userPrivileges);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return userPrivileges;
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
    
	/**
	 * 읽기 액션 - 롤 정보
	 */
    @RequestMapping(value = "/readRoles", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readRoles(@RequestBody DataSourceRequest request,
    		Locale locale) {
    	try {
    		SortDescriptor sort = new SortDescriptor();
    		sort.setDir("asc");
    		sort.setField("ukid");
    		
    		ArrayList<SortDescriptor> list = new ArrayList<SortDescriptor>();
    		list.add(sort);
    		
    		request.setSort(list);
    		
    		DataSourceResult result = privService.getRoleList(request);
    		
    		for(Object roleObj : result.getData()) {
    			Role role = (Role) roleObj;
    			role.setLocalUkid(Util.getLocalizedMessageString(role.getUkid(), locale));
    		}
    		
            return result;
    	} catch (Exception e) {
    		logger.error("readRoles", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
	/**
	 * 읽기 액션 - 권한 정보
	 */
    @RequestMapping(value = "/readPrivileges", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readPrivilieges(@RequestBody DataSourceRequest request,
    		Locale locale) {
    	try {
    		SortDescriptor sort = new SortDescriptor();
    		sort.setDir("asc");
    		sort.setField("ukid");
    		
    		ArrayList<SortDescriptor> list = new ArrayList<SortDescriptor>();
    		list.add(sort);
    		
    		request.setSort(list);
    		
    		DataSourceResult result = privService.getPrivilegeList(request);
    		
    		for(Object privObj : result.getData()) {
    			Privilege priv = (Privilege) privObj;
    			priv.setLocalUkid(Util.getLocalizedMessageString(priv.getUkid(), locale));
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("readPrivilieges", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
}
