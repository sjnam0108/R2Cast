package kr.co.r2cast.models.eco.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.r2cast.models.eco.DispMenu;
import kr.co.r2cast.models.eco.SiteOpt;
import kr.co.r2cast.models.eco.dao.DispMenuDao;
import kr.co.r2cast.models.eco.dao.SiteOptDao;
import kr.co.r2cast.utils.Util;
import kr.co.r2cast.viewmodels.eco.OptionItem;
import kr.co.r2cast.viewmodels.eco.OptionItem.OptType;


@Transactional
@Service("optService")
public class OptServiceImpl implements OptService {
    @Autowired
    private SessionFactory sessionFactory;
    
    @Autowired
    private SiteOptDao siteOptDao;
    
    @Autowired
    private DispMenuDao dispMenuDao;
    
	@Override
	public void flush() {
		sessionFactory.getCurrentSession().flush();
	}
	

	//
	// local methods
	//
	private ArrayList<OptionItem> registerOption(List<OptionItem> list, OptionItem optItem) {
		return registerOption(list, optItem, true);
	}
	
	private ArrayList<OptionItem> registerOption(List<OptionItem> list, 
			OptionItem optItem, boolean isAddedMode) {
		ArrayList<OptionItem> ret = new ArrayList<OptionItem>();
		
		if (list != null && list.size() > 0) {
			ret.addAll(list);
		}
		
		if (optItem != null) {
			boolean containsOptName = false;
			for(OptionItem item : list) {
				if (optItem.getName().equals(item.getName())) {
					item.setValue(optItem.getValue());
					item.setOptType(optItem.getOptType());
					
					containsOptName = true;
					
					break;
				}
			}
			
			if (isAddedMode && !containsOptName) {
				ret.add(optItem);
			}
		}
		
		return ret;
	}
	
	private OptionItem getGlobalSiteOption(String optName, Locale locale) {
		if (Util.isNotValid(optName)) {
			return null;
		}
		
		//String value = msgMgr.message(optName, locale);
		String value = Util.message(optName, locale);
		value = Util.isValid(value) ? value : "";
		
		if (optName.equals(value)) {
			value = "";
		}
		
		return new OptionItem(optName, value, OptType.GlobalSite);
	}
	

	//
	// for SiteOptDao
	//
	@Override
	public SiteOpt getSiteOpt(int id) {
		return siteOptDao.get(id);
	}
	
	@Override
	public void saveOrUpdate(SiteOpt siteOpt) {
		siteOptDao.saveOrUpdate(siteOpt);
	}

	@Override
	public void deleteSiteOpt(SiteOpt siteOpt) {
		siteOptDao.delete(siteOpt);
	}

	@Override
	public void deleteSiteOpts(List<SiteOpt> siteOpts) {
		siteOptDao.delete(siteOpts);
	}

	@Override
	public SiteOpt getSiteOptByOptNameSiteId(String optName, int siteId) {
		return siteOptDao.getByOptNameSiteId(optName, siteId);
	}

	@Override
	public List<SiteOpt> getSiteOptListBySiteId(int siteId) {
		return siteOptDao.getListBySiteId(siteId);
	}
	
	@Override
	public ArrayList<OptionItem> getGlobalSiteOptions(Locale locale) {
		ArrayList<OptionItem> ret = new ArrayList<OptionItem>();
		
		String[] optNames = new String[] { 
				"edition.code", 
				"auto.rvmReg",
				"auto.siteUser",
				"auto.schdDeploy",
				"ftpServer.advConfig",
				"map.init.latitude", 
				"map.init.longitude",
				"rvmGroup.order",
				"routineSched.prefix",
				"auto.rvmStatus",
				"playerID.prefix",
				"playerID.digit",
				"rvm.model.list",
				"asset.enabled",
				"assetID.digit",
				"asset.allowed.siteID.list",
				"asset.siteID",
				"playerasset.max.stb",
				"playerasset.max.display",
				"quicklink.max.menu",
				"logo.title",
				"failureControl.enabled",
				"failureControl.startTime",
				"failureControl.endTime",
				"failureControl.regMins",
				"failureControl.removalAllowed",
			};
		
		for(String optName : optNames) {
			ret = registerOption(ret, getGlobalSiteOption(optName, locale));
		}
		
		return ret;
	}
	
	@Override
	public ArrayList<OptionItem> getCurrentSiteOptions(int siteId, Locale locale) {
		ArrayList<OptionItem> items = getGlobalSiteOptions(locale);
		
		List<SiteOpt> siteOptions = getSiteOptListBySiteId(siteId);
		for(SiteOpt item : siteOptions) {
			items = registerOption(items, 
					new OptionItem(item.getOptName(), item.getOptValue(), OptType.Site), false);
		}
		
		return items;
	}

	@Override
	public String getSiteOption(int siteId, String name) {
		return getSiteOption(siteId, name, null);
	}

	@Override
	public String getSiteOption(int siteId, String name, Locale locale) {
		List<OptionItem> options = getCurrentSiteOptions(siteId, locale);
		for(OptionItem optItem : options) {
			if (optItem.getName().equals(name)) {
				return optItem.getValue();
			}
		}
		
		return "";
	}

	@Override
	public String getSiteOption(HttpSession session, String name) {
		return getSiteOption(Util.getSessionSiteId(session), name, null);
	}
	
	@Override
	public String getSiteOption(HttpSession session, String name, Locale locale) {
		return getSiteOption(Util.getSessionSiteId(session), name, locale);
	}
	

	//
	// for DispMenuDao
	//
	@Override
	public DispMenu getDispMenu(int id) {
		return dispMenuDao.get(id);
	}

	@Override
	public void saveOrUpdate(DispMenu dispMenu) {
		dispMenuDao.saveOrUpdate(dispMenu);
	}

	@Override
	public void deleteDispMenu(DispMenu dispMenu) {
		dispMenuDao.delete(dispMenu);
	}

	@Override
	public void deleteDispMenus(List<DispMenu> dispMenus) {
		dispMenuDao.delete(dispMenus);
	}

	@Override
	public DispMenu getDispMenuBySiteId(int siteId) {
		return dispMenuDao.getBySiteId(siteId);
	}
	
}
