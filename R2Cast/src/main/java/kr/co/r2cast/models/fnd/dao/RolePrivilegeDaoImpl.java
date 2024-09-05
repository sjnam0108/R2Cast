package kr.co.r2cast.models.fnd.dao;

import java.util.HashMap;
import java.util.List;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.fnd.Privilege;
import kr.co.r2cast.models.fnd.Role;
import kr.co.r2cast.models.fnd.RolePrivilege;

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
public class RolePrivilegeDaoImpl implements RolePrivilegeDao {
    @Autowired
    private SessionFactory sessionFactory;
    
	@Override
	public RolePrivilege get(int id) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<RolePrivilege> list = session.createCriteria(RolePrivilege.class)
				.add(Restrictions.eq("id", id)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RolePrivilege> getList() {
		return sessionFactory.getCurrentSession().createCriteria(RolePrivilege.class).list();
	}

	@Override
	public void saveOrUpdate(RolePrivilege rolePrivilege) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(rolePrivilege);
	}

	@Override
	public void delete(RolePrivilege rolePrivilege) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RolePrivilege.class, rolePrivilege.getId()));
	}

	@Override
	public void delete(List<RolePrivilege> rolePrivileges) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RolePrivilege rolePrivilege : rolePrivileges) {
            session.delete(session.load(RolePrivilege.class, rolePrivilege.getId()));
        }
	}

	@Override
	public int getCount() {
		return ((Long)sessionFactory.getCurrentSession().createCriteria(RolePrivilege.class)
				.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("role", Role.class);
		map.put("privilege", Privilege.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), RolePrivilege.class, map);
	}

	@Override
	public boolean isRegistered(int roleId, int privilegeId) {
		Session session = sessionFactory.getCurrentSession();
		
		Criterion rest1 = Restrictions.eq("role.id", roleId);
		Criterion rest2 = Restrictions.eq("privilege.id", privilegeId);
		
		@SuppressWarnings("unchecked")
		List<RolePrivilege> list = session.createCriteria(RolePrivilege.class)
				.createAlias("role", "role")
				.createAlias("privilege", "privilege")
				.add(Restrictions.and(rest1, rest2)).list();
		
		return !list.isEmpty();
	}

	@Override
	public void saveOrUpdate(List<RolePrivilege> rolePrivileges) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(rolePrivileges);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RolePrivilege> getListByRoleId(int roleId) {
		Session session = sessionFactory.getCurrentSession();
		
		return session.createCriteria(RolePrivilege.class)
				.createAlias("role", "role")
				.createAlias("privilege", "privilege")
				.add(Restrictions.eq("role.id", roleId)).list();
	}
}
