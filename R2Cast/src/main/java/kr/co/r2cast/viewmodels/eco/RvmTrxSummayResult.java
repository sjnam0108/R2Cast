package kr.co.r2cast.viewmodels.eco;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kr.co.r2cast.utils.Util;

public class RvmTrxSummayResult {

	// 수거일
	private List<Date> dates;
	
	
	// 평균 영수증 건수(하루 기준)
	private List<Double> receiptAvgCounts;
	
	// 평균 수거 용기수(하루 기준)
	private List<Double> emptiesAvgCounts;
	
	// 영수증 건수
	private List<Integer> receiptCounts;
	
	// 수거 용기수
	private List<Integer> emptiesCounts;
	
	// 대상 RVM 영수증 건수
	private List<Integer> rvmReceiptCounts;
	

	// 특정 RVM 비교 모드
	private boolean rvmCompareMode;
	
	// 최고 RVM 수
	private int maxRvmCount;
	
	
	public RvmTrxSummayResult(Date endDate) {
		dates = new ArrayList<Date>();
		rvmReceiptCounts = new ArrayList<Integer>();
		receiptAvgCounts = new ArrayList<Double>();
		emptiesAvgCounts = new ArrayList<Double>();
		receiptCounts = new ArrayList<Integer>();
		emptiesCounts = new ArrayList<Integer>();
		
		endDate =  Util.removeTimeOfDate(endDate);
		Calendar cal = Calendar.getInstance();
		
		for(int i = 0; i < 33; i ++) {
			cal.setTime(endDate);
			cal.add(Calendar.DATE, i * -1);
			
			dates.add(0, cal.getTime());
			receiptAvgCounts.add(0d);
			emptiesAvgCounts.add(0d);
			rvmReceiptCounts.add(0);
			receiptCounts.add(0);
			emptiesCounts.add(0);
		}
	}
	
	public void setValues(int idx, double receiptAvgCount, double emptiesAvgCount, int receiptCount, int emptiesCount) {
		if (idx >= 0 && idx < dates.size()) {
			receiptAvgCounts.set(idx, receiptAvgCount);
			emptiesAvgCounts.set(idx, emptiesAvgCount);
			receiptCounts.set(idx, receiptCount);
			emptiesCounts.set(idx, emptiesCount);
		}
	}
	
	public void setValues(Date dstDate, double receiptAvgCount, double emptiesAvgCount, int receiptCount, int emptiesCount) {
		setValues(Util.toDateString(dstDate), receiptAvgCount, emptiesAvgCount, receiptCount, emptiesCount);
	}
	
	public void setValues(String dstDate, double receiptAvgCount, double emptiesAvgCount, int receiptCount, int emptiesCount) {
		for(Date date : dates) {
			if (Util.toDateString(date).equals(dstDate)) {
				int idx = dates.indexOf(date);
				
				receiptAvgCounts.set(idx, receiptAvgCount);
				emptiesAvgCounts.set(idx, emptiesAvgCount);
				receiptCounts.set(idx, receiptCount);
				emptiesCounts.set(idx, emptiesCount);
				
				break;
			}
		}
	}
	
	
	public void setRvmValues(int idx, int receiptCount) {
		if (idx >= 0 && idx < dates.size()) {
			rvmReceiptCounts.set(idx, receiptCount);
		}
	}
	
	public void setRvmValues(Date dstDate, int receiptCount) {
		setRvmValues(Util.toDateString(dstDate), receiptCount);
	}
	
	public void setRvmValues(String dstDate, int receiptCount) {
		for(Date date : dates) {
			if (Util.toDateString(date).equals(dstDate)) {
				int idx = dates.indexOf(date);
				
				rvmReceiptCounts.set(idx, receiptCount);
				
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

	public List<Double> getReceiptAvgCounts() {
		return receiptAvgCounts;
	}

	public void setReceiptAvgCounts(List<Double> receiptAvgCounts) {
		this.receiptAvgCounts = receiptAvgCounts;
	}

	public List<Double> getEmptiesAvgCounts() {
		return emptiesAvgCounts;
	}

	public void setEmptiesAvgCounts(List<Double> emptiesAvgCounts) {
		this.emptiesAvgCounts = emptiesAvgCounts;
	}

	public List<Integer> getRvmReceiptCounts() {
		return rvmReceiptCounts;
	}

	public void setRvmReceiptCounts(List<Integer> rvmReceiptCounts) {
		this.rvmReceiptCounts = rvmReceiptCounts;
	}

	public boolean isRvmCompareMode() {
		return rvmCompareMode;
	}

	public void setRvmCompareMode(boolean rvmCompareMode) {
		this.rvmCompareMode = rvmCompareMode;
	}
	
	public Date getToDate() {
		return dates.get(dates.size() - 1);
	}

	public List<Integer> getReceiptCounts() {
		return receiptCounts;
	}

	public void setReceiptCounts(List<Integer> receiptCounts) {
		this.receiptCounts = receiptCounts;
	}

	public List<Integer> getEmptiesCounts() {
		return emptiesCounts;
	}

	public void setEmptiesCounts(List<Integer> emptiesCounts) {
		this.emptiesCounts = emptiesCounts;
	}
	
	public int getMaxRvmCount() {
		return maxRvmCount;
	}

	public void setMaxRvmCount(int maxRvmCount) {
		this.maxRvmCount = maxRvmCount;
	}

	
	public String getSummaryPeriod() {
		return getMinDate() + " - " + getMaxDate();
	}

	public Date getFromDate() {
		return dates.get(0);
	}
	
	public String getMinDate() {
		return Util.toDateString(dates.get(0));
	}
	
	public String getMaxDate() {
		return Util.toDateString(dates.get(dates.size() - 1));
	}
	
	public String getSummaryReceiptCount() {
		int sum = 0;
		for (Integer val : receiptCounts) {
			sum += val;
		}
		
		return new DecimalFormat("##,###,##0").format(sum);
	}
	
	public String getSummaryReceiptAvgCount() {
		int cnt = 0;
		double sum = 0;
		for (Double val : receiptAvgCounts) {
			sum += val;
			if (val > 0) {
				cnt++;
			}
		}
		
		if (cnt == 0) {
			return new DecimalFormat("###,##0.0").format(0d);
		}

		return new DecimalFormat("###,##0.0").format(sum / cnt);
	}
	
	public String getSummaryRvmReceiptAvgCount() {
		int cnt = 0;
		int sum = 0;
		for (Integer val : rvmReceiptCounts) {
			sum += val;
			if (val > 0) {
				cnt++;
			}
		}
		
		if (cnt == 0) {
			return new DecimalFormat("###,##0.0").format(0d);
		}

		return new DecimalFormat("###,##0.0").format((double)sum / cnt);
	}
	
	public String getSummaryEmptiesCount() {
		int sum = 0;
		for (Integer val : emptiesCounts) {
			sum += val;
		}
		
		return new DecimalFormat("##,###,##0").format(sum);
	}
	
	public String getSummaryEmptiesAvgCount() {
		int cnt = 0;
		double sum = 0;
		for (Double val : emptiesAvgCounts) {
			sum += val;
			if (val > 0) {
				cnt++;
			}
		}
		
		if (cnt == 0) {
			return new DecimalFormat("###,##0.0").format(0d);
		}

		return new DecimalFormat("###,##0.0").format(sum / cnt);
	}
}
