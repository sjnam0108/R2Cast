package kr.co.r2cast.models.eco.dao;

import java.util.Date;
import java.util.List;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.eco.MonTask;

public interface MonTaskDao {
	// Common
	public MonTask get(int id);
	public List<MonTask> getList();
	public void saveOrUpdate(MonTask monTask);
	public void delete(MonTask monTask);
	public void delete(List<MonTask> monTasks);
	public int getCount();

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);
	public DataSourceResult getList(DataSourceRequest request,
			List<Integer> rvmIds);
	public DataSourceResult getList(DataSourceRequest request,
			List<Integer> rvmIds, int lastMins);
	
	// for MonTask Specific
	public List<MonTask> getListByRvmId(int rvmId);
	public List<MonTask> getListByRtnSchdTaskRvmId(int rtnSchdTaskRvmId);
	public List<Integer> getOldIdsBefore(Date date, int maxCnt);
}
