package kr.co.r2cast.models.fnd.dao;

import java.util.HashMap;
import java.util.List;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.SiteUser;
import kr.co.r2cast.models.fnd.User;

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
public class SiteUserDaoImpl implements SiteUserDao {
    @Autowired
    private SessionFactory sessionFactory;
    
	@Override
	public SiteUser get(int id) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<SiteUser> list = session.createCriteria(SiteUser.class)
				.add(Restrictions.eq("id", id)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SiteUser> getList() {
		return sessionFactory.getCurrentSession().createCriteria(SiteUser.class).list();
	}

	@Override
	public void saveOrUpdate(SiteUser siteUser) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(siteUser);
	}

	@Override
	public void delete(SiteUser siteUser) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(SiteUser.class, siteUser.getId()));
	}

	@Override
	public void delete(List<SiteUser> siteUsers) {
		Session session = sessionFactory.getCurrentSession();
		
        for (SiteUser siteUser : siteUsers) {
            session.delete(session.load(SiteUser.class, siteUser.getId()));
        }
	}

	@Override
	public int getCount() {
		return ((Long)sessionFactory.getCurrentSession().createCriteria(SiteUser.class)
				.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("site", Site.class);
		map.put("user", User.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), SiteUser.class, map);
	}

	@Override
	public boolean isRegistered(int siteId, int userId) {
		Session session = sessionFactory.getCurrentSession();
		
		Criterion rest1 = Restrictions.eq("user.id", userId);
		Criterion rest2 = Restrictions.eq("site.id", siteId);
		
		@SuppressWarnings("unchecked")
		List<SiteUser> list = session.createCriteria(SiteUser.class)
				.createAlias("user", "user")
				.createAlias("site", "site")
				.add(Restrictions.and(rest1, rest2)).list();
		
		return !list.isEmpty();
	}

	@Override
	public void saveOrUpdate(List<SiteUser> siteUsers) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(siteUsers);
	}
}
