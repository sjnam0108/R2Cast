package kr.co.r2cast.viewmodels.eco;

public class RvmCommandItem {
	private int id;
	private String command;
	private String params;
	private String execTime;
	private int rvmId;
	
	public RvmCommandItem(int id, String command, String params, String execTime) {
		this(id, command, params, execTime, 0);
	}
	
	public RvmCommandItem(int id, String command, String params, String execTime,
			int rvmId) {
		this.id = id;
		this.command = command;
		this.params = params;
		this.execTime = execTime == null ? "" : execTime;
		this.rvmId = rvmId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getExecTime() {
		return execTime;
	}

	public void setExecTime(String execTime) {
		this.execTime = execTime;
	}

	public int getRvmId() {
		return rvmId;
	}

	public void setRvmId(int rvmId) {
		this.rvmId = rvmId;
	}
	
}
