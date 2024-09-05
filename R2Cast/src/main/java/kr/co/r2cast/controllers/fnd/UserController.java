package kr.co.r2cast.controllers.fnd;

import java.util.ArrayList;
import java.util.Date;
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
import kr.co.r2cast.models.Message;
import kr.co.r2cast.models.MessageManager;
import kr.co.r2cast.models.ModelManager;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.SiteUser;
import kr.co.r2cast.models.fnd.User;
import kr.co.r2cast.models.fnd.service.PrivilegeService;
import kr.co.r2cast.models.fnd.service.SiteService;
import kr.co.r2cast.models.fnd.service.UserService;
import kr.co.r2cast.utils.Util;

/**
 * 사용자 컨트롤러
 */
@Controller("fnd-user-controller")
@RequestMapping(value="/fnd/user")
public class UserController {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired 
    private UserService userService;

    @SuppressWarnings("unused")
	@Autowired 
    private PrivilegeService privService;

	@Autowired
	private MessageManager msgMgr;
    
	@Autowired
	private ModelManager modelMgr;
    

	/**
	 * 사용자 페이지
	 */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String index(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	modelMgr.addMainMenuModel(model, locale, session, request);
    	msgMgr.addCommonMessages(model, locale, session, request);
    	
    	msgMgr.addViewMessages(model, locale,
    			new Message[] {
					new Message("pageTitle", "user.title"),
    				new Message("title_username", "user.username"),
    				new Message("title_familiarName", "user.familiarName"),
    				new Message("title_password", "user.password"),
    				new Message("title_effectiveStartDate", "user.effectiveStartDate"),
    				new Message("title_effectiveEndDate", "user.effectiveEndDate"),
    				new Message("cmd_setDefaultPassword", "user.setDefaultPassword"),
    				new Message("msg_wrongLevel2Pwd", "user.msg.wrongLevel2Pwd"),
    				new Message("val_pwdLevel", Util.parseString(Util.getFileProperty("password.level"), "0")),
    			});

    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
        


        return "fnd/user";
    }
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request) {
    	try {
            return userService.getUserList(request);
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
	/**
	 * 추가 액션
	 */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody User create(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	String username = (String)model.get("username");
    	String familiarName = (String)model.get("familiarName");
    	String newPassword = (String)model.get("newPassword");
    	Date effectiveStartDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("effectiveStartDate")));
    	Date effectiveEndDate = Util.setMaxTimeOfDate(Util.parseZuluTime((String)model.get("effectiveEndDate")));
    	
    	// 파라미터 검증
    	if (newPassword == null || newPassword.isEmpty()) {
        	throw new ServerOperationForbiddenException(msgMgr.message("user.server.msg.passwordMandatory", locale));
        }
    	
    	User target = new User(username, familiarName, newPassword, effectiveStartDate, effectiveEndDate, session);
    	
        saveOrUpdate(target, locale, session);

        return target;
    }
    
	/**
	 * 변경 액션
	 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody User update(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	User target = userService.getUser((int)model.get("id"));
    	if (target != null) {
            target.setUsername((String)model.get("username"));
            target.setFamiliarName((String)model.get("familiarName"));
            target.setEffectiveStartDate(Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("effectiveStartDate"))));
            target.setEffectiveEndDate(Util.setMaxTimeOfDate(Util.parseZuluTime((String)model.get("effectiveEndDate"))));
            
            String newPassword = Util.encrypt((String)model.get("newPassword"), target.getSalt());
            if (newPassword != null && ! newPassword.isEmpty()) {
            	target.setPassword(newPassword);
            	target.setNewPassword("");
            	target.setPasswordUpdateDate(new Date());
            }
            
            target.touchWho(session);
            
            saveOrUpdate(target, locale, session);
    	}
    	
        return target;
    }
    
	/**
	 * 추가 / 변경 시의 자료 저장
	 */
    private void saveOrUpdate(User target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {
    	// 비즈니스 로직 검증
        if (target.getEffectiveStartDate() != null && target.getEffectiveEndDate() != null
        		&& target.getEffectiveStartDate().after(target.getEffectiveEndDate())) {
        	throw new ServerOperationForbiddenException(msgMgr.message("common.server.msg.effectivedates", locale));
        }
        
        // DB 작업 수행 결과 검증
        try {
            userService.saveOrUpdate(target);
            
        } catch (DataIntegrityViolationException dive) {
    		logger.error("saveOrUpdate", dive);
        	throw new ServerOperationForbiddenException(msgMgr.message("user.server.msg.sameUsername", locale));
        } catch (ConstraintViolationException cve) {
    		logger.error("saveOrUpdate", cve);
        	throw new ServerOperationForbiddenException(msgMgr.message("user.server.msg.sameUsername", locale));
        } catch (Exception e) {
    		logger.error("saveOrUpdate", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }
    }
    
    /**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody List<User> destroy(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<User> users = new ArrayList<User>();

    	for (Object id : objs) {
    		User user = new User();
    		
    		user.setId((int)id);
    		
    		users.add(user);
    	}
    	
    	try {
        	userService.deleteUsers(users);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return users;
    }
    
    /**
	 * 기본 패스워드 설정 액션
	 */
    @RequestMapping(value = "/defaultpassword", method = RequestMethod.POST)
    public @ResponseBody String defaultPassword(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	try {
        	for (Object id : objs) {
        		User target = userService.getUser((int)id);
        		
        		if (target != null) {
        			target.setPassword(Util.encrypt(
        					Util.parseString(Util.getFileProperty("password.default"), "welcome"), 
        					target.getSalt()));
        			target.setPasswordUpdateDate(new Date());
        			
        			target.touchWho(session);
        			
        			userService.saveOrUpdate(target);
        		}
        	}
    	} catch (Exception e) {
    		logger.error("defaultPassword", e);
    		throw new ServerOperationForbiddenException("OperationError");
    	}

        return "OperationSuccess";
    }
}
