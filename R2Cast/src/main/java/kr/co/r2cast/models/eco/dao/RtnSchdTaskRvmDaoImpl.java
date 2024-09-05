package kr.co.r2cast.models.eco.dao;

import java.util.HashMap;
import java.util.List;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.eco.RtnSchdTask;
import kr.co.r2cast.models.eco.RtnSchdTaskRvm;
import kr.co.r2cast.models.eco.Rvm;
import kr.co.r2cast.models.eco.RvmGroupRvm;
import kr.co.r2cast.models.fnd.RolePrivilege;
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
public class RtnSchdTaskRvmDaoImpl implements RtnSchdTaskRvmDao {
    @Autowired
    private SessionFactory sessionFactory;
    
	@Override
	public RtnSchdTaskRvm get(int id) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<RtnSchdTaskRvm> list = session.createCriteria(RtnSchdTaskRvm.class)
				.add(Restrictions.eq("id", id)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RtnSchdTaskRvm> getList() {
		return sessionFactory.getCurrentSession().createCriteria(RtnSchdTaskRvm.class).list();
	}

	@Override
	public void saveOrUpdate(RtnSchdTaskRvm rtnSchdTaskRvm) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(rtnSchdTaskRvm);
	}

	@Override
	public void delete(RtnSchdTaskRvm rtnSchdTaskRvm) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RtnSchdTaskRvm.class, rtnSchdTaskRvm.getId()));
	}

	@Override
	public void delete(List<RtnSchdTaskRvm> rtnSchdTaskRvms) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RtnSchdTaskRvm rtnSchdTaskRvm : rtnSchdTaskRvms) {
            session.delete(session.load(RtnSchdTaskRvm.class, rtnSchdTaskRvm.getId()));
        }
	}

	@Override
	public int getCount() {
		return ((Long)sessionFactory.getCurrentSession().createCriteria(RtnSchdTaskRvm.class)
				.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("rtnSchdTask", RtnSchdTask.class);
		map.put("rvm", Rvm.class);
		map.put("site", Site.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), RtnSchdTaskRvm.class, map);
	}

	@Override
	public boolean isRegistered(int siteId, int rtnSchdTaskId, int rvmId) {
		Session session = sessionFactory.getCurrentSession();
		
		Criterion rest1 = Restrictions.eq("rtnSchdTask.id", rtnSchdTaskId);
		Criterion rest2 = Restrictions.eq("rvm.id", rvmId);
		Criterion rest3 = Restrictions.eq("site.id", siteId);
		
		@SuppressWarnings("unchecked")
		List<RolePrivilege> list = session.createCriteria(RvmGroupRvm.class)
				.createAlias("rtnSchdTask", "rtnSchdTask")
				.createAlias("rvm", "rvm")
				.createAlias("site", "site")
				.add(Restrictions.and(Restrictions.and(rest1, rest2), rest3)).list();
		
		return !list.isEmpty();
	}

	@Override
	public void saveOrUpdate(List<RtnSchdTaskRvm> rtnSchdTaskRvms) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(rtnSchdTaskRvms);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RtnSchdTaskRvm> getListBySiteIdRtnSchdTaskId(int siteId,
			int rtnSchdTaskId) {
		Session session = sessionFactory.getCurrentSession();
		
		return session.createCriteria(RtnSchdTaskRvm.class)
				.createAlias("rtnSchdTask", "rtnSchdTask")
				.createAlias("rvm", "rvm")
				.createAlias("site", "site")
				.add(Restrictions.eq("site.id", siteId))
				.add(Restrictions.eq("rtnSchdTask.id", rtnSchdTaskId)).list();
	}

	@Override
	public int getCountBySiteIdRtnSchdTaskId(int siteId, int rtnSchdTaskId) {
		Session session = sessionFactory.getCurrentSession();
		
		return ((Long)session.createCriteria(RtnSchdTaskRvm.class)
				.createAlias("rtnSchdTask", "rtnSchdTask")
				.createAlias("rvm", "rvm")
				.createAlias("site", "site")
				.add(Restrictions.eq("site.id", siteId))
				.add(Restrictions.eq("rtnSchdTask.id", rtnSchdTaskId))
				.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RtnSchdTaskRvm> getListByRvmId(int rvmId) {
		Session session = sessionFactory.getCurrentSession();
		
		return session.createCriteria(RtnSchdTaskRvm.class)
				.createAlias("rvm", "rvm")
				.add(Restrictions.eq("rvm.id", rvmId)).list();
	}
}
