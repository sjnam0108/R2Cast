package kr.co.r2cast.viewmodels.eco;

import com.fasterxml.jackson.annotation.JsonIgnore;

import kr.co.r2cast.models.eco.RvmTrx;
import kr.co.r2cast.utils.Util;

public class JsonCosmoData {
	
	private String date;
	private String serial_no;
	private String container_code;
	private String area_code;
	private String branch_code;
	private String branch_name;
	private String purpose_code = "1";
	private String receipt_no = "0000";
	private int sibling_seq = 0;
	private String sys_type = "API";
	private int deposit_value;
	private int qty;
	private String reg_id = "RELAB";

	private int amount;
	
	public JsonCosmoData(RvmTrx trx, String containercode, int price) {
		this.date = Util.toSimpleString(trx.getOpDate(), "yyyyMMdd");
		this.serial_no = trx.getRvm().getSerialNo();
		this.container_code = containercode;
		this.area_code = trx.getRvm().getAreaCode();
		this.branch_code = trx.getRvm().getBranchCode();
		this.branch_name = trx.getRvm().getBranchName();
		this.receipt_no = String.format("%04d", trx.getReceiptNo());
		
		this.amount = price;
	}
	
	public String getSerial_no() {
		return serial_no;
	}

	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
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
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getContainer_code() {
		return container_code;
	}
	
	public void setContainer_code(String container_code) {
		this.container_code = container_code;
	}
	
	public int getQty() {
		return qty;
	}
	
	public void setQty(int qty) {
		this.qty = qty;
	}
	
	@JsonIgnore
	public int getAmount() {
		return amount;
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getPurpose_code() {
		return purpose_code;
	}

	public void setPurpose_code(String purpose_code) {
		this.purpose_code = purpose_code;
	}

	public String getSys_type() {
		return sys_type;
	}

	public void setSys_type(String sys_type) {
		this.sys_type = sys_type;
	}

	public String getReg_id() {
		return reg_id;
	}

	public void setReg_id(String reg_id) {
		this.reg_id = reg_id;
	}

	public String getReceipt_no() {
		return receipt_no;
	}

	public void setReceipt_no(String receipt_no) {
		this.receipt_no = receipt_no;
	}

	public int getDeposit_value() {
		return deposit_value;
	}

	public void setDeposit_value(int deposit_value) {
		this.deposit_value = deposit_value;
	}

	public int getSibling_seq() {
		return sibling_seq;
	}

	public void setSibling_seq(int sibling_seq) {
		this.sibling_seq = sibling_seq;
	}

}
