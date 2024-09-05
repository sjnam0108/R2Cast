package kr.co.r2cast.models.eco.dao;

import java.util.List;

import kr.co.r2cast.models.eco.SiteOpt;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
public class SiteOptDaoImpl implements SiteOptDao {
    @Autowired
    private SessionFactory sessionFactory;

	public SiteOpt get(int id) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<SiteOpt> list = session.createCriteria(SiteOpt.class)
				.add(Restrictions.eq("id", id)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SiteOpt> getList() {
		return sessionFactory.getCurrentSession().createCriteria(SiteOpt.class).list();
	}

	@Override
	public void saveOrUpdate(SiteOpt siteOpt) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(siteOpt);
	}

	@Override
	public void delete(SiteOpt siteOpt) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(SiteOpt.class, siteOpt.getId()));
	}

	@Override
	public void delete(List<SiteOpt> siteOpts) {
		Session session = sessionFactory.getCurrentSession();
		
        for (SiteOpt siteOpt : siteOpts) {
            session.delete(session.load(SiteOpt.class, siteOpt.getId()));
        }
	}

	@Override
	public int getCount() {
		return ((Long)sessionFactory.getCurrentSession().createCriteria(SiteOpt.class)
				.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	@Override
	public SiteOpt getByOptNameSiteId(String optName, int siteId) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<SiteOpt> list = session.createCriteria(SiteOpt.class)
				.createAlias("site", "site")
				.add(Restrictions.eq("site.id", siteId))
				.add(Restrictions.eq("optName", optName)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SiteOpt> getListBySiteId(int siteId) {
		Session session = sessionFactory.getCurrentSession();
		
		return session.createCriteria(SiteOpt.class)
				.createAlias("site", "site")
				.add(Restrictions.eq("site.id", siteId)).list();
	}

}
