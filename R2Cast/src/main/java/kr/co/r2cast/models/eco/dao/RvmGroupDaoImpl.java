package kr.co.r2cast.models.eco.dao;

import java.util.HashMap;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.eco.RvmGroup;
import kr.co.r2cast.models.fnd.Site;

@Transactional
@Component
public class RvmGroupDaoImpl implements RvmGroupDao {
    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public RvmGroup get(int id) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<RvmGroup> list = session.createCriteria(RvmGroup.class)
				.add(Restrictions.eq("id", id)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RvmGroup> getList() {
		return sessionFactory.getCurrentSession().createCriteria(RvmGroup.class).list();
	}

	@Override
	public void saveOrUpdate(RvmGroup rvmGroup) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(rvmGroup);
	}

	@Override
	public void delete(RvmGroup rvmGroup) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RvmGroup.class, rvmGroup.getId()));
	}

	@Override
	public void delete(List<RvmGroup> rvmGroups) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RvmGroup rvmGroup : rvmGroups) {
            session.delete(session.load(RvmGroup.class, rvmGroup.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("site", Site.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), RvmGroup.class, map);
	}

	@Override
	public RvmGroup get(Site site, String rvmGroupName) {
		Session session = sessionFactory.getCurrentSession();
		
		if (site == null) {
			return null;
		}
		
		@SuppressWarnings("unchecked")
		List<RvmGroup> list = session.createCriteria(RvmGroup.class)
				.createAlias("site", "site")
				.add(Restrictions.eq("site.id", site.getId()))
				.add(Restrictions.eq("rvmGroupName", rvmGroupName)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RvmGroup> getListBySiteId(int siteId) {
		Session session = sessionFactory.getCurrentSession();
		
		return session.createCriteria(RvmGroup.class)
				.createAlias("site", "site")
				.add(Restrictions.eq("site.id", siteId)).list();
	}
}
