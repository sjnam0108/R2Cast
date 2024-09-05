package kr.co.r2cast.models.eco.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpSession;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.LoginUser;
import kr.co.r2cast.models.eco.Rvm;
import kr.co.r2cast.models.eco.RvmGroupRvm;
import kr.co.r2cast.models.eco.RvmLastReport;
import kr.co.r2cast.models.eco.RvmStatusLine;
import kr.co.r2cast.models.eco.service.MonitoringService;
import kr.co.r2cast.models.eco.service.RvmService;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.SiteSite;
import kr.co.r2cast.models.fnd.service.SiteService;
import kr.co.r2cast.utils.SolUtil;
import kr.co.r2cast.utils.Util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
public class MonitoringDaoImpl implements MonitoringDao {
    @Autowired
    private SessionFactory sessionFactory;
    
    @Autowired
    private RvmService rvmService;
    
    @Autowired
    private SiteService siteService;
    
    @Autowired
    private MonitoringService monService;
    
    @Override
    public ArrayList<Integer> getSiteIdAndRvmGroupId(HttpSession session) {
    	if (session != null) {
    		LoginUser loginUser = (LoginUser) session.getAttribute("loginUser");
    		if (loginUser != null) {
    			StringTokenizer st = new StringTokenizer(loginUser.getUserViewId(), "|");
    			
    			if (st.countTokens() == 2) {
    				int siteId = -1;
    				int rvmGroupId = -1;
    				
    				try {
    					siteId = Integer.parseInt(st.nextToken());
    					rvmGroupId = Integer.parseInt(st.nextToken());
    				} catch (Exception e) {}
    				
    				if (siteId != -1) {
    					return new ArrayList<Integer>(Arrays.asList(siteId, rvmGroupId));
    				}
    			}
    		}
    	}
    	
    	return null;
    }

    @Override
    public List<Integer> getViewRvmIds(HttpSession session, boolean isEffectiveMode) {
    	return getViewRvmIds(session, isEffectiveMode, false);
    }

    @Override
    public List<Integer> getViewRvmIds(HttpSession session, boolean isEffectiveMode,
    		boolean isOnlyServiceMode) {
    	ArrayList<Integer> rvmIds = new ArrayList<Integer>();
    	
    	List<Integer> ids = getSiteIdAndRvmGroupId(session);
    	if (ids != null) {
    		int siteId = ids.get(0);
    		int rvmGroupId = ids.get(1);
    		
	    	// All Sites: {SiteId}, 0
        	// Site: {SiteId}, -1
        	// StbGroup: {SiteId}, {StbGroupId}
    		if (rvmGroupId < 1) {
    			List<Rvm> siteRvms = rvmService.getRvmListBySiteId(siteId, isEffectiveMode);
    			
    			for(Rvm rvm : siteRvms) {
    				if (!rvmIds.contains(rvm.getId()) && ((!isOnlyServiceMode && !rvm.getServiceType().equals("C")) || 
    						(isOnlyServiceMode && (rvm.getServiceType().equals("M") || rvm.getServiceType().equals("S"))))) {
    					rvmIds.add(rvm.getId());
    				}
    			}
    			
    			if (rvmGroupId == 0) {
    				List<SiteSite> siteSiteList = 
    						siteService.getSiteSiteListByParentSiteId(siteId);
    		    	for (SiteSite siteSite : siteSiteList) {
    		    		siteRvms = rvmService.getRvmListBySiteId(siteSite.
    		    				getChildSite().getId(), true);
    	    			
    	    			for(Rvm rvm : siteRvms) {
    	    				if (!rvmIds.contains(rvm.getId()) && ((!isOnlyServiceMode && !rvm.getServiceType().equals("C")) || 
    	    						(isOnlyServiceMode && (rvm.getServiceType().equals("M") || rvm.getServiceType().equals("S"))))) {
    	    					rvmIds.add(rvm.getId());
    	    				}
    	    			}
    		    	}
    			}
    		} else {
    			List<RvmGroupRvm> rvmGroupRvms = 
    					rvmService.getRvmGroupRvmListBySiteIdRvmGroupId(siteId, 
    							rvmGroupId, true);
    			
    			for(RvmGroupRvm rvmGroupRvm : rvmGroupRvms) {
    				if (!rvmIds.contains(rvmGroupRvm.getRvm().getId()) && 
    						((!isOnlyServiceMode && !rvmGroupRvm.getRvm().getServiceType().equals("C")) || 
    						(isOnlyServiceMode && (rvmGroupRvm.getRvm().getServiceType().equals("M") || 
    								rvmGroupRvm.getRvm().getServiceType().equals("S"))))) {
    					rvmIds.add(rvmGroupRvm.getRvm().getId());
    				}
    			}
    		}
    	}
    	
    	return rvmIds;
    }

