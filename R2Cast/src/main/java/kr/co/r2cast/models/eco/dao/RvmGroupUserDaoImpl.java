package kr.co.r2cast.models.eco.dao;

import java.util.HashMap;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.eco.RvmGroup;
import kr.co.r2cast.models.eco.RvmGroupUser;
import kr.co.r2cast.models.fnd.RolePrivilege;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.User;

@Transactional
@Component
public class RvmGroupUserDaoImpl implements RvmGroupUserDao {
    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public RvmGroupUser get(int id) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<RvmGroupUser> list = session.createCriteria(RvmGroupUser.class)
				.add(Restrictions.eq("id", id)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(RvmGroupUser rvmGroupUser) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(rvmGroupUser);
	}

	@Override
	public void delete(RvmGroupUser rvmGroupUser) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RvmGroupUser.class, rvmGroupUser.getId()));
	}

	@Override
	public void delete(List<RvmGroupUser> rvmGroupUsers) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RvmGroupUser rvmGroupUser : rvmGroupUsers) {
            session.delete(session.load(RvmGroupUser.class, rvmGroupUser.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("rvmGroup", RvmGroup.class);
		map.put("user", User.class);
		map.put("site", Site.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), RvmGroupUser.class, map);
	}

	@Override
	public boolean isRegistered(int siteId, int rvmGroupId, int userId) {
		Session session = sessionFactory.getCurrentSession();
		
		Criterion rest1 = Restrictions.eq("rvmGroup.id", rvmGroupId);
		Criterion rest2 = Restrictions.eq("user.id", userId);
		Criterion rest3 = Restrictions.eq("site.id", siteId);
		
		@SuppressWarnings("unchecked")
		List<RolePrivilege> list = session.createCriteria(RvmGroupUser.class)
				.createAlias("rvmGroup", "rvmGroup")
				.createAlias("user", "user")
				.createAlias("site", "site")
				.add(Restrictions.and(Restrictions.and(rest1, rest2), rest3)).list();
		
		return !list.isEmpty();
	}

	@Override
	public void saveOrUpdate(List<RvmGroupUser> rvmGroupUsers) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(rvmGroupUsers);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RvmGroupUser> getListBySiteIdUserId(int siteId, int userId) {
		Session session = sessionFactory.getCurrentSession();
		
		return session.createCriteria(RvmGroupUser.class)
				.createAlias("rvmGroup", "rvmGroup")
				.createAlias("user", "user")
				.createAlias("site", "site")
				.add(Restrictions.eq("site.id", siteId))
				.add(Restrictions.eq("user.id", userId)).list();
	}
}
