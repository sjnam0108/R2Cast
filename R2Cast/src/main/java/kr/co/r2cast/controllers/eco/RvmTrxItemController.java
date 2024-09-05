package kr.co.r2cast.controllers.eco;

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
import kr.co.r2cast.models.EcoMessageManager;
import kr.co.r2cast.models.Message;
import kr.co.r2cast.models.MessageManager;
import kr.co.r2cast.models.ModelManager;
import kr.co.r2cast.models.eco.RvmTrxItem;
import kr.co.r2cast.models.eco.service.TrxService;
import kr.co.r2cast.utils.Util;
import kr.co.r2cast.viewmodels.DropDownListItem;

/**
 * RVM 트랜잭션 항목 컨트롤러
 */
@Controller("eco-rvm-trx-item-controller")
@RequestMapping(value="/eco/rvmtrxitem")
public class RvmTrxItemController {
	private static final Logger logger = LoggerFactory.getLogger(RvmTrxItemController.class);

    @Autowired 
    private TrxService trxService;
    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private EcoMessageManager solMsgMgr;

	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * RVM 트랜잭션 항목 페이지
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
    	model.addAttribute("pageTitle", "RVM 트랜잭션 항목");

    	
    	int trxId = Util.parseInt(request.getParameter("trxid"));
    	if (trxId > 0) {
    		model.addAttribute("initFilterApplied", true);
    		model.addAttribute("trxId", trxId);
    	}
    	
    	
    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
    	
        return "eco/rvmtrxitem";
    }
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request, 
    		Locale locale) {
    	try {
    		return trxService.getRvmTrxItemList(request);
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("readError");
    	}
    }
    
	/**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<RvmTrxItem> items = new ArrayList<RvmTrxItem>();
    	
    	for (Object id : objs) {
    		RvmTrxItem item = new RvmTrxItem();
    		
    		item.setId((int)id);
    		
    		items.add(item);
    	}
    	
    	try {
        	trxService.deleteRvmTrxItems(items);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }
    
	/**
  	 * 읽기 액션 - 리필 유형 정보
  	 */
      @RequestMapping(value = "/readTypes", method = RequestMethod.POST)
      public @ResponseBody List<DropDownListItem> readReportIntervals(Locale locale) { 

		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-bottle-water text-blue fa-fw", "0: Refillable", "0"));
  		list.add(new DropDownListItem("fa-regular fa-bottle-water text-muted fa-fw", "1: Non-refillable", "1"));
		
		return list;
    }
}