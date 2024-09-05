package kr.co.r2cast.models.fnd.dao;

import java.util.List;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.fnd.Role;

public interface RoleDao {
	// Common
	public Role get(int id);
	public List<Role> getList();
	public void saveOrUpdate(Role role);
	public void delete(Role role);
	public void delete(List<Role> roles);
	public int getCount();

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for Role specific
	public Role get(String ukid);
	public Role get(org.hibernate.Session hnSession, String ukid);
}
