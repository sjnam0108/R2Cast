package kr.co.r2cast.models.eco.service;

import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.eco.Rvm;
import kr.co.r2cast.models.eco.RvmGroup;
import kr.co.r2cast.models.eco.RvmGroupRvm;
import kr.co.r2cast.models.eco.RvmGroupUser;
import kr.co.r2cast.models.fnd.Site;

@Transactional
public interface RvmService {
	// Common
	public void flush();

	
	//
	// for RvmDao
	//
	// Common
	public Rvm getRvm(int id);
	public void saveOrUpdate(Rvm rvm);
	public void deleteRvm(Rvm rvm);
	public void deleteRvms(List<Rvm> rvms);
	
	// for Kendo Grid Remote Read
	public DataSourceResult getRvmList(DataSourceRequest request);
	public DataSourceResult getRvmList(DataSourceRequest request, boolean isEffectiveMode);
	public DataSourceResult getRvmList(DataSourceRequest request, List<Integer> rvmIds);

	// for Rvm specific
	public Rvm getRvm(String deviceID);
	public List<Rvm> getEffectiveRvmList();
	public List<Rvm> getEffectiveRvmList(Date time);
	public List<Rvm> getRvmListBySiteId(int siteId, boolean isEffectiveMode);
	public List<Rvm> getRvmListBySiteIdRvmName(int siteId, boolean isEffectiveMode, 
			String rvmName);
	public Rvm getEffectiveRvmByDeviceIDSiteShortName(String deviceID ,String siteUkid);
	public Rvm getEffectiveRvmByDeviceIDRvmId(String deviceID , int rvmId);
	public Rvm getEffectiveRvmByDeviceID(String deviceID);
	public Rvm getRvm(Site site, String rvmName);
	public Rvm getRvm(String rvmName, List<Integer> siteIds);
	public List<Rvm> getUpdatedRvmListAfter(Site site, Date date);
	public List<Object[]> getValidRvmModelCountListBySiteId(int siteId);

	public boolean isEffectiveRvm(Rvm rvm);
	

	//
	// for RvmGroupDao
	//
	// Common
	public RvmGroup getRvmGroup(int id);
	public List<RvmGroup> getRvmGroupList();
	public void saveOrUpdate(RvmGroup rvmGroup);
	public void deleteRvmGroup(RvmGroup rvmGroup);
	public void deleteRvmGroups(List<RvmGroup> rvmGroups);
	
	// for Kendo Grid Remote Read
	public DataSourceResult getRvmGroupList(DataSourceRequest request);

	// for RvmGroup specific
	public RvmGroup getRvmGroup(Site site, String rvmGroupName);
	public List<RvmGroup> getRvmGroupListBySiteId(int siteId);
	
	public List<Integer> getRvmIdListBySiteIdRvmGroupId(int siteId, int rvmGroupId);
	

	//
	// for RvmGroupRvmDao
	//
	// Common
	public RvmGroupRvm getRvmGroupRvm(int id);
	public void saveOrUpdate(RvmGroupRvm rvmGroupRvm);
	public void deleteRvmGroupRvm(RvmGroupRvm rvmGroupRvm);
	public void deleteRvmGroupRvms(List<RvmGroupRvm> rvmGroupRvms);

	// for Kendo Grid Remote Read
	public DataSourceResult getRvmGroupRvmList(DataSourceRequest request);

	// for RvmGroupRvm specific
	public boolean isRegisteredRvmGroupRvm(int siteId, int rvmGroupId, int rvmId);
	public List<RvmGroupRvm> getRvmGroupRvmListBySiteIdRvmGroupId(int siteId, int rvmGroupId,
			boolean isEffectiveMode);
	public List<RvmGroupRvm> getRvmGroupRvmListBySiteIdRvmId(int siteId, int rvmId);


	//
	// for RvmGroupUserDao
	//
	// Common
	public RvmGroupUser getRvmGroupUser(int id);
	public void saveOrUpdate(RvmGroupUser rvmGroupUser);
	public void deleteRvmGroupUser(RvmGroupUser rvmGroupUser);
	public void deleteRvmGroupUsers(List<RvmGroupUser> rvmGroupUsers);

	// for Kendo Grid Remote Read
	public DataSourceResult getRvmGroupUserList(DataSourceRequest request);

	// for RvmGroupUser specific
	public boolean isRegisteredRvmGroupUser(int siteId, int rvmGroupId, int userId);
	public List<RvmGroupUser> getRvmGroupUserListBySiteIdUserId(int siteId, int userId);

}
