package kr.co.r2cast.utils;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;

import kr.co.r2cast.exceptions.ServerOperationForbiddenException;
import kr.co.r2cast.info.GlobalInfo;
import kr.co.r2cast.models.MessageManager;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.service.SiteService;
import kr.co.r2cast.viewmodels.NotifMessage;
import kr.co.r2cast.models.eco.service.OptService;
import kr.co.r2cast.utils.SolUtil;
import kr.co.r2cast.utils.Util;

@Component
public class SolUtil {
	private static final Logger logger = LoggerFactory.getLogger(SolUtil.class);

	/**
	 * 사이트 설정 String 값의 동일 여부 반환(session 값으로)
	 */
	public static boolean propEqVal(HttpSession session, String code, String value) {
		String tmp = getProperty(session, code);
		
		return (Util.isValid(tmp) && tmp.equals(value));
	}

	/**
	 * 사이트 설정 String 값의 동일 여부 반환(사이트 번호로)
	 */
	public static boolean propEqVal(int siteId, String code, String value) {
		String tmp = getProperty(siteId, code);
		
		return (Util.isValid(tmp) && tmp.equals(value));
	}
	
	/**
	 * 사이트 설정 String 값 획득
	 */
	public static String getProperty(HttpSession session, String code) {
		return getProperty(session, code, null, "");
	}

	/**
	 * 사이트 설정 String 값 획득
	 */
	public static String getProperty(HttpSession session, String code, Locale locale) {
		return getProperty(session, code, locale, "");
	}

	/**
	 * 사이트 설정 String 값 획득
	 */
	public static String getProperty(HttpSession session, String code, Locale locale, 
			String defaultValue) {
		String value = getPropertyValue(Util.getSessionSiteId(session), code, locale);
		
		return Util.isValid(value) ? value : defaultValue;
	}
	
	/**
	 * 사이트 설정 String 값 획득
	 */
	public static String getProperty(int siteId, String code) {
		return getProperty(siteId, code, null, "");
	}

	/**
	 * 사이트 설정 String 값 획득
	 */
	public static String getProperty(int siteId, String code, Locale locale) {
		return getProperty(siteId, code, locale, "");
	}

	/**
	 * 사이트 설정 String 값 획득
	 */
	public static String getProperty(int siteId, String code, Locale locale, 
			String defaultValue) {
		String value = getPropertyValue(siteId, code, locale);
		
		return Util.isValid(value) ? value : defaultValue;
	}
	
	private static String getPropertyValue(int siteId, String code, Locale locale) {
		return code;
		
    	// [WAB] --------------------------------------------------------------------------
		/*
		if (Util.isValid(code) && 
				(code.equals("logo.title") || code.equals("quicklink.max.menu"))) {
			return Util.getFileProperty(code);
		} else {
			return "";
		}
		*/
    	// [WAB] --------------------------------------------------------------------------

	}
	
	//
	// [SignCast] ext -----------------------------------------------------------------
	//
	
	@SuppressWarnings("unused")
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	public void setStaticMsgMgr(MessageManager msgMgr) {
		SolUtil.sMsgMgr = msgMgr;
	}
	
	@Autowired
	public void setStaticOptService(OptService optService) {
		SolUtil.sOptService = optService;
	}


	@Autowired
	public void setStaticSiteService(SiteService siteService) {
		SolUtil.sSiteService = siteService;
	}
	

	
	static MessageManager sMsgMgr;
    
	static OptService sOptService;
    static SiteService sSiteService;

    /**
     * 스케줄 묶음 파일 게시 -
	 * 		스케줄 파일 요약 정보 획득
	 */
    @SuppressWarnings("unchecked")
	private static String[] getScheduleFileSummary(File file) {
    	String[] ret = { "", "", "", "", "", "", "", "" };
    	
    	if (file.exists()) {
    		String fileContent = Util.getFileContent(file.getAbsolutePath());
    		if (Util.isValid(fileContent)) {
    			try {
    			    Document document = DocumentHelper.parseText(fileContent);

    			    Node root = document.selectSingleNode("/SignCast");
    			    ret[5] = root.valueOf("@mountdir");
    			    
					List<Node> list = document.selectNodes("/SignCast/Area");
    			    List<Node> slideList;

    			    boolean isFirst = true;
    			    int pageCount = 0;
    			    for(Node node : list) {
	    				if (isFirst) {
	    				    isFirst = false;
	    				    
	    				    ret[3] = node.valueOf("@width");
	    				    ret[4] = node.valueOf("@height");
	    				}
	
	    				slideList = node.selectNodes("Page");
	    				pageCount += slideList.size();
    			    }

    			    ret[0] = String.valueOf(list.size());
    			    ret[1] = String.valueOf(pageCount);

    			    list = document.selectNodes("/SignCast/Tracking/Content");
    			    ret[2] = String.valueOf(list.size());
    			    
    			    Node auxRoot = document.selectSingleNode("/SignCast/AuxiliarySchedule");
    			    if (auxRoot != null) {
    			    	ret[6] = auxRoot.valueOf("@startdate");
    			    	ret[7] = auxRoot.valueOf("@enddate");
    			    }
    			} catch (Exception e) {
    				logger.error("getScheduleFileSummary", e);
    			}
    		}
	    }

    	return ret;
    }

    /**
     * 스케줄 묶음 파일 게시 -
	 * 		지정된 폴더의 파일명을 mount point 연계한 파일로 변경
	 */
    private static void renameFilesToMount(String path, String mountName) {
    	if (Util.isValid(mountName)) {
        	File tmp = new File(path);
        	if (tmp.exists() && tmp.isDirectory()) {
        		File[] files = tmp.listFiles();
        		for(File file : files) {
        			if (file.isFile()) {
        				file.renameTo(new File(path + File.separator + "[" + mountName + "]" + file.getName()));
        			}
        		}
        	}
    	}
    }

