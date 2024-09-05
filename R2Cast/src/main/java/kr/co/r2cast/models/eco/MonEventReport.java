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

import kr.co.r2cast.models.fnd.Site;

@Entity
@Table(name="ECO_MON_EVNT_RPTS")
public class MonEventReport {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mon_evnt_rpt_seq_gen")
	@SequenceGenerator(name = "mon_evnt_rpt_seq_gen", sequenceName = "ECO_MON_EVNT_RPTS_SEQ")
	@Column(name = "EVENT_REPORT_ID")
	private int id;
	
	@Column(name = "REPORT_TYPE", nullable = false, length = 1)
	private String reportType;
	
	@Column(name = "CATEGORY", nullable = false, length = 1)
	private String category;
	
	@Column(name = "EQUIP_TYPE", nullable = false, length = 1)
	private String equipType;
	
	@Column(name = "EQUIP_ID", nullable = false)
	private int equipId;
	
	@Column(name = "EQUIP_NAME", nullable = false, length = 100)
	private String equipName;
	
	@Column(name = "EVENT", nullable = false, length = 20)
	private String event;
	
	@Column(name = "DETAILS", nullable = true, length = 300)
	private String details;
	
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SITE_ID", nullable = false)
	private Site site;

	
	public MonEventReport() {}
	
	public MonEventReport(Site site, String category, String reportType, String equipType,
			int equipId, String equipName, String event, String details) {
		this.site = site;
		this.category = category;
		this.reportType = reportType;
		this.equipType = equipType;
		this.equipId = equipId;
		this.equipName = equipName;
		this.event = event;
		this.details = details;
		
		this.whoCreationDate = new Date();
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

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public String getEquipType() {
		return equipType;
	}

	public void setEquipType(String equipType) {
		this.equipType = equipType;
	}

	public int getEquipId() {
		return equipId;
	}

	public void setEquipId(int equipId) {
		this.equipId = equipId;
	}

	public String getEquipName() {
		return equipName;
	}

	public void setEquipName(String equipName) {
		this.equipName = equipName;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Date getWhoCreationDate() {
		return whoCreationDate;
	}

	public void setWhoCreationDate(Date whoCreationDate) {
		this.whoCreationDate = whoCreationDate;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}
