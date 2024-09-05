package kr.co.r2cast.interceptors;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kr.co.r2cast.exceptions.ServerOperationForbiddenException;
import kr.co.r2cast.models.LoginUser;
import kr.co.r2cast.models.fnd.LoginLog;
import kr.co.r2cast.models.fnd.User;
import kr.co.r2cast.models.fnd.service.PrivilegeService;
import kr.co.r2cast.models.fnd.service.UserService;
import kr.co.r2cast.utils.Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class LoginCheckInterceptor extends HandlerInterceptorAdapter {

    @Autowired 
    private PrivilegeService privService;

	@Autowired 
    private UserService userService;

    @Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {

    	// request.getSession(false)가 null을 되돌려줄 수 있으나,
    	// 아래의 Util.isLoginUser(session)에서 null 처리가 되어 있음.
		HttpSession session = request.getSession(false);
		String loginUri = "/";
		
		String requestUri = request.getRequestURI();
		
    	// [WAB] --------------------------------------------------------------------------
    	/*
		// Agent 요청 접근일 때 "통과"
		if (requestUri != null && !requestUri.isEmpty()) {
			if (requestUri.startsWith("/ext/agent/")) {
				return true;
			}
		}
		*/
    	// [WAB] --------------------------------------------------------------------------
    	// [SignCast] ext ----------------------------------------------------------- start
    	//
    	//
		
		// Agent 요청 접근일 때 "통과"
		if (requestUri != null && !requestUri.isEmpty()) {
			if (requestUri.startsWith("/eco/agent/") || requestUri.startsWith("/eco/common/")) {
				return true;
			}
		}
    	
    	//
    	//
    	// [SignCast] ext ------------------------------------------------------------- end
		
		if (session == null) {
			response.sendRedirect(loginUri);
			return false;
		}
		
		session.removeAttribute("prevUri");
		session.removeAttribute("prevQuery");
		
		if (!Util.isLoginUser(session)) {
			if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With")))
			{
				throw new ServerOperationForbiddenException("ToLoginPage");
			} else {
				session.setAttribute("prevUri", requestUri);
				session.setAttribute("prevQuery", Util.parseString(request.getQueryString()));

				response.sendRedirect(loginUri);
			}
			
			return false;
		}
		
		LoginUser loginUser = (LoginUser) session.getAttribute("loginUser");
		if (loginUser == null) {
			response.sendRedirect(loginUri);
			return false;
		}

		String allowedRequestUri = "";
		if (Util.isValid(request.getMethod()) && request.getMethod().equals("GET")) {
			allowedRequestUri = requestUri;
		}
		
		// 동일 계정 동시 사용중 체크
    	if (!Util.hasThisPriv(session, "internal.NoConcurrentCheck")) {
    		LoginLog lastLoginLog = userService.getLastLoginLogByUserId(loginUser.getId());
    		if (lastLoginLog != null && lastLoginLog.getId() != loginUser.getLoginId()) {
    			userService.logout(session, true);
    			
    			if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With")))
    			{
    				throw new ServerOperationForbiddenException("ToLoginPage2");
    			} else {
    				response.sendRedirect("/common/loginAfterForcedLogout");
    			}
    			
    			return false;
    		}
    	}

		// 패스워드 수정 페이지 접근일 때 "통과"
		if (requestUri.startsWith("/common/passwordupdate")) {
			privService.touchRecentAccessMenus(session, request.getLocale(), allowedRequestUri);
			return true;
		}
    	
    	// 패스워드 유효 기간 지정(password.life.days)이 있을 경우
    	int lifeDays = Util.parseInt(Util.getFileProperty("password.life.days"), 0);
    	if (lifeDays > 0) {
    		User user = userService.getUser(loginUser.getId());
    		if (user != null) {
    			Date limitDate = Util.addDays(Util.setMaxTimeOfDate(user.getPasswordUpdateDate()), lifeDays);
    			if (limitDate.compareTo(new Date()) < 0) {
        			response.sendRedirect("/common/passwordupdate?aging=Y");
        			return false;
    			}
    		}
    	}
    	

		// 모든 페이지 접근 권한 가질 때 "통과"
		if (loginUser.isAnyMenuAccessAllowed()) {
			privService.touchRecentAccessMenus(session, request.getLocale(), allowedRequestUri);
			return true;
		}
		
		if (!requestUri.endsWith("/")) {
			requestUri += "/";
		}
		
		List<String> allowedUrlList = loginUser.getAllowedUrlList();
		for (String url : allowedUrlList) {
			String tmpUrl = url;
			if (!tmpUrl.endsWith("/")) {
				tmpUrl += "/";
			}
			
			if (requestUri.startsWith(tmpUrl)) {
				privService.touchRecentAccessMenus(session, request.getLocale(), allowedRequestUri);
				return true;
			}
		}
		
		// 허용되지 않은 페이지 접근
		response.sendRedirect(loginUri);
		
		return false;
	}
}
