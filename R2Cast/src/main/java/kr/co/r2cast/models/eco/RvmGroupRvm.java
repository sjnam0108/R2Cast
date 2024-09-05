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
import javax.servlet.http.HttpSession;

import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.utils.Util;

@Entity
@Table(name="ECO_RVM_GROUP_RVMS", uniqueConstraints = 
	@javax.persistence.UniqueConstraint(columnNames = {"RVM_GROUP_ID", "RVM_ID"}))
public class RvmGroupRvm {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "rvm_group_rvm_seq_gen")
	@SequenceGenerator(name = "rvm_group_rvm_seq_gen", sequenceName = "ECO_RVM_GROUP_RVMS_SEQ")
	@Column(name = "RVM_GROUP_RVM_ID")
	private int id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SITE_ID", nullable = false)
	private Site site;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "RVM_GROUP_ID", nullable = false)
	private RvmGroup rvmGroup;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "RVM_ID", nullable = false)
	private Rvm rvm;
	
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	@Column(name = "CREATED_BY", nullable = false)
	private int whoCreatedBy;
	
	@Column(name = "LAST_UPDATE_LOGIN", nullable = false)
	private int whoLastUpdateLogin;

	
	public RvmGroupRvm() {}
	
	public RvmGroupRvm(Site site, RvmGroup rvmGroup, Rvm rvm) {
		this(site, rvmGroup, rvm, null);
	}
	
	public RvmGroupRvm(Site site, RvmGroup rvmGroup, Rvm rvm, HttpSession session) {
		this.site = site;
		this.rvmGroup = rvmGroup;
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

	public RvmGroup getRvmGroup() {
		return rvmGroup;
	}

	public void setRvmGroup(RvmGroup rvmGroup) {
		this.rvmGroup = rvmGroup;
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

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}
}
