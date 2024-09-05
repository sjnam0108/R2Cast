package kr.co.r2cast.models.eco.dao;

import java.util.List;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.eco.UpdSetupFile;

public interface UpdSetupFileDao {
	// Common
	public UpdSetupFile get(int id);
	public List<UpdSetupFile> getList();
	public void saveOrUpdate(UpdSetupFile updSetupFile);
	public void delete(UpdSetupFile updSetupFile);
	public void delete(List<UpdSetupFile> updSetupFiles);
	public int getCount();

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for UpdSetupFile specific
	public UpdSetupFile get(String filename);
	public List<UpdSetupFile> getListByPublished(String published);
}
