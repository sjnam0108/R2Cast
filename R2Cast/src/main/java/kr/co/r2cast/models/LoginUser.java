package kr.co.r2cast.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.User;

public class LoginUser {
	private int id;
	private int loginId;

	private String username;
	private String familiarName;
	
	private String dispSiteName = "";
	private String dispViewName = "";
	
	private String userViewId;
	
	private String icon;
	
	private Date loginDate;

	private List<Site> userSites;
	private List<String> allowedUrlList = new ArrayList<String>();
	
	private boolean siteSwitcherShown;
	private boolean viewSwitcherShown;
	private boolean anyMenuAccessAllowed;
	private boolean manageSiteJobAllowed;
	
	public LoginUser(User user, int loginId) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.familiarName = user.getFamiliarName();
		this.loginId = loginId;
		this.loginDate = new Date();
	}
	
	public boolean hasSiteIdInUserSites(String value) {
		for(Site site : userSites) {
			if (String.valueOf(site.getId()).equals(value)) {
				return true;
			}
		}
		
		return false;
	}
	
	public String getFirstSiteIdInUserSites() {
		for(Site site : userSites) {
			return String.valueOf(site.getId());
		}
		
		return null;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getFamiliarName() {
		return familiarName;
	}
	
	public void setFamiliarName(String familiarName) {
		this.familiarName = familiarName;
	}
	
	public int getLoginId() {
		return loginId;
	}
	
	public void setLoginId(int loginId) {
		this.loginId = loginId;
	}
	
	public Date getLoginDate() {
		return loginDate;
	}
	
	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	public List<Site> getUserSites() {
		return userSites;
	}

	public void setUserSites(List<Site> userSites) {
		this.userSites = userSites;
	}

	public boolean isSiteSwitcherShown() {
		return siteSwitcherShown;
	}

	public void setSiteSwitcherShown(boolean siteSwitcherShown) {
		this.siteSwitcherShown = siteSwitcherShown;
	}

	public boolean isViewSwitcherShown() {
		return viewSwitcherShown;
	}

	public void setViewSwitcherShown(boolean viewSwitcherShown) {
		this.viewSwitcherShown = viewSwitcherShown;
	}

	public boolean isAnyMenuAccessAllowed() {
		return anyMenuAccessAllowed;
	}

	public void setAnyMenuAccessAllowed(boolean anyMenuAccessAllowed) {
		this.anyMenuAccessAllowed = anyMenuAccessAllowed;
	}

	public boolean isManageSiteJobAllowed() {
		return manageSiteJobAllowed;
	}

	public void setManageSiteJobAllowed(boolean manageSiteJobAllowed) {
		this.manageSiteJobAllowed = manageSiteJobAllowed;
	}

	public List<String> getAllowedUrlList() {
		return allowedUrlList;
	}

	public void setAllowedUrlList(List<String> allowedUrlList) {
		this.allowedUrlList = allowedUrlList;
	}

	public String getUserViewId() {
		return userViewId;
	}

	public void setUserViewId(String userViewId) {
		this.userViewId = userViewId;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getDispSiteName() {
		return dispSiteName;
	}

	public void setDispSiteName(String dispSiteName) {
		this.dispSiteName = dispSiteName;
	}

	public String getDispViewName() {
		return dispViewName;
	}

	public void setDispViewName(String dispViewName) {
		this.dispViewName = dispViewName;
	}
	
	// [WAB] --------------------------------------------------------------------------
	/*
	private List<kr.co.signcast.viewmodels.MonitoringViewItem> userViews;
	
	public boolean hasViewIdInUserViews(String value) {
		for(kr.co.signcast.viewmodels.MonitoringViewItem item : userViews) {
			if (item.getValue().equals(value)) {
				return true;
			}
		}
		
		return false;
	}
	
	public String getFirstViewIdInUserViews() {
		for(kr.co.signcast.viewmodels.MonitoringViewItem item : userViews) {
			if (item.getView() == kr.co.signcast.viewmodels.MonitoringViewItem.ViewType.SiteAll) {
				continue;
			}
			
			return item.getValue();
		}
		return "";
	}

	public List<kr.co.signcast.viewmodels.MonitoringViewItem> getUserViews() {
		return userViews;
	}

	public void setUserViews(List<kr.co.signcast.viewmodels.MonitoringViewItem> userViews) {
		this.userViews = userViews;
	}
	*/
	// [WAB] --------------------------------------------------------------------------
	// [SignCast] ext ----------------------------------------------------------- start
	//
	//
	
	private boolean viewRecentTaskAllowed;

	public boolean isViewRecentTaskAllowed() {
		return viewRecentTaskAllowed;
	}

	public void setViewRecentTaskAllowed(boolean viewRecentTaskAllowed) {
		this.viewRecentTaskAllowed = viewRecentTaskAllowed;
	}
	
	
	private List<kr.co.r2cast.viewmodels.MonitoringViewItem> userViews;
	
	public boolean hasViewIdInUserViews(String value) {
		for(kr.co.r2cast.viewmodels.MonitoringViewItem item : userViews) {
			if (item.getValue().equals(value)) {
				return true;
			}
		}
		
		return false;
	}
	
	public String getFirstViewIdInUserViews() {
		for(kr.co.r2cast.viewmodels.MonitoringViewItem item : userViews) {
			if (item.getView() == kr.co.r2cast.viewmodels.MonitoringViewItem.ViewType.SiteAll) {
				continue;
			}
			
			return item.getValue();
		}
		return "";
	}

	public List<kr.co.r2cast.viewmodels.MonitoringViewItem> getUserViews() {
		return userViews;
	}

	public void setUserViews(List<kr.co.r2cast.viewmodels.MonitoringViewItem> userViews) {
		this.userViews = userViews;
	}
	
	//
	//
	// [SignCast] ext ------------------------------------------------------------- end
}
