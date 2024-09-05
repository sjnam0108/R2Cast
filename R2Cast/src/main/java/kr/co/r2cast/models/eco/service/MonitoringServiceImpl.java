package kr.co.r2cast.models.eco.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.MessageManager;
import kr.co.r2cast.models.eco.EcoComparator;
import kr.co.r2cast.models.eco.MonEventReport;
import kr.co.r2cast.models.eco.MonTask;
import kr.co.r2cast.models.eco.RecDailySummary;
import kr.co.r2cast.models.eco.RecStatusStat;
import kr.co.r2cast.models.eco.RtnSchdTask;
import kr.co.r2cast.models.eco.RtnSchdTaskRvm;
import kr.co.r2cast.models.eco.Rvm;
import kr.co.r2cast.models.eco.RvmGroupRvm;
import kr.co.r2cast.models.eco.RvmLastReport;
import kr.co.r2cast.models.eco.RvmStatusLine;
import kr.co.r2cast.models.eco.dao.MonEventReportDao;
import kr.co.r2cast.models.eco.dao.MonTaskDao;
import kr.co.r2cast.models.eco.dao.MonitoringDao;
import kr.co.r2cast.models.eco.dao.RecDailySummaryDao;
import kr.co.r2cast.models.eco.dao.RecStatusStatDao;
import kr.co.r2cast.models.eco.dao.RtnSchdTaskDao;
import kr.co.r2cast.models.eco.dao.RtnSchdTaskRvmDao;
import kr.co.r2cast.models.eco.dao.RvmDao;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.service.SiteService;
import kr.co.r2cast.utils.SolUtil;
import kr.co.r2cast.utils.Util;
import kr.co.r2cast.viewmodels.eco.RvmItem;


@Transactional
@Service("monService")
public class MonitoringServiceImpl implements MonitoringService {
	private static final Logger logger = LoggerFactory.getLogger(MonitoringServiceImpl.class);

	@Autowired
    private SessionFactory sessionFactory;
    
    @Autowired
    private RvmDao rvmDao;
    
    @Autowired
    private MonitoringDao monDao;

    @Autowired
    private RtnSchdTaskDao rtnSchdTaskDao;
    
    @Autowired
    private RtnSchdTaskRvmDao rtnSchdTaskRvmDao;
    
    @Autowired
    private MonTaskDao monTaskDao;
    
    @Autowired
    private MonEventReportDao monEventReportDao;
    
    @Autowired
    private RecStatusStatDao recStatusStatDao;
    
    @Autowired
    private RecDailySummaryDao recDailySummaryDao;

    @Autowired 
    private RvmService rvmService;
    
    @Autowired
    private SiteService siteService;
    
    @Autowired
	private MessageManager msgMgr;

	@Override
	public void flush() {
		sessionFactory.getCurrentSession().flush();
	}

	
	//
	// for Monitoring Dao
	//
	// Common
	@Override
	public void saveOrUpdate(RvmLastReport rvmLastReport) {
		monDao.saveOrUpdate(rvmLastReport);
	}

	@Override
	public void saveOrUpdate(RvmStatusLine rvmStatusLine) {
		monDao.saveOrUpdate(rvmStatusLine);
	}

	@Override
	public boolean isRvmIdValidInView(HttpSession session, int rvmId, 
			boolean isEffectiveMode) {
		if (rvmId < 1) {
			return false;
		}
		
		List<Integer> viewRvmIds = monDao.getViewRvmIds(session, isEffectiveMode);
		
		return viewRvmIds.contains(rvmId);
	}

	@Override
	public ArrayList<Integer> getSiteIdAndRvmGroupId(HttpSession session) {
		return monDao.getSiteIdAndRvmGroupId(session);
	}

	@Override
	public int getMonitoringViewRvmCount(HttpSession session,
			boolean isEffectiveMode) {
		List<Integer> list = monDao.getViewRvmIds(session, isEffectiveMode);
		return list.size();
	}

	@Override
	public List<Rvm> getMonValCalcedSiteRvmList(int siteId,
			boolean isEffectiveMode, boolean isOnlyServiceMode) {
		return monDao.getMonValCalcedSiteRvmList(siteId, isEffectiveMode, isOnlyServiceMode);
	}

	@Override
	public List<Rvm> getFailureRvmListBySiteId(int siteId, boolean isEffectiveMode, int allowedMins) {
		
		return monDao.getFailureRvmListBySiteId(siteId, isEffectiveMode, allowedMins);
	}

