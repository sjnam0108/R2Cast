package kr.co.r2cast.viewmodels.eco;

import java.text.DecimalFormat;
import java.util.Date;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class RvmStatusItem {
	private int status6 = 0;
	private int status5 = 0;
	private int status4 = 0;
	private int status3 = 0;
	private int status2 = 0;
	private int status0 = 0;

	private boolean autoCalcMode = false;
	
	private long createdTime = new Date().getTime();
	private DecimalFormat dFormat = new DecimalFormat("##,##0");
	
	public RvmStatusItem(int status6, int status5, int status4, int status3, 
			int status2, int status0) {
		this(status6, status5, status4, status3, status2, status0, true);
	}
	
	public RvmStatusItem(int status6, int status5, int status4, int status3, 
			int status2, int status0, boolean autoCalcMode) {
		this.status6 = status6;
		this.status5 = status5;
		this.status4 = status4;
		this.status3 = status3;
		this.status2 = status2;
		this.status0 = status0;
		this.autoCalcMode = autoCalcMode;
	}
	
	public String getStatus6() {
		return dFormat.format(status6);
	}
	
	public void setStatus6(int status6) {
		this.status6 = status6;
	}
	
	public String getStatus5() {
		return dFormat.format(status5);
	}

	public void setStatus5(int status5) {
		this.status5 = status5;
	}
	
	public String getStatus4() {
		return dFormat.format(status4);
	}
	
	public void setStatus4(int status4) {
		this.status4 = status4;
	}

	public String getStatus3() {
		return dFormat.format(status3);
	}

	public void setStatus3(int status3) {
		this.status3 = status3;
	}
	
	public String getStatus2() {
		return dFormat.format(status2);
	}

	public void setStatus2(int status2) {
		this.status2 = status2;
	}

	public String getStatus0() {
		return dFormat.format(status0);
	}

	public void setStatus0(int status0) {
		this.status0 = status0;
	}
	
	public boolean isAutoCalcMode() {
		return autoCalcMode;
	}
	
	public long getCreatedTime() {
		return createdTime;
	}

	public String toJSONString() {
		return JSONObject.fromObject(JSONSerializer.toJSON(this)).toString();
	}
}
