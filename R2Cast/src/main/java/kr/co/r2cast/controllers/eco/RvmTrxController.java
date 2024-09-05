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
import kr.co.r2cast.models.eco.RvmTrx;
import kr.co.r2cast.models.eco.service.TrxService;
import kr.co.r2cast.utils.Util;

/**
 * RVM 트랜잭션 컨트롤러
 */
@Controller("eco-rvm-trx-controller")
@RequestMapping(value="/eco/rvmtrx")
public class RvmTrxController {
	private static final Logger logger = LoggerFactory.getLogger(RvmTrxController.class);

    @Autowired 
    private TrxService trxService;
    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private EcoMessageManager solMsgMgr;

	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * RVM 트랜잭션 페이지
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
    	model.addAttribute("pageTitle", "RVM 트랜잭션");

    	
    	int trxId = Util.parseInt(request.getParameter("trxid"));
    	if (trxId > 0) {
    		model.addAttribute("initFilterApplied", true);
    		model.addAttribute("trxId", trxId);
    	}
    	
    	
    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
    	
        return "eco/rvmtrx";
    }
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request, 
    		Locale locale) {
    	try {
    		DataSourceResult result = trxService.getRvmTrxList(request);
    		
    		for(Object obj : result.getData()) {
    			RvmTrx trx = (RvmTrx)obj;
    			
    			trx.setGroupCount(trxService.getRvmTrxItemCountByTrxId(trx.getId()));
    		}
    		
    		return result;
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
    	
    	List<RvmTrx> trxs = new ArrayList<RvmTrx>();
    	
    	for (Object id : objs) {
    		RvmTrx trx = new RvmTrx();
    		
    		trx.setId((int)id);
    		
    		trxs.add(trx);
    	}
    	
    	try {
        	trxService.deleteRvmTrxs(trxs);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }
}
