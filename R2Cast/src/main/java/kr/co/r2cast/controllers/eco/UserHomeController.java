package kr.co.r2cast.controllers.eco;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import kr.co.r2cast.info.EcoGlobalInfo;
import kr.co.r2cast.models.EcoMessageManager;
import kr.co.r2cast.models.LoginUser;
import kr.co.r2cast.models.Message;
import kr.co.r2cast.models.MessageManager;
import kr.co.r2cast.models.ModelManager;
import kr.co.r2cast.models.eco.DispMenu;
import kr.co.r2cast.models.eco.service.OptService;
import kr.co.r2cast.models.fnd.Privilege;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.UserPrivilege;
import kr.co.r2cast.models.fnd.service.PrivilegeService;
import kr.co.r2cast.models.fnd.service.SiteService;
import kr.co.r2cast.utils.Util;
import kr.co.r2cast.viewmodels.QuickLinkItem;

/**
 * 사용자 홈 컨트롤러
 */
@Controller("eco-user-home-controller")
@RequestMapping(value="/eco/userhome")
public class UserHomeController {

    @Autowired 
    private SiteService siteService;

    @Autowired 
    private OptService optService;
    
    @Autowired 
    private PrivilegeService privService;

    @Autowired
	private MessageManager msgMgr;

	@Autowired
	private EcoMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	/**
	 * 사용자 홈 페이지
	 */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String index(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	modelMgr.addMainMenuModel(model, locale, session, request);
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {
					new Message("pageTitle", "userhome.title"),
					new Message("tip_quickLink", "userhome.quickLink"),
					new Message("tip_visit", "userhome.visit"),
					
					new Message("dfFireAlarm", "dataFeed.fireAlarm"),
					new Message("dfFireAlarmDesc", "dataFeed.fireAlarmDesc"),
					new Message("dfDrill", "dataFeed.drill"),
					new Message("dfEmergency", "dataFeed.emergency"),
    			});
    	
    	String menus = Util.getFileProperty("menu.link");
    	Site site = siteService.getSite(Util.getSessionSiteId(session));
    	if (site != null) {
        	DispMenu menu = optService.getDispMenuBySiteId(site.getId());
        	if (menu != null) {
        		menus = menu.getUserHome();
        	}
    	}
    	
    	List<QuickLinkItem> list = new ArrayList<QuickLinkItem>();
    	List<String> menuUkids = Util.tokenizeValidStr(menus);

    	for(String ukid : menuUkids) {
    		QuickLinkItem item = privService.getQuickLinkMenuItem(ukid, locale);
    		
    		if (item != null) {
    			list.add(item);
    		}
    	}
    	
    	List<QuickLinkItem> candList = new ArrayList<QuickLinkItem>();

    	if (session != null) {
    		LoginUser loginUser = (LoginUser) session.getAttribute("loginUser");
    		if (loginUser != null) {
            	// 사용자 소유 권한 체크
            	ArrayList<String> userPrivKeys = new ArrayList<String>();

    			List<UserPrivilege> userPrivList = 
    					privService.getUserPrivilegeListByUserId(loginUser.getId());
    			
    			for (UserPrivilege userPriv : userPrivList) {
    				Privilege priv = userPriv.getPrivilege();
    				
    				if (!userPrivKeys.contains(priv.getUkid())) {
    					userPrivKeys.add(priv.getUkid());
    				}
    			}
    			
    	    	for(QuickLinkItem item : list) {
    	    		if (userPrivKeys.contains(item.getUkid()) || userPrivKeys.contains("internal.AccessAnyMenu")) {
    	    			candList.add(item);
    	    		}
    	    	}
    			
    		}
    	}
    	
    	String lastLogin = (String) session.getAttribute("loginUserLastLoginTime");
    	if (Util.isValid(lastLogin)) {
    		session.removeAttribute("loginUserLastLoginTime");
    		
        	model.addAttribute("LastLogin", msgMgr.message("userhome.lastLogin", locale).replace("{0}", lastLogin));
    	}
    	
    	model.addAttribute("QuickLinkItems", candList);
    	
    	model.addAttribute("fireAlarmSwitchDisplayed", Util.hasThisPriv(session, 
    			"internal.ControlFireAlarm"));
    	
    	model.addAttribute("fireAlarmType", EcoGlobalInfo.FireAlarmType);
    	model.addAttribute("fireAlarmActive", 
    			EcoGlobalInfo.FireAlarmActive == null ? false : EcoGlobalInfo.FireAlarmActive.booleanValue());
    	
        return "eco/userhome";
    }
}
