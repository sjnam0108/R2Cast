package kr.co.r2cast.controllers.eco;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

//import kr.co.ecocast.viewmodels.dsg.StbOverviewData;
import kr.co.r2cast.exceptions.ServerOperationForbiddenException;
import kr.co.r2cast.models.DataSourceRequest;	
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.EcoMessageManager;
import kr.co.r2cast.models.EcoUserCookie;
import kr.co.r2cast.models.Message;
import kr.co.r2cast.models.MessageManager;
import kr.co.r2cast.models.ModelManager;
import kr.co.r2cast.models.eco.Rvm;
import kr.co.r2cast.models.eco.service.OptService;
import kr.co.r2cast.models.eco.service.RvmService;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.service.SiteService;
import kr.co.r2cast.utils.Util;
import kr.co.r2cast.viewmodels.DropDownListItem;

/**
 * RVM 컨트롤러
 */
@Controller("eco-rvm-controller")
@RequestMapping(value="/eco/rvm")
public class RvmController {
	private static final Logger logger = LoggerFactory.getLogger(RvmController.class);

    @Autowired 
    private RvmService rvmService;
    
    @Autowired 
    private SiteService siteService;
    
    @Autowired 
    private OptService optService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private EcoMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * RVM 페이지
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
    	model.addAttribute("pageTitle", "RVM");
    	
    	
    	// RVM 지도 위치 표시 중 필요할 수 있는 사이트 HQ 정보
    	model.addAttribute("value_lat",optService.getSiteOption(session, "map.init.latitude"));
    	model.addAttribute("value_lng",optService.getSiteOption(session, "map.init.longitude"));
    	
    	
    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
    	
