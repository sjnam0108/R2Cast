package kr.co.r2cast.models.eco;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.annotation.JsonIgnore;

import kr.co.r2cast.utils.Util;

@Entity
@Table(name="ECO_UPD_SETUP_FILES")
public class UpdSetupFile {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "upd_setup_file_seq_gen")
	@SequenceGenerator(name = "upd_setup_file_seq_gen", sequenceName = "ECO_UPD_SETUP_FILES_SEQ")
	@Column(name = "SETUP_FILE_ID")
	private int id;
	
	@Column(name = "FILENAME", nullable = false, length = 50, unique = true)
	private String filename;
	
	@Column(name = "FILE_LENGTH", nullable = false)
	private long fileLength;
	
	@Column(name = "FILE_TYPE", nullable = false, length = 1)
	private String fileType;
	
	@Column(name = "RELEASE_DATE", nullable = false)
	private Date releaseDate;
	
	@Column(name = "DESC_ENG", nullable = false, length = 2000)
	private String descEng;
	
	@Column(name = "DESC_LOCAL", nullable = true, length = 2000)
	private String descLocal;
	
	@Column(name = "PUBLISHED", nullable = false, length = 1)
	private String published;
	
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

	public UpdSetupFile() {}
	
	public UpdSetupFile(String filename, long fileLength, String fileType, Date releaseDate,
			String descEng, String descLocal, String published) {
		this(filename, fileLength, fileType, releaseDate, descEng, descLocal, published, null);
	}
	
	public UpdSetupFile(String filename, long fileLength, String fileType, Date releaseDate,
			String descEng, String descLocal, String published, HttpSession session) {
		this.filename = filename;
		this.fileLength = fileLength;
		this.fileType = fileType;
		this.releaseDate = releaseDate;
		this.descEng = descEng;
		this.descLocal = descLocal;
		this.published = published;
		
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

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public long getFileLength() {
		return fileLength;
	}

	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getDescEng() {
		return descEng;
	}

	public void setDescEng(String descEng) {
		this.descEng = descEng;
	}

	public String getDescLocal() {
		return descLocal;
	}

	public void setDescLocal(String descLocal) {
		this.descLocal = descLocal;
	}

	public String getPublished() {
		return published;
	}

	public void setPublished(String published) {
		this.published = published;
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

	public String getSmartLength() {
		return Util.getSmartFileLength(fileLength);
	}
	
	public String getProgType() {
		if (!Util.isValid(filename)) {
			return "";
		} else if (filename.toLowerCase().indexOf("player") > -1) {
			return "P";
		} else if (filename.toLowerCase().indexOf("agent") > -1) {
			return "A";
		} else {
			return "";
		}
	}
	
	public String getEdition() {
		if (!Util.isValid(filename) || filename.indexOf("_") == -1) {
			return "";
		} else {
			try {
				String tmp = filename.substring(0, filename.indexOf("_"));
				return tmp.substring(tmp.length() - 2);
			} catch (Exception e) {}
			
			return "";
		}
	}
	
	public String getVersion() {
		if (!Util.isValid(filename) || filename.indexOf("_") == -1) {
			return "";
		} else {
			try {
				String tmp = filename.substring(filename.indexOf("_") + 1);
				tmp = tmp.substring(tmp.indexOf("_") + 1);
				
				if (tmp.toLowerCase().endsWith(".apk")) {
					return tmp.substring(0, tmp.length() - 4);
				}
			} catch (Exception e) {}
			
			return "";
		}
	}
	
	@JsonIgnore
	public int getVersionAsInt() {
		return Util.getVersionAsInt(getVersion());
	}
}