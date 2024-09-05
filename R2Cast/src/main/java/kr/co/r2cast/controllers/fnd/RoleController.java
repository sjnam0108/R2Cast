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
import kr.co.r2cast.models.fnd.Role;
import kr.co.r2cast.models.fnd.service.PrivilegeService;
import kr.co.r2cast.utils.Util;

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

/**
 * 롤 컨트롤러
 */
@Controller("fnd-role-controller")
@RequestMapping(value="/fnd/role")
public class RoleController {
	private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Autowired 
    private PrivilegeService privService;
    
	@Autowired
	private MessageManager msgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	/**
	 * 롤 페이지
	 */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String index(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	modelMgr.addMainMenuModel(model, locale, session, request);
    	msgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {
					new Message("pageTitle", "role.title"),
    				new Message("title_ukid", "role.ukid"),
    				new Message("msg_menu_delNotAllowed", "role.msg.menu.delNotAllowed"),
    				new Message("msg_delNotAllowed", "role.msg.delNotAllowed"),
    			});
    	
        return "fnd/role";
    }
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request,
    		Locale locale) {
    	try {
    		DataSourceResult result = privService.getRoleList(request);
    		
    		for(Object roleObj : result.getData()) {
    			Role role = (Role) roleObj;
    			role.setLocalUkid(Util.getLocalizedMessageString(role.getUkid(), locale));
    		}
    		
            return result;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
	/**
	 * 추가 액션
	 */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody Role create(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	String ukid = (String)model.get("ukid");

    	Role target = new Role(ukid, session);
    	
        saveOrUpdate(target, locale);

        return target;
    }
    
	/**
	 * 변경 액션
	 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody Role update(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	Role target = privService.getRole((int)model.get("id"));
    	if (target != null) {
            target.setUkid((String)model.get("ukid"));
            
            target.touchWho(session);
            
            saveOrUpdate(target, locale);
    	}
    	
        return target;
    }
    
	/**
	 * 추가 / 변경 시의 자료 저장
	 */
    private void saveOrUpdate(Role target, Locale locale) throws ServerOperationForbiddenException {
    	// 비즈니스 로직 검증
    	// 해당 없음
        
        // DB 작업 수행 결과 검증
        try {
            privService.saveOrUpdate(target);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("saveOrUpdate", dive);
        	throw new ServerOperationForbiddenException(msgMgr.message("role.server.msg.sameUkid", locale));
        } catch (ConstraintViolationException cve) {
    		logger.error("saveOrUpdate", cve);
        	throw new ServerOperationForbiddenException(msgMgr.message("role.server.msg.sameUkid", locale));
        } catch (Exception e) {
    		logger.error("saveOrUpdate", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }
    }
    
	/**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody List<Role> destroy(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<Role> roles = new ArrayList<Role>();
    	
    	for (Object id : objs) {
    		Role role = new Role();
    		
    		role.setId((int)id);
    		
    		roles.add(role);
    	}
    	
    	try {
        	privService.deleteRoles(roles);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return roles;
    }
}
