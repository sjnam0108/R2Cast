package kr.co.r2cast.controllers.fnd;

import java.util.ArrayList;
import java.util.Collections;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.co.r2cast.exceptions.ServerOperationForbiddenException;
import kr.co.r2cast.models.CustomComparator;
import kr.co.r2cast.models.FormRequest;
import kr.co.r2cast.models.Message;
import kr.co.r2cast.models.MessageManager;
import kr.co.r2cast.models.ModelManager;
import kr.co.r2cast.models.fnd.Menu;
import kr.co.r2cast.models.fnd.service.PrivilegeService;
import kr.co.r2cast.utils.Util;
import kr.co.r2cast.viewmodels.fnd.TreeViewItem;

/**
 * 메뉴 컨트롤러
 */
@Controller("fnd-menu-controller")
@RequestMapping(value="/fnd/menu")
public class MenuController {
	private static final Logger logger = LoggerFactory.getLogger(MenuController.class);
    
    @Autowired 
    private PrivilegeService privService;
    
	@Autowired
	private MessageManager msgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	/**
	 * 메뉴 페이지
	 */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String index(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	modelMgr.addMainMenuModel(model, locale, session, request);
    	msgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {
					new Message("pageTitle", "menu.title"),
					new Message("title_propertyPanel", "menu.propertyPanel"),
					new Message("title_treePanel", "menu.treePanel"),
					new Message("label_ukid", "menu.ukid"),
					new Message("label_url", "menu.url"),
					new Message("label_icon", "menu.icon"),
					new Message("label_group", "menu.group"),
					new Message("label_new", "menu.new"),
					new Message("label_refresh", "menu.refresh"),
					new Message("label_reqSiteSwitcher", "menu.reqSiteSwitcher"),
					new Message("label_customized", "menu.customized"),
					new Message("label_userFriendly", "menu.userFriendly"),
					new Message("tip_iconTitle", "menu.iconTipTitle"),
					new Message("tip_icon", "menu.iconTip"),
    			});
    	
    	model.addAttribute("MenuGroupItems", privService.getDropDownMenuGroupDispList(locale));
    	
