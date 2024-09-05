package kr.co.r2cast.models.eco.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.eco.MonTask;
import kr.co.r2cast.models.eco.Rvm;
import kr.co.r2cast.models.fnd.Site;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
public class MonTaskDaoImpl implements MonTaskDao {
    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public MonTask get(int id) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<MonTask> list = session.createCriteria(MonTask.class)
				.add(Restrictions.eq("id", id)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MonTask> getList() {
		return sessionFactory.getCurrentSession().createCriteria(MonTask.class).list();
	}

	@Override
	public void saveOrUpdate(MonTask monTask) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(monTask);
	}

	@Override
	public void delete(MonTask monTask) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(MonTask.class, monTask.getId()));
	}

	@Override
	public void delete(List<MonTask> monTasks) {
		Session session = sessionFactory.getCurrentSession();
		
        for (MonTask monTask : monTasks) {
            session.delete(session.load(MonTask.class, monTask.getId()));
        }
	}

	@Override
	public int getCount() {
		return ((Long)sessionFactory.getCurrentSession().createCriteria(MonTask.class)
				.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("site", Site.class);
		map.put("rvm", Rvm.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), MonTask.class, map);
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request, 
			List<Integer> rvmIds) {
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("site", Site.class);
		map.put("rvm", Rvm.class);
		return request.toDataSourceResult(sessionFactory.getCurrentSession(), MonTask.class, 
        		map, "rvm.id", rvmIds, 0);
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request, 
			List<Integer> rvmIds, int lastMins) {
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("site", Site.class);
		map.put("rvm", Rvm.class);
		
		return request.toDataSourceResult(sessionFactory.getCurrentSession(), MonTask.class, 
        		map, "rvm.id", rvmIds, lastMins);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MonTask> getListByRvmId(int rvmId) {
		Session session = sessionFactory.getCurrentSession();
		
		return session.createCriteria(MonTask.class)
				.createAlias("rvm", "rvm")
				.add(Restrictions.eq("rvm.id", rvmId)).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MonTask> getListByRtnSchdTaskRvmId(int rtnSchdTaskRvmId) {
		Session session = sessionFactory.getCurrentSession();
		
		return session.createCriteria(MonTask.class)
				.createAlias("rtnSchdTaskRvm", "rtnSchdTaskRvm")
				.add(Restrictions.eq("rtnSchdTaskRvm.id", rtnSchdTaskRvmId)).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> getOldIdsBefore(Date date, int maxCnt) {
		Date time = new Date();

		Criterion rest1 = Restrictions.ne("status", "1");
		Criterion rest2 = Restrictions.eq("status", "1");
		Criterion rest3 = Restrictions.lt("cancelDate", time);
		
		return (List<Integer>) sessionFactory.getCurrentSession()
				.createCriteria(MonTask.class)
				.add(Restrictions.lt("destDate", date))
				.add(Restrictions.or(rest1, Restrictions.and(rest2, rest3)))
				.setProjection(Projections.property("id"))
				.setMaxResults(maxCnt)
				.list();
	}
}
