package kr.co.r2cast.models.eco.dao;

import java.util.List;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.eco.RtnSchdTaskRvm;

public interface RtnSchdTaskRvmDao {
	// Common
	public RtnSchdTaskRvm get(int id);
	public List<RtnSchdTaskRvm> getList();
	public void saveOrUpdate(RtnSchdTaskRvm rtnSchdTaskRvm);
	public void delete(RtnSchdTaskRvm rtnSchdTaskRvm);
	public void delete(List<RtnSchdTaskRvm> rtnSchdTaskRvms);
	public int getCount();

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for StbGroupStb specific
	public boolean isRegistered(int siteId, int rtnSchdTaskId, int rvmId);
	public void saveOrUpdate(List<RtnSchdTaskRvm> rtnSchdTaskRvms);
	public List<RtnSchdTaskRvm> getListBySiteIdRtnSchdTaskId(int siteId, 
			int rtnSchdTaskId);
	public int getCountBySiteIdRtnSchdTaskId(int siteId, 
			int rtnSchdTaskId);
	public List<RtnSchdTaskRvm> getListByRvmId(int rvmId);
}
