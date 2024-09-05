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

import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.utils.Util;


@Entity
@Table(name="ECO_REC_DAILY_SUMMARIES", uniqueConstraints = 
	@javax.persistence.UniqueConstraint(columnNames = {"SITE_ID", "WORK_DATE"}))
public class RecDailySummary {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "rec_daily_summ_seq_gen")
	@SequenceGenerator(name = "rec_daily_summ_seq_gen", sequenceName = "ECO_REC_DAILY_SUMMS_SEQ")
	@Column(name = "REC_DAILY_SUMMARY_ID")
	private int id;
	
	@Column(name = "WORK_DATE", nullable = false)
	private Date workDate;
	
	@Column(name = "RVM_COUNT", nullable = false)
	private int rvmCount;
	
	@Column(name = "TOTAL_COUNT", nullable = false)
	private int totalCount;
	
	@Column(name = "AVG_RUN_MINS", nullable = false)
	private float avgRunningMins;
	
	@Column(name = "CALCULATED", nullable = false, length = 1)
	private String calculated;
	
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	@Column(name = "LAST_UPDATE_DATE", nullable = false)
	private Date whoLastUpdateDate;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SITE_ID", nullable = false)
	private Site site;
	
	@Transient
	private String siteName;
	
	public RecDailySummary() {}
	
	public RecDailySummary(Site site, Date workDate, int rvmCount, int totalCount, 
			long totalRunningMins, String calculated) {
		this.rvmCount = rvmCount;
		this.totalCount = totalCount;
		this.calculated = calculated;
		
		this.workDate = Util.removeTimeOfDate(workDate);
		this.site = site;
		
		if (totalRunningMins > 0 && rvmCount > 0) {
			this.avgRunningMins = (float)(Math.round((double)totalRunningMins / (double)rvmCount 
					* Math.pow(10, 1)) / Math.pow(10, 1));
		}
		
		Date now = new Date();
		
		this.whoCreationDate = now;
		this.whoLastUpdateDate = now;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getWorkDate() {
		return workDate;
	}

	public void setWorkDate(Date workDate) {
		this.workDate = workDate;
	}

	public int getRvmCount() {
		return rvmCount;
	}

	public void setRvmCount(int rvmCount) {
		this.rvmCount = rvmCount;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public float getAvgRunningMins() {
		return avgRunningMins;
	}

	public void setAvgRunningMins(float avgRunningMins) {
		this.avgRunningMins = avgRunningMins;
	}

	public String getCalculated() {
		return calculated;
	}

	public void setCalculated(String calculated) {
		this.calculated = calculated;
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

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getWorkDateDisp() {
		return Util.toSimpleString(getWorkDate(), "yyyy/MM/dd HH:mm");
	}
}
