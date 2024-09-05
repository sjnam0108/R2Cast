package kr.co.r2cast.models.eco.dao;

import java.util.List;

import kr.co.r2cast.models.eco.DispMenu;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
public class DispMenuDaoImpl implements DispMenuDao {
    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public DispMenu get(int id) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<DispMenu> list = session.createCriteria(DispMenu.class)
				.add(Restrictions.eq("id", id)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DispMenu> getList() {
		return sessionFactory.getCurrentSession().createCriteria(DispMenu.class).list();
	}

	@Override
	public void saveOrUpdate(DispMenu dispMenu) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(dispMenu);
	}

	@Override
	public void delete(DispMenu dispMenu) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(DispMenu.class, dispMenu.getId()));
	}

	@Override
	public void delete(List<DispMenu> dispMenus) {
		Session session = sessionFactory.getCurrentSession();
		
        for (DispMenu dispMenu : dispMenus) {
            session.delete(session.load(DispMenu.class, dispMenu.getId()));
        }
	}

	@Override
	public DispMenu getBySiteId(int siteId) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<DispMenu> list = session.createCriteria(DispMenu.class)
				.createAlias("site", "site")
				.add(Restrictions.eq("site.id", siteId)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}
}
