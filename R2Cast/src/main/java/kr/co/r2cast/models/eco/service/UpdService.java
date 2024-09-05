package kr.co.r2cast.models.eco.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.eco.UpdSetupFile;

@Transactional
public interface UpdService {
	// Common
	public void flush();
	
	
	//
	// for UpdSetupFileDao
	//
	// Common
	public UpdSetupFile getUpdSetupFile(int id);
	public List<UpdSetupFile> getUpdSetupFileList();
	public void saveOrUpdate(UpdSetupFile updSetupFile);
	public void deleteUpdSetupFile(UpdSetupFile updSetupFile);
	public void deleteUpdSetupFiles(List<UpdSetupFile> updSetupFiles);
	public int getUpdSetupFileCount();
	
	// for Kendo Grid Remote Read
	public DataSourceResult getUpdSetupFileList(DataSourceRequest request);

	// for UpdSetupFile specific
	public UpdSetupFile getUpdSetupFile(String filename);
	public List<UpdSetupFile> getUpdSetupFileListByPublished(String published);
	public String getLatestUpdSetupFileStableVersionByEditionProgType(
			String edition, String progType, String vendor);

}
