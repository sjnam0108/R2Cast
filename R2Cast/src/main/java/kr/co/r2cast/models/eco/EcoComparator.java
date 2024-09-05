package kr.co.r2cast.models.eco;

import java.util.Comparator;

import kr.co.r2cast.viewmodels.eco.DailyRunningTimeItem;
import kr.co.r2cast.viewmodels.eco.RvmItem;
import kr.co.r2cast.viewmodels.eco.RvmServiceStatusItem;
import kr.co.r2cast.viewmodels.eco.UploadFileItem;

public class EcoComparator {

	public static Comparator<RvmServiceStatusItem> RvmServiceStatusItemCountReverseComparator = 
			new Comparator<RvmServiceStatusItem>() {
    	public int compare(RvmServiceStatusItem item1, RvmServiceStatusItem item2) {
    		return Integer.compare(item2.getCount(), item1.getCount());
    	}
    };

    public static Comparator<UploadFileItem> UploadFileItemNameComparator =
    		new Comparator<UploadFileItem>() {
    	public int compare(UploadFileItem item1, UploadFileItem item2) {
    		return item1.getName().compareTo(item2.getName());
    	}
    };
    
    public static Comparator<RvmItem> RvmItemRvmNameComparator =
    		new Comparator<RvmItem>() {
    	public int compare(RvmItem item1, RvmItem item2) {
    		return item1.getRvmName().compareTo(item2.getRvmName());
    	}
    };

	public static Comparator<RvmGroup> RvmGroupRvmGroupNameComparator = 
			new Comparator<RvmGroup>() {
    	public int compare(RvmGroup item1, RvmGroup item2) {
    		return item1.getRvmGroupName().toUpperCase()
    				.compareTo(item2.getRvmGroupName().toUpperCase());
    	}
    };

    private static int getColorNumber(String colorCode) {
    	switch (colorCode) {
    	case "R":
    		return 1;
    	case "O":
    		return 2;
    	case "Y":
    		return 3;
    	case "G":
    		return 4;
    	case "B":
    		return 5;
    	case "P":
    		return 6;
    	}
    	
    	return 7;
    }
    
	public static Comparator<RvmGroup> RvmGroupTagColorRvmGroupNameComparator = 
			new Comparator<RvmGroup>() {
    	public int compare(RvmGroup item1, RvmGroup item2) {
    		if (item1.getCategory().equals(item2.getCategory())) {
        		return item1.getRvmGroupName().toUpperCase()
        				.compareTo(item2.getRvmGroupName().toUpperCase());
    		} else {
    			int itemColor1 = getColorNumber(item1.getCategory());
    			int itemColor2 = getColorNumber(item2.getCategory());
    			
    			return Integer.compare(itemColor1, itemColor2);
    		}
    	}
    };

	public static Comparator<DailyRunningTimeItem> DailyRunningTimeItemDateNumberComparator = 
			new Comparator<DailyRunningTimeItem>() {
    	public int compare(DailyRunningTimeItem item1, DailyRunningTimeItem item2) {
    		Long date1 = 0l;
    		Long date2 = 0l;
    		
    		try {
    			date1 = Long.parseLong(item1.getDateNumber());
    			date2 = Long.parseLong(item2.getDateNumber());
    		} catch (Exception e) {}
    		
    		return date1.compareTo(date2);
    	}
    };

	public static Comparator<Rvm> RvmRvmNameComparator = 
			new Comparator<Rvm>() {
    	public int compare(Rvm item1, Rvm item2) {
    		return item1.getRvmName().toUpperCase()
    				.compareTo(item2.getRvmName().toUpperCase());
    	}
    };
    
    public static Comparator<RvmGroupRvm> RvmGroupRvmRvmNameComparator =
			new Comparator<RvmGroupRvm>() {
    	public int compare(RvmGroupRvm item1, RvmGroupRvm item2) {
    		return item1.getRvm().getRvmName().toUpperCase()
    				.compareTo(item2.getRvm().getRvmName().toUpperCase());
    	}
    };

