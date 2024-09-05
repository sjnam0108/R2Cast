package kr.co.r2cast.models;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import kr.co.r2cast.models.eco.service.MonitoringService;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.service.SiteService;
import kr.co.r2cast.utils.SolUtil;
import kr.co.r2cast.utils.Util;

@Component
public class EcoMessageManager {
    
	@Autowired
	private MessageSource messages;

    @Autowired 
    private SiteService siteService;

	@Autowired 
    private MonitoringService monService;

	@Autowired
	private MessageManager msgMgr;
	
	public EcoMessageManager() { }
	
	
	private String message(String code, Object[] args, Locale locale) {
		return messages.getMessage(code, args, code, locale);
	}
	
	private void addViewMessages(Model model, Locale locale, Message[] msgs) {
		if (msgs != null) {
			for(Message msg : msgs) {
				model.addAttribute(msg.getIdInView(), message(msg.getCode(), msg.getArgs(), locale));
			}
		}
	}
	
	
	public void addCommonMessages(Model model, Locale locale, HttpSession session, HttpServletRequest request) {

		msgMgr.addCommonMessages(model, locale, session, request);
		
    	// 현재의 뷰 모드가 사이트 전체 여부: 사이트 전체로 전달되는 소켓 자료 사용 여부 파악
    	model.addAttribute("isSiteLevelViewMode", monService.isSiteLevelViewMode(session));
	}
	
	public void addNocMessages(Model model, Locale locale) {
    	addViewMessages(model, locale,
    			new Message[] {
       				new Message("noc_titleTaskView", "noctaskview.titleTaskView"),
   					new Message("noc_playerCode", "noctaskview.playerCode"),
   					new Message("noc_stbName", "noctaskview.stbName"),
   					new Message("noc_taskType", "noctaskview.taskType"),
   					new Message("noc_status", "noctaskview.status"),
   					new Message("noc_regDate", "noctaskview.regDate"),
   					new Message("noc_register", "noctaskview.register"),
   					new Message("noc_desc", "noctaskview.desc"),
   					new Message("noc_visitDate", "noctaskview.visitDate"),
   					new Message("noc_urgent", "noctaskview.urgent"),
   					new Message("noc_nocTask", "noctaskview.nocTask"),
   					new Message("noc_assetHistory", "noctaskview.assetHistory"),
   					new Message("noc_taskId", "noctaskview.taskId"),
    			});
	}
}
