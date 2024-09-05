package kr.co.r2cast.models.eco;

public class EcoFormRequest {
	
	// 일반
	private int id;
	private Integer rvmId;
	
	// STB 개요 모달
	private String stateDate;

	// 광고추적 상세
	private Integer adId;
	private String playDate;
	
	
	public EcoFormRequest() {}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public Integer getRvmId() {
		return rvmId;
	}

	public void setRvmId(Integer rvmId) {
		this.rvmId = rvmId;
	}

	public String getStateDate() {
		return stateDate;
	}

	public void setStateDate(String stateDate) {
		this.stateDate = stateDate;
	}

	public Integer getAdId() {
		return adId;
	}

	public void setAdId(Integer adId) {
		this.adId = adId;
	}

	public String getPlayDate() {
		return playDate;
	}

	public void setPlayDate(String playDate) {
		this.playDate = playDate;
	}
}