        return "fnd/menu";
    }
    
	/**
	 * 메뉴 트리 구조 자료 반환
	 */
    private List<TreeViewItem> getMenuData(Integer id, Locale locale) {
    	List<Menu> menuList = privService.getMenuListById(id);
    	
    	ArrayList<TreeViewItem> list = new ArrayList<TreeViewItem>();
    	
    	for (Menu menu : menuList) {
    		TreeViewItem item = new TreeViewItem(menu.getId(), 
    				msgMgr.message("mainmenu." + menu.getUkid(), locale), 
    				menu.getSiblingSeq());
    		
    		item.setCustom1(menu.getUkid());
    		item.setCustom2(menu.getUrl());
    		item.setCustom3(menu.getIconType());
    		item.setCustom4(menu.getDispGroup());
    		item.setCustom5(menu.isSiteSelector() ? "Y" : "N");
    		item.setCustom6(menu.isCustomized() ? "Y" : "N");
    		item.setCustom7(menu.isUserFriendly() ? "Y" : "N");
    		item.setChildrenCount(menu.getSubMenus().size());
    		
    		list.add(item);
    	}

    	// Top Level Menu Sort
        Collections.sort(list, CustomComparator.TreeViewItemSiblingSeqComparator);

        return list;
    }
    
	/**
	 * 추가/변경 액션
	 */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public @ResponseBody TreeViewItem create(@RequestBody FormRequest form, Locale locale,
    		HttpSession session) {
    	int id = form.getId();
    	String ukid = form.getUkid();
    	String url = form.getUrl();
    	String oper = form.getOper();
    	String iconType = form.getIcon();
    	String dispGroup = form.getGroup();
    	
    	if (Util.isNotValid(ukid) || Util.isNotValid(oper)) {
    		throw new ServerOperationForbiddenException(
    				msgMgr.message("common.server.msg.wrongParamError", locale));
    	}
    	
    	if (id == 0 && oper.equals("Update")) {
    		throw new ServerOperationForbiddenException(
    				msgMgr.message("common.server.msg.wrongParamError", locale));
    	}
    	
    	iconType = Util.isNotValid(iconType) ? null : iconType;
    	dispGroup = Util.isNotValid(dispGroup) ? null : dispGroup;
    	
    	Menu target;

    	try {
        	if (oper.equals("Update")) {
        		target = privService.getMenu(id);
        		
        		if (target == null) {
            		throw new ServerOperationForbiddenException(
            				msgMgr.message("common.server.msg.wrongParamError", locale));
        		}
        		
        		target.setUkid(ukid);
        		target.setUrl(url);
        		target.setIconType(iconType);
        		target.setDispGroup(dispGroup);
        		target.setSiteSelector(form.isSiteSelector());
        		target.setCustomized(form.isCustomized());
        		target.setUserFriendly(form.isUserFriendly());
        		
        		target.touchWho(session);
            	
        		privService.saveAndReorderMenu(target, target, session);
        	} else {
        		target = new Menu(ukid, url, iconType, dispGroup, form.isSiteSelector(), session);
        		
        		Menu parent = privService.getMenu(id);
        		
        		if (parent != null) {
        			target.setParent(parent);
        		}
            	
        		privService.saveAndReorderMenu(null, target, session);
        	}
        } catch (DataIntegrityViolationException dive) {
        	logger.error("create", dive);
        	throw new ServerOperationForbiddenException(
        			msgMgr.message("menu.server.msg.sameUkid", locale));
        } catch (ConstraintViolationException cve) {
        	logger.error("create", cve);
        	throw new ServerOperationForbiddenException(
        			msgMgr.message("menu.server.msg.sameUkid", locale));
        } catch (Exception e) {
        	logger.error("create", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }

        TreeViewItem item = new TreeViewItem(target.getId(), 
        		msgMgr.message("mainmenu." + target.getUkid(), locale), 
				target.getSiblingSeq());
		
		item.setCustom1(target.getUkid());
		item.setCustom2(target.getUrl());
		item.setCustom3(target.getIconType());
		item.setCustom4(target.getDispGroup());
		item.setCustom5(target.isSiteSelector() ? "Y" : "N");
		item.setCustom6(target.isCustomized() ? "Y" : "N");
		item.setCustom7(target.isUserFriendly() ? "Y" : "N");
		item.setChildrenCount(target.getSubMenus().size());
    	
    	return item;
    }
    
	/**
	 * 마우스 끌어놓기 액션
	 */
    @RequestMapping(value = "/dragdrop", method = RequestMethod.POST)
    @Transactional
    public @ResponseBody String dragDrop(@RequestBody Map<String, Object> model, Locale locale,
    		HttpSession session) {
    	Menu target = privService.getMenu((int)model.get("sourceId"));
    	Menu dest = privService.getMenu((int)model.get("destId"));
    	String dropPosition = (String)model.get("position");
    	
    	if (target == null || dest == null || 
    			!(dropPosition.equals("over") || dropPosition.equals("before") || dropPosition.equals("after"))) {
    		throw new ServerOperationForbiddenException(
    				msgMgr.message("common.server.msg.wrongParamError", locale));
    	}
    	
    	try {
    		Menu parent = target.getParent();
    		
    		if (dropPosition.equals("over")) {
    			target.setParent(dest);
    			target.setSiblingSeq(1000);
    		} else if (dropPosition.equals("before")) {
    			target.setParent(dest.getParent());
    			target.setSiblingSeq(dest.getSiblingSeq() - 1);
    		} else if (dropPosition.equals("after")) {
    			target.setParent(dest.getParent());
    			target.setSiblingSeq(dest.getSiblingSeq() + 1);
    		}
			
			target.touchWho(session);
    		
			privService.saveAndReorderMenu(parent, target, session);
    	} catch (Exception e) {
        	logger.error("dragDrop", e);
    		throw new ServerOperationForbiddenException("OperationError");
    	}

        return "OK";
    }
    
	/**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model) {
    	Menu target = new Menu();
    	target.setId((int)model.get("id"));
    	
    	try {
    		privService.deleteMenu(target);
    	} catch (Exception e) {
        	logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "OK";
    }
    
	/**
	 * 읽기 액션 - 메뉴 정보
	 */
    @RequestMapping(value = "/readMenus", method = RequestMethod.POST)
    public @ResponseBody List<TreeViewItem> readMenus(@RequestBody Map<String, Object> model, Locale locale) {
    	try {
    		return getMenuData((Integer)model.get("id"), locale);
    	} catch (Exception e) {
        	logger.error("readMenus", e);
    		throw new ServerOperationForbiddenException("readError");
    	}
    }
}