    /**
     * 스케줄 묶음 파일 게시 -
	 * 		지정된 폴더의 파일 정보 목록 획득
	 */


    /**
	 * 스케줄 묶음 파일 게시
	 */
    public static int deploySchedulePkg(Site site, File pkgFile, HttpSession session) 
    		throws ServerOperationForbiddenException {
    	String logStep = "";
    	

    	
    	try {
    		if (site == null || pkgFile == null || !pkgFile.exists() ||
    				!pkgFile.isFile()) {
    			return -1;
    		}
    		
    		//
    		// 스케줄 묶음 파일 게시 결과
    		//
    		//                               scd 파일명                    DB FolderName       DB Filename
    		// 기본저장소NoContent
    		//     a.scd                     a.scd                         Schedule/           a.scd
    		//     scd 파일 내 mountdir      ""
    		// 기본저장소Content
    		//     a.scd                     a.scd                         Schedule/           a.scd
    		//     Content/Image/I00001.jpg  Content/Image/I00001.jpg      Content/Image/      I00001.jpg
    		//     scd 파일 내 mountdir      ""
    		//     scd 파일 내 컨텐츠파일    Content/Image/I00001.jpg
    		//
    		// abc저장소NoContent(3754)
    		//     a.scd                     a.scd                         Schedule/           a.scd
    		//     scd 파일 내 mountdir      abc
    		// abc저장소Content(3753)
    		//     a.scd                     a.scd                         Schedule/           a.scd
    		//     Content/Image/I00001.jpg  Content/Image/[abc]I00001.jpg Content/Image/      [abc]I00001.jpg
    		//     scd 파일 내 mountdir      abc
    		//     scd 파일 내 컨텐츠파일    Content/Image/[abc]I00001.jpg
    		//
    		
    		// Stop 0. 스케줄 묶음 압축 풀기
    		logStep += "0";

    		String tmpFolderName = Util.getRandomSalt();
    		String tmpDeployRootDir = getPhysicalRoot("DeployedSchedule") + "/" + tmpFolderName;

    		if (!Util.unzipFile(pkgFile.getAbsolutePath(), tmpDeployRootDir)) {
    			return -1;
    		}
    		
    		// Step 1. 게시 스케줄 저장
    		logStep += ".1";
    		
			File dir = new File(tmpDeployRootDir);
			final String extName = getFileSearchPattern("PlayerSchedule");
			
			File scdFile = null;
			if (dir.exists() && dir.isDirectory()) {
				File[] files = dir.listFiles(new FilenameFilter() {
					
					@Override
					public boolean accept(File dir, String name) {
						return extName.equals("*") ||  name.toLowerCase().endsWith(extName);
					}
				});
				
				for(File file : files) {
					scdFile = file;
					break;
				}
			}
			
			if (scdFile == null) {
				return -1;
			}

			String[] schdSummary = getScheduleFileSummary(scdFile);

			String scheduleName = scdFile.getAbsolutePath();
    		scheduleName = scheduleName.substring(scheduleName.lastIndexOf(File.separator) + 1);
    		scheduleName = scheduleName.substring(0, scheduleName.lastIndexOf('.'));
    		

    		
    		// Step 2. 압축 해제 폴더를 정식 게시 폴더로 변경
    		logStep += ".2";

    		String deployRootDir = getPhysicalRoot("DeployedSchedule") + "/";
    		
    		// 만약 기존 폴더가 존재한다면, 삭제
    		Util.deleteDirectory(deployRootDir);
    		
    		// 압축을 푼 폴더명을 정식 명칭으로 변경
    		File tmpDplyDir = new File(tmpDeployRootDir);
    		tmpDplyDir.renameTo(new File(deployRootDir));
    		
    		// Step 3. 탑재 위치에 맞도록 컨텐츠 파일명을 변경
    		logStep += ".3";
    		
    		renameFilesToMount(deployRootDir + "/Content/Audio", schdSummary[5]);
    		renameFilesToMount(deployRootDir + "/Content/Component", schdSummary[5]);
    		renameFilesToMount(deployRootDir + "/Content/Flash", schdSummary[5]);
    		renameFilesToMount(deployRootDir + "/Content/Image", schdSummary[5]);
    		renameFilesToMount(deployRootDir + "/Content/PowerPoint", schdSummary[5]);
    		renameFilesToMount(deployRootDir + "/Content/Text", schdSummary[5]);
    		renameFilesToMount(deployRootDir + "/Content/Video", schdSummary[5]);

    		// Step 4. 게시된 파일 DB 등록
    		logStep += ".4";
    		

    		
    		// Step 5. 게시된 스케줄 상태 정보 변경
    		logStep += ".5";
    		
    		
    		return 0;
    	} catch (ServerOperationForbiddenException se) {
    		logger.error("logStep: " + logStep);
    		logger.error("deploySchedulePkg", se);
    		
    		throw se;
    	} catch (Exception e) {
    		logger.error("logStep: " + logStep);
    		logger.error("deploySchedulePkg", e);
    		
    		return -1;
    	}
    }


