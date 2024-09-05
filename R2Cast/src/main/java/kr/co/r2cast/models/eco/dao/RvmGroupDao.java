package kr.co.r2cast.models.eco.dao;

import java.util.List;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.eco.RvmGroup;
import kr.co.r2cast.models.fnd.Site;

public interface RvmGroupDao {
	// Common
	public RvmGroup get(int id);
	public List<RvmGroup> getList();
	public void saveOrUpdate(RvmGroup rvmGroup);
	public void delete(RvmGroup rvmGroup);
	public void delete(List<RvmGroup> rvmGroups);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for RvmGroup specific
	public RvmGroup get(Site site, String rvmGroupName);
	public List<RvmGroup> getListBySiteId(int siteId);
}