	@Override
	public boolean isSiteLevelViewMode(HttpSession session) {
    	List<Integer> ids = getSiteIdAndRvmGroupId(session);

    	if (ids != null) {
    		int rvmGroupId = ids.get(1);
    		
    		return rvmGroupId == -1;
    	}
    	
    	return false;
	}

	
	// for RvmLastReport specific
	@Override
	public Date getRvmLastReportLastUpdateDateByRvmId(int rvmId) {
		return monDao.getLastUpdateDateByRvmId(rvmId);
	}

	
	// for RvmStatusLine specific
	@Override
	public List<RvmStatusLine> getRvmStatusLineListByRvmId(int rvmId) {
		return monDao.getRvmStatusLineListByRvmId(rvmId);
	}

	@Override
	public RvmStatusLine getRvmStatusLineByStateDateRvmId(Date stateDate,
			int rvmId) {
		return monDao.getRvmStatusLineByStateDateRvmId(stateDate, rvmId);
	}

	@Override
	public void recalcMonitoringValues(List<Integer> rvmIds) {
		monDao.recalcMonitoringValues(rvmIds);
	}
	
	@Override
	public void checkRvmRemoteControlTypeAndLastReportTime(Rvm rvm, String command) {
		if (rvm != null && Util.isValid(command) &&
				!command.equals("PowerOnWol.bbmc")) {
			RvmStatusLine rvmStatusLine = getRvmStatusLineByStateDateRvmId(
        			Util.removeTimeOfDate(new Date()), rvm.getId());
			if (rvmStatusLine != null && 
					SolUtil.isDateDuring1Minute(rvmStatusLine.getWhoLastUpdateDate())) {
			}
		}
	}

	@Override
	public void deleteRvmStatusLines(List<RvmStatusLine> rvmStatusLines) {
		monDao.delete(rvmStatusLines);
	}

	@Override
	public List<Integer> getRvmStatusLineIdsBefore(Date stateDate, int maxCnt) {
		return monDao.getIdsBefore(stateDate, maxCnt);
	}


	// for Kendo Grid Remote Read
	@Override
	public DataSourceResult getMonitoringRvmList(DataSourceRequest request, 
			HttpSession session, boolean isEffectiveMode, boolean recalcRequired) {
		return getMonitoringRvmList(request, session, isEffectiveMode, recalcRequired, false);
	}

	@Override
	public DataSourceResult getMonitoringRvmList(DataSourceRequest request, 
			HttpSession session, boolean isEffectiveMode, boolean recalcRequired,
			boolean isOnlyServiceMode) {
		List<Integer> viewRvmIds = monDao.getViewRvmIds(session, isEffectiveMode, isOnlyServiceMode);
		
		DataSourceResult result = monDao.getMonitoringRvmList(request, viewRvmIds);
		if (recalcRequired) {
			List<Rvm> rvms = new ArrayList<Rvm>();
			for(Object obj : result.getData()) {
				rvms.add((Rvm)obj);
			}
			
			monDao.recalcBatchMonitoringValues(rvms);
			
			result = monDao.getMonitoringRvmList(request, viewRvmIds);
		}

		return result;
	}

	@Override
	public DataSourceResult getServiceRecordList(DataSourceRequest request, 
			HttpSession session, boolean isEffectiveMode) {
		List<Integer> viewRvmIds = monDao.getViewRvmIds(session, isEffectiveMode);
		
		return monDao.getServiceRecordList(request, viewRvmIds);
	}
	@Override
	public List<RvmStatusLine> getServiceRecordListByPeriod(
			Date startDate, Date endDate, HttpSession session) {
		List<Integer> viewRvmIds = monDao.getViewRvmIds(session, false);
		
		return monDao.getServiceRecordListByPeriod(viewRvmIds, startDate, endDate);
	}

	@Override
	public List<Object[]> getServiceRecordRunningMinCountListByPeriod(
			Date startDate, Date endDate, HttpSession session) {
		List<Integer> viewRvmIds = monDao.getViewRvmIds(session, false);
		
		return monDao.getServiceRecordRunningMinCountListByPeriod(viewRvmIds, startDate, endDate);
	}

	@Override
	public List<Object[]> getServiceRecordRunningMinCountListByStateDate(
			Date stateDate, HttpSession session) {
		List<Integer> viewRvmIds = monDao.getViewRvmIds(session, false);
		
		return monDao.getServiceRecordRunningMinCountListByStateDate(viewRvmIds, stateDate);
	}

