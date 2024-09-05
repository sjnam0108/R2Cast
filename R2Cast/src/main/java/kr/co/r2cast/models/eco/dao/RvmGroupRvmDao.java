package kr.co.r2cast.models.eco.dao;

import java.util.List;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.eco.RvmGroupRvm;

public interface RvmGroupRvmDao {
	// Common
	public RvmGroupRvm get(int id);
	public void saveOrUpdate(RvmGroupRvm rvmGroupRvm);
	public void delete(RvmGroupRvm rvmGroupRvm);
	public void delete(List<RvmGroupRvm> rvmGroupRvms);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for RvmGroupRvm specific
	public boolean isRegistered(int siteId, int rvmGroupId, int rvmId);
	public void saveOrUpdate(List<RvmGroupRvm> rvmGroupRvms);
	public List<RvmGroupRvm> getListBySiteIdRvmGroupId(int siteId, int rvmGroupId,
			boolean isEffectiveMode);
	public List<RvmGroupRvm> getListBySiteIdRvmId(int siteId, int rvmId);
}
