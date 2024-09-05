package kr.co.r2cast.models.fnd.dao;

import java.util.List;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.fnd.Privilege;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
public class PrivilegeDaoImpl implements PrivilegeDao {
    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public Privilege get(int id) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<Privilege> list = session.createCriteria(Privilege.class)
				.add(Restrictions.eq("id", id)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Privilege> getList() {
		return sessionFactory.getCurrentSession().createCriteria(Privilege.class).list();
	}

	@Override
	public void saveOrUpdate(Privilege privilege) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(privilege);
	}

	@Override
	public void delete(Privilege privilege) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(Privilege.class, privilege.getId()));
	}

	@Override
	public void delete(List<Privilege> privileges) {
		Session session = sessionFactory.getCurrentSession();
		
        for (Privilege privilege : privileges) {
            session.delete(session.load(Privilege.class, privilege.getId()));
        }
	}

	@Override
	public int getCount() {
		return ((Long)sessionFactory.getCurrentSession().createCriteria(Privilege.class)
				.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), Privilege.class);
	}

	@Override
	public Privilege get(String ukid) {
		return get(null, ukid);
	}

	@Override
	public Privilege get(org.hibernate.Session hnSession, String ukid) {
		Session session = hnSession == null ? sessionFactory.getCurrentSession()
				: hnSession;
		
		@SuppressWarnings("unchecked")
		List<Privilege> list = session.createCriteria(Privilege.class)
				.add(Restrictions.eq("ukid", ukid)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}
}