	@Override
	public DataSourceResult getRvmStatusList(DataSourceRequest request,
			HttpSession session) {
		Date reqDate = Util.parseDate(request.getReqStrValue2());
		Date stateDate = Util.removeTimeOfDate(reqDate);
		ArrayList<Integer> ids = new ArrayList<Integer>();
		
		Calendar cal = Calendar.getInstance();
		int minuteIndex = 0;
    	String status = request.getReqStrValue1();
		
    	if (Util.isValid(status) && status.equals("0")) {
    		List<Rvm> rvmList = rvmDao.getListBySiteId(Util.getSessionSiteId(session), false);
    		
    		ArrayList<Integer> rvmIds = new ArrayList<Integer>();
    		ArrayList<Integer> minusRvmIds = new ArrayList<Integer>();
    		
    		if (stateDate != null) {
        		for(Rvm rvm : rvmList) {
            		if (stateDate.before(rvm.getEffectiveStartDate())) {
            		} else if (rvm.getEffectiveEndDate() != null && stateDate.after(rvm.getEffectiveEndDate())) {
            		} else if (rvm.getServiceType().equals("M") || rvm.getServiceType().equals("S")) {
            			rvmIds.add(rvm.getId());
            		}
        		}
    		}

    		if (Util.isValid(status)) {
        		List<RvmStatusLine> statusLines = monDao.getRvmStatusLineListByStateDateSiteId(
        				stateDate, Util.getSessionSiteId(session));
        		for(RvmStatusLine line : statusLines) {
        			minusRvmIds.add(line.getRvm().getId());
        		}
    		}

    		rvmIds.removeAll(minusRvmIds);
    		
    		return rvmDao.getList(request, rvmIds);
    	} else {
    		if (reqDate != null) {
    			cal.setTime(reqDate);
    			minuteIndex = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
    			if (minuteIndex < 0 || minuteIndex >= 1440) {
    				minuteIndex = 0;
    			}
    		}

    		if (Util.isValid(status)) {
        		List<RvmStatusLine> statusLines = monDao.getRvmStatusLineListByStateDateSiteId(
        				stateDate, Util.getSessionSiteId(session));
        		for(RvmStatusLine line : statusLines) {
        			Rvm rvm = line.getRvm();
        			if (rvmService.isEffectiveRvm(rvm) && (rvm.getServiceType().equals("M") || rvm.getServiceType().equals("S")) && 
        					line.getStatusLine().substring(minuteIndex, minuteIndex + 1).equals(status)) {
        				ids.add(line.getId());
        			}
        		}
    		}
        	
    		return monDao.getRvmStatusList(request, ids);
    	}
	}

	
	//
	// for Common
	//
	@Override
	public void cleanUpOldTaskDataBefore(Date date) {
		cleanUpOldTaskDataBefore(date, 30000);
	}

	@Override
	public void cleanUpOldTaskDataBefore(Date date, int maxCnt) {
		List<MonTask> oldMonTasks = new ArrayList<MonTask>();
		
		List<Integer> oldMonTaskIds = getOldMonTaskIdsBefore(date, maxCnt);
		for(Integer id : oldMonTaskIds) {
			MonTask monTask = new MonTask();
			monTask.setId(id);
			oldMonTasks.add(monTask);
		}
		
		int monTaskSize = oldMonTasks.size();

		if (monTaskSize > 0) {
			deleteMonTasks(oldMonTasks);
		}
		
		logger.info("Clean up Old Task Data(before " + Util.toDateString(date) + "): " +
					"monTasks " + monTaskSize);
	}

	@Override
	public void cleanUpOldStatusDataBefore(Date date) {
		cleanUpOldStatusDataBefore(date, 30000);
	}

	@Override
	public void cleanUpOldStatusDataBefore(Date date, int maxCnt) {
		List<RecStatusStat> oldRecStatusStats = new ArrayList<RecStatusStat>();
		List<RvmStatusLine> oldStbStatusLines = new ArrayList<RvmStatusLine>();
		
		List<Integer> oldRecStatusStatIds = getRecStatusStatIdsBefore(date, maxCnt);
		for(Integer id : oldRecStatusStatIds) {
			RecStatusStat statusStat = new RecStatusStat();
			statusStat.setId(id);
			oldRecStatusStats.add(statusStat);
		}
		
		List<Integer> oldStatusLineIds = getRvmStatusLineIdsBefore(date, maxCnt);
		for(Integer id : oldStatusLineIds) {
			RvmStatusLine statusLine = new RvmStatusLine();
			statusLine.setId(id);
			oldStbStatusLines.add(statusLine);
		}
		
		int statusStatSize = oldRecStatusStats.size();
		int statusLineSize = oldStbStatusLines.size();

		if (statusStatSize > 0) {
			deleteRecStatusStats(oldRecStatusStats);
		}

		if (statusLineSize > 0) {
			deleteRvmStatusLines(oldStbStatusLines);
		}
		
		logger.info("Clean up Old Status Data(before " + Util.toDateString(date) + "): " +
					"statusStats " + statusStatSize + ", statusLines " + statusLineSize);
	}
	
