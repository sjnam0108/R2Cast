package kr.co.r2cast.models.eco.dao;

import java.util.Date;
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
import kr.co.r2cast.models.eco.RecDailySummary;
import kr.co.r2cast.models.fnd.Site;

@Transactional
@Component
public class RecDailySummaryDaoImpl implements RecDailySummaryDao {
    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public RecDailySummary get(int id) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<RecDailySummary> list = session.createCriteria(RecDailySummary.class)
				.add(Restrictions.eq("id", id)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(RecDailySummary recDailySummary) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(recDailySummary);
	}

	@Override
	public void delete(RecDailySummary recDailySummary) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RecDailySummary.class, recDailySummary.getId()));
	}

	@Override
	public void delete(List<RecDailySummary> recDailySummaries) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RecDailySummary recDailySummary : recDailySummaries) {
            session.delete(session.load(RecDailySummary.class, recDailySummary.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("site", Site.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), RecDailySummary.class, map);
	}

	@Override
	public RecDailySummary get(Site site, Date workDate) {
		Session session = sessionFactory.getCurrentSession();
		
		if (site == null) {
			return null;
		}
		
		@SuppressWarnings("unchecked")
		List<RecDailySummary> list = session.createCriteria(RecDailySummary.class)
				.createAlias("site", "site")
				.add(Restrictions.eq("site.id", site.getId()))
				.add(Restrictions.eq("workDate", workDate)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}
}
