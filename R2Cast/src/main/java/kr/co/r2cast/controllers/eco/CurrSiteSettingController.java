package kr.co.r2cast.controllers.eco;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import kr.co.r2cast.exceptions.ServerOperationForbiddenException;
import kr.co.r2cast.models.EcoMessageManager;
import kr.co.r2cast.models.Message;
import kr.co.r2cast.models.MessageManager;
import kr.co.r2cast.models.ModelManager;
import kr.co.r2cast.models.eco.SiteOpt;
import kr.co.r2cast.models.eco.service.OptService;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.service.SiteService;
import kr.co.r2cast.utils.SolUtil;
import kr.co.r2cast.utils.Util;
import kr.co.r2cast.viewmodels.DropDownListItem;
import kr.co.r2cast.viewmodels.eco.OptionItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 현재 사이트 설정 컨트롤러
 */
@Controller("eco-currsite-setting-controller")
@RequestMapping(value="/eco/currsitesetting")
public class CurrSiteSettingController {
	private static final Logger logger = LoggerFactory.getLogger(CurrSiteSettingController.class);

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
	 * 현재 사이트 설정 페이지
	 */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String index(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	modelMgr.addMainMenuModel(model, locale, session, request);
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {
					new Message("pageTitle", "currsitesetting.title"),
    				new Message("tab_general", "currsitesetting.general"),
    				new Message("tab_location", "currsitesetting.location"),
    				new Message("tab_monitoring", "currsitesetting.monitoring"),
    				new Message("tab_asset", "currsitesetting.asset"),
    				new Message("tab_advanced", "currsitesetting.advanced"),
					new Message("title_editionCode", "currsitesetting.editioncode.title"),
					new Message("desc_editionCode", "currsitesetting.editioncode.desc"),
					new Message("item_editionEE", "currsitesetting.editioncode.dropItemEE"),
					new Message("item_editionCU", "currsitesetting.editioncode.dropItemCU"),
					new Message("item_editionDT", "currsitesetting.editioncode.dropItemDT"),
					new Message("title_autoSiteUser", "currsitesetting.autositeuser.title"),
					new Message("desc_autoSiteUser", "currsitesetting.autositeuser.desc"),
					new Message("title_autoRvmReg", "currsitesetting.autoRvmReg.title"),
					new Message("desc_autoRvmReg", "currsitesetting.autoRvmReg.desc"),
					new Message("title_siteLoc", "currsitesetting.siteLoc.title"),
					new Message("desc_siteLoc", "currsitesetting.siteLoc.desc"),
					new Message("title_rvmGroupOrder", "currsitesetting.rvmgrouporder.title"),
					new Message("desc_rvmGroupOrder", "currsitesetting.rvmgrouporder.desc"),
					new Message("item_orderColor", "currsitesetting.rvmgrouporder.dropItemColor"),
					new Message("item_orderGroup", "currsitesetting.rvmgrouporder.dropItemGroup"),
					new Message("title_routinedSchdPrefix", "currsitesetting.routinedSchdPrefix.title"),
					new Message("desc_routinedSchdPrefix", "currsitesetting.routinedSchdPrefix.desc"),
					new Message("desc_routinedSchdPrefixEx", "currsitesetting.routinedSchdPrefix.ex"),
					new Message("title_autoRvmStatus", "currsitesetting.autorvmstatus.title"),
					new Message("desc_autoRvmStatus", "currsitesetting.autorvmstatus.desc"),
					new Message("title_playerIdPrefix", "currsitesetting.playerIdPrefix.title"),
					new Message("desc_playerIdPrefix", "currsitesetting.playerIdPrefix.desc"),
					new Message("title_playerIdDigit", "currsitesetting.playerIdDigit.title"),
					new Message("desc_playerIdDigit", "currsitesetting.playerIdDigit.desc"),
					new Message("title_rvmModelList", "currsitesetting.rvmModelList.title"),
					new Message("desc_rvmModelList", "currsitesetting.rvmModelList.desc"),
					new Message("title_siteLogo", "currsitesetting.siteLogo.title"),
					new Message("desc_siteLogo", "currsitesetting.siteLogo.desc"),
					new Message("label_allSite", "currsitesetting.siteLogo.allSite"),
					new Message("label_currSite", "currsitesetting.siteLogo.currSite"),
					new Message("label_currSetting", "currsitesetting.siteLogo.currSetting"),
					new Message("label_serviceURL", "currsitesetting.siteLogo.serviceURL"),
					new Message("label_logoFile", "currsitesetting.siteLogo.logoFile"),
					new Message("label_login", "currsitesetting.siteLogo.login"),
					new Message("label_slide", "currsitesetting.siteLogo.slide"),
					new Message("label_top", "currsitesetting.siteLogo.top"),
					new Message("label_logoText", "currsitesetting.siteLogo.text"),
					new Message("tip_changeable", "currsitesetting.siteLogo.changeable"),
					new Message("title_applyAssetModule", "currsitesetting.applyAssetModule.title"),
					new Message("desc_applyAssetModule", "currsitesetting.applyAssetModule.desc"),
					new Message("title_assetSiteID", "currsitesetting.assetSiteID.title"),
					new Message("desc_assetSiteID", "currsitesetting.assetSiteID.desc"),
					new Message("title_assetIdDigit", "currsitesetting.assetIdDigit.title"),
					new Message("desc_assetIdDigit", "currsitesetting.assetIdDigit.desc"),
					new Message("title_allowedSiteIDList", "currsitesetting.allowedSiteIDList.title"),
					new Message("desc_allowedSiteIDList", "currsitesetting.allowedSiteIDList.desc"),
					new Message("title_maxPlayerAsset", "currsitesetting.maxPlayerAsset.title"),
					new Message("desc_maxPlayerAsset", "currsitesetting.maxPlayerAsset.desc"),
					new Message("item_rvm", "currsitesetting.maxPlayerAsset.rvm"),
					new Message("item_display", "currsitesetting.maxPlayerAsset.display"),
					new Message("item_noLimit", "currsitesetting.maxPlayerAsset.noLimit"),
					new Message("title_maxQuickLinkMenu", "currsitesetting.maxQuickLinkMenu.title"),
					new Message("desc_maxQuickLinkMenu", "currsitesetting.maxQuickLinkMenu.desc"),
					new Message("title_failureControlMode", "currsitesetting.failureControlMode.title"),
					new Message("desc_failureControlMode", "currsitesetting.failureControlMode.desc"),
					new Message("title_failureControlPeriod", "currsitesetting.failureControlPeriod.title"),
					new Message("desc_failureControlPeriod", "currsitesetting.failureControlPeriod.desc"),
					new Message("label_start", "currsitesetting.failureControlPeriod.start"),
					new Message("label_end", "currsitesetting.failureControlPeriod.end"),
					new Message("title_failureReg", "currsitesetting.failureReg.title"),
					new Message("desc_failureReg", "currsitesetting.failureReg.desc"),
					new Message("title_failureRemoval", "currsitesetting.failureRemoval.title"),
					new Message("desc_failureRemoval", "currsitesetting.failureRemoval.desc"),
					new Message("title_autoSchdDeploy", "currsitesetting.autoSchdDeploy.title"),
					new Message("desc_autoSchdDeploy", "currsitesetting.autoSchdDeploy.desc"),
					new Message("title_advFtpServer", "currsitesetting.advFtpServer.title"),
					new Message("desc_advFtpServer", "currsitesetting.advFtpServer.desc"),
					
					new Message("msg_updateComplete", "currsitesetting.msg.updateComplete"),
    			});
    	