	@Override
	public List<Integer> getDestRvmIds(boolean isAllRvmMode, ArrayList<Object> rvmGroupIds, ArrayList<Object> rvmIds,
			ArrayList<Object> tagIds, int siteId, int rvmGroupId) {
		
    	Site site = siteService.getSite(siteId);
    	List<Integer> ids = new ArrayList<Integer>();
    	
    	if (site != null) {
        	if (isAllRvmMode) {
        		if (rvmGroupId > 0) {
        			List<RvmGroupRvm> rvmGroupRvms = 
        					rvmService.getRvmGroupRvmListBySiteIdRvmGroupId(site.getId(), rvmGroupId, true);
        			
        			for (RvmGroupRvm rvmGroupRvm : rvmGroupRvms) {
						ids.add(rvmGroupRvm.getRvm().getId());
        			}
        		} else {
        			// 현재 사이트 모드
            		List<Rvm> rvmList = rvmService.getRvmListBySiteId(site.getId(), true);
            		
            		for (Rvm rvm : rvmList) {
            			ids.add(rvm.getId());
            		}
        		}
        	} else {
        		for(Object rvmGroupObj : rvmGroupIds) {
        			List<RvmGroupRvm> rvmGroupRvms = 
        					rvmService.getRvmGroupRvmListBySiteIdRvmGroupId(site.getId(), (int) rvmGroupObj, true);
        			for (RvmGroupRvm rvmGroupRvm : rvmGroupRvms) {
    					if (!ids.contains(rvmGroupRvm.getRvm().getId())) {
    						ids.add(rvmGroupRvm.getRvm().getId());
    					}
        			}
        		}
    			
    			for(Object rvmObj : rvmIds) {
    				int rvmId = (int) rvmObj;
    				if (!ids.contains(rvmId)) {
    					ids.add(rvmId);
    				}
    			}
    			
    			if (tagIds.size() > 0) {
    				List<Rvm> tagRvms = getRvmListByOpTagValues(siteId, tagIds);
    				
    				if (rvmGroupId > 0) {
            			List<RvmGroupRvm> rvmGroupRvms = 
            					rvmService.getRvmGroupRvmListBySiteIdRvmGroupId(site.getId(), rvmGroupId, true);
            			List<Integer> tempIds = new ArrayList<Integer>();
            			
            			for(RvmGroupRvm rvmGroupStb : rvmGroupRvms) {
            				tempIds.add(rvmGroupStb.getRvm().getId());
            			}
            			
            			for(Rvm rvm : tagRvms) {
            				if (!ids.contains(rvm.getId()) && tempIds.contains(rvm.getId())) {
            					ids.add(rvm.getId());
            				}
            			}
    				} else {
            			for(Rvm rvm : tagRvms) {
            				if (!ids.contains(rvm.getId())) {
            					ids.add(rvm.getId());
            				}
            			}
    				}
    			}
        	}
    	}
    	
		return ids;
	}

	@Override
	public List<Integer> getDestRvmIds(boolean isAllStbMode, ArrayList<Object> rvmGroupIds, ArrayList<Object> rvmIds,
			int siteId, int rvmGroupId) {
		
    	Site site = siteService.getSite(siteId);
    	List<Integer> ids = new ArrayList<Integer>();
    	
    	if (site != null) {
        	if (isAllStbMode) {
        		if (rvmGroupId > 0) {
        			List<RvmGroupRvm> rvmGroupStbs = 
        					rvmService.getRvmGroupRvmListBySiteIdRvmGroupId(site.getId(), rvmGroupId, true);
        			
        			for (RvmGroupRvm rvmGroupStb : rvmGroupStbs) {
						ids.add(rvmGroupStb.getRvm().getId());
        			}
        		} else {
        			// 현재 사이트 모드
            		List<Rvm> rvmList = rvmService.getRvmListBySiteId(site.getId(), true);
            		
            		for (Rvm rvm : rvmList) {
            			ids.add(rvm.getId());
            		}
        		}
        	} else {
        		for(Object rvmGroupObj : rvmGroupIds) {
        			List<RvmGroupRvm> rvmGroupStbs = 
        					rvmService.getRvmGroupRvmListBySiteIdRvmGroupId(site.getId(), (int) rvmGroupObj, true);
        			for (RvmGroupRvm rvmGroupStb : rvmGroupStbs) {
    					if (!ids.contains(rvmGroupStb.getRvm().getId())) {
    						ids.add(rvmGroupStb.getRvm().getId());
    					}
        			}
        		}
    			
    			for(Object rvmObj : rvmIds) {
    				int rvmId = (int) rvmObj;
    				if (!ids.contains(rvmId)) {
    					ids.add(rvmId);
    				}
    			}
        	}
    	}
    	
		return ids;
	}

