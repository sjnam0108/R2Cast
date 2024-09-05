package kr.co.r2cast.models.fnd.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import kr.co.r2cast.models.LoginUser;
import kr.co.r2cast.models.MessageManager;
import kr.co.r2cast.models.eco.EcoComparator;
import kr.co.r2cast.models.eco.RvmGroup;
import kr.co.r2cast.models.eco.RvmGroupUser;
import kr.co.r2cast.models.eco.service.RvmService;
import kr.co.r2cast.models.fnd.LoginLog;
import kr.co.r2cast.models.fnd.Privilege;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.SiteSite;
import kr.co.r2cast.models.fnd.SiteUser;
import kr.co.r2cast.models.fnd.User;
import kr.co.r2cast.models.fnd.UserPrivilege;
import kr.co.r2cast.models.fnd.dao.LoginLogDao;
import kr.co.r2cast.models.fnd.dao.UserDao;
import kr.co.r2cast.utils.SolUtil;

@Transactional
@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private SessionFactory sessionFactory;
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private LoginLogDao loginLogDao;
	
    @Autowired 
    private PrivilegeService privService;
    
	// [SignCast] ext ----------------------------------------------------------- start
	//
	//
    
    @Autowired 
    private RvmService rvmService;
	
	//
	//
	// [SignCast] ext ------------------------------------------------------------- end

    @Autowired
    private SiteService siteService;
    
	@Autowired
	private MessageManager msgMgr;
    
	@Override
	public void saveOrUpdate(User user) {
		userDao.saveOrUpdate(user);
	}

	@Override
	public List<User> getUserList() {
		return userDao.getList();
	}

	@Override
	public User getUser(String username) {
		return userDao.get(username);
	}

	@Override
	public User getUser(int id) {
		return userDao.get(id);
	}

	@Override
	public List<User> getEffectiveUserList() {
		return userDao.getEffectiveList();
	}

	@Override
	public List<User> getEffectiveUserList(Date time) {
		return userDao.getEffectiveList(time);
	}

	@Override
	public DataSourceResult getUserList(DataSourceRequest request) {
		return userDao.getList(request);
	}

	@Override
	public void deleteUser(User user) {
		userDao.delete(user);
	}

	@Override
	public void deleteUsers(List<User> users) {
		userDao.delete(users);
	}

	@Override
	public int getUserCount() {
		return userDao.getCount();
	}

	@Override
	public void flush() {
		sessionFactory.getCurrentSession().flush();
	}

	@Override
	public boolean isEffectiveUser(User user) {
		if (user == null) {
			return false;
		}
		
		Date now = new Date();
		if (now.before(user.getEffectiveStartDate())) {
			return false;
		}
		
		if (user.getEffectiveEndDate() != null && now.after(user.getEffectiveEndDate())) {
			return false;
		}

		return true;
	}

	@Override
	public List<Site> getUserSites(int userId) {
		User user = userDao.get(userId);
		
		ArrayList<Site> list = new ArrayList<Site>();
		for (SiteUser siteUser : user.getSiteUsers()) {
			Site site = siteUser.getSite();
			
			Site simpleSite = new Site();
			simpleSite.setShortName(site.getShortName());
			simpleSite.setSiteName(site.getSiteName());
			simpleSite.setId(site.getId());
			
			list.add(simpleSite);
		}
		
		Collections.sort(list, Site.ShortNameComparator);

		return list;
	}

	@Override
	public LoginLog getLoginLog(int id) {
		return loginLogDao.get(id);
	}

	@Override
	public List<LoginLog> getLoginLogList() {
		return loginLogDao.getList();
	}

	@Override
	public void saveOrUpdate(LoginLog loginLog) {
		loginLogDao.saveOrUpdate(loginLog);
	}

	@Override
	public void deleteLoginLog(LoginLog loginLog) {
		loginLogDao.delete(loginLog);
	}

	@Override
	public void deleteLoginLogs(List<LoginLog> loginLogs) {
		loginLogDao.delete(loginLogs);
	}

	@Override
	public int getLoginLogCount() {
		return loginLogDao.getCount();
	}

	@Override
	public DataSourceResult getLoginLogList(DataSourceRequest request) {
		return loginLogDao.getList(request);
	}

	@Override
	public void logout(HttpSession session) {
		logout(session, false);
	}

	@Override
	public void logout(HttpSession session, boolean forcedMode) {
    	if (session != null) {
        	LoginUser loginUser = (LoginUser) session.getAttribute("loginUser");
    		
        	if (loginUser != null) {
        		LoginLog loginLog = loginLogDao.get(loginUser.getLoginId());
        		
        		if (loginLog != null) {
        			loginLog.setLogout(true);
        			loginLog.setLogoutDate(new Date());
        			loginLog.setForcedLogout(forcedMode);
        			
        			loginLog.touchWho(session);
        			
        			saveOrUpdate(loginLog);
        		}
        	}

        	session.removeAttribute("loginUser");
        	session.removeAttribute("recentMenus");
    	}
	}

	@Override
	public LoginLog getLastLoginLogByUserId(int userId) {
		return loginLogDao.getLastByUserId(userId);
	}

	@Override
	public void setUserViews(LoginUser loginUser, String currentSiteIdStr, String viewId, 
			HttpSession session, Locale locale) {
		
    	String tmpViewId = "";

    	ArrayList<kr.co.r2cast.viewmodels.MonitoringViewItem> viewList = new ArrayList<kr.co.r2cast.viewmodels.MonitoringViewItem>();
    	
    	int currentSiteId = -1;
    	
    	try {
    		currentSiteId = Integer.parseInt(currentSiteIdStr);
    	} catch (Exception e) {}
    	
    	Site currentSite = siteService.getSite(currentSiteId);
    	
    	if (currentSite != null && loginUser != null) {
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

	    	boolean anyRvmGroupAccessAllowed = userPrivKeys.contains("internal.AccessAnyRvmGroup");
	    	boolean siteJobMngAllowed = userPrivKeys.contains("internal.ManageSiteJob");
	    	boolean anyChildSiteAccessAllowed = userPrivKeys.contains("internal.AccessAnyChildSite");
	    	//-
	    	
	    	ArrayList<RvmGroup> rvmGroupOfCurrentSite = new ArrayList<RvmGroup>();
	    	if (siteJobMngAllowed || anyRvmGroupAccessAllowed) {
	    		List<RvmGroup> rvmGroupList = rvmService.getRvmGroupListBySiteId(currentSiteId);
	    		
    			for (RvmGroup rvmGroup : rvmGroupList) {
    				if (rvmGroup.getViewType().equals("Y")) {
    					rvmGroupOfCurrentSite.add(rvmGroup);
    				}
    			}
	    	} else {
	    		List<RvmGroupUser> rvmGroupUserList = rvmService.getRvmGroupUserListBySiteIdUserId(
	    				currentSiteId, loginUser.getId());
	    		
    			for (RvmGroupUser rvmGroupUser : rvmGroupUserList) {
    				if (rvmGroupUser.getRvmGroup().getViewType().equals("Y")) {
        				rvmGroupOfCurrentSite.add(rvmService.getRvmGroup(rvmGroupUser.getRvmGroup().getId()));
    				}
    			}
	    	}
	    	
	    	ArrayList<Site> childrenSiteList = new ArrayList<Site>();
	    	List<SiteSite> siteSiteList = siteService.getSiteSiteListByParentSiteId(currentSiteId);
	    	for (SiteSite siteSite : siteSiteList) {
	    		childrenSiteList.add(siteSite.getChildSite());
	    	}
	    	
	    	// 대상 자료 정렬
	    	if (SolUtil.propEqVal(session, "rvmGroup.order", "G")) {
		    	Collections.sort(rvmGroupOfCurrentSite, EcoComparator.RvmGroupRvmGroupNameComparator);
	    	} else {
		    	Collections.sort(rvmGroupOfCurrentSite, EcoComparator.RvmGroupTagColorRvmGroupNameComparator);
	    	}
	    	Collections.sort(childrenSiteList, CustomComparator.SiteSiteNameComparator);

	    	// All Sites: {SiteId}, 0
        	// Site: {SiteId}, -1
        	// RvmGroup: {SiteId}, {RvmGroupId}
	    	int tmpIndent = 1;
	    	
	    	if (anyChildSiteAccessAllowed && siteJobMngAllowed && childrenSiteList.size() > 0) {
	    		viewList.add(new kr.co.r2cast.viewmodels.MonitoringViewItem(
	    				kr.co.r2cast.viewmodels.MonitoringViewItem.ViewType.SiteAll, tmpIndent, 
	    				msgMgr.message("viewswitcher.allSites", locale), currentSiteId, 0));
	    		tmpIndent += 10;
	    	}
	    	
	    	if (siteJobMngAllowed) {
	    		viewList.add(new kr.co.r2cast.viewmodels.MonitoringViewItem(
	    				kr.co.r2cast.viewmodels.MonitoringViewItem.ViewType.Site, tmpIndent, 
	    				msgMgr.message("viewswitcher.currentSite", locale), currentSiteId, -1));
	    		tmpIndent += 10;
	    	}
	    	
    		for (RvmGroup rvmGroup : rvmGroupOfCurrentSite) {
	    		viewList.add(new kr.co.r2cast.viewmodels.MonitoringViewItem(
	    				kr.co.r2cast.viewmodels.MonitoringViewItem.ViewType.RvmGroup, tmpIndent, 
	    				rvmGroup.getRvmGroupName(), rvmGroup.getCategory(), currentSiteId, rvmGroup.getId()));
    		}

    		if (anyChildSiteAccessAllowed && siteJobMngAllowed) {
        		for (Site childSite : childrenSiteList) {
    	    		viewList.add(new kr.co.r2cast.viewmodels.MonitoringViewItem(
    	    				kr.co.r2cast.viewmodels.MonitoringViewItem.ViewType.ChildSite, tmpIndent, 
    	    				childSite.getSiteName(), childSite.getId() , -1));
    	    		
    	    		List<RvmGroup> childRvmGroupList = rvmService.getRvmGroupListBySiteId(
    	    				childSite.getId());
    	    		
    	    		if (SolUtil.propEqVal(session, "rvmGroup.order", "G")) {
        	    		Collections.sort(childRvmGroupList, EcoComparator.RvmGroupRvmGroupNameComparator);
    	    		} else {
        	    		Collections.sort(childRvmGroupList, EcoComparator.RvmGroupTagColorRvmGroupNameComparator);
    	    		}
    	    		
    	    		for (RvmGroup rvmGroup : childRvmGroupList) {
    	    			if (rvmGroup.getViewType().equals("Y")) {
            	    		viewList.add(new kr.co.r2cast.viewmodels.MonitoringViewItem(
            	    				kr.co.r2cast.viewmodels.MonitoringViewItem.ViewType.ChildRvmGroup, tmpIndent + 10, 
            	    				rvmGroup.getRvmGroupName(), rvmGroup.getCategory(), childSite.getId() , rvmGroup.getId()));
    	    			}
    	    		}
        		}
    		}
    		
        	loginUser.setUserViews(viewList);
        	
        	tmpViewId = viewId;
        	if (tmpViewId == null) {
        		tmpViewId = loginUser.getFirstViewIdInUserViews();
        	}
    	}
    	
    	if (loginUser != null) {
        	loginUser.setUserViewId(tmpViewId);
        	loginUser.setViewSwitcherShown(viewList.size() > 1);
        	
        	if (loginUser.getUserViews() != null) {
            	for(kr.co.r2cast.viewmodels.MonitoringViewItem item : loginUser.getUserViews()) {
            		if (item.getValue().equals(loginUser.getUserViewId())) {
            			loginUser.setDispViewName(item.getText());
            			break;
            		}
            	}
        	}
    	}
    	
    	//
    	//
    	// [SignCast] ext ------------------------------------------------------------- end
    	
    	session.setAttribute("currentViewId", tmpViewId);
	}
}
