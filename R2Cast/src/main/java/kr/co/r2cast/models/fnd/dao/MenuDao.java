package kr.co.r2cast.models.fnd.dao;

import java.util.List;

import javax.servlet.http.HttpSession;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.fnd.Menu;

public interface MenuDao {
	// Common
	public Menu get(int id);
	public List<Menu> getList();
	public void saveOrUpdate(Menu menu);
	public void delete(Menu menu);
	public void delete(List<Menu> menus);
	public int getCount();

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for Menu specific
	public Menu get(String ukid);
	public Menu get(org.hibernate.Session hnSession, String ukid);
	public List<Menu> getListById(Integer id);
	public List<Menu> getListById(org.hibernate.Session hnSession, Integer id); 
	public List<String> getAllChildrenById(Integer id);
	public void saveAndReorder(Menu sourceParent, Menu dest, HttpSession httpSession);
	public void syncWithPrivAndRole(HttpSession httpSession);
	public Menu getByUrl(String url);
	public List<Menu> getExececutableList();
}
