package kr.co.r2cast.models.fnd.dao;

import java.util.List;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.fnd.SiteUser;

public interface SiteUserDao {
	// Common
	public SiteUser get(int id);
	public List<SiteUser> getList();
	public void saveOrUpdate(SiteUser siteUser);
	public void delete(SiteUser siteUser);
	public void delete(List<SiteUser> siteUsers);
	public int getCount();

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for SiteUser specific
	public boolean isRegistered(int siteId, int userId);
	public void saveOrUpdate(List<SiteUser> siteUsers);
}
