package kr.co.r2cast.viewmodels.eco;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RvmServiceResult {
	private int rvmId;

	private String rvmName;
	private String stateDate;
	
	private String workingCount;
	private String storeClosedCount;
	private String failureReportedCount;
	private String rvmOffCount;
	private String stbOffCount;
	private String noShowCount;

	private String avgRunningTime;
	private String totalCount;
	
	private String workingPct;
	private String todayWorkingPct;
	private String todayWorkingCount;
	
	private List<RunningTimeItem> runningTimeItems;
	
	private int realWorkingCount;
	private int realTodayWorkingCount;
	private int realNoShowCount;
	private int realTotalCount;
	
	private List<Date> dates = new ArrayList<Date>();
	private List<Integer> rvmCounts = new ArrayList<Integer>();
	private List<Double> runningMins = new ArrayList<Double>();
	
	private RvmServiceStatusItem statusItemNo1;
	private RvmServiceStatusItem statusItemNo2;
	private RvmServiceStatusItem statusItemNo3;

	public RvmServiceResult() {
		workingCount = "0";
		storeClosedCount = "0";
		failureReportedCount = "0";
		rvmOffCount = "0";
		stbOffCount = "0";
		noShowCount = "0";
		
		totalCount = "0";
		avgRunningTime = "0.0";
		
		workingPct = "0.0";
		todayWorkingPct = "0.0";
		todayWorkingCount = "0";
		
		runningTimeItems = new ArrayList<RunningTimeItem>();
	}
	
	public int getRvmId() {
		return rvmId;
	}

	public void setRvmId(int rvmId) {
		this.rvmId = rvmId;
	}

	public String getRvmName() {
		return rvmName;
	}

	public void setRvmName(String rvmName) {
		this.rvmName = rvmName;
	}

	public String getStateDate() {
		return stateDate;
	}

	public void setStateDate(String stateDate) {
		this.stateDate = stateDate;
	}

	public String getWorkingCount() {
		return workingCount;
	}

	public void setWorkingCount(String workingCount) {
		this.workingCount = workingCount;
	}

	public String getStoreClosedCount() {
		return storeClosedCount;
	}

	public void setStoreClosedCount(String storeClosedCount) {
		this.storeClosedCount = storeClosedCount;
	}

	public String getFailureReportedCount() {
		return failureReportedCount;
	}

	public void setFailureReportedCount(String failureReportedCount) {
		this.failureReportedCount = failureReportedCount;
	}

	public String getRvmOffCount() {
		return rvmOffCount;
	}

	public void setRvmOffCount(String rvmOffCount) {
		this.rvmOffCount = rvmOffCount;
	}

	public String getStbOffCount() {
		return stbOffCount;
	}

	public void setStbOffCount(String stbOffCount) {
		this.stbOffCount = stbOffCount;
	}

	public String getNoShowCount() {
		return noShowCount;
	}

	public void setNoShowCount(String noShowCount) {
		this.noShowCount = noShowCount;
	}

	public String getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}

	public List<RunningTimeItem> getRunningTimeItems() {
		return runningTimeItems;
	}

	public void setRunningTimeItems(List<RunningTimeItem> runningTimeItems) {
		this.runningTimeItems = runningTimeItems;
	}

	public String getAvgRunningTime() {
		return avgRunningTime;
	}

	public void setAvgRunningTime(String avgRunningTime) {
		this.avgRunningTime = avgRunningTime;
	}

	public String getWorkingPct() {
		return workingPct;
	}

	public void setWorkingPct(String workingPct) {
		this.workingPct = workingPct;
	}

	public String getTodayWorkingPct() {
		return todayWorkingPct;
	}

	public void setTodayWorkingPct(String todayWorkingPct) {
		this.todayWorkingPct = todayWorkingPct;
	}

	public String getTodayWorkingCount() {
		return todayWorkingCount;
	}

	public void setTodayWorkingCount(String todayWorkingCount) {
		this.todayWorkingCount = todayWorkingCount;
	}

	public int getRealWorkingCount() {
		return realWorkingCount;
	}

	public void setRealWorkingCount(int realWorkingCount) {
		this.realWorkingCount = realWorkingCount;
	}

	public int getRealTodayWorkingCount() {
		return realTodayWorkingCount;
	}

	public void setRealTodayWorkingCount(int realTodayWorkingCount) {
		this.realTodayWorkingCount = realTodayWorkingCount;
	}

	public int getRealNoShowCount() {
		return realNoShowCount;
	}

	public void setRealNoShowCount(int realNoShowCount) {
		this.realNoShowCount = realNoShowCount;
	}

	public int getRealTotalCount() {
		return realTotalCount;
	}

	public void setRealTotalCount(int realTotalCount) {
		this.realTotalCount = realTotalCount;
	}

	public List<Date> getDates() {
		return dates;
	}

	public void setDates(List<Date> dates) {
		this.dates = dates;
	}

	public List<Integer> getRvmCounts() {
		return rvmCounts;
	}

	public void setRvmCounts(List<Integer> rvmCounts) {
		this.rvmCounts = rvmCounts;
	}

	public List<Double> getRunningMins() {
		return runningMins;
	}

	public void setRunningMins(List<Double> runningMins) {
		this.runningMins = runningMins;
	}

	public RvmServiceStatusItem getStatusItemNo1() {
		return statusItemNo1;
	}

	public void setStatusItemNo1(RvmServiceStatusItem statusItemNo1) {
		this.statusItemNo1 = statusItemNo1;
	}

	public RvmServiceStatusItem getStatusItemNo2() {
		return statusItemNo2;
	}

	public void setStatusItemNo2(RvmServiceStatusItem statusItemNo2) {
		this.statusItemNo2 = statusItemNo2;
	}

	public RvmServiceStatusItem getStatusItemNo3() {
		return statusItemNo3;
	}

	public void setStatusItemNo3(RvmServiceStatusItem statusItemNo3) {
		this.statusItemNo3 = statusItemNo3;
	}
}
