package kr.co.r2cast.models;

public class UploadTransitionModel {
	private int siteId = -1;
	private String type = "NO";
	private String message = "";
	private String siteShortName = "";
	
	private String saveUrl = "/common/uploadsave";

	private String allowedExtensions = "";
	private String assetSite = "";
	private String code = "";
	
	public UploadTransitionModel() { }

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAssetSite() {
		return assetSite;
	}

	public void setAssetSite(String assetSite) {
		this.assetSite = assetSite;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSiteShortName() {
		return siteShortName;
	}

	public void setSiteShortName(String siteShortName) {
		this.siteShortName = siteShortName;
	}

	public String getAllowedExtensions() {
		return allowedExtensions;
	}

	public void setAllowedExtensions(String allowedExtensions) {
		this.allowedExtensions = allowedExtensions;
	}

	public String getSaveUrl() {
		return saveUrl;
	}

	public void setSaveUrl(String saveUrl) {
		this.saveUrl = saveUrl;
	}
}
