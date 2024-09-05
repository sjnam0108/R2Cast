package kr.co.r2cast.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import kr.co.r2cast.models.fnd.Menu;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.service.PrivilegeService;
import kr.co.r2cast.models.fnd.service.SiteService;
import kr.co.r2cast.utils.SolUtil;
import kr.co.r2cast.utils.Util;
import kr.co.r2cast.viewmodels.fnd.TreeViewItem;

@Component
public class ModelManager {
    
	@Autowired
	private MessageManager msgMgr;

	@Autowired 
    private SiteService siteService;
    
    @Autowired 
    private PrivilegeService privService;
	
	/**
	 * 메인 메뉴 자료를 모델에 추가
	 */
    public void addMainMenuModel(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	if (session != null) {
    		// 세션에 메인 메뉴 자료 존재 파악, 없으면 생성
    		String lang = (String) session.getAttribute("mainMenuLang");
    		@SuppressWarnings("unchecked")
			List<TreeViewItem> data = (List<TreeViewItem>) session.getAttribute("mainMenuData");
    		
    		String appMode = Util.getAppModeFromRequest(request);
    		if (lang == null || !lang.equals(Util.kendoLangCountryCode(locale)) || data == null) {
    			lang = Util.kendoLangCountryCode(locale);
    			data = getMainMenuData(appMode, session, locale);
    			session.setAttribute("mainMenuLang", lang);
    			session.setAttribute("mainMenuData", data);
    		}
    		
    		collapseAllTreeNode(data);
    		
    		int menuTotCnt = getReachableCount(appMode, locale, session);
    		int maxQuickLinkCnt = Util.parseInt(SolUtil.getProperty(session, "quicklink.max.menu"), 5);
    		boolean isOnlyQuickLinkMode = menuTotCnt <= maxQuickLinkCnt;
    		
    		String requestUri = request.getRequestURI();
    		String targetIdStr = expandTreeNodeIncludingParent(data, requestUri);
    		
    		List<TreeViewItem> quickLinks = getQuickLinkMenus(isOnlyQuickLinkMode, appMode, locale, session);
    		if (Util.isValid(targetIdStr)) {
        		for(TreeViewItem item : quickLinks) {
        			if (String.valueOf(item.getId()).equals(targetIdStr)) {
        				item.setChecked(true);
        			}
        		}
    		}
    		
    		model.addAttribute("main_menu_data", data);
    		model.addAttribute("main_quick_link", quickLinks);
    		model.addAttribute("menuTotCnt", menuTotCnt);
    		model.addAttribute("isOnlyQuickLinkMode", isOnlyQuickLinkMode);
    		
    		if (targetIdStr != null && !targetIdStr.isEmpty()) {
    			model.addAttribute("main_menu_current", targetIdStr);
    		}
    		//-
    		
    		// Site Switcher 출력 여부 및 아이콘 이름 설정
    		LoginUser loginUser = (LoginUser) session.getAttribute("loginUser");
			if (loginUser != null && requestUri != null && !requestUri.isEmpty()) {
	    		TreeViewItem tvi = getTreeViewItem(data, requestUri);

				loginUser.setSiteSwitcherShown(
						loginUser.getUserSites().size() > 1 && tvi != null && 
						Util.isValid(tvi.getCustom5()) && tvi.getCustom5().equals("Y"));
				
				loginUser.setIcon(getIconName(tvi));
				
				Site site = siteService.getSite(Util.getSessionSiteId(session));
				if (site == null || Util.isNotValid(site.getShortName())) {
					loginUser.setDispSiteName("");
				} else {
					loginUser.setDispSiteName(site.getShortName());
				}
			}
    	}
    	
    	String logoFile = Util.getLogoPathFile("top", request.getServerName());
    	model.addAttribute("logoTopPathFile", logoFile);
    	model.addAttribute("watermarkDisplayed", !logoFile.startsWith("/resources/shared"));
    	model.addAttribute("logoTitleText", SolUtil.getProperty(session, "R2Cast"));
    }
	
