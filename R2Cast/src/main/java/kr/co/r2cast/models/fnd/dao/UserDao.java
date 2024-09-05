package kr.co.r2cast.models.fnd.dao;

import java.util.Date;
import java.util.List;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.fnd.User;

public interface UserDao {
	// Common
	public User get(int id);
	public List<User> getList();
	public void saveOrUpdate(User user);
	public void delete(User user);
	public void delete(List<User> users);
	public int getCount();

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for User specific
	public User get(String username);
	public List<User> getEffectiveList();
	public List<User> getEffectiveList(Date time);
}
