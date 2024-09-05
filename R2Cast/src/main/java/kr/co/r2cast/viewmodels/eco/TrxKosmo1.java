package kr.co.r2cast.viewmodels.eco;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"Amountt"})
public class TrxKosmo1 {
	@JsonIgnoreProperties("Amountt")
	private int Amountt;
	private String date;
	private String serial_no;
	private String container_code;
	private String area_code;
	private String branch_code;
	private String branch_name;
	private String purpose_code;
	private String receipt_no;
	private int sibling_seq;
	private String sys_type;
	private int deposit_value;
	private int qty;
	private String reg_id;


	
	public TrxKosmo1(boolean isValid, int count,int Amount, String amountSt, String dateTime, String seriaNo,String areaCode,String branchCode ,String branchName, String purposeCode,String ReceiptNoStr1,int siblingSeq, String sysType,int deposit,String regId) {
		if (isValid) {
			qty = count;
			container_code = amountSt;
			date = dateTime;
			serial_no = seriaNo;
			area_code = areaCode;
			branch_name = branchName;
			branch_code = branchCode;
			purpose_code = purposeCode;
			receipt_no = ReceiptNoStr1;
			sibling_seq = siblingSeq;
			sys_type = sysType;
			deposit_value = Amount * count;
			reg_id = regId;
			Amountt = Amount;
		} else {
			qty = count;
			container_code = amountSt;
			date = dateTime;
			serial_no = seriaNo;
			area_code = areaCode;
			branch_name = branchName;
			branch_code = branchCode;
			purpose_code = purposeCode;
			receipt_no = ReceiptNoStr1;
			sibling_seq = siblingSeq;
			sys_type = sysType;
			deposit_value = Amount * count;
			reg_id = regId;
			Amountt = Amount;
		}
		
	}
	
	public void add(boolean isValid, int count, int Amount,int amountInt,int deposit, String amountSt) {
		if (isValid) {
			qty += count;
			container_code = amountSt;
			deposit_value += Amount * count;
		} else {
			qty += count;
			container_code = amountSt;
			deposit_value += Amount * count;
		}
	}
	
	public void addseq(boolean isValid, int siblingSeq) {
		if (isValid) {
			sibling_seq += siblingSeq;
		} else {
			sibling_seq += siblingSeq;
		}
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getSerial_no() {
		return serial_no;
	}
	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
	}
	public String getContainer_code() {
		return container_code;
	}
	public void setContainer_code(String container_code) {
		this.container_code = container_code;
	}
	public String getArea_code() {
		return area_code;
	}
	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}
	public String getBranch_code() {
		return branch_code;
	}
	public void setBranch_code(String branch_code) {
		this.branch_code = branch_code;
	}
	public String getBranch_name() {
		return branch_name;
	}
	public void setBranch_name(String branch_name) {
		this.branch_name = branch_name;
	}

	public String getPurpose_code() {
		return purpose_code;
	}
	public void setPurpose_code(String purpose_code) {
		this.purpose_code = purpose_code;
	}
	public String getReceipt_no() {
		return receipt_no;
	}
	public void setReceipt_no(String receipt_no) {
		this.receipt_no = receipt_no;
	}
	public int getSibling_seq() {
		return sibling_seq;
	}
	public void setSibling_seq(int sibling_seq) {
		this.sibling_seq = sibling_seq;
	}
	public String getSys_type() {
		return sys_type;
	}
	public void setSys_type(String sys_type) {
		this.sys_type = sys_type;
	}
	public int getDeposit_value() {
		return deposit_value;
	}
	public void setDeposit_value(int deposit_value) {
		this.deposit_value = deposit_value;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	public String getReg_id() {
		return reg_id;
	}
	public void setReg_id(String reg_id) {
		this.reg_id = reg_id;
	}
	@JsonIgnore
	public int getAmountt() {
		return Amountt;
	}
	@JsonIgnore
	public void setAmountt(int amount) {
		Amountt = amount;
	}



}
