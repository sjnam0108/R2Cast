package kr.co.r2cast.models.eco.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.eco.RecStatusStat;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.utils.Util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
public class RecStatusStatDaoImpl implements RecStatusStatDao {
    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public RecStatusStat get(int id) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<RecStatusStat> list = session.createCriteria(RecStatusStat.class)
				.add(Restrictions.eq("id", id)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<RecStatusStat> getList() {
		return sessionFactory.getCurrentSession().createCriteria(RecStatusStat.class).list();
	}

	@Override
	public void saveOrUpdate(RecStatusStat recStatusStat) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(recStatusStat);
	}

	@Override
	public void delete(RecStatusStat recStatusStat) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RecStatusStat.class, recStatusStat.getId()));
	}

	@Override
	public void delete(List<RecStatusStat> recStatusStats) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RecStatusStat recStatusStat : recStatusStats) {
            session.delete(session.load(RecStatusStat.class, recStatusStat.getId()));
        }
	}

	@Override
	public int getCount() {
		return ((Long)sessionFactory.getCurrentSession().createCriteria(RecStatusStat.class)
				.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("site", Site.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), RecStatusStat.class, map);
	}

	@Override
	public DataSourceResult getListByDate(DataSourceRequest request,
			Date stateDate) {
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("site", Site.class);
		
		Date nextDate = Util.addDays(stateDate, 1);
		
		Criterion rest1 = Restrictions.ge("stateDate", stateDate);
		Criterion rest2 = Restrictions.lt("stateDate", nextDate);
		
		Criterion criterion = Restrictions.and(rest1, rest2);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), RecStatusStat.class, 
        		map, criterion);
	}

	@Override
	public DataSourceResult getListByTime(DataSourceRequest request,
			String stateTime) {
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("site", Site.class);
		
		Criterion criterion = Restrictions.eq("stateTime", stateTime);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), RecStatusStat.class, 
        		map, criterion);
	}

	@Override
	public RecStatusStat get(Site site, Date stateDate) {
		Session session = sessionFactory.getCurrentSession();
		
		if (site == null) {
			return null;
		}
		
		@SuppressWarnings("unchecked")
		List<RecStatusStat> list = session.createCriteria(RecStatusStat.class)
				.createAlias("site", "site")
				.add(Restrictions.eq("site.id", site.getId()))
				.add(Restrictions.eq("stateDate", stateDate)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getIdsBefore(Date stateDate, int maxCnt) {
		return (List<Integer>) sessionFactory.getCurrentSession()
				.createCriteria(RecStatusStat.class)
				.add(Restrictions.lt("stateDate", stateDate))
				.setProjection(Projections.property("id"))
				.setMaxResults(maxCnt)
				.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RecStatusStat> getListByTime(Site site, String stateTime) {
		Session session = sessionFactory.getCurrentSession();
		
		if (site == null) {
			return new ArrayList<RecStatusStat>();
		}
		
		return session.createCriteria(RecStatusStat.class)
				.createAlias("site", "site")
				.add(Restrictions.eq("site.id", site.getId()))
				.add(Restrictions.eq("stateTime", stateTime))
				.addOrder(Order.asc("stateDate")).list();
	}
}
