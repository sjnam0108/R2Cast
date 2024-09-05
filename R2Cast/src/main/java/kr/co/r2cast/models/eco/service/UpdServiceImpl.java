package kr.co.r2cast.models.eco.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.eco.UpdSetupFile;
import kr.co.r2cast.models.eco.dao.UpdSetupFileDao;
import kr.co.r2cast.utils.Util;

@Transactional
@Service("updService")
public class UpdServiceImpl implements UpdService {
	private static final Logger logger = LoggerFactory.getLogger(UpdServiceImpl.class);

    @Autowired
    private SessionFactory sessionFactory;
    
    @Autowired
    private UpdSetupFileDao updSetupFileDao;
    
	@Override
	public void flush() {
		sessionFactory.getCurrentSession().flush();
	}

	@Override
	public UpdSetupFile getUpdSetupFile(int id) {
		return updSetupFileDao.get(id);
	}

	@Override
	public List<UpdSetupFile> getUpdSetupFileList() {
		return updSetupFileDao.getList();
	}

	@Override
	public void saveOrUpdate(UpdSetupFile updSetupFile) {
		updSetupFileDao.saveOrUpdate(updSetupFile);
	}

	@Override
	public void deleteUpdSetupFile(UpdSetupFile updSetupFile) {
		updSetupFileDao.delete(updSetupFile);
	}

	@Override
	public void deleteUpdSetupFiles(List<UpdSetupFile> updSetupFiles) {
		updSetupFileDao.delete(updSetupFiles);
	}

	@Override
	public int getUpdSetupFileCount() {
		return updSetupFileDao.getCount();
	}

	@Override
	public DataSourceResult getUpdSetupFileList(DataSourceRequest request) {
		return updSetupFileDao.getList(request);
	}

	@Override
	public UpdSetupFile getUpdSetupFile(String filename) {
		return updSetupFileDao.get(filename);
	}

	@Override
	public List<UpdSetupFile> getUpdSetupFileListByPublished(String published) {
		return updSetupFileDao.getListByPublished(published);
	}
	
	@Override
	public String getLatestUpdSetupFileStableVersionByEditionProgType(
			String edition, String progType, String vendor) {

		List<UpdSetupFile> publishedList = getUpdSetupFileListByPublished("Y");
		Collections.sort(publishedList, new Comparator<UpdSetupFile>() {
	    	public int compare(UpdSetupFile item1, UpdSetupFile item2) {
	    		return Integer.compare(item2.getVersionAsInt(), item1.getVersionAsInt());
	    	}
	    });
		
		for(UpdSetupFile setupFile : publishedList) {

			if (setupFile.getEdition().equals(edition) && setupFile.getProgType().equals(progType)
					&& setupFile.getFileType().equals("S")) {
				if (Util.isValid(vendor)) {
					if (setupFile.getVersion().endsWith("." + vendor)) {
						return setupFile.getVersion();
					}
				} else {
					return setupFile.getVersion();
				}
			}
		}
		
		return "NO";
	}

}
