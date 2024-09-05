package kr.co.r2cast.controllers;

import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import kr.co.r2cast.info.GlobalInfo;
import kr.co.r2cast.utils.Util;

@Component
public class StartupHouseKeeper implements ApplicationListener<ContextRefreshedEvent> {
	private static final Logger logger = LoggerFactory.getLogger(StartupHouseKeeper.class);

	private static Timer bgKeyGenTimer;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		String appId = event.getApplicationContext().getId();
		logger.info("Enter onApplicationEvent() - id=" + appId);
		
		if (!appId.equals("org.springframework.web.context.WebApplicationContext:/" + GlobalInfo.AppId)) {
			return;
		}
		
		//
		// 역할: RSA 키 주기적 갱신
		// 주기: 구동 즉시부터 30분 단위
		// 
		if (bgKeyGenTimer == null) {
			bgKeyGenTimer = new Timer();
			bgKeyGenTimer.scheduleAtFixedRate(new TimerTask() {
		    	   public void run()
		 	       {
		    		   GlobalInfo.RSAKeyPair = Util.getKeyPairRSA();
		    		   GlobalInfo.RSAKeyMod = "";

		    		   logger.info("bgKeyGenTimer - KeyPair: " + (GlobalInfo.RSAKeyPair == null ? "null" : "new"));
			       }
		        }, 0, 30 * (60 * 1000));
		}
	}
}
