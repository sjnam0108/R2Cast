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
import kr.co.r2cast.models.DataSourceRequest.FilterDescriptor;
import kr.co.r2cast.models.DataSourceRequest.SortDescriptor;
import kr.co.r2cast.models.fnd.Privilege;
import kr.co.r2cast.models.fnd.Role;
import kr.co.r2cast.models.fnd.RolePrivilege;
import kr.co.r2cast.models.fnd.service.PrivilegeService;
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
 * 롤 권한 컨트롤러
 */
@Controller("fnd-role-privilege-controller")
@RequestMapping(value="/fnd/rolepriv")
public class RolePrivilegeController {
	private static final Logger logger = LoggerFactory.getLogger(RolePrivilegeController.class);

    @Autowired 
    private PrivilegeService privService;
    
	@Autowired
	private MessageManager msgMgr;
    
	@Autowired
	private ModelManager modelMgr;
    
	/**
	 * 롤 권한 페이지
	 */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String index(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	modelMgr.addMainMenuModel(model, locale, session, request);
    	msgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {
					new Message("pageTitle", "roleprivilege.title"),
    				new Message("title_roleUkid", "roleprivilege.roleUkid"),
    				new Message("title_privilegeUkid", "roleprivilege.privilegeUkid"),
    				new Message("title_ukid", "roleprivilege.ukid"),
    				new Message("label_role", "roleprivilege.role"),
    				new Message("label_privilege", "roleprivilege.privilege"),
    			});

    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
        
    	
        return "fnd/rolepriv";
    }
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request,
    		Locale locale) {
    	try {
    		return privService.getRolePrivilegeList(request);
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
    	ArrayList<Object> roleIds = (ArrayList<Object>) model.get("roleIds");
    	ArrayList<Object> privIds = (ArrayList<Object>) model.get("privIds");
    	
		int cnt = 0;

		if (roleIds.size() > 0 && privIds.size() > 0) {
    		try {
        		for(Object roleObj : roleIds) {
        			Role role = privService.getRole((int) roleObj);
        			
        			for(Object privObj : privIds) {
        				Privilege privilege = privService.getPrivilege((int) privObj);
        				
            			if (!privService.isRegisteredRolePrivilege(role.getId(), privilege.getId())) {
            				privService.saveOrUpdate(new RolePrivilege(role, privilege, session));
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
    public @ResponseBody List<RolePrivilege> destroy(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<RolePrivilege> rolePrivileges = new ArrayList<RolePrivilege>();

    	for (Object id : objs) {
    		RolePrivilege rolePrivilege = new RolePrivilege();
    		
    		rolePrivilege.setId((int)id);
    		
    		rolePrivileges.add(rolePrivilege);
    	}
    	
    	try {
        	privService.deleteRolePrivileges(rolePrivileges);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return rolePrivileges;
    }
    
	/**
	 * 읽기 액션 - 롤 정보
	 */
    @RequestMapping(value = "/readRoles", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readRoles(@RequestBody DataSourceRequest request,
    		Locale locale) {
    	try {
    		FilterDescriptor filter = new FilterDescriptor();
    		filter.setField("ukid");
    		filter.setOperator("doesnotcontain");
    		filter.setValue("menu.");
    		
    		FilterDescriptor outer = new FilterDescriptor();
    		outer.setLogic("and");
    		outer.getFilters().add(filter);
    		
    		request.setFilter(outer);
    		
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
    public @ResponseBody DataSourceResult readPrivileges(@RequestBody DataSourceRequest request,
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
    		logger.error("readPrivileges", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
}