	@Override
	public List<Rvm> sortRvmListOrderByLastReportDate(List<Rvm> list) {
		
		if (list == null || list.size() == 0) {
			return list;
		}
		
		ArrayList<RvmItem> items = new ArrayList<RvmItem>();
		HashMap<String, Rvm> map = new HashMap<String, Rvm>();
		
		for(Rvm rvm : list) {
			map.put(rvm.getId() + "", rvm);
			items.add(new RvmItem(rvm.getId(), getRvmLastReportLastUpdateDateByRvmId(rvm.getId()) ));
		}
		
		Collections.sort(items, EcoComparator.RvmItemDateComparator);
		
		ArrayList<Rvm> ret = new ArrayList<Rvm>();
		for(RvmItem item : items) {
			ret.add(map.get(item.getId() + ""));
		}
		
		return ret;
	}
	
	@Override
	public List<Rvm> getRvmListByOpTagValue(int siteId, String value) {
		
		Session session = sessionFactory.getCurrentSession();

		Date time = new Date();

		Criterion rest1 = Restrictions.lt("effectiveStartDate", time);
		Criterion rest2 = Restrictions.isNull("effectiveEndDate");
		Criterion rest3 = Restrictions.gt("effectiveEndDate", time);

		@SuppressWarnings("unchecked")
		List<Rvm> list = session.createCriteria(Rvm.class)
				.createAlias("site", "site")
				.createAlias("rvmLastReport", "rvmLastReport")
				.add(Restrictions.and(rest1, Restrictions.or(rest2, rest3)))
				.add(Restrictions.eq("site.id", siteId))
				.add(Restrictions.like("rvmLastReport.opTag", "%" + value + "%"))
				.list();

		ArrayList<Rvm> retList = new ArrayList<Rvm>();
		for(Rvm rvm : list) {
			if (rvm.getRvmLastReport() != null && 
					("|" + rvm.getRvmLastReport().getOpTag() + "|").indexOf("|" + value + "|") > -1) {
				retList.add(rvm);
			}
		}

		return retList;
	}

	@Override
	public List<Rvm> getRvmListByOpTagValues(int siteId, ArrayList<Object> ids) {
		
		ArrayList<Rvm> list = new ArrayList<Rvm>();
		List<Integer> rvmIds = new ArrayList<Integer>();

		if (ids != null && ids.size() > 0) {
			for(Object tagObj : ids) {
//				BasTag tag = basService.getTag((int) tagObj);
//				if (tag != null) {
//					List<Stb> oneList = getStbListByOpTagValue(siteId, tag.getTagValue());
//					for(Stb stb : oneList) {
//	    				if (!stbIds.contains(stb.getId())) {
//	    					stbIds.add(stb.getId());
//	    					list.add(stb);
//	    				}
//					}
//				}
			}
		}
		
		return list;
	}
	
	
	//
	// for RtnSchdTask Dao
	//
	// Common
	@Override
	public RtnSchdTask getRtnSchdTask(int id) {
		return rtnSchdTaskDao.get(id);
	}

	@Override
	public void saveOrUpdate(RtnSchdTask rtnSchdTask) {
		rtnSchdTaskDao.saveOrUpdate(rtnSchdTask);
	}

	@Override
	public void deleteRtnSchdTask(RtnSchdTask rtnSchdTask) {
		rtnSchdTaskDao.delete(rtnSchdTask);
	}

	@Override
	public void deleteRtnSchdTasks(List<RtnSchdTask> rtnSchdTasks) {
		rtnSchdTaskDao.delete(rtnSchdTasks);
	}
	
	// for Kendo Grid Remote Read
	@Override
	public DataSourceResult getRtnSchdTaskList(DataSourceRequest request) {
		return rtnSchdTaskDao.getList(request);
	}

