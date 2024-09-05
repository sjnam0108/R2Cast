package kr.co.r2cast.viewmodels.eco;

import java.util.Date;

public class RvmItem {
	private int id;
	private String rvmName;
	private Date date;
	
	public RvmItem(int id, String rvmName) {
		this.id = id;
		this.rvmName = rvmName;
	}
	
	public RvmItem(int id, Date date) {
		this.id = id;
		this.date = date;
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
