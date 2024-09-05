package kr.co.r2cast.models.eco.dao;

import java.util.Date;
import java.util.HashMap;
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
import kr.co.r2cast.models.eco.Rvm;
import kr.co.r2cast.models.eco.RvmTrx;
import kr.co.r2cast.models.eco.RvmTrxItem;

@Transactional
@Component
public class RvmTrxItemDaoImpl implements RvmTrxItemDao {
	
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public RvmTrxItem get(int id) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<RvmTrxItem> list = session.createCriteria(RvmTrxItem.class)
				.add(Restrictions.eq("id", id)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(RvmTrxItem trxItem) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(trxItem);
	}

	@Override
	public void delete(RvmTrxItem trxItem) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RvmTrxItem.class, trxItem.getId()));
	}

	@Override
	public void delete(List<RvmTrxItem> trxItems) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RvmTrxItem rvmTrxItem : trxItems) {
            session.delete(session.load(RvmTrxItem.class, rvmTrxItem.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("rvm", Rvm.class);
		map.put("rvmTrx", RvmTrx.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), RvmTrxItem.class, map);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RvmTrxItem> getListByTrxId(int siteId, int trxId) {
		Session session = sessionFactory.getCurrentSession();
		
		return session.createCriteria(RvmTrxItem.class)
				.createAlias("site", "site")
				.createAlias("rvmTrx", "rvmTrx")
				.add(Restrictions.eq("site.id", siteId))
				.add(Restrictions.eq("rvmTrx.id", trxId)).list();
	}

	@Override
	public int getCountByTrxId(int trxId) {
		return ((Long)sessionFactory.getCurrentSession().createCriteria(RvmTrxItem.class)
				.createAlias("rvmTrx", "rvmTrx")
				.add(Restrictions.eq("rvmTrx.id", trxId))
				.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getDailyListBySiteIdOpDates(int siteId, Date date1, Date date2) {
		Session session = sessionFactory.getCurrentSession();
		
		return session.createCriteria(RvmTrxItem.class)
				.createAlias("site", "site")
				.setProjection(Projections.projectionList()
						.add(Projections.groupProperty("opDate"))
						.add(Projections.sum("count"))
						.add(Projections.countDistinct("groupId"))
						)
				.add(Restrictions.eq("site.id", siteId))
				.add(Restrictions.between("opDate", date1, date2)).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getDailyListBySiteIdOpDate(int siteId, Date date) {
		Session session = sessionFactory.getCurrentSession();
		
		return session.createCriteria(RvmTrxItem.class)
				.createAlias("site", "site")
				.createAlias("rvm", "rvm")
				.setProjection(Projections.projectionList()
						.add(Projections.groupProperty("rvm.id"))
						.add(Projections.sum("count"))
						.add(Projections.count("id"))
						)
				.add(Restrictions.eq("site.id", siteId))
				.add(Restrictions.eq("opDate", date)).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getDailyListByRvmIdOpDates(int rvmId, Date date1, Date date2) {
		Session session = sessionFactory.getCurrentSession();
		
		return session.createCriteria(RvmTrxItem.class)
				.createAlias("rvm", "rvm")
				.setProjection(Projections.projectionList()
						.add(Projections.groupProperty("opDate"))
						.add(Projections.sum("count"))
						)
				.add(Restrictions.eq("rvm.id", rvmId))
				.add(Restrictions.between("opDate", date1, date2)).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getSumDataByRvmId(int rvmId) {
		Session session = sessionFactory.getCurrentSession();
		
		List<Object[]> list = session.createCriteria(RvmTrxItem.class)
				.createAlias("rvm", "rvm")
				.setProjection(Projections.projectionList()
						.add(Projections.sum("count"))
						.add(Projections.countDistinct("groupId"))
						)
				.add(Restrictions.eq("rvm.id", rvmId)).list();
		
		if (list == null) {
			return null;
		} else {
			return list.get(0);
		}
	}

}
