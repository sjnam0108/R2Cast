package kr.co.r2cast.viewmodels.eco;

public class RvmGroupItem {
	private int id;
	private String rvmGroupName;
	private String category = "";
	
	public RvmGroupItem(int id, String rvmGroupName, String category) {
		this.id = id;
		this.rvmGroupName = rvmGroupName;
		this.category = category;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRvmGroupName() {
		return rvmGroupName;
	}

	public void setRvmGroupName(String rvmGroupName) {
		this.rvmGroupName = rvmGroupName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getColor() {
		if (category.equals("R")) {
			return "red";
		} else if (category.equals("O")) {
			return "orange";
		} else if (category.equals("Y")) {
			return "yellow";
		} else if (category.equals("G")) {
			return "green";
		} else if (category.equals("B")) {
			return "blue";
		} else if (category.equals("P")) {
			return "purple";
		}
		
		return "";
	}
}
