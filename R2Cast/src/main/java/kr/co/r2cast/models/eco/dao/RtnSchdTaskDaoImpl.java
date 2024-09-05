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
import kr.co.r2cast.models.eco.RtnSchdTask;
import kr.co.r2cast.models.fnd.Site;

@Transactional
@Component
public class RtnSchdTaskDaoImpl implements RtnSchdTaskDao {
    @Autowired
    private SessionFactory sessionFactory;

	public RtnSchdTask get(int id) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<RtnSchdTask> list = session.createCriteria(RtnSchdTask.class)
				.add(Restrictions.eq("id", id)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(RtnSchdTask rtnSchdTask) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(rtnSchdTask);
	}

	@Override
	public void delete(RtnSchdTask rtnSchdTask) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RtnSchdTask.class, rtnSchdTask.getId()));
	}

	@Override
	public void delete(List<RtnSchdTask> rtnSchdTasks) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RtnSchdTask rtnSchdTask : rtnSchdTasks) {
            session.delete(session.load(RtnSchdTask.class, rtnSchdTask.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("site", Site.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), RtnSchdTask.class, map);
	}

	@Override
	public RtnSchdTask get(Site site, String taskName) {
		Session session = sessionFactory.getCurrentSession();
		
		if (site == null) {
			return null;
		}
		
		@SuppressWarnings("unchecked")
		List<RtnSchdTask> list = session.createCriteria(RtnSchdTask.class)
				.createAlias("site", "site")
				.add(Restrictions.eq("site.id", site.getId()))
				.add(Restrictions.eq("taskName", taskName)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RtnSchdTask> getListBySiteId(int siteId) {
		Session session = sessionFactory.getCurrentSession();
		
		return session.createCriteria(RtnSchdTask.class)
				.createAlias("site", "site")
				.add(Restrictions.eq("site.id", siteId)).list();
	}

}