	// for RtnSchdTask specific
	@Override
	public RtnSchdTask getRtnSchdTask(Site site, String taskName) {
		return rtnSchdTaskDao.get(site, taskName);
	}
	
	@Override
	public List<RtnSchdTask> getRtnSchdTaskListBySiteId(int siteId) {
		return rtnSchdTaskDao.getListBySiteId(siteId);
	}
	
	
	//
	// for RtnSchdTaskRvm Dao
	//
	// Common
	@Override
	public RtnSchdTaskRvm getRtnSchdTaskRvm(int id) {
		return rtnSchdTaskRvmDao.get(id);
	}

	@Override
	public void saveOrUpdate(RtnSchdTaskRvm rtnSchdTaskRvm) {
		rtnSchdTaskRvmDao.saveOrUpdate(rtnSchdTaskRvm);
	}

	@Override
	public void deleteRtnSchdTaskRvm(RtnSchdTaskRvm rtnSchdTaskRvm) {
		rtnSchdTaskRvmDao.delete(rtnSchdTaskRvm);
	}

	@Override
	public void deleteRtnSchdTaskRvms(List<RtnSchdTaskRvm> rtnSchdTaskRvms) {
		rtnSchdTaskRvmDao.delete(rtnSchdTaskRvms);
	}
	
	// for Kendo Grid Remote Read
	@Override
	public DataSourceResult getRvmGroupRvmList(DataSourceRequest request) {
		return rtnSchdTaskRvmDao.getList(request);
	}

	// for RtnSchdTaskRvm specific
	@Override
	public List<RtnSchdTaskRvm> getRtnSchdTaskRvmListBySiteIdRtnSchdTaskId(
			int siteId, int rtnSchdTaskId) {
		return rtnSchdTaskRvmDao.getListBySiteIdRtnSchdTaskId(siteId, rtnSchdTaskId);
	}

	@Override
	public int getRtnSchdTaskRvmCountBySiteIdRtnSchdTaskId(int siteId,
			int rtnSchdTaskId) {
		return rtnSchdTaskRvmDao.getCountBySiteIdRtnSchdTaskId(siteId, rtnSchdTaskId);
	}

	@Override
	public List<RtnSchdTaskRvm> getRtnSchdTaskRvmListByRvmId(int rvmId) {
		return rtnSchdTaskRvmDao.getListByRvmId(rvmId);
	}
	
	@Override
	public List<RtnSchdTaskRvm> getNextRtnSchdTaskRvmListByRvmId(int rvmId, Date now) {
		ArrayList<RtnSchdTaskRvm> toBeExecuted = new ArrayList<RtnSchdTaskRvm>();
		
		if (now != null) {
			List<RtnSchdTaskRvm> list = getRtnSchdTaskRvmListByRvmId(rvmId);
			Calendar cal = Calendar.getInstance();
			Date destDate = null;
			Date cancelDate = null;
			
			for (RtnSchdTaskRvm taskRvm : list) {
				if (taskRvm.getRtnSchdTask().getPublished().equals("Y")) {
					String destTime = "";
					cal.setTime(now);
					
					switch (cal.get(Calendar.DAY_OF_WEEK)) {
					case Calendar.MONDAY:
						destTime = taskRvm.getRtnSchdTask().getMonTime();
						break;
					case Calendar.TUESDAY:
						destTime = taskRvm.getRtnSchdTask().getTueTime();
						break;
					case Calendar.WEDNESDAY:
						destTime = taskRvm.getRtnSchdTask().getWedTime();
						break;
					case Calendar.THURSDAY:
						destTime = taskRvm.getRtnSchdTask().getThuTime();
						break;
					case Calendar.FRIDAY:
						destTime = taskRvm.getRtnSchdTask().getFriTime();
						break;
					case Calendar.SATURDAY:
						destTime = taskRvm.getRtnSchdTask().getSatTime();
						break;
					case Calendar.SUNDAY:
						destTime = taskRvm.getRtnSchdTask().getSunTime();
						break;
					}
					
					if (Util.isNotValid(destTime)) {
						continue;
					}
					
					destDate = Util.getDate(destTime);
					
					cal.setTime(destDate);

					if (taskRvm.getRtnSchdTask().getAutoCancelMins() == 0) {
						cal.add(Calendar.DATE, 1);
						cancelDate = Util.removeTimeOfDate(cal.getTime());
					} else {
						cal.add(Calendar.MINUTE, taskRvm.getRtnSchdTask().getAutoCancelMins());
						cancelDate = cal.getTime();
					}
					
					if (now.after(destDate) && now.before(cancelDate) &&
							!taskRvm.getRtnSchdTask().getCommand().equals("PowerOnWol.bbmc")) {
						boolean sameDataFound = false;
						List<MonTask> tasklist = getMonTaskListByRvmId(rvmId);
						for (MonTask monTask : tasklist) {
							if (now.after(monTask.getDestDate()) && now.before(monTask.getCancelDate()) 
									&& monTask.getRtnSchdTask() != null && Util.isNotValid(monTask.getParams())
									&& monTask.getRtnSchdTaskRvm() != null) {
								if (monTask.getCommand().equals(taskRvm.getRtnSchdTask().getCommand()) &&
										monTask.getRtnSchdTask().getId() == taskRvm.getRtnSchdTask().getId() &&
										monTask.getRtnSchdTaskRvm().getId() == taskRvm.getId() &&
										(monTask.getStatus().equals("2") || monTask.getStatus().equals("3") || 
										monTask.getStatus().equals("S") || monTask.getStatus().equals("P") ||
										monTask.getStatus().equals("F") || monTask.getStatus().equals("C")) &&
										monTask.getDestDate().compareTo(destDate) == 0 &&
										monTask.getCancelDate().compareTo(cancelDate) == 0) {
									sameDataFound = true;
									break;
								}
							}
						}
						
						if (!sameDataFound) {
							taskRvm.setDestDate(destDate);
							taskRvm.setCancelDate(cancelDate);
							
							toBeExecuted.add(taskRvm);
						}
					}
				}
			}
		}
		
		return toBeExecuted;
	}
	
	
	//
	// for MonTask Dao
	//
	// Common
	@Override
	public MonTask getMonTask(int id) {
		return monTaskDao.get(id);
	}
	
