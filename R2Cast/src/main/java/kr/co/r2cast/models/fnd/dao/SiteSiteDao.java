package kr.co.r2cast.models.fnd.dao;

import java.util.List;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.fnd.SiteSite;

public interface SiteSiteDao {
	// Common
	public SiteSite get(int id);
	public List<SiteSite> getList();
	public void saveOrUpdate(SiteSite siteSite);
	public void delete(SiteSite siteSite);
	public void delete(List<SiteSite> siteSites);
	public int getCount();

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for SiteSite specific
	public boolean isRegistered(int parentSiteId, int childSiteId);
	public void saveOrUpdate(List<SiteSite> siteSites);
	public List<SiteSite> getListByParentSiteId(int siteId);
}
