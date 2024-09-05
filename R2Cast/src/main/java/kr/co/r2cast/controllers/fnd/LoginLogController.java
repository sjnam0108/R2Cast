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
import kr.co.r2cast.models.fnd.LoginLog;
import kr.co.r2cast.models.fnd.service.UserService;
import kr.co.r2cast.utils.Util;

/**
 * 로그인 로그 컨트롤러
 */
@Controller("fnd-login-log-controller")
@RequestMapping(value="/fnd/loginlog")
public class LoginLogController {
	private static final Logger logger = LoggerFactory.getLogger(LoginLogController.class);

    @Autowired 
    private UserService userService;
    
	@Autowired
	private MessageManager msgMgr;
    
	@Autowired
	private ModelManager modelMgr;
    
	/**
	 * 로그인 로그 페이지
	 */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String index(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	modelMgr.addMainMenuModel(model, locale, session, request);
    	msgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {
					new Message("pageTitle", "loginlog.title"),
    				new Message("title_loginTime", "loginlog.loginTime"),
    				new Message("title_username", "loginlog.username"),
    				new Message("title_familiarName", "loginlog.familiarName"),
    				new Message("title_ip", "loginlog.ip"),
    				new Message("title_logout", "loginlog.logout"),
    				new Message("title_forcedLogout", "loginlog.forcedLogout"),
    				new Message("title_logoutTime", "loginlog.logoutTime"),
    			});

    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
    	
        return "fnd/loginlog";
    }
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request) {
    	try {
            return userService.getLoginLogList(request);
    	} catch (RuntimeException re) {
    		logger.error("read", re);
    		throw new ServerOperationForbiddenException("ReadError");
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
	/**
	 * 추가 액션
	 */
    // 해당 없음

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
    public @ResponseBody List<LoginLog> destroy(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<LoginLog> loginLogs = new ArrayList<LoginLog>();

    	for (Object id : objs) {
    		LoginLog loginLog = new LoginLog();
    		
    		loginLog.setId((int)id);
    		
    		loginLogs.add(loginLog);
    	}
    	
    	try {
    		userService.deleteLoginLogs(loginLogs);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return loginLogs;
    }
}