	@Override
	public void saveOrUpdate(MonTask monTask) {
		monTaskDao.saveOrUpdate(monTask);
	}
	
	@Override
	public void deleteMonTask(MonTask monTask) {
		monTaskDao.delete(monTask);
	}

	@Override
	public void deleteMonTasks(List<MonTask> monTasks) {
		monTaskDao.delete(monTasks);
	}
	
	// for Kendo Grid Remote Read
	@Override
	public DataSourceResult getMonTaskList(DataSourceRequest request, 
			HttpSession session) {
		return monTaskDao.getList(request, monDao.getViewRvmIds(
				session, false));
	}

	@Override
	public DataSourceResult getSpecialMonTaskList(DataSourceRequest request,
			HttpSession session) {
		return monTaskDao.getList(request);
	}

	@Override
	public DataSourceResult getMonTaskList(DataSourceRequest request,
			HttpSession session, int lastMins) {
		return monTaskDao.getList(request, monDao.getViewRvmIds(
				session, false), lastMins);
	}
	
	// for MonTask specific
	@Override
	public List<MonTask> getMonTaskListByRvmId(int rvmId) {
		return monTaskDao.getListByRvmId(rvmId);
	}
	
	@Override
	public List<MonTask> getNextMonTaskListByRvmId(int rvmId, Date now) {
		ArrayList<MonTask> toBeExecuted = new ArrayList<MonTask>();
		
		if (now != null) {
			List<MonTask> list = getMonTaskListByRvmId(rvmId);
			for (MonTask monTask : list) {
				if (now.after(monTask.getDestDate()) && now.before(monTask.getCancelDate()) 
						&& monTask.getRtnSchdTask() == null && monTask.getStatus().equals("1")
						&& !monTask.getCommand().equals("PowerOnWol.bbmc")) {
					toBeExecuted.add(monTask);
				}
			}
		}
		
		return toBeExecuted;
	}

