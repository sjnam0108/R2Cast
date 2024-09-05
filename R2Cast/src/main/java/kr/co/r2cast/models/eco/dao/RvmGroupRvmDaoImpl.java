 package kr.co.r2cast.models.eco.dao;

import java.util.Date;
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
import kr.co.r2cast.models.eco.Rvm;
import kr.co.r2cast.models.eco.RvmGroup;
import kr.co.r2cast.models.eco.RvmGroupRvm;
import kr.co.r2cast.models.fnd.RolePrivilege;
import kr.co.r2cast.models.fnd.Site;

@Transactional
@Component
public class RvmGroupRvmDaoImpl implements RvmGroupRvmDao {
    @Autowired
    private SessionFactory sessionFactory;
    
	@Override
	public RvmGroupRvm get(int id) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<RvmGroupRvm> list = session.createCriteria(RvmGroupRvm.class)
				.add(Restrictions.eq("id", id)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(RvmGroupRvm rvmGroupRvm) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(rvmGroupRvm);
	}

	@Override
	public void delete(RvmGroupRvm rvmGroupRvm) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RvmGroupRvm.class, rvmGroupRvm.getId()));
	}

	@Override
	public void delete(List<RvmGroupRvm> rvmGroupRvms) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RvmGroupRvm rvmGroupRvm : rvmGroupRvms) {
            session.delete(session.load(RvmGroupRvm.class, rvmGroupRvm.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("rvmGroup", RvmGroup.class);
		map.put("rvm", Rvm.class);
		map.put("site", Site.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), RvmGroupRvm.class, map);
	}

	@Override
	public boolean isRegistered(int siteId, int rvmGroupId, int rvmId) {
		Session session = sessionFactory.getCurrentSession();
		
		Criterion rest1 = Restrictions.eq("rvmGroup.id", rvmGroupId);
		Criterion rest2 = Restrictions.eq("rvm.id", rvmId);
		Criterion rest3 = Restrictions.eq("site.id", siteId);
		
		@SuppressWarnings("unchecked")
		List<RolePrivilege> list = session.createCriteria(RvmGroupRvm.class)
				.createAlias("rvmGroup", "rvmGroup")
				.createAlias("rvm", "rvm")
				.createAlias("site", "site")
				.add(Restrictions.and(Restrictions.and(rest1, rest2), rest3)).list();
		
		return !list.isEmpty();
	}

	@Override
	public void saveOrUpdate(List<RvmGroupRvm> rvmGroupRvms) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(rvmGroupRvms);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RvmGroupRvm> getListBySiteIdRvmGroupId(int siteId,
			int rvmGroupId, boolean isEffectiveMode) {
		Session session = sessionFactory.getCurrentSession();
		
		if (isEffectiveMode) {
			Date time = new Date();
			
			Criterion rest1 = Restrictions.lt("rvm.effectiveStartDate", time);
			Criterion rest2 = Restrictions.isNull("rvm.effectiveEndDate");
			Criterion rest3 = Restrictions.gt("rvm.effectiveEndDate", time);
			
			return session.createCriteria(RvmGroupRvm.class)
					.createAlias("rvmGroup", "rvmGroup")
					.createAlias("rvm", "rvm")
					.createAlias("site", "site")
					.add(Restrictions.and(rest1, Restrictions.or(rest2, rest3)))
					.add(Restrictions.eq("site.id", siteId))
					.add(Restrictions.eq("rvmGroup.id", rvmGroupId)).list();
		} else {
			return session.createCriteria(RvmGroupRvm.class)
					.createAlias("rvmGroup", "rvmGroup")
					.createAlias("rvm", "rvm")
					.createAlias("site", "site")
					.add(Restrictions.eq("site.id", siteId))
					.add(Restrictions.eq("rvmGroup.id", rvmGroupId)).list();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RvmGroupRvm> getListBySiteIdRvmId(int siteId, int rvmId) {
		Session session = sessionFactory.getCurrentSession();
		
		return session.createCriteria(RvmGroupRvm.class)
				.createAlias("rvmGroup", "rvmGroup")
				.createAlias("rvm", "rvm")
				.createAlias("site", "site")
				.add(Restrictions.eq("site.id", siteId))
				.add(Restrictions.eq("rvm.id", rvmId)).list();
	}
}
