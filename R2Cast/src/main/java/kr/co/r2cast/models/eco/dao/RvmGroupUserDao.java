package kr.co.r2cast.models.eco.dao;

import java.util.List;

import kr.co.r2cast.models.eco.RvmGroupUser;
import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;


public interface RvmGroupUserDao {
	// Common
	public RvmGroupUser get(int id);
	public void saveOrUpdate(RvmGroupUser rvmGroupUser);
	public void delete(RvmGroupUser rvmGroupUser);
	public void delete(List<RvmGroupUser> rvmGroupUsers);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for RvmGroupUser specific
	public boolean isRegistered(int siteId, int rvmGroupId, int userId);
	public void saveOrUpdate(List<RvmGroupUser> rvmGroupUsers);
	public List<RvmGroupUser> getListBySiteIdUserId(int siteId, int userId);
}
