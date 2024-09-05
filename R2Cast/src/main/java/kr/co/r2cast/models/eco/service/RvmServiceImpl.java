package kr.co.r2cast.models.eco.service;
 
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.eco.Rvm;
import kr.co.r2cast.models.eco.RvmGroup;
import kr.co.r2cast.models.eco.RvmGroupRvm;
import kr.co.r2cast.models.eco.RvmGroupUser;
import kr.co.r2cast.models.eco.dao.RvmDao;
import kr.co.r2cast.models.eco.dao.RvmGroupDao;
import kr.co.r2cast.models.eco.dao.RvmGroupRvmDao;
import kr.co.r2cast.models.eco.dao.RvmGroupUserDao;
import kr.co.r2cast.models.fnd.Site;

@Transactional
@Service("rvmService")
public class RvmServiceImpl implements RvmService {
    @Autowired
    private SessionFactory sessionFactory;
    
    @Autowired
    private RvmDao rvmDao;
    
    @Autowired
    private RvmGroupDao rvmGroupDao;
    
    @Autowired
    private RvmGroupRvmDao rvmGroupRvmDao;
    
    @Autowired
    private RvmGroupUserDao rvmGroupUserDao;

    
	@Override
	public void flush() {
		sessionFactory.getCurrentSession().flush();
	}
	

	//
	// for RvmDao
	//
	@Override
	public Rvm getRvm(int id) {
		return rvmDao.get(id);
	}

	@Override
	public void saveOrUpdate(Rvm rvm) {
		rvmDao.saveOrUpdate(rvm);
	}

	@Override
	public void deleteRvm(Rvm rvm) {
		rvmDao.delete(rvm);
	}

	@Override
	public void deleteRvms(List<Rvm> rvms) {
		rvmDao.delete(rvms);
	}

	@Override
	public DataSourceResult getRvmList(DataSourceRequest request) {
		return rvmDao.getList(request);
	}

	@Override
	public DataSourceResult getRvmList(DataSourceRequest request, boolean isEffectiveMode) {
		return rvmDao.getList(request, isEffectiveMode);
	}

	@Override
	public DataSourceResult getRvmList(DataSourceRequest request, List<Integer> rvmIds) {
		return rvmDao.getList(request, rvmIds);
	}

	@Override
	public Rvm getRvm(String deviceID) {
		return rvmDao.get(deviceID);
	}

	@Override
	public List<Rvm> getEffectiveRvmList() {
		return rvmDao.getEffectiveList();
	}

	@Override
	public List<Rvm> getEffectiveRvmList(Date time) {
		return rvmDao.getEffectiveList(time);
	}

	@Override
	public List<Rvm> getRvmListBySiteId(int siteId, boolean isEffectiveMode) {
		return rvmDao.getListBySiteId(siteId, isEffectiveMode);
	}

	@Override
	public List<Rvm> getRvmListBySiteIdRvmName(int siteId, boolean isEffectiveMode, String rvmName) {
		return rvmDao.getListBySiteIdRvmName(siteId, isEffectiveMode, rvmName);
	}

	@Override
	public Rvm getEffectiveRvmByDeviceIDSiteShortName(String deviceID, String siteUkid) {
		return rvmDao.getByDeviceIDSiteShortName(deviceID, siteUkid);
	}

	@Override
	public Rvm getEffectiveRvmByDeviceIDRvmId(String deviceID, int rvmId) {
		return rvmDao.getByDeviceIDRvmId(deviceID, rvmId);
	}

	@Override
	public Rvm getEffectiveRvmByDeviceID(String deviceID) {
		return rvmDao.getByDeviceID(deviceID);
	}

	@Override
	public Rvm getRvm(Site site, String rvmName) {
		return rvmDao.get(site, rvmName);
	}

	@Override
	public Rvm getRvm(String rvmName, List<Integer> siteIds) {
		return rvmDao.get(rvmName, siteIds);
	}

	@Override
	public List<Rvm> getUpdatedRvmListAfter(Site site, Date date) {
		return getUpdatedRvmListAfter(site, date);
	}

	@Override
	public List<Object[]> getValidRvmModelCountListBySiteId(int siteId) {
		return rvmDao.getValidModelCountListBySiteId(siteId);
	}

	
	@Override
	public boolean isEffectiveRvm(Rvm rvm) {
		if (rvm == null) {
			return false;
		}
		
		Date now = new Date();
		if (now.before(rvm.getEffectiveStartDate())) {
			return false;
		}
		
		if (rvm.getEffectiveEndDate() != null && now.after(rvm.getEffectiveEndDate())) {
			return false;
		}

		return true;
	}
	

	//
	// for RvmGroupDao
	//
	@Override
	public RvmGroup getRvmGroup(int id) {
		return rvmGroupDao.get(id);
	}
	
