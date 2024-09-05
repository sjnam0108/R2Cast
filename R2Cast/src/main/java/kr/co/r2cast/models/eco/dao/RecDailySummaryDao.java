package kr.co.r2cast.models.eco.dao;

import java.util.Date;
import java.util.List;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.eco.RecDailySummary;
import kr.co.r2cast.models.fnd.Site;

public interface RecDailySummaryDao {
	// Common
	public RecDailySummary get(int id);
	public void saveOrUpdate(RecDailySummary recDailySummary);
	public void delete(RecDailySummary recDailySummary);
	public void delete(List<RecDailySummary> recDailySummaries);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for RecDailySummary specific
	public RecDailySummary get(Site site, Date workDate);
}
