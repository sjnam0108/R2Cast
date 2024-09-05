package kr.co.r2cast.models.eco;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.annotation.JsonIgnore;

import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.utils.Util;

@Entity
@Table(name="ECO_RVMS", uniqueConstraints = {
	@javax.persistence.UniqueConstraint(columnNames = {"SITE_ID", "RVM_NAME"}),
})
public class Rvm {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "rvm_seq_gen")
	@SequenceGenerator(name = "rvm_seq_gen", sequenceName = "RVM_RVMS_SEQ")
	@Column(name = "RVM_ID")
	private int id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SITE_ID", nullable = false)
	private Site site;
	
	@Column(name = "RVM_NAME", nullable = false, length = 100)
	private String rvmName;
	
	@Column(name = "DEVICE_ID", length = 8)
	private String deviceID;
	
	@Column(name = "EXTERNAL_IP", nullable = false, length = 15)
	private String externalIp = "127.0.0.1";
	
	@Column(name = "EFFECTIVE_START_DATE", nullable = false)
	private Date effectiveStartDate;
	
	@Column(name = "EFFECTIVE_END_DATE")
	private Date effectiveEndDate;
	
	@Column(name = "MODEL", length = 100)
	private String model;
	
	@Column(name = "DISPLAY_PLACE", length = 100)
	private String displayPlace;
	
	@Column(name = "RVM_LATITUDE", nullable = true)
	private String rvmLatitude;
	
	@Column(name = "RVM_LONGITUDE", nullable = true)
	private String rvmLongitude;
	
	@Column(name = "SERVICE_TYPE", nullable = false, length = 1)
	private String serviceType;
	
	@Column(name = "RUNNING_MIN_COUNT", nullable = true)
	private int runningMinCount;
	
	@Column(name = "LAST_STATUS", nullable = true, length = 1)
	private String lastStatus;
	
	@Column(name = "LOCAL_CODE", nullable = true, length = 15)
	private String localCode;
	
	@Column(name = "LOCAL_NAME", nullable = true, length = 100)
	private String localName;
	
	@Column(name = "SERVICE_NO", nullable = true, length = 100)
	private String serviceNo;
	
	@Column(name = "STORE_CONTACT", nullable = true, length = 300)
	private String storeContact;
	
	@Column(name = "SALES_CONTACT", nullable = true, length = 300)
	private String salesContact;
	
	@Column(name = "MEMO", nullable = true, length = 300)
	private String memo;
	
	@Column(name = "SERIAL_NO", nullable = true, length = 20)
	private String serialNo;
	
	@Column(name = "AREA_CODE", nullable = true, length = 10)
	private String areaCode;
	
	@Column(name = "BRANCH_CODE", nullable = true, length = 20)
	private String branchCode;
	
	@Column(name = "BRANCH_NAME", nullable = true, length = 100)
	private String branchName;
	
	@Column(name = "IMPORT_REQ", nullable = true)
	private boolean importRequired;
	
	@Column(name = "RESULT_TYPE", nullable = true, length = 3)
	private String resultType;
	
	@Column(name = "CUST_ID_REQ", nullable = true)
	private boolean customerIdRequired;
	
	@Column(name = "REPORT_INTERVAL", nullable = true)
	private int reportInterval;

	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	@Column(name = "LAST_UPDATE_DATE", nullable = false)
	private Date whoLastUpdateDate;
	
	@Column(name = "CREATED_BY", nullable = false)
	private int whoCreatedBy;
	
	@Column(name = "LAST_UPDATED_BY", nullable = false)
	private int whoLastUpdatedBy;
	
	@Column(name = "LAST_UPDATE_LOGIN", nullable = false)
	private int whoLastUpdateLogin;
	
	
	//
	// 아래 transient 자료는 원래 자료형으로 하는 것이 맞으나,
	// 단순 출력용이고, 필터에도 사용되지 않기 때문에
	// 표현에 용이한 String으로 처리함
	//
	// 최근 트랜잰셕 번호
	@Transient
	private String lastTrxNo = "";
	
	// 최근 영수증 번호
	@Transient
	private String lastReceiptNo = "";
	
	// 최근 수거일시
	@Transient
	private String lastOpDt = "";
	
	// 누적 트랜잭션 건수
	@Transient
	private String cumTrxCount = "";
	
	// 누적 수거용기 수
	@Transient
	private String cumEmptiesCount = "";
	
	// 누적 영수증 총액
	@Transient
	private String cumReceiptAmount = "";
	

	@OneToOne(mappedBy = "rvm", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private RvmLastReport rvmLastReport;

	@OneToMany(mappedBy = "rvm", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RvmStatusLine> rvmStatusLines = new HashSet<RvmStatusLine>(0);

	@OneToMany(mappedBy = "rvm", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RvmTrx> rvmTrxs = new HashSet<RvmTrx>(0);

	@OneToMany(mappedBy = "rvm", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RvmTrxItem> rvmTrxItems = new HashSet<RvmTrxItem>(0);

	@OneToMany(mappedBy = "rvm", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<MonTask> monTasks = new HashSet<MonTask>(0);

	@OneToMany(mappedBy = "rvm", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RtnSchdTaskRvm> rtnSchdTaskRvms = new HashSet<RtnSchdTaskRvm>(0);
	
	@OneToMany(mappedBy = "rvmGroup", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RvmGroupRvm> rvmGroupRvms = new HashSet<RvmGroupRvm>(0);
	
	
	public Rvm() {}
	
	// AgentController에서 RVM info 정보 획득 시 이용
	public Rvm(Site site, String rvmName) {
		
	  	this(site, rvmName, null, null, "S", null);
	}
	

	// RVM 추가 시
	public Rvm(Site site, String rvmName, Date effectiveStartDate, Date effectiveEndDate, 
			String serviceType, HttpSession session) {
		
		this.site = site;
		this.rvmName = rvmName;
		this.effectiveStartDate = Util.removeTimeOfDate(effectiveStartDate == null ? new Date() : effectiveStartDate);
		this.effectiveEndDate = Util.setMaxTimeOfDate(effectiveEndDate);
		this.serviceType = serviceType;
		this.lastStatus = "0";
		
		touchWhoC(session);
	}

	private void touchWhoC(HttpSession session) {
		this.whoCreatedBy = Util.loginUserId(session);
		this.whoCreationDate = new Date();
		touchWho(session);
	}
	
	public void touchWho(HttpSession session) {
		this.whoLastUpdatedBy = Util.loginUserId(session);
		this.whoLastUpdateDate = new Date();
		this.whoLastUpdateLogin = Util.loginId(session);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int rvmId) {
		this.id = rvmId;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public String getExternalIp() {
		return externalIp;
	}

	public void setExternalIp(String externalIp) {
		this.externalIp = externalIp;
	}

	public Date getEffectiveStartDate() {
		return effectiveStartDate;
	}

	public void setEffectiveStartDate(Date effectiveStartDate) {
		this.effectiveStartDate = effectiveStartDate;
	}

	public Date getEffectiveEndDate() {
		return effectiveEndDate;
	}

	public void setEffectiveEndDate(Date effectiveEndDate) {
		this.effectiveEndDate = effectiveEndDate;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getDisplayPlace() {
		return displayPlace;
	}

	public void setDisplayPlace(String displayPlace) {
		this.displayPlace = displayPlace;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
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

	public int getWhoCreatedBy() {
		return whoCreatedBy;
	}

	public void setWhoCreatedBy(int whoCreatedBy) {
		this.whoCreatedBy = whoCreatedBy;
	}

	public int getWhoLastUpdatedBy() {
		return whoLastUpdatedBy;
	}

	public void setWhoLastUpdatedBy(int whoLastUpdatedBy) {
		this.whoLastUpdatedBy = whoLastUpdatedBy;
	}

	public int getWhoLastUpdateLogin() {
		return whoLastUpdateLogin;
	}

	public void setWhoLastUpdateLogin(int whoLastUpdateLogin) {
		this.whoLastUpdateLogin = whoLastUpdateLogin;
	}

	public RvmLastReport getRvmLastReport() {
		return rvmLastReport;
	}

	public void setRvmLastReport(RvmLastReport rvmLastReport) {
		this.rvmLastReport = rvmLastReport;
	}

	@JsonIgnore
	public Set<RvmStatusLine> getrvmStatusLines() {
		return rvmStatusLines;
	}

	public void setrvmStatusLines(Set<RvmStatusLine> rvmStatusLines) {
		this.rvmStatusLines = rvmStatusLines;
	}

	public int getRunningMinCount() {
		return runningMinCount;
	}

	public void setRunningMinCount(int runningMinCount) {
		this.runningMinCount = runningMinCount;
	}

	public String getLastStatus() {
		return lastStatus;
	}

	public void setLastStatus(String lastStatus) {
		this.lastStatus = lastStatus;
	}

	public String getLocalCode() {
		return localCode;
	}

	public void setLocalCode(String localCode) {
		this.localCode = localCode;
	}

	public String getLocalName() {
		return localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	public String getServiceNo() {
		return serviceNo;
	}

	public void setServiceNo(String serviceNo) {
		this.serviceNo = serviceNo;
	}

	public String getStoreContact() {
		return storeContact;
	}

	public void setStoreContact(String storeContact) {
		this.storeContact = storeContact;
	}

	public String getSalesContact() {
		return salesContact;
	}

	public void setSalesContact(String salesContact) {
		this.salesContact = salesContact;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public String getAreaCode() {
		return areaCode;
	}
	
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	
	public String getResultType() {
		return resultType;
	}
	
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}
	
	public Boolean getImportRequired() {
		return importRequired;
	}
	
	public void setImportRequired(boolean importRequired) {
		this.importRequired = importRequired;
	}
	
	public String getBranchCode() {
		return branchCode;
	}
	
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	
	public String getBranchName() {
		return branchName;
	}
	
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	
	public boolean getCustomerIdRequired() {
		return customerIdRequired;
	}
	
	public void setCustomerIdRequired(boolean customerIdRequired) {
		this.customerIdRequired = customerIdRequired;
	}
	
	public String getMemo() {
		return memo;
	}
	
	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getDeviceID() {
		return deviceID;
	}

	public int getReportInterval() {
		return reportInterval;
	}

	public void setReportInterval(int reportInterval) {
		this.reportInterval = reportInterval;
	}

	public Set<RvmStatusLine> getRvmStatusLines() {
		return rvmStatusLines;
	}

	public void setRvmStatusLines(Set<RvmStatusLine> rvmStatusLines) {
		this.rvmStatusLines = rvmStatusLines;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public String getRvmName() {
		return rvmName;
	}

	public void setRvmName(String rvmName) {
		this.rvmName = rvmName;
	}

	public String getRvmLatitude() {
		return rvmLatitude;
	}

	public void setRvmLatitude(String rvmLatitude) {
		this.rvmLatitude = rvmLatitude;
	}

	public String getRvmLongitude() {
		return rvmLongitude;
	}

	public void setRvmLongitude(String rvmLongitude) {
		this.rvmLongitude = rvmLongitude;
	}

	public boolean isEffective() {
		Date now = new Date();
		if (effectiveStartDate == null || now.before(effectiveStartDate)) {
			return false;
		}
		
		if (effectiveEndDate != null && now.after(effectiveEndDate)) {
			return false;
		}

		return true;
	}
	
	public String getEffectiveMode() {
		return isEffective() ? "Y" : "N";
	}

	public String getAssetLastStatus() {
		if (isEffective()) {
			return lastStatus;
		} else {
			return "9";
		}
	}

	
	@JsonIgnore
	public Set<RvmTrx> getRvmTrxs() {
		return rvmTrxs;
	}

	public void setRvmTrxs(Set<RvmTrx> rvmTrxs) {
		this.rvmTrxs = rvmTrxs;
	}

	@JsonIgnore
	public Set<RvmTrxItem> getRvmTrxItems() {
		return rvmTrxItems;
	}

	public void setRvmTrxItems(Set<RvmTrxItem> rvmTrxItems) {
		this.rvmTrxItems = rvmTrxItems;
	}

	@JsonIgnore
	public Set<MonTask> getMonTasks() {
		return monTasks;
	}

	public void setMonTasks(Set<MonTask> monTasks) {
		this.monTasks = monTasks;
	}

	@JsonIgnore
	public Set<RtnSchdTaskRvm> getRtnSchdTaskRvms() {
		return rtnSchdTaskRvms;
	}

	public void setRtnSchdTaskRvms(Set<RtnSchdTaskRvm> rtnSchdTaskRvms) {
		this.rtnSchdTaskRvms = rtnSchdTaskRvms;
	}

	@JsonIgnore
	public Set<RvmGroupRvm> getRvmGroupRvms() {
		return rvmGroupRvms;
	}

	public void setRvmGroupRvms(Set<RvmGroupRvm> rvmGroupRvms) {
		this.rvmGroupRvms = rvmGroupRvms;
	}

	public String getLastTrxNo() {
		return lastTrxNo;
	}

	public void setLastTrxNo(String lastTrxNo) {
		this.lastTrxNo = lastTrxNo;
	}

	public String getLastReceiptNo() {
		return lastReceiptNo;
	}

	public void setLastReceiptNo(String lastReceiptNo) {
		this.lastReceiptNo = lastReceiptNo;
	}

	public String getLastOpDt() {
		return lastOpDt;
	}

	public void setLastOpDt(String lastOpDt) {
		this.lastOpDt = lastOpDt;
	}

	public String getCumTrxCount() {
		return cumTrxCount;
	}

	public void setCumTrxCount(String cumTrxCount) {
		this.cumTrxCount = cumTrxCount;
	}

	public String getCumEmptiesCount() {
		return cumEmptiesCount;
	}

	public void setCumEmptiesCount(String cumEmptiesCount) {
		this.cumEmptiesCount = cumEmptiesCount;
	}

	public String getCumReceiptAmount() {
		return cumReceiptAmount;
	}

	public void setCumReceiptAmount(String cumReceiptAmount) {
		this.cumReceiptAmount = cumReceiptAmount;
	}
}
