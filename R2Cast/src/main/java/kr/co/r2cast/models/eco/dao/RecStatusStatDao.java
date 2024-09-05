package kr.co.r2cast.models.eco.dao;

import java.util.Date;
import java.util.List;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.eco.RecStatusStat;
import kr.co.r2cast.models.fnd.Site;

public interface RecStatusStatDao {
	// Common
	public RecStatusStat get(int id);
	public List<RecStatusStat> getList();
	public void saveOrUpdate(RecStatusStat recStatusStat);
	public void delete(RecStatusStat recStatusStat);
	public void delete(List<RecStatusStat> recStatusStats);
	public int getCount();

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);
	public DataSourceResult getListByDate(DataSourceRequest request,
			Date stateDate);
	public DataSourceResult getListByTime(DataSourceRequest request,
			String stateTime);

	// for RecStatusStat specific
	public RecStatusStat get(Site site, Date stateDate);
	public List<Integer> getIdsBefore(Date stateDate, int maxCnt);
	public List<RecStatusStat> getListByTime(Site site, String stateTime);
}
