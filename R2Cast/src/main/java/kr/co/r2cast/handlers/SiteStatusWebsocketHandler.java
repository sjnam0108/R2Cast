package kr.co.r2cast.handlers;

import kr.co.r2cast.utils.WebSocketUtil;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class SiteStatusWebsocketHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session)
            throws Exception {
        super.afterConnectionEstablished(session);
        
        WebSocketUtil.addStatusWsSession(session);
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session,
            CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);

        WebSocketUtil.removeStatusWsSession(session);
    }
    
	@Override
	protected void handleTextMessage(WebSocketSession session,
			TextMessage message) throws Exception {
		super.handleTextMessage(session, message);
		
		WebSocketUtil.sendStatusMessage();
	}
}
