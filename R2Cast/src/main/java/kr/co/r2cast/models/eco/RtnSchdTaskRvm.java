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
@Table(name="ECO_RTN_SCHD_TASK_RVMS", uniqueConstraints = 
	@javax.persistence.UniqueConstraint(columnNames = {"SCHD_TASK_ID", "RVM_ID"}))
public class RtnSchdTaskRvm {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "rtn_schd_task_rvm_seq_gen")
	@SequenceGenerator(name = "rtn_schd_task_rvm_seq_gen", sequenceName = "ECO_RTN_SCHD_TASK_RVMS_SEQ")
	@Column(name = "SCHD_TASK_RVM_ID")
	private int id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SITE_ID", nullable = false)
	private Site site;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SCHD_TASK_ID", nullable = false)
	private RtnSchdTask rtnSchdTask;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "RVM_ID", nullable = false)
	private Rvm rvm;
	
	@OneToMany(mappedBy = "rtnSchdTaskRvm", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<MonTask> monTasks = new HashSet<MonTask>(0);
	
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	@Column(name = "CREATED_BY", nullable = false)
	private int whoCreatedBy;
	
	@Column(name = "LAST_UPDATE_LOGIN", nullable = false)
	private int whoLastUpdateLogin;
	
	@Transient
	private Date destDate;
	
	@Transient
	private Date cancelDate;

	
	public RtnSchdTaskRvm() {}
	
	public RtnSchdTaskRvm(Site site, RtnSchdTask rtnSchdTask, Rvm rvm) {
		this(site, rtnSchdTask, rvm, null);
	}
	
	public RtnSchdTaskRvm(Site site, RtnSchdTask rtnSchdTask, Rvm rvm, HttpSession session) {
		this.site = site;
		this.rtnSchdTask = rtnSchdTask;
		this.rvm = rvm;
		
		touchWhoC(session);
	}

	private void touchWhoC(HttpSession session) {
		this.whoCreatedBy = Util.loginUserId(session);
		this.whoCreationDate = new Date();
		this.whoLastUpdateLogin = Util.loginId(session);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public RtnSchdTask getRtnSchdTask() {
		return rtnSchdTask;
	}

	public void setRtnSchdTask(RtnSchdTask rtnSchdTask) {
		this.rtnSchdTask = rtnSchdTask;
	}

	public Rvm getRvm() {
		return rvm;
	}

	public void setRvm(Rvm rvm) {
		this.rvm = rvm;
	}

	public Date getWhoCreationDate() {
		return whoCreationDate;
	}

	public void setWhoCreationDate(Date whoCreationDate) {
		this.whoCreationDate = whoCreationDate;
	}

	public int getWhoCreatedBy() {
		return whoCreatedBy;
	}

	public void setWhoCreatedBy(int whoCreatedBy) {
		this.whoCreatedBy = whoCreatedBy;
	}

	public int getWhoLastUpdateLogin() {
		return whoLastUpdateLogin;
	}

	public void setWhoLastUpdateLogin(int whoLastUpdateLogin) {
		this.whoLastUpdateLogin = whoLastUpdateLogin;
	}

	@JsonIgnore
	public Set<MonTask> getMonTasks() {
		return monTasks;
	}

	public void setMonTasks(Set<MonTask> monTasks) {
		this.monTasks = monTasks;
	}

	@JsonIgnore
	public Date getDestDate() {
		return destDate;
	}

	public void setDestDate(Date destDate) {
		this.destDate = destDate;
	}

	@JsonIgnore
	public Date getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}
	
}
