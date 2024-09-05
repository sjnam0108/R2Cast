package kr.co.r2cast.models.eco.dao;

import java.util.List;

import kr.co.r2cast.models.eco.SiteOpt;

public interface SiteOptDao {
	// Common
	public SiteOpt get(int id);
	public List<SiteOpt> getList();
	public void saveOrUpdate(SiteOpt siteOpt);
	public void delete(SiteOpt siteOpt);
	public void delete(List<SiteOpt> siteOpts);
	public int getCount();

	// for SiteOpt specific
	public SiteOpt getByOptNameSiteId(String optName, int siteId);
	public List<SiteOpt> getListBySiteId(int siteId);
}
