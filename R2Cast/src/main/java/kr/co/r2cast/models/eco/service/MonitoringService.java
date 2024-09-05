package kr.co.r2cast.models.eco.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.eco.MonEventReport;
import kr.co.r2cast.models.eco.MonTask;
import kr.co.r2cast.models.eco.RecDailySummary;
import kr.co.r2cast.models.eco.RecStatusStat;
import kr.co.r2cast.models.eco.RtnSchdTask;
import kr.co.r2cast.models.eco.RtnSchdTaskRvm;
import kr.co.r2cast.models.eco.Rvm;
import kr.co.r2cast.models.eco.RvmLastReport;
import kr.co.r2cast.models.eco.RvmStatusLine;
import kr.co.r2cast.models.fnd.Site;

@Transactional
public interface MonitoringService {
	// Common
	public void flush();

	
	//
	// for Monitoring Dao
	//
	// Common
	public void saveOrUpdate(RvmLastReport rvmLastReport);
	public void saveOrUpdate(RvmStatusLine rvmStatusLine);
	public boolean isRvmIdValidInView(HttpSession session, int rvmId, 
			boolean isEffectiveMode);
	public ArrayList<Integer> getSiteIdAndRvmGroupId(HttpSession session);
	public int getMonitoringViewRvmCount(HttpSession session, boolean isEffectiveMode);
	public List<Rvm> getMonValCalcedSiteRvmList(int siteId, boolean isEffectiveMode, 
			boolean isOnlyServiceMode);
	public List<Rvm> getFailureRvmListBySiteId(int siteId, boolean isEffectiveMode, int allowedMins);
	public boolean isSiteLevelViewMode(HttpSession session);
	
	// for RvmLastReport specific
	public Date getRvmLastReportLastUpdateDateByRvmId(int rvmId);
	
	// for RvmStatusLine specific
	public List<RvmStatusLine> getRvmStatusLineListByRvmId(int rvmId);
	public RvmStatusLine getRvmStatusLineByStateDateRvmId(Date stateDate, 
			int rvmId);
	public void recalcMonitoringValues(List<Integer> rvmIds);
	public void checkRvmRemoteControlTypeAndLastReportTime(Rvm rvm, String command);
	public void deleteRvmStatusLines(List<RvmStatusLine> rvmStatusLines);
	public List<Integer> getRvmStatusLineIdsBefore(Date stateDate, int maxCnt);
	
	// for Kendo Grid Remote Read
	public DataSourceResult getMonitoringRvmList(DataSourceRequest request, 
			HttpSession session, boolean isEffectiveMode, boolean recalcRequired);
	public DataSourceResult getMonitoringRvmList(DataSourceRequest request, 
			HttpSession session, boolean isEffectiveMode, boolean recalcRequired,
			boolean isOnlyServiceMode);
	public DataSourceResult getServiceRecordList(DataSourceRequest request, 
			HttpSession session, boolean isEffectiveMode);
	public List<RvmStatusLine> getServiceRecordListByPeriod(Date startDate, 
			Date endDate, HttpSession session);
	public List<Object[]> getServiceRecordRunningMinCountListByPeriod(Date startDate, 
			Date endDate, HttpSession session);
	public List<Object[]> getServiceRecordRunningMinCountListByStateDate(Date stateDate, 
			HttpSession session);
	public DataSourceResult getRvmStatusList(DataSourceRequest request, 
			HttpSession session);

	
	//
	// for Common
	//
	public void cleanUpOldTaskDataBefore(Date date);
	public void cleanUpOldTaskDataBefore(Date date, int maxCnt);
	public void cleanUpOldStatusDataBefore(Date date);
	public void cleanUpOldStatusDataBefore(Date date, int maxCnt);
	public List<Integer> getDestRvmIds(boolean isAllRvmMode, ArrayList<Object> rvmGroupIds,
			ArrayList<Object> rvmIds, int siteId, int rvmGroupId);
	public List<Integer> getDestRvmIds(boolean isAllRvmMode, ArrayList<Object> rvmGroupIds,
			ArrayList<Object> rvmIds, ArrayList<Object> tagIds, int siteId, int rvmGroupId);
	public List<Rvm> sortRvmListOrderByLastReportDate(List<Rvm> list);
	public List<Rvm> getRvmListByOpTagValue(int siteId, String value);
	public List<Rvm> getRvmListByOpTagValues(int siteId, ArrayList<Object> ids);
	
	
	//
	// for RtnSchdTask Dao
	//
	// Common
	public RtnSchdTask getRtnSchdTask(int id);
	public void saveOrUpdate(RtnSchdTask rtnSchdTask);
	public void deleteRtnSchdTask(RtnSchdTask rtnSchdTask);
	public void deleteRtnSchdTasks(List<RtnSchdTask> rtnSchdTasks);
	
	// for Kendo Grid Remote Read
	public DataSourceResult getRtnSchdTaskList(DataSourceRequest request);