	@Override
	public MonTask getMonTaskByRtnSchdTaskRvmId(int rtnSchdTaskRvmId) {
		List<MonTask> list = monTaskDao.getListByRtnSchdTaskRvmId(rtnSchdTaskRvmId);
		Collections.sort(list, EcoComparator.MonTaskIdReverseComparator);
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<Integer> getOldMonTaskIdsBefore(Date destDate, int maxCnt) {
		return monTaskDao.getOldIdsBefore(destDate, maxCnt);
	}
	
	
	//
	// for MonEventReportDao
	//
	// Common
	@Override
	public MonEventReport getMonEventReport(int id) {
		return monEventReportDao.get(id);
	}

	@Override
	public void saveOrUpdate(MonEventReport monEventReport) {
		monEventReportDao.saveOrUpdate(monEventReport);
	}

	@Override
	public void deleteMonEventReport(MonEventReport monEventReport) {
		monEventReportDao.delete(monEventReport);
	}

	@Override
	public void deleteMonEventReports(List<MonEventReport> monEventReports) {
		monEventReportDao.delete(monEventReports);
	}

	// for Kendo Grid Remote Read
	@Override
	public DataSourceResult getMonEventReportList(DataSourceRequest request) {
		return monEventReportDao.getList(request);
	}
	
	// for MonEventReport Specific
	@Override
	public List<MonEventReport> getMonEventReportListBySiteId(int siteId) {
		return monEventReportDao.getListBySiteId(siteId);
	}
	
	@Override
	public boolean isInSeriousWERStatus(String event, String equipType, int equipId) {
		if (Util.isValid(event) && event.equals("STBWER") && Util.isValid(equipType) && equipType.equals("P")) {
			// 3분 내 3개 이상의 STBWER 등록 시도 체크
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, -3);

			return (monEventReportDao.getCount(cal.getTime(), event, equipType, equipId) >= 2);
		}

		return false;
	}

	@Override
	public int getMonEventReportCount(Date fromDt, String event, String equipType, int equipId) {
		return monEventReportDao.getCount(fromDt, event, equipType, equipId);
	}
	
	@Override
	public MonEventReport getLastMonEventReport(Date fromDt, String event, String details, String equipType,
			int equipId) {
		return monEventReportDao.getLast(fromDt, event, details, equipType, equipId);
	}


	@Override
	public List<MonEventReport> getMonEventReportListByRvmId(int rvmId) {
		return monEventReportDao.getListByRvmId(rvmId);
	}
	
	
	//
	// for RecDailySummaryDao
	//
	// Common
	@Override
	public RecDailySummary getRecDailySummary(int id) {
		return recDailySummaryDao.get(id);
	}
	
	@Override
	public void saveOrUpdate(RecDailySummary recDailySummary) {
		recDailySummaryDao.saveOrUpdate(recDailySummary);
	}

	@Override
	public void deleteRecDailySummary(RecDailySummary recDailySummary) {
		recDailySummaryDao.delete(recDailySummary);
	}

	@Override
	public void deleteRecDailySummaries(List<RecDailySummary> recDailySummaries) {
		recDailySummaryDao.delete(recDailySummaries);
	}

	// for Kendo Grid Remote Read
	@Override
	public DataSourceResult getRecDailySummaryList(DataSourceRequest request) {
		return recDailySummaryDao.getList(request);
	}

	// for RecDailySummary specific
	@Override
	public RecDailySummary getRecDailySummary(Site site, Date workDate) {
		return recDailySummaryDao.get(site, workDate);
	}

	@Override
	public void saveOrUpdateRecDailySummary(Site site, Date workDate, int rvmCount, int totalCount, long totalRunningMins,
			String calculated) {
		RecDailySummary dailySummary = getRecDailySummary(site, Util.removeTimeOfDate(workDate));
		if (dailySummary == null) {
			dailySummary = new RecDailySummary(site, workDate, rvmCount, 
					totalCount, totalRunningMins, calculated);
		} else {
			dailySummary.setRvmCount(rvmCount);
			dailySummary.setTotalCount(totalCount);
			dailySummary.setCalculated(calculated);
			
			if (totalRunningMins > 0 && rvmCount > 0) {
				dailySummary.setAvgRunningMins((float)(Math.round((double)totalRunningMins / (double)rvmCount 
						* Math.pow(10, 1)) / Math.pow(10, 1)));
			}
			
			dailySummary.setWhoLastUpdateDate(new Date());
		}
		
		recDailySummaryDao.saveOrUpdate(dailySummary);
	}







	
	
	
	
	
	

	@Override
	public void deleteRecStatusStats(List<RecStatusStat> recStatusStats) {
		recStatusStatDao.delete(recStatusStats);
	}


	@Override
	public int getRecStatusStatCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DataSourceResult getRecStatusStatList(DataSourceRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataSourceResult getRecStatusStatListByDate(DataSourceRequest request, Date stateDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataSourceResult getRecStatusStatListByTime(DataSourceRequest request, String stateTime) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveOrUpdateRecStatusStat(Site site, Date stateDate, int status6, int status5, int status4, int status3,
			int status2, int status0, long totalRuningMins) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Integer> getRecStatusStatIdsBefore(Date stateDate, int maxCnt) {
		// TODO Auto-generated method stub
		return null;
	}


	
}
