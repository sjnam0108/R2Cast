package kr.co.r2cast.controllers.eco;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import kr.co.r2cast.exceptions.ServerOperationForbiddenException;
import kr.co.r2cast.models.EcoMessageManager;
import kr.co.r2cast.models.Message;
import kr.co.r2cast.models.MessageManager;
import kr.co.r2cast.models.ModelManager;
import kr.co.r2cast.models.eco.DispMenu;
import kr.co.r2cast.models.eco.service.OptService;
import kr.co.r2cast.models.fnd.Menu;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.service.PrivilegeService;
import kr.co.r2cast.models.fnd.service.SiteService;
import kr.co.r2cast.utils.Util;
import kr.co.r2cast.viewmodels.fnd.MenuItem;

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
 * 표시 메뉴 컨트롤러
 */
@Controller("eco-curr-disp-menu-controller")
@RequestMapping(value="/eco/currdispmenu")
public class CurrDispMenuController {
	private static final Logger logger = LoggerFactory.getLogger(CurrDispMenuController.class);

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
	 * 표시 메뉴 페이지
	 */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String index(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	modelMgr.addMainMenuModel(model, locale, session, request);
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});

    	// 페이지 제목
    	model.addAttribute("pageTitle", "표시 메뉴");
    	
    	
    	String menus = Util.getFileProperty("menu.link");
    	Site site = siteService.getSite(Util.getSessionSiteId(session));
    	if (site != null) {
        	DispMenu menu = optService.getDispMenuBySiteId(site.getId());
        	if (menu != null) {
        		menus = menu.getUserHome();
        	}
    	}
    	
    	List<String> userHomeMenus = Util.tokenizeValidStr(menus);
    	MenuItem[] selMenus = null;
    	if (userHomeMenus.size() > 0) {
    		selMenus = new MenuItem[userHomeMenus.size()];
    	}
    	
    	ArrayList<MenuItem> optionList = new ArrayList<MenuItem>();
    	List<MenuItem> selectionList = new ArrayList<MenuItem>();
    	
    	List<Menu> menuList = privService.getExecutableMenuList();
    	
    	for(Menu item : menuList) {
    		String iconName = "fal fa-question-circle";
    		if (Util.isValid(item.getIconType())) {
    	    	if (item.getIconType().startsWith("fas fa-") || item.getIconType().startsWith("far fa-") || 
    	    			item.getIconType().startsWith("fal fa-")) {
    	    		iconName = item.getIconType();
    	    	} else {
    	    		iconName = "fal fa-" + item.getIconType();
    	    	}
    		}
    		
    		MenuItem menu = new MenuItem(item.getUkid(), 
    				msgMgr.message("mainmenu." + item.getUkid(), locale), 
    				item.getUrl(), getHierGroupName(item, locale), 
    				item.getIconType(), iconName);
    		
    		if (selMenus != null && userHomeMenus.contains(item.getUkid())) {
    			selMenus[userHomeMenus.indexOf(item.getUkid())] = menu;
    		} else {
    			optionList.add(menu);
    		}
    	}
    	
    	Collections.sort(optionList);
    	
    	if (selMenus != null) {
    		selectionList = Arrays.asList(selMenus);
    	}

    	model.addAttribute("OptMenus", optionList);
    	model.addAttribute("SelMenus", selectionList);

        return "eco/currdispmenu";
    }
	
	/**
	 * 변경 액션
	 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody String update(@RequestBody Map<String, Object> model, Locale locale, 
    		HttpSession session) {
		Site site = siteService.getSite(Util.getSessionSiteId(session));
		if (site == null) {
    		throw new ServerOperationForbiddenException(
    				msgMgr.message("common.server.msg.wrongParamError", locale));
		}
		
		// 새로운 값에 대한 처리 흐름
		// 1) ukid != ""
		//    기존 DB 값 획득하여, 
		//      존재 시 새 값과 비교 -> 동일하면 skip, 다르면 DB 저장
		//      미존재 시 전체사이트 디폴트 값과 비교하여, 다르면 DB 저장
		// 2) ukid == ""
		//    -> DB에 본 사이트의 자료가 있으면 삭제, 없으면 skip
		
		String ukids = (String)model.get("ukids");
		
		try {
			DispMenu menu = optService.getDispMenuBySiteId(site.getId());
			if (Util.isNotValid(ukids) || ukids.length() < 2) {
				if (menu != null) {
					optService.deleteDispMenu(menu);
				}
			} else {
				ukids = Util.removeTrailingChar(ukids);
				
				if (menu == null) {
					if (!ukids.equals(Util.getFileProperty("menu.link"))) {
						optService.saveOrUpdate(new DispMenu(site, ukids, session));
					}
				} else {
					if (!menu.getUserHome().equals(ukids)) {
						menu.setUserHome(ukids);
						menu.touchWho(session);
						
						optService.saveOrUpdate(menu);
					}
				}
			}
		} catch (Exception e) {
    		logger.error("update", e);
    		throw new ServerOperationForbiddenException("SaveError");
    	}

        return "OK";
    }

	private String getHierGroupName(Menu menu, Locale locale) {

		if (menu != null) {
			String dispGroupKey = "menuGroup." + menu.getDispGroup();
			String dispGroupName = msgMgr.message(dispGroupKey, locale);
			
			if (dispGroupKey.equals(dispGroupName)) {
				dispGroupName = "";
			}
			
			if (Util.isValid(dispGroupName)) {
				return dispGroupName;
			} else {
				if (menu.getParent() == null) {
					return "";
				} else {
					return getHierGroupName(menu.getParent(), locale);
				}
			}
		}
		
		return "";
	}
}
