package kr.co.r2cast.models.eco;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.annotation.JsonIgnore;

import kr.co.r2cast.utils.Util;

@Entity
@Table(name="ECO_RVM_LAST_REPORTS")
public class RvmLastReport {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "rvm_last_report_seq_gen")
	@SequenceGenerator(name = "rvm_last_report_seq_gen", sequenceName = "ECO_RVM_LAST_REPORTS_SEQ")
	@Column(name = "RVM_LAST_REPORT_ID")
	private int id;
	
	@Column(name = "STATUS", nullable = false, length = 1)
	private String status;
	
	@Column(name = "DISK_SIZE", nullable = true, length = 10)
	private Float diskSize;
	
	@Column(name = "DISK_FREE", nullable = true, length = 10)
	private Float diskFree;
	
	@Column(name = "DISK_USED_RATIO", nullable = true, length = 10)
	private Float diskUsedRatio;
	
	@Column(name = "REPORT_IP", nullable = true, length = 100)
	private String reportIp;
	
	// jason:playerversion: Player Version 서버에 기록(2014/07/10)
	@Column(name = "APP_VER", nullable = true, length = 20)
	private String appVersion;
	//-
	
	@Column(name = "NETWORK_SPEED", nullable = true)
	private Integer networkSpeed;
	
	@Column(name = "AGENT_START_DATE", nullable = true)
	private Date agentStartDate;
	
	@Column(name = "RES_TAG", nullable = true, length = 50)
	private String resTag;
	
	@Column(name = "OP_TAG", nullable = true, length = 50)
	private String opTag;

	
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	@Column(name = "LAST_UPDATE_DATE", nullable = false)
	private Date whoLastUpdateDate;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "RVM_ID", nullable = false, unique = true)
	private Rvm rvm;
	
	public RvmLastReport() {}
	
	public RvmLastReport(Rvm rvm) {
		this(rvm, "", "", null, null, null, null);
	}
	
	public RvmLastReport(Rvm rvm, String status, String reportIp, 
			Float diskSize, Float diskFree, Float diskUsedRatio) {
		this(rvm, status, reportIp, diskSize, 
				diskFree, diskUsedRatio, null);
	}
	
	public RvmLastReport(Rvm rvm, String status, String reportIp, 
			Float diskSize, Float diskFree, Float diskUsedRatio, 
			HttpSession session) {
		this.rvm = rvm;
		this.status = status;
		this.reportIp = reportIp;
		this.diskSize = diskSize;
		this.diskFree = diskFree;
		this.diskUsedRatio = diskUsedRatio;
		
		this.networkSpeed = 0;
		
		touchWhoC(session);
	}

	private void touchWhoC(HttpSession session) {
		this.whoCreationDate = new Date();
		touchWho(session);
	}
	
	public void touchWho(HttpSession session) {
		this.whoLastUpdateDate = new Date();
	}
	
	public String getLastReportTime() {
		return Util.toSimpleString(getWhoLastUpdateDate());
	}
	
	public String getNetworkSpeedStr() {
		return networkSpeed == 0 ? "-" : Util.getSmartFileLength(networkSpeed);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Float getDiskSize() {
		return this.diskSize;
	}

	public void setDiskSize(Float diskSize) {
		this.diskSize = diskSize;
	}

	public Float getDiskFree() {
		return this.diskFree;
	}

	public void setDiskFree(Float diskFree) {
		this.diskFree = diskFree;
	}

	public Float getDiskUsedRatio() {
		return this.diskUsedRatio;
	}

	public void setDiskUsedRatio(Float diskUsedRatio) {
		this.diskUsedRatio = diskUsedRatio;
	}

	public String getReportIp() {
		return reportIp == null ? "" : reportIp;
	}

	public void setReportIp(String reportIp) {
		this.reportIp = reportIp;
	}

	public Integer getNetworkSpeed() {
		return networkSpeed;
	}

	public void setNetworkSpeed(Integer networkSpeed) {
		this.networkSpeed = networkSpeed;
	}

	public Date getWhoCreationDate() {
		return whoCreationDate;
	}

	public void setWhoCreationDate(Date whoCreationDate) {
		this.whoCreationDate = whoCreationDate;
	}

	public Date getWhoLastUpdateDate() {
		return whoLastUpdateDate;
	}

	public void setWhoLastUpdateDate(Date whoLastUpdateDate) {
		this.whoLastUpdateDate = whoLastUpdateDate;
	}

	@JsonIgnore
	public Rvm getRvm() {
		return rvm;
	}

	public void setRvm(Rvm rvm) {
		this.rvm = rvm;
	}

	// jason:playerversion: Player Version 서버에 기록(2014/07/10)
	public String getAppVersion() {
		return appVersion == null ? "" : appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	//-

	public String getAgentStart() {
		return Util.toSimpleString(getAgentStartDate());
	}

	public Date getAgentStartDate() {
		return agentStartDate;
	}

	public void setAgentStartDate(Date agentStartDate) {
		this.agentStartDate = agentStartDate;
	}

	public String getResTag() {
		return resTag == null ? "" : resTag;
	}

	public void setResTag(String resTag) {
		this.resTag = resTag;
	}

	public String getOpTag() {
		return opTag == null ? "" : opTag;
	}

	public void setOpTag(String opTag) {
		this.opTag = opTag;
	}
}
