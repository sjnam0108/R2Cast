package kr.co.r2cast.viewmodels.eco;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kr.co.r2cast.utils.Util;

public class RvmServiceSummaryResult {
	private List<Date> dates;
	private List<Integer> rvmCounts;
	private List<Double> runningMins;
	private List<Integer> rvmRunningMins;
	
	private boolean rvmCompareMode;
	
	private int summaryRvmCount;
	
	public RvmServiceSummaryResult(Date endDate) {
		dates = new ArrayList<Date>();
		rvmCounts = new ArrayList<Integer>();
		runningMins = new ArrayList<Double>();
		rvmRunningMins = new ArrayList<Integer>();
		
		endDate =  Util.removeTimeOfDate(endDate);
		Calendar cal = Calendar.getInstance();
		
		for(int i = 0; i < 33; i ++) {
			cal.setTime(endDate);
			cal.add(Calendar.DATE, i * -1);
			
			dates.add(0, cal.getTime());
			rvmCounts.add(0);
			runningMins.add(0d);
			rvmRunningMins.add(0);
		}
	}
	
	public void setValues(int idx, int rvmCount, double runningMins) {
		if (idx >= 0 && idx < dates.size()) {
			rvmCounts.set(idx, rvmCount);
			this.runningMins.set(idx, runningMins);
		}
	}
	
	public void setValues(Date dstDate, int rvmCount, double runningMins) {
		setValues(Util.toDateString(dstDate), rvmCount, runningMins);
	}
	
	public void setValues(String dstDate, int rvmCount, double runningMins) {
		for(Date date : dates) {
			if (Util.toDateString(date).equals(dstDate)) {
				int idx = dates.indexOf(date);
				
				rvmCounts.set(idx, rvmCount);
				this.runningMins.set(idx, runningMins);
				
				break;
			}
		}
	}
	
	public void setRvmValue(int idx, int rvmRunningMins) {
		if (idx >= 0 && idx < dates.size()) {
			this.rvmRunningMins.set(idx, rvmRunningMins);
		}
	}
	
	public void setRvmValue(Date dstDate, int rvmRunningMins) {
		setRvmValue(Util.toDateString(dstDate), rvmRunningMins);
	}

	public void setRvmValue(String dstDate, int rvmRunningMins) {
		for(Date date : dates) {
			if (Util.toDateString(date).equals(dstDate)) {
				int idx = dates.indexOf(date);
				
				this.rvmRunningMins.set(idx, rvmRunningMins);
				
				break;
			}
		}
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
	
	public String getMinDate() {
		return Util.toDateString(dates.get(0));
	}
	
	public String getMaxDate() {
		return Util.toDateString(dates.get(dates.size() - 1));
	}

	public List<Integer> getRvmRunningMins() {
		return rvmRunningMins;
	}

	public void setRvmRunningMins(List<Integer> rvmRunningMins) {
		this.rvmRunningMins = rvmRunningMins;
	}

	public boolean isRvmCompareMode() {
		return rvmCompareMode;
	}

	public void setRvmCompareMode(boolean rvmCompareMode) {
		this.rvmCompareMode = rvmCompareMode;
	}
	
	public void setSummaryRvmCount(int summaryRvmCount) {
		this.summaryRvmCount = summaryRvmCount;
	}

	public String getSummaryPeriod() {
		return getMinDate() + " - " + getMaxDate();
	}

	public String getSummaryRvmCount() {
		return new DecimalFormat("###,##0").format(summaryRvmCount);
	}
	
	private int getEffectiveRvmCount() {
		if (rvmCounts.size() == 0) {
			return 0;
		} else {
			int cnt = 0;
			for (Integer val : rvmCounts) {
				if (val > 0) {
					cnt ++;
				}
			}
			
			return cnt;
		}
	}
	
	public String getSummaryAvgRunningMins() {
		int effCnt = getEffectiveRvmCount();
		
		if (effCnt == 0) {
			return new DecimalFormat("#,##0.0").format(0d);
		}
		double sum = 0d;
		for (Double val : runningMins) {
			sum += val;
		}
		
		return new DecimalFormat("#,##0.0").format(sum / effCnt);
	}
	
	public String getSummaryAvgRvmCount() {
		int effCnt = getEffectiveRvmCount();
		
		if (effCnt == 0) {
			return new DecimalFormat("###,##0.0").format(0d);
		}
		
		long sum = 0l;
		for (Integer val : rvmCounts) {
			sum += val.intValue();
		}
		
		return new DecimalFormat("###,##0.0").format((double)sum / effCnt);
	}
	
	public String getSummaryAvgRvmRunningMins() {
		if (rvmCompareMode == false) {
			return "";
		}
		
		int effCnt = getEffectiveRvmCount();
		
		if (effCnt == 0) {
			return new DecimalFormat("#,##0.0").format(0d);
		}
		double sum = 0d;
		for (Integer val : rvmRunningMins) {
			sum += val.intValue();
		}
		
		return new DecimalFormat("#,##0.0").format(sum / effCnt);
	}
}
