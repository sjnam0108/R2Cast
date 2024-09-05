package kr.co.r2cast.models;


public class FormRequest {
	// for Menu model
	private int id;
	private String ukid;
	private String url;
	private String oper;
	private String icon;
	private String group;
	private boolean siteSelector;
	private boolean customized;
	private boolean userFriendly;
	
	// for Password Update
	private String currentPassword;
	private String newPassword;
	private String confirmPassword;

	
	public FormRequest() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUkid() {
		return ukid;
	}

	public void setUkid(String ukid) {
		this.ukid = ukid;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getOper() {
		return oper;
	}

	public void setOper(String oper) {
		this.oper = oper;
	}
	
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getCurrentPassword() {
		return currentPassword;
	}
	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}
	
	public String getNewPassword() {
		return newPassword;
	}
	
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	
	public String getConfirmPassword() {
		return confirmPassword;
	}
	
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public boolean isSiteSelector() {
		return siteSelector;
	}

	public void setSiteSelector(boolean siteSelector) {
		this.siteSelector = siteSelector;
	}

	public boolean isCustomized() {
		return customized;
	}

	public void setCustomized(boolean customized) {
		this.customized = customized;
	}

	public boolean isUserFriendly() {
		return userFriendly;
	}

	public void setUserFriendly(boolean userFriendly) {
		this.userFriendly = userFriendly;
	}
}
