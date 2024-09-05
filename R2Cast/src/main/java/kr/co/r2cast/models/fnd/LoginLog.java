package kr.co.r2cast.models.fnd;

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
import javax.servlet.http.HttpSession;

@Entity
@Table(name="FND_LOGIN_LOGS")
public class LoginLog {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "login_log_seq_gen")
	@SequenceGenerator(name = "login_log_seq_gen", sequenceName = "FND_LOGIN_LOGS_SEQ")
	@Column(name = "LOGIN_LOG_ID")
	private int id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "USER_ID", nullable = false)
	private User user;
	
	@Column(name = "IP", nullable = false, length = 50)
	private String ip;
	
	@Column(name = "LOGOUT", nullable = false)
	private boolean logout; 
	
	@Column(name = "FORCED_LOGOUT", nullable = false)
	private boolean forcedLogout; 
	
	@Column(name = "LOGOUT_DATE", nullable = true)
	private Date logoutDate;
	
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	@Column(name = "LAST_UPDATE_DATE", nullable = false)
	private Date whoLastUpdateDate;
	
	public LoginLog() {}
	
	public LoginLog(String ip, User user) {
		this(ip, false, user, null);
	}
	
	public LoginLog(String ip, User user, HttpSession session) {
		this(ip, false, user, session);
	}
	
	public LoginLog(String ip, boolean logout, User user) {
		this(ip, logout, false, user, null);
	}
	
	public LoginLog(String ip, boolean logout, User user, HttpSession session) {
		this(ip, logout, false, user, session);
	}
	
	public LoginLog(String ip, boolean logout, boolean forcedLogout, User user, HttpSession session) {
		this.ip = ip;
		this.logout = logout;
		this.forcedLogout = forcedLogout;
		
		this.user = user;
		
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public boolean isLogout() {
		return logout;
	}

	public void setLogout(boolean logout) {
		this.logout = logout;
	}

	public boolean isForcedLogout() {
		return forcedLogout;
	}

	public void setForcedLogout(boolean forcedLogout) {
		this.forcedLogout = forcedLogout;
	}

	public Date getLogoutDate() {
		return logoutDate;
	}

	public void setLogoutDate(Date logoutDate) {
		this.logoutDate = logoutDate;
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
}
