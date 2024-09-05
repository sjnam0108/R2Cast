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
import kr.co.r2cast.models.fnd.Privilege;
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
 * 권한 컨트롤러
 */
@Controller("fnd-privilege-controller")
@RequestMapping(value="/fnd/privilege")
public class PrivilegeController {
	private static final Logger logger = LoggerFactory.getLogger(PrivilegeController.class);

    @Autowired 
    private PrivilegeService privService;
    
	@Autowired
	private MessageManager msgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	/**
	 * 권한 페이지
	 */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String index(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	modelMgr.addMainMenuModel(model, locale, session, request);
    	msgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {
					new Message("pageTitle", "privilege.title"),
    				new Message("title_ukid", "privilege.ukid"),
    				new Message("msg_menu_delNotAllowed", "priviliege.msg.menu.delNotAllowed"),
    				new Message("msg_delNotAllowed", "priviliege.msg.delNotAllowed"),
    			});
    	
        return "fnd/privilege";
    }
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request,
    		Locale locale) {
    	try {
    		DataSourceResult result = privService.getPrivilegeList(request);
    		
    		for(Object privObj : result.getData()) {
    			Privilege priv = (Privilege) privObj;
    			priv.setLocalUkid(Util.getLocalizedMessageString(priv.getUkid(), locale));
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
    public @ResponseBody Privilege create(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	String ukid = (String)model.get("ukid");

    	Privilege target = new Privilege(ukid, session);
    	
        saveOrUpdate(target, locale);

        return target;
    }
    
	/**
	 * 변경 액션
	 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody Privilege update(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	Privilege target = privService.getPrivilege((int)model.get("id"));
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
    private void saveOrUpdate(Privilege target, Locale locale) throws ServerOperationForbiddenException {
    	// 비즈니스 로직 검증
    	// 해당 없음
        
        // DB 작업 수행 결과 검증
        try {
            privService.saveOrUpdate(target);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("saveOrUpdate", dive);
        	throw new ServerOperationForbiddenException(msgMgr.message("priviliege.server.msg.sameUkid", locale));
        } catch (ConstraintViolationException cve) {
    		logger.error("saveOrUpdate", cve);
        	throw new ServerOperationForbiddenException(msgMgr.message("priviliege.server.msg.sameUkid", locale));
        } catch (Exception e) {
    		logger.error("saveOrUpdate", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }
    }
    
	/**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody List<Privilege> destroy(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<Privilege> privileges = new ArrayList<Privilege>();
    	
    	for (Object id : objs) {
    		Privilege privilege = new Privilege();
    		
    		privilege.setId((int)id);
    		
    		privileges.add(privilege);
    	}
    	
    	try {
        	privService.deletePrivileges(privileges);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return privileges;
    }
}
