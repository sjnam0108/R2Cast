package kr.co.r2cast.models.fnd.dao;

import java.util.HashMap;
import java.util.List;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.SiteSite;

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
public class SiteSiteDaoImpl implements SiteSiteDao {
    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public SiteSite get(int id) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<SiteSite> list = session.createCriteria(SiteSite.class)
				.add(Restrictions.eq("id", id)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SiteSite> getList() {
		return sessionFactory.getCurrentSession().createCriteria(SiteSite.class).list();
	}

	@Override
	public void saveOrUpdate(SiteSite siteSite) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(siteSite);
	}

	@Override
	public void delete(SiteSite siteSite) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(SiteSite.class, siteSite.getId()));
	}

	@Override
	public void delete(List<SiteSite> siteSites) {
		Session session = sessionFactory.getCurrentSession();
		
        for (SiteSite siteSite : siteSites) {
            session.delete(session.load(SiteSite.class, siteSite.getId()));
        }
	}

	@Override
	public int getCount() {
		return ((Long)sessionFactory.getCurrentSession().createCriteria(SiteSite.class)
				.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("parentSite", Site.class);
		map.put("childSite", Site.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), SiteSite.class, map);
	}

	@Override
	public boolean isRegistered(int parentSiteId, int childSiteId) {
		Session session = sessionFactory.getCurrentSession();
		
		Criterion rest1 = Restrictions.eq("parentSite.id", parentSiteId);
		Criterion rest2 = Restrictions.eq("childSite.id", childSiteId);
		
		@SuppressWarnings("unchecked")
		List<SiteSite> list = session.createCriteria(SiteSite.class)
				.createAlias("parentSite", "parentSite")
				.createAlias("childSite", "childSite")
				.add(Restrictions.and(rest1, rest2)).list();
		
		return !list.isEmpty();
	}

	@Override
	public void saveOrUpdate(List<SiteSite> siteSites) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(siteSites);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SiteSite> getListByParentSiteId(int siteId) {
		Session session = sessionFactory.getCurrentSession();
		
		return session.createCriteria(SiteSite.class)
				.createAlias("parentSite", "parentSite")
				.createAlias("childSite", "childSite")
				.add(Restrictions.eq("parentSite.id", siteId)).list();
	}
}
