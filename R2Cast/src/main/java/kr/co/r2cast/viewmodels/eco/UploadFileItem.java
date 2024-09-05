package kr.co.r2cast.viewmodels.eco;

import java.util.Date;

import kr.co.r2cast.utils.Util;

public class UploadFileItem {
	private String rvm;
	private String date;
	private String filename;
	private String name;
	
	private String siteShortName;

	private long length;
	private long lastModified;
	
	private int id;
    
    private String custom1;
    private String custom2;
    private String custom3;
    
	public UploadFileItem(String filename, long length, long lastModified) {
		this.filename = filename;
		this.length = length;
		this.lastModified = lastModified;
		this.siteShortName = "";
	}
	
	public UploadFileItem(String filename, long length, long lastModified,
			String rvm,  String date, int id) {
		this.filename = filename;
		this.length = length;
		this.lastModified = lastModified;
		this.rvm = rvm;
		this.date = date;
		this.id = id;
		this.siteShortName = "";
	}
	
	public UploadFileItem(String custom1, String name, long lastModified, String custom2,
			String filename) {
		this.name = name;
		this.lastModified = lastModified;
		this.filename = filename;
		this.custom1 = custom1;
		this.custom2 = custom2;
		this.custom3 = "";
	}
	
	public UploadFileItem(int id, String name, String filename, String siteShortName) {
		this.id = id;
		this.name = name;
		this.filename = filename;
		this.siteShortName = siteShortName + "/";
	}
	
	public UploadFileItem(String filename, String siteShortName) {
		this.filename = filename;
		this.siteShortName = siteShortName + "/";
	}
	
	public UploadFileItem(int id, String name, String filename) {
		this.id = id;
		this.name = name;
		this.filename = filename;
		this.siteShortName = "";
	}
	
	public UploadFileItem(String filename) {
		this.filename = filename;
		this.siteShortName = "";
	}
	
	public UploadFileItem(int id) {
		this.id = id;
	}

	public String getRvm() {
		return rvm;
	}

	public String getDate() {
		return date;
	}

	public String getFilename() {
		return filename;
	}

	public long getLength() {
		return length;
	}
	
	public String getSmartLength() {
		return Util.getSmartFileLength(length);
	}

	public String getLastModified() {
		return Util.toSimpleString(new Date(lastModified));
	}

	public String getSimpleLastModified() {
		return Util.toSimpleString(new Date(lastModified), "yyyy-MM-dd HH:mm");
	}

	public int getId() {
		return id;
	}

	public String getPathName() {
		return "/" + siteShortName + filename;
	}
	
	public String getName() {
		return name;
	}
    
    public String getCustom1() {
		return custom1;
	}
    
    public Date getUploadDate() {
    	return new Date(lastModified);
    }
    
    public void setId(int id) {
    	this.id = id;
    }

	public void setCustom1(String custom1) {
		this.custom1 = custom1;
	}

	public String getCustom2() {
		return custom2;
	}

	public void setCustom2(String custom2) {
		this.custom2 = custom2;
	}

	public String getCustom3() {
		return custom3;
	}

	public void setCustom3(String custom3) {
		this.custom3 = custom3;
	}
}
