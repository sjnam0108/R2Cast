package kr.co.r2cast.models.fnd.service;

import java.util.Date;
import java.util.List;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.SiteSite;
import kr.co.r2cast.models.fnd.SiteUser;
import kr.co.r2cast.models.fnd.dao.LoginLogDao;
import kr.co.r2cast.models.fnd.dao.SiteDao;
import kr.co.r2cast.models.fnd.dao.SiteSiteDao;
import kr.co.r2cast.models.fnd.dao.SiteUserDao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service("siteService")
public class SiteServiceImpl implements SiteService {
    @Autowired
    private SessionFactory sessionFactory;
    
    @Autowired
    private SiteDao siteDao;

    @Autowired
    private SiteUserDao siteUserDao;

    @Autowired
    private SiteSiteDao siteSiteDao;

    @SuppressWarnings("unused")
	@Autowired
    private LoginLogDao loginLogDao;

	@Override
	public void flush() {
		sessionFactory.getCurrentSession().flush();
	}

	@Override
	public Site getSite(int id) {
		return siteDao.get(id);
	}

	@Override
	public List<Site> getSiteList() {
		return siteDao.getList();
	}

	@Override
	public void saveOrUpdate(Site site) {
		siteDao.saveOrUpdate(site);
	}

	@Override
	public void deleteSite(Site site) {
		siteDao.delete(site);
	}

	@Override
	public void deleteSites(List<Site> sites) {
		siteDao.delete(sites);
	}

	@Override
	public int getSiteCount() {
		return siteDao.getCount();
	}

	@Override
	public DataSourceResult getSiteList(DataSourceRequest request) {
		return siteDao.getList(request);
	}

	@Override
	public Site getSite(String shortName) {
		return siteDao.get(shortName);
	}

	@Override
	public List<Site> getEffectiveSiteList() {
		return siteDao.getEffectiveList();
	}

	@Override
	public List<Site> getEffectiveSiteList(Date time) {
		return siteDao.getEffectiveList(time);
	}

	@Override
	public boolean isEffectiveSite(Site site) {
		if (site == null) {
			return false;
		}
		
		Date now = new Date();
		if (now.before(site.getEffectiveStartDate())) {
			return false;
		}
		
		if (site.getEffectiveEndDate() != null && now.after(site.getEffectiveEndDate())) {
			return false;
		}

		return true;
	}

	@Override
	public SiteUser getSiteUser(int id) {
		return siteUserDao.get(id);
	}

	@Override
	public List<SiteUser> getSiteUserList() {
		return siteUserDao.getList();
	}

	@Override
	public void saveOrUpdate(SiteUser siteUser) {
		siteUserDao.saveOrUpdate(siteUser);
	}

	@Override
	public void deleteSiteUser(SiteUser siteUser) {
		siteUserDao.delete(siteUser);
	}

	@Override
	public void deleteSiteUsers(List<SiteUser> siteUsers) {
		siteUserDao.delete(siteUsers);
	}

	@Override
	public int getSiteUserCount() {
		return siteUserDao.getCount();
	}

	@Override
	public DataSourceResult getSiteUserList(DataSourceRequest request) {
		return siteUserDao.getList(request);
	}

	@Override
	public boolean isRegisteredSiteUser(int siteId, int userId) {
		return siteUserDao.isRegistered(siteId, userId);
	}

	@Override
	public SiteSite getSiteSite(int id) {
		return siteSiteDao.get(id);
	}

	@Override
	public List<SiteSite> getSiteSiteList() {
		return siteSiteDao.getList();
	}

	@Override
	public void saveOrUpdate(SiteSite siteSite) {
		siteSiteDao.saveOrUpdate(siteSite);
	}

	@Override
	public void deleteSiteSite(SiteSite siteSite) {
		siteSiteDao.delete(siteSite);
	}

	@Override
	public void deleteSiteSites(List<SiteSite> siteSites) {
		siteSiteDao.delete(siteSites);
	}

	@Override
	public int getSiteSiteCount() {
		return siteSiteDao.getCount();
	}

	@Override
	public DataSourceResult getSiteSiteList(DataSourceRequest request) {
		return siteSiteDao.getList(request);
	}

	@Override
	public boolean isRegisteredSiteSite(int parentSiteId, int childSiteId) {
		return siteSiteDao.isRegistered(parentSiteId, childSiteId);
	}

	@Override
	public List<SiteSite> getSiteSiteListByParentSiteId(int siteId) {
		return siteSiteDao.getListByParentSiteId(siteId);
	}

	@Override
	public int getRegionCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DataSourceResult getRegionList(DataSourceRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	// [SignCast] ext ----------------------------------------------------------- start
	//
	//

//    @Autowired
//    private RegionDao regionDao;
//    
//	
//	@Override
//	public Region getRegion(int id) {
//		return regionDao.get(id);
//	}
//
//	@Override
//	public List<Region> getRegionList() {
//		return regionDao.getList();
//	}
//
//	@Override
//	public void saveOrUpdate(Region region) {
//		regionDao.saveOrUpdate(region);
//	}
//
//	@Override
//	public void deleteRegion(Region region) {
//		regionDao.delete(region);
//	}
//
//	@Override
//	public void deleteRegions(List<Region> regions) {
//		regionDao.delete(regions);
//	}
//
//	@Override
//	public int getRegionCount() {
//		return regionDao.getCount();
//	}
//
//	@Override
//	public DataSourceResult getRegionList(DataSourceRequest request) {
//		return regionDao.getList(request);
//	}
//
//	@Override
//	public Region getRegion(String regionCode) {
//		return regionDao.get(regionCode);
//	}
//
//	@Override
//	public Region getRegionByRegionName(String regionName) {
//		return regionDao.getByRegionName(regionName);
//	}
//
//	@Override
//	public List<Region> getRegionListByCountryCode(String countryCode) {
//		return regionDao.getListByCountryCode(countryCode);
//	}
//	
	//
	//
	// [SignCast] ext ------------------------------------------------------------- end
}
