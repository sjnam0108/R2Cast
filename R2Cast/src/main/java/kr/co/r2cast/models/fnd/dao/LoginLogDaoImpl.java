package kr.co.r2cast.models.fnd.dao;

import java.util.HashMap;
import java.util.List;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.fnd.LoginLog;
import kr.co.r2cast.models.fnd.User;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Component
public class LoginLogDaoImpl implements LoginLogDao {
    @Autowired
    private SessionFactory sessionFactory;
    
	@Override
	public LoginLog get(int id) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<LoginLog> list = session.createCriteria(LoginLog.class)
				.add(Restrictions.eq("id", id)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LoginLog> getList() {
		return sessionFactory.getCurrentSession().createCriteria(LoginLog.class).list();
	}

	@Override
	public void saveOrUpdate(LoginLog loginLog) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(loginLog);
	}

	@Override
	public void delete(LoginLog loginLog) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(LoginLog.class, loginLog.getId()));
	}

	@Override
	public void delete(List<LoginLog> loginLogs) {
		Session session = sessionFactory.getCurrentSession();
		
        for (LoginLog loginLog : loginLogs) {
            session.delete(session.load(LoginLog.class, loginLog.getId()));
        }
	}

	@Override
	public int getCount() {
		return ((Long)sessionFactory.getCurrentSession().createCriteria(LoginLog.class)
				.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("user", User.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), LoginLog.class, map);
	}

	@Override
	public LoginLog getLastByUserId(int userId) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<LoginLog> list = session.createCriteria(LoginLog.class)
				.createAlias("user", "user")
				.add(Restrictions.eq("user.id", userId))
				.addOrder(Order.desc("id"))
				.list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

}
