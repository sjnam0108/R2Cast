package kr.co.r2cast.models;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.co.r2cast.utils.Util;

public class UserCookie {
	
	private String appMode = "";

	public UserCookie() {}
	
	public UserCookie(HttpServletRequest request) {
		setAppMode(Util.cookieValue(request, "appMode"));
	}

	public String getAppMode() {
		return appMode;
	}

	public void setAppMode(String appMode) {
		if (Util.isValid(appMode)) {
			this.appMode = appMode;
		}
	}

	public void setAppMode(String appMode, HttpServletResponse response) {
		if (Util.isValid(appMode)) {
			this.appMode = appMode;
			response.addCookie(Util.cookie("appMode", appMode));
		}
	}
}
