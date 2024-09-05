package kr.co.r2cast.models.fnd.dao;

import java.util.Date;
import java.util.List;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
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
public class SiteDaoImpl implements SiteDao {
    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public Site get(int id) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<Site> list = session.createCriteria(Site.class)
				.add(Restrictions.eq("id", id)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Site> getList() {
		return sessionFactory.getCurrentSession().createCriteria(Site.class).list();
	}

	@Override
	public void saveOrUpdate(Site site) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(site);
	}

	@Override
	public void delete(Site site) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(Site.class, site.getId()));
	}

	@Override
	public void delete(List<Site> sites) {
		Session session = sessionFactory.getCurrentSession();
		
        for (Site site : sites) {
            session.delete(session.load(Site.class, site.getId()));
        }
	}

	@Override
	public int getCount() {
		return ((Long)sessionFactory.getCurrentSession().createCriteria(Site.class)
				.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), Site.class);
	}

	@Override
	public Site get(String shortName) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<Site> list = session.createCriteria(Site.class)
				.add(Restrictions.eq("shortName", shortName)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<Site> getEffectiveList() {
		return getEffectiveList(new Date());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Site> getEffectiveList(Date time) {
		Session session = sessionFactory.getCurrentSession();
		
		Criterion rest1 = Restrictions.lt("effectiveStartDate", time);
		Criterion rest2 = Restrictions.isNull("effectiveEndDate");
		Criterion rest3 = Restrictions.gt("effectiveEndDate", time);
		
		return session.createCriteria(Site.class)
				.add(Restrictions.and(rest1, Restrictions.or(rest2, rest3))).list();
	}
}
