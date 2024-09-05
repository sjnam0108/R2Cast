package kr.co.r2cast.models.eco.dao;

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
import kr.co.r2cast.models.eco.UpdSetupFile;

@Transactional
@Component
public class UpdSetupFileDaoImpl implements UpdSetupFileDao {
    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public UpdSetupFile get(int id) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<UpdSetupFile> list = session.createCriteria(UpdSetupFile.class)
				.add(Restrictions.eq("id", id)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UpdSetupFile> getList() {
		return sessionFactory.getCurrentSession().createCriteria(UpdSetupFile.class).list();
	}

	@Override
	public void saveOrUpdate(UpdSetupFile updSetupFile) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(updSetupFile);
	}

	@Override
	public void delete(UpdSetupFile updSetupFile) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(UpdSetupFile.class, updSetupFile.getId()));
	}

	@Override
	public void delete(List<UpdSetupFile> updSetupFiles) {
		Session session = sessionFactory.getCurrentSession();
		
        for (UpdSetupFile updSetupFile : updSetupFiles) {
            session.delete(session.load(UpdSetupFile.class, updSetupFile.getId()));
        }
	}

	@Override
	public int getCount() {
		return ((Long)sessionFactory.getCurrentSession().createCriteria(UpdSetupFile.class)
				.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), UpdSetupFile.class);
	}

	@Override
	public UpdSetupFile get(String filename) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<UpdSetupFile> list = session.createCriteria(UpdSetupFile.class)
				.add(Restrictions.eq("filename", filename)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<UpdSetupFile> getListByPublished(String published) {
		Session session = sessionFactory.getCurrentSession();
		
		return session.createCriteria(UpdSetupFile.class)
				.add(Restrictions.eq("published", published)).list();
	}
}