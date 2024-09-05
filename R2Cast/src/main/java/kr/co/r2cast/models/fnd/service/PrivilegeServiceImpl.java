package kr.co.r2cast.models.fnd.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.r2cast.models.CustomComparator;
import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.MessageManager;
import kr.co.r2cast.models.fnd.Menu;
import kr.co.r2cast.models.fnd.Privilege;
import kr.co.r2cast.models.fnd.Role;
import kr.co.r2cast.models.fnd.RolePrivilege;
import kr.co.r2cast.models.fnd.UserPrivilege;
import kr.co.r2cast.models.fnd.dao.MenuDao;
import kr.co.r2cast.models.fnd.dao.PrivilegeDao;
import kr.co.r2cast.models.fnd.dao.RoleDao;
import kr.co.r2cast.models.fnd.dao.RolePrivilegeDao;
import kr.co.r2cast.models.fnd.dao.UserPrivilegeDao;
import kr.co.r2cast.utils.Util;
import kr.co.r2cast.viewmodels.DropDownListItem;
import kr.co.r2cast.viewmodels.QuickLinkItem;

@Transactional
@Service("privService")
public class PrivilegeServiceImpl implements PrivilegeService {
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
	private MessageManager msgMgr;
    
    @Autowired
    private MenuDao menuDao;
    
    @Autowired
    private PrivilegeDao privDao;
    
    @Autowired
    private RoleDao roleDao;
    
    @Autowired
    private RolePrivilegeDao rolePrivDao;
    
    @Autowired
    private UserPrivilegeDao userPrivDao;

	@Override
	public void flush() {
		sessionFactory.getCurrentSession().flush();
	}

	@Override
	public Menu getMenu(int id) {
		return menuDao.get(id);
	}

	@Override
	public List<Menu> getMenuList() {
		return menuDao.getList();
	}

	@Override
	public void saveOrUpdate(Menu menu) {
		menuDao.saveOrUpdate(menu);
	}

	@Override
	public void deleteMenu(Menu menu) {
		menuDao.delete(menu);
	}

	@Override
	public void deleteMenus(List<Menu> menus) {
		menuDao.delete(menus);
	}

	@Override
	public int getMenuCount() {
		return menuDao.getCount();
	}

	@Override
	public DataSourceResult getMenuList(DataSourceRequest request) {
		return menuDao.getList(request);
	}

	@Override
	public Menu getMenu(String ukid) {
		return menuDao.get(ukid);
	}

	@Override
	public Menu getMenuByUrl(String url) {
		return menuDao.getByUrl(url);
	}

	@Override
	public List<Menu> getExecutableMenuList() {
		return menuDao.getExececutableList();
	}

	@Override
	public List<Menu> getMenuListById(Integer id) {
		return menuDao.getListById(id);
	}

	@Override
	public List<String> getAllMenuChildrenById(Integer id) {
		return menuDao.getAllChildrenById(id);
	}

	@Override
	public void saveAndReorderMenu(Menu source, Menu dest, HttpSession httpSession) {
		menuDao.saveAndReorder(source, dest, httpSession);
	}

	@Override
	public void syncWithPrivAndRole(HttpSession httpSession) {
		menuDao.syncWithPrivAndRole(httpSession);
	}
	
	@Override
	public Privilege getPrivilege(int id) {
		return privDao.get(id);
	}

	@Override
	public List<Privilege> getPrivilegeList() {
		return privDao.getList();
	}

	@Override
	public void saveOrUpdate(Privilege privilege) {
		privDao.saveOrUpdate(privilege);
	}

	@Override
	public void deletePrivilege(Privilege privilege) {
		privDao.delete(privilege);
	}

	@Override
	public void deletePrivileges(List<Privilege> privileges) {
		privDao.delete(privileges);
	}

	@Override
	public int getPrivilegeCount() {
		return privDao.getCount();
	}

	@Override
	public DataSourceResult getPrivilegeList(DataSourceRequest request) {
		return privDao.getList(request);
	}

	@Override
	public Privilege getPrivilege(String ukid) {
		return privDao.get(ukid);
	}

	@Override
	public Role getRole(int id) {
		return roleDao.get(id);
	}

	@Override
	public List<Role> getRoleList() {
		return roleDao.getList();
	}

