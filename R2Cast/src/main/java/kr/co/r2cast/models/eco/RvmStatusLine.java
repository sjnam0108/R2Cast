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

import kr.co.r2cast.utils.SolUtil;
import kr.co.r2cast.utils.Util;

@Entity
@Table(name="ECO_RVM_STATUS_LINES", uniqueConstraints = 
	@javax.persistence.UniqueConstraint(columnNames = {"RVM_ID", "STATE_DATE"}))
public class RvmStatusLine {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "rvm_status_line_seq_gen")
	@SequenceGenerator(name = "rvm_status_line_seq_gen", sequenceName = "ECO_RVM_STATUS_LINES_SEQ")
	@Column(name = "RVM_STATUS_LINE_ID")
	private int id;
	
	@Column(name = "STATE_DATE", nullable = false)
	private Date stateDate;
	
	@Column(name = "LAST_STATUS", nullable = false, length = 1)
	private String lastStatus;
	
	@Column(name = "RUNNING_MIN_COUNT", nullable = false)
	private int runningMinCount;
	
	@Column(name = "STATUS_LINE", nullable = false, length = 1440)
	private String statusLine;
	
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	@Column(name = "LAST_UPDATE_DATE", nullable = false)
	private Date whoLastUpdateDate;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "RVM_ID", nullable = false)
	private Rvm rvm;
	
	@Transient
	private String siteName;
	
	public RvmStatusLine() {}
	
	public RvmStatusLine(Rvm rvm) {
		this(rvm, "2", 0, "", null);
	}
	
	public RvmStatusLine(Rvm rvm, String lastStatus, int runningMinCount,
			String statusLine, HttpSession session) {
		this.rvm = rvm;
		this.lastStatus = lastStatus;
		this.runningMinCount = runningMinCount;
		this.statusLine = SolUtil.getRvmStatusLine("", new Date(), lastStatus);
		
		this.stateDate = Util.removeTimeOfDate(new Date());
		
		touchWhoC(session);
	}

	private void touchWhoC(HttpSession session) {
		this.whoCreationDate = new Date();
		touchWho(session);
	}
	
	public void touchWho(HttpSession session) {
		this.whoLastUpdateDate = new Date();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getStateDate() {
		return stateDate;
	}

	public void setStateDate(Date stateDate) {
		this.stateDate = stateDate;
	}

	public String getLastStatus() {
		return lastStatus;
	}

	public void setLastStatus(String lastStatus) {
		this.lastStatus = lastStatus;
	}

	public int getRunningMinCount() {
		return runningMinCount;
	}

	public void setRunningMinCount(int runningMinCount) {
		this.runningMinCount = runningMinCount;
	}

	@JsonIgnore
	public String getStatusLine() {
		return statusLine;
	}

	public void setStatusLine(String statusLine) {
		this.statusLine = statusLine;
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

	public Rvm getRvm() {
		return rvm;
	}

	public void setRvm(Rvm rvm) {
		this.rvm = rvm;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
}