    @Override
    public List<Integer> getSiteViewRvmIds(HttpSession session, boolean isEffectiveMode, 
    		boolean isOnlyServiceMode) {
    	return getSiteViewRvmIds(Util.getSessionSiteId(session), isEffectiveMode, isOnlyServiceMode);
    }

    @Override
    public List<Integer> getSiteViewRvmIds(int siteId, boolean isEffectiveMode, 
    		boolean isOnlyServiceMode) {
    	ArrayList<Integer> rvmIds = new ArrayList<Integer>();
    	
    	if (siteId > 0) {
			List<Rvm> siteRvms = rvmService.getRvmListBySiteId(siteId, isEffectiveMode);
			
			for(Rvm rvm : siteRvms) {
				if (!rvmIds.contains(rvm.getId()) && ((!isOnlyServiceMode && !rvm.getServiceType().equals("C")) || 
						(isOnlyServiceMode && (rvm.getServiceType().equals("M") || rvm.getServiceType().equals("S"))))) {
					rvmIds.add(rvm.getId());
				}
			}
    	}
    	
    	return rvmIds;
    }
    
    @Override
	public List<Rvm> getMonValCalcedSiteRvmList(int siteId, boolean isEffectiveMode, 
			boolean isOnlyServiceMode) {
		ArrayList<Integer> rvmIds = new ArrayList<Integer>();
    	ArrayList<Rvm> rvms = new ArrayList<Rvm>();
    	
    	if (siteId > 0) {
			List<Rvm> siteRvms = rvmService.getRvmListBySiteId(siteId, isEffectiveMode);
			
			for(Rvm rvm : siteRvms) {
				if (!rvmIds.contains(rvm.getId()) && ((!isOnlyServiceMode && !rvm.getServiceType().equals("C")) || 
						(isOnlyServiceMode && (rvm.getServiceType().equals("M") || rvm.getServiceType().equals("S"))))) {
					rvmIds.add(rvm.getId());
					rvms.add(rvm);
				}
			}
    	}

    	if (rvms.size() > 0) {
    		List<RvmStatusLine> statusLines = 
    				getRvmStatusLineListByStateDateSiteId(Util.removeTimeOfDate(new Date()), siteId);
    		HashMap<Integer, RvmStatusLine> statusMap = new HashMap<Integer, RvmStatusLine>();
    		for(RvmStatusLine line : statusLines) {
    			statusMap.put(line.getRvm().getId(), line);
    		}

    		for(Rvm rvm : rvms) {
    			RvmStatusLine rvmStatusLine = statusMap.get(rvm.getId());
    			String oldLastStatus = rvm.getLastStatus();
    			String newLastStatus = oldLastStatus;
    			
    			int oldRunningMinCount = rvm.getRunningMinCount();
    			int newRunningMinCount = oldRunningMinCount;

    			if (rvmStatusLine == null) {
    				newLastStatus = "0";
    				newRunningMinCount = 0;
    			} else {
    				newRunningMinCount = rvmStatusLine.getRunningMinCount();

    				if (SolUtil.isDateDuring1Minute(rvmStatusLine.getWhoLastUpdateDate())) {
    					newLastStatus = rvmStatusLine.getLastStatus();
    				} else {
    					newLastStatus = "2";
    				}
    			}
    			
    			if (!oldLastStatus.equals(newLastStatus) || oldRunningMinCount != newRunningMinCount) {
    				rvm.setLastStatus(newLastStatus);
    				rvm.setRunningMinCount(newRunningMinCount);
    			}
    		}
    	}
    	
    	return rvms;
    }

	@Override
	public List<Rvm> getFailureRvmListBySiteId(int siteId, boolean isEffectiveMode, int allowedMins) {
		
		ArrayList<Integer> rvmIds = new ArrayList<Integer>();
    	ArrayList<Rvm> rvms = new ArrayList<Rvm>();
    	
    	if (siteId > 0) {
			List<Rvm> siteRvms = rvmService.getRvmListBySiteId(siteId, isEffectiveMode);
			
			for(Rvm rvm : siteRvms) {
				if (!rvmIds.contains(rvm.getId()) && rvm.getServiceType().equals("M") && 
						(rvm.getLastStatus().equals("0") || rvm.getLastStatus().equals("2"))) {

					Date lastReportDate = getLastUpdateDateByRvmId(rvm.getId());
					if (lastReportDate != null && !SolUtil.isDateDuringMins(lastReportDate, allowedMins)) {
						rvmIds.add(rvm.getId());
						rvms.add(rvm);
					}
				}
			}
    	}
    	
    	return rvms;
	}

