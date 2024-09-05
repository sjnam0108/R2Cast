package kr.co.r2cast.models.eco.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.eco.Rvm;
import kr.co.r2cast.models.eco.RvmTrx;

@Transactional
@Component
public class RvmTrxDaoImpl implements RvmTrxDao {
	
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public RvmTrx get(int id) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<RvmTrx> list = session.createCriteria(RvmTrx.class)
				.add(Restrictions.eq("id", id)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(RvmTrx trx) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(trx);
	}

	@Override
	public void delete(RvmTrx trx) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RvmTrx.class, trx.getId()));
	}

	@Override
	public void delete(List<RvmTrx> trxs) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RvmTrx rvmTrx : trxs) {
            session.delete(session.load(RvmTrx.class, rvmTrx.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("rvm", Rvm.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), RvmTrx.class, map);
	}

	@Override
	public RvmTrx get(Rvm rvm, Date opDate) {
		Session session = sessionFactory.getCurrentSession();
		
		if (rvm == null || opDate == null) {
			return null;
		}
		
		@SuppressWarnings("unchecked")
		List<RvmTrx> list = session.createCriteria(RvmTrx.class)
				.createAlias("rvm", "rvm")
				.add(Restrictions.eq("rvm.id", rvm.getId()))
				.add(Restrictions.eq("opDt", opDate)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RvmTrx> getListBySiteId(int siteId) {
		Session session = sessionFactory.getCurrentSession();
		
		return session.createCriteria(RvmTrx.class)
				.createAlias("site", "site")
				.add(Restrictions.eq("site.id", siteId)).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RvmTrx> getListBySiteIdRvmId(int siteId, int rvmId) {
		Session session = sessionFactory.getCurrentSession();
		
		return session.createCriteria(RvmTrx.class)
				.createAlias("site", "site")
				.createAlias("rvm", "rvm")
				.add(Restrictions.eq("site.id", siteId))
				.add(Restrictions.eq("rvm.id", rvmId)).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RvmTrx> getListBySiteIdOpDate(int siteId, Date opDate) {
		Session session = sessionFactory.getCurrentSession();
		
		if (opDate == null) {
			return getListBySiteId(siteId);
		}
		
		return session.createCriteria(RvmTrx.class)
				.createAlias("site", "site")
				.add(Restrictions.eq("site.id", siteId))
				.add(Restrictions.eq("opDate", opDate)).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RvmTrx> getListBySiteIdOpDateRvmId(int siteId, Date opDate, int rvmId) {
		Session session = sessionFactory.getCurrentSession();
		
		if (opDate == null) {
			return getListBySiteIdRvmId(siteId, rvmId);
		}
		
		return session.createCriteria(RvmTrx.class)
				.createAlias("site", "site")
				.createAlias("rvm", "rvm")
				.add(Restrictions.eq("site.id", siteId))
				.add(Restrictions.eq("rvm.id", rvmId))
				.add(Restrictions.eq("opDate", opDate)).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getDailyListBySiteIdOpDates(int siteId, Date date1, Date date2) {
		Session session = sessionFactory.getCurrentSession();
		
		return session.createCriteria(RvmTrx.class)
				.createAlias("site", "site")
				.createAlias("rvm", "rvm")
				.setProjection(Projections.projectionList()
						.add(Projections.groupProperty("opDate"))
						.add(Projections.count("id"))
						.add(Projections.sum("overallAmount"))
						.add(Projections.countDistinct("rvm.id"))
						)
				.add(Restrictions.eq("site.id", siteId))
				.add(Restrictions.between("opDate", date1, date2)).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getDailyListBySiteIdOpDate(int siteId, Date date) {
		Session session = sessionFactory.getCurrentSession();
		
		return session.createCriteria(RvmTrx.class)
				.createAlias("site", "site")
				.createAlias("rvm", "rvm")
				.setProjection(Projections.projectionList()
						.add(Projections.groupProperty("rvm.id"))
						.add(Projections.count("id"))
						.add(Projections.sum("overallAmount"))
						.add(Projections.max("id"))
						)
				.add(Restrictions.eq("site.id", siteId))
				.add(Restrictions.eq("opDate", date)).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getDailyListByRvmIdOpDates(int rvmId, Date date1, Date date2) {
		Session session = sessionFactory.getCurrentSession();
		
		return session.createCriteria(RvmTrx.class)
				.createAlias("rvm", "rvm")
				.setProjection(Projections.projectionList()
						.add(Projections.groupProperty("opDate"))
						.add(Projections.count("id"))
						.add(Projections.sum("overallAmount"))
						.add(Projections.countDistinct("rvm.id"))
						)
				.add(Restrictions.eq("rvm.id", rvmId))
				.add(Restrictions.between("opDate", date1, date2)).list();
	}

	@Override
	public Date getMaxOpDateByRvmId(int rvmId) {
		Session session = sessionFactory.getCurrentSession();
		
		Object obj = session.createCriteria(RvmTrx.class)
				.createAlias("rvm", "rvm")
				.setProjection(Projections.projectionList()
						.add(Projections.max("opDt"))
						)
				.add(Restrictions.eq("rvm.id", rvmId)).list().get(0);
		
		if (obj == null) {
			return null;
		} else {
			return (Date) obj;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getSumDataByRvmId(int rvmId) {
		Session session = sessionFactory.getCurrentSession();
		
		List<Object[]> list = session.createCriteria(RvmTrx.class)
				.createAlias("rvm", "rvm")
				.setProjection(Projections.projectionList()
						.add(Projections.count("id"))
						.add(Projections.sum("overallAmount"))
						)
				.add(Restrictions.eq("rvm.id", rvmId)).list();
		
		if (list == null) {
			return null;
		} else {
			return list.get(0);
		}
	}

}
