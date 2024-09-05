package kr.co.r2cast.models.eco.service;
 
import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.eco.Rvm;
import kr.co.r2cast.models.eco.RvmTrx;
import kr.co.r2cast.models.eco.RvmTrxItem;
import kr.co.r2cast.models.eco.dao.RvmTrxDao;
import kr.co.r2cast.models.eco.dao.RvmTrxItemDao;

@Transactional
@Service("trxService")
public class TrxServiceImpl implements TrxService {
	private static final Logger logger = LoggerFactory.getLogger(TrxServiceImpl.class);
    @Autowired
    private SessionFactory sessionFactory;
    
    @Autowired
    private RvmTrxDao rvmTrxDao;
    
    @Autowired
    private RvmTrxItemDao rvmTrxItemDao;

    
	@Override
	public void flush() {
		sessionFactory.getCurrentSession().flush();
	}
	

	//
	// for RvmTrxDao
	//
	@Override
	public RvmTrx getRvmTrx(int id) {
		return rvmTrxDao.get(id);
	}
	
	@Override
	public void saveOrUpdate(RvmTrx trx) {
		rvmTrxDao.saveOrUpdate(trx);
	}

	@Override
	public void deleteRvmTrx(RvmTrx trx) {
		rvmTrxDao.delete(trx);
	}

	@Override
	public void deleteRvmTrxs(List<RvmTrx> trxs) {
		rvmTrxDao.delete(trxs);
	}

	@Override
	public DataSourceResult getRvmTrxList(DataSourceRequest request) {
		return rvmTrxDao.getList(request);
	}

	@Override
	public RvmTrx getRvmTrx(Rvm rvm, Date opDate) {
		return rvmTrxDao.get(rvm, opDate);
	}

	@Override
	public List<RvmTrx> getRvmTrxListBySiteId(int siteId) {
		return rvmTrxDao.getListBySiteId(siteId);
	}

	@Override
	public List<RvmTrx> getRvmTrxListBySiteIdRvmId(int siteId, int rvmId) {
		return rvmTrxDao.getListBySiteIdRvmId(siteId, rvmId);
	}

	@Override
	public List<RvmTrx> getRvmTrxListBySiteIdOpDate(int siteId, Date opDate) {
		return rvmTrxDao.getListBySiteIdOpDate(siteId, opDate);
	}

	@Override
	public List<RvmTrx> getRvmTrxListBySiteIdOpDateRvmId(int siteId, Date opDate, int rvmId) {
		return rvmTrxDao.getListBySiteIdOpDateRvmId(siteId, opDate, rvmId);
	}

	@Override
	public List<Object[]> getDailyRvmTrxListBySiteIdOpDate(int siteId, Date date) {
		return rvmTrxDao.getDailyListBySiteIdOpDate(siteId, date);
	}

	@Override
	public List<Object[]> getDailyRvmTrxListBySiteIdOpDates(int siteId, Date date1, Date date2) {
		return rvmTrxDao.getDailyListBySiteIdOpDates(siteId, date1, date2);
	}

	@Override
	public List<Object[]> getDailyRvmTrxListByRvmIdOpDates(int rvmId, Date date1, Date date2) {
		return rvmTrxDao.getDailyListByRvmIdOpDates(rvmId, date1, date2);
	}

	@Override
	public Date getRvmTrxMaxOpDateByRvmId(int rvmId) {
		return rvmTrxDao.getMaxOpDateByRvmId(rvmId);
	}

	@Override
	public Object[] getSumRvmTrxDataByRvmId(int rvmId) {
		return rvmTrxDao.getSumDataByRvmId(rvmId);
	}

	

	//
	// for RvmTrxItemDao
	//
	@Override
	public RvmTrxItem getRvmTrxItem(int id) {
		return rvmTrxItemDao.get(id);
	}

	@Override
	public void saveOrUpdate(RvmTrxItem trxItem) {
		rvmTrxItemDao.saveOrUpdate(trxItem);
	}

	@Override
	public void deleteRvmTrxItem(RvmTrxItem trxItem) {
		rvmTrxItemDao.delete(trxItem);
	}

	@Override
	public void deleteRvmTrxItems(List<RvmTrxItem> trxItems) {
		rvmTrxItemDao.delete(trxItems);
	}

	@Override
	public DataSourceResult getRvmTrxItemList(DataSourceRequest request) {
		return rvmTrxItemDao.getList(request);
	}

	@Override
	public List<RvmTrxItem> getRvmTrxItemListByTrxId(int siteId, int trxId) {
		return rvmTrxItemDao.getListByTrxId(siteId, trxId);
	}

	@Override
	public int getRvmTrxItemCountByTrxId(int trxId) {
		return rvmTrxItemDao.getCountByTrxId(trxId);
	}

	@Override
	public List<Object[]> getDailyRvmTrxItemListBySiteIdOpDate(int siteId, Date date) {
		return rvmTrxItemDao.getDailyListBySiteIdOpDate(siteId, date);
	}

	@Override
	public List<Object[]> getDailyRvmTrxItemListBySiteIdOpDates(int siteId, Date date1, Date date2) {
		return rvmTrxItemDao.getDailyListBySiteIdOpDates(siteId, date1, date2);
	}

	@Override
	public List<Object[]> getDailyRvmTrxItemListByRvmIdOpDates(int rvmId, Date date1, Date date2) {
		return rvmTrxItemDao.getDailyListByRvmIdOpDates(rvmId, date1, date2);
	}

	@Override
	public Object[] getSumRvmTrxItemDataByRvmId(int rvmId) {
		return rvmTrxItemDao.getSumDataByRvmId(rvmId);
	}

}
