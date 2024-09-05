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
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.annotation.JsonIgnore;

import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.utils.Util;

@Entity
@Table(name="ECO_RVM_GROUPS", uniqueConstraints = 
	@javax.persistence.UniqueConstraint(columnNames = {"SITE_ID", "RVM_GROUP_NAME"}))
public class RvmGroup {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "rvm_group_seq_gen")
	@SequenceGenerator(name = "rvm_group_seq_gen", sequenceName = "ECO_RVM_GROUPS_SEQ")
	@Column(name = "RVM_GROUP_ID")
	private int id;
	
	@Column(name = "RVM_GROUP_NAME", nullable = false, length = 50)
	private String rvmGroupName;
	
	@Column(name = "CATEGORY", nullable = false, length = 1)
	private String category;
	
	@Column(name = "VIEW_TYPE", nullable = false, length = 1)
	private String viewType;
	
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
	
	@OneToMany(mappedBy = "rvmGroup", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RvmGroupRvm> rvmGroupRvms = new HashSet<RvmGroupRvm>(0);
	
	@OneToMany(mappedBy = "rvmGroup", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RvmGroupUser> rvmGroupUsers = new HashSet<RvmGroupUser>(0);
	
	public RvmGroup() {}
	
	public RvmGroup(Site site, String rvmGroupName, String category, String viewType) {
		this(site, rvmGroupName, category, viewType, null);
	}
	
	public RvmGroup(Site site, String rvmGroupName, String category, String viewType, 
			HttpSession session) {
		this.site = site;
		this.rvmGroupName = rvmGroupName;
		this.category = category;
		this.viewType = viewType;
		
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

	public String getRvmGroupName() {
		return rvmGroupName;
	}

	public void setRvmGroupName(String rvmGroupName) {
		this.rvmGroupName = rvmGroupName;
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

	@JsonIgnore
	public Set<RvmGroupRvm> getRvmGroupRvms() {
		return rvmGroupRvms;
	}

	public void setRvmGroupRvms(Set<RvmGroupRvm> rvmGroupRvms) {
		this.rvmGroupRvms = rvmGroupRvms;
	}

	@JsonIgnore
	public Set<RvmGroupUser> getRvmGroupUsers() {
		return rvmGroupUsers;
	}

	public void setRvmGroupUsers(Set<RvmGroupUser> rvmGroupUsers) {
		this.rvmGroupUsers = rvmGroupUsers;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getViewType() {
		return viewType;
	}

	public void setViewType(String viewType) {
		this.viewType = viewType;
	}
}
