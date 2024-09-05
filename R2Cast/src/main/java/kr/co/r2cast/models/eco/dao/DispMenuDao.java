package kr.co.r2cast.models.eco.dao;

import java.util.List;

import kr.co.r2cast.models.eco.DispMenu;

public interface DispMenuDao {
	// Common
	public DispMenu get(int id);
	public List<DispMenu> getList();
	public void saveOrUpdate(DispMenu dispMenu);
	public void delete(DispMenu dispMenu);
	public void delete(List<DispMenu> dispMenus);

	// for DispMenu specific
	public DispMenu getBySiteId(int siteId);
}