	// for RtnSchdTask specific
	public RtnSchdTask getRtnSchdTask(Site site, String taskName);
	public List<RtnSchdTask> getRtnSchdTaskListBySiteId(int siteId);

	
	//
	// for RtnSchdTaskRvm Dao
	//
	// Common
	public RtnSchdTaskRvm getRtnSchdTaskRvm(int id);
	public void saveOrUpdate(RtnSchdTaskRvm rtnSchdTaskRvmdeleteRtnSchdTaskRvms);
	public void deleteRtnSchdTaskRvm(RtnSchdTaskRvm rtnSchdTaskRvm);
	public void deleteRtnSchdTaskRvms(List<RtnSchdTaskRvm> rtnSchdTaskRvms);
	
	// for Kendo Grid Remote Read
	public DataSourceResult getRvmGroupRvmList(DataSourceRequest request);

	// for RtnSchdTaskRvm specific
	public List<RtnSchdTaskRvm> getRtnSchdTaskRvmListBySiteIdRtnSchdTaskId(int siteId, 
			int rtnSchdTaskId);
	public int getRtnSchdTaskRvmCountBySiteIdRtnSchdTaskId(int siteId, 
			int rtnSchdTaskId);
	public List<RtnSchdTaskRvm> getRtnSchdTaskRvmListByRvmId(int rvmId);
	public List<RtnSchdTaskRvm> getNextRtnSchdTaskRvmListByRvmId(int rvmId, Date now);
	
	
	//
	// for MonTask Dao
	//
	// Common
	public MonTask getMonTask(int id);
	public void saveOrUpdate(MonTask monTask);
	public void deleteMonTask(MonTask monTask);
	public void deleteMonTasks(List<MonTask> monTasks);
	
	// for Kendo Grid Remote Read
	public DataSourceResult getMonTaskList(DataSourceRequest request,
			HttpSession session);
	public DataSourceResult getSpecialMonTaskList(DataSourceRequest request,
			HttpSession session);
	public DataSourceResult getMonTaskList(DataSourceRequest request,
			HttpSession session, int lastMins);
	
	// for MonTask specific
	public List<MonTask> getMonTaskListByRvmId(int rvmId);
	public List<MonTask> getNextMonTaskListByRvmId(int rvmId, Date now);
	public MonTask getMonTaskByRtnSchdTaskRvmId(int rtnSchdTaskRvmId);
	public List<Integer> getOldMonTaskIdsBefore(Date destDate, int maxCnt);
	
	
	//
	// for MonEventReportDao
	//
	// Common
	public MonEventReport getMonEventReport(int id);
	public void saveOrUpdate(MonEventReport monEventReport);
	public void deleteMonEventReport(MonEventReport monEventReport);
	public void deleteMonEventReports(List<MonEventReport> monEventReports);

	// for Kendo Grid Remote Read
	public DataSourceResult getMonEventReportList(DataSourceRequest request);
	
	// for MonEventReport Specific
	public List<MonEventReport> getMonEventReportListBySiteId(int siteId);
	public boolean isInSeriousWERStatus(String event, String equipType, int equipId);
	public int getMonEventReportCount(Date fromDt, String event, String equipType,
			int equipId);
	public MonEventReport getLastMonEventReport(Date fromDt, String event, String details, 
			String equipType, int equipId);
	public List<MonEventReport> getMonEventReportListByRvmId(int rvmId);
	
	
	//
	// for RecDailySummaryDao
	//
	// Common
	public RecDailySummary getRecDailySummary(int id);
	public void saveOrUpdate(RecDailySummary recDailySummary);
	public void deleteRecDailySummary(RecDailySummary recDailySummary);
	public void deleteRecDailySummaries(List<RecDailySummary> recDailySummaries);

	// for Kendo Grid Remote Read
	public DataSourceResult getRecDailySummaryList(DataSourceRequest request);

	// for RecDailySummary specific
	public RecDailySummary getRecDailySummary(Site site, Date workDate);
	public void saveOrUpdateRecDailySummary(Site site, Date workDate, int rvmCount, 
			int totalCount, long totalRunningMins, String calculated);

	
	//
	// for RecStatusStat Dao
	//
	// Common
//	public RecStatusStat getRecStatusStat(int id);
//	public List<RecStatusStat> getRecStatusStatList();
//	public void saveOrUpdate(RecStatusStat recStatusStat);
//	public void deleteRecStatusStat(RecStatusStat recStatusStat);
	public void deleteRecStatusStats(List<RecStatusStat> recStatusStats);
	public int getRecStatusStatCount();

	// for Kendo Grid Remote Read
	public DataSourceResult getRecStatusStatList(DataSourceRequest request);
	public DataSourceResult getRecStatusStatListByDate(DataSourceRequest request,
			Date stateDate);
	public DataSourceResult getRecStatusStatListByTime(DataSourceRequest request,
			String stateTime);

	// for RecStatusStat specific
//	public RecStatusStat getRecStatusStat(Site site, Date stateDate);
	public void saveOrUpdateRecStatusStat(Site site, Date stateDate, int status6, int status5, 
			int status4, int status3, int status2, int status0, long totalRuningMins);
	public List<Integer> getRecStatusStatIdsBefore(Date stateDate, int maxCnt);
//	public List<RecStatusStat> getRecStatusStatListByTime(Site site, String stateTime);





}
