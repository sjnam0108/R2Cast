package kr.co.r2cast.models.fnd.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.fnd.Menu;
import kr.co.r2cast.models.fnd.Privilege;
import kr.co.r2cast.models.fnd.Role;
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
public class MenuDaoImpl implements MenuDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private PrivilegeDao privDao;
    
    @Autowired
    private RoleDao roleDao;
    
	@Override
	public Menu get(int id) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<Menu> list = session.createCriteria(Menu.class)
				.add(Restrictions.eq("id", id)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Menu> getList() {
		return sessionFactory.getCurrentSession().createCriteria(Menu.class).list();
	}

	@Override
	public void saveOrUpdate(Menu menu) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(menu);
	}

	@Override
	public void delete(Menu menu) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(Menu.class, menu.getId()));
		
		syncWithPrivAndRole(session, null);
	}

	@Override
	public void delete(List<Menu> menus) {
		Session session = sessionFactory.getCurrentSession();
		
        for (Menu menu : menus) {
            session.delete(session.load(User.class, menu.getId()));
        }
		
		syncWithPrivAndRole(session, null);
	}

	@Override
	public int getCount() {
		return ((Long)sessionFactory.getCurrentSession().createCriteria(Menu.class)
				.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), Menu.class);
	}

	@Override
	public Menu get(String ukid) {
		return get(null, ukid);
	}

	@Override
	public Menu get(org.hibernate.Session hnSession, String ukid) {
		Session session = hnSession == null ? sessionFactory.getCurrentSession()
				: hnSession;
		
		@SuppressWarnings("unchecked")
		List<Menu> list = session.createCriteria(Menu.class)
				.add(Restrictions.eq("ukid", ukid)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<Menu> getListById(Integer id) {
		return getListById(null, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Menu> getListById(org.hibernate.Session hnSession, Integer id) {
		Session session = hnSession == null ? sessionFactory.getCurrentSession()
				: hnSession;
		
		Criterion rest;
		
		if (id == null) {
			rest = Restrictions.isNull("parent");
		} else {
			rest = Restrictions.eq("parent.id", id);
		}
		
		return session.createCriteria(Menu.class).add(rest).list();
	}

	private ArrayList<String> getChildren(ArrayList<String> retList, List<Menu> list) {
		for (Menu menu : list) {
			retList.add(menu.getUkid());
			getChildren(retList, getListById(menu.getId()));
		}
		
		return retList;
	}
	
	@Override
	public ArrayList<String> getAllChildrenById(Integer id) {
		ArrayList<String> retList = new ArrayList<String>();

		if (id != null) {
			Menu current = get(id);
			if (!retList.contains(current.getUkid())) {
				retList.add(current.getUkid());
			}
			
			retList = getChildren(retList, getListById(id));
		}
		
		return retList;
	}

	@SuppressWarnings("unchecked")
	private void reorder(Menu parent, org.hibernate.Session session, HttpSession httpSession) {
		List<Menu> children = new ArrayList<Menu>();
		
		if (parent == null) {
			children = session.createCriteria(Menu.class)
					.add(Restrictions.isNull("parent")).list();
			
		} else {
			children = session.createCriteria(Menu.class)
					.add(Restrictions.eq("parent.id", parent.getId())).list();
		}
		
		Collections.sort(children, Menu.SiblingSeqComparator);
		
		int cnt = 1;
		for (Menu item : children) {
			item.setSiblingSeq((cnt++) * 10);
			item.touchWho(httpSession);
			
			session.saveOrUpdate(item);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void syncWithPrivAndRole(org.hibernate.Session hnSession, HttpSession httpSession) {
		Session session = hnSession == null ? sessionFactory.getCurrentSession()
				: hnSession;
		
		List<Menu> menuList = session.createCriteria(Menu.class).list();
		
		for (Menu menu : menuList) {
			String ukidKey =  "menu." + menu.getUkid();
			if (privDao.get(session, ukidKey) == null)
			{
				session.saveOrUpdate(new Privilege(ukidKey, httpSession));
			}

			if (! getListById(session, menu.getId()).isEmpty() &&
					roleDao.get(session, ukidKey) == null) {
				session.saveOrUpdate(new Role(ukidKey, httpSession));
			}
		}
		
		List<Privilege> privList = session.createCriteria(Privilege.class).list();
		List<Role> roleList = session.createCriteria(Role.class).list();

		ArrayList<Privilege> delPrivList = new ArrayList<Privilege>();
		for (Privilege priv : privList) {
			if (priv.getUkid().startsWith("menu.")) {
				if (get(session, priv.getUkid().substring(5)) == null) {
					delPrivList.add(priv);
				}
			}
		}
        for (Privilege privilege : delPrivList) {
            session.delete(session.load(Privilege.class, privilege.getId()));
        }

		ArrayList<Role> delRoleList = new ArrayList<Role>();
		for (Role role : roleList) {
			if (role.getUkid().startsWith("menu.")) {
				Menu menu = get(session, role.getUkid().substring(5));
				if (menu == null || getListById(session, menu.getId()).isEmpty()) {
					delRoleList.add(role);
				}
			}
		}
        for (Role role : delRoleList) {
            session.delete(session.load(Role.class, role.getId()));
        }
	}
	
	@Override
	public void syncWithPrivAndRole(HttpSession httpSession) {
		syncWithPrivAndRole(null, httpSession);
	}
	
	@Override
	public void saveAndReorder(Menu sourceParent, Menu dest, HttpSession httpSession) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(dest);
		session.flush();
		
		reorder(dest.getParent(), session, httpSession);
		
		if (sourceParent != dest.getParent()) {
			reorder(sourceParent, session, httpSession);
		}
		
		syncWithPrivAndRole(session, httpSession);
	}

	@Override
	public Menu getByUrl(String url) {
		Session session = sessionFactory.getCurrentSession();
		
		@SuppressWarnings("unchecked")
		List<Menu> list = session.createCriteria(Menu.class)
				.add(Restrictions.eq("url", url)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Menu> getExececutableList() {
		Session session = sessionFactory.getCurrentSession();
		
		Criterion rest1 = Restrictions.isNotNull("url");
		Criterion rest2 = Restrictions.ne("url", "");
		
		return session.createCriteria(Menu.class).add(
				Restrictions.and(rest1, rest2)).list();
	}
}
