package kr.co.r2cast.models.eco;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.annotation.JsonIgnore;

import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.utils.Util;

@Entity
@Table(name="ECO_MON_TASKS")
public class MonTask {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mon_task_seq_gen")
	@SequenceGenerator(name = "mon_task_seq_gen", sequenceName = "RVM_MON_TASKS_SEQ")
	@Column(name = "TASK_ID")
	private int id;
	
	@Column(name = "COMMAND", nullable = false, length = 50)
	private String command;
	
	@Column(name = "PARAMS", nullable = true, length = 2000)
	private String params;
	
	@Column(name = "STATUS", nullable = false, length = 1)
	private String status;
	
	@Column(name = "EXEC_TIME", nullable = true, length = 8)
	private String execTime;
	
	@Column(name = "DEST_DATE", nullable = false)
	private Date destDate;
	
	@Column(name = "CANCEL_DATE", nullable = false)
	private Date cancelDate;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SITE_ID", nullable = false)
	private Site site;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "RVM_ID", nullable = false)
	private Rvm rvm;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SCHD_TASK_ID", nullable = true)
	private RtnSchdTask rtnSchdTask;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SCHD_TASK_Rvm_ID", nullable = true)
	private RtnSchdTaskRvm rtnSchdTaskRvm;
	
	@Transient
	private String univCommand;
	
	@Transient
	private String remainingSecs;
	
	@Transient
	private String flagCode;
	
	@Transient
	private String statusTip;
	
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
	
	public MonTask() {}
	
	public MonTask(Site site, Rvm rvm, String command) {
		this(site, rvm, command, "", "", "1", new Date(), Util.setMaxTimeOfDate(new Date()), null,
				null, null);
	}
	
	public MonTask(Site site, Rvm rvm, String command, String params, String execTime, String status,
			Date destDate, Date cancelDate, RtnSchdTask rtnSchdTask, RtnSchdTaskRvm rtnSchdTaskRvm) {
		this(site, rvm, command, params, execTime, status, destDate, cancelDate, rtnSchdTask,
				rtnSchdTaskRvm, null);
	}

	public MonTask(Site site, Rvm rvm, String command, String params, String execTime, String status,
			Date destDate, Date cancelDate, RtnSchdTask rtnSchdTask, RtnSchdTaskRvm rtnSchdTaskRvm, 
			HttpSession session) {
		this.site = site;
		this.rvm = rvm;
		this.command = command;
		this.params = params;
		this.execTime = execTime;
		this.status = status;
		this.destDate = destDate;
		this.cancelDate = cancelDate;
		this.rtnSchdTask = rtnSchdTask;
		this.rtnSchdTaskRvm = rtnSchdTaskRvm;
		
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getExecTime() {
		return execTime;
	}

	public void setExecTime(String execTime) {
		this.execTime = execTime;
	}

	public Date getDestDate() {
		return destDate;
	}

	public void setDestDate(Date destDate) {
		this.destDate = destDate;
	}

	public Date getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public Rvm getRvm() {
		return rvm;
	}

	public void setRvm(Rvm rvm) {
		this.rvm = rvm;
	}

	public RtnSchdTask getRtnSchdTask() {
		return rtnSchdTask;
	}

	public void setRtnSchdTask(RtnSchdTask rtnSchdTask) {
		this.rtnSchdTask = rtnSchdTask;
	}

	public RtnSchdTaskRvm getRtnSchdTaskRvm() {
		return rtnSchdTaskRvm;
	}

	public void setRtnSchdTaskRvm(RtnSchdTaskRvm rtnSchdTaskRvm) {
		this.rtnSchdTaskRvm = rtnSchdTaskRvm;
	}

	@JsonIgnore
	public Date getWhoCreationDate() {
		return whoCreationDate;
	}

	public void setWhoCreationDate(Date whoCreationDate) {
		this.whoCreationDate = whoCreationDate;
	}

	@JsonIgnore
	public Date getWhoLastUpdateDate() {
		return whoLastUpdateDate;
	}

	public void setWhoLastUpdateDate(Date whoLastUpdateDate) {
		this.whoLastUpdateDate = whoLastUpdateDate;
	}

	@JsonIgnore
	public int getWhoCreatedBy() {
		return whoCreatedBy;
	}

	public void setWhoCreatedBy(int whoCreatedBy) {
		this.whoCreatedBy = whoCreatedBy;
	}

	@JsonIgnore
	public int getWhoLastUpdatedBy() {
		return whoLastUpdatedBy;
	}

	public void setWhoLastUpdatedBy(int whoLastUpdatedBy) {
		this.whoLastUpdatedBy = whoLastUpdatedBy;
	}

	@JsonIgnore
	public int getWhoLastUpdateLogin() {
		return whoLastUpdateLogin;
	}

	public void setWhoLastUpdateLogin(int whoLastUpdateLogin) {
		this.whoLastUpdateLogin = whoLastUpdateLogin;
	}

	public String getUnivCommand() {
		return univCommand;
	}

	public void setUnivCommand(String univCommand) {
		this.univCommand = univCommand;
	}
	
	public String getRemainingSecs() {
		return remainingSecs;
	}

	public void setRemainingSecs(String remainingSecs) {
		this.remainingSecs = remainingSecs;
	}

	public boolean isRoutineTask() {
		return rtnSchdTask != null;
	}
	
	public boolean isExecuted() {
		// jason:extendstbtaskstatus: STB 작업의 결과 상태 확장(2015/03/03)
		//return status != null && status.equals("3");
		return status != null && 
				(status.equals("3") || status.equals("S") || status.equals("P") || 
						status.equals("F") || status.equals("C"));
		//-
	}
	
	public Date getRequestedDate() {
		return whoCreationDate;
	}
	
	public Date getExecutedDate() {
		if (status == null || status.equals("1")) {
			return null;
		}
		
		return whoLastUpdateDate;
	}

	public String getFlagCode() {
		return flagCode;
	}

	public void setFlagCode(String flagCode) {
		this.flagCode = flagCode;
	}

	public String getStatusTip() {
		return statusTip;
	}

	public void setStatusTip(String statusTip) {
		this.statusTip = statusTip;
	}

	public String getSingleParamValue() {
		if (Util.isValid(params) && params.indexOf("=") > -1 && 
				params.indexOf("&") == -1 && !params.endsWith("=")) {
			return params.substring(params.indexOf("=") + 1);
		}
		
		return "";
	}
}