    /**
	 * 템플릿 타일 게시
	 */
    public static int deployTemplateTile(Site site,HttpSession session) 
    		throws ServerOperationForbiddenException {
    	
    	String logStep = "";
    	

    	
    	try {
    		if (site == null) {
    			return -1;
    		}
    		
        	
    		String rootDir = SolUtil.getPhysicalRoot("TmplTile", site.getShortName());
    		File tileFile = new File(rootDir + File.separator);
    		if (!tileFile.exists()) {
    			return -2;
    		}
    		
    		// 기존재 가능성있는 동일 템플릿 타일 모두 삭제

    		
    		
    		
    		// Step 1. 템플릿 타일 압축 풀기
    		logStep += "1";

    		String tmpFolderName = Util.getRandomSalt();
    		String tmpDeployRootDir = SolUtil.getPhysicalRoot("DeployedBundle") + "/" + tmpFolderName;

    		if (!Util.unzipFile(tileFile.getAbsolutePath(), tmpDeployRootDir)) {
    			return -3;
    		}
    		
    		
    		// Step 2. 게시 템플릿 타일(기타 컨텐츠) 저장
    		logStep += "2";
    		

    		
    		// 기타 묶음 게시 소스 기록

    		
    		
    		// Step 3. 압축 해제 폴더를 정식 게시 폴더로 변경
    		logStep += ".3";

    		String deployRootDir = getPhysicalRoot("DeployedBundle") + "/";
    		
    		// 만약 기존 폴더가 존재한다면, 삭제
    		Util.deleteDirectory(deployRootDir);
    		
    		
    		// 압축을 푼 폴더명을 정식 명칭으로 변경
    		//
    		//   압축 파일의 구성에 따라
    		//     1) 압축 파일 루트에 단 하나의 폴더가 존재(폴더명은 상관없음)
    		//     2) 그 외 모든 경우
    		File tmpDplyDir = new File(tmpDeployRootDir);
    		boolean zipRootMode = true;
    		File[] tmpDplyFile = tmpDplyDir.listFiles();
    		if (tmpDplyFile.length == 1 && tmpDplyFile[0].isDirectory()) {
    			tmpDplyDir = new File(tmpDeployRootDir + "/" + tmpDplyFile[0].getName());
    			zipRootMode = false;
    		}
    		
    		tmpDplyDir.renameTo(new File(deployRootDir));
    		if (!zipRootMode) {
    			Util.deleteDirectory(tmpDeployRootDir);
    		}
    		
    		
    		// Step 4. 게시된 파일 DB 등록
    		logStep += ".4";
    		
    		Path root = Paths.get(deployRootDir);




    		
    		// Step 5. 게시된 스케줄 상태 정보 변경
    		logStep += ".5";
    		

    		
    		return 0;
    	} catch (ServerOperationForbiddenException se) {
    		logger.error("logStep: " + logStep);
    		logger.error("deployTemplateTile", se);
    		
    		throw se;
    	} catch (Exception e) {
    		logger.error("logStep: " + logStep);
    		logger.error("deployTemplateTile", e);
    		return -1;
    	}
    }

