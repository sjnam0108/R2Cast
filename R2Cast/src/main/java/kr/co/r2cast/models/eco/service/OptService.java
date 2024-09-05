package kr.co.r2cast.models.eco.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;

import kr.co.r2cast.models.eco.DispMenu;
import kr.co.r2cast.models.eco.SiteOpt;
import kr.co.r2cast.viewmodels.eco.OptionItem;

@Transactional
public interface OptService {
	// Common
	public void flush();

	
	//
	// for SiteOpt Dao
	//
	// Common
	public SiteOpt getSiteOpt(int id);
	public void saveOrUpdate(SiteOpt siteOpt);
	public void deleteSiteOpt(SiteOpt siteOpt);
	public void deleteSiteOpts(List<SiteOpt> siteOpts);

	// for SiteOpt specific
	public SiteOpt getSiteOptByOptNameSiteId(String optName, int siteId);
	public List<SiteOpt> getSiteOptListBySiteId(int siteId);
	
	// for General purposes
	public ArrayList<OptionItem> getGlobalSiteOptions(Locale locale);
	public ArrayList<OptionItem> getCurrentSiteOptions(int siteId, Locale locale);
	public String getSiteOption(int siteId, String name);
	public String getSiteOption(int siteId, String name, Locale locale);
	public String getSiteOption(HttpSession session, String name);
	public String getSiteOption(HttpSession session, String name, Locale locale);

	
	//
	// for DispMenu Dao
	//
	// Common
	public DispMenu getDispMenu(int id);
	public void saveOrUpdate(DispMenu dispMenu);
	public void deleteDispMenu(DispMenu dispMenu);
	public void deleteDispMenus(List<DispMenu> dispMenus);

	// for DispMenu specific
	public DispMenu getDispMenuBySiteId(int siteId);
	
}
