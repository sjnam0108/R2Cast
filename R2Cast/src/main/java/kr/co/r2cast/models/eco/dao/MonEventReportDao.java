package kr.co.r2cast.models.eco.dao;

import java.util.Date;
import java.util.List;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.eco.MonEventReport;

public interface MonEventReportDao {
	// Common
	public MonEventReport get(int id);
	public void saveOrUpdate(MonEventReport monEventReport);
	public void delete(MonEventReport monEventReport);
	public void delete(List<MonEventReport> monEventReports);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);
	
	// for MonEventReport Specific
	public List<MonEventReport> getListBySiteId(int siteId);
	public int getCount(Date fromDt, String event, String equipType,
			int equipId);
	public MonEventReport getLast(Date fromDt, String event, String details, 
			String equipType, int equipId);
	public List<MonEventReport> getListByRvmId(int rvmId);
}
