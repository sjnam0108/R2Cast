package kr.co.r2cast.viewmodels.eco;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class NocFailureDisplayItem {

	private int totalCnt = 0;
	private int addCnt = 0;
	private int removeCnt = 0;
	
	private String currentTime = "";
	private String addedDevices = "";
	private String removedDevices = "";
	
	private long createdTime = new Date().getTime();
	
	public NocFailureDisplayItem(int totalCnt, int addCnt, int removeCnt,
			String addedDevices, String removedDevices) {
		this.totalCnt = totalCnt;
		this.addCnt = addCnt;
		this.removeCnt = removeCnt;
		this.addedDevices = addedDevices;
		this.removedDevices = removedDevices;
		
		this.currentTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
	}

	public int getTotalCnt() {
		return totalCnt;
	}

	public void setTotalCnt(int totalCnt) {
		this.totalCnt = totalCnt;
	}

	public int getAddCnt() {
		return addCnt;
	}

	public void setAddCnt(int addCnt) {
		this.addCnt = addCnt;
	}

	public int getRemoveCnt() {
		return removeCnt;
	}

	public void setRemoveCnt(int removeCnt) {
		this.removeCnt = removeCnt;
	}

	public String getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(String currentTime) {
		this.currentTime = currentTime;
	}

	public String getAddedDevices() {
		return addedDevices;
	}

	public void setAddedDevices(String addedDevices) {
		this.addedDevices = addedDevices;
	}

	public String getRemovedDevices() {
		return removedDevices;
	}

	public void setRemovedDevices(String removedDevices) {
		this.removedDevices = removedDevices;
	}
	
	public long getCreatedTime() {
		return createdTime;
	}

	public String toJSONString() {
		return JSONObject.fromObject(JSONSerializer.toJSON(this)).toString();
	}
}