    	// 사이트 로고 관련 모델값 설정
    	String logoDomainName = Util.getFileProperty("logo.domain.name");
    	String currentSiteUrl = "-";
    	String siteShortName = "[siteID]";
//    	System.out.println(logoDomainName);
//    	System.out.println(currentSiteUrl);
//    	System.out.println(siteShortName);
    	Site site = siteService.getSite(Util.getSessionSiteId(session));
    	if (site != null) {
    		siteShortName = site.getShortName();

    		if (Util.isValid(logoDomainName)) {
    			currentSiteUrl = site.getShortName() + "." + logoDomainName;
        	}
    	}
    	
    	model.addAttribute("globalCustLogoDisplayed", Util.getBooleanFileProperty("custom.logo.displayed", false));
    	model.addAttribute("currCustLogoDisplayed", Util.isValid(logoDomainName));
    	
    	model.addAttribute("currSiteLogoServiceURL", currentSiteUrl);
    	model.addAttribute("siteShortName", siteShortName);
    	
    	model.addAttribute("logoFileLogin", Util.getLogoPathFile("login", request.getServerName()));
    	model.addAttribute("logoFileTop", Util.getLogoPathFile("top", request.getServerName()));
    	//-
    	
    	model.addAttribute("Editions", getEditionDropDownList(locale));
    	