	@Override
	public DataSourceResult getMonitoringRvmList(DataSourceRequest request, 
			List<Integer> rvmIds) {
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("site", Site.class);
		
		HashMap<String, Class<?>> outerMap = new HashMap<String, Class<?>>();
		outerMap.put("rvmLastReport", RvmLastReport.class);

		return request.toDataSourceResult(sessionFactory.getCurrentSession(), Rvm.class, 
        		map, outerMap, "id", rvmIds);
	}
    
	@Override
	public DataSourceResult getServiceRecordList(DataSourceRequest request, 
			List<Integer> rvmIds) {
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("rvm", Rvm.class);
		
		HashMap<String, Class<?>> outerMap = new HashMap<String, Class<?>>();

		DataSourceResult result = request.toDataSourceResult(sessionFactory.getCurrentSession(), 
				RvmStatusLine.class, map, outerMap, "rvm.id", rvmIds);
		
		return result;
	}
    
	@SuppressWarnings("unchecked")
	@Override
	public List<RvmStatusLine> getServiceRecordListByPeriod(List<Integer> rvmIds, 
			Date startDate, Date endDate) {
		Session session = sessionFactory.getCurrentSession();
		
        if (rvmIds.size() == 0) {
        	rvmIds.add(Integer.MAX_VALUE);
        }
        
		return session.createCriteria(RvmStatusLine.class)
				.createAlias("rvm", "rvm")
				.add(Restrictions.in("rvm.id", rvmIds))
				.add(Restrictions.ge("stateDate", startDate))
				.add(Restrictions.le("stateDate", endDate)).list();
	}
    
	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getServiceRecordRunningMinCountListByPeriod(List<Integer> rvmIds, 
			Date startDate, Date endDate) {
		Session session = sessionFactory.getCurrentSession();
		
        if (rvmIds.size() == 0) {
        	rvmIds.add(Integer.MAX_VALUE);
        }
        
		return session.createCriteria(RvmStatusLine.class)
				.createAlias("rvm", "rvm")
				.setProjection(Projections.projectionList()
						.add(Projections.property("rvm.id").as("rvmId"))
						.add(Projections.property("stateDate").as("stateDate"))
						.add(Projections.property("runningMinCount").as("runningMinCount"))
						)
				.add(Restrictions.in("rvm.id", rvmIds))
				.add(Restrictions.ge("stateDate", startDate))
				.add(Restrictions.le("stateDate", endDate)).list();
	}
    
	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getServiceRecordRunningMinCountListByStateDate(List<Integer> rvmIds, 
			Date stateDate) {
		Session session = sessionFactory.getCurrentSession();
		
        if (rvmIds.size() == 0) {
        	rvmIds.add(Integer.MAX_VALUE);
        }
        
		return session.createCriteria(RvmStatusLine.class)
				.createAlias("rvm", "rvm")
				.setProjection(Projections.projectionList()
						.add(Projections.property("rvm.id").as("rvmId"))
						.add(Projections.property("stateDate").as("stateDate"))
						.add(Projections.property("runningMinCount").as("runningMinCount"))
						)
				.add(Restrictions.in("rvm.id", rvmIds))
				.add(Restrictions.eq("stateDate", stateDate)).list();
	}

