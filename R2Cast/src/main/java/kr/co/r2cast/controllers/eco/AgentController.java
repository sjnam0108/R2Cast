package kr.co.r2cast.controllers.eco;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import kr.co.r2cast.models.MessageManager;
import kr.co.r2cast.models.eco.MonEventReport;
import kr.co.r2cast.models.eco.MonTask;
import kr.co.r2cast.models.eco.RtnSchdTaskRvm;
import kr.co.r2cast.models.eco.Rvm;
import kr.co.r2cast.models.eco.RvmLastReport;
import kr.co.r2cast.models.eco.RvmStatusLine;
import kr.co.r2cast.models.eco.UpdSetupFile;
import kr.co.r2cast.models.eco.service.MonitoringService;
import kr.co.r2cast.models.eco.service.OptService;
import kr.co.r2cast.models.eco.service.RvmService;
import kr.co.r2cast.models.eco.service.UpdService;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.service.SiteService;
import kr.co.r2cast.utils.SolUtil;
import kr.co.r2cast.utils.Util;
import kr.co.r2cast.viewmodels.eco.RvmCommandItem;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 * SignCast Agent/STB 컨트롤러
 */
@Controller("eco-agent-controller")
@RequestMapping(value="/eco/agent")
public class AgentController {
	private static final Logger logger = LoggerFactory.getLogger(AgentController.class);

	@Autowired 
    private RvmService rvmService;

    @Autowired 
    private MonitoringService monService;
	
	@Autowired 
	private OptService optService;

    @Autowired 
    private SiteService siteService;
    
    @Autowired
    private UpdService updService;

	@Autowired
	private MessageManager msgMgr;
	
	public static String boolToward(boolean b) {
		return (b == true) ? "Y" : "N";
	}

