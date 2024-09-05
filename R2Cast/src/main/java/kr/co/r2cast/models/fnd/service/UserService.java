package kr.co.r2cast.models.fnd.service;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.LoginUser;
import kr.co.r2cast.models.fnd.LoginLog;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.User;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UserService {
	// Common
	public void flush();

	//
	// for User Dao
	//
	// Common
	public User getUser(int id);
	public List<User> getUserList();
	public void saveOrUpdate(User user);
	public void deleteUser(User user);
	public void deleteUsers(List<User> users);
	public int getUserCount();
	
	// for Kendo Grid Remote Read
	public DataSourceResult getUserList(DataSourceRequest request);

	// for User specific
	public User getUser(String username);
	public List<User> getEffectiveUserList();
	public List<User> getEffectiveUserList(Date time);
	public boolean isEffectiveUser(User user);
	public List<Site> getUserSites(int userId);

	//
	// for LoginLog Dao
	//
	// Common
	public LoginLog getLoginLog(int id);
	public List<LoginLog> getLoginLogList();
	public void saveOrUpdate(LoginLog loginLog);
	public void deleteLoginLog(LoginLog loginLog);
	public void deleteLoginLogs(List<LoginLog> loginLogs);
	public int getLoginLogCount();
	
	// for Kendo Grid Remote Read
	public DataSourceResult getLoginLogList(DataSourceRequest request);

	// for LoginLog specific
	public void logout(HttpSession session);
	public void logout(HttpSession session, boolean forcedMode);
	public LoginLog getLastLoginLogByUserId(int userId);

	//
	// for General Cases
	//
	public void setUserViews(LoginUser loginUser, String currentSiteIdStr, 
			String viewId, HttpSession session, Locale locale);
}
