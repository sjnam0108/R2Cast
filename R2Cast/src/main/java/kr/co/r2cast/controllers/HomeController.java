package kr.co.r2cast.controllers;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.PrivateKey;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import kr.co.r2cast.exceptions.ServerOperationForbiddenException;
import kr.co.r2cast.info.GlobalInfo;
import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.EcoUserCookie;
import kr.co.r2cast.models.ExcelDownloadView;
import kr.co.r2cast.models.FormRequest;
import kr.co.r2cast.models.LoginUser;
import kr.co.r2cast.models.Message;
import kr.co.r2cast.models.MessageManager;
import kr.co.r2cast.models.ModelManager;
import kr.co.r2cast.models.fnd.LoginLog;
import kr.co.r2cast.models.fnd.User;
import kr.co.r2cast.models.fnd.service.UserService;
import kr.co.r2cast.utils.Util;

/**
 * 홈 컨트롤러
 */
@Controller("home-controller")
@RequestMapping(value="")
public class HomeController {
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@Autowired 
    private UserService userService;

	@Autowired
	private MessageManager msgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	/**
	 * 웹 애플리케이션 컨텍스트 홈
	 */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model, Locale locale, HttpServletRequest request,
    		HttpSession session) {
    	
		String logoutType = Util.parseString(request.getParameter("forcedLogout"));
		if (Util.isValid(logoutType) && logoutType.equals("true")) {
	    	model.addAttribute("forcedLogout", true);
		}

		String appMode = Util.getAppModeFromRequest(request);
		
		if (Util.isValid(appMode)) {
			if (appMode.equals("A") || appMode.equals("I")) {
				return "forward:/applogin";
			} else {
				return "forward:/home";
			}
		} else {
			return "forward:/home";
		}
	}
	
	/**
	 * 로그인 페이지
	 */
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String toLogin(Model model, Locale locale, HttpServletRequest request,
    		HttpSession session) {
    	msgMgr.addCommonMessages(model, locale, session, request);
    	
    	msgMgr.addViewMessages(model, locale,
    			new Message[] {
   					new Message("pageTitle", "home.title"),
       				new Message("label_username", "home.username"),
       				new Message("label_password", "home.password"),
    				new Message("tip_remember", "home.remember"),
    				new Message("btn_login", "home.login"),
    				new Message("msg_forcedLogout", "home.msg.forcedLogout"),
    			});

    	model.addAttribute("logoPathFile", Util.getLogoPathFile("login", request.getServerName()));
    	
    	Util.prepareKeyRSA(model, session);
    	
		return "home";
	}
	
	/**
	 * FavIcon 액션
	 */
    @RequestMapping(value = "/favicon.ico", method = RequestMethod.GET)
    public void favicon(HttpServletRequest request, HttpServletResponse response) {
    	try {
    		response.sendRedirect("/resources/favicon.ico");
    	} catch (IOException e) {
    		logger.error("favicon", e);
    	}
    }

	/**
	 * 로그인 암호키 확인 액션
	 */
    @RequestMapping(value = "/loginkey", method = RequestMethod.POST)
    public @ResponseBody String checkLoginKey(@RequestBody Map<String, Object> model) {
    	
    	String clientKey = Util.parseString((String)model.get("key"));
    	
    	return (Util.isValid(clientKey) && clientKey.equals(GlobalInfo.RSAKeyMod)) ? "Y" : "N";
    }
    
	/**
	 * 로그인 프로세스
	 */
    private String doLogin(String username, String password, String appMode, HttpSession session, 
    		Locale locale, HttpServletRequest request, HttpServletResponse response) {
    	
    	if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
    		return "home.server.msg.wrongIdPwd";
    	}
    	
    	// RSA 인코딩되었을 때의 처리
    	if (username.length() == 512 && password.length() == 512) {
    		PrivateKey privateKey = (PrivateKey) session.getAttribute("rsaPrivateKey");
    		if (privateKey != null) {
    			username = Util.decryptRSA(privateKey, username);
    			password = Util.decryptRSA(privateKey, password);
    		}
    	}
    	//-
    	
    	User dbUser = userService.getUser(username);
    	if (dbUser == null || !Util.isSameUserPassword(password, dbUser.getSalt(), dbUser.getPassword())) {
    		logger.info("Login Error(WrongIdPwd): {}/{}", username, password);
    		
    		return "home.server.msg.wrongIdPwd";
    	}
    	
    	// 여기까지 오면 패스워드까지 일치
    	if (!userService.isEffectiveUser(dbUser)) {
    		logger.info("Login Error(EffectiveDate): {}/{}", username, password);
    		
    		return "home.server.msg.notEffectiveUser";
    	}
    	
    	LoginLog lastLoginLog = userService.getLastLoginLogByUserId(dbUser.getId());
    	if (lastLoginLog != null) {
        	DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
        	
    		session.setAttribute("loginUserLastLoginTime", df.format(lastLoginLog.getWhoCreationDate()));
    	}
    	
    	LoginLog loginLog = new LoginLog(request.getRemoteAddr(), dbUser, session);

    	try {
        	userService.saveOrUpdate(loginLog);
        	
        	LoginUser successLoginUser = new LoginUser(dbUser, loginLog.getId());
        	successLoginUser.setUserSites(userService.getUserSites(dbUser.getId()));
        	
        	session.setAttribute("loginUser", successLoginUser);
    		
        	         	EcoUserCookie userCookie = new EcoUserCookie(request);
        	         	if (Util.isValid(appMode)) {
        	          		userCookie.setAppMode(appMode);
        	          	}
        	          	
        	         	session.setAttribute("userCookie", userCookie);

        	String cookieKey = "currentSiteId_" + dbUser.getId();
        	String cookieValue = Util.cookieValue(request, cookieKey);
        	
        	if (cookieValue == null) {
        		// LoginUser의 dropdownlist의 첫 값 획득
        		// 있으면 변수에 설정하고 쿠키 저장
        		// 없으면 패스
        		cookieValue = successLoginUser.getFirstSiteIdInUserSites();
        		if (cookieValue != null) {
        			response.addCookie(Util.cookie(cookieKey, cookieValue));
        		}
        	} else {
        		// LoginUser의 dropdownlist의 값 존재 확인
        		// 있으면 패스
        		// 없으면 
        		// LoginUser의 dropdownlist의 첫 값 획득
        		// 있으면 변수에 설정하고 쿠키 저장
        		// 없으면 패스
        		if (!successLoginUser.hasSiteIdInUserSites(cookieValue)) {
            		cookieValue = successLoginUser.getFirstSiteIdInUserSites();
            		if (cookieValue != null) {
            			response.addCookie(Util.cookie(cookieKey, cookieValue));
            		}
        		}
        	}
        	
        	session.setAttribute("currentSiteId", cookieValue);
        	
        	session.removeAttribute("mainMenuLang");
        	session.removeAttribute("mainMenuData");
        	
        	// 세션 무효화 권한 사용자 처리
        	if (Util.hasThisPriv(session, "internal.NoTimeOut")) {
        		session.setMaxInactiveInterval(-1);
        	}
        	
        	// 사용자의 View 설정
        	userService.setUserViews(successLoginUser, cookieValue, null, session, locale);
    	} catch (Exception e) {
    		logger.error("doLogin", e);
    		
    		return "common.server.msg.loginError";
    	}

        return "OK";
    }
    
	/**
	 * 로그인 액션
	 */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public @ResponseBody String login(@RequestBody User loginUser, HttpSession session, 
    		Locale locale, HttpServletRequest request, HttpServletResponse response) {
    	
    	String username = loginUser.getUsername();
    	String password = loginUser.getPassword();
    	
    	String result = doLogin(username, password, "", session, locale, request, response);
    	
    	if (result.equals("OK")) {
    		return result;
    	} else {
    		throw new ServerOperationForbiddenException(msgMgr.message(result, locale));
    	}
    }
    
	/**
	 * 앱 로그인 액션
	 */
    @RequestMapping(value = "/applogin", method = RequestMethod.POST)
    public void appLogin(HttpServletRequest request, HttpSession session, 
    		Locale locale, HttpServletResponse response) throws ServletException, IOException {
    	
    	String username = Util.parseString(request.getParameter("username"));
    	String password = Util.parseString(request.getParameter("password"));
    	String appMode = Util.parseString(request.getParameter("appMode"));
    	
    	String result = doLogin(username, password, appMode, session, locale, request, response);
        
        PrintWriter out = new PrintWriter(new OutputStreamWriter(
      		  response.getOutputStream(), "UTF-8"));
        
        response.setHeader("Cache-Control", "no-store");              // HTTP 1.1
        response.setHeader("Pragma", "no-cache, must-revalidate");    // HTTP 1.0
        response.setContentType("text/plain;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        out.print(result);
        out.close();
    }
    
	/**
	 * SSO 로그인 프로세스
	 */
    private String doSsoLogin(String username, String fromUrl, HttpSession session, 
    		Locale locale, HttpServletRequest request, HttpServletResponse response) {
    	
    	if (Util.isNotValid(username) || Util.isNotValid(fromUrl)) {
    		logger.info("SSO Login Error: No username or url");
    		
    		return "NoUserOrUrlValue";
    	}
    	
    	// Referer 확인
    	String ssoAllowedUrl = Util.parseString(Util.getFileProperty("url.sso.from"), "R2Cast");
		if (!fromUrl.startsWith(ssoAllowedUrl)) {
    		logger.info("SSO Login Error: Not allowed referer: {}", fromUrl);
    		
    		return "NotAllowedReferer";
		}
    	
    	
    	User dbUser = userService.getUser(username);
    	if (dbUser == null) {
    		logger.info("SSO Login Error: {}", username);
    		
    		return "NoUser";
    	}

    	if (!Util.hasThisPriv(dbUser.getId(), "internal.LoginSSO")) {
    		logger.info("SSO Login Error(LoginSSO priv): {}", username);
    		
    		return "NoPriv";
    	}

    	if (!userService.isEffectiveUser(dbUser)) {
    		logger.info("SSO Login Error(EffectiveDate): {}", username);
    		
    		return "NotEffectiveUser";
    	}
    	
    	LoginLog lastLoginLog = userService.getLastLoginLogByUserId(dbUser.getId());
    	if (lastLoginLog != null) {
        	DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
        	
    		session.setAttribute("loginUserLastLoginTime", df.format(lastLoginLog.getWhoCreationDate()));
    	}
    	
    	LoginLog loginLog = new LoginLog(request.getRemoteAddr(), dbUser, session);

    	try {
        	userService.saveOrUpdate(loginLog);
        	
        	LoginUser successLoginUser = new LoginUser(dbUser, loginLog.getId());
        	successLoginUser.setUserSites(userService.getUserSites(dbUser.getId()));
        	
        	session.setAttribute("loginUser", successLoginUser);

        	
        	// [SignCast] ext ----------------------------------------------------------- start
        	//
        	//
    		
//        	DsgUserCookie userCookie = new DsgUserCookie(request);
//        	session.setAttribute("userCookie", userCookie);
        	
        	//
        	//
        	// [SignCast] ext ------------------------------------------------------------- end

        	String cookieKey = "currentSiteId_" + dbUser.getId();
        	String cookieValue = Util.cookieValue(request, cookieKey);
        	
        	if (cookieValue == null) {
        		// LoginUser의 dropdownlist의 첫 값 획득
        		// 있으면 변수에 설정하고 쿠키 저장
        		// 없으면 패스
        		cookieValue = successLoginUser.getFirstSiteIdInUserSites();
        		if (cookieValue != null) {
        			response.addCookie(Util.cookie(cookieKey, cookieValue));
        		}
        	} else {
        		// LoginUser의 dropdownlist의 값 존재 확인
        		// 있으면 패스
        		// 없으면 
        		// LoginUser의 dropdownlist의 첫 값 획득
        		// 있으면 변수에 설정하고 쿠키 저장
        		// 없으면 패스
        		if (!successLoginUser.hasSiteIdInUserSites(cookieValue)) {
            		cookieValue = successLoginUser.getFirstSiteIdInUserSites();
            		if (cookieValue != null) {
            			response.addCookie(Util.cookie(cookieKey, cookieValue));
            		}
        		}
        	}
        	
        	session.setAttribute("currentSiteId", cookieValue);
        	
        	session.removeAttribute("mainMenuLang");
        	session.removeAttribute("mainMenuData");
        	
        	// 세션 무효화 권한 사용자 처리
        	if (Util.hasThisPriv(session, "internal.NoTimeOut")) {
        		session.setMaxInactiveInterval(-1);
        	}
        	
        	// 사용자의 View 설정
        	userService.setUserViews(successLoginUser, cookieValue, null, session, locale);
    	} catch (Exception e) {
    		logger.error("doSsoLogin", e);
    		
    		return "Exception";
    	}

        return "OK";
    }
    
	/**
	 * 로그아웃 액션
	 */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ModelAndView logout(HttpSession session) {
    	userService.logout(session);
    	
    	return new ModelAndView("redirect:/");
    }
    
	/**
	 * 패스워드 변경 액션
	 */
    @RequestMapping(value = "/passwordupdate", method = RequestMethod.POST)
    public @ResponseBody String updatePassword(@RequestBody FormRequest form, HttpSession session, 
    		Locale locale, HttpServletRequest request) {
    	String currentPwd = form.getCurrentPassword();
    	String newPwd = form.getNewPassword();
    	String confirmPwd = form.getConfirmPassword();
    	
    	LoginUser loginUser = (LoginUser) session.getAttribute("loginUser");

    	if (currentPwd == null || currentPwd.isEmpty() || newPwd == null || newPwd.isEmpty() ||
    			confirmPwd == null || confirmPwd.isEmpty() || loginUser == null) {
    		throw new ServerOperationForbiddenException(msgMgr.message("common.server.msg.wrongParamError", locale));
    	}
    	
    	// RSA 인코딩되었을 때의 처리
    	if (currentPwd.length() == 512 && newPwd.length() == 512 && confirmPwd.length() == 512) {
    		PrivateKey privateKey = (PrivateKey) session.getAttribute("rsaPrivateKey");
    		if (privateKey != null) {
    			currentPwd = Util.decryptRSA(privateKey, currentPwd);
    			newPwd = Util.decryptRSA(privateKey, newPwd);
    			confirmPwd = Util.decryptRSA(privateKey, confirmPwd);
    		}
    	}
    	//-
    	
    	if (!newPwd.equals(confirmPwd)) {
    		throw new ServerOperationForbiddenException(msgMgr.message("passwordupdate.msg.samePassword", locale));
    	}
    	
    	User dbUser = userService.getUser(loginUser.getId());
    	if (dbUser == null || !Util.isSameUserPassword(currentPwd, dbUser.getSalt(), dbUser.getPassword())) {
    		throw new ServerOperationForbiddenException(msgMgr.message("common.server.msg.wrongParamError", locale));
    	}
    	
    	// 지금부터 새 패스워드 저장
    	dbUser.setPassword(Util.encrypt(newPwd, dbUser.getSalt()));
    	dbUser.setPasswordUpdateDate(new Date());
        
    	dbUser.touchWho(session);
    	
        try {
            userService.saveOrUpdate(dbUser);
        } catch (Exception e) {
    		logger.error("updatePassword", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }

        return "OK";
    }
	
	/**
	 * 엑셀 다운로드 최종 페이지
	 */
	@RequestMapping(value = "/export", method = RequestMethod.GET)
    public View excelExportFile() {
    	return new ExcelDownloadView();
    }
	
	/**
	 * 사용자의 현재 사이트 변경 액션
	 */
	@RequestMapping(value = "/changesite", method = RequestMethod.GET)
    public ModelAndView changeSite(HttpServletRequest request, HttpServletResponse response, 
    		HttpSession session, Locale locale) {
		String userUrl = request.getParameter("uri");
		
		if (userUrl == null || userUrl.isEmpty()) {
			userUrl = "/userhome";
		}

		if (session != null) {
			LoginUser loginUser = (LoginUser) session.getAttribute("loginUser");
			if (loginUser != null) {
				String cookieKey = "currentSiteId_" + Util.loginUserId(session);
				String cookieValue = request.getParameter("siteId");

				if (!loginUser.hasSiteIdInUserSites(cookieValue)) {
		    		cookieValue = loginUser.getFirstSiteIdInUserSites();
				}

				response.addCookie(Util.cookie(cookieKey, cookieValue));
				
				session.setAttribute("currentSiteId", cookieValue);
	        	
	        	// 사용자의 View 설정
	        	userService.setUserViews(loginUser, cookieValue, null, session, locale);
			}
		}
		
    	return new ModelAndView("redirect:" + userUrl);
    }
	
	/**
	 * 사용자의 현재 뷰 변경 액션
	 */
	@RequestMapping(value = "/changeview", method = RequestMethod.GET)
    public ModelAndView changeView(HttpServletRequest request, HttpServletResponse response, 
    		HttpSession session, Locale locale) {
		String userUrl = request.getParameter("uri");
		
		if (userUrl == null || userUrl.isEmpty()) {
			userUrl = "/userhome";
		}

		if (session != null) {
			LoginUser loginUser = (LoginUser) session.getAttribute("loginUser");
			if (loginUser != null) {
				String viewId = request.getParameter("viewId");

				if (!loginUser.hasViewIdInUserViews(viewId)) {
					viewId = loginUser.getFirstViewIdInUserViews();
				}
	        	
	        	// 사용자의 View 설정
	        	userService.setUserViews(loginUser, Util.getSessionSiteId(session) + "", 
	        			viewId, session, locale);
			}
		}
		
    	return new ModelAndView("redirect:" + userUrl);
    }
	
    /**
     * 로컬 파일 저장을 지원하지 않는 브라우저를 위한 프록시 기능 액션
     * 대상 브라우저: IE9 혹은 그 이하, Safari
     */
    
    @RequestMapping(value = "/proxySave", method = RequestMethod.POST)
    public @ResponseBody void save(String fileName, String base64, 
    		String contentType, HttpServletResponse response) throws IOException {

        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

        response.setContentType(contentType);

        byte[] data = DatatypeConverter.parseBase64Binary(base64);

        response.setContentLength(data.length);
        response.getOutputStream().write(data);
        response.flushBuffer();
    }
    
    /**
     * 로그인 후 엔트리 페이지로 이동 액션
     */
    @RequestMapping(value = "/userhome", method = RequestMethod.GET)
    public ModelAndView userhome(HttpSession session, Locale locale, HttpServletRequest request) {
    	
    	// getFirstReachableUrl의 로직을 먼저 실행 후 선택 필요
    	String url = modelMgr.getFirstReachableUrl(Util.getAppModeFromRequest(request), locale, session);
    	
    	String tmp = Util.parseString((String)request.getParameter("dst"));
    	if (Util.isValid(tmp)) {
    		url =  tmp;
    	}
		
		if (Util.isValid(url)) {
			return new ModelAndView("redirect:" + url);
		} else {
			return new ModelAndView("redirect:/common/passwordupdate");
		}
    }
	
	/**
	 * 앱 로그인 페이지
	 */
    @RequestMapping(value = "/applogin", method = RequestMethod.GET)
    public String appLogin(Model model, Locale locale, HttpServletRequest request,
    		HttpSession session) {
    	
    	// 하이브리드 앱에서의 접근 z코드
    	model.addAttribute("appMode", Util.getAppModeFromRequest(request));
    	
		return "applogin";
	}
	
	//
	// [SignCast] ext -----------------------------------------------------------------
	//
	
	//
	// Spring Version 기준
	//
	// 1. /info/stb -> /dsg/agent/stbinfo
	// 2. /mon/stbsttsreport -> /dsg/agent/stbsttsreport
	// 3. /info/dctnt -> /dsg/agent/dctntinfo
	// 4. /mon/stbrccmd -> /dsg/agent/stbrccmd
	// 5. /info/dctntreport -> /dsg/agent/dctntreport
	// 11. /mgrlogin -> /dsg/mgr/mgrlogin
	// 12. /mgrstb -> /dsg/mgr/mgrstb
	// 13. /mgrcontentfile -> /dsg/mgr/mgrcontentfile
	// 14. /mgrschedule -> /dsg/mgr/mgrschedule
	// 15. /info/repos -> /dsg/mgr/mgrrepos
	// 16. /info/server -> /dsg/mgr/mgrserver
	
	//
	/**
	 * STB 초기 구동 시 서버에 등록된 STB 정보 반환
	 */
	@RequestMapping(value = "/info/rvm", method = RequestMethod.GET)
    public String rvmInfo(HttpServletRequest request, HttpServletResponse response, 
    		HttpSession session) {
		return "forward:/eco/agent/rvminfo";
    }
	
	/**
	 * STB 구동 후 전달된 STB 정보에 따른 STB 상태 보고
	 */
	@RequestMapping(value = "/mon/rvmsttsreport", method = RequestMethod.GET)
    public String rvmStatusReport(HttpServletRequest request, HttpServletResponse response, 
    		HttpSession session) {
		return "forward:/eco/agent/rvmsttsreport";
    }
	
	/**
	 * 컨텐츠 동기화 시 필요한 게시된 컨텐츠 목록 획득
	 */
	@RequestMapping(value = "/info/dctnt", method = RequestMethod.GET)
    public String dctntInfo(HttpServletRequest request, HttpServletResponse response, 
    		HttpSession session) {
		return "forward:/eco/agent/dctntinfo";
    }
	
	/**
	 * 컨텐츠 동기화 시 STB 존재 컨텐츠 파일 보고
	 */
	@RequestMapping(value = "/info/dctntreport", method = { RequestMethod.GET, RequestMethod.POST })
    public String dctntReport(HttpServletRequest request, HttpServletResponse response, 
    		HttpSession session) {
		return "forward:/eco/agent/dctntreport";
    }
	
	/**
	 * STB 원격제어 명령 수행 결과 보고
	 */
	@RequestMapping(value = "/mon/rvmrccmd", method = RequestMethod.GET)
    public String rvmRcCmd(HttpServletRequest request, HttpServletResponse response, 
    		HttpSession session) {
		return "forward:/eco/agent/rvmrccmd";
    }
	
	/**
	 * SignCast Manager 로그인 인증
	 */
	@RequestMapping(value = "/mgrlogin", method = RequestMethod.POST)
    public String mgrLogin(HttpServletRequest request, HttpServletResponse response) {
		return "forward:/eco/mgr/mgrlogin";
    }
	
	/**
	 * SignCast Manager 기기 정보 동기화
	 */
	@RequestMapping(value = "/mgrrvm", method = RequestMethod.POST)
    public String mgrRvm(HttpServletRequest request, HttpServletResponse response) {
		return "forward:/eco/mgr/mgrrvm";
    }
	
	/**
	 * SignCast Manager 컨텐츠 동기화 시 필요한 서버 컨텐츠 파일 목록
	 */
	@RequestMapping(value = "/mgrcontentfile", method = RequestMethod.POST)
    public String mgrContent(HttpServletRequest request, HttpServletResponse response) {
		return "forward:/eco/mgr/mgrcontentfile";
    }
	
	/**
	 * SignCast Manager 스케줄 목록
	 */
	@RequestMapping(value = "/mgrschedule", method = RequestMethod.POST)
    public String mgrSchedule(HttpServletRequest request, HttpServletResponse response) {
		return "forward:/eco/mgr/mgrschedule";
    }
	
	/**
	 * SignCast Manager 저장소 목록
	 */
	@RequestMapping(value = "/info/repos", method = RequestMethod.POST)
    public String mgrRepository(HttpServletRequest request, HttpServletResponse response) {
		return "forward:/eco/mgr/mgrrepos";
    }
	
	/**
	 * SignCast Manager 서버 소프트웨어 정보
	 */
	@RequestMapping(value = "/info/server", method = RequestMethod.GET)
    public String mgrServer(HttpServletRequest request, HttpServletResponse response) {
		return "forward:/eco/mgr/mgrserver";
    }

	
	/**
	 * SignCast Update 액션
	 */
    @RequestMapping(value = "/R2CastUpdate.xml", method = { RequestMethod.GET, RequestMethod.POST })
    public void updateXml(HttpServletRequest request, HttpServletResponse response) {
    	try {
    		response.sendRedirect("/eco/agent/updateinfo");
    	} catch (IOException e) {
    		logger.error("updateXml", e);
    	}
    }
    
    /**
	 * 자산 QR 접근 시 이동
	 */
    @RequestMapping(value = "/asset", method = RequestMethod.GET)
    public void viewQR(Model model, Locale locale, HttpServletRequest request, 
    		HttpServletResponse response, HttpSession session) {
    	String ID = Util.parseString(request.getParameter("ID"), "");
    	
    	try {
    		response.sendRedirect("/ast/astview?ID=" + ID);
    	} catch (IOException e) {
    		logger.error("viewQR", e);
    	}
	}
	
	/**
	 * SSO 페이지(로그인 처리를 위한 임시 페이지)
	 */
    @RequestMapping(value = "/sso", method = RequestMethod.GET)
    public String toSsoLogin(Model model, Locale locale, HttpServletRequest request,
    		HttpSession session) {
    	
    	msgMgr.addCommonMessages(model, locale, session, request);
    	
    	msgMgr.addViewMessages(model, locale,
    			new Message[] {
   					new Message("user", Util.parseString(request.getParameter("user"), "")),
   					new Message("referer", Util.parseString((String)request.getHeader("referer"), "")),
    			});
    	
		return "ssologin";
	}
    
	/**
	 * SSO 로그인 액션
	 */
    @RequestMapping(value = "/ssologin", method = RequestMethod.POST)
    public @ResponseBody String ssoLogin(@RequestBody DataSourceRequest formRequest, HttpSession session, 
    		Locale locale, HttpServletRequest request, HttpServletResponse response) {
    	
    	String username = formRequest.getReqStrValue1();
    	String referer = formRequest.getReqStrValue2();
    	

    	String result = doSsoLogin(username, referer, session, locale, request, response);
    	
    	if (result.equals("OK")) {
    		// 로그인 처리 시 실행 필요!!
    		modelMgr.getFirstReachableUrl(Util.getAppModeFromRequest(request), locale, session);
    		
    		return result;
    	} else {
    		throw new ServerOperationForbiddenException(msgMgr.message(result, locale));
    	}
    }

}