        return "eco/rvm";
    }
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request,  
    		HttpSession session, HttpServletRequest req, HttpServletResponse res) {
    	String viewMode = request.getReqStrValue1();
    	boolean isEffectiveMode = false;
    	
    	if (Util.isValid(viewMode) && viewMode.equals("E")) {
    		isEffectiveMode = true;
    	}
    	
		if (session != null) {
			EcoUserCookie userCookie = (EcoUserCookie) session.getAttribute("userCookie");
			if (userCookie == null) {
				userCookie = new EcoUserCookie(req);
				session.setAttribute("userCookie", userCookie);
			}
			
			userCookie.setViewCodeRvm(isEffectiveMode ? "E" : "A", res);
		}

		try {

			return rvmService.getRvmList(request, isEffectiveMode);
			
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
	/**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody List<Rvm> destroy(@RequestBody Map<String, Object> model, 
    		Locale locale) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<Rvm> rvms = new ArrayList<Rvm>();
    	
    	for (Object id : objs) {
    		
    		Rvm rvm = new Rvm();
    		
    		rvm.setId((int)id);
    		
    		rvms.add(rvm);
    	}
    	
    	try {
        	rvmService.deleteRvms(rvms);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return rvms;
    }
	
	/**
	 * 추가/변경 액션
	 */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public @ResponseBody String edit(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
		Site site = siteService.getSite(Util.getSessionSiteId(session));
		
    	Integer id = (Integer)model.get("id");

    	String rvmName = Util.parseString((String)model.get("rvmName")); 
    	String deviceID = Util.parseString((String)model.get("deviceID"));
    	
    	     	String memo = Util.parseString((String)model.get("memo"));
    	      	//
    	      	String serialNo = Util.parseString((String)model.get("serialNo"));
    	      	String areaCode = Util.parseString((String)model.get("areaCode"));
    	      	String branchCode = Util.parseString((String)model.get("branchCode"));
    	      	String branchName = Util.parseString((String)model.get("branchName"));
    	      	//
    	      	String storeContact = Util.parseString((String)model.get("storeContact"));
    	      	String salesContact = Util.parseString((String)model.get("salesContact"));
    	      	String rvmLatitude = Util.parseString((String)model.get("rvmLatitude"));
    	      	String rvmLongitude = Util.parseString((String)model.get("rvmLongitude"));
    	      	//
    	
    	Date effectiveStartDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("effectiveStartDate")));
    	Date effectiveEndDate = Util.setMaxTimeOfDate(Util.parseZuluTime((String)model.get("effectiveEndDate")));

    	//
    	      	String serviceType = Util.parseString((String)model.get("serviceType"));
    	      	int reportInterval = Util.parseInt((String)model.get("reportInterval"), 1);
    	          boolean customerIdRequired = Util.parseBoolean((String)model.get("customerIdRequired"), false);
    	          boolean importRequired = Util.parseBoolean((String)model.get("importRequired"), true);
    	      	String resultType = Util.parseString((String)model.get("resultType"));
    	  
    	      	
    	    if (site == null || id == null || Util.isNotValid(rvmName)) {
        	throw new ServerOperationForbiddenException(msgMgr.message("common.server.msg.wrongParamError", locale));
    	}
    	
        if (!Util.isFloatNumber(rvmLatitude)) {
    		rvmLatitude = optService.getSiteOption(session, "map.init.latitude");
    	}
    	if (!Util.isFloatNumber(rvmLongitude)) {
    		rvmLongitude = optService.getSiteOption(session, "map.init.longitude");
    	}
    	

    	
    	Rvm target = null;
    	// RvmLastReport rvmLastReport = null;
    	
    	if (id.intValue() < 1) {
    		target = new Rvm(site, rvmName, effectiveStartDate, effectiveEndDate, serviceType, session);
    		target.setDeviceID(deviceID);
    		target.setMemo(memo);
        	target.setSerialNo(serialNo);
        	target.setAreaCode(areaCode);
        	target.setBranchCode(branchCode);
        	target.setBranchName(branchName);
        	target.setStoreContact(storeContact);
        	target.setSalesContact(salesContact);
        	target.setRvmLatitude(rvmLatitude);
        	target.setRvmLongitude(rvmLongitude);
        	target.setReportInterval(reportInterval);
        	target.setCustomerIdRequired(customerIdRequired);
        	target.setImportRequired(importRequired);
        	target.setResultType(resultType);
        	
    	} else {
    		target = rvmService.getRvm(id);
    		if (target != null) {

            	target.setRvmName(rvmName);
            	target.setDeviceID(deviceID);
            	target.setMemo(memo);
            	target.setSerialNo(serialNo);
            	target.setAreaCode(areaCode);
            	target.setBranchCode(branchCode);
            	target.setBranchName(branchName);
            	target.setStoreContact(storeContact);
            	target.setSalesContact(salesContact);
            	target.setRvmLatitude(rvmLatitude);
            	target.setRvmLongitude(rvmLongitude);
            	target.setEffectiveStartDate(effectiveStartDate);
            	target.setEffectiveEndDate(effectiveEndDate);
            	target.setServiceType(serviceType);

            	target.setReportInterval(reportInterval);
            	target.setCustomerIdRequired(customerIdRequired);
            	
            	target.setImportRequired(importRequired);
            	target.setResultType(resultType);
            	
                target.touchWho(session);
                
    		}
    	}
    	
    	if (target == null) {
        	throw new ServerOperationForbiddenException(msgMgr.message("common.server.msg.wrongParamError", locale));
    	} else {
            if (target.getEffectiveStartDate() != null && target.getEffectiveEndDate() != null
            		&& target.getEffectiveStartDate().after(target.getEffectiveEndDate())) {
            	throw new ServerOperationForbiddenException(msgMgr.message("common.server.msg.effectivedates", locale));
            }
            
            // 맥주소와 기기ID의 값으로 null이 입력될 수 있는데,
            // null이 있을 경우 DB의 UK로 처리할 수가 없어 애플리케이션 레벨에서 처리함
            if (Util.isValid(target.getDeviceID())) {
            	Rvm tmpRvm = rvmService.getRvm(target.getDeviceID());
            	if (tmpRvm != null && tmpRvm.getId() != target.getId()) {
            		throw new ServerOperationForbiddenException("동일한 기기ID의 자료가 이미 등록되어 있습니다.");
            	}
            }
            //-
            
            try {
                rvmService.saveOrUpdate(target);
                
//                if (rvmLastReport != null) {
//                	monService.saveOrUpdate(rvmLastReport);
//                }
            } catch (DataIntegrityViolationException dive) {
        		logger.error("saveOrUpdate", dive);
        		throw new ServerOperationForbiddenException("동일한 RVM명의 자료가 이미 등록되어 있습니다.");
            } catch (ConstraintViolationException cve) {
        		logger.error("saveOrUpdate", cve);
        		throw new ServerOperationForbiddenException("동일한 RVM명의 자료가 이미 등록되어 있습니다.");
            } catch (Exception e) {
        		logger.error("saveOrUpdate", e);
            	throw new ServerOperationForbiddenException("SaveError");
            }
            
    	}

        return "OK";
    }
    
	/**
  	 * 읽기 액션 - 보고 주기 정보
  	 */
      @RequestMapping(value = "/readReportIntervals", method = RequestMethod.POST)
      public @ResponseBody List<DropDownListItem> readReportIntervals(Locale locale) { 

		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("1분", "1"));
		
		  		list.add(new DropDownListItem("5분", "5"));
		  		list.add(new DropDownListItem("30분", "30"));
		  		list.add(new DropDownListItem("1시간", "60"));
		
		return list;
    }
    
}
