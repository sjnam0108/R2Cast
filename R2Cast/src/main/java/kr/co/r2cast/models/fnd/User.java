package kr.co.r2cast.models.fnd;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.annotation.JsonIgnore;

import kr.co.r2cast.utils.Util;

@Entity
@Table(name="FND_USERS")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "user_seq_gen")
	@SequenceGenerator(name = "user_seq_gen", sequenceName = "FND_USERS_SEQ")
	@Column(name = "USER_ID")
	private int id;
	
	@Column(name = "USERNAME", nullable = false, length = 50, unique = true)
	private String username;
	
	@Column(name = "FAMILIAR_NAME", nullable = false, length = 50)
	private String familiarName;
	
	@Column(name = "SALT", nullable = false, length = 22)
	private String salt;
	
	@Column(name = "PASSWORD", nullable = false, length = 50)
	private String password;
	
	@Transient
	private String newPassword;
	
	@Column(name = "EFFECTIVE_START_DATE", nullable = false)
	private Date effectiveStartDate;
	
	@Column(name = "EFFECTIVE_END_DATE")
	private Date effectiveEndDate;
	
	@Column(name = "PASSWORD_UPDATE_DATE", nullable = false)
	private Date passwordUpdateDate;
	
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
	
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<LoginLog> loginLogs = new HashSet<LoginLog>(0);
	
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<SiteUser> siteUsers = new HashSet<SiteUser>(0);
	
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<UserPrivilege> userPrivileges = new HashSet<UserPrivilege>(0);
	
	// [SignCast] ext ----------------------------------------------------------- start
	//
	//
	
//	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
//	private Set<StbGroupUser> stbGroupUsers = new HashSet<StbGroupUser>(0);
//	
//	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
//	private Set<AdvertiserUser> advertiserUsers = new HashSet<AdvertiserUser>(0);
//	
//	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
//	private Set<AppUser> appUsers = new HashSet<AppUser>(0);
	
	//
	//
	// [SignCast] ext ------------------------------------------------------------- end
	
	public User() {}

	public User(String username, String familiarName, String password, 
			Date effectiveStartDate, Date effectiveEndDate) {
		this(username, familiarName, password, effectiveStartDate, effectiveEndDate, null);
	}

	public User(String username, String familiarName, String password, 
			Date effectiveStartDate, Date effectiveEndDate, HttpSession session) {
		this.username = username;
		this.familiarName = familiarName;
		this.salt = Util.getRandomSalt();
		this.password = Util.encrypt(password, salt);
		this.effectiveStartDate = Util.removeTimeOfDate(effectiveStartDate == null ? new Date() : effectiveStartDate);
		this.effectiveEndDate = Util.setMaxTimeOfDate(effectiveEndDate);

		touchWhoC(session);
	}
	
	public User(String username, String familiarName, String salt, String password, 
			Date effectiveStartDate, Date effectiveEndDate) {
		this(username, familiarName, salt, password, effectiveStartDate, effectiveEndDate, null);
	}
	
	public User(String username, String familiarName, String salt, String password, 
			Date effectiveStartDate, Date effectiveEndDate, HttpSession session) {
		this.username = username;
		this.familiarName = familiarName;
		this.salt = salt;
		this.password = password;
		this.effectiveStartDate = Util.removeTimeOfDate(effectiveStartDate == null ? new Date() : effectiveStartDate);
		this.effectiveEndDate = Util.setMaxTimeOfDate(effectiveEndDate);

		touchWhoC(session);
	}

	private void touchWhoC(HttpSession session) {
		this.whoCreatedBy = Util.loginUserId(session);
		this.whoCreationDate = new Date();
		this.passwordUpdateDate = new Date();
		
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFamiliarName() {
		return familiarName;
	}

	public void setFamiliarName(String familiarName) {
		this.familiarName = familiarName;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	@JsonIgnore
	public Set<LoginLog> getLoginLogs() {
		return loginLogs;
	}

	public void setLoginLogs(Set<LoginLog> loginLogs) {
		this.loginLogs = loginLogs;
	}

	@JsonIgnore
	public Set<SiteUser> getSiteUsers() {
		return siteUsers;
	}

	public void setSiteUsers(Set<SiteUser> siteUsers) {
		this.siteUsers = siteUsers;
	}

	@JsonIgnore
	public Set<UserPrivilege> getUserPrivileges() {
		return userPrivileges;
	}

	public void setUserPrivileges(Set<UserPrivilege> userPrivileges) {
		this.userPrivileges = userPrivileges;
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

	public Date getPasswordUpdateDate() {
		return passwordUpdateDate;
	}

	public void setPasswordUpdateDate(Date passwordUpdateDate) {
		this.passwordUpdateDate = passwordUpdateDate;
	}

	// [SignCast] ext ----------------------------------------------------------- start
	//
	//
	
//	@JsonIgnore
//	public Set<StbGroupUser> getStbGroupUsers() {
//		return stbGroupUsers;
//	}
//
//	public void setStbGroupUsers(Set<StbGroupUser> stbGroupUsers) {
//		this.stbGroupUsers = stbGroupUsers;
//	}
//
//	@JsonIgnore
//	public Set<AdvertiserUser> getAdvertiserUsers() {
//		return advertiserUsers;
//	}
//
//	public void setAdvertiserUsers(Set<AdvertiserUser> advertiserUsers) {
//		this.advertiserUsers = advertiserUsers;
//	}
//
//	@JsonIgnore
//	public Set<AppUser> getAppUsers() {
//		return appUsers;
//	}
//
//	public void setAppUsers(Set<AppUser> appUsers) {
//		this.appUsers = appUsers;
//	}
	
	//
	//
	// [SignCast] ext ------------------------------------------------------------- end
}
