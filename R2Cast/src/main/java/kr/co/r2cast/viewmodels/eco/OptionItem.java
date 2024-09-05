package kr.co.r2cast.viewmodels.eco;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class OptionItem {
	public static enum OptType {
		GlobalSite, Site, RvmGroup, Rvm
	}

	private String name;
	private String value;
	private OptType optType;
	
	public OptionItem(String name, String value, OptType optType) {
		this.name = name;
		this.value = value;
		this.optType = optType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@JsonIgnore
	public OptType getOptType() {
		return optType;
	}

	public void setOptType(OptType optType) {
		this.optType = optType;
	}

}
