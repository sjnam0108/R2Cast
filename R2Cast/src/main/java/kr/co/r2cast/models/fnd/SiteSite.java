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

import kr.co.r2cast.utils.Util;


@Entity
@Table(name="FND_SITE_SITES", uniqueConstraints = 
	@javax.persistence.UniqueConstraint(columnNames = {"PARENT_ID", "CHILD_ID"}))
public class SiteSite {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "site_site_seq_gen")
	@SequenceGenerator(name = "site_site_seq_gen", sequenceName = "FND_SITE_SITES_SEQ")
	@Column(name = "SITE_SITE_ID")
	private int id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "PARENT_ID", nullable = false)
	private Site parentSite;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CHILD_ID", nullable = false)
	private Site childSite;
	
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	@Column(name = "CREATED_BY", nullable = false)
	private int whoCreatedBy;
	
	@Column(name = "LAST_UPDATE_LOGIN", nullable = false)
	private int whoLastUpdateLogin;

	
	public SiteSite() {}
	
	public SiteSite(Site parentSite, Site childSite) {
		this(parentSite, childSite, null);
	}
	
	public SiteSite(Site parentSite, Site childSite, HttpSession session) {
		this.parentSite = parentSite;
		this.childSite = childSite;
		
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

	public Site getParentSite() {
		return parentSite;
	}

	public void setParentSite(Site parentSite) {
		this.parentSite = parentSite;
	}

	public Site getChildSite() {
		return childSite;
	}

	public void setChildSite(Site childSite) {
		this.childSite = childSite;
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
	
}
