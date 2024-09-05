package kr.co.r2cast.viewmodels.eco;

import java.text.DecimalFormat;

import kr.co.r2cast.models.eco.Rvm;

public class RvmTrxDailyResult {

	// RVM id
	private int id;
	
	// RVM명
	private String rvmName;
	
	// 영수증 건수
	private int receiptCount;
	
	// 수거 용기수
	private int emptiesCount;
	
	// 영수증 총액
	private long receiptAmount;
	
	// 마지막 영수증 번호
	private int lastReceiptNo;
	
	// 용기 유형 수
	private int emptiesTypeCount;
	
	
	public RvmTrxDailyResult(Rvm rvm) {
		this.id = rvm.getId();
		this.rvmName = rvm.getRvmName();
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRvmName() {
		return rvmName;
	}

	public void setRvmName(String rvmName) {
		this.rvmName = rvmName;
	}

	public int getReceiptCount() {
		return receiptCount;
	}

	public void setReceiptCount(int receiptCount) {
		this.receiptCount = receiptCount;
	}

	public int getEmptiesCount() {
		return emptiesCount;
	}

	public void setEmptiesCount(int emptiesCount) {
		this.emptiesCount = emptiesCount;
	}
	
	public long getReceiptAmount() {
		return receiptAmount;
	}

	public void setReceiptAmount(long receiptAmount) {
		this.receiptAmount = receiptAmount;
	}
	
	public int getLastReceiptNo() {
		return lastReceiptNo;
	}

	public void setLastReceiptNo(int lastReceiptNo) {
		this.lastReceiptNo = lastReceiptNo;
	}

	public int getEmptiesTypeCount() {
		return emptiesTypeCount;
	}
	
	public void setEmptiesTypeCount(int emptiesTypeCount) {
		this.emptiesTypeCount = emptiesTypeCount;
	}

	public String getDispReceiptCount() {
		return new DecimalFormat("###,###,##0").format(receiptCount);
	}

	public String getDispEmptiesCount() {
		return new DecimalFormat("###,###,##0").format(emptiesCount);
	}

	public String getDispReceiptAmount() {
		return new DecimalFormat("###,###,##0").format(receiptAmount);
	}

	public String getDispLastReceiptNo() {
		return new DecimalFormat("###,##0").format(lastReceiptNo);
	}

	public String getDispEmptiesTypeCount() {
		return new DecimalFormat("###,##0").format(emptiesTypeCount);
	}
	
}
