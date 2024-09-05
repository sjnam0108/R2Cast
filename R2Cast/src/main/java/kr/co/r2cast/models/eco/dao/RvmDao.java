package kr.co.r2cast.models.eco.dao;

import java.util.Date;
import java.util.List;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.eco.Rvm;
import kr.co.r2cast.models.fnd.Site;

public interface RvmDao {
	// Common
	public Rvm get(int id);
	public void saveOrUpdate(Rvm rvm);
	public void delete(Rvm rvm);
	public void delete(List<Rvm> rvms);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);
	public DataSourceResult getList(DataSourceRequest request, boolean isEffectiveMode);
	public DataSourceResult getList(DataSourceRequest request, List<Integer> rvmIds);

	// for Rvm specific
	public Rvm get(String deviceID);
	public List<Rvm> getEffectiveList();
	public List<Rvm> getEffectiveList(Date time);
	public List<Rvm> getListBySiteId(int siteId, boolean isEffectiveMode);
	public List<Rvm> getListBySiteIdRvmName(int siteId, boolean isEffectiveMode, String rvmName);
	public Rvm getByDeviceIDSiteShortName(String deviceID, String siteShortName);
	public Rvm getByDeviceIDRvmId(String deviceID, int rvmId);
	public Rvm getByDeviceID(String deviceID);
	public Rvm get(Site site, String rvmName);
	public Rvm get(String rvmName, List<Integer> siteIds);
	public List<Object[]> getValidModelCountListBySiteId(int siteId);
	public List<Rvm> getUpdatedListAfter(Site site, Date date);
}
