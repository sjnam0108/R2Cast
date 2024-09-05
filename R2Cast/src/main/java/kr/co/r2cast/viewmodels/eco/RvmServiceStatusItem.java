package kr.co.r2cast.viewmodels.eco;

import java.text.DecimalFormat;

public class RvmServiceStatusItem {
	private String title = "";
	private String color = "";
	private int count = 0;
	private int total = 0;
	
	public RvmServiceStatusItem(String title, String color, int count, int total) {
		this.title = title;
		this.color = color;
		this.count = count;
		this.total = total;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
	
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getPercent() {
		if (total > 0) {
			return new DecimalFormat("##0.0").format((float)count * 100f / (float)total);
		} else {
			return new DecimalFormat("##0.0").format(0f);
		}
	}
	
	public String getCountDisp() {
		return new DecimalFormat("##,##0").format(count);
	}
}
