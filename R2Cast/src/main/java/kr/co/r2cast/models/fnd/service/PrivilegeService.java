package kr.co.r2cast.models.fnd.service;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.fnd.Menu;
import kr.co.r2cast.models.fnd.Privilege;
import kr.co.r2cast.models.fnd.Role;
import kr.co.r2cast.models.fnd.RolePrivilege;
import kr.co.r2cast.models.fnd.UserPrivilege;
import kr.co.r2cast.viewmodels.DropDownListItem;
import kr.co.r2cast.viewmodels.QuickLinkItem;

@Transactional
public interface PrivilegeService {
	// Common
	public void flush();

	//
	// for Menu Dao
	//
	// Common
	public Menu getMenu(int id);
	public List<Menu> getMenuList();
	public void saveOrUpdate(Menu menu);
	public void deleteMenu(Menu menu);
	public void deleteMenus(List<Menu> menus);
	public int getMenuCount();
	
	// for Kendo Grid Remote Read
	public DataSourceResult getMenuList(DataSourceRequest request);
	
	// for Menu specific
	public Menu getMenu(String ukid);
	public List<Menu> getMenuListById(Integer id);
	public List<String> getAllMenuChildrenById(Integer id);
	public void saveAndReorderMenu(Menu source, Menu dest, HttpSession httpSession);
	public void syncWithPrivAndRole(HttpSession httpSession);
	public Menu getMenuByUrl(String url);
	public List<Menu> getExecutableMenuList();
	public void touchRecentAccessMenus(HttpSession session, Locale locale, String uri);
	public QuickLinkItem getQuickLinkMenuItem(String ukid, Locale locale);
	public QuickLinkItem getQuickLinkMenuItem(Menu menu, Locale locale);
	public List<DropDownListItem> getDropDownMenuGroupDispList(Locale locale);

	//
	// for Privilege Dao
	//
	// Common
	public Privilege getPrivilege(int id);
	public List<Privilege> getPrivilegeList();
	public void saveOrUpdate(Privilege privilege);
	public void deletePrivilege(Privilege privilege);
	public void deletePrivileges(List<Privilege> privileges);
	public int getPrivilegeCount();

	// for Kendo Grid Remote Read
	public DataSourceResult getPrivilegeList(DataSourceRequest request);

	// for Privilege specific
	public Privilege getPrivilege(String ukid);

	//
	// for Role Dao
	//
	// Common
	public Role getRole(int id);
	public List<Role> getRoleList();
	public void saveOrUpdate(Role role);
	public void deleteRole(Role role);
	public void deleteRoles(List<Role> roles);
	public int getRoleCount();

	// for Kendo Grid Remote Read
	public DataSourceResult getRoleList(DataSourceRequest request);

	// for Role specific
	public Role getRole(String ukid);
	public List<RolePrivilege> getRolePrivilegeListByRoleId(int roleId);

	//
	// for RolePrivilege Dao
	//
	// Common
	public RolePrivilege getRolePrivilege(int id);
	public List<RolePrivilege> getRolePrivilegeList();
	public void saveOrUpdate(RolePrivilege rolePrivilege);
	public void deleteRolePrivilege(RolePrivilege rolePrivilege);
	public void deleteRolePrivileges(List<RolePrivilege> rolePrivileges);
	public int getRolePrivilegeCount();
	
	// for Kendo Grid Remote Read
	public DataSourceResult getRolePrivilegeList(DataSourceRequest request);

	// for RolePrivilege specific
	public boolean isRegisteredRolePrivilege(int roleId, int privilegeId);

	//
	// for SiteUser Dao
	//
	// Common
	public UserPrivilege getUserPrivilege(int id);
	public List<UserPrivilege> getUserPrivilegeList();
	public void saveOrUpdate(UserPrivilege userPrivilege);
	public void deleteUserPrivilege(UserPrivilege userPrivilege);
	public void deleteUserPrivileges(List<UserPrivilege> userPrivileges);
	public int getUserPrivilegeCount();
	
	// for Kendo Grid Remote Read
	public DataSourceResult getUserPrivilegeList(DataSourceRequest request);

	// for UserPrivilege specific
	public boolean isRegisteredUserPrivilege(int userId, int privilegeId);
	public List<UserPrivilege> getUserPrivilegeListByUserId(int userId);
	public List<String> getAllUserPrivileges(int userId);
}
