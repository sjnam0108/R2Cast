package kr.co.r2cast.models.fnd.dao;

import java.util.HashMap;
import java.util.List;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.fnd.Privilege;
import kr.co.r2cast.models.fnd.User;
import kr.co.r2cast.models.fnd.UserPrivilege;

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
public class UserPrivilegeDaoImpl implements UserPrivilegeDao {
    @Autowired
    private SessionFactory sessionFactory;
    
	@Override
	public UserPrivilege get(int id) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<UserPrivilege> list = session.createCriteria(UserPrivilege.class)
				.add(Restrictions.eq("id", id)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserPrivilege> getList() {
		return sessionFactory.getCurrentSession().createCriteria(UserPrivilege.class).list();
	}

	@Override
	public void saveOrUpdate(UserPrivilege userPrivilege) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(userPrivilege);
	}

	@Override
	public void delete(UserPrivilege userPrivilege) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(UserPrivilege.class, userPrivilege.getId()));
	}

	@Override
	public void delete(List<UserPrivilege> userPrivileges) {
		Session session = sessionFactory.getCurrentSession();
		
        for (UserPrivilege userPrivilege : userPrivileges) {
            session.delete(session.load(UserPrivilege.class, userPrivilege.getId()));
        }
	}

	@Override
	public int getCount() {
		return ((Long)sessionFactory.getCurrentSession().createCriteria(UserPrivilege.class)
				.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("user", User.class);
		map.put("privilege", Privilege.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), UserPrivilege.class, map);
	}

	@Override
	public boolean isRegistered(int userId, int privilegeId) {
		Session session = sessionFactory.getCurrentSession();
		
		Criterion rest1 = Restrictions.eq("user.id", userId);
		Criterion rest2 = Restrictions.eq("privilege.id", privilegeId);
		
		@SuppressWarnings("unchecked")
		List<UserPrivilege> list = session.createCriteria(UserPrivilege.class)
				.createAlias("user", "user")
				.createAlias("privilege", "privilege")
				.add(Restrictions.and(rest1, rest2)).list();
		
		return !list.isEmpty();
	}

	@Override
	public void saveOrUpdate(List<UserPrivilege> userPrivileges) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(userPrivileges);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UserPrivilege> getListByUserId(int userId) {
		Session session = sessionFactory.getCurrentSession();
		
		return session.createCriteria(UserPrivilege.class)
				.createAlias("user", "user")
				.createAlias("privilege", "privilege")
				.add(Restrictions.eq("user.id", userId)).list();
	}
}
