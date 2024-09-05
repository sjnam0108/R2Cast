package kr.co.r2cast.viewmodels.eco;

import java.text.DecimalFormat;
import java.util.Date;

import kr.co.r2cast.utils.Util;

public class RvmTrxDailySummaryResult {

	// 수거일
	private Date date;
	
	// 수거 RVM 수
	private int rvmCount;
	
	// 용기 유형 수
	private int emptiesTypeCount;
	
	// 영수증 건수
	private int receiptCount;
	
	// 영수증 금액
	private long receiptAmount;
	
	// 수거 용기수
	private int emptiesCount;

	
	public RvmTrxDailySummaryResult(Date date, int rvmCount, int receiptCount, int receiptAmount,
			int emptiesCount, int emptiesTypeCount) {
		this.date = date;
		this.rvmCount = rvmCount;
		this.receiptCount = receiptCount;
		this.receiptAmount = receiptAmount;
		this.emptiesCount = emptiesCount;
		this.emptiesTypeCount = emptiesTypeCount;
	}
	
	
	public Date getDate() {
		return date;
	}
	
	public String getSummaryTitle() {
		return Util.toSimpleString(date, "yyyy-MM-dd (EEE)");
	}
	
	public String getSummaryRvmCount() {
		return new DecimalFormat("###,##0").format(rvmCount);
	}
	
	public String getSummaryEmptiesTypeCount() {
		return new DecimalFormat("###,##0").format(emptiesTypeCount);
	}
	
	public String getSummaryReceiptCount() {
		return new DecimalFormat("###,###,##0").format(receiptCount);
	}
	
	public String getSummaryReceiptAmount() {
		return new DecimalFormat("###,###,##0").format(receiptAmount);
	}
	
	public String getSummaryEmptiesCount() {
		return new DecimalFormat("###,###,##0").format(emptiesCount);
	}
	
	public String getSummaryReceiptAvgCount() {
		if (rvmCount == 0) {
			return "0";
		} else {
			return new DecimalFormat("###,##0.0").format((double)receiptCount / (double)rvmCount);
		}
	}
	
	public String getSummaryReceiptAvgAmount() {
		if (rvmCount == 0 || receiptCount == 0) {
			return "0";
		} else {
			return new DecimalFormat("##,###,##0").format((double)receiptAmount / (double)rvmCount / (double)receiptCount);
		}
	}
	
	public String getSummaryEmptiesAvgCount() {
		if (rvmCount == 0 || receiptCount == 0) {
			return "0";
		} else {
			return new DecimalFormat("###,##0.0").format((double)emptiesCount / (double)rvmCount / (double)receiptCount);
		}
	}
}
