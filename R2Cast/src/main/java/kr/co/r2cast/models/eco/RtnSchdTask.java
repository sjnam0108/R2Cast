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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.annotation.JsonIgnore;

import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.utils.Util;

@Entity
@Table(name="ECO_RTN_SCHD_TASKS", uniqueConstraints = 
	@javax.persistence.UniqueConstraint(columnNames = {"SITE_ID", "TASK_NAME"}))
public class RtnSchdTask {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "rtn_schd_task_seq_gen")
	@SequenceGenerator(name = "rtn_schd_task_seq_gen", sequenceName = "ECO_RTN_SCHD_TASKS_SEQ")
	@Column(name = "SCHD_TASK_ID")
	private int id;
	
	@Column(name = "TASK_NAME", nullable = false, length = 50)
	private String taskName;
	
	@Column(name = "COMMAND", nullable = false, length = 50)
	private String command;
	
	@Column(name = "MON_TIME", nullable = true, length = 8)
	private String monTime;
	
	@Column(name = "TUE_TIME", nullable = true, length = 8)
	private String tueTime;
	
	@Column(name = "WED_TIME", nullable = true, length = 8)
	private String wedTime;
	
	@Column(name = "THU_TIME", nullable = true, length = 8)
	private String thuTime;
	
	@Column(name = "FRI_TIME", nullable = true, length = 8)
	private String friTime;
	
	@Column(name = "SAT_TIME", nullable = true, length = 8)
	private String satTime;
	
	@Column(name = "SUN_TIME", nullable = true, length = 8)
	private String sunTime;
	
	@Column(name = "AUTO_CANCEL_MINS", nullable = false)
	private int autoCancelMins;
	
	@Column(name = "PUBLISHED", nullable = false, length = 1)
	private String published;
	
	@Transient
	private int rvmCount;
	
	@Transient
	private String univCommand;
	
	@Transient
	private String univAutoCancel;
	
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
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SITE_ID", nullable = false)
	private Site site;
	
	@OneToMany(mappedBy = "rtnSchdTask", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RtnSchdTaskRvm> rtnSchdTaskRvms = new HashSet<RtnSchdTaskRvm>(0);
	
	@OneToMany(mappedBy = "rtnSchdTask", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<MonTask> monTasks = new HashSet<MonTask>(0);
	
	public RtnSchdTask() {}
	
	public RtnSchdTask(Site site, String taskName, String command) {
		this(site, taskName, command, "00:00:00", "00:00:00", "00:00:00", "00:00:00", "00:00:00", 
				"", "", 10, "N", null);
	}
	
	public RtnSchdTask(Site site, String taskName, String command, String monTime, String tueTime, 
			String wedTime, String thuTime, String friTime, String satTime, String sunTime, 
			int autoCancelMins, String published) {
		this(site, taskName, command, monTime, tueTime, wedTime, thuTime, friTime, satTime, sunTime, autoCancelMins, 
				published, null);
	}

	public RtnSchdTask(Site site, String taskName, String command, String monTime, String tueTime, 
			String wedTime, String thuTime, String friTime, String satTime, String sunTime, 
			int autoCancelMins, String published, HttpSession session) {
		this.site = site;
		
		this.taskName = taskName;
		this.command = command;
		
		this.monTime = monTime;
		this.tueTime = tueTime;
		this.wedTime = wedTime;
		this.thuTime = thuTime;
		this.friTime = friTime;
		this.satTime = satTime;
		this.sunTime = sunTime;
		
		this.autoCancelMins = autoCancelMins;
		this.published = published;
		
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

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getMonTime() {
		return monTime;
	}

	public void setMonTime(String monTime) {
		this.monTime = monTime;
	}

	public String getTueTime() {
		return tueTime;
	}

	public void setTueTime(String tueTime) {
		this.tueTime = tueTime;
	}

	public String getWedTime() {
		return wedTime;
	}

	public void setWedTime(String wedTime) {
		this.wedTime = wedTime;
	}

	public String getThuTime() {
		return thuTime;
	}

	public void setThuTime(String thuTime) {
		this.thuTime = thuTime;
	}

	public String getFriTime() {
		return friTime;
	}

	public void setFriTime(String friTime) {
		this.friTime = friTime;
	}

	public String getSatTime() {
		return satTime;
	}

	public void setSatTime(String satTime) {
		this.satTime = satTime;
	}

	public String getSunTime() {
		return sunTime;
	}

	public void setSunTime(String sunTime) {
		this.sunTime = sunTime;
	}

	public int getAutoCancelMins() {
		return autoCancelMins;
	}

	public void setAutoCancelMins(int autoCancelMins) {
		this.autoCancelMins = autoCancelMins;
	}

	public String getPublished() {
		return published;
	}

	public void setPublished(String published) {
		this.published = published;
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

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public int getRvmCount() {
		return rvmCount;
	}

	public void setRvmCount(int rvmCount) {
		this.rvmCount = rvmCount;
	}

	public String getUnivCommand() {
		return univCommand;
	}

	public void setUnivCommand(String univCommand) {
		this.univCommand = univCommand;
	}

	public String getUnivAutoCancel() {
		return univAutoCancel;
	}

	public void setUnivAutoCancel(String univAutoCancel) {
		this.univAutoCancel = univAutoCancel;
	}

	@JsonIgnore
	public Set<RtnSchdTaskRvm> getRtnSchdTaskRvms() {
		return rtnSchdTaskRvms;
	}

	public void setRtnSchdTaskRvms(Set<RtnSchdTaskRvm> rtnSchdTaskRvms) {
		this.rtnSchdTaskRvms = rtnSchdTaskRvms;
	}

	@JsonIgnore
	public Set<MonTask> getMonTasks() {
		return monTasks;
	}

	public void setMonTasks(Set<MonTask> monTasks) {
		this.monTasks = monTasks;
	}
}