	@Override
	public void saveOrUpdate(Role role) {
		roleDao.saveOrUpdate(role);
	}

	@Override
	public void deleteRole(Role role) {
		roleDao.delete(role);
	}

	@Override
	public void deleteRoles(List<Role> roles) {
		roleDao.delete(roles);
	}

	@Override
	public int getRoleCount() {
		return roleDao.getCount();
	}

	@Override
	public DataSourceResult getRoleList(DataSourceRequest request) {
		return roleDao.getList(request);
	}

	@Override
	public Role getRole(String ukid) {
		return roleDao.get(ukid);
	}

	@Override
	public RolePrivilege getRolePrivilege(int id) {
		return rolePrivDao.get(id);
	}

	@Override
	public List<RolePrivilege> getRolePrivilegeList() {
		return rolePrivDao.getList();
	}

	@Override
	public void saveOrUpdate(RolePrivilege rolePrivilege) {
		rolePrivDao.saveOrUpdate(rolePrivilege);
	}

	@Override
	public void deleteRolePrivilege(RolePrivilege rolePrivilege) {
		rolePrivDao.delete(rolePrivilege);
	}

	@Override
	public void deleteRolePrivileges(List<RolePrivilege> rolePrivileges) {
		rolePrivDao.delete(rolePrivileges);
	}

	@Override
	public int getRolePrivilegeCount() {
		return rolePrivDao.getCount();
	}

	@Override
	public DataSourceResult getRolePrivilegeList(DataSourceRequest request) {
		return rolePrivDao.getList(request);
	}

	@Override
	public boolean isRegisteredRolePrivilege(int roleId, int privilegeId) {
		return rolePrivDao.isRegistered(roleId, privilegeId);
	}

	@Override
	public List<RolePrivilege> getRolePrivilegeListByRoleId(int roleId) {
		return rolePrivDao.getListByRoleId(roleId);
	}

	@Override
	public UserPrivilege getUserPrivilege(int id) {
		return userPrivDao.get(id);
	}

	@Override
	public List<UserPrivilege> getUserPrivilegeList() {
		return userPrivDao.getList();
	}

	@Override
	public void saveOrUpdate(UserPrivilege userPrivilege) {
		userPrivDao.saveOrUpdate(userPrivilege);
	}

	@Override
	public void deleteUserPrivilege(UserPrivilege userPrivilege) {
		userPrivDao.delete(userPrivilege);
	}

	@Override
	public void deleteUserPrivileges(List<UserPrivilege> userPrivileges) {
		userPrivDao.delete(userPrivileges);
	}

	@Override
	public int getUserPrivilegeCount() {
		return userPrivDao.getCount();
	}

	@Override
	public DataSourceResult getUserPrivilegeList(DataSourceRequest request) {
		return userPrivDao.getList(request);
	}

	@Override
	public boolean isRegisteredUserPrivilege(int userId, int privilegeId) {
		return userPrivDao.isRegistered(userId, privilegeId);
	}

	@Override
	public List<UserPrivilege> getUserPrivilegeListByUserId(int userId) {
		return userPrivDao.getListByUserId(userId);
	}

