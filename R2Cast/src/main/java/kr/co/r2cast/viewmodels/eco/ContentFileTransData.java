package kr.co.r2cast.viewmodels.eco;

import java.text.DecimalFormat;

import kr.co.r2cast.utils.Util;

public class ContentFileTransData {
	private long latestScheduleLength;
	private long latestScheduleTransLength;
	private long totalScheduleLength;
	private long totalScheduleTransLength;
	private long totalOtherLength;
	private long totalOtherTransLength;
	
	private String tipCompleted;
	private String tipLeft;
	private String tipNA;
	
	private String latestSchedule;
	
	private String latestSchdValue = "";
	private String latestSchdDisp = "";
	private String latestSchdDesc = "";
	private String latestSchdColor = "bg-info";
	
	private String totalSchdValue = "";
	private String totalSchdDisp = "";
	private String totalSchdDesc = "";
	private String totalSchdColor = "bg-info";
	
	private String totalOtherValue = "";
	private String totalOtherDisp = "";
	private String totalOtherDesc = "";
	private String totalOtherColor = "bg-info";
	
	private DecimalFormat pctFormat = new DecimalFormat("##0.0%");

	public ContentFileTransData(String latestSchedule, 
			long latestScheduleLength, long latestScheduleTransLength,
			long totalScheduleLength, long totalScheduleTransLength,
			long totalOtherLength, long totalOtherTransLength,
			String tipCompleted, String tipLeft, String tipNA) {
		this.latestSchedule = latestSchedule;
		this.latestScheduleLength = latestScheduleLength;
		this.latestScheduleTransLength = latestScheduleTransLength;
		
		this.totalScheduleLength = totalScheduleLength;
		this.totalScheduleTransLength = totalScheduleTransLength;
		
		this.totalOtherLength = totalOtherLength;
		this.totalOtherTransLength = totalOtherTransLength;
		
		this.tipCompleted = tipCompleted;
		this.tipLeft = tipLeft;
		this.tipNA = tipNA;
	}

	private String getRatio(long numerator, long denominator) {
		if (numerator < 0 || denominator <= 0) {
			return "N/A";
		} else if (numerator > denominator) {
			return "100.0%";
		}
		
		return pctFormat.format((double)numerator / (double)denominator);
	}

	public void calcValues() {
		String pctLatestRatio = getRatio(latestScheduleTransLength, latestScheduleLength);
		if (pctLatestRatio.equals("N/A")) {
			latestSchdValue = "0%";
			latestSchdDisp = tipNA;
		} else {
			latestSchdValue = pctLatestRatio;
			latestSchdDisp = pctLatestRatio;
			
			if (pctLatestRatio.equals("100.0%")) {
				latestSchdDesc = tipCompleted;
				latestSchdColor = "bg-success";
			} else {
				latestSchdDesc = Util.getSmartFileLength(
						latestScheduleLength - latestScheduleTransLength) + tipLeft;
			}
		}
		
		String pctSchdRatio = getRatio(totalScheduleTransLength, totalScheduleLength);
		if (pctSchdRatio.equals("N/A")) {
			totalSchdValue = "0%";
			totalSchdDisp = tipNA;
		} else {
			totalSchdValue = pctLatestRatio;
			totalSchdDisp = pctLatestRatio;
			
			if (pctSchdRatio.equals("100.0%")) {
				totalSchdDesc = tipCompleted;
				totalSchdColor = "bg-success";
			} else {
				totalSchdDesc = Util.getSmartFileLength(
						totalScheduleLength - totalScheduleTransLength) + tipLeft;
			}
		}
		
		String pctOthrRatio = getRatio(totalOtherTransLength, totalOtherLength);
		if (pctOthrRatio.equals("N/A")) {
			totalOtherValue = "0%";
			totalOtherDisp = tipNA;
		} else {
			totalSchdValue = pctLatestRatio;
			totalSchdDisp = pctLatestRatio;
			
			if (pctOthrRatio.equals("100.0%")) {
				totalOtherDesc = tipCompleted;
				totalOtherColor = "bg-success";
			} else {
				totalOtherDesc = Util.getSmartFileLength(
						totalOtherLength - totalOtherTransLength) + tipLeft;
			}
		}
	}

	public String getLatestSchedule() {
		return latestSchedule;
	}

	public String getLatestSchdValue() {
		return latestSchdValue;
	}

	public String getLatestSchdDisp() {
		return latestSchdDisp;
	}

	public String getLatestSchdDesc() {
		return latestSchdDesc;
	}

	public String getLatestSchdColor() {
		return latestSchdColor;
	}

	public String getTotalSchdValue() {
		return totalSchdValue;
	}

	public String getTotalSchdDisp() {
		return totalSchdDisp;
	}

	public String getTotalSchdDesc() {
		return totalSchdDesc;
	}

	public String getTotalSchdColor() {
		return totalSchdColor;
	}

	public String getTotalOtherValue() {
		return totalOtherValue;
	}

	public String getTotalOtherDisp() {
		return totalOtherDisp;
	}

	public String getTotalOtherDesc() {
		return totalOtherDesc;
	}

	public String getTotalOtherColor() {
		return totalOtherColor;
	}
}
