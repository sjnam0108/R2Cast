package kr.co.r2cast.info;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import kr.co.r2cast.models.eco.RecStatusStat;

public class EcoGlobalInfo {
	
	public static Date LastTrxImportDt = null;
	public static HashMap<Integer, RecStatusStat> SiteStatusMap = 
			new HashMap<Integer, RecStatusStat>();
	
	public static ArrayList<Integer> CTAStbIds = new ArrayList<Integer>();
	
	// Data Feed: Fire Alarm
	public static String FireAlarmType = "E";
	public static Boolean FireAlarmActive = null;
}