	@Override
	public List<String> getAllUserPrivileges(int userId) {
		ArrayList<String> userPrivKeys = new ArrayList<String>();
		
		List<UserPrivilege> userPrivList = getUserPrivilegeListByUserId(userId);
		List<Menu> menuList = getMenuList();
		
		for (UserPrivilege userPriv : userPrivList) {
			Privilege priv = userPriv.getPrivilege();
			
			if (!userPrivKeys.contains(priv.getUkid())) {
				userPrivKeys.add(priv.getUkid());
			}
		}
		
		//
		// [사용자 홈] 페이지 권한은 수동으로 관리(2019/02/28)
		//
    	//if (!userPrivKeys.contains("menu.UserHome")) {
    	//	userPrivKeys.add("menu.UserHome");
    	//}
    	//
    	
    	boolean anyMenuAccessAllowed = userPrivKeys.contains("internal.AccessAnyMenu");
    	
    	if (anyMenuAccessAllowed) {
    		/*
        	if (!userPrivKeys.contains("internal.PasswordUpdate")) {
        		userPrivKeys.add("internal.PasswordUpdate");
        	}
        	*/
    	} else {
        	for (Menu menu : menuList) {
        		if (userPrivKeys.contains("menu." + menu.getUkid())) {
        			Menu tmp = menu.getParent();
        			while (tmp != null) {
        				if (!userPrivKeys.contains("menu." + tmp.getUkid())) {
        					userPrivKeys.add("menu." + tmp.getUkid());
        				}
        				
        				tmp = tmp.getParent();
        			}
        		}
        	}
    	}
    	
    	return userPrivKeys;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void touchRecentAccessMenus(HttpSession session, Locale locale, String uri) {
		if (session != null && Util.isValid(uri)) {
			Menu currMenu = getMenuByUrl(uri);
			if (currMenu != null) {
				ArrayList<String> recentMenus = (ArrayList<String>) session.getAttribute("recentMenus");
				if (recentMenus == null) {
					recentMenus = new ArrayList<String>();
				}
				
				recentMenus.remove(currMenu.getId() + "");
				recentMenus.add(0, currMenu.getId() + "");

				Iterator<String> itr = recentMenus.iterator();
				while (itr.hasNext()) {
					String id = itr.next();
					if (recentMenus.indexOf(id) > 4) {
						itr.remove();
					}
				}
				
				List<QuickLinkItem> list = new ArrayList<QuickLinkItem>();
		    	
				for(String id : recentMenus) {
					QuickLinkItem item = getQuickLinkMenuItem(getMenu(Integer.parseInt(id)), locale);
					if (item != null) {
						list.add(item);
					}
				}
				
				session.setAttribute("recentMenus", recentMenus);
				session.setAttribute("recentMenuItems", list);
			}
		}
	}

	@Override
	public QuickLinkItem getQuickLinkMenuItem(String ukid, Locale locale) {
		return getQuickLinkMenuItem(getMenu(ukid), locale);
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
	
	@Override
	public QuickLinkItem getQuickLinkMenuItem(Menu menu, Locale locale) {
		if (menu == null) {
			return null;
		}

		String pageTitle = msgMgr.message(menu.getUkid().toLowerCase() + ".title", locale);
		if (pageTitle.endsWith(".title")) {
			pageTitle = msgMgr.message("mainmenu." + menu.getUkid(), locale);
		}
		
		// 현재까지 사용자홈에서만 호출하며,
		// 사용자홈에서의 아이콘은 fa-thin 으로 강제변환시킴
		/*

		
		String iconName = "fal fa-question-circle";
		if (Util.isValid(menu.getIconType())) {
	    	if (menu.getIconType().startsWith("fas fa-") || menu.getIconType().startsWith("far fa-") || 
	    			menu.getIconType().startsWith("fal fa-")) {
	    		iconName = menu.getIconType();
	    	} else {
	    		iconName = "fal fa-" + menu.getIconType();
	    	}
		}
		
		*/
		String iconName = "fa-thin fa-question-circle"; 
		if (Util.isValid(menu.getIconType())) {
	    	if (menu.getIconType().startsWith("fas fa-") || menu.getIconType().startsWith("far fa-") || 
	    			menu.getIconType().startsWith("fal fa-")) {
	    		iconName = "fa-thin " + menu.getIconType().substring(4);
	    	} else {
	    		iconName = "fa-thin fa-" + menu.getIconType();
	    	}
		}

		
		return new QuickLinkItem(menu.getIconType(), iconName, "menu." + menu.getUkid(), 
				menu.getUrl(), pageTitle, getHierGroupName(menu, locale));
	}

	@Override
	public List<DropDownListItem> getDropDownMenuGroupDispList(
			Locale locale) {
		ArrayList<DropDownListItem> ret = new ArrayList<DropDownListItem>();
		
		String text = "";
		String value = "";

		for(int i = 1; i <= 100; i++) {
			value = String.format("%03d", i);
			text = msgMgr.message("menuGroup." + value, locale);
			
			if (Util.isValid(text) && !text.equals("menuGroup." + value)) {
				ret.add(new DropDownListItem(text, value));
			}
		}
		
		Collections.sort(ret, CustomComparator.DropDownListItemTextComparator);

		return ret;
	}
}
