package kr.co.r2cast.models.eco.service;

import java.util.Date;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.eco.Rvm;
import kr.co.r2cast.models.eco.RvmTrx;
import kr.co.r2cast.models.eco.RvmTrxItem;

@Transactional
public interface TrxService {
	// Common
	public void flush();

	
	//
	// for RvmTrxDao
	//
	// Common
	public RvmTrx getRvmTrx(int id);
	public void saveOrUpdate(RvmTrx trx);
	public void deleteRvmTrx(RvmTrx trx);
	public void deleteRvmTrxs(List<RvmTrx> trxs);

	// for Kendo Grid Remote Read
	public DataSourceResult getRvmTrxList(DataSourceRequest request);

	// for RvmTrxDao specific
	public RvmTrx getRvmTrx(Rvm rvm, Date opDate);
	public List<RvmTrx> getRvmTrxListBySiteId(int siteId);
	public List<RvmTrx> getRvmTrxListBySiteIdRvmId(int siteId, int rvmId);
	public List<RvmTrx> getRvmTrxListBySiteIdOpDate(int siteId, Date opDate);
	public List<RvmTrx> getRvmTrxListBySiteIdOpDateRvmId(int siteId, Date opDate, int rvmId);
	public List<Object[]> getDailyRvmTrxListBySiteIdOpDate(int siteId, Date date);
	public List<Object[]> getDailyRvmTrxListBySiteIdOpDates(int siteId, Date date1, Date date2);
	public List<Object[]> getDailyRvmTrxListByRvmIdOpDates(int rvmId, Date date1, Date date2);
	public Date getRvmTrxMaxOpDateByRvmId(int rvmId);
	public Object[] getSumRvmTrxDataByRvmId(int rvmId);

	
	//
	// for RvmTrxItemDao
	//
	// Common
	public RvmTrxItem getRvmTrxItem(int id);
	public void saveOrUpdate(RvmTrxItem trxItem);
	public void deleteRvmTrxItem(RvmTrxItem trxItem);
	public void deleteRvmTrxItems(List<RvmTrxItem> trxItems);

	// for Kendo Grid Remote Read
	public DataSourceResult getRvmTrxItemList(DataSourceRequest request);

	// for RvmTrxItemDao specific
	public List<RvmTrxItem> getRvmTrxItemListByTrxId(int siteId, int trxId);
	public int getRvmTrxItemCountByTrxId(int trxId);
	public List<Object[]> getDailyRvmTrxItemListBySiteIdOpDate(int siteId, Date date);
	public List<Object[]> getDailyRvmTrxItemListBySiteIdOpDates(int siteId, Date date1, Date date2);
	public List<Object[]> getDailyRvmTrxItemListByRvmIdOpDates(int rvmId, Date date1, Date date2);
	public Object[] getSumRvmTrxItemDataByRvmId(int rvmId);
	
}
