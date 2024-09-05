package kr.co.r2cast.models.eco.dao;

import java.util.Date;
import java.util.List;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.eco.RvmTrxItem;

public interface RvmTrxItemDao {
	// Common
	public RvmTrxItem get(int id);
	public void saveOrUpdate(RvmTrxItem trxItem);
	public void delete(RvmTrxItem trxItem);
	public void delete(List<RvmTrxItem> trxItems);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for RvmTrxItemDao specific
	public List<RvmTrxItem> getListByTrxId(int siteId, int trxId);
	public int getCountByTrxId(int trxId);
	public List<Object[]> getDailyListBySiteIdOpDate(int siteId, Date date);
	public List<Object[]> getDailyListBySiteIdOpDates(int siteId, Date date1, Date date2);
	public List<Object[]> getDailyListByRvmIdOpDates(int rvmId, Date date1, Date date2);
	public Object[] getSumDataByRvmId(int rvmId);
}
