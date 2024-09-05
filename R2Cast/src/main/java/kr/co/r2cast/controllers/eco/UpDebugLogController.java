package kr.co.r2cast.controllers.eco;

import java.io.File;
import java.io.FilenameFilter;
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
import kr.co.r2cast.models.EcoMessageManager;
import kr.co.r2cast.models.Message;
import kr.co.r2cast.models.MessageManager;
import kr.co.r2cast.models.ModelManager;
import kr.co.r2cast.models.eco.Rvm;
import kr.co.r2cast.models.eco.service.RvmService;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.service.SiteService;
import kr.co.r2cast.utils.SolUtil;
import kr.co.r2cast.utils.Util;
import kr.co.r2cast.viewmodels.eco.UploadFileItem;

/**
 * 디버그 로그 파일 컨트롤러
 */
@Controller("dsg-up-debug-log-controller")
@RequestMapping(value="/eco/updebuglog")
public class UpDebugLogController {
	private static final Logger logger = LoggerFactory.getLogger(UpDebugLogController.class);

    @Autowired 
    private SiteService siteService;

    @Autowired 
    private RvmService rvmService;

	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private EcoMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	/**
	 * 디버그 로그 파일 페이지
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
    	model.addAttribute("pageTitle", "디버그 로그 파일");
    	
    	
    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
    	
        return "eco/updebuglog";
    }
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody List<UploadFileItem> read(@RequestBody DataSourceRequest request, 
    		HttpSession session, Locale locale) {
    	
    	Site site = siteService.getSite(Util.getSessionSiteId(session));
    	ArrayList<UploadFileItem> list = new ArrayList<UploadFileItem>();
		
		if (site == null) {
    		throw new ServerOperationForbiddenException(msgMgr.message("common.server.msg.wrongParamError", locale));
		} else {
	    	try {
				String typeRootDir = SolUtil.getPhysicalRoot("Debug");
				
				File dir = new File(typeRootDir);
				final String extName = SolUtil.getFileSearchPattern("Debug");
				
				if (dir.exists() && dir.isDirectory()) {
					File[] files = dir.listFiles(new FilenameFilter() {
						
						@Override
						public boolean accept(File dir, String name) {
							return extName.equals("*") ||  name.toLowerCase().endsWith(extName);
						}
					});
					
					String prefix = site.getShortName() + "_debug_";
					
					//
					// 업로드 파일 형식: {siteShortName}_debug_{rvmId}_{대상일:yyyyMMdd}.log.txt
					// 형식의 예: oasis_debug_37_20131030.log.txt
					//
					for(File file : files) {
						if (file.getName().startsWith(prefix)) {
							String remain = file.getName().substring(prefix.length()).replace(".log.txt", "");
							if (remain.length() > 9 && remain.indexOf("_") > 0) {
								String rvmIdStr = remain.substring(0, remain.indexOf("_"));
								String dateStr = remain.substring(remain.indexOf("_") + 1);
								
								if (Util.isIntNumber(rvmIdStr) && dateStr.length() == 8) {
									Rvm rvm = rvmService.getRvm(Integer.parseInt(rvmIdStr));
									
									if (rvm != null) {
										list.add(new UploadFileItem(file.getName(), file.length(), file.lastModified(), 
												rvm.getRvmName(), Util.delimitDateStr(dateStr),
												rvm.getId()));
									}
								}
							}
						}
					}
				}
	    	} catch (Exception e) {
	    		logger.error("read", e);
	    		throw new ServerOperationForbiddenException("readError");
	    	}
		}
		
		return list;
    }
    
	/**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model, Locale locale) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
		try {
			String rootDir = SolUtil.getPhysicalRoot("Debug");
			
	    	for (Object pathName : objs) {
	    		File file = new File(rootDir + ((String) pathName));
	
	    		if (file.exists() && file.isFile()) {
	    			file.delete();
	    		}
	    	}
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "OK";
    }
}
