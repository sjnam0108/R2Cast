package kr.co.r2cast.models.eco.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.eco.Rvm;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.utils.Util;

@Transactional
@Component
public class RvmDaoImpl implements RvmDao {
	
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public Rvm get(int id) {
		Session session = sessionFactory.getCurrentSession();

		@SuppressWarnings("unchecked")
		List<Rvm> list = session.createCriteria(Rvm.class).add(Restrictions.eq("id", id)).list();

		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(Rvm rvm) {
		Session session = sessionFactory.getCurrentSession();

		session.saveOrUpdate(rvm);
	}

	@Override
	public void delete(Rvm rvm) {
		Session session = sessionFactory.getCurrentSession();

		session.delete(session.load(Rvm.class, rvm.getId()));
	}

	@Override
	public void delete(List<Rvm> rvms) {
		Session session = sessionFactory.getCurrentSession();

		for (Rvm rvm : rvms) {
			session.delete(session.load(Rvm.class, rvm.getId()));
		}
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		return getList(request, false);
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request, boolean isEffectiveMode) {
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("site", Site.class);

		return request.toEffectiveDataSourceResult(sessionFactory.getCurrentSession(), Rvm.class, map, isEffectiveMode);
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request, List<Integer> rmvIds) {
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();

		return request.toDataSourceResult(sessionFactory.getCurrentSession(), Rvm.class, map, "id", rmvIds);
	}

	@Override
	public Rvm get(String deviceID) {
		Session session = sessionFactory.getCurrentSession();

		@SuppressWarnings("unchecked")
		List<Rvm> list = session.createCriteria(Rvm.class).add(Restrictions.eq("deviceID", deviceID)).list();

		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<Rvm> getEffectiveList() {
		return getEffectiveList(new Date());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Rvm> getEffectiveList(Date time) {
		Session session = sessionFactory.getCurrentSession();

		Criterion rest1 = Restrictions.lt("effectiveStartDate", time);
		Criterion rest2 = Restrictions.isNull("effectiveEndDate");
		Criterion rest3 = Restrictions.gt("effectiveEndDate", time);

		return session.createCriteria(Rvm.class).add(Restrictions.and(rest1, Restrictions.or(rest2, rest3))).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Rvm> getListBySiteId(int siteId, boolean isEffectiveMode) {
		Session session = sessionFactory.getCurrentSession();

		Criterion rest = Restrictions.eq("site.id", siteId);

		if (isEffectiveMode) {
			Date time = new Date();

			Criterion rest1 = Restrictions.lt("effectiveStartDate", time);
			Criterion rest2 = Restrictions.isNull("effectiveEndDate");
			Criterion rest3 = Restrictions.gt("effectiveEndDate", time);

			return session.createCriteria(Rvm.class).createAlias("site", "site")
					.add(Restrictions.and(rest1, Restrictions.or(rest2, rest3))).add(rest).list();
		} else {
			return session.createCriteria(Rvm.class).createAlias("site", "site").add(rest).list();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Rvm> getListBySiteIdRvmName(int siteId, boolean isEffectiveMode, String rvmName) {
		Session session = sessionFactory.getCurrentSession();

		Criterion rest = Restrictions.eq("site.id", siteId);

		if (isEffectiveMode) {
			Date time = new Date();

			Criterion rest1 = Restrictions.lt("effectiveStartDate", time);
			Criterion rest2 = Restrictions.isNull("effectiveEndDate");
			Criterion rest3 = Restrictions.gt("effectiveEndDate", time);

			if (Util.isValid(rvmName)) {
				return session.createCriteria(Rvm.class).createAlias("site", "site")
						.add(Restrictions.and(rest1, Restrictions.or(rest2, rest3)))
						.add(Restrictions.like("rvmName", rvmName, MatchMode.ANYWHERE)).add(rest).list();
			} else {
				return session.createCriteria(Rvm.class).createAlias("site", "site")
						.add(Restrictions.and(rest1, Restrictions.or(rest2, rest3))).add(rest).list();
			}
		} else {
			if (Util.isValid(rvmName)) {
				return session.createCriteria(Rvm.class).createAlias("site", "site")
						.add(Restrictions.like("rvmName", rvmName, MatchMode.ANYWHERE)).add(rest).list();
			} else {
				return session.createCriteria(Rvm.class).createAlias("site", "site").add(rest).list();
			}
		}
	}

	@Override
	public Rvm getByDeviceIDSiteShortName(String deviceID, String siteShortName) {
		Session session = sessionFactory.getCurrentSession();

		Date time = new Date();

		Criterion rest1 = Restrictions.lt("effectiveStartDate", time);
		Criterion rest2 = Restrictions.isNull("effectiveEndDate");
		Criterion rest3 = Restrictions.gt("effectiveEndDate", time);

		@SuppressWarnings("unchecked")
		List<Rvm> list = session.createCriteria(Rvm.class).createAlias("site", "site")
				.add(Restrictions.and(rest1, Restrictions.or(rest2, rest3))).add(Restrictions.eq("deviceID", deviceID))
				.add(Restrictions.eq("site.shortName", siteShortName)).list();

		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public Rvm getByDeviceIDRvmId(String deviceID, int rvmId) {
		Session session = sessionFactory.getCurrentSession();

		Date time = new Date();

		Criterion rest1 = Restrictions.lt("effectiveStartDate", time);
		Criterion rest2 = Restrictions.isNull("effectiveEndDate");
		Criterion rest3 = Restrictions.gt("effectiveEndDate", time);

		@SuppressWarnings("unchecked")
		List<Rvm> list = session.createCriteria(Rvm.class).add(Restrictions.and(rest1, Restrictions.or(rest2, rest3)))
				.add(Restrictions.eq("id", rvmId)).add(Restrictions.eq("deviceID", deviceID)).list();

		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public Rvm getByDeviceID(String deviceID) {
		Session session = sessionFactory.getCurrentSession();

		Date time = new Date();

		Criterion rest1 = Restrictions.lt("effectiveStartDate", time);
		Criterion rest2 = Restrictions.isNull("effectiveEndDate");
		Criterion rest3 = Restrictions.gt("effectiveEndDate", time);

		@SuppressWarnings("unchecked")
		List<Rvm> list = session.createCriteria(Rvm.class).add(Restrictions.and(rest1, Restrictions.or(rest2, rest3)))
				.add(Restrictions.eq("deviceID", deviceID)).list();

		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public Rvm get(Site site, String rvmName) {
		Session session = sessionFactory.getCurrentSession();

		if (site == null) {
			return null;
		}

		@SuppressWarnings("unchecked")
		List<Rvm> list = session.createCriteria(Rvm.class).createAlias("site", "site")
				.add(Restrictions.eq("site.id", site.getId())).add(Restrictions.eq("rvmName", rvmName)).list();

		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public Rvm get(String rvmName, List<Integer> siteIds) {
		Session session = sessionFactory.getCurrentSession();

		@SuppressWarnings("unchecked")
		List<Rvm> list = session.createCriteria(Rvm.class).createAlias("site", "site")
				.add(Restrictions.in("site.id", siteIds)).add(Restrictions.eq("rvmName", rvmName)).list();

		return (list.isEmpty() ? null : list.get(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Rvm> getUpdatedListAfter(Site site, Date date) {
		int siteId = -1;
		if (site != null) {
			siteId = site.getId();
		}

		return sessionFactory.getCurrentSession().createCriteria(Rvm.class).createAlias("site", "site")
				.add(Restrictions.eq("site.id", siteId)).add(Restrictions.ge("whoLastUpdateDate", date))
				.addOrder(Order.asc("rvmName")).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getValidModelCountListBySiteId(int siteId) {
		Date time = new Date();

		Criterion rest1 = Restrictions.lt("effectiveStartDate", time);
		Criterion rest2 = Restrictions.isNull("effectiveEndDate");
		Criterion rest3 = Restrictions.gt("effectiveEndDate", time);

		return sessionFactory.getCurrentSession().createCriteria(Rvm.class).createAlias("site", "site")
				.add(Restrictions.eq("site.id", siteId)).add(Restrictions.ne("serviceType", "R"))
				.add(Restrictions.and(rest1, Restrictions.or(rest2, rest3))).setProjection(Projections.projectionList()
						.add(Projections.groupProperty("model")).add(Projections.count("id")))
				.list();
	}
}
