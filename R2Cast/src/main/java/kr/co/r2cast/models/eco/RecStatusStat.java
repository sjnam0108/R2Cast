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

import org.hibernate.annotations.Index;

import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.utils.SolUtil;
import kr.co.r2cast.utils.Util;

@Entity
@Table(name="ECO_REC_STATUS_STATS", uniqueConstraints = 
	@javax.persistence.UniqueConstraint(columnNames = {"SITE_ID", "STATE_DATE"}))
public class RecStatusStat {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "rec_status_stat_seq_gen")
	@SequenceGenerator(name = "rec_status_stat_seq_gen", sequenceName = "ECO_REC_STATUS_STATS_SEQ")
	@Column(name = "REC_STATUS_STAT_ID")
	private int id;
	
	@Column(name = "STATE_DATE", nullable = false)
	private Date stateDate;
	
	@Column(name = "STATUS_6", nullable = false)
	private int status6;
	
	@Column(name = "STATUS_5", nullable = false)
	private int status5;
	
	@Column(name = "STATUS_4", nullable = false)
	private int status4;
	
	@Column(name = "STATUS_3", nullable = false)
	private int status3;
	
	@Column(name = "STATUS_2", nullable = false)
	private int status2;
	
	@Column(name = "STATUS_0", nullable = false)
	private int status0;
	
	@Column(name = "AVG_RUN_MINS", nullable = false)
	private float avgRunningMins;
	
	@Column(name = "STATE_TIME", nullable = true, length = 4)
	@Index(name = "ECO_REC_STATUS_STATS_STATE_TIME_IDX")
	private String stateTime;
	
	@Transient
	private String day;
	
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	@Column(name = "LAST_UPDATE_DATE", nullable = false)
	private Date whoLastUpdateDate;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SITE_ID", nullable = false)
	private Site site;
	
	@Transient
	private String siteName;
	
	public RecStatusStat() {}
	
	public RecStatusStat(Site site, Date stateDate, int status6, int status5, int status4, 
			int status3, int status2, int status0, long totalRunningMins) {
		this.status6 = status6;
		this.status5 = status5;
		this.status4 = status4;
		this.status3 = status3;
		this.status2 = status2;
		this.status0 = status0;
		
		this.stateDate = stateDate;
		this.site = site;
		
		this.stateTime = Util.toSimpleString(stateDate, "HHmm");
		
		if (totalRunningMins > 0 && getPlayingCount() > 0) {
			this.avgRunningMins = (float)(Math.round((double)totalRunningMins / (double)getPlayingCount() 
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

	public Date getStateDate() {
		return stateDate;
	}

	public void setStateDate(Date stateDate) {
		this.stateDate = stateDate;
	}

	public int getStatus6() {
		return status6;
	}

	public void setStatus6(int status6) {
		this.status6 = status6;
	}

	public int getStatus5() {
		return status5;
	}

	public void setStatus5(int status5) {
		this.status5 = status5;
	}

	public int getStatus4() {
		return status4;
	}

	public void setStatus4(int status4) {
		this.status4 = status4;
	}

	public int getStatus3() {
		return status3;
	}

	public void setStatus3(int status3) {
		this.status3 = status3;
	}

	public int getStatus2() {
		return status2;
	}

	public void setStatus2(int status2) {
		this.status2 = status2;
	}

	public int getStatus0() {
		return status0;
	}

	public void setStatus0(int status0) {
		this.status0 = status0;
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

	public float getAvgRunningMins() {
		return avgRunningMins;
	}

	public void setAvgRunningMins(float avgRunningMins) {
		this.avgRunningMins = avgRunningMins;
	}

	public String getStateDateDisp() {
		return Util.toSimpleString(getStateDate(), "yyyy/MM/dd HH:mm");
	}
	
	public boolean isValid() {
		return SolUtil.isDateDuring1Minute(whoLastUpdateDate);
	}
	
	public int getPlayingCount() {
		return status6 + status5 + status4 + status3 + status2;
	}
	
	public int getTotalCount() {
		return getPlayingCount() + status0;
	}

	public String getStateTime() {
		return stateTime;
	}

	public void setStateTime(String stateTime) {
		this.stateTime = stateTime;
	}

	public String getPlayingRatio() {
		int total = status6 + status5 + status4 + status3 + status2 + status0;
		if ( total == 0) {
			return "";
		}
		
		return Double.toString(Math.round((double)status6 / (double)total * 1000d) / 10d);
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}
	
	public long getStateDateLong()
	{
		return stateDate.getTime();
	}
}
