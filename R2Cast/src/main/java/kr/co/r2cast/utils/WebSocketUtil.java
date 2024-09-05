package kr.co.r2cast.utils;

import java.util.ArrayList;
import java.util.HashMap;

import kr.co.r2cast.viewmodels.eco.NocFailureDisplayItem;
import kr.co.r2cast.viewmodels.eco.RvmStatusItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
public class WebSocketUtil {
	private static final Logger logger = LoggerFactory.getLogger(WebSocketUtil.class);

	private static ArrayList<WebSocketSession> statusWsSessions = null;
	private static HashMap<Integer, RvmStatusItem> siteMap = new HashMap<Integer, RvmStatusItem>();
	
	public static ArrayList<WebSocketSession> getStatusWsInstance() {
		if (statusWsSessions == null) {
			statusWsSessions = new  ArrayList<WebSocketSession>();
		}
		
		return statusWsSessions;
	}
	
	public static void addStatusWsSession(WebSocketSession session) {
		getStatusWsInstance().add(session);
		
		Integer site = (Integer)session.getAttributes().get("siteId");
		if (site != null) {
			RvmStatusItem item = siteMap.get(site);
			if (item != null) {
				try {
					session.sendMessage(new TextMessage(item.toJSONString()));
				} catch (Exception e) {
					logger.error("addStatusWsSession", e);
				}
			}
		}

		//logger.info("[Add] Status web socket session count:" + getStatusWsInstance().size());
	}
	
	public static void removeStatusWsSession(WebSocketSession session) {
		getStatusWsInstance().remove(session);
		
		//logger.info("[Remove] Status web socket session count:" + 
		//		getStatusWsInstance().size());
	}
	
	public static void sendStatusMessage() {
		for (WebSocketSession client : getStatusWsInstance()) {
			Integer site = (Integer)client.getAttributes().get("site");
			RvmStatusItem item = siteMap.get(site);
			if (item != null) {
				try {
					client.sendMessage(new TextMessage(item.toJSONString()));
				} catch (Exception e) {
					logger.error("sendStatusMessage", e);
				}
			}
		}
		
		//logger.info("[Send] Status web socket session count:" + 
		//		getStatusWsInstance().size());
	}
	
	public static void sendStatusMessageBySiteId(int siteId) {
		if (siteId > 0) {
			ArrayList<WebSocketSession> list = getStatusWsInstance();
			for (WebSocketSession client : list) {
				Integer site = (Integer)client.getAttributes().get("siteId");
				if (site != null && siteId == site.intValue()) {
					RvmStatusItem item = siteMap.get(site);
					if (item != null) {
						try {
							if (client.isOpen()) {
								client.sendMessage(new TextMessage(item.toJSONString()));
							} else {
								logger.info("socket is closed(" + list.size() + ")");
							}
						} catch (Exception e) {
							logger.error("sendStatusMessageBySite", e);
						}
					}
				}
			}
			
			//logger.info("[Send] Site status web socket session count:" + cnt);
		}
	}
	
	public static void setSiteRvmStatus(int siteId, int status6, int status5,
			int status4, int status3, int status2, int status0) {
		RvmStatusItem item = new RvmStatusItem(status6, status5, status4,
				status3, status2, status0);
		
		if (siteId > 0) {
			siteMap.put(siteId, item);
			sendStatusMessageBySiteId(siteId);
		}
	}
	
}
