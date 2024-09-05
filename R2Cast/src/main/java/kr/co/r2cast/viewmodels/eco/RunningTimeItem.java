package kr.co.r2cast.viewmodels.eco;

public class RunningTimeItem {
	private String title;
	private String group;
	private int value;
	
	public RunningTimeItem(String title, String group, int value) {
		this.title = title;
		this.group = group;
		this.value = value;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
}
