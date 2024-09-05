package kr.co.r2cast.controllers.fnd;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import kr.co.r2cast.exceptions.ServerOperationForbiddenException;
import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.Message;
import kr.co.r2cast.models.MessageManager;
import kr.co.r2cast.models.ModelManager;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.service.SiteService;
import kr.co.r2cast.utils.SolUtil;
import kr.co.r2cast.utils.Util;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 사이트 컨트롤러
 */
@Controller("fnd-site-controller")
@RequestMapping(value="/fnd/site")
public class SiteController {
	private static final Logger logger = LoggerFactory.getLogger(SiteController.class);

    @Autowired 
    private SiteService siteService;
    
	@Autowired
	private MessageManager msgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	/**
	 * 사이트 페이지
	 */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String index(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	modelMgr.addMainMenuModel(model, locale, session, request);
    	msgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {
					new Message("pageTitle", "site.title"),
    				new Message("title_shortName", "site.shortName"),
    				new Message("title_siteName", "site.siteName"),
    				new Message("title_effectiveStartDate", "user.effectiveStartDate"),
    				new Message("title_effectiveEndDate", "user.effectiveEndDate"),
    				new Message("title_serverHost", "site.serverHost"),
    				new Message("title_serverPort", "site.serverPort"),
    				new Message("title_ftpHost", "site.ftpHost"),
    				new Message("title_ftpPort", "site.ftpPort"),
    				new Message("title_ftpUsername", "site.ftpUsername"),
    				new Message("title_ftpPassword", "site.ftpPassword"),
    				new Message("cmd_checkDirectory", "site.checkDirectory"),
    			});

    	
        return "fnd/site";
    }
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request) {
    	try {
            return siteService.getSiteList(request);
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
	/**
	 * 추가 액션
	 */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody Site create(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	String shortName = (String)model.get("shortName");
    	String siteName = (String)model.get("siteName");
    	Date effectiveStartDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("effectiveStartDate")));
    	Date effectiveEndDate = Util.setMaxTimeOfDate(Util.parseZuluTime((String)model.get("effectiveEndDate")));
    	
    	String serverHost = (String)model.get("serverHost");
    	Integer serverPort = (Integer)model.get("serverPort");

    	String ftpHost = (String)model.get("ftpHost");
    	Integer ftpPort = (Integer)model.get("ftpPort");
    	String ftpUsername = (String)model.get("ftpUsername");
    	String ftpPassword = (String)model.get("ftpPassword");
    	
    	Site target = new Site(shortName, siteName, effectiveStartDate, effectiveEndDate, serverHost, serverPort,
    			ftpHost, ftpPort, ftpUsername, ftpPassword, session);
    	
        saveOrUpdate(target, locale);

        return target;
    }
    
	/**
	 * 변경 액션
	 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody Site update(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	Site target = siteService.getSite((int)model.get("id"));
    	if (target != null) {
            target.setShortName((String)model.get("shortName"));
            target.setSiteName((String)model.get("siteName"));
            target.setEffectiveStartDate(Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("effectiveStartDate"))));
            target.setEffectiveEndDate(Util.setMaxTimeOfDate(Util.parseZuluTime((String)model.get("effectiveEndDate"))));

            target.setServerHost((String)model.get("serverHost"));
            target.setServerPort((Integer)model.get("serverPort"));
            target.setFtpHost((String)model.get("ftpHost"));
            target.setFtpPort((Integer)model.get("ftpPort"));
            target.setFtpUsername((String)model.get("ftpUsername"));
            target.setFtpPassword((String)model.get("ftpPassword"));
            
            target.touchWho(session);
            
            saveOrUpdate(target, locale);
    	}
    	
        return target;
    }
    
	/**
	 * 추가 / 변경 시의 자료 저장
	 */
    private void saveOrUpdate(Site target, Locale locale) throws ServerOperationForbiddenException {
    	// 비즈니스 로직 검증
        if (target.getEffectiveStartDate() != null && target.getEffectiveEndDate() != null
        		&& target.getEffectiveStartDate().after(target.getEffectiveEndDate())) {
        	throw new ServerOperationForbiddenException(msgMgr.message("common.server.msg.effectivedates", locale));
        }
        
        // DB 작업 수행 결과 검증
        try {
            siteService.saveOrUpdate(target);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("saveOrUpdate", dive);
        	throw new ServerOperationForbiddenException(msgMgr.message("site.server.msg.sameShortName", locale));
        } catch (ConstraintViolationException cve) {
    		logger.error("saveOrUpdate", cve);
        	throw new ServerOperationForbiddenException(msgMgr.message("site.server.msg.sameShortName", locale));
        } catch (Exception e) {
    		logger.error("saveOrUpdate", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }
    }
    
	/**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody List<Site> destroy(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<Site> sites = new ArrayList<Site>();
    	
    	for (Object id : objs) {
    		Site site = new Site();
    		
    		site.setId((int)id);
    		
    		sites.add(site);
    	}
    	
    	try {
        	siteService.deleteSites(sites);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return sites;
    }
    
    /**
	 * 디렉토리 확인 액션
	 */
    @RequestMapping(value = "/checkdir", method = RequestMethod.POST)
    public @ResponseBody String checkDirectory(@RequestBody Map<String, Object> model, 
    		HttpSession session, Locale locale) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	try {
    		
    		          	for (Object id : objs) {
    			          		Site target = siteService.getSite((int) id);
    			          		
    			          		if (target != null) {
    			          			if (!SolUtil.checkSiteDirectory(target.getShortName())) {
    			          	    		throw new ServerOperationForbiddenException("OperationError");
    			          			}
    			          		} else {
    			              		throw new ServerOperationForbiddenException(
    			              				msgMgr.message("common.server.msg.wrongParamError", locale));
    			          		}
    			          	}
    			          	
    		
    	} catch (Exception e) {
    		logger.error("checkDirectory", e);
    		throw new ServerOperationForbiddenException("OperationError");
    	}

        return "OperationSuccess";
    }
}