	/**
	 * 사용자의 메인 메뉴 트리 중 첫번째로 접근 가능한 URL 정보를 반환
	 */
    public String getFirstReachableUrl(String appMode, Locale locale, HttpSession session) {
    	if (session == null) {
    		return null;
    	}
    	
    	List<TreeViewItem> items = getMainMenuData(appMode, session, locale);
    	for(TreeViewItem item1 : items) {
    		if (item1.getChildrenCount() > 0) {
    	    	for(TreeViewItem item2 : item1.getItems()) {
    	    		if (item2.getChildrenCount() > 0) {
    	    	    	for(TreeViewItem item3 : item2.getItems()) {
    	    	    		if (item3.getChildrenCount() > 0) {
    	    	    	    	for(TreeViewItem item4 : item3.getItems()) {
    	    	    	    		if (Util.isValid(item4.getCustom1()) && Util.isValid(item4.getCustom2())) {
    	    	    	    			return item4.getCustom2();
    	    	    	    		}
    	    	    	    	}
    	    	    		}
    	    	    		
    	    	    		if (Util.isValid(item3.getCustom1()) && Util.isValid(item3.getCustom2())) {
    	    	    			return item3.getCustom2();
    	    	    		}
    	    	    	}
    	    		}
    	    		
    	    		if (Util.isValid(item2.getCustom1()) && Util.isValid(item2.getCustom2())) {
    	    			return item2.getCustom2();
    	    		}
    	    	}
    		}
    		
    		if (Util.isValid(item1.getCustom1()) && Util.isValid(item1.getCustom2())) {
    			return item1.getCustom2();
    		}
    	}
    	
    	return null;
    }
    
	/**
	 * 사용자의 메인 메뉴 트리 중 실행 가능한 메뉴 항목 수를 반환
	 */
    public int getReachableCount(String appMode, Locale locale, HttpSession session) {
    	if (session == null) {
    		return 0;
    	}
    	
    	int cnt = 0;
    	
    	List<TreeViewItem> items = getMainMenuData(appMode, session, locale);
    	for(TreeViewItem item1 : items) {
    		if (item1.getChildrenCount() > 0) {
    	    	for(TreeViewItem item2 : item1.getItems()) {
    	    		if (item2.getChildrenCount() > 0) {
    	    	    	for(TreeViewItem item3 : item2.getItems()) {
    	    	    		if (item3.getChildrenCount() > 0) {
    	    	    	    	for(TreeViewItem item4 : item3.getItems()) {
    	    	    	    		if (Util.isValid(item4.getCustom1()) && Util.isValid(item4.getCustom2())) {
    	    	    	    			cnt++;
    	    	    	    		}
    	    	    	    	}
    	    	    		}
    	    	    		
    	    	    		if (Util.isValid(item3.getCustom1()) && Util.isValid(item3.getCustom2())) {
    	    	    			cnt++;
    	    	    		}
    	    	    	}
    	    		}
    	    		
    	    		if (Util.isValid(item2.getCustom1()) && Util.isValid(item2.getCustom2())) {
    	    			cnt++;
    	    		}
    	    	}
    		}
    		
    		if (Util.isValid(item1.getCustom1()) && Util.isValid(item1.getCustom2())) {
    			cnt++;
    		}
    	}
    	
    	return cnt;
    }
    
	/**
	 * 빠른 링크 메뉴(레벨 1에서 실행가능한 메뉴) 반환
	 */
    public List<TreeViewItem> getQuickLinkMenus(boolean isOnlyQuickLinkMode, String appMode, Locale locale, HttpSession session) {
    	ArrayList<TreeViewItem> list = new ArrayList<TreeViewItem>();
    	
    	if (session != null) {
        	List<TreeViewItem> items = getMainMenuData(appMode, session, locale);
        	
        	if (isOnlyQuickLinkMode) {
            	for(TreeViewItem item1 : items) {
            		if (item1.getChildrenCount() > 0) {
            	    	for(TreeViewItem item2 : item1.getItems()) {
            	    		if (item2.getChildrenCount() > 0) {
            	    	    	for(TreeViewItem item3 : item2.getItems()) {
            	    	    		if (item3.getChildrenCount() > 0) {
            	    	    	    	for(TreeViewItem item4 : item3.getItems()) {
            	    	    	    		if (Util.isValid(item4.getCustom1()) && Util.isValid(item4.getCustom2())) {
            	    	    	    			list.add(item4);
            	    	    	    		}
            	    	    	    	}
            	    	    		}
            	    	    		
            	    	    		if (Util.isValid(item3.getCustom1()) && Util.isValid(item3.getCustom2())) {
    	    	    	    			list.add(item3);
            	    	    		}
            	    	    	}
            	    		}
            	    		
            	    		if (Util.isValid(item2.getCustom1()) && Util.isValid(item2.getCustom2())) {
    	    	    			list.add(item2);
            	    		}
            	    	}
            		}
            		
            		if (Util.isValid(item1.getCustom1()) && Util.isValid(item1.getCustom2())) {
    	    			list.add(item1);
            		}
            	}
        	} else {
            	for(TreeViewItem item : items) {
            		if (item.getItems() == null && item.getChildrenCount() == 0 && 
            				Util.isValid(item.getCustom1()) && Util.isValid(item.getCustom2())) {
            			list.add(item);
            		}
            	}
        	}
    	}
    	
    	return list;
    }
    
