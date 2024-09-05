package kr.co.r2cast.models.fnd.dao;

import java.util.Date;
import java.util.List;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.fnd.User;

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
public class UserDaoImpl implements UserDao {
    @Autowired
    private SessionFactory sessionFactory;
    

	@Override
	public void saveOrUpdate(User user) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(user);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getList() {
		return sessionFactory.getCurrentSession().createCriteria(User.class).list();
	}

	@Override
	public User get(String username) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<User> list = session.createCriteria(User.class)
				.add(Restrictions.eq("username", username)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public User get(int id) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<User> list = session.createCriteria(User.class)
				.add(Restrictions.eq("id", id)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<User> getEffectiveList() {
		return getEffectiveList(new Date());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getEffectiveList(Date time) {
		Session session = sessionFactory.getCurrentSession();
		
		Criterion rest1 = Restrictions.lt("effectiveStartDate", time);
		Criterion rest2 = Restrictions.isNull("effectiveEndDate");
		Criterion rest3 = Restrictions.gt("effectiveEndDate", time);
		
		return session.createCriteria(User.class)
				.add(Restrictions.and(rest1, Restrictions.or(rest2, rest3))).list();
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), User.class);
	}

	@Override
	public void delete(User user) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(User.class, user.getId()));
	}

	@Override
	public void delete(List<User> users) {
		Session session = sessionFactory.getCurrentSession();
		
        for (User user : users) {
            session.delete(session.load(User.class, user.getId()));
        }
	}

	@Override
	public int getCount() {
		return ((Long)sessionFactory.getCurrentSession().createCriteria(User.class)
				.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}
}
