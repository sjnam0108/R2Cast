package kr.co.r2cast.viewmodels.eco;

import kr.co.r2cast.models.eco.Rvm;

public class RvmOverviewData {
	private RvmServiceResult service;
	private Rvm rvm;

	public RvmOverviewData() {}

	public RvmServiceResult getService() {
		return service;
	}

	public void setService(RvmServiceResult service) {
		this.service = service;
	}

	public Rvm getRvm() {
		return rvm;
	}

	public void setRvm(Rvm rvm) {
		this.rvm = rvm;
	}

}
