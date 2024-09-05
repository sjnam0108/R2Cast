package kr.co.r2cast.controllers.eco;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
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
import kr.co.r2cast.models.EcoMessageManager;
import kr.co.r2cast.models.Message;
import kr.co.r2cast.models.MessageManager;
import kr.co.r2cast.models.ModelManager;
import kr.co.r2cast.models.eco.MonTask;
import kr.co.r2cast.models.eco.Rvm;
import kr.co.r2cast.models.eco.RvmTrx;
//import kr.co.r2cast.models.eco.service.BasService;
import kr.co.r2cast.models.eco.service.MonitoringService;
import kr.co.r2cast.models.eco.service.RvmService;
import kr.co.r2cast.models.eco.service.TrxService;
import kr.co.r2cast.utils.Util;

/**
 * 그리드 뷰 컨트롤러
 */
@Controller("eco-mon-grid-view-controller")
@RequestMapping(value="/eco/mongridview")
public class MonGridViewController {
	private static final Logger logger = LoggerFactory.getLogger(MonGridViewController.class);

    @Autowired 
    private MonitoringService monService;

    @Autowired 
    private RvmService rvmService;

    @Autowired 
    private TrxService trxService;

    @Autowired
	private MessageManager msgMgr;

	@Autowired
	private EcoMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	/**
	 * 그리드 뷰 페이지
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
    	model.addAttribute("pageTitle", "그리드 뷰");

    	
    	model.addAttribute("value_defaultStatus", Util.parseString(request.getParameter("status")));

    	
    	model.addAttribute("isViewSwitcherMode", true);
    	

    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
    	
    	return "eco/mongridview";
    }
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request, 
    		HttpSession session) {
    	
    	try {
    		DataSourceResult result = monService.getMonitoringRvmList(request, session, true, true);
    		
    		for(Object obj : result.getData()) {
    			Rvm rvm = (Rvm)obj;
    			
				RvmTrx rvmTrx = trxService.getRvmTrx(rvm, 
						trxService.getRvmTrxMaxOpDateByRvmId(rvm.getId()));
				if (rvmTrx != null) {
					rvm.setLastOpDt(Util.toSimpleString(rvmTrx.getOpDt(), "yyyy-MM-dd ") + 
							"<small>" + Util.toSimpleString(rvmTrx.getOpDt(), "HH:mm:ss") + "</small>");
					rvm.setLastTrxNo(new DecimalFormat("###,###,##0").format(rvmTrx.getTrxNo()));
					rvm.setLastReceiptNo(new DecimalFormat("###,##0").format(rvmTrx.getReceiptNo()));
				}
    			
				Object[] sumTrxObjs = trxService.getSumRvmTrxDataByRvmId(rvm.getId());
				if (sumTrxObjs != null) {
					Long trxCount = (Long) sumTrxObjs[0];
					Long receiptAmount = (Long) sumTrxObjs[1];
					
					if (trxCount != null && trxCount.longValue() != 0) {
						rvm.setCumTrxCount(new DecimalFormat("###,###,##0").format(trxCount.longValue()));
					}
					if (receiptAmount != null && receiptAmount.longValue() != 0) {
						rvm.setCumReceiptAmount(new DecimalFormat("###,###,###,##0").format(receiptAmount.longValue()));
					}
				}
    			
				Object[] sumTrxItemObjs = trxService.getSumRvmTrxItemDataByRvmId(rvm.getId());
				if (sumTrxItemObjs != null) {
					Long emptiesCount = (Long) sumTrxItemObjs[0];
					
					if (emptiesCount != null && emptiesCount.longValue() != 0) {
						rvm.setCumEmptiesCount(new DecimalFormat("###,###,##0").format(emptiesCount.longValue()));
					}
				}
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
	/**
	 * 추가 액션
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody String create(@RequestBody Map<String, Object> model, Locale locale, 
    		HttpSession session) {
    	String command = (String)model.get("command");
    	String execTime = (String)model.get("execTime");

    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("rvmIds");
    	
    	Date destDate = Util.parseZuluTime((String)model.get("destDate"));
    	Date cancelDate = Util.parseZuluTime((String)model.get("cancelDate"));
		
		ArrayList<Integer> rvmIds = new ArrayList<Integer>();
    	for (Object rvmIdObj : objs) {
    		rvmIds.add((int)rvmIdObj);
    	}

    	if (Util.isNotValid(command) || rvmIds.size() == 0 || destDate == null || cancelDate == null) {
    		throw new ServerOperationForbiddenException(
    				msgMgr.message("common.server.msg.wrongParamError", locale));
    	}
    	
		try {
	    	for (int i : rvmIds) {
	    		Rvm rvm = rvmService.getRvm(i);
	    		if (rvm != null) {
	    			monService.saveOrUpdate(new MonTask(rvm.getSite(), rvm, command, "", execTime, "1", 
	    					destDate, cancelDate, null, null, session));
	    			
	    			monService.checkRvmRemoteControlTypeAndLastReportTime(rvm, command);
	    		}
	    	}
		} catch (Exception e) {
    		logger.error("create", e);
    		throw new ServerOperationForbiddenException("SaveError");
    	}
    	
    	return "OK";
    }
}