    /**
	 * RVM 초기 구동 시 서버에 등록된 STB 정보 반환
	 */
    @RequestMapping(value = "/rvminfo", method = RequestMethod.GET)
    public void rvmInfo(HttpServletRequest request, 
    		HttpServletResponse response) 
    				throws ServletException, IOException {
    	
    	Document document = DocumentHelper.createDocument();
        Element rootEl = document.addElement("R2Cast");
        Element rvmEl = rootEl.addElement("Server");
        rootEl.addAttribute("generated", Util.toSimpleString(new Date()));
        
        String siteUkid = request.getParameter("site");
        String deviceID = request.getParameter("deviceID");
        
        String rvmId = "-1";
        String rvmName = "";
        String ftpHost = "www.r2cast.co.kr";
        String ftpPort = "21";
        String ftpUser = "r2cast";
        String ftpPassword = "r2cast1234";

        String serverHost = "www.r2cast.co.kr";
        String serverPort = "80";
        String serverUkid = "";
        String serialNo = "";
        String areaCode = "";
        String branchCode = "";
        String branchName = "";
        String custIdReq = "";
        String importReq = "";
        String resultType = "";
        String reportInterval = "";
        
        Rvm rvm = null;

        if (deviceID != null && !deviceID.isEmpty() && siteUkid != null && !siteUkid.isEmpty()) {
        	rvm = rvmService.getEffectiveRvmByDeviceIDSiteShortName(deviceID ,siteUkid);
        	
        	if (rvm == null) {
        		// maybe http://auth.bbmc.co.kr/ext/agent/v2/autoreg?deviceId={0}
        		String checkUrl = Util.getFileProperty("url.checkAutoReg");
        		if (Util.isValid(checkUrl)) {
        			String result = Util.readResponseFromUrl(checkUrl.replace("{0}", deviceID));
        			if (Util.isValid(result) && result.startsWith("user:") && result.length() > 5) {
        				String tmpRvmName = result.substring(5);
        				Site site = siteService.getSite(siteUkid);
        				
        				if (site != null) {
        					String siteOptVal = optService.getSiteOption(site.getId(), "auto.rvmReg");
        					if (Util.isValid(siteOptVal) && siteOptVal.equals("Y")) {
            					Rvm tmpRvm = rvmService.getRvm(site, tmpRvmName);
            					if (tmpRvm != null) {
            						for (int i = 1; i <= 100; i++) {
            							tmpRvmName = result.substring(5) + " (" + i + ")";
            							tmpRvm = rvmService.getRvm(site, tmpRvmName);
            							if (tmpRvm == null) {
            								break;
            							}
            						}
            					}
            					
            					if (tmpRvm == null) {
                					Rvm newRvm = new Rvm(site, tmpRvmName);
                					newRvm.setDeviceID(deviceID);
                					
                					newRvm.setRvmLatitude(optService.getSiteOption(site.getId(), "map.init.latitude"));
                					newRvm.setRvmLongitude(optService.getSiteOption(site.getId(), "map.init.longitude"));
                					newRvm.setModel("etc");
                					newRvm.setServiceType("I");
                					
                		            try {
                		                rvmService.saveOrUpdate(newRvm);
                		            } catch (Exception e) {
                		        		logger.error("stbInfo", e);
                		            }
            					}
//            		            
            		            rvm = rvmService.getRvm(deviceID);
        					}
        				}
        			}
        		}
        	}
        	
        	if (rvm != null) {
        		rvmId = String.valueOf(rvm.getId());
        		rvmName = rvm.getRvmName();
        		deviceID = rvm.getDeviceID();
        		ftpHost = rvm.getSite().getFtpHost();
        		ftpPort = String.valueOf(rvm.getSite().getFtpPort());
        		ftpUser = rvm.getSite().getFtpUsername();
        		ftpPassword = rvm.getSite().getFtpPassword();
        		serverHost = rvm.getSite().getServerHost();
        		serverPort = String.valueOf(rvm.getSite().getServerPort());
        		serverUkid = rvm.getSite().getShortName();
        		serialNo = rvm.getSerialNo();
                areaCode = rvm.getAreaCode();
                branchCode = rvm.getBranchCode();
                branchName = rvm.getBranchName();
                custIdReq = String.valueOf(boolToward(rvm.getCustomerIdRequired()));
                importReq = String.valueOf(boolToward(rvm.getImportRequired()));
                resultType = rvm.getResultType();
                reportInterval = String.valueOf(rvm.getReportInterval());
        	}
        }
        
        rvmEl.addAttribute("rvmId", rvmId);
        rvmEl.addAttribute("rvmName", rvmName);
        rvmEl.addAttribute("ftpHost", ftpHost);
        rvmEl.addAttribute("ftpPort", ftpPort);
        rvmEl.addAttribute("ftpUser", ftpUser);
        rvmEl.addAttribute("ftpPassword", ftpPassword);
        rvmEl.addAttribute("deviceID", deviceID);
        rvmEl.addAttribute("serverHost", serverHost);
        rvmEl.addAttribute("serverPort", serverPort);
        rvmEl.addAttribute("serverUkid", serverUkid);
        rvmEl.addAttribute("serialNo", serialNo);
        rvmEl.addAttribute("areaCode", areaCode);
        rvmEl.addAttribute("branchCode", branchCode);
        rvmEl.addAttribute("branchName", branchName);
        rvmEl.addAttribute("custIdReq", custIdReq.toString());
        rvmEl.addAttribute("importReq", importReq.toString());
        rvmEl.addAttribute("resultType", resultType);
        rvmEl.addAttribute("reportInterval", reportInterval);

        //
        // 별도의 Properties, EditionProperties, Options 생략(이후 추가 가능성은 있음)
        //
        
        
        response.setHeader("Cache-Control", "no-store");              // HTTP 1.1
        response.setHeader("Pragma", "no-cache, must-revalidate");    // HTTP 1.0
        response.setContentType("text/xml;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        PrintWriter pw = response.getWriter();

        document.write(pw);
    }
    
    /**
	 * RVM 구동 후 전달된 RVM 정보에 따른 RVM 상태 보고
	 */
    @RequestMapping(value = "/rvmsttsreport", method = RequestMethod.GET)
    public void rvmStatusReport(HttpServletRequest request, 
    		HttpServletResponse response, HttpSession session) 
    				throws ServletException, IOException{
    	
    	int rvmId = Util.parseInt(request.getParameter("rvmId"));
    	String deviceID = Util.parseString(request.getParameter("deviceID"));
    	String status = Util.parseString(request.getParameter("status"));
    	
    	
    	Rvm rvm = null;
        if (rvmId > 0 && !deviceID.isEmpty() && !status.isEmpty())
        {
        	rvm = rvmService.getEffectiveRvmByDeviceIDRvmId(deviceID, rvmId);
        	if (rvm != null) {
        		RvmLastReport rvmLastReport = rvm.getRvmLastReport();
        		if (rvmLastReport == null) {
        			rvmLastReport = new RvmLastReport(rvm);
        			rvm.touchWho(session);
        		}
        		
        		if (status.equals("0")) {
        			status = "2";
        		}

        		
        		// 외부IP 다를 경우 변경
        		String remoteAddr = request.getRemoteAddr();
        		if (!remoteAddr.equals(rvm.getExternalIp())) {
        			rvm.setExternalIp(remoteAddr);

        			rvm.touchWho(session);
        		}
        		
        		
        		//
        		// Status Line
        		//
        		RvmStatusLine todayStatusLine = monService.getRvmStatusLineByStateDateRvmId(
        				Util.removeTimeOfDate(new Date()), rvmId);
        		if (todayStatusLine == null) {
        			todayStatusLine = new RvmStatusLine(rvm);
        		}
        		
        		todayStatusLine.setLastStatus(status);
        		todayStatusLine.setStatusLine(SolUtil.getRvmStatusLine(
        				todayStatusLine.getStatusLine() , new Date(), status));
        		todayStatusLine.setRunningMinCount(
        				SolUtil.getRvmRunningMinutes(todayStatusLine.getStatusLine()));
        		
        		todayStatusLine.touchWho(session);
        		
        		try {
        			monService.saveOrUpdate(todayStatusLine);

                } catch (Exception e) {
            		logger.error("rvmStatusReport - todayStatusLine", e);
                }
        		// - Status Line[E]

        		//
        		// 상위까지가 status, rvmId, deviceID로 처리하는 부분
        		// ---------------------------------------------------
        		
        		
        		//
        		// 여기서부터는 reportTypes에 따른 전달 값 저장
        		//
        		int reportTypes = Util.parseInt(request.getParameter("reportTypes"));
        		if (reportTypes > -1) {

        			//
        			//  보고 유형 목록
        			//
        			//  1) ONE_TIME_ON_START: R2Cast 앱이 시작할 때, 1회 보고되는 내용
        			//     agentStart, model, version, diskSize, diskFree, usedRatio, time 총 7개 항목
        			//
        			
        			// 0: ONE_TIME_ON_START = 1
        			if (((reportTypes >>> 0) & 1) != 0) {
        				
        				// 값 저장을 위한 6개 항목
                    	Date agentStartDt = Util.parseDate(request.getParameter("agentStart"));
                    	String model = Util.parseString(request.getParameter("model"));
                    	String version = Util.parseString(request.getParameter("version"));
                    	
                    	float diskSize = Util.parseFloat(request.getParameter("diskSize"));
                    	float diskFree = Util.parseFloat(request.getParameter("diskFree"));
                    	float usedRatio = Util.parseFloat(request.getParameter("usedRatio"));
                    	
                    	// 기기 상태 확인을 위한 1개 항목
                    	Date rvmDt = Util.parseDate(request.getParameter("time"));
                    	
                    	
                    	// agentStart: 에이전트 시작
                    	if (agentStartDt != null) {
                			rvmLastReport.setAgentStartDate(agentStartDt);
                    	}
                    	
                    	// model: 모델명(QB, QB2, KRZ 등)
                		if (Util.isValid(model)) {
                			rvm.setModel(model);
                		}
                		
                		// version: 버전
                		if (Util.isValid(version)) {
                			rvmLastReport.setAppVersion(version);
                		}
                		
                		// diskSize, diskFree, usedRatio: 디스크 크기, 여유, 사용율(%)
                		if (diskSize > 0 && diskFree >= 0 && usedRatio > -1) {
                			rvmLastReport.setDiskSize(diskSize);
                			rvmLastReport.setDiskFree(diskFree);
                			rvmLastReport.setDiskUsedRatio(usedRatio);
                			
            				if (diskFree >= 0 && diskFree <= 3) {
                    			try {
                    				monService.saveOrUpdate(
                    						new MonEventReport(rvm.getSite(), "R", "W", "P", rvm.getId(), rvm.getRvmName(),
                    								"RVMHEALTH", "free:" + diskFree + "G"));
                    			} catch (Exception e) {
                    	    		logger.error("rvmStatusReport - RVMHEALTH", e);
                    			}
            				}
                		}
                		
                		// time: 기기 시간 확인. 1시간 이상 차이가 날 경우
                		if (rvmDt != null) {
                			long[] diffs = Util.getDiffTimespanArr(rvmDt);
                			
                			if (diffs[1] > 0 || diffs[2] > 0 || diffs[3] > 0 || diffs[4] > 0) {
                    			try {
                    				monService.saveOrUpdate(
                    						new MonEventReport(rvm.getSite(), "R", "W", "P", rvm.getId(), rvm.getRvmName(),
                    								"RVMTIME", "diff:" + Util.getEngTimespan(diffs) + 
                    								"(" + Util.toSimpleString(rvmDt) + ")"));
                    			} catch (Exception e) {
                    	    		logger.error("rvmStatusReport - RVMTIME", e);
                    			}
                			}
                		}
                		
        			}
        			
        		}
        		
        		
        		
        		// ---------------------------------------------------
        		// 하위부터는 공통 처리 부분
        		//
        		
        		//
        		// RVM 및 Last Report 변경 - 마지막 단계
        		//
        		if (!remoteAddr.equals(rvmLastReport.getReportIp())) {
            		rvmLastReport.setReportIp(remoteAddr);
        		}
        		
        		rvmLastReport.setStatus(status);
        		
        		rvmLastReport.touchWho(session);
        		
        		try {
        			rvmService.saveOrUpdate(rvm);
        			monService.saveOrUpdate(rvmLastReport);

                } catch (Exception e) {
            		logger.error("rvmStatusReport", e);
                }
        	} else {
        		rvm = rvmService.getRvm(rvmId);
        		if (rvm != null && rvmService.isEffectiveRvm(rvm)) {
        			Calendar cal = Calendar.getInstance();
        			cal.add(Calendar.HOUR, -6);
        			
        			if (monService.getMonEventReportCount(cal.getTime(), "RVMALIVE", "P", rvmId) == 0) {
            			try {
            				monService.saveOrUpdate(
            						new MonEventReport(rvm.getSite(), "R", "W", "P", rvm.getId(), rvm.getRvmName(),
            								"RVMALIVE", "deviceID:" + deviceID));
            			} catch (Exception e) {
            	    		logger.error("rvmStatusReport", e);
            			}
        			}
        		}
        	}
        }
    		
    	Document document = DocumentHelper.createDocument();

        response.setHeader("Cache-Control", "no-store");              // HTTP 1.1
        response.setHeader("Pragma", "no-cache, must-revalidate");    // HTTP 1.0
        response.setContentType("text/plain;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        PrintWriter pw = response.getWriter();
        Element dataEl = document.addElement("data");
        Element commandsEl = dataEl.addElement("commands");
        
        if (rvm != null) {
        	ArrayList<RvmCommandItem> toBeExecuted = new ArrayList<RvmCommandItem>();
        	Date now = new Date();
        	
        	List<RtnSchdTaskRvm> routineTaskRvms = monService.getNextRtnSchdTaskRvmListByRvmId(rvmId, now);
        	List<MonTask> monTasks = monService.getNextMonTaskListByRvmId(rvmId, now);
        	
        	try {
        		for (RtnSchdTaskRvm taskRvm : routineTaskRvms) {
	    			monService.saveOrUpdate(new MonTask(rvm.getSite(), rvm, taskRvm.getRtnSchdTask().getCommand(), 
	    					"", "", "2", taskRvm.getDestDate(), taskRvm.getCancelDate(), 
	    					taskRvm.getRtnSchdTask(), taskRvm));

	    			toBeExecuted.add(new RvmCommandItem(-1 * taskRvm.getId(), taskRvm.getRtnSchdTask().getCommand(), 
	    					"", ""));
        		}
        		
        		for (MonTask monTask : monTasks) {
        			monTask.setStatus("2");
        			monTask.touchWho(session);
        			
        			monService.saveOrUpdate(monTask);

	    			toBeExecuted.add(new RvmCommandItem(monTask.getId(), monTask.getCommand(), monTask.getParams(),
	    					monTask.getExecTime()));
        		}
        	} catch (Exception e) {
        		logger.error("rvmStatusReport", e);
            }
        	
        	for (RvmCommandItem item : toBeExecuted) {
        		Element commandEl = commandsEl.addElement("command");
        		
        		commandEl.addAttribute("rcCommandId", String.valueOf(item.getId()));
        		commandEl.addAttribute("command", item.getCommand());
        		commandEl.addAttribute("execTime", item.getExecTime());
        		
        		commandEl.addCDATA(item.getParams());
        	}
        } else {
        	dataEl.addAttribute("exists", "");
        }
        
		if (Util.getBooleanFileProperty("rvmTime.synchronized", true)) {
	        dataEl.addAttribute("servertime", System.currentTimeMillis() + "");
		}

        document.write(pw);
    }
    
    /**
	 * STB 원격제어 명령 수행 결과 보고
	 */
    @RequestMapping(value = "/rvmrccmd", method = RequestMethod.GET)
    public void rvmRcCmd(HttpServletRequest request, 
    		HttpServletResponse response, HttpSession session) 
    				throws ServletException, IOException {
    	int rcCmdId = Util.parseInt(request.getParameter("rcCmdId"), 0);
    	
		// jason:extendstbtaskstatus: STB 작업의 결과 상태 확장(2015/03/03)
    	String result = Util.parseString(request.getParameter("result"), "S");
    	if (result.length() != 1) {
    		result = "S";
    	}
    	//-

    	try {
    		MonTask target = null;
    		
    		if (rcCmdId > 0) {
    			target = monService.getMonTask(rcCmdId);
    		} else if (rcCmdId < 0) {
    			target = monService.getMonTaskByRtnSchdTaskRvmId(rcCmdId * -1);
    		}

			if (target != null) {
				// jason:extendstbtaskstatus: STB 작업의 결과 상태 확장(2015/03/03)
				//target.setStatus("3");
				target.setStatus(result);
				//-
				target.touchWho(session);
				
				monService.saveOrUpdate(target);
			}
    	} catch (Exception e) {
    		logger.error("rvmRcCmd", e);
        }
    }

    /**
	 * 컨텐츠 동기화 시 필요한 게시된 컨텐츠 목록 획득
	 */
    @RequestMapping(value = "/dctntinfo", method = RequestMethod.GET)
    public void dctntInfo(HttpServletRequest request, 
    		HttpServletResponse response) 
    				throws ServletException, IOException {
    	Document document = DocumentHelper.createDocument();
    	
    	Element itemsEl = document.addElement("Items");
    	String macAddress = request.getParameter("deviceID");
    	String ver = Util.parseString(request.getParameter("ver"), "1");
    	
        if (Util.isValid(macAddress)) {
        	Rvm rvm = rvmService.getEffectiveRvmByDeviceID(macAddress);
        	
        	if (rvm != null) {
        		// CTA 체크 대상 체크
//        		if (DsgGlobalInfo.CTAStbIds.contains(stb.getId())) {
//            		int siteId = stb.getSite().getId();
//            		
//            		List<Integer> candDplySchdIds = SolUtil.getCtaCandiDplySchdList(siteId, stb.getId());
//            		List<DplySchedule> dplySchdList = schdService.getDplyScheduleListBySiteId(siteId);
//            		for (DplySchedule dplySchedule : dplySchdList) {
//            			if (candDplySchdIds.contains(dplySchedule.getId())) {
//                			SolUtil.checkSchdPkgMatchRule(dplySchedule, stb, null);
//            			}
//            		}
//            		
//            		List<Integer> candDplyOthrIds = SolUtil.getCtaCandiDplyOthrList(siteId, stb.getId());
//            		List<DplyOther> dplyOthrList = schdService.getDplyOtherListBySiteId(siteId);
//            		for (DplyOther dplyOther : dplyOthrList) {
//            			if (candDplyOthrIds.contains(dplyOther.getId())) {
//                			SolUtil.checkOthrPkgMatchRule(dplyOther, stb, null);
//            			}
//            		}
//
//        			DsgGlobalInfo.CTAStbIds.remove(new Integer(stb.getId()));
//        		}
        		//-
        		
        		// 서버 스케줄링 되었을 경우, 강제 다운로드 대상 확인
//        		ArrayList<String> forcedFilenames = new ArrayList<String>();
//        		List<Content> ctntList = ctntService.getContentListBySiteId(stb.getSite().getId());
//        		for(Content content : ctntList) {
//        			if (content.getForcedDownload().equals("Y")) {
//        				forcedFilenames.add(String.format("[%s]%s", stb.getSite().getShortName(), content.getFilename()));
//        			}
//        		}
        		
        		
        		Element itemEl = null;

//        		List<DplySchdCondFile> schdFiles = schdService.getDplySchdCondFileListByStbId(stb.getId(), false);
//        		for(DplySchdCondFile condFile : schdFiles) {
//        			itemEl = itemsEl.addElement("Item");
//        			
//        			itemEl.addAttribute("foldername", condFile.getDplySchdCond().getDplySchedule().getId()
//        					+ "/" + condFile.getDplySchdFile().getFolderName());
//        			itemEl.addAttribute("filename", condFile.getDplySchdFile().getFilename());
//        			itemEl.addAttribute("filelength", String.valueOf(condFile.getDplySchdFile().getFileLength()));
//        			itemEl.addAttribute("stbfileid", String.valueOf(condFile.getId()));
//        			itemEl.addAttribute("playatonce", (condFile.getDplySchdFile().getFilename().toLowerCase().endsWith(".scd") &&
//        					ver.equals("2") && condFile.getDplySchdCond().isPlayOnDownload()) ? "Y" : "N");
//        			itemEl.addAttribute("forceddownload", forcedFilenames.contains(condFile.getDplySchdFile().getFilename()) ? "Y" : "N");
//        			itemEl.addAttribute("kfileid", "-1");
//        			itemEl.addAttribute("kroot", "");
//        		}

//        		List<DplyOthrCondFile> othrFiles = schdService.getDplyOthrCondFileListByStbId(stb.getId());
//        		for(DplyOthrCondFile condFile : othrFiles) {
//        			itemEl = itemsEl.addElement("Item");
//        			
//        			itemEl.addAttribute("foldername", condFile.getDplyOthrCond().getDplyOther().getId()
//        					+ "/" + condFile.getDplyOthrFile().getFolderName());
//        			itemEl.addAttribute("filename", condFile.getDplyOthrFile().getFilename());
//        			itemEl.addAttribute("filelength", String.valueOf(condFile.getDplyOthrFile().getFileLength()));
//        			itemEl.addAttribute("stbfileid", "-1");
//        			itemEl.addAttribute("kfileid", String.valueOf(condFile.getId()));
//        			itemEl.addAttribute("kroot", condFile.getDplyOthrCond().getDplyOther().getStbRootDir());
//        			itemEl.addAttribute("playatonce", "N");
//        			itemEl.addAttribute("forceddownload", "N");
//        		}
        		
        		// 기타 컨텐츠 폴더 동기화 옵션이 더 추가되어 그 정보를
        		// Items 노드의 syncdir attribute로 전달함
//        		List<OtherBundle> list = schdService.getOtherBundleListBySiteId(stb.getSite().getId());
//        		String syncDir = "";
//        		for(OtherBundle bundle : list) {
//        			if (bundle.getDirSyncType().equals("Y")) {
//        				if (Util.isValid(syncDir)) {
//        					syncDir += ";";
//        				}
//        				syncDir += bundle.getStbRootDir();
//        			}
//        		}
        		
//        		itemsEl.addAttribute("syncdir", syncDir);
        	}
        }
        
        response.setHeader("Cache-Control", "no-store");              // HTTP 1.1
        response.setHeader("Pragma", "no-cache, must-revalidate");    // HTTP 1.0
        response.setContentType("text/xml;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        PrintWriter pw = response.getWriter();

        document.write(pw);
    }

    /**
	 * 컨텐츠 동기화 시 STB 존재 컨텐츠 파일 보고
	 */
    @RequestMapping(value = "/dctntreport", method = { RequestMethod.GET, RequestMethod.POST })
    public void dctntReport(HttpServletRequest request, 
    		HttpServletResponse response, HttpSession session) 
    				throws ServletException, IOException {
    	String macAddress = request.getParameter("deviceID");
    	String completeY = request.getParameter("completeY");
    	String completeN = request.getParameter("completeN");
    	String completeKY = request.getParameter("completeKY");
    	String completeKN = request.getParameter("completeKN");
    	
    	boolean success = true;
    	
        if (Util.isValid(macAddress)) {
        	Rvm rvm = rvmService.getEffectiveRvmByDeviceID(macAddress);
        	
        	if (rvm != null) {
        		List<String> items = Util.tokenizeValidStr(completeY);
        		
        		for(String idStr : items) {
//        			if (Util.isIntNumber(idStr)) {
//        				DplySchdCondFile condFile = schdService.getDplySchdCondFile(Util.parseInt(idStr));
//        				if (condFile != null && success) {
//        					condFile.setTransferred("Y");
//        					condFile.touchWho(session);
//        					
//        					schdService.saveOrUpdate(condFile);
//        				} else {
//        					success = false;
//        				}
//        			}
        		}
        		
        		if (success) {
        			items = Util.tokenizeValidStr(completeN);
            		
            		for(String idStr : items) {
//            			if (Util.isIntNumber(idStr)) {
//            				DplySchdCondFile condFile = schdService.getDplySchdCondFile(Util.parseInt(idStr));
//            				if (condFile != null && success) {
//            					condFile.setTransferred("N");
//            					condFile.touchWho(session);
//            					
//            					schdService.saveOrUpdate(condFile);
//            				} else {
//            					success = false;
//            				}
//            			}
            		}
        		}
        		
        		if (success) {
        			items = Util.tokenizeValidStr(completeKY);
            		
            		for(String idStr : items) {
//            			if (Util.isIntNumber(idStr)) {
//            				DplyOthrCondFile condFile = schdService.getDplyOthrCondFile(Util.parseInt(idStr));
//            				if (condFile != null && success) {
//            					condFile.setTransferred("Y");
//            					condFile.touchWho(session);
//            					
//            					schdService.saveOrUpdate(condFile);
//            				} else {
//            					success = false;
//            				}
//            			}
            		}
        		}
        		
        		if (success) {
        			items = Util.tokenizeValidStr(completeKN);
            		
            		for(String idStr : items) {
//            			if (Util.isIntNumber(idStr)) {
//            				DplyOthrCondFile condFile = schdService.getDplyOthrCondFile(Util.parseInt(idStr));
//            				if (condFile != null && success) {
//            					condFile.setTransferred("N");
//            					condFile.touchWho(session);
//            					
//            					schdService.saveOrUpdate(condFile);
//            				} else {
//            					success = false;
//            				}
//            			}
            		}
        		}
        	}
        }
        
        PrintWriter out = new PrintWriter(new OutputStreamWriter(
      		  response.getOutputStream(), "UTF-8"));
        
        response.setHeader("Cache-Control", "no-store");              // HTTP 1.1
        response.setHeader("Pragma", "no-cache, must-revalidate");    // HTTP 1.0
        response.setContentType("text/plain;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        out.print(success ? "Y" : "N");
        out.close();
    }
    
    /**
	 * Manager/Player의 업데이트 정보 반환
	 */
    @RequestMapping(value = "/updateinfo", method = { RequestMethod.GET, RequestMethod.POST })
    public void updateInfo(HttpServletRequest request, HttpSession session, 
    		HttpServletResponse response) 
    				throws ServletException, IOException {
    	Document document = DocumentHelper.createDocument();
    	
    	List<UpdSetupFile> setupFiles = updService.getUpdSetupFileListByPublished("Y");
    	Collections.sort(setupFiles, new Comparator<UpdSetupFile>() {
	    	public int compare(UpdSetupFile item1, UpdSetupFile item2) {
	    		return item1.getReleaseDate().compareTo(item2.getReleaseDate());
	    	}
	    });
    	
    	String localLang = Util.getFileProperty("lang.update.desc");
    	localLang = Util.isNotValid(localLang) ? "en" : localLang;
    	
        Element rootEl = document.addElement("Items");
        rootEl.addAttribute("generated", Util.toSimpleString(new Date()));
        rootEl.addAttribute("locallang", localLang);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        
        for(UpdSetupFile setupFile : setupFiles) {
            Element itemEl = rootEl.addElement("Item");
            
            itemEl.addAttribute("type", setupFile.getProgType());
            itemEl.addAttribute("editioncode", setupFile.getEdition());
            itemEl.addAttribute("version", setupFile.getVersion());
            itemEl.addAttribute("length", String.valueOf(setupFile.getFileLength()));
            itemEl.addAttribute("stable", setupFile.getFileType().equals("S") ? "True" : "False");
            itemEl.addAttribute("date", sdf.format(setupFile.getReleaseDate()));
            
            Element descEngEl = itemEl.addElement("DescEn");
            descEngEl.addCDATA(setupFile.getDescEng());
            
            Element descLocalEl = itemEl.addElement("DescKo");
            descLocalEl.addCDATA(setupFile.getDescLocal());
        }
        
        response.setHeader("Cache-Control", "no-store");              // HTTP 1.1
        response.setHeader("Pragma", "no-cache, must-revalidate");    // HTTP 1.0
        response.setContentType("text/xml;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        PrintWriter pw = response.getWriter();

        document.write(pw);
    }
    

    /**
	 * 업데이트 설치 파일 중 최신의 안정 버전 정보 획득
	 */
    @RequestMapping(value = "/updatelatest", method = { RequestMethod.GET, RequestMethod.POST })
    public void updateLatest(HttpServletRequest request, 
    		HttpServletResponse response, HttpSession session) 
    				throws ServletException, IOException {
        String progType = Util.parseString(request.getParameter("type"), "");
        String edition = Util.parseString(request.getParameter("edition"), "");
        String vendor = Util.parseString(request.getParameter("vendor"), "");

        PrintWriter out = new PrintWriter(new OutputStreamWriter(
      		  response.getOutputStream(), "UTF-8"));
        
        response.setHeader("Cache-Control", "no-store");              // HTTP 1.1
        response.setHeader("Pragma", "no-cache, must-revalidate");    // HTTP 1.0
        response.setContentType("text/plain;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        out.print(updService.getLatestUpdSetupFileStableVersionByEditionProgType(
        		edition, progType, vendor));
        out.close();
    }
    
    /**
	 * 기기(Player, Server 등)의 이벤트 보고
	 */
    @RequestMapping(value = "/reportevent", method = RequestMethod.GET)
    public void reportEvent(HttpServletRequest request, 
    		HttpServletResponse response, HttpSession session) 
    				throws ServletException, IOException {
    	Site site = siteService.getSite(request.getParameter("site"));
    	if (site != null) {
    		String equipType = Util.parseString(request.getParameter("type"), "");
    		String equipName = Util.parseString(request.getParameter("name"), "");
    		String event = Util.parseString(request.getParameter("event"), "");
    		String details = Util.parseString(request.getParameter("detail"), "");
    		String reportType = Util.parseString(request.getParameter("repType"), "0");
    		
    		String category = "P";
    		
    		int equipId = Util.parseInt(request.getParameter("id"), -1);
    		
    		// equipType: P(플레이어), M(매니저), B(Backup Server), W(WAS), D(DB Server), F(FTP Server)
    		// reportType: I(Info), W(Warning), E(Error)
    		//
    		// 예)
    		//   site=asp&type=P&id=100&name=STBName&event=STBWER&detail=proc:SignCastPlayer
    		//   site=asp&type=B&id=1&event=DBBACKUP&detail=size:1.4M
    		
    		if (Util.isValid(equipType) && Util.isValid(event) && equipId > 0) {
    			if (equipType.equals("B")) {
    				equipName = "Backup Server #" + equipId; 
    			} else if (equipType.equals("W")) {
    				equipName = "WAS #" + equipId; 
    			} else if (equipType.equals("D")) {
    				equipName = "DB Server #" + equipId; 
    			} else if (equipType.equals("F")) {
    				equipName = "FTP Server #" + equipId; 
    			}
    			
    			if (Util.isValid(equipName)) {
    				if (!reportType.equals("I") && !reportType.equals("W") && !reportType.equals("E")) {
    					reportType = "I";
    				}
    				
    				if (event.equals("STBYELLOW") || event.equals("STBTIME") || event.equals("STBMODEL") || 
    						event.equals("STBHEALTH") || event.equals("STBSHOT")) {
    					category = "R";
    				} else if (event.equals("WRONGFILE")) {
    					category = "O";
    				} else if (event.equals("STBWER") || event.equals("STBRES")) {
    					category = "G";
    				}
    				
        			try {
//        				if (monService.isInSeriousWERStatus(event, equipType, equipId)) {
        	    			Rvm rvm = rvmService.getRvm(equipId);
        	    			if (rvm != null) {
            	    			String command = "Reboot.bbmc";
            	    			
            	    			Calendar cal = Calendar.getInstance();
            	    			cal.add(Calendar.MINUTE, 10);
            	    			
            	    			Date cancelDate = cal.getTime();
            	    			
//        	        			monService.saveOrUpdate(new MonTask(stb.getSite(), stb, command, 
//        	        					"", null, "1", new Date(), cancelDate, null, null, session));
//        	        			monService.checkStbRemoteControlTypeAndLastReportTime(stb, command);
//
//        	        			monService.saveOrUpdate(
//                						new MonEventReport(site, "R", "I", equipType, equipId, equipName,
//                								"STBWERINIT", "act:Reboot"));
        	    			}
//        				}
        				else if (event.equals("STBSHOT")) {
                			Calendar cal = Calendar.getInstance();
                			cal.add(Calendar.HOUR, -3);
                			
//                			if (monService.getMonEventReportCount(cal.getTime(), "STBSHOT", "P", equipId) == 0) {
//                				monService.saveOrUpdate(
//                						new MonEventReport(site, category, reportType, equipType, equipId, equipName,
//                								event, details));
//                			}
        				} else {
        					// Windows XP에서 이중으로 등록되는 STBWER 이벤트에 대해 직전 등록 건을 삭제
        					if (event.equals("STBWER") && Util.isValid(details) && details.indexOf("proc:R2CastPlayer") > -1) {
//        						Rvm rvm = rvmService.getRvm(equipId);
//        						if (rvm != null && astService.isAssetModuleAppliedBySiteId(stb.getSite().getId())) {
//        							String os = astService.getStbModelOSbySiteModelShortName(astService.getAssetSiteIDBySiteId(
//        									stb.getSite().getId()), stb.getModel());
//        							if (Util.isValid(os) && os.equals("Windows XP")) {
//                            			Calendar cal = Calendar.getInstance();
//                            			cal.add(Calendar.SECOND, -90);
//                            			
//            							MonEventReport lastReport = monService.getLastMonEventReport(cal.getTime(), "STBWER", 
//            									"proc:Other", "P", equipId);
//            							if (lastReport != null) {
//            								monService.deleteMonEventReport(lastReport);
//            							}
//        							}
//        						}
        					}
        					//-
        					
//            				monService.saveOrUpdate(
//            						new MonEventReport(site, category, reportType, equipType, equipId, equipName,
//            								event, details));
        				}
        			} catch (Exception e) {
        	    		logger.error("reportEvent", e);
        			}
    			}
    		}
    	}
    }

    /**
	 * 대한민국의 동네 날씨 정보 반환
	 */
    @RequestMapping(value = "/localweatherinfo", method = { RequestMethod.GET, RequestMethod.POST })
    public void localWeatherInfo(HttpServletRequest request, HttpSession session, 
    		HttpServletResponse response) 
    				throws ServletException, IOException {
    	Document document = DocumentHelper.createDocument();
        Element rootEl = document.addElement("Data");
        rootEl.addAttribute("generated", Util.toSimpleString(new Date()));
    	
    	int rvmId = Util.parseInt(request.getParameter("rvmId"), -1);
    	
    	Rvm rvm = null;
        if (rvmId >= 0)
        {
        	String localCode = "09650101";  // 서초구 임시값
        	String rvmName = "RVM_TEMP";
        	
        	rvm = rvmService.getRvm(rvmId);
        	if (rvm != null) {
        		if (Util.isValid(rvm.getLocalCode()) && rvm.getLocalCode().length() == 8) {
        			localCode = rvm.getLocalCode();
        		}
    			
    			rvmName = rvm.getRvmName();
        	}
        	
    		try {
    			// 기존 URL source에서 File Source로 변경
    			/*
    			String url = Util.getFileProperty("url.localWeather");
    			if (Util.isValid(url)) {
    				if (url.startsWith("/")) {
    					url = request.getRequestURL().toString().replace(request.getRequestURI(), "") + url;
    				}
    				
        			SAXReader reader = new SAXReader();
        			document = reader.read(new URL(url.replace("{0}", localCode)));
        			
        			rootEl = document.getRootElement();
        			rootEl.addElement("Stb").addElement("Name").setText(stbName);
    			}
    			*/
    			//-
    			
    			File xmlFile = new File((SolUtil.getPhysicalRoot("WeatherData") + "/weather_{0}.xml")
    					.replace("{0}", localCode));
    			if (xmlFile.exists()) {
    				SAXReader reader = new SAXReader();
    				document = reader.read(xmlFile);
        			
        			rootEl = document.getRootElement();
        			rootEl.addElement("Rvm").addElement("Name").setText(rvmName);
    			} else {
    				throw new Exception("File Not Found: weather_" + localCode + ".xml");
    			}
    		} catch (Exception e) {
	    		logger.error("localWeatherInfo", e);
    		}
        }
        
        response.setHeader("Cache-Control", "no-store");              // HTTP 1.1
        response.setHeader("Pragma", "no-cache, must-revalidate");    // HTTP 1.0
        response.setContentType("text/xml;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        PrintWriter pw = response.getWriter();

        document.write(pw);
    }
    
    /**
	 * 기기에서 사용될 데이터피드
	 */
    @RequestMapping(value = "/datafeed", method = { RequestMethod.GET, RequestMethod.POST })
    public void dataFeed(HttpServletRequest request, 
    		HttpServletResponse response, HttpSession session) 
    				throws ServletException, IOException {
    	Document document = DocumentHelper.createDocument();
    	
//    	List<UpdSetupFile> setupFiles = updService.getUpdSetupFileListByPublished("Y");
//    	Collections.sort(setupFiles, DsgComparator.UpdSetupFileReleaseDateComparator);
    	
    	String localLang = Util.getFileProperty("lang.update.desc");
    	localLang = Util.isNotValid(localLang) ? "en" : localLang;
    	
        Element rootEl = document.addElement("Data");
        
//        if (DsgGlobalInfo.FireAlarmActive != null && Util.isValid(DsgGlobalInfo.FireAlarmType)) {
//        	Element fireAlarmEl = rootEl.addElement("FireAlarm");
//        	
//        	fireAlarmEl.addAttribute("type", DsgGlobalInfo.FireAlarmType);
//        	fireAlarmEl.addAttribute("active", DsgGlobalInfo.FireAlarmActive.toString());
//        }
        
        response.setHeader("Cache-Control", "no-store");              // HTTP 1.1
        response.setHeader("Pragma", "no-cache, must-revalidate");    // HTTP 1.0
        response.setContentType("text/xml;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        PrintWriter pw = response.getWriter();

        document.write(pw);
    }
    
    /**
	 * FCM 토큰 등록
	 */
    @RequestMapping(value = "/token", method = {RequestMethod.GET, RequestMethod.POST})
    public void setFCMToken(HttpServletRequest request, HttpServletResponse response) 
    		throws ServletException, IOException {
        
        String ret = "F";
        
    	request.setCharacterEncoding("UTF-8");
    	
        String deviceId = Util.parseString(request.getParameter("deviceId"), "");
        String token = Util.parseString(request.getParameter("token"), "");
        
        if (!Util.isValid(deviceId) || !Util.isValid(token)) {
        	// 잘못된 인자 전달
        	// Pass: return "F"
        } else {
        	ret = "N";
        	
        	Rvm rvm = rvmService.getEffectiveRvmByDeviceID(deviceId);
        	if (rvm != null) {
        		try {

        		} catch (Exception e) {
                	logger.error("setFCMToken", e);
        		}
        		
        		ret = "Y";
        	}

    		String syncUrl = Util.getFileProperty("url.syncToken");
    		if (Util.isValid(syncUrl)) {
    			String result = Util.readResponseFromUrl(syncUrl.replace("{0}", deviceId).replace("{1}", token));
    			if (Util.isValid(result) && result.equals("N")) {
		        	logger.info("setFCMToken - wrong deviceID: " + deviceId);
    			}
    		}
        }
        
        response.setHeader("Cache-Control", "no-store");              // HTTP 1.1
        response.setHeader("Pragma", "no-cache, must-revalidate");    // HTTP 1.0
        response.setContentType("text/plain;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        PrintWriter pw = response.getWriter();

        pw.print(ret);
        pw.flush();
    }

    private String getValidCaptionStr(String s) {
    	
    	if (Util.isValid(s)) {
    		if (s.length() > 0) {
    			return s;
    		}
    	}
    	
    	return null;
    }
    
    /**
	 * 서버 자막 정보 반환
	 */
    @RequestMapping(value = "/caption", method = { RequestMethod.GET, RequestMethod.POST })
    public void serverCaption(HttpServletRequest request, 
    		HttpServletResponse response, Locale locale) 
    				throws ServletException, IOException {
    	Document document = DocumentHelper.createDocument();
    	
        Element rootEl = document.addElement("Data");
        
        rootEl.addAttribute("generated", Util.toSimpleString(new Date()));
        
        String siteUkid = request.getParameter("site");
        
        
		List<String> itemList = new ArrayList<String>();
		
        if (Util.isValid(siteUkid)) {
        	Site site = siteService.getSite(siteUkid);
        	if (site != null) {
//        		SiteCaption siteCap = null;
//    			List<SiteCaption> captionList = dsgService.getSiteCaptionDefaultValueListBySiteId(site.getId());
//    			if (captionList.size() > 0) {
//    				siteCap = captionList.get(0);
//    			}
        		
//    			if (siteCap != null) {
//    				String line01 = getValidCaptionStr(siteCap.getLine01());
//    				String line02 = getValidCaptionStr(siteCap.getLine02());
//    				String line03 = getValidCaptionStr(siteCap.getLine03());
//    				String line04 = getValidCaptionStr(siteCap.getLine04());
//    				String line05 = getValidCaptionStr(siteCap.getLine05());
//    				String line06 = getValidCaptionStr(siteCap.getLine06());
//    				String line07 = getValidCaptionStr(siteCap.getLine07());
//    				String line08 = getValidCaptionStr(siteCap.getLine08());
//    				String line09 = getValidCaptionStr(siteCap.getLine09());
//    				String line10 = getValidCaptionStr(siteCap.getLine10());
//    				String line11 = getValidCaptionStr(siteCap.getLine11());
//    				String line12 = getValidCaptionStr(siteCap.getLine12());
//    				String line13 = getValidCaptionStr(siteCap.getLine13());
//    				String line14 = getValidCaptionStr(siteCap.getLine14());
//    				String line15 = getValidCaptionStr(siteCap.getLine15());
    				
//    				if (Util.isValid(line01)) { itemList.add(line01); }
//    				if (Util.isValid(line02)) { itemList.add(line02); }
//    				if (Util.isValid(line03)) { itemList.add(line03); }
//    				if (Util.isValid(line04)) { itemList.add(line04); }
//    				if (Util.isValid(line05)) { itemList.add(line05); }
//    				if (Util.isValid(line06)) { itemList.add(line06); }
//    				if (Util.isValid(line07)) { itemList.add(line07); }
//    				if (Util.isValid(line08)) { itemList.add(line08); }
//    				if (Util.isValid(line09)) { itemList.add(line09); }
//    				if (Util.isValid(line10)) { itemList.add(line10); }
//    				if (Util.isValid(line11)) { itemList.add(line11); }
//    				if (Util.isValid(line12)) { itemList.add(line12); }
//    				if (Util.isValid(line13)) { itemList.add(line13); }
//    				if (Util.isValid(line14)) { itemList.add(line14); }
//    				if (Util.isValid(line15)) { itemList.add(line15); }
    				
    				
					// 서버 자막 항목은 존재하나 포함된 행의 자료가 모두 유효하지 않은 상태(code: -1)
    				if (itemList.size() == 0) {
    					itemList.add(msgMgr.message("sitecaption.server.msg.configRequired", locale) + "(code: -1)");
    				}
    			}
        	}
//        }
        
		// 서버 자막 항목 미존재하거나 기본값 설정이 안되어 있는 상태(code: -2)
        if (itemList.size() == 0) {
			itemList.add(msgMgr.message("sitecaption.server.msg.configRequired", locale) + "(code: -2)");
        }

        for(String s : itemList) {
            Element itemEl = rootEl.addElement("Item");
			itemEl.addCDATA(s);
        }
        
        response.setHeader("Cache-Control", "no-store");              // HTTP 1.1
        response.setHeader("Pragma", "no-cache, must-revalidate");    // HTTP 1.0
        response.setContentType("text/xml;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        PrintWriter pw = response.getWriter();

        document.write(pw);
    }
    
    /**
	 * 서버 자막 정보 갱신
	 */
    @RequestMapping(value = "/updateCaption", method = {RequestMethod.GET, RequestMethod.POST})
    public void updateCaption(HttpServletRequest request, HttpServletResponse response) 
    		throws ServletException, IOException {
        
        String ret = "F";
        
    	request.setCharacterEncoding("UTF-8");
    	
        String code = Util.parseString(request.getParameter("code"), "");
        
        if (!Util.isValid(code)) {
        	// 잘못된 인자 전달
        	// Pass: return "F"
        } else {
        	ret = "N";
        	
        	List<String> lines = Util.tokenizeValidStr(Util.parseString(request.getParameter("line"), ""));
        	
        	if (lines.size() > 0) {
//            	List<SiteCaption> siteCaptions = dsgService.getSiteCaptionListByShortName(code);
//            	for(SiteCaption siteCaption : siteCaptions) {
//            		try {
//                		if (siteCaption.getShortName().equals(code)) {
//                			if (lines.size() > 0) {
//                				siteCaption.setLine01(lines.get(0).replace("__vert__", "|")); 
//                			} else {
//                				siteCaption.setLine01("");
//                			}
//                			if (lines.size() > 1) {
//                				siteCaption.setLine02(lines.get(1).replace("__vert__", "|")); 
//                			} else {
//                				siteCaption.setLine02("");
//                			}
//                			if (lines.size() > 2) {
//                				siteCaption.setLine03(lines.get(2).replace("__vert__", "|")); 
//                			} else {
//                				siteCaption.setLine03("");
//                			}
//                			if (lines.size() > 3) {
//                				siteCaption.setLine04(lines.get(3).replace("__vert__", "|")); 
//                			} else {
//                				siteCaption.setLine04("");
//                			}
//                			if (lines.size() > 4) {
//                				siteCaption.setLine05(lines.get(4).replace("__vert__", "|")); 
//                			} else {
//                				siteCaption.setLine05("");
//                			}
//                			
//                			if (lines.size() > 5) {
//                				siteCaption.setLine06(lines.get(5).replace("__vert__", "|")); 
//                			} else {
//                				siteCaption.setLine06("");
//                			}
//                			if (lines.size() > 6) {
//                				siteCaption.setLine07(lines.get(6).replace("__vert__", "|")); 
//                			} else {
//                				siteCaption.setLine07("");
//                			}
//                			if (lines.size() > 7) {
//                				siteCaption.setLine08(lines.get(7).replace("__vert__", "|")); 
//                			} else {
//                				siteCaption.setLine08("");
//                			}
//                			if (lines.size() > 8) {
//                				siteCaption.setLine09(lines.get(8).replace("__vert__", "|")); 
//                			} else {
//                				siteCaption.setLine09("");
//                			}
//                			if (lines.size() > 9) {
//                				siteCaption.setLine10(lines.get(9).replace("__vert__", "|")); 
//                			} else {
//                				siteCaption.setLine10("");
//                			}
//                			
//                			if (lines.size() > 10) {
//                				siteCaption.setLine11(lines.get(10).replace("__vert__", "|")); 
//                			} else {
//                				siteCaption.setLine11("");
//                			}
//                			if (lines.size() > 11) {
//                				siteCaption.setLine12(lines.get(11).replace("__vert__", "|")); 
//                			} else {
//                				siteCaption.setLine12("");
//                			}
//                			if (lines.size() > 12) {
//                				siteCaption.setLine13(lines.get(12).replace("__vert__", "|")); 
//                			} else {
//                				siteCaption.setLine13("");
//                			}
//                			if (lines.size() > 13) {
//                				siteCaption.setLine14(lines.get(13).replace("__vert__", "|")); 
//                			} else {
//                				siteCaption.setLine14("");
//                			}
//                			if (lines.size() > 14) {
//                				siteCaption.setLine15(lines.get(14).replace("__vert__", "|")); 
//                			} else {
//                				siteCaption.setLine15("");
//                			}
//                			
//                			rvmService.saveOrUpdate(siteCaption);
//                		}
//            		} catch (Exception e) {
//                    	logger.error("updateCaption", e);
//            		}
//            		
//            		ret = "Y";
//            	}
        	}
        }
        
        response.setHeader("Cache-Control", "no-store");              // HTTP 1.1
        response.setHeader("Pragma", "no-cache, must-revalidate");    // HTTP 1.0
        response.setContentType("text/plain;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        PrintWriter pw = response.getWriter();

        pw.print(ret);
        pw.flush();
    }

    /**
	 * 템플릿 타일의 동적 파일 자료 목록 반환 - 향후 개선
	 */
    /*
    @RequestMapping(value = "/tiledata", method = { RequestMethod.GET, RequestMethod.POST })
    public void getTileData(HttpServletRequest request, HttpSession session, 
    		HttpServletResponse response) 
    				throws ServletException, IOException {
    	
    	Document document = DocumentHelper.createDocument();
        Element rootEl = document.addElement("Data");
        rootEl.addAttribute("generated", Util.toSimpleString(new Date()));
        
        String startPage = "";
        
        // 요청인자:
        //    - tile: 타일ID, 예) HV001, BIRAF006
        //    - folder: 폴더, 기준은 [SIGNCAST_HOME] 예) /download/abc
        //    - data: 자료ID, 예) EGS2108
        //    - stbId: 기기id, 예) 50
        //    - site: 사이트 short name, 예) signcast
        //
        // 요청인자 관련 설명:
        //    - 핵심은 "자료ID". 자료ID에 따라 서버에서 설정한 파일 목록 및 광고id 목록을 획득하게 됨
        //      stbId, site, folder는 모두 "자료ID"에서 참조가능하게 위한 보조값
        //    - "타일ID". 플레이어에서의 로컬웹페이지 컴포넌트에서 직접 파일 경로를 지정하지 않고
        //      타일ID를 지정하게 되면, 템플릿타일에서 타일ID로 지정한 경로의 진입 파일 경로를 얻게 됨
        // 이 메소드를 호출할 경우:
        //    - 로컬웹페이지에서 지정된 타일ID를 바탕으로 서버에서 설정된 템플릿타일의 진입 파일 경로 획득(1)
        //    - 자료ID에 따라 서버에서 지정한 파일 및 광고 목록 획득(2)
        //    - 1과 2의 경우를 함께 획득
        
		Stb stb = stbService.getStb(Util.parseInt(request.getParameter("stbId"), -1));
    	Site site = siteService.getSite(request.getParameter("site"));
    	if (stb != null && site != null) {
    		String tile = Util.parseString(request.getParameter("tile"), "");
    		//String folder = Util.parseString(request.getParameter("folder"), "");
    		String data = Util.parseString(request.getParameter("data"), "");
    		
    		if (Util.isValid(tile)) {
    			TmplTile tmplTile = dataService.getTmplTile(site, tile);
    			if (tmplTile != null) {
    				// 시작 진입 페이지는 현재 html.html로 지정.
    				// 이후 변동 가능성 있음
    				startPage = "/" + tmplTile.getTileCode() + "/html.html";
    			}
    		}
    		
    		// dataID에 따라 사이트 전체 적용되는 단순 모델을 현재 적용함
    		// folder값은 참조 안함
    		if (Util.isValid(data)) {
    			List<TmplTileFile> list = dataService.getTmplTileFileListBySiteId(site.getId());
    			ArrayList<TmplTileFile> retList = new ArrayList<TmplTileFile>();

    			Date now = new Date();
    			
    			for(TmplTileFile tileFile : list) {
    				if (tileFile.getDataCode().equals(data)) {
    					if (tileFile.getEffectiveStartDate() != null && 
    							now.before(tileFile.getEffectiveStartDate())) {
    						continue;
    					} else if (tileFile.getEffectiveEndDate() != null && 
    							now.after(tileFile.getEffectiveEndDate())) {
    						continue;
    					}
    					
    					retList.add(tileFile);
    				}
    			}
    			
    			Collections.sort(retList, new Comparator<TmplTileFile>() {
    				public int compare(TmplTileFile f1, TmplTileFile f2) {
    					return Integer.compare(f1.getSiblingSeq(), f2.getSiblingSeq());
    				}
    			});
    			
    			for(TmplTileFile tileFile : retList) {
    	            Element itemEl = rootEl.addElement("Item");
    	            
    	            itemEl.addAttribute("name", tileFile.getName());
    	            itemEl.addAttribute("file", tileFile.getPathFilename());
    	            itemEl.addAttribute("adID", tileFile.getAdCode());
    			}
    		}
    	}
        
        rootEl.addAttribute("start", startPage);
        
        response.setHeader("Cache-Control", "no-store");              // HTTP 1.1
        response.setHeader("Pragma", "no-cache, must-revalidate");    // HTTP 1.0
        response.setContentType("text/xml;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        PrintWriter pw = response.getWriter();

        document.write(pw);
    }
    */
    
	/**
	 * 플레이어 정보 보고를 기록
	 */
	@RequestMapping(value = "/rcvPlayerInfo", method = RequestMethod.POST)
	public void rcvPlayerInfo(HttpServletRequest request, 
			HttpServletResponse response) {
		
    	BufferedReader reader = null;
		String line = null;
		StringBuilder builder = new StringBuilder();
		
    	try {
			reader = request.getReader();
			while ((line = reader.readLine()) != null) {
				builder.append(line);
				builder.append(System.getProperty("line.separator"));
			}
		} catch (Exception e) {
			logger.error("rcvPlayerInfo", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
	    			logger.error("rcvPlayerInfo", ex);
				}
			}
		}
    	
    	String info = Util.removeTrailingChar(builder.toString(), System.getProperty("line.separator"));
    	if (Util.isValid(info)) {
        	logger.info("----------------- rcvPlayerInfo");
        	logger.info(info);
        	
        	String deviceID = "", schedule = "";
        	ArrayList<String> schedules = new ArrayList<String>();
        	ArrayList<Long> lengthes = new ArrayList<Long>();

        	ArrayList<String> configTypes = new ArrayList<String>();
        	ArrayList<String> configNames = new ArrayList<String>();
        	ArrayList<String> configValues = new ArrayList<String>();

        	
        	// 따로 저장이 필요한 항목명을 따로 확인
        	// 이 값이 존재하면, 매 STB 자료건마다 해당 STB로 등록된 자료를 한꺼번에 가져와서 비교한다.
        	String configKeys = Util.getFileProperty("config.keys");
        	
    		try {
        		JSONObject infoObj = JSONObject.fromObject(JSONSerializer.toJSON(info));
        		if (infoObj != null) {
        			deviceID = infoObj.getString("deviceID");
        			schedule = infoObj.getString("schedule");
        			
        			JSONArray schedArray = infoObj.getJSONArray("schedules");
        			if (schedArray != null) {
        				for (int i = 0; i < schedArray.size(); i++) {
        					JSONObject sObj = schedArray.getJSONObject(i);
        					schedules.add(sObj.getString("name"));
        					lengthes.add(sObj.getLong("length"));
            		    }        			
        			}
        			
        			try {
            			JSONArray configArray = infoObj.getJSONArray("configs");
            			if (configArray != null) {
            				for (int i = 0; i < configArray.size(); i++) {
            					JSONObject sObj = configArray.getJSONObject(i);
            					configTypes.add(sObj.getString("type"));
            					configNames.add(sObj.getString("name"));
            					configValues.add(sObj.getString("value"));
                		    }        			
            			}
        			} catch (Exception e1) {}

        			
        			String apps = "";
        			try {
            			JSONArray appArray = infoObj.getJSONArray("apps");
            			if (appArray != null) {
            				for (int i = 0; i < appArray.size(); i++) {
            					JSONObject sObj = appArray.getJSONObject(i);
            					String app = sObj.getString("name") + " " + sObj.getString("version");
            					
            					if (Util.isValid(apps)) {
            						apps += "|";
            					}
            					apps += app;
                		    }        			
            			}
        			} catch (Exception e1) {}

        			
        			Rvm rvm = rvmService.getRvm(deviceID);
        			if (rvm != null) {
//        				AnzMaster master = anlsService.getLastMaster(stb.getSite());
//        				if (master == null) {
//            				// 신규 자료일 경우 일단 master 자료 저장
//        					master = new AnzMaster(stb.getSite(), 
//        							Util.toSimpleString(new Date(), "yyMMdd") + "_" + String.format("%02d", 1));
//        					
//        					// 신규 자료에서의 대상 기기 수 계산
//        					List<Stb> stbs = stbService.getEffectiveStbList();
//        					int stbCount = 0;
//        					for(Stb stb2 : stbs) {
//        						if (stb2.getServiceType().equals("S") || stb2.getServiceType().equals("M")) {
//        							String ver = stb2.getStbLastReport().getAppVersion();
//        							if (Util.isValid(ver) && ver.startsWith("AE2.1.")) {
//        								List<String> verParts = Util.tokenizeValidStr(ver.substring(6), ".");
//        								if (verParts.size() == 2) {
//        									int ver1 = Util.parseInt(verParts.get(0));
//        									int ver2 = Util.parseInt(verParts.get(1));
//        									if (ver1 > 4 || (ver1 == 4 && ver2 >= 65)) {
//        										stbCount++;
//        									}
//        								}
//        							}
//        						}
//        					}
//        					master.setTargetCount(stbCount);
//        					
//        					anlsService.saveOrUpdate(master);
//        				}
//        				
//        				// 기 등록된 Rpt 자료 삭제
//    					AnzRptStb rptStb = anlsService.getRptStb(master, stb.getId());
//    					if (rptStb != null) {
//    						anlsService.deleteRptStb(rptStb);
//    					}
//    					List<AnzRptStbSchedule> rptStbSchedules = anlsService.getRptStbScheduleListByMasterIdStbId(master.getId(), stb.getId());
//    					if (rptStbSchedules != null && rptStbSchedules.size() > 0) {
//    						anlsService.deleteRptStbSchedules(rptStbSchedules);
//    					}
//    					List<AnzRptStbConfig> rptStbConfigs = anlsService.getRptStbConfigListByMasterIdStbId(master.getId(), stb.getId());
//    					if (rptStbConfigs != null && rptStbConfigs.size() > 0) {
//    						anlsService.deleteRptStbConfigs(rptStbConfigs);
//    					}
//        				
//    					// Rpt 자료 신규 등록(업데이트는 하지 않음)
//    					anlsService.saveOrUpdate(new AnzRptStb(master, stb, apps));
//    					
//    					for(int i = 0; i < schedules.size(); i ++) {
//    						String s = schedules.get(i);
//            				AnzRptStbSchedule rptSchedule = new AnzRptStbSchedule(master, stb, s, lengthes.get(i));
//            				if (s.equals(schedule)) {
//            					rptSchedule.setDefaultValue(true);
//            				}
//            				
//            				anlsService.saveOrUpdate(rptSchedule);
//    					}
//    					
//    					for(int i = 0; i < configTypes.size(); i ++) {
//            				AnzRptStbConfig rptConfig = new AnzRptStbConfig(master, stb, configTypes.get(i), configNames.get(i), configValues.get(i));
//            				
//            				anlsService.saveOrUpdate(rptConfig);
//    					}
//    					
//    					// 앱 정보를 변경
//    					if (!stb.getServiceNo().equals(apps)) {
//    						stb.setServiceNo(apps);
//    						stbService.saveOrUpdate(stb);
//    					}
//    					
//    					
//    					// 이 값이 유효하다는 것은 이 사이트에서 중요 구성 항목을 따로 보관한다는 것이고,
//    					// 따라서 매 STB에 대해 기존 저장되어 있는 항목의 조회가 필요하다.
//    					if (Util.isValid(configKeys)) {
//        					List<StbConfig> stbConfigs = stbService.getStbConfigListByStbId(stb.getId());
//        					List<String> configKeyList = Util.tokenizeValidStr(configKeys);
//        					String keyText = "";
//        					
//        					for(int i = 0; i < configTypes.size(); i ++) {
//        						keyText = configTypes.get(i) + "." + configNames.get(i);
//        						if (configKeyList.contains(keyText)) {
//        							// 중요 구성 항목임
//        							
//        							StbConfig thisConfig = null;
//        							for(StbConfig config : stbConfigs) {
//        								if (config.getKeyText().equals(keyText)) {
//        									thisConfig = config;
//        									break;
//        								}
//        							}
//        							
//        							if (thisConfig == null) {
//        								// DB 등록 필요
//        								stbService.saveOrUpdate(new StbConfig(stb, keyText, configValues.get(i)));
//        							} else {
//        								if (thisConfig.getValue().equals(configValues.get(i))) {
//        									
//        								} else {
//        									thisConfig.setValue(configValues.get(i));
//        									thisConfig.touchWho(null);
//        									
//        									stbService.saveOrUpdate(thisConfig);
//        								}
//        							}
//        						}
//        					}
//    						
//    					}
//    					
//            			
//                    	logger.info("==> saved to db");
        			}
        		}
    		} catch (Exception e) {
    			logger.error("rcvPlayerInfo - json parsing", e);
    		}
    	}
	}
}
