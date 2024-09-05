package kr.co.r2cast.models.eco.dao;

import java.util.Date;
import java.util.List;

import kr.co.r2cast.models.DataSourceRequest;
import kr.co.r2cast.models.DataSourceResult;
import kr.co.r2cast.models.eco.Rvm;
import kr.co.r2cast.models.eco.RvmTrx;

public interface RvmTrxDao {
	// Common
	public RvmTrx get(int id);
	public void saveOrUpdate(RvmTrx trx);
	public void delete(RvmTrx trx);
	public void delete(List<RvmTrx> trxs);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for RvmTrxDao specific
	public RvmTrx get(Rvm rvm, Date opDate);
	public List<RvmTrx> getListBySiteId(int siteId);
	public List<RvmTrx> getListBySiteIdRvmId(int siteId, int rvmId);
	public List<RvmTrx> getListBySiteIdOpDate(int siteId, Date opDate);
	public List<RvmTrx> getListBySiteIdOpDateRvmId(int siteId, Date opDate, int rvmId);
	public List<Object[]> getDailyListBySiteIdOpDate(int siteId, Date date);
	public List<Object[]> getDailyListBySiteIdOpDates(int siteId, Date date1, Date date2);
	public List<Object[]> getDailyListByRvmIdOpDates(int rvmId, Date date1, Date date2);
	public Date getMaxOpDateByRvmId(int rvmId);
	public Object[] getSumDataByRvmId(int rvmId);
}
