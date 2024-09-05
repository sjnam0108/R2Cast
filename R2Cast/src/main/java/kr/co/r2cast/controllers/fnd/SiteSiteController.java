package kr.co.r2cast.controllers.fnd;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.co.r2cast.exceptions.ServerOperationForbiddenException;
import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.Message;
import kr.co.r2cast.models.MessageManager;
import kr.co.r2cast.models.ModelManager;
import kr.co.r2cast.models.DataSourceRequest.SortDescriptor;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.SiteSite;
import kr.co.r2cast.models.fnd.service.SiteService;
import kr.co.r2cast.utils.Util;

/**
 * 자식 사이트 컨트롤러
 */
@Controller("fnd-site-site-controller")
@RequestMapping(value="/fnd/childsite")
public class SiteSiteController {
	private static final Logger logger = LoggerFactory.getLogger(SiteSiteController.class);

    @Autowired 
    private SiteService siteService;
    
	@Autowired
	private MessageManager msgMgr;
    
	@Autowired
	private ModelManager modelMgr;
    
	/**
	 * 자식 사이트 페이지
	 */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String index(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	modelMgr.addMainMenuModel(model, locale, session, request);
    	msgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {
					new Message("pageTitle", "childsite.title"),
    				new Message("title_parentShortName", "childsite.parentShortName"),
    				new Message("title_parentSiteName", "childsite.parentSiteName"),
    				new Message("title_childShortName", "childsite.childShortName"),
    				new Message("title_childSiteName", "childsite.childSiteName"),
    				new Message("label_parent", "childsite.parent"),
    				new Message("label_child", "childsite.child"),
    				new Message("title_shortName", "childsite.shortName"),
    				new Message("title_siteName", "childsite.siteName"),
    			});

    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
        
    	
        return "fnd/childsite";
    }
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request) {
    	try {
            return siteService.getSiteSiteList(request);
    	} catch (RuntimeException re) {
    		logger.error("read", re);
    		throw new ServerOperationForbiddenException("ReadError");
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }

	/**
	 * 추가 액션(자료 저장 포함)
	 */
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody String create(@RequestBody Map<String, Object> model, Locale locale, 
    		HttpSession session) {
    	ArrayList<Object> parentSiteIds = (ArrayList<Object>) model.get("parentSiteIds");
    	ArrayList<Object> childSiteIds = (ArrayList<Object>) model.get("childSiteIds");
    	
		int cnt = 0;

		if (parentSiteIds.size() > 0 && childSiteIds.size() > 0) {
    		try {
        		for(Object parentSiteObj : parentSiteIds) {
        			Site parentSite = siteService.getSite((int) parentSiteObj);

        			if (parentSite == null) {
        				continue;
        			}
        			
        			for(Object childSiteObj : childSiteIds) {
        				Site childSite = siteService.getSite((int) childSiteObj);
        				
        				if (childSite == null || parentSite.getId() == childSite.getId()) {
        					continue;
        				}
        				
            			if (!siteService.isRegisteredSiteSite(parentSite.getId(), childSite.getId())) {
            				siteService.saveOrUpdate(new SiteSite(parentSite, childSite, session));
            				cnt ++;
            			}
        			}
        		}
    		} catch (Exception e) {
        		logger.error("create", e);
        		throw new ServerOperationForbiddenException("SaveError");
        	}
    	} else {
    		throw new ServerOperationForbiddenException(msgMgr.message("common.server.msg.wrongParamError", locale));
    	}
    	
		if (cnt == 0) {
			return msgMgr.message("childsite.server.msg.operationNotRequired", locale);
		}
		
    	return msgMgr.message("common.server.msg.saveSuccessWithCount", new Object[] {cnt}, locale);
    }

	/**
	 * 변경 액션
	 */
    // 해당 없음

	/**
	 * 추가 / 변경 시의 자료 저장
	 */
    // 해당 없음
    
	/**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody List<SiteSite> destroy(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<SiteSite> siteSites = new ArrayList<SiteSite>();

    	for (Object id : objs) {
    		SiteSite siteSite = new SiteSite();
    		
    		siteSite.setId((int)id);
    		
    		siteSites.add(siteSite);
    	}
    	
    	try {
        	siteService.deleteSiteSites(siteSites);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return siteSites;
    }
    
	/**
	 * 읽기 액션 - 사이트 정보
	 */
    @RequestMapping(value = "/readSites", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readSites(@RequestBody DataSourceRequest request, Locale locale) {
    	try {
    		SortDescriptor sort = new SortDescriptor();
    		sort.setDir("asc");
    		sort.setField("shortName");
    		
    		ArrayList<SortDescriptor> list = new ArrayList<SortDescriptor>();
    		list.add(sort);
    		
    		request.setSort(list);
    		
            return siteService.getSiteList(request);
    	} catch (Exception e) {
    		logger.error("readSites", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
}
