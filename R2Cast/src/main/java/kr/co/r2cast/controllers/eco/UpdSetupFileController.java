package kr.co.r2cast.controllers.eco;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
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
import kr.co.r2cast.models.UploadTransitionModel;
import kr.co.r2cast.models.eco.UpdSetupFile;
import kr.co.r2cast.models.eco.service.UpdService;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.service.SiteService;
import kr.co.r2cast.utils.SolUtil;
import kr.co.r2cast.utils.Util;

/**
 * 설치 파일 컨트롤러
 */
@Controller("eco-upd-setup-file-controller")
@RequestMapping(value="/eco/updsetupfile")
public class UpdSetupFileController {
	private static final Logger logger = LoggerFactory.getLogger(UpdSetupFileController.class);

    @Autowired 
    private UpdService updService;
    
    @Autowired 
    private SiteService siteService;
    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private EcoMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	/**
	 * 설치 파일 페이지
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
    	model.addAttribute("pageTitle", "설치 파일");

    	
    	UploadTransitionModel uploadModel = new UploadTransitionModel();

    	Site site = siteService.getSite(Util.getSessionSiteId(session));

		uploadModel.setSiteId(site == null ? -1 : site.getId());
		uploadModel.setType("SETUP");
		uploadModel.setSaveUrl("/eco/common/uploadsave");
		uploadModel.setAllowedExtensions("[\".apk\"]");
		
    	model.addAttribute("uploadModel", uploadModel);

    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
    	
        return "eco/updsetupfile";
    }
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request) {
    	try {
            return updService.getUpdSetupFileList(request);
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
	/**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model,
    		Locale locale) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	String rootDir = SolUtil.getPhysicalRoot("Setup");
    	List<UpdSetupFile> setupFiles = new ArrayList<UpdSetupFile>();
    	
    	try {
        	for (Object id : objs) {
        		int setupFileId = (Integer)id;
        		
        		UpdSetupFile oldFile = updService.getUpdSetupFile(setupFileId);
        		if (oldFile != null) {
        			File file = new File(rootDir + File.separator + oldFile.getFilename());
        			
    	    		if (file.exists() && file.isFile()) {
    	    			file.delete();
    	    		}
        		}
        		
        		UpdSetupFile setupFile = new UpdSetupFile();
        		setupFile.setId(setupFileId);
        		
        		setupFiles.add(setupFile);
        	}
        	
        	updService.deleteUpdSetupFiles(setupFiles);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "OK";
    }
    
	/**
	 * 변경 액션
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody String update(@RequestBody Map<String, Object> model, Locale locale, 
    		HttpSession session) {
    	Integer id  = (Integer)model.get("id");
    	String published = (String)model.get("published");
    	String fileType = (String)model.get("fileType");
    	String descEng = (String)model.get("descEng");
    	String descLocal = (String)model.get("descLocal");

    	Date releaseDate = Util.parseZuluTime((String)model.get("releaseDate"));

    	if (Util.isNotValid(published) || Util.isNotValid(fileType) ||
    			Util.isNotValid(descEng) || id == null) {
    		throw new ServerOperationForbiddenException(
    				msgMgr.message("common.server.msg.wrongParamError", locale));
    	}
    	
    	UpdSetupFile target = updService.getUpdSetupFile(id);
    	if (target == null) {
    		throw new ServerOperationForbiddenException(
    				msgMgr.message("common.server.msg.wrongParamError", locale));
    	}
    	
    	if (Util.isNotValid(descLocal)) {
    		descLocal = "";
    	}
    	if (releaseDate == null) {
    		releaseDate = new Date();
    	}
    	
    	target.setPublished(published);
    	target.setFileType(fileType);
    	target.setDescEng(descEng);
    	target.setDescLocal(descLocal);
    	target.setReleaseDate(releaseDate);
		
		target.touchWho(session);
    	
		try {
			updService.saveOrUpdate(target);
		} catch (Exception e) {
    		logger.error("update", e);
    		throw new ServerOperationForbiddenException("SaveError");
    	}
    	
    	return "OK";
	}
    
	/**
	 * 추가 액션
	 */
	@RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody String create(@RequestBody Map<String, Object> model, Locale locale, 
    		HttpSession session) {
    	String filename = (String)model.get("filename");
    	String published = (String)model.get("published");
    	String fileType = (String)model.get("fileType");
    	String descEng = (String)model.get("descEng");
    	String descLocal = (String)model.get("descLocal");

    	if (Util.isNotValid(published) || Util.isNotValid(fileType) ||
    			Util.isNotValid(descEng) || Util.isNotValid(filename)) {
    		throw new ServerOperationForbiddenException(
    				msgMgr.message("common.server.msg.wrongParamError", locale));
    	}
    	
    	if (Util.isNotValid(descLocal)) {
    		descLocal = "";
    	}

		String rootDir = SolUtil.getPhysicalRoot("Setup");
		File file = new File(rootDir + File.separator + filename);
		if (!file.exists()) {
    		throw new ServerOperationForbiddenException(
    				msgMgr.message("common.server.msg.wrongParamError", locale));
		}
		
		try {
			UpdSetupFile target = new UpdSetupFile(filename, file.length(), fileType, 
					Util.getDate(file.lastModified()), descEng, descLocal, published, session);
			
			updService.saveOrUpdate(target);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("create", dive);
        	throw new ServerOperationForbiddenException("동일한 설치 파일 이름의 자료가 이미 등록되어 있습니다.");
        } catch (ConstraintViolationException cve) {
    		logger.error("create", cve);
        	throw new ServerOperationForbiddenException("동일한 설치 파일 이름의 자료가 이미 등록되어 있습니다.");
		} catch (Exception e) {
    		logger.error("create", e);
    		throw new ServerOperationForbiddenException("SaveError");
    	}
		
    	return "OK";
    }
}