	@Override
	public List<RvmGroup> getRvmGroupList() {
		return rvmGroupDao.getList();
	}

	@Override
	public void saveOrUpdate(RvmGroup rvmGroup) {
		rvmGroupDao.saveOrUpdate(rvmGroup);
	}
	
	@Override
	public void deleteRvmGroup(RvmGroup rvmGroup) {
		rvmGroupDao.delete(rvmGroup);
	}

	@Override
	public void deleteRvmGroups(List<RvmGroup> rvmGroups) {
		rvmGroupDao.delete(rvmGroups);
	}
	
	@Override
	public DataSourceResult getRvmGroupList(DataSourceRequest request) {
		return rvmGroupDao.getList(request);
	}

	@Override
	public RvmGroup getRvmGroup(Site site, String rvmGroupName) {
		return rvmGroupDao.get(site, rvmGroupName);
	}
	
	@Override
	public List<RvmGroup> getRvmGroupListBySiteId(int siteId) {
		return rvmGroupDao.getListBySiteId(siteId);
	}
	
	
	@Override
	public List<Integer> getRvmIdListBySiteIdRvmGroupId(int siteId, int rvmGroupId) {
		
		ArrayList<Integer> rvmIds = new ArrayList<Integer>();
		if (siteId > 0 && rvmGroupId > 0) {
			List<RvmGroupRvm> rvmGroupRvms = 
					getRvmGroupRvmListBySiteIdRvmGroupId(siteId, rvmGroupId, true);
			for (RvmGroupRvm rvmGroupRvm : rvmGroupRvms) {
				rvmIds.add(rvmGroupRvm.getRvm().getId());
			}
		}
		
		return rvmIds;
	}
	

	//
	// for RvmGroupRvmDao
	//
	@Override
	public RvmGroupRvm getRvmGroupRvm(int id) {
		return rvmGroupRvmDao.get(id);
	}

	@Override
	public void saveOrUpdate(RvmGroupRvm rvmGroupRvm) {
		rvmGroupRvmDao.saveOrUpdate(rvmGroupRvm);
	}

	@Override
	public void deleteRvmGroupRvm(RvmGroupRvm rvmGroupRvm) {
		rvmGroupRvmDao.delete(rvmGroupRvm);
	}

	@Override
	public void deleteRvmGroupRvms(List<RvmGroupRvm> rvmGroupRvms) {
		rvmGroupRvmDao.delete(rvmGroupRvms);
	}

	@Override
	public DataSourceResult getRvmGroupRvmList(DataSourceRequest request) {
		return rvmGroupRvmDao.getList(request);
	}

	@Override
	public boolean isRegisteredRvmGroupRvm(int siteId, int rvmGroupId, int rvmId) {
		return rvmGroupRvmDao.isRegistered(siteId, rvmGroupId, rvmId);
	}

	@Override
	public List<RvmGroupRvm> getRvmGroupRvmListBySiteIdRvmGroupId(int siteId, int rvmGroupId, boolean isEffectiveMode) {
		return rvmGroupRvmDao.getListBySiteIdRvmGroupId(siteId, rvmGroupId, isEffectiveMode);
	}

	@Override
	public List<RvmGroupRvm> getRvmGroupRvmListBySiteIdRvmId(int siteId, int rvmId) {
		return rvmGroupRvmDao.getListBySiteIdRvmId(siteId, rvmId);
	}
	

	//
	// for RvmGroupUserDao
	//
	@Override
	public RvmGroupUser getRvmGroupUser(int id) {
		return rvmGroupUserDao.get(id);
	}

	@Override
	public void saveOrUpdate(RvmGroupUser rvmGroupUser) {
		rvmGroupUserDao.saveOrUpdate(rvmGroupUser);
	}

	@Override
	public void deleteRvmGroupUser(RvmGroupUser rvmGroupUser) {
		rvmGroupUserDao.delete(rvmGroupUser);
	}

	@Override
	public void deleteRvmGroupUsers(List<RvmGroupUser> rvmGroupUsers) {
		rvmGroupUserDao.delete(rvmGroupUsers);
	}

	@Override
	public DataSourceResult getRvmGroupUserList(DataSourceRequest request) {
		return rvmGroupUserDao.getList(request);
	}

	@Override
	public boolean isRegisteredRvmGroupUser(int siteId, int rvmGroupId, int userId) {
		return rvmGroupUserDao.isRegistered(siteId, rvmGroupId, userId);
	}

	@Override
	public List<RvmGroupUser> getRvmGroupUserListBySiteIdUserId(int siteId,
			int userId) {
		return rvmGroupUserDao.getListBySiteIdUserId(siteId, userId);
	}
	
}
