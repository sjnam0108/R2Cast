package kr.co.r2cast.models.eco.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.eco.MonEventReport;

@Transactional
@Component
public class MonEventReportDaoImpl implements MonEventReportDao {
    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public MonEventReport get(int id) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<MonEventReport> list = session.createCriteria(MonEventReport.class)
				.add(Restrictions.eq("id", id)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(MonEventReport monEventReport) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(monEventReport);
	}

	@Override
	public void delete(MonEventReport monEventReport) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(MonEventReport.class, monEventReport.getId()));
	}

	@Override
	public void delete(List<MonEventReport> monEventReports) {
		Session session = sessionFactory.getCurrentSession();
		
        for (MonEventReport monEventReport : monEventReports) {
            session.delete(session.load(MonEventReport.class, monEventReport.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), MonEventReport.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MonEventReport> getListBySiteId(int siteId) {
		Session session = sessionFactory.getCurrentSession();
		
		return session.createCriteria(MonEventReport.class)
				.createAlias("site", "site")
				.add(Restrictions.eq("site.id", siteId)).list();
	}

	@Override
	public int getCount(Date fromDt, String event, String equipType, int equipId) {
		return ((Long)sessionFactory.getCurrentSession().createCriteria(MonEventReport.class)
				.add(Restrictions.eq("event", event))
				.add(Restrictions.eq("equipType", equipType))
				.add(Restrictions.eq("equipId", equipId))
				.add(Restrictions.gt("whoCreationDate", fromDt))
				.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	@Override
	public MonEventReport getLast(Date fromDt, String event, String details,
			String equipType, int equipId) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<MonEventReport> list = session.createCriteria(MonEventReport.class)
				.add(Restrictions.eq("event", event))
				.add(Restrictions.eq("equipType", equipType))
				.add(Restrictions.eq("equipId", equipId))
				.add(Restrictions.like("details", "%" + details + "%"))
				.add(Restrictions.gt("whoCreationDate", fromDt)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MonEventReport> getListByRvmId(int rvmId) {
		Session session = sessionFactory.getCurrentSession();
		
		return session.createCriteria(MonEventReport.class)
				.add(Restrictions.eq("equipType", "P"))
				.add(Restrictions.eq("equipId", rvmId)).list();
	}
}