        return "eco/currsitesetting";
    }

	/**
	 * 읽기 액션
	 */
	@RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody List<OptionItem> read(Locale locale, HttpSession session) {
		return optService.getCurrentSiteOptions(Util.getSessionSiteId(session), locale);
    }
    
	/**
	 * 개별 옵션 항목의 값 변경
	 */
	private List<OptionItem> updateSiteOption(List<OptionItem> items, String name, String value) {
		if (items == null || items.size() == 0) {
			return items;
		}
		
		for(OptionItem item : items) {
			if (item.getName().equals(name)) {
				item.setValue(Util.isNotValid(value) ? "" : value);
			}
		}
//		System.out.println(items);
		return items;
	}
	
	/**
	 * 변경 액션
	 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody String update(@RequestBody Map<String, Object> model, Locale locale, 
    		HttpSession session) {
		Site site = siteService.getSite(Util.getSessionSiteId(session));
		if (site == null) {
    		throw new ServerOperationForbiddenException(
    				msgMgr.message("common.server.msg.wrongParamError", locale));
		}
		
		String routineSchedPrefix = (String)model.get("routineSchedPrefix");

		routineSchedPrefix = Util.isNotValid(routineSchedPrefix) ? "" : routineSchedPrefix;
		System.out.println(routineSchedPrefix);
		List<OptionItem> siteOptions = optService.getCurrentSiteOptions(site.getId(), locale);

		// 전달된 옵션 정리(추가 혹은 변경 대상)
		
		//
		// [Key]: [paramName]
		//
		// map.init.latitude: mapLateg
		// map.init.longitude: mapLng
		// auto.siteUser: autoSiteUser
		// edition.code: editionCode
		// stbGroup.order: stbGroupOrder
		// auto.rvmStatus: autoRvmStatus
		// playerID.prefix: playerIdPrefix
		// playerID.digit: playerIdDigit
		// stb.model.list: stbModelList
		// asset.enabled: applyAssetModule
		// assetID.digit: assetIdDigit
		// asset.allowed.siteID.list: allowedSiteIdList
		// asset.siteID: assetSiteID
		// playerasset.max.stb: maxPlayerAssetStb
		// playerasset.max.display: maxPlayerAssetDisplay
		// quicklink.max.menu: maxMenuCount
		// logo.title: logoTitle
		// failureControl.enabled: failureControlMode
		// failureControl.startTime: failureControlStart
		// failureControl.endTime: failureControlEnd
		// failureControl.regMins: failureControlRegMins
		// failureControl.removalAllowed: failureControlRemovable
		// auto.schdDeploy: autoSchdDeploy
		// ftpServer.advConfig: advFtpServer
    	siteOptions = updateSiteOption(siteOptions, "map.init.latitude", (String)model.get("mapLat"));
    	siteOptions = updateSiteOption(siteOptions, "map.init.longitude", (String)model.get("mapLng"));
    	siteOptions = updateSiteOption(siteOptions, "auto.rvmReg", (String)model.get("autoRvmReg"));
    	siteOptions = updateSiteOption(siteOptions, "auto.siteUser", (String)model.get("autoSiteUser"));
    	siteOptions = updateSiteOption(siteOptions, "edition.code", (String)model.get("editionCode"));
    	siteOptions = updateSiteOption(siteOptions, "rvmGroup.order", (String)model.get("rvmGroupOrder"));
    	siteOptions = updateSiteOption(siteOptions, "routineSched.prefix", routineSchedPrefix);
    	siteOptions = updateSiteOption(siteOptions, "auto.rvmStatus", (String)model.get("autoRvmStatus"));
    	siteOptions = updateSiteOption(siteOptions, "playerID.prefix", (String)model.get("playerIdPrefix"));
    	siteOptions = updateSiteOption(siteOptions, "playerID.digit", (String)model.get("playerIdDigit"));
    	siteOptions = updateSiteOption(siteOptions, "rvm.model.list", (String)model.get("rvmModelList"));
    	siteOptions = updateSiteOption(siteOptions, "asset.enabled", (String)model.get("applyAssetModule"));
    	siteOptions = updateSiteOption(siteOptions, "assetID.digit", (String)model.get("assetIdDigit"));
    	siteOptions = updateSiteOption(siteOptions, "asset.allowed.siteID.list", (String)model.get("allowedSiteIDList"));
    	siteOptions = updateSiteOption(siteOptions, "asset.siteID", (String)model.get("assetSiteID"));
    	siteOptions = updateSiteOption(siteOptions, "playerasset.max.rvm", (String)model.get("maxPlayerAssetRvm"));
    	siteOptions = updateSiteOption(siteOptions, "playerasset.max.display", (String)model.get("maxPlayerAssetDisplay"));
    	siteOptions = updateSiteOption(siteOptions, "quicklink.max.menu", (String)model.get("maxMenuCount"));
    	siteOptions = updateSiteOption(siteOptions, "logo.title", 
    			Util.parseString((String)model.get("logoTitle"), Util.getFileProperty("logo.title", null, "R2Cast")) );
    	siteOptions = updateSiteOption(siteOptions, "failureControl.enabled", (String)model.get("failureControlMode"));
    	siteOptions = updateSiteOption(siteOptions, "failureControl.startTime", (String)model.get("failureControlStart"));
    	siteOptions = updateSiteOption(siteOptions, "failureControl.endTime", (String)model.get("failureControlEnd"));
    	siteOptions = updateSiteOption(siteOptions, "failureControl.regMins", (String)model.get("failureControlRegMins"));
    	siteOptions = updateSiteOption(siteOptions, "failureControl.removalAllowed", (String)model.get("failureControlRemovable"));
    	siteOptions = updateSiteOption(siteOptions, "auto.schdDeploy", (String)model.get("autoSchdDeploy"));
    	siteOptions = updateSiteOption(siteOptions, "ftpServer.advConfig", (String)model.get("advFtpServer"));
    	System.out.println(model);
		ArrayList<SiteOpt> items = new ArrayList<SiteOpt>();
		for(OptionItem item : siteOptions) {
			SiteOpt siteOpt = optService.getSiteOptByOptNameSiteId(item.getName(), site.getId());
			
			if (siteOpt == null) {
				siteOpt = new SiteOpt(site, item.getName(), item.getValue(), session);
			} else {
				siteOpt.setOptValue(item.getValue());
				siteOpt.touchWho(session);
			}
			
			items.add(siteOpt);
		}
    	
		// 저장된 옵션 검토(삭제 대상 파악)
		List<SiteOpt> dbOptions = optService.getSiteOptListBySiteId(site.getId());
		ArrayList<SiteOpt> delOptions = new ArrayList<SiteOpt>();
		
		for(SiteOpt dbSiteOpt : dbOptions) {
			boolean exists = false;
			for(SiteOpt siteOpt : items) {
				if (siteOpt.getOptName().equals(dbSiteOpt.getOptName())) {
					exists = true;
					break;
				}
			}
			
			if (!exists) {
				delOptions.add(dbSiteOpt);
			}
		}
		
		try {
			// 삭제 대상 자료 삭제
			optService.deleteSiteOpts(delOptions);
			
			// 추가, 변경 대상 자료 저장
			for (SiteOpt siteOpt : items) {
				optService.saveOrUpdate(siteOpt);
			}
			
			// 메모리에 있는 기존 값을 지움
			SolUtil.clearSiteOption(site.getId());
		} catch (Exception e) {
    		logger.error("update", e);
    		throw new ServerOperationForbiddenException("SaveError");
    	}

        return "OK";
    }
    
	/**
	 * 허용된 Edition 목록 획득
	 */
    public List<DropDownListItem> getEditionDropDownList(Locale locale) {
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		List<String> edList = Util.tokenizeValidStr(Util.getFileProperty("edition.list"));
		for (String ed : edList) {
			if (Util.isValid(ed) && ed.length() == 2) {
				String key = "currsitesetting.editioncode.dropItem" + ed;
				String text = msgMgr.message(key, locale);
				if (Util.isValid(text) && !key.equals(text)) {
					list.add(new DropDownListItem(text, ed));
				}
			}
		}

		if (list.size() == 0) {
			list.add(new DropDownListItem(msgMgr.message("currsitesetting.editioncode.dropItemEE", 
					locale), "EE"));
		}
		
		return list;
    }
}
