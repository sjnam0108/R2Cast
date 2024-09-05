package kr.co.r2cast.viewmodels;

public class MonitoringViewItem {
	public static enum ViewType {
		SiteAll, Site, ChildSite, RvmGroup, ChildRvmGroup
	}
	
	private ViewType view;
	private String text;
	
	private int siteId;
	private int indentWidth;
	private int rvmGroupId;
	private String category;
	
	public MonitoringViewItem(ViewType view, int indentWidth, String text, int siteId, int rvmGroupId) {
		this.view = view;
		this.indentWidth = indentWidth;
		this.text = text;
		this.siteId = siteId;
		this.rvmGroupId = rvmGroupId;
	}
	
	public MonitoringViewItem(ViewType view, int indentWidth, String text, String category, int siteId, int rvmGroupId) {
		this.view = view;
		this.indentWidth = indentWidth;
		this.text = text;
		this.category = category;
		this.siteId = siteId;
		this.rvmGroupId = rvmGroupId;
	}
	
	public String getTypeImageCssKeyword() {
		switch (view) {
		case SiteAll:
			return "fa-regular fa-star";
		case Site:
		case ChildSite:
			return "fa-regular fa-globe";
			
		case RvmGroup:
		case ChildRvmGroup:
			if (getCategory().equals("R")) { return "fa-solid fa-square text-red"; }
			else if (getCategory().equals("O")) { return "fa-solid fa-square text-orange"; }
			else if (getCategory().equals("Y")) { return "fa-solid fa-square text-yellow"; }
			else if (getCategory().equals("G")) { return "fa-solid fa-square text-green"; }
			else if (getCategory().equals("B")) { return "fa-solid fa-square text-blue"; }
			else if (getCategory().equals("P")) { return "fa-solid fa-square text-purple"; }
			
			return "fa-solid fa-square";
		}
		
		return "";
	}
	
	public String getValue() {
		return String.format("%s|%s", siteId, rvmGroupId);
	}

	public ViewType getView() {
		return view;
	}

	public void setView(ViewType view) {
		this.view = view;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getSiteId() {
		return siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public int getIndentWidth() {
		return indentWidth;
	}

	public void setIndentWidth(int indentWidth) {
		this.indentWidth = indentWidth;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getRvmGroupId() {
		return rvmGroupId;
	}

	public void setRvmGroupId(int rvmGroupId) {
		this.rvmGroupId = rvmGroupId;
	}
	
}
