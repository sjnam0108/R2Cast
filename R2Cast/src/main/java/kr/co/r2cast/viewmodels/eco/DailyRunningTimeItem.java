package kr.co.r2cast.viewmodels.eco;

public class DailyRunningTimeItem {
	private String dateNumber;
	private String cssName;
	private int runningTimeMins;
	
	public DailyRunningTimeItem(String dateNumber, String cssName, 
			int runningTimeMins) {
		this.dateNumber = dateNumber;
		this.cssName = cssName;
		this.runningTimeMins = runningTimeMins;
	}

	public String getDateNumber() {
		return dateNumber;
	}

	public void setDateNumber(String dateNumber) {
		this.dateNumber = dateNumber;
	}

	public int getRunningTimeMins() {
		return runningTimeMins;
	}

	public void setRunningTimeMins(int runningTimeMins) {
		this.runningTimeMins = runningTimeMins;
	}

	public String getCssName() {
		return cssName;
	}

	public void setCssName(String cssName) {
		this.cssName = cssName;
	}
}
