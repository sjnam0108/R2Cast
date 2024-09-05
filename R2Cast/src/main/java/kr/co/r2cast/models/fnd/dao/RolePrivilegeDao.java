package kr.co.r2cast.models.fnd.dao;

import java.util.List;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.fnd.RolePrivilege;

public interface RolePrivilegeDao {
	// Common
	public RolePrivilege get(int id);
	public List<RolePrivilege> getList();
	public void saveOrUpdate(RolePrivilege rolePrivilege);
	public void delete(RolePrivilege rolePrivilege);
	public void delete(List<RolePrivilege> rolePrivileges);
	public int getCount();

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for RolePrivilege specific
	public boolean isRegistered(int roleId, int privilegeId);
	public void saveOrUpdate(List<RolePrivilege> rolePrivileges);
	public List<RolePrivilege> getListByRoleId(int roleId);
}
