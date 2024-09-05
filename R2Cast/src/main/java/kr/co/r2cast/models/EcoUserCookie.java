package kr.co.r2cast.models;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.co.r2cast.utils.Util;

public class EcoUserCookie extends UserCookie {

	private String viewCodeRvm = "A";
	
	
	public EcoUserCookie() {}
	
	public EcoUserCookie(HttpServletRequest request) {
		
		super(request);
		
		setViewCodeRvm(Util.cookieValue(request, "viewCodeRvm"));
	}

	
	public String getViewCodeRvm() {
		return viewCodeRvm;
	}

	public void setViewCodeRvm(String viewCodeRvm) {
		if (Util.isValid(viewCodeRvm)) {
			this.viewCodeRvm = viewCodeRvm;
		}
	}

	public void setViewCodeRvm(String viewCodeRvm, HttpServletResponse response) {
		if (Util.isValid(viewCodeRvm)) {
			this.viewCodeRvm = viewCodeRvm;
			response.addCookie(Util.cookie("viewCodeRvm", viewCodeRvm));
		}
	}
}