	public static Comparator<MonTask> MonTaskIdReverseComparator = 
			new Comparator<MonTask>() {
    	public int compare(MonTask item1, MonTask item2) {
    		return Integer.compare(item2.getId(), item1.getId());
    	}
    };

//	public static Comparator<Advertiser> AdvertiserAdvertiserNameComparator = 
//			new Comparator<Advertiser>() {
//    	public int compare(Advertiser item1, Advertiser item2) {
//    		return item1.getAdvertiserName().toUpperCase()
//    				.compareTo(item2.getAdvertiserName().toUpperCase());
//    	}
//    };

//	public static Comparator<Ad> AdAdNameComparator = 
//			new Comparator<Ad>() {
//    	public int compare(Ad item1, Ad item2) {
//    		return item1.getAdName().toUpperCase()
//    				.compareTo(item2.getAdName().toUpperCase());
//    	}
//    };

//	public static Comparator<UpdSetupFile> UpdSetupFileReleaseDateComparator = 
//			new Comparator<UpdSetupFile>() {
//    	public int compare(UpdSetupFile item1, UpdSetupFile item2) {
//    		return item1.getReleaseDate().compareTo(item2.getReleaseDate());
//    	}
//    };
//
//	public static Comparator<UpdSetupFile> UpdSetupFileVersionReverseComparator = 
//			new Comparator<UpdSetupFile>() {
//    	public int compare(UpdSetupFile item1, UpdSetupFile item2) {
//    		return Integer.compare(item2.getVersionAsInt(), item1.getVersionAsInt());
//    	}
//    };
//    
    
//    public static Comparator<AdTrack> AdTrackStbPlayDatePlaySecComparator =
//    		new Comparator<AdTrack>() {
//    	public int compare(AdTrack item1, AdTrack item2) {
//    		if (Integer.compare(item1.getStb().getId(), item2.getStb().getId()) == 0) {
//    			if (item1.getPlayDate().equals(item2.getPlayDate())) {
//    				return Integer.compare(item1.getPlaySec(), item2.getPlaySec());
//    			} else {
//    				return item1.getPlayDate().compareTo(item2.getPlayDate());
//    			}
//    		} else {
//    			return Integer.compare(item1.getStb().getId(), item2.getStb().getId());
//    		}
//    	}
//    };
    
//    public static Comparator<NocAction> NocActionIdComparator =
//    		new Comparator<NocAction>() {
//    	public int compare(NocAction item1, NocAction item2) {
//    		return Integer.compare(item1.getId(), item2.getId());
//    	}
//    };
    
//    public static Comparator<BasPlayerId> BasPlayerIdPlayerCodeComparator =
//    		new Comparator<BasPlayerId>() {
//    	public int compare(BasPlayerId item1, BasPlayerId item2) {
//    		return item1.getPlayerCode().compareTo(item2.getPlayerCode());
//    	}
//    };

//	public static Comparator<DplySchedule> DplyScheduleIdReverseComparator = 
//			new Comparator<DplySchedule>() {
//    	public int compare(DplySchedule item1, DplySchedule item2) {
//    		return Integer.compare(item2.getId(), item1.getId());
//    	}
//    };

//	public static Comparator<DplyOther> DplyOtherIdReverseComparator = 
//			new Comparator<DplyOther>() {
//    	public int compare(DplyOther item1, DplyOther item2) {
//    		return Integer.compare(item2.getId(), item1.getId());
//    	}
//    };
    
//    public static Comparator<NocTask> NocTaskComparator =
//    		new Comparator<NocTask>() {
//    	public int compare(NocTask item1, NocTask item2) {
//    		return item1.getWhoCreationDate().compareTo(item2.getWhoCreationDate());
//    	}
//    };
    
//    public static Comparator<NocTask> NocTaskReverseComparator =
//    		new Comparator<NocTask>() {
//    	public int compare(NocTask item1, NocTask item2) {
//    		return item2.getWhoCreationDate().compareTo(item1.getWhoCreationDate());
//    	}
//    };
    
//    public static Comparator<NocAction> NocActionComparator =
//    		new Comparator<NocAction>() {
//    	public int compare(NocAction item1, NocAction item2) {
//    		return item1.getWhoCreationDate().compareTo(item2.getWhoCreationDate());
//    	}
//    };
    
    public static Comparator<RvmItem> RvmItemDateComparator =
    		new Comparator<RvmItem>() {
    	public int compare(RvmItem item1, RvmItem item2) {
    		return item1.getDate().compareTo(item2.getDate());
    	}
    };

//    public static Comparator<OtherBundle> OtherBundleNameComparator =
//    		new Comparator<OtherBundle>() {
//    	public int compare(OtherBundle item1, OtherBundle item2) {
//    		return item1.getBundleName().compareTo(item2.getBundleName());
//    	}
//    };
//
//    public static Comparator<DataSet> DataSetDataCodeComparator =
//    		new Comparator<DataSet>() {
//    	public int compare(DataSet item1, DataSet item2) {
//    		return item1.getDataCode().toLowerCase().compareTo(item2.getDataCode().toLowerCase());
//    	}
//    };
    
}