    /**
	 * 서버에 위치하는 방송 쉬트 게시
	 */
   
    
    /**
	 * 서버 생성 스케줄 파일 저장
	 */
    public static boolean saveServerSchedule(String pathFile, String mountDir,
    		String durStr, String externalAdIndexes,  List<Integer> trackIdList) {
    	
    	final String AREA_NAME = "Default Section";
    	
    	try {
        	Document document = DocumentHelper.createDocument();
        	
            Element rootEl = document.addElement("SignCast");
            
            rootEl.addAttribute("version", "1");
            rootEl.addAttribute("generated", Util.toSimpleString(new Date()));
            rootEl.addAttribute("mountdir", mountDir);
            rootEl.addAttribute("lastseq", "0");
            
            Element areaEl = rootEl.addElement("Area");
            areaEl.addAttribute("areaname", AREA_NAME);
            areaEl.addAttribute("playtime", durStr);
            areaEl.addAttribute("defaultdir", "");
            
            Element pageEl = areaEl.addElement("Page");
            pageEl.addAttribute("playtime", durStr);
            pageEl.addAttribute("bgcolor", "#FF000000");
            pageEl.addAttribute("file", "");
            pageEl.addAttribute("hidden", "False");
            pageEl.addAttribute("touchindex", "-1");
            pageEl.addAttribute("stopindex", "-1");
            pageEl.addAttribute("name", "");
            pageEl.addAttribute("fileseq", "");
            pageEl.addAttribute("filename", "");

            Element frameEl = pageEl.addElement("Frame");
            frameEl.addAttribute("isMainFrame", "True");
            frameEl.addAttribute("touchindex", "-1");
            frameEl.addAttribute("name", "");
            frameEl.addAttribute("angle", "");
            frameEl.addAttribute("externalAdIndexes", externalAdIndexes);
            

            pageEl.addElement("BackgroundAudio");
            
            Element trackingEl = rootEl.addElement("Tracking");
            for(Integer id : trackIdList) {
            	Element ctntEl = trackingEl.addElement("Content");
            	ctntEl.addAttribute("seq", String.valueOf(id));
            }
            
            Element scheduleEl = rootEl.addElement("Schedule");
            scheduleEl.addAttribute("mon", AREA_NAME);
            scheduleEl.addAttribute("tue", AREA_NAME);
            scheduleEl.addAttribute("wed", AREA_NAME);
            scheduleEl.addAttribute("thu", AREA_NAME);
            scheduleEl.addAttribute("fri", AREA_NAME);
            scheduleEl.addAttribute("sat", AREA_NAME);
            scheduleEl.addAttribute("sun", AREA_NAME);
            scheduleEl.addAttribute("montime", "");
            scheduleEl.addAttribute("tuetime", "");
            scheduleEl.addAttribute("wedtime", "");
            scheduleEl.addAttribute("thutime", "");
            scheduleEl.addAttribute("fritime", "");
            scheduleEl.addAttribute("sattime", "");
            scheduleEl.addAttribute("suntime", "");
            
            
            Element intgEl = rootEl.addElement("IntegrationSchedule");
            Element baseEl = intgEl.addElement("Base");
            Element intSchdEl = baseEl.addElement("Schedule");
            intSchdEl.addAttribute("name", "");
            intSchdEl.addAttribute("condition", "this");
            
            intgEl.addElement("Advanced");
            
            
            Element auxEl = rootEl.addElement("AuxiliarySchedule");
            auxEl.addAttribute("startdate", "");
            auxEl.addAttribute("enddate", "");
            auxEl.addAttribute("daysofweek", "");
            auxEl.addAttribute("starttime", "");
            auxEl.addAttribute("endtime", "");
            
            
            StringWriter writer = new StringWriter();
            XMLWriter xmlWriter = new XMLWriter(writer, OutputFormat.createPrettyPrint());
            xmlWriter.write(document);
            xmlWriter.close();
            
            
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathFile), "UTF-8"));
            
            output.write(writer.toString());
            output.close();
            writer.close();
    	} catch (Exception e) {
			logger.error("saveServerSchedule", e);
			
			return false;
    	}
    	
    	return true;
    }


	/**
	 * 지정 유형에 대한 기본 확장자 검색 패턴 획득
	 */
	public static String getFileSearchPattern(String ukid) {
		if (ukid == null || ukid.isEmpty()) {
			return "*.*";
		}
		
		if (ukid.equals("Debug")) {
			return ".txt";
		} else if (ukid.equals("Trx")) {
			return ".xml";
		}
		
		return "*";
	}
	
	/**
	 * 물리적인 루트 디렉토리 획득
	 */
	public static String getPhysicalRoot(String ukid) {
		return getPhysicalRoot(ukid, "");
	}

	/**
	 * 물리적인 루트 디렉토리 획득
	 */
	public static String getPhysicalRoot(String ukid, String site) {
		if (Util.isNotValid(ukid)) {
			return null;
		}
		
		String rootDirPath = Util.getFileProperty("dir.rootPath");
		String ftpDirName = Util.getFileProperty("dir.ftp");
		
		if (ukid.equals("Cosmo")) {
			return Util.getValidRootDir(rootDirPath) + "cosmo";
		} else if (ukid.equals("Debug")) {
            return Util.getValidRootDir(rootDirPath) + ftpDirName + "/upload/debugs";
		} else if (ukid.equals("Setup")) {
            return Util.getValidRootDir(rootDirPath) + "setups";
		} else if (ukid.equals("Temp")) {
            return Util.getValidRootDir(rootDirPath) + "temp";
		} else if (ukid.equals("Trx")) {
            return Util.getValidRootDir(rootDirPath) + ftpDirName + "/upload/trxs";
		} else if (ukid.equals("TrxBackup")) {
            return Util.getValidRootDir(rootDirPath) + "trxbackups";
		} else if (ukid.equals("TrxError")) {
            return Util.getValidRootDir(rootDirPath) + "trxerrors";
		}

        return Util.getPhysicalRoot(ukid);
	}

	/**
	 * 초기 디렉토리 확인(없으면 생성)
	 */
	public static boolean checkInitDirectory() {
		Util.checkInitDirectory();
		
		String rootDirPath = Util.getFileProperty("dir.rootPath");
		if (rootDirPath == null || rootDirPath.isEmpty()) {
			logger.error("checkSiteDirectory: No Root Directory Path");
			return false;
		}
		
		String ftpDirName = Util.getFileProperty("dir.ftp");
		if (ftpDirName == null || ftpDirName.isEmpty()) {
			logger.error("checkSiteDirectory: No FTP Directory Name");
			return false;
		}
		
		if (Util.checkDirectory(getPhysicalRoot("Cosmo")) &&
				Util.checkDirectory(getPhysicalRoot("Debug")) &&
				Util.checkDirectory(getPhysicalRoot("Setup")) &&
				Util.checkDirectory(getPhysicalRoot("Temp")) &&
				Util.checkDirectory(getPhysicalRoot("Trx")) &&
				Util.checkDirectory(getPhysicalRoot("TrxBackup")) &&
				Util.checkDirectory(getPhysicalRoot("TrxError"))) {
			return true;
		}
		
		return false;
	}

	/**
	 * 지정된 사이트 단축명으로 사이트 디렉토리 확인(없으면 생성)
	 */
	public static boolean checkSiteDirectory(String siteShortName) {
		if (siteShortName == null || siteShortName.isEmpty()) {
			return false;
		}

		String rootDirPath = Util.getFileProperty("dir.rootPath");
		if (rootDirPath == null || rootDirPath.isEmpty()) {
			logger.error("checkSiteDirectory: No Root Directory Path");
			return false;
		}
		
		String ftpDirName = Util.getFileProperty("dir.ftp");
		if (ftpDirName == null || ftpDirName.isEmpty()) {
			logger.error("checkSiteDirectory: No FTP Directory Name");
			return false;
		}

		//
		// 기본 디렉토리 구조
		//
		// --- 사이트만을 위한 별도의 구조를 현재 가지지 않음
		//
		
		return checkInitDirectory();
	}
	
	/**
	 * 자산 모듈에서 사용하는 로컬 문자열 반환
	 */
	public static String getAssetResourceMessage(String code, Locale locale) {
		if (Util.isNotValid(code)) {
			return code;
		}
		
		String key = "";
		if (code.startsWith("A") || code.startsWith("Z")) {
			key = "asset.changeType." + code;
		} else if (code.startsWith("P") || code.startsWith("C")) {
			key = "asset.repairType." + code;
		} else {
			key = "asset.status." + code;
		}
		
		String ret = sMsgMgr.message(key, locale);
		if (ret.equals(key)) {
			return code;
		}
		
		return ret;
	}
	
	/**
	 * 일상 스케줄 여부 판단
	 */
    public static boolean isRoutineSchedule(String schedule, HttpSession session) {
    	if (Util.isNotValid(schedule)) {
    		return false;
    	}
    	
    	List<String> prefixes = Util.tokenizeValidStr(getProperty(session, "routineSched.prefix"));
    	
    	for(String prefix : prefixes) {
    		if (schedule.startsWith(prefix) && schedule.length() >= prefix.length() + 10) {
        		String datePart = schedule.substring(prefix.length());
        		if (datePart.length() >= 10) {
        			datePart = datePart.substring(0, 10).replace("-", "").replace("/", "").replace(".", "");
        			if (Util.parseDate(datePart) != null) {
        				return true;
        			}
        		}
    		}
    	}
    	
    	return false;
    }

	/**
	 * 원격제어 명령의 결과의 tip 획득
	 */
	public static String getRCCommandStatusTip(String code, Locale locale) {
		String statusR = "등록";
		String statusW = "통지";
		String statusS = "성공";
		String statusP = "성공(수락)";
		String statusF = "실패";
		String statusC = "자동 취소";

		String tip = statusR;
		if (Util.isNotValid(code)) {
			return tip;
		}

		if (code.equals("1")) {
			tip = statusR;
		} else if (code.equals("2")) {
			tip = statusW;
		} else if (code.equals("3") || code.equals("S")) {
			tip = statusS;
		} else if (code.equals("P")) {
			tip = statusP;
		} else if (code.equals("F")) {
			tip = statusF;
		} else if (code.equals("C")) {
			tip = statusC;
		}

		return tip;
	}

	/**
	 * 원격제어 명령의 결과 flag code 획득
	 */
	public static String getRCCommandStatusFlagCode(String code) {
		String flagCode = "R";
		if (Util.isNotValid(code)) {
			return flagCode;
		}

		if (code.equals("1")) {
			flagCode = "R";
		} else if (code.equals("2")) {
			flagCode = "W";
		} else if (code.equals("3") || code.equals("S")) {
			flagCode = "S";
		} else if (code.equals("P")) {
			flagCode = "P";
		} else if (code.equals("F")) {
			flagCode = "F";
		} else if (code.equals("C")) {
			flagCode = "C";
		}

		return flagCode;
	}
	
	/**
	 * 작업 관련 최후의 날짜 획득
	 */
	public static Date getMaxTaskDate() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		
		cal.add(Calendar.YEAR, 5);
		
		return cal.getTime();
	}

    /**
	 * 현재 시간 기준의 상태 표시 문자열 획득
	 */
	public static String getRvmStatusLine(String prevStatusLine, Date date, String currentStatus) {
		if (prevStatusLine == null || prevStatusLine.length() != 1440) {
			prevStatusLine = String.format("%1440s", "2").replace(' ', '2');
		}
		
		if (currentStatus == null || currentStatus.isEmpty() || currentStatus.equals("2")) {
			return prevStatusLine;
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		int pos = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
		if (pos < 0 || pos > 1439) {
			return prevStatusLine;
		}
		
		if (pos == 0) {
			return currentStatus + prevStatusLine.substring(1);
		} else if (pos == 1439) {
			return prevStatusLine.substring(0, 1439) + currentStatus;
		}
		
		return prevStatusLine.substring(0, pos) + currentStatus + prevStatusLine.substring(pos + 1);
	}
	
	/**
	 * 대상 상태 표시 문자열로부터 정상 운행 시간(분) 획득
	 */
	public static int getRvmRunningMinutes(String statusLine) {
		if (statusLine == null || statusLine.isEmpty() || statusLine.length() != 1440) {
			return 0;
		}
		
		int count = 0;
		for (int i = 0; i < 1440; i ++) {
			char aChar = statusLine.charAt(i);
			if (aChar != '2') {
				count ++;
			}
		}
		
		return count;
	}
	
	/**
	 * 현재 시간 +1 분 이후 시간을 상태 코드 '9'로 변경
	 */
	public static String getTodayStbStatusLine(String prevStatusLine) {
		String defaultStr = String.format("%1440s", "9").replace(' ', '9');
		
		if (prevStatusLine == null || prevStatusLine.length() != 1440) {
			return defaultStr;
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());

		int pos = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);

		if (pos < 0 || pos > 1439) {
			return defaultStr;
		} else if (pos == 1439) {
			return prevStatusLine;
		}
		
		pos ++;
		
		return prevStatusLine.substring(0, pos) + defaultStr.substring(0, 1440 - pos);
	}
	
	/**
	 * 로컬화된 상태 문자열 획득
	 */
	public static String getLocalizedStatusMsgString(String status, Locale locale) {
		String msgWorking = "정상 동작";
		String msgStoreClosed = "투입 금지";
		String msgFailureReported = "장애 보고";
		String msgRvmOff = "RVM 꺼짐";
		String msgStbOff = "STB 꺼짐";
		String msgNoShow = "미확인";
		
		if (Util.isNotValid(status)) {
			return msgNoShow;
		}
		
		switch (status) {
		case "6":
			return msgWorking;
		case "5":
			return msgStoreClosed;
		case "4":
			return msgFailureReported;
		case "3":
			return msgRvmOff;
		case "2":
			return msgStbOff;
		}
		
		return msgNoShow;
	}
	
	/**
	 * 원격제어 명령에 의한 보편적 명령 문자열 획득
	 */
	public static String getUniversalAutoCancelTime(int autoCancelMins, Locale locale) {
		switch (autoCancelMins) {
		case 0:
			return "자정";
		case 5:
			return "5 분후";
		case 10:
			return "10 분후";
		case 30:
			return "30 분후";
		case 60:
			return "1 시간후";
		case 120:
			return "2 시간후";
		case 180:
			return "3 시간후";
		case 360:
			return "6 시간후";
		case 720:
			return "12 시간후";
		}
		
		return "-";
	}
	
	/**
	 * 원격제어 명령에 의한 보편적 명령 문자열 획득
	 */
	public static String getUniversalCommandName(String cmd, Locale locale) {
		if (Util.isNotValid(cmd)) {
			return "";
		}
		
		if (cmd.endsWith(".bbmc")) {
			cmd = cmd.replace(".bbmc", "");
		}
		
		switch (cmd) {
		// 추가 파라미터없으며, 사용자에 의해 발행
		case "Reboot":
			return "STB 재시작(리부팅)";
		case "RestartAgent":
			return "에이전트 재시작";
		case "UpdateAgent":
			return "에이전트 업데이트";
		case "UploadDebugFile":
			return "디버그 로그 업로드";
		case "UploadTrxFile":
			return "원시 트랜잭션파일 업로드";
			
		// 추가 파라미터 필요 명령

		// 시스템 자동 발행 명령, 추가 파라미터 있음
		case "DeleteTrxFile":
			return "원시 트랜잭션파일 삭제";
		}

		return "";
	}

	/**
	 * 재생 시간 문자열 획득
	 */
	public static String getDurationStr(String val) {
		if (Util.isNotValid(val)) {
			return val;
		} else if (!Util.isFloatNumber(val) && !Util.isIntNumber(val) && !Util.isLongNumber(val)) {
			return val;
		}
		
		String overPointStr = "";
		String underPointStr = "";
		
		if (val.indexOf(".") > -1) {
			overPointStr = val.substring(0, val.indexOf("."));
			underPointStr = val.substring(val.indexOf("."));
		} else {
			overPointStr = val;
			underPointStr = "";
		}
		
		long longVal;
		try {
			longVal = Long.parseLong(overPointStr);
		} catch (Exception e) {
			return val;
		}
		
		if (underPointStr.indexOf(".") > -1 && underPointStr.length() > 4) {
			underPointStr = underPointStr.substring(0, 4);
		}
		
		long time[] = new long[] {0, 0, 0};

		time[2] = (longVal >= 60 ? longVal % 60 : longVal);                    // secs
		time[1] = (longVal = (longVal / 60)) >= 60 ? longVal % 60 : longVal;   // mins
		time[0] = longVal / 60;   // hours

		DecimalFormat dFormat = new DecimalFormat("00");
		
		if (time[0] > 0) {
			return time[0] + ":" + dFormat.format(time[1]) + ":" + dFormat.format(time[2]) + underPointStr;
		} else if (time[1] > 0) {
			return time[1] + ":" + dFormat.format(time[2]) + underPointStr;
		}
		
		return time[2] + underPointStr;
	}
	
	/**
	 * WOL 호출
	 */
	public static void wakeOnLan(String macAddress) {
		wakeOnLan(macAddress, Util.getIntFileProperty("wol.port"));
	}
	
	/**
	 * WOL 호출
	 */
	public static void wakeOnLan(String macAddress, int port) {
		try {
			if (Util.isValid(macAddress)) {
				byte[] bytes = getMagicBytes(macAddress);
				
				InetAddress address = getMulticastAddress();
				
				DatagramPacket p = new DatagramPacket(bytes, bytes.length, address, port);
				
				DatagramSocket socket = new DatagramSocket();
				
				socket.send(p);
				socket.close();
				
				logger.info("wakeOnLan Success - macAddress: " + macAddress + ", port: " + port);
			} else {
				logger.info("wakeOnLan Pass");
			}
		} catch (Exception e) {
			logger.error("macAddress: " + macAddress + ", port: " + port);
			logger.error("wakeOnLan Error", e);
		}
	}
	
	/**
	 * 매직 패킷 획득
	 */
	private static byte[] getMagicBytes(String macAddress) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        for (int i = 0; i < 6; i++)
            bytes.write(0xff);
        byte[] macAddressBytes = Util.parseHexString(macAddress);

        for (int i = 0; i < 16; i++)
            bytes.write(macAddressBytes);

        bytes.flush();

        return bytes.toByteArray();
	}

	/**
	 * 멀티캐스트 주소 획득
	 */
	private static InetAddress getMulticastAddress() throws UnknownHostException {
		return InetAddress.getByAddress(new byte[] {-1, -1, -1, -1});
	}

	/**
	 * 원격 STB에 연결 명령 호출
	 */
	public static boolean executeRemoteStbConnectCommand(String ip, int port) {
		if (Util.isNotValid(ip) || port < 1 || port > 65535) {
			return false;
		}
		
		String cmd = "CONNECT";
		
		try {
			InetAddress stbAddr = InetAddress.getByName(ip);
			
			DatagramSocket socket = new DatagramSocket();
			
            byte[] buf = (cmd).getBytes(); 
            DatagramPacket p = new DatagramPacket(buf, buf.length, stbAddr, port);
            
            socket.setSoTimeout(30000);
            socket.send(p);
            
            byte[] buf2 = new byte[1024];
            DatagramPacket packet2 = new DatagramPacket(buf2, buf2.length); 

            socket.receive(packet2);
            String ret = new String(packet2.getData(), 0, packet2.getLength(), "UTF-8");
            
            socket.close();

            return Util.isValid(ret) && ret.equals("Y");
		} catch (Exception e) {
			logger.error("executeRemoteStbConnectCommand", e);
		}
		
		return false;
	}
	
    /**
	 * Date 값이 1분 이내의 값 여부 체크
	 */
	public static boolean isDateDuring1Minute(Date date) {
		
		// 기기 시간 및 요청/처리 시간을 고려해서 2분으로 변경
		return isDateDuringMins(date, 2);
	}
	
    /**
	 * Date 값이 1분 이내의 값 여부 체크
	 */
	public static boolean isDateDuringMins(Date date, int mins) {
		if (date == null) {
			return false;
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		
		// 기기 시간 및 요청/처리 시간을 고려해서 2분으로 변경
		cal.add(Calendar.MINUTE, mins * -1);
		
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date);
		
		return date.compareTo(cal.getTime()) >= 0;
	}
	
    /**
	 * NOC 업무 코드에 대한 표시 문자열 획득
	 */
	public static String getNocTaskTypeDisp(String taskType, Locale locale) {
		
		return sMsgMgr.message("nocTaskType." + taskType, locale);
	}
	
    /**
	 * NOC 업무/업무 진행 상태 코드에 대한 표시 문자열 획득
	 */
	public static String getNocActionStatusDisp(String statusCode, Locale locale) {
		
		return sMsgMgr.message("nocTaskStatus." + statusCode, locale);
	}

    /**
	 * 사이트 설정 옵션 값을 획득
	 */
	public static String getSiteOption(int siteId, String name) {
		
		String mapKey = (Util.isValid(name) ? name : "") + "." + siteId;
		
		if (!GlobalInfo.SiteOptionMap.containsKey(mapKey) && Util.isValid(name) && siteId > 0) {
			String siteOptVal = sOptService.getSiteOption(siteId, name);
			if (Util.isValid(siteOptVal)) {
				GlobalInfo.SiteOptionMap.put(mapKey, siteOptVal);
			}
		}
		
		return GlobalInfo.SiteOptionMap.get(mapKey);
	}

	
    /**
	 * 특정 사이트 설정 옵션 값을 지움
	 */
	public static void clearSiteOption(int siteId) {
		
		Set<String> set = GlobalInfo.SiteOptionMap.keySet();
		Iterator<String> iterator = set.iterator();
		
		while(iterator.hasNext() ) {
			String key = (String)iterator.next();
			if (key.endsWith("." + siteId)) {
				GlobalInfo.SiteOptionMap.remove(key);
			}
		}
	}
    
	/**
	 * 두 문자열의 유사성 정도를 점수로 획득
	 */
	public static double getSimilarityScore(String s1, String s2) {
		
	    String longer = s1, shorter = s2;
	    if (s1.length() < s2.length()) { // longer should always have greater length
	      longer = s2; shorter = s1;
	    }
	    
	    int longerLength = longer.length();
	    if (longerLength == 0) { return 1.0; /* both strings are zero length */ }
	    /* // If you have Apache Commons Text, you can use it to calculate the edit distance:
	    LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
	    return (longerLength - levenshteinDistance.apply(longer, shorter)) / (double) longerLength; */
	    return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
	}
    
	/**
	 * Example implementation of the Levenshtein Edit Distance
	 */
	private static int editDistance(String s1, String s2) {
		
	    s1 = s1.toLowerCase();
	    s2 = s2.toLowerCase();

	    int[] costs = new int[s2.length() + 1];
	    for (int i = 0; i <= s1.length(); i++) {
	    	int lastValue = i;
	    	for (int j = 0; j <= s2.length(); j++) {
	    		if (i == 0)
	    			costs[j] = j;
	    		else {
	    			if (j > 0) {
	    				int newValue = costs[j - 1];
	    				if (s1.charAt(i - 1) != s2.charAt(j - 1))
	    					newValue = Math.min(Math.min(newValue, lastValue),
	    							costs[j]) + 1;
	    				costs[j - 1] = lastValue;
	    				lastValue = newValue;
	    			}
	    		}
	    	}
	    	if (i > 0)
	    		costs[s2.length()] = lastValue;
	    }
	    
	    return costs[s2.length()];
	}
    
	/**
	 * 기기의 운영 시간 문자열을 획득
	 */
	public static String getStbBizHours(int stbId, Locale locale) {
		
		String monBegin = null, monEnd = null, tueBegin = null, tueEnd = null;
		String wedBegin = null, wedEnd = null, thuBegin = null, thuEnd = null;
		String friBegin = null, friEnd = null, satBegin = null, satEnd = null;
		String sunBegin = null, sunEnd = null;
		
		if (monBegin == null || monEnd == null || tueBegin == null || tueEnd == null || 
				wedBegin == null || wedEnd == null || thuBegin == null || thuEnd == null || 
				friBegin == null || friEnd == null || satBegin == null || satEnd == null || 
				sunBegin == null || sunEnd == null) {
			
			return sMsgMgr.message("stboverview.noData", locale);
		}
		
		
		String[] dayDesc = new String[7];

		dayDesc[0] = getBizHoursDesc(monBegin, monEnd, locale);
		dayDesc[1] = getBizHoursDesc(tueBegin, tueEnd, locale);
		dayDesc[2] = getBizHoursDesc(wedBegin, wedEnd, locale);
		dayDesc[3] = getBizHoursDesc(thuBegin, thuEnd, locale);
		dayDesc[4] = getBizHoursDesc(friBegin, friEnd, locale);
		dayDesc[5] = getBizHoursDesc(satBegin, satEnd, locale);
		dayDesc[6] = getBizHoursDesc(sunBegin, sunEnd, locale);
		
		//logger.error("MON: " + monBegin + " / " + monEnd + " - " + dayDesc[0]);
		//logger.error("TUE: " + tueBegin + " / " + tueEnd + " - " + dayDesc[1]);
		//logger.error("WED: " + wedBegin + " / " + wedEnd + " - " + dayDesc[2]);
		//logger.error("THU: " + thuBegin + " / " + thuEnd + " - " + dayDesc[3]);
		//logger.error("FRI: " + friBegin + " / " + friEnd + " - " + dayDesc[4]);
		//logger.error("SAT: " + satBegin + " / " + satEnd + " - " + dayDesc[5]);
		//logger.error("SUN: " + sunBegin + " / " + sunEnd + " - " + dayDesc[6]);
		
		
		int idx = -1;
		String idxStr = "";
		String idxDay = "", currDay = "";
		String ret = "";
		String dayStr = "", currStr = "";
		for(int i = 0; i < 7; i++) {
			if (i == 0) { currDay = sMsgMgr.message("stboverview.mon", locale); }
			else if (i == 1) { currDay = sMsgMgr.message("stboverview.tue", locale); }
			else if (i == 2) { currDay = sMsgMgr.message("stboverview.wed", locale); }
			else if (i == 3) { currDay = sMsgMgr.message("stboverview.thu", locale); }
			else if (i == 4) { currDay = sMsgMgr.message("stboverview.fri", locale); }
			else if (i == 5) { currDay = sMsgMgr.message("stboverview.sat", locale); }
			else if (i == 6) { currDay = sMsgMgr.message("stboverview.sun", locale); }
			
			if (idx == -1) {
				// 시작 요일에 대한 처리
				idx = i;
				idxStr = dayDesc[i];
				idxDay = currDay;
			}
			
			currStr = dayDesc[i];
			if (idxStr.equals(currStr)) {
				if (i == idx) {
					dayStr = idxDay + " : " + idxStr;
				} else if (i == idx + 1) {
					dayStr = idxDay + "/" + currDay + " : " + idxStr;
				} else {
					if (idxDay.equals(sMsgMgr.message("stboverview.mon", locale)) && currDay.equals(sMsgMgr.message("stboverview.sun", locale))) {
						dayStr = sMsgMgr.message("stboverview.everyDay", locale) + " : " + idxStr;
					} else {
						dayStr = idxDay + "-" + currDay + " : " + idxStr;
					}
				}
			} else {
				// 종료 처리
				if (idx != 0) {
					ret += ", ";
				}
				ret += dayStr;
				
				// 현재 요일로의 초기화
				idx = i;
				idxStr = dayDesc[i];
				idxDay = currDay;
				
				dayStr = idxDay + " : " + idxStr;
			}
		}
		
		if (Util.isValid(dayStr)) {
			// 종료 처리가 되지 않은 상태
			if (idx != 0) {
				ret += ", ";
			}
			ret += dayStr;
		}
		
		//logger.error("");
		//logger.error("last: " + ret);
		
		return ret;
	}
	
	private static String getBizHoursDesc(String beginTime, String endTime, Locale locale) {
		
		String begin = getBizHoursDesc(beginTime, true, locale);
		String end = getBizHoursDesc(endTime, false, locale);
		
		if (Util.isNotValid(begin) && Util.isNotValid(end)) {
			return sMsgMgr.message("stboverview.closed", locale);
		} else if (Util.isNotValid(begin)) {
			return "- " + end;
		} else if (Util.isNotValid(end)) {
			return begin + " -";
		} else if (begin.equals(end) || (begin.equals("0") && end.equals("24"))) {
			return "24H";
		} else {
			return begin + " - " + end;
		}
	}
	
	private static String getBizHoursDesc(String time, boolean isFirstPart, Locale locale) {
		
		if (Util.isNotValid(time)) {
			return "";
		}
		
		String ret = "";
		
		if (!isFirstPart && time.startsWith("00")) {
			ret = "24";
		} else if (time.startsWith("0")) {
			ret = time.substring(1, 2);
		} else {
			ret = time.substring(0, 2);
		}
		
		if (time.endsWith(":00")) {
			return ret;
		} else {
			return ret + time.substring(3);
		}
	}
	
    /**
	 * 서버로 문자열 스트림 전송
	 */
	public static int sendStreamToServer(String url, String ctntType, String data) {
		if (Util.isNotValid(url) || Util.isNotValid(ctntType) || 
				Util.isNotValid(data)) {
			return 0;
		}
		
		OutputStreamWriter wr= null;
		try {
    		URL urlObj = new URL(url);
        	HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
			
        	// 서버 통신을 위한 필수 설정
        	conn.setDoOutput(true);
        	conn.setDoInput(true);

        	conn.setRequestProperty("Content-Type", ctntType);
        	conn.setRequestMethod("POST");
        	
           	wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            wr.write(data);
            wr.flush();
            
            return conn.getResponseCode();
    	} catch (Exception e) {
    		logger.error("sendStreamToServer", e);
    	} finally {
    		if (wr != null) {
    			try {
    				wr.close();
    			} catch (IOException ex) {
        			logger.error("sendStreamToServer", ex);
    			}
    		}
    	}
		
		return -2;
	}
}
