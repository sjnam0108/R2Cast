package kr.co.r2cast.models.eco.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.eco.Rvm;
import kr.co.r2cast.models.eco.RvmLastReport;
import kr.co.r2cast.models.eco.RvmStatusLine;

public interface MonitoringDao {
	// Common
    public ArrayList<Integer> getSiteIdAndRvmGroupId(HttpSession session);
	public List<Integer> getViewRvmIds(HttpSession session, boolean isEffectiveMode);
	public List<Integer> getViewRvmIds(HttpSession session, boolean isEffectiveMode,
			boolean isOnlyServiceMode);
	public List<Integer> getSiteViewRvmIds(HttpSession session, boolean isEffectiveMode, 
			boolean isOnlyServiceMode);
	public List<Integer> getSiteViewRvmIds(int siteId, boolean isEffectiveMode, 
			boolean isOnlyServiceMode);
	public List<Rvm> getMonValCalcedSiteRvmList(int siteId, boolean isEffectiveMode, 
			boolean isOnlyServiceMode);
	public List<Rvm> getFailureRvmListBySiteId(int siteId, boolean isEffectiveMode, int allowedMins);
	
	
	// for Kendo Grid Remote Read
	public DataSourceResult getMonitoringRvmList(DataSourceRequest request, 
			List<Integer> rvmIds);
	public DataSourceResult getServiceRecordList(DataSourceRequest request, 
			List<Integer> rvmIds);
	public List<RvmStatusLine> getServiceRecordListByPeriod(List<Integer> rvmIds, 
			Date startDate, Date endDate);
	public List<Object[]> getServiceRecordRunningMinCountListByPeriod(List<Integer> rvmIds, 
			Date startDate, Date endDate);
	public List<Object[]> getServiceRecordRunningMinCountListByStateDate(List<Integer> rvmIds, 
			Date stateDate);
	
	// for StbLastReport specific
	public void saveOrUpdate(RvmLastReport rvmLastReport);
	public Date getLastUpdateDateByRvmId(int rvmId);
	
	// for StbStatusLine specific
	public void saveOrUpdate(RvmStatusLine rvmStatusLine);
	public List<RvmStatusLine> getRvmStatusLineListByRvmId(int rvmId);
	public List<RvmStatusLine> getRvmStatusLineListByStateDate(Date stateDate);
	public List<RvmStatusLine> getRvmStatusLineListByStateDateSiteId(Date stateDate, int siteId);
	public RvmStatusLine getRvmStatusLineByStateDateRvmId(Date stateDate, int rvmId);
	public void recalcMonitoringValues(List<Integer> rvmIds);
	public void recalcBatchMonitoringValues(List<Rvm> rvms);
	public void delete(List<RvmStatusLine> rvmStatusLines);
	public List<Integer> getIdsBefore(Date stateDate, int maxCnt);
	public DataSourceResult getRvmStatusList(DataSourceRequest request,
			List<Integer> ids);
}