	/**
	 * 대상 페이지의 메뉴 및 그 상위 노드 펼치기
	 */
    private String expandTreeNodeIncludingParent(List<TreeViewItem> list, String uri) {
    	if (uri == null || uri.isEmpty() || list == null) {
    		return null;
    	}
    	
    	TreeViewItem target = getTreeViewItem(list, uri);
    	if (target == null) {
    		return null;
    	}
    	
    	String ret = String.valueOf(target.getId());

    	if (target != null) {
    		do {
    			if (target.getItems() != null && target.getItems().size() > 0) {
    				target.setExpanded(true);
    			} else {
    				target.setChecked(true);
    			}
    			
    			target = getParentTreeViewItem(list, target.getId());
    		} while (target != null);
    	}
    	
    	return ret;
    }
    
	/**
	 * 모든 트리 노드 닫기
	 */
	private void collapseAllTreeNode(List<TreeViewItem> list) {
		for (TreeViewItem item : list) {
			item.setExpanded(false);
			item.setChecked(false);
			
			if (item.getItems() != null && item.getItems().size() > 0) {
				collapseAllTreeNode(item.getItems());
			}
		}
	}
	
	/**
	 * 트리 구조의 자료에서 URL로 TreeViewItem 찾기
	 */
	private TreeViewItem getTreeViewItem(List<TreeViewItem> list, String uri) {
		for (TreeViewItem item : list) {
    		if (item.getCustom2() != null) {
    			if (item.getCustom2().equals(uri)) {
    				return item;
    			}
    		}

			if (item.getItems() != null && item.getItems().size() > 0) {
				TreeViewItem tmp = getTreeViewItem(item.getItems(), uri);
				if (tmp != null) {
					return tmp;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 트리 구조의 자료에서 번호로 TreeViewItem 찾기
	 */
	private TreeViewItem getTreeViewItem(List<TreeViewItem> list, int id) {
		for (TreeViewItem item : list) {
			if (item.getId() == id) {
				return item;
			}
			
			if (item.getItems() != null && item.getItems().size() > 0) {
				TreeViewItem tmp = getTreeViewItem(item.getItems(), id);
				if (tmp != null) {
					return tmp;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 트리 구조의 자료에서 부모 TreeViewItem 찾기
	 */
	private TreeViewItem getParentTreeViewItem(List<TreeViewItem> list, int id) {
		for (TreeViewItem item : list) {
			if (item.getItems() != null && item.getItems().size() > 0) {
				for (TreeViewItem child : item.getItems()) {
					if (child.getId() == id) {
						return item;
					}
				}
				
				TreeViewItem tmp = getParentTreeViewItem(item.getItems(), id);
				if (tmp != null) {
					return tmp;
				}
			}
		}
		
		return null;
	}
	
	private ArrayList<String> getUserMenuUrlList(ArrayList<String> list, String url) {
		if (url != null && !url.isEmpty() && !list.contains(url)) {
			list.add(url);
		}
		
		return list;
	}
	
	/**
	 * 메인 메뉴 트리 구조 자료 반환
	 */
    private List<TreeViewItem> getMainMenuData(String appMode, HttpSession session, Locale locale) {
		List<Menu> menuList = privService.getMenuList();
    	
    	ArrayList<TreeViewItem> list = new ArrayList<TreeViewItem>();
    	LoginUser loginUser = null;
    	
    	// 사용자 소유 권한 체크
    	List<String> userPrivKeys = new ArrayList<String>();
    	if (session != null) {
    		loginUser = (LoginUser) session.getAttribute("loginUser");
    		
    		if (loginUser != null) {
    			userPrivKeys = privService.getAllUserPrivileges(loginUser.getId());
    		}
    	}
    	
    	ArrayList<String> urlList = new ArrayList<String>();
    	
    	// [SignCast] ext ----------------------------------------------------------- start
    	//
    	//
    	
    	if (loginUser != null) {
    		loginUser.setViewRecentTaskAllowed(userPrivKeys.contains("internal.ViewRecentTask"));
    	}
    	
    	//
    	//
    	// [SignCast] ext ------------------------------------------------------------- end
    	
    	boolean anyMenuAccessAllowed = userPrivKeys.contains("internal.AccessAnyMenu");
    	if (loginUser != null) {
    		loginUser.setAnyMenuAccessAllowed(anyMenuAccessAllowed);
    		loginUser.setManageSiteJobAllowed(userPrivKeys.contains("internal.ManageSiteJob"));
    	}
    	
    	if (!anyMenuAccessAllowed) {
        	for (Menu menu : menuList) {
        		if (userPrivKeys.contains("menu." + menu.getUkid())) {
        			urlList = getUserMenuUrlList(urlList, menu.getUrl());
        			
        			Menu tmp = menu.getParent();
        			while (tmp != null) {
        				urlList = getUserMenuUrlList(urlList, tmp.getUrl());
        				
        				tmp = tmp.getParent();
        			}
        		}
        	}
    	}
    	
    	for (Menu menu : menuList) {
    		if (!anyMenuAccessAllowed && !userPrivKeys.contains("menu." + menu.getUkid())) {
    			continue;
    		}
    		if (anyMenuAccessAllowed && menu.isUserFriendly() && !userPrivKeys.contains("menu." + menu.getUkid())) {
    			continue;
    		}
    		
    		TreeViewItem item = new TreeViewItem(menu.getId(), 
    				msgMgr.message("mainmenu." + menu.getUkid(), locale), 
    				menu.getSiblingSeq());
    		
    		item.setCustom1(menu.getUkid());
    		item.setCustom2(menu.getUrl());
    		item.setCustom5(menu.isSiteSelector() ? "Y" : "N");
    		item.setCustom6(menu.isCustomized() ? "Y" : "N");
    		item.setChildrenCount(menu.getSubMenus().size());
    		
    		item.setIcon(getIconName(menu.getIconType()));
    		
    		
    		list.add(item);
    	}

    	for (Menu menu : menuList) {
    		if (!anyMenuAccessAllowed && !userPrivKeys.contains("menu." + menu.getUkid())) {
    			continue;
    		}
    		
    		if (menu.getParent() != null) {
        		TreeViewItem current = getTreeViewItem(list, menu.getId());
        		TreeViewItem parentOfCurrent = getTreeViewItem(list, menu.getParent().getId());
        		
        		parentOfCurrent.addSubItem(current);
        		list.remove(current);
    		}
    	}
    	
    	// 자식 메뉴 수 설정 검증(사용자 홈 메뉴의 자식 메뉴 존재 시 문제 발견)
    	for (TreeViewItem item : list) {
    		if (item.getItems() == null && item.getChildrenCount() != 0) {
    			item.setChildrenCount(0);
    		}
    	}
    	
    	if (Util.isValid(appMode)) {
        	TreeViewItem appUserItem = new TreeViewItem(90000, 
    				msgMgr.message("mainmenu.AppUser", locale), 90000);
        	appUserItem.setIcon("far fa-bell");
        	appUserItem.setCustom1("AppUser");
        	appUserItem.setCustom2("/dsg/appuser");
        	appUserItem.setCustom5("Y");
        	appUserItem.setChildrenCount(0);
        	list.add(appUserItem);
        	
        	urlList = getUserMenuUrlList(urlList, "/dsg/appuser");
    	}
    	
    	
    	// Top Level Menu Sort
        Collections.sort(list, CustomComparator.TreeViewItemSiblingSeqComparator);

        // 사용자 접근 가능한 URL 등록
    	if (loginUser != null) {
    		loginUser.setAllowedUrlList(urlList);
    	}
    	
        return list;
    }
	
    private String getIconName(TreeViewItem tvi) {
    	
    	if (tvi == null) {
    		return "fas fa-arrow-circle-right";
    	} else {
    		return getIconName(tvi.getIcon());
    	}
    }
    
    private String getIconName(String value) {
    	
    	if (Util.isNotValid(value)) {
    		return "fal fa-arrow-circle-right";
    	}
    	
    	if (value.startsWith("fas fa-") || value.startsWith("far fa-") || 
    			value.startsWith("fal fa-")) {
    		return value;
    	} else {
    		return "fal fa-" + value;
    	}
    }
}
