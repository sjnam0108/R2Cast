package kr.co.r2cast.models.fnd;

import java.util.Comparator;
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

import kr.co.r2cast.utils.Util;

@Entity
@Table(name="FND_MENUS")
public class Menu {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "menu_seq_gen")
	@SequenceGenerator(name = "menu_seq_gen", sequenceName = "FND_MENUS_SEQ")
	@Column(name = "MENU_ID")
	private int id;
	
	@Column(name = "UKID", nullable = false, length = 20, unique = true)
	private String ukid;
	
	@Column(name = "URL", length = 50)
	private String url;
	
	@Column(name = "ICON_TYPE", length = 50)
	private String iconType;
	
	@Column(name = "DISP_GROUP", length = 20)
	private String dispGroup;
	
	@Column(name = "SIBLING_SEQ", nullable = false)
	private Integer siblingSeq;
	
	@Column(name = "SITE_SELECTOR", nullable = false)
	private boolean siteSelector; 
	
	@Column(name = "CUSTOMIZED", nullable = false)
	private boolean customized; 
	
	@Column(name = "USER_FRIENDLY", nullable = false)
	private boolean userFriendly; 
	
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
	
	@ManyToOne
	@JoinColumn(name = "PARENT_ID")
	private Menu parent;
	
	@OneToMany(mappedBy = "parent", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private Set<Menu> subMenus = new HashSet<Menu>(0);
	
	public Menu() {}

	public Menu(String ukid, Integer siblingSeq) {
		this(ukid, null, null, null, siblingSeq, false, null);
	}

	public Menu(String ukid, Integer siblingSeq, String dispGroup) {
		this(ukid, null, null, dispGroup, siblingSeq, false, null);
	}

	public Menu(String ukid, Integer siblingSeq, String iconType, String dispGroup) {
		this(ukid, null, iconType, dispGroup, siblingSeq, false, null);
	}

	public Menu(String ukid, String url, String iconType, String dispGroup, Integer siblingSeq) {
		this(ukid, url, iconType, dispGroup, siblingSeq, false, null);
	}

	public Menu(String ukid, String url, String iconType, String dispGroup, Integer siblingSeq, 
			boolean siteSelector) {
		this(ukid, url, iconType, dispGroup, siblingSeq, siteSelector, null);
	}
	
	public Menu(String ukid, String url, String iconType, String dispGroup, boolean siteSelector, 
			HttpSession session) {
		this(ukid, url, iconType, dispGroup, 1000, siteSelector, session);
	}
	
	public Menu(String ukid, String url, String iconType, String dispGroup, Integer siblingSeq, 
			boolean siteSelector, HttpSession session) {
		this.ukid = ukid;
		this.url = url;
		this.iconType = iconType;
		this.dispGroup = dispGroup;
		this.siblingSeq = siblingSeq;
		this.siteSelector = siteSelector;
		
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

	public String getUkid() {
		return ukid;
	}

	public void setUkid(String ukid) {
		this.ukid = ukid;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getSiblingSeq() {
		return siblingSeq;
	}

	public void setSiblingSeq(Integer siblingSeq) {
		this.siblingSeq = siblingSeq;
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

	public Menu getParent() {
		return parent;
	}

	public void setParent(Menu parent) {
		this.parent = parent;
	}

	public Set<Menu> getSubMenus() {
		return subMenus;
	}

	public void setSubMenus(Set<Menu> subMenus) {
		this.subMenus = subMenus;
	}
	
    public String getIconType() {
		return iconType;
	}

	public void setIconType(String iconType) {
		this.iconType = iconType;
	}

	public String getDispGroup() {
		return dispGroup;
	}

	public void setDispGroup(String dispGroup) {
		this.dispGroup = dispGroup;
	}

	public boolean isSiteSelector() {
		return siteSelector;
	}

	public void setSiteSelector(boolean siteSelector) {
		this.siteSelector = siteSelector;
	}

	public boolean isCustomized() {
		return customized;
	}

	public void setCustomized(boolean customized) {
		this.customized = customized;
	}

	public boolean isUserFriendly() {
		return userFriendly;
	}

	public void setUserFriendly(boolean userFriendly) {
		this.userFriendly = userFriendly;
	}

	public static Comparator<Menu> SiblingSeqComparator = new Comparator<Menu>() {
    	public int compare(Menu item1, Menu item2) {
    		return item1.getSiblingSeq().compareTo(item2.getSiblingSeq());
    	}
    };
}
