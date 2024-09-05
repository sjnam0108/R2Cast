package kr.co.r2cast.controllers.eco;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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

import kr.co.r2cast.exceptions.ServerOperationForbiddenException;
import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.EcoMessageManager;
import kr.co.r2cast.models.Message;
import kr.co.r2cast.models.MessageManager;
import kr.co.r2cast.models.ModelManager;
import kr.co.r2cast.models.eco.RvmGroup;
import kr.co.r2cast.models.eco.service.RvmService;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.service.SiteService;
import kr.co.r2cast.utils.Util;

/**
 * RVM 그룹 컨트롤러
 */
@Controller("eco-rvm-group-controller")
@RequestMapping(value="/eco/rvmgroup")
public class RvmGroupController {
	private static final Logger logger = LoggerFactory.getLogger(RvmGroupController.class);

    @Autowired 
    private RvmService rvmService;
    
    @Autowired 
    private SiteService siteService;
    
    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private EcoMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * RVM 그룹 페이지
	 */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String index(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	modelMgr.addMainMenuModel(model, locale, session, request);
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});

    	// 페이지 제목
    	model.addAttribute("pageTitle", "RVM 그룹");

    	
    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
    	
        return "eco/rvmgroup";
    }
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request, 
    		Locale locale) {
    	try {
    		return rvmService.getRvmGroupList(request);
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("readError");
    	}
    }
    
	/**
	 * 추가 액션
	 */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody String create(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	int siteId = Util.getSessionSiteId(session);
    	String rvmGroupName = (String)model.get("rvmGroupName");
    	String category = (String)model.get("category");
    	String viewType = (String)model.get("viewType");
    	
    	// 파라미터 검증
    	if (Util.isNotValid(rvmGroupName) || Util.isNotValid(category) || Util.isNotValid(viewType)) {
        	throw new ServerOperationForbiddenException(msgMgr.message("common.server.msg.wrongParamError", locale));
        }

    	Site site = siteService.getSite(siteId);
    	if (site == null) {
        	throw new ServerOperationForbiddenException(msgMgr.message("common.server.msg.wrongParamError", locale));
    	}

    	RvmGroup target = new RvmGroup(site, rvmGroupName, category, viewType, session);
    	
        saveOrUpdate(target, locale);

        return "OK";
    }
    
	/**
	 * 변경 액션
	 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody String update(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	RvmGroup target = rvmService.getRvmGroup((int)model.get("id"));
    	if (target != null) {
        	String rvmGroupName = (String)model.get("rvmGroupName");
        	String category = (String)model.get("category");
        	String viewType = (String)model.get("viewType");
        	
        	// 파라미터 검증
        	if (Util.isNotValid(rvmGroupName) || Util.isNotValid(category) || Util.isNotValid(viewType)) {
            	throw new ServerOperationForbiddenException(msgMgr.message("common.server.msg.wrongParamError", locale));
            }

        	Site site = siteService.getSite(Util.getSessionSiteId(session));
        	if (site == null) {
            	throw new ServerOperationForbiddenException(msgMgr.message("common.server.msg.wrongParamError", locale));
        	}

        	target.setSite(site);
        	target.setRvmGroupName(rvmGroupName);
        	target.setCategory(category);
        	target.setViewType(viewType);
        	
            target.touchWho(session);
            
            saveOrUpdate(target, locale);
    	}
    	
        return "OK";
    }
    
	/**
	 * 추가 / 변경 시의 자료 저장
	 */
    private void saveOrUpdate(RvmGroup target, Locale locale) throws ServerOperationForbiddenException {
    	// 비즈니스 로직 검증
    	// 해당 사항 없음
        
        // DB 작업 수행 결과 검증
        try {
            rvmService.saveOrUpdate(target);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("saveOrUpdate", dive);
        	throw new ServerOperationForbiddenException("동일한 RVM 그룹명의 자료가 이미 등록되어 있습니다.");
        } catch (ConstraintViolationException cve) {
    		logger.error("saveOrUpdate", cve);
        	throw new ServerOperationForbiddenException("동일한 RVM 그룹명의 자료가 이미 등록되어 있습니다.");
        } catch (Exception e) {
    		logger.error("saveOrUpdate", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }
    }
    
	/**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<RvmGroup> rvmGroups = new ArrayList<RvmGroup>();
    	
    	for (Object id : objs) {
    		RvmGroup rvmGroup = new RvmGroup();
    		
    		rvmGroup.setId((int)id);
    		
    		rvmGroups.add(rvmGroup);
    	}
    	
    	try {
        	rvmService.deleteRvmGroups(rvmGroups);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "OK";
    }
}