	@Override
	public void saveOrUpdate(RvmLastReport rvmLastReport) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(rvmLastReport);
	}

	@Override
	public Date getLastUpdateDateByRvmId(int rvmId) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<RvmLastReport> list = session.createCriteria(RvmLastReport.class)
				.createAlias("rvm", "rvm")
				.add(Restrictions.eq("rvm.id", rvmId)).list();
		
		return (list.isEmpty() ? null : list.get(0).getWhoLastUpdateDate());
	}

	@Override
	public void saveOrUpdate(RvmStatusLine rvmStatusLine) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(rvmStatusLine);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RvmStatusLine> getRvmStatusLineListByRvmId(int rvmId) {
		Session session = sessionFactory.getCurrentSession();

		return session.createCriteria(RvmStatusLine.class)
				.createAlias("rvm", "rvm")
				.add(Restrictions.eq("rvm.id", rvmId)).list();
	}

	@Override
	public RvmStatusLine getRvmStatusLineByStateDateRvmId(Date stateDate,
			int rvmId) {
		Session session = sessionFactory.getCurrentSession();

		@SuppressWarnings("unchecked")
		List<RvmStatusLine> list = session.createCriteria(RvmStatusLine.class)
				.createAlias("rvm", "rvm")
				.add(Restrictions.eq("rvm.id", rvmId))
				.add(Restrictions.eq("stateDate", stateDate)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RvmStatusLine> getRvmStatusLineListByStateDate(Date stateDate) {
		Session session = sessionFactory.getCurrentSession();

		return session.createCriteria(RvmStatusLine.class)
				.add(Restrictions.eq("stateDate", stateDate)).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RvmStatusLine> getRvmStatusLineListByStateDateSiteId(Date stateDate, int siteId) {
		Session session = sessionFactory.getCurrentSession();

		return session.createCriteria(RvmStatusLine.class)
				.createAlias("rvm", "rvm")
				.createAlias("rvm.site", "site")
				.add(Restrictions.eq("rvm.site.id", siteId))
				.add(Restrictions.eq("stateDate", stateDate)).list();
	}

	@Override
	public DataSourceResult getRvmStatusList(DataSourceRequest request,
			List<Integer> ids) {
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("rvm", Rvm.class);

		return request.toDataSourceResult(sessionFactory.getCurrentSession(), RvmStatusLine.class, 
        		map, "id", ids);
	}
	
	@Override
	public void recalcMonitoringValues(List<Integer> rvmIds) {
		Session session = sessionFactory.getCurrentSession();
		
		for(Integer id : rvmIds) {
			Rvm rvm = rvmService.getRvm(id);
			if (rvm != null) {
				RvmStatusLine rvmStatusLine = monService.getRvmStatusLineByStateDateRvmId(
	        			Util.removeTimeOfDate(new Date()), rvm.getId());
				String oldLastStatus = rvm.getLastStatus();
				String newLastStatus = oldLastStatus;
				
				int oldRunningMinCount = rvm.getRunningMinCount();
				int newRunningMinCount = oldRunningMinCount;

				if (rvmStatusLine == null) {
					newLastStatus = "0";
					newRunningMinCount = 0;
				} else {
					newRunningMinCount = rvmStatusLine.getRunningMinCount();

					if (SolUtil.isDateDuring1Minute(rvmStatusLine.getWhoLastUpdateDate())) {
						newLastStatus = rvmStatusLine.getLastStatus();
					} else {
						newLastStatus = "2";
					}
				}
				
				if (!oldLastStatus.equals(newLastStatus) || oldRunningMinCount != newRunningMinCount) {
					rvm.setLastStatus(newLastStatus);
					rvm.setRunningMinCount(newRunningMinCount);
					
					session.saveOrUpdate(rvm);
				}
			}
		}
	}
	
	@Override
	public void recalcBatchMonitoringValues(List<Rvm> rvms) {
		Session session = sessionFactory.getCurrentSession();

		List<RvmStatusLine> statusLines = 
				getRvmStatusLineListByStateDate(Util.removeTimeOfDate(new Date()));
		HashMap<Integer, RvmStatusLine> statusMap = new HashMap<Integer, RvmStatusLine>();
		for(RvmStatusLine line : statusLines) {
			statusMap.put(line.getRvm().getId(), line);
		}

		for(Rvm rvm : rvms) {
			RvmStatusLine rvmStatusLine = statusMap.get(rvm.getId());
			String oldLastStatus = rvm.getLastStatus();
			String newLastStatus = oldLastStatus;
			
			int oldRunningMinCount = rvm.getRunningMinCount();
			int newRunningMinCount = oldRunningMinCount;

			if (rvmStatusLine == null) {
				newLastStatus = "0";
				newRunningMinCount = 0;
			} else {
				newRunningMinCount = rvmStatusLine.getRunningMinCount();

				if (SolUtil.isDateDuring1Minute(rvmStatusLine.getWhoLastUpdateDate())) {
					newLastStatus = rvmStatusLine.getLastStatus();
				} else {
					newLastStatus = "2";
				}
			}
			
			if (!oldLastStatus.equals(newLastStatus) || oldRunningMinCount != newRunningMinCount) {
				rvm.setLastStatus(newLastStatus);
				rvm.setRunningMinCount(newRunningMinCount);
				
				session.saveOrUpdate(rvm);
			}
		}
	}

	@Override
	public void delete(List<RvmStatusLine> rvmStatusLines) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RvmStatusLine rvmStatusLine : rvmStatusLines) {
            session.delete(session.load(RvmStatusLine.class, rvmStatusLine.getId()));
        }
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getIdsBefore(Date stateDate, int maxCnt) {
		return (List<Integer>) sessionFactory.getCurrentSession()
				.createCriteria(RvmStatusLine.class)
				.add(Restrictions.lt("stateDate", stateDate))
				.setProjection(Projections.property("id"))
				.setMaxResults(maxCnt)
				.list();
	}
}
