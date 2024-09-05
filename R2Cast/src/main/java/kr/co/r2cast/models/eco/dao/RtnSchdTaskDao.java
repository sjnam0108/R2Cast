package kr.co.r2cast.models.eco.dao;

import java.util.List;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.eco.RtnSchdTask;
import kr.co.r2cast.models.fnd.Site;

public interface RtnSchdTaskDao {
	// Common
	public RtnSchdTask get(int id);
	public void saveOrUpdate(RtnSchdTask rtnSchdTask);
	public void delete(RtnSchdTask rtnSchdTask);
	public void delete(List<RtnSchdTask> rtnSchdTasks);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for RtnSchdTask specific
	public RtnSchdTask get(Site site, String taskName);
	public List<RtnSchdTask> getListBySiteId(int siteId);
}
