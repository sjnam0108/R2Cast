package kr.co.r2cast.models.fnd.dao;

import java.util.List;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.fnd.Role;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
public class RoleDaoImpl implements RoleDao {
    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public Role get(int id) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<Role> list = session.createCriteria(Role.class)
				.add(Restrictions.eq("id", id)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Role> getList() {
		return sessionFactory.getCurrentSession().createCriteria(Role.class).list();
	}

	@Override
	public void saveOrUpdate(Role role) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(role);
	}

	@Override
	public void delete(Role role) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(Role.class, role.getId()));
	}

	@Override
	public void delete(List<Role> roles) {
		Session session = sessionFactory.getCurrentSession();
		
        for (Role role : roles) {
            session.delete(session.load(Role.class, role.getId()));
        }
	}

	@Override
	public int getCount() {
		return ((Long)sessionFactory.getCurrentSession().createCriteria(Role.class)
				.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), Role.class);
	}

	@Override
	public Role get(String ukid) {
		return get(null, ukid);
	}

	@Override
	public Role get(org.hibernate.Session hnSession, String ukid) {
		Session session = hnSession == null ? sessionFactory.getCurrentSession()
				: hnSession;
		
		@SuppressWarnings("unchecked")
		List<Role> list = session.createCriteria(Role.class)
				.add(Restrictions.eq("ukid", ukid)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}
}
