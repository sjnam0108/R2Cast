package kr.co.r2cast.dbsetup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import kr.co.r2cast.models.fnd.Menu;
import kr.co.r2cast.models.fnd.Privilege;
import kr.co.r2cast.models.fnd.Role;
import kr.co.r2cast.models.fnd.RolePrivilege;
import kr.co.r2cast.models.fnd.Site;
import kr.co.r2cast.models.fnd.SiteUser;
import kr.co.r2cast.models.fnd.User;
import kr.co.r2cast.models.fnd.UserPrivilege;
import kr.co.r2cast.utils.HibernateUtil;

public class InitDataCreation {
	/**
	 * Main Method
	 */
	public static void main(String[] args) {
		Session session = HibernateUtil.getSessionFactory().openSession();

		
		// ----------------------------------------------------------------
		// 
		// 권한:
		//
		//     AccessAnyChildSite   모든 자식 사이트 접근 권한
		//     AccessAnyMenu        모든 메뉴 접근 권한
		//     ManageSiteJob        사이트 관리 권한
		//     NoConcurrentCheck    동일계정 동시사용체크 무효화 권한
		//     NoTimeOut            세션만기시간 무효화 권한
		//
		//
		//     상세 설명:
		//     
		//         AccessAnyChildSite:
		//            - 현재 사이트와 매핑된 모든 자식 사이트 및 자식 사이트 하위 STB 그룹 "사용자 뷰"를 
		//              이용할 수 있는 권한.
		//            - 이 권한의 원할한 사용을 위해서는 "ManageSiteJob" 권한이 필요함.
		//
		//         AccessAnyMenu:
		//            - 서버에 등록된 모든 메뉴에 접근할 수 있는 권한.
		//            - super user인 경우에는 이 권한만 부여하면 충분하며, 어떤 사용자 계정에 특정 페이지 
		//              접근만 허락할 경우에는 대상 페이지 메뉴 권한을 사용자 계정에 부여해야 함.
		//
		//         ManageSiteJob:
		//            - 사이트 수준의 모든 관리 작업을 수행할 수 있는 권한.
		//
		//         NoConcurrentCheck:
		//            - 동일 계정 사용자의 동시 사용(타 브라우저, 타 기기) 체크를 무효화 시키는 권한.
		//            - 보안의 요소보다 편의성 요소를 더 중요시 하는 고객 또는 상황 발생 시 적용.
		//            - 이 권한을 가진 사용자 계정은 동일 시점 동시 사용이 가능함.
		//
		//         NoTimeOut:
		//            - WAS에 설정된 세션 만기 시간을 무효화 시키는 권한.
		//            - 보안의 요소보다 편의성 요소를 더 중요시 하는 고객 또는 상황 발생 시 적용.
		//            - 이 권한을 가진 사용자는 로그인 시 세션 만기 시간이 자동적으로 무효화되며 수동적으로
		//              로그아웃 버튼을 누르거나 사용중인 브라우저가 닫히기 전까지 계속 세션이 유효하게 됨.
		//            - 기본적으로 롤에 포함되지 않으며, 필요시 개별 사용자에게 권한으로 부여함.
		//
		// ----------------------------------------------------------------
		// 
		// SignCast 권한:
		//
		//     AccessAnyAd          모든 광고 접근 권한
		//     AccessAnyStbGroup    모든 STB 그룹 접근 권한
		//     ControlFireAlarm		화재경보스위치 제어 권한
		//     LoginSSO             SSO 로그인 허용 권한
		//     ManageEtcJob         통합 관제 업무 중 STB 기타 관리 권한
		//     ManageHWJob          통합 관제 업무 중 STB H/W 관리 권한
		//     ManageMAJob          통합 관제 업무 중 STB 유지보수 관리 권한
		//     ManageNWJob          통합 관제 업무 중 STB N/W 관리 권한
		//     ManagerAdmin         SignCast Manager 로그인/동기화 권한
		//     ViewRecentTask       STB 최근 작업 조회
		//
		//
		//     상세 설명:
		//     
		//         AccessAnyAd:
		//            - 모든 광고 정보를 조회/관리할 수 있는 권한.
		//
		//         AccessAnyStbGroup:
		//            - 모든 STB 그룹 "사용자 뷰"를 이용할 수 있는 권한.
		//
		//         ControlFireAlarm:
		//            - 사용자 홈에서 화재경보스위치를 제어할 수 있는 권한.
		//
		//         LoginSSO:
		//            - SSO 로그인 경로(/sso)를 통해 접근한 사용자 계정의 SSO 허가 여부 지정 권한.
		//
		//         ManageEtcJob:
		//            - 통합 관제 업무진행 항목 중 "기타 파트" 업무 진행을 조회/관리할 수 있는 권한.
		//            - 통합 관제 업무는 관제센터, MA 파트, H/W 파트, N/W 파트, 기타 파트 등 총 5개 그룹으로 
		//              구분되며, 전체 업무 진행을 위해서는 "ManageSiteJob" 권한이 필요함.
		//
		//         ManageHWJob:
		//            - 통합 관제 업무진행 항목 중 "H/W 파트" 업무 진행을 조회/관리할 수 있는 권한.
		//            - 통합 관제 업무는 관제센터, MA 파트, H/W 파트, N/W 파트, 기타 파트 등 총 5개 그룹으로 
		//              구분되며, 전체 업무 진행을 위해서는 "ManageSiteJob" 권한이 필요함.
		//
		//         ManageMAJob:
		//            - 통합 관제 업무진행 항목 중 "MA 파트" 업무 진행을 조회/관리할 수 있는 권한.
		//            - 통합 관제 업무는 관제센터, MA 파트, H/W 파트, N/W 파트, 기타 파트 등 총 5개 그룹으로 
		//              구분되며, 전체 업무 진행을 위해서는 "ManageSiteJob" 권한이 필요함.
		//
		//         ManageNWJob:
		//            - 통합 관제 업무진행 항목 중 "N/W 파트" 업무 진행을 조회/관리할 수 있는 권한.
		//            - 통합 관제 업무는 관제센터, MA 파트, H/W 파트, N/W 파트, 기타 파트 등 총 5개 그룹으로 
		//              구분되며, 전체 업무 진행을 위해서는 "ManageSiteJob" 권한이 필요함.
		//
		//         ManagerAdmin:
		//            - SignCast Manager 프로그램을 이용한 서버 로그인이 가능하며, 로그인 후에는 
		//              Manager 프로그램에 있는 서버와의 모든 작업(서버 스케줄 저장, 컨텐츠 동기화 등)을 
		//              수행하는 것이 가능함.
		//
		//         ManageSiteJob:
		//            - 사이트 수준의 모든 관리 작업을 수행할 수 있는 권한.
		//            - 모든 통합 관제 업무 진행(관제센터, MA 파트, H/W 파트, N/W 파트, 기타 파트 등 총 5개 그룹)을 
		//              조회/관리할 수 있는 권한.
		//            - 모든 광고 정보를 조회/관리할 수 있는 권한(AccessAnyAd).
		//            - 모든 STB 그룹 "사용자 뷰"를 이용할 수 있는 권한(AccessAnyStbGroup).
		//
		//         ViewRecentTask:
		//            - 글로벌 페이지 기능인 "STB 최근 작업" 목록을 조회할 수 있는 권한.
		//            - 글로벌 페이지 기능인 "FTP 서버 파일 전송" 목록을 조회할 수 있는 권한.
		//
		// ----------------------------------------------------------------
		
		

		// ----------------------------------------------------------------
		// 
		// 롤:
		//
		//     SystemAdmin      모든 사이트 관리 목적의 슈퍼 관리자 롤
		//
		//
		//     상세 설명:
		//     
		//         SystemAdmin:
		//            - 모든 사이트 관리 목적의 슈퍼 관리자 롤
		//            - AccessAnyMenu 권한을 포함하기 때문에 별도의 페이지 접근 권한을 포함하지 않음
		//            - 아래의 특수 권한을 포함:
		//                AccessAnyChildSite   모든 자식 사이트 접근 권한
		//                AccessAnyMenu        모든 메뉴 접근 권한
		//                ManageSiteJob        사이트 관리 권한
		//
		// ----------------------------------------------------------------
		// 
		// SignCast 롤:
		//
		//     Admin            단일 사이트 관리 목적의 사이트 관리자 롤(간략형)
		//     AssetAdmin       자산 관리자 롤
		//     SiteAdmin        단일 사이트 관리 목적의 사이트 관리자 롤
		//
		//
		//     상세 설명:
		//     
		//         Admin:
		//            - 단일 사이트 관리를 위한 사이트 관리자 롤
		//            - 사전에 지정된 페이지 접근 권한을 포함
		//            - AccessAnyMenu 권한이 포함되지 않기 때문에 접근 가능한 페이지 권한을 모두 포함
		//            - 아래의 특수 권한을 포함:
		//                ManagerAdmin         SignCast Manager 로그인/동기화 권한
		//                ManageSiteJob        사이트 관리 권한
		//                ViewRecentTask       STB 최근 작업 조회
		//
		//         SiteAdmin:
		//            - 단일 사이트 관리를 위한 사이트 관리자 롤
		//            - Foundation 기능 모듈을 제외한 거의 대부분의 페이지 접근 권한을 포함
		//            - AccessAnyMenu 권한이 포함되지 않기 때문에 접근 가능한 페이지 권한을 모두 포함
		//            - 아래의 특수 권한을 포함:
		//                ManagerAdmin         SignCast Manager 로그인/동기화 권한
		//                ManageSiteJob        사이트 관리 권한
		//                ViewRecentTask       STB 최근 작업 조회
		//
		//         AssetAdmin:
		//            - 자산 관리자 롤
		//            - 자산 관리 페이지 접근 권한을 포함
		//            - 아래의 특수 권한을 포함:
		//
		//         SystemAdmin:
		//            - 모든 사이트 관리 목적의 슈퍼 관리자 롤
		//            - AccessAnyMenu 권한을 포함하기 때문에 별도의 페이지 접근 권한을 포함하지 않음
		//            - 아래의 특수 권한을 포함:
		//                AccessAnyChildSite   모든 자식 사이트 접근 권한
		//                AccessAnyMenu        모든 메뉴 접근 권한
		//                ManagerAdmin         SignCast Manager 로그인/동기화 권한
		//                ManageSiteJob        사이트 관리 권한
		//                ViewRecentTask       STB 최근 작업 조회
		//
		// ----------------------------------------------------------------
		
		
		//
		// 사용자 홈을 초기 데이터로 생성 여부를 지정
		//
		//   USER_HOME_MODE:
		//        true : 사용자 홈 추가 모드
		//        false: 사용자 홈을 사용하지 않음
		//
		final boolean USER_HOME_MODE = true;
		

		

		Transaction tx = session.beginTransaction();

		
		//
		//
		// Step 1. 필수 자료
		//
		//
		
		
		//
		// 메뉴
		//

		Menu rvmConfigMenu = new Menu("RvmConfig", 100, "album", "010");
		Menu monMenu = new Menu("Monitoring", 200, "tachometer-alt", "020");
		Menu collectMenu = new Menu("Collecting", 300, "database", "030");
		Menu upFileMgmtMenu = new Menu("UpFileMgmt", 400, "cabinet-filing", "040");
		Menu currSiteMenu = new Menu("CurrSite", 600, "globe", "050");
		
		Menu siteUserConfigMenu = new Menu("SiteUserConfig", 800, "wrench", "001");
		
		
		// SiteAdmin 롤에 포함될 메뉴 추가
		ArrayList<String> siteAdminMenuGroups = new ArrayList<String>();
		siteAdminMenuGroups.add("RvmConfig");
		siteAdminMenuGroups.add("Monitoring");
		siteAdminMenuGroups.add("Collecting");
		siteAdminMenuGroups.add("UpFileMgmt");
		siteAdminMenuGroups.add("CurrSite");


		// 사용자 홈 페이지
		if (USER_HOME_MODE) {
			Menu userHomeMenu = new Menu("UserHome", "/eco/userhome", "home", "009", 10, true);
			siteAdminMenuGroups.add("UserHome");
			
			session.save(userHomeMenu);
		}


		
		
		Menu rvmMenu = new Menu("Rvm", "/eco/rvm", "album", "", 10, true);
		rvmMenu.setParent(rvmConfigMenu);

		Menu rvmGroupMenu = new Menu("RvmGroup", "/eco/rvmgroup", "folder", "", 20, true);
		rvmGroupMenu.setParent(rvmConfigMenu);

		Menu rvmGroupRvmMenu = new Menu("RvmGroupRvm", "/eco/rvmgrouprvm", "folder", "", 30, true);
		rvmGroupRvmMenu.setParent(rvmConfigMenu);

		
		
		Menu dashboardMenu = new Menu("MonDashboard", "/eco/mondashboard", "tachometer-alt", "", 10, true);
		dashboardMenu.setParent(monMenu);
		
		Menu gridViewMenu = new Menu("MonGridView", "/eco/mongridview", "table", "", 20, true);
		gridViewMenu.setParent(monMenu);
		
		Menu taskMenu = new Menu("MonTask", "/eco/montask", "list-timeline", "", 60, true);
		taskMenu.setParent(monMenu);

		Menu rtnSchdTaskMenu = new Menu("RtnSchdTask", "/eco/rtnschdtask", "calendar-lines-pen", "", 70, true);
		rtnSchdTaskMenu.setParent(monMenu);

		Menu eventReportMenu = new Menu("MonEventReport", "/eco/moneventreport", "magic", "", 90, true);
		eventReportMenu.setParent(monMenu);

		
		
		Menu dailyCollectMenu = new Menu("DailyCollect", "/eco/dailycollect", "notebook", "", 10, true);
		dailyCollectMenu.setParent(collectMenu);
		
		Menu recPeriodMenu = new Menu("RecPeriod", "/eco/recperiod", "chart-area", "", 20, true);
		recPeriodMenu.setParent(collectMenu);
		
		Menu rvmTrxMenu = new Menu("RvmTrx", "/eco/rvmtrx", "bottle-water", "", 60, true);
		rvmTrxMenu.setParent(collectMenu);

		Menu rvmTrxItemMenu = new Menu("RvmTrxItem", "/eco/rvmtrxitem", "bottle-water", "", 70, true);
		rvmTrxItemMenu.setParent(collectMenu);

		
		
		Menu upRawMenu = new Menu("UpRawTrx", "/eco/uprawtrx", "file-code", "", 10, true);
		upRawMenu.setParent(upFileMgmtMenu);
		
		Menu upDebugLogMenu = new Menu("UpDebugLog", "/eco/updebuglog", "file-alt", "", 20, true);
		upDebugLogMenu.setParent(upFileMgmtMenu);
		
		Menu updSetupFileMenu = new Menu("UpdSetupFile", "/eco/updsetupfile", "file-archive", "", 50, false);
		updSetupFileMenu.setParent(upFileMgmtMenu);

		
		
		Menu currSiteSettingMenu = new Menu("CurrSiteSetting", "/eco/currsitesetting", "globe", "", 10, true);
		currSiteSettingMenu.setParent(currSiteMenu);
		
		Menu currSiteTaskMenu = new Menu("CurrSiteTask", "/eco/currsitetask", "wrench", "", 20, true);
		currSiteTaskMenu.setParent(currSiteMenu);
		
		Menu currDispMenuMenu = new Menu("CurrDispMenu", "/eco/currdispmenu", "concierge-bell", "", 30, true);
		currDispMenuMenu.setParent(currSiteMenu);
		
		
		
		Menu siteMenu = new Menu("Site", "/fnd/site", "globe", "", 10);
		siteMenu.setParent(siteUserConfigMenu);
		
		Menu userMenu = new Menu("User", "/fnd/user", "user", "", 20);
		userMenu.setParent(siteUserConfigMenu);
		
		Menu siteUserMenu = new Menu("SiteUser", "/fnd/siteuser", "user-check", "", 30);
		siteUserMenu.setParent(siteUserConfigMenu);
		
		Menu loginLogMenu = new Menu("LoginLog", "/fnd/loginlog", "sign-in-alt", "", 50);
		loginLogMenu.setParent(siteUserConfigMenu);

		Menu childSiteMenu = new Menu("ChildSite", "/fnd/childsite", "child", "", 60);
		childSiteMenu.setParent(siteUserConfigMenu);
		
		Menu menuMenu = new Menu("Menu", "/fnd/menu", "concierge-bell", "", 70);
		menuMenu.setParent(siteUserConfigMenu);

		Menu privMenu = new Menu("Privilege", "/fnd/privilege", "cog", "", 80);
		privMenu.setParent(siteUserConfigMenu);

		Menu roleMenu = new Menu("Role", "/fnd/role", "cogs", "", 90);
		roleMenu.setParent(siteUserConfigMenu);

		Menu rolePrivMenu = new Menu("RolePrivilege", "/fnd/rolepriv", "award", "", 100);
		rolePrivMenu.setParent(siteUserConfigMenu);

		Menu userPrivMenu = new Menu("UserPrivilege", "/fnd/userpriv", "user-cog", "", 110);
		userPrivMenu.setParent(siteUserConfigMenu);
		
		

		// 1차 그룹 메뉴
		session.save(rvmConfigMenu);
		session.save(monMenu);
		session.save(collectMenu);
		session.save(upFileMgmtMenu);
		session.save(currSiteMenu);
		session.save(siteUserConfigMenu);
		
		
		// 기기 설정
		session.save(rvmMenu);
		session.save(rvmGroupMenu);
		session.save(rvmGroupRvmMenu);
		
		
		// 모니터링/원격제어
		session.save(dashboardMenu);
		session.save(gridViewMenu);
		session.save(taskMenu);
		session.save(rtnSchdTaskMenu);
		session.save(eventReportMenu);
		
		
		// 수거 자료
		session.save(recPeriodMenu);
		session.save(dailyCollectMenu);
		session.save(rvmTrxMenu);
		session.save(rvmTrxItemMenu);
		
		
		// 파일 관리
		session.save(upRawMenu);
		session.save(upDebugLogMenu);
		session.save(updSetupFileMenu);
				
		
		// 현재 사이트 관리
		session.save(currSiteSettingMenu);
		session.save(currSiteTaskMenu);
		session.save(currDispMenuMenu);
		

		// 일반 설정
		session.save(siteMenu);
		session.save(userMenu);
		session.save(siteUserMenu);
		session.save(loginLogMenu);
		session.save(childSiteMenu);
		session.save(menuMenu);
		session.save(privMenu);
		session.save(roleMenu);
		session.save(rolePrivMenu);
		session.save(userPrivMenu);

		
		
		// 메뉴 관련 권한 및 롤 자동 생성
		syncWithPrivAndRole(session);
		
		
		//
		// 권한
		//
		Privilege accessAnyMenuPriv = new Privilege("internal.AccessAnyMenu");
		session.save(accessAnyMenuPriv);
		
		Privilege accessAnyChildSitePriv = new Privilege("internal.AccessAnyChildSite");
		session.save(accessAnyChildSitePriv);
		
		Privilege manageSiteJobPriv = new Privilege("internal.ManageSiteJob");
		session.save(manageSiteJobPriv);
		
		Privilege noConCheckPriv = new Privilege("internal.NoConcurrentCheck");
		session.save(noConCheckPriv);
		
		Privilege noTimeOutPriv = new Privilege("internal.NoTimeOut");
		session.save(noTimeOutPriv);
		
		Privilege viewRecentTaskPriv = new Privilege("internal.ViewRecentTask");
		session.save(viewRecentTaskPriv);
		
 
		
		
		//
		// 롤
		//
		Role systemAdminRole = new Role("internal.SystemAdmin");
		session.save(systemAdminRole);
		

		
		
		//
		// 롤 권한
		//
		session.save(new RolePrivilege(systemAdminRole, accessAnyMenuPriv));
		session.save(new RolePrivilege(systemAdminRole, accessAnyChildSitePriv));
		session.save(new RolePrivilege(systemAdminRole, manageSiteJobPriv));
		session.save(new RolePrivilege(systemAdminRole, viewRecentTaskPriv));

		
		// 메뉴 그룹 접근 권한을 롤에 부여
		grantMenuGroupsToRole(session, siteAdminMenuGroups, "internal.SiteAdmin");
		

		
		
		//
		//
		// Step 2. Foundation 옵션 자료
		//
		//
		
		//
		// 사용자
		//
		session.save(new User("system", "관리자", "bbmc4312", new Date(), null));
		
    	// [WAB] --------------------------------------------------------------------------
    	/*
		//
		// 사이트
		//
		Site site = new Site("site", "초기사이트", new Date(), null, "127.0.0.1", 80, "127.0.0.1", 21, "site", "sitepwd");
		*/
    	// [WAB] --------------------------------------------------------------------------
    	// [SignCast] ext ----------------------------------------------------------- start
    	//
    	//
    	
		//
		// 사이트
		//
		Site site = new Site("r2cast", "초기사이트", new Date(), null, "127.0.0.1", 80, "127.0.0.1", 21, "r2cast", "r2castpwd");
    	
    	//
    	//
    	// [SignCast] ext ------------------------------------------------------------- end
		
		session.save(site);
		
		
		//
		// 사이트 사용자
		//
		User siteUser = (User) session.createCriteria(User.class)
				.add(Restrictions.eq("username", "system")).list().get(0);
		
		session.save(new SiteUser(site, siteUser));

		
		//
		// 사용자 권한
		//
		grantRoleToUser(session, "internal.SystemAdmin", "system");
		

		
		//
		//
		// Step 3. 추가 옵션 자료
		//
		//

		
    	// [SignCast] ext ----------------------------------------------------------- start
    	//
    	//
    	
		// 대한민국의 지역 자료를 생성
//		if (KR_REGION_CREATION_MODE) {
//			session.save(new Region("01150101", "강원도 강릉시", "92", "131", "KR"));
//			session.save(new Region("01820250", "강원도 고성군", "85", "146", "KR"));
//			session.save(new Region("01170101", "강원도 동해시", "97", "127", "KR"));
//			session.save(new Region("01230101", "강원도 삼척시", "97", "125", "KR"));
//			session.save(new Region("01210101", "강원도 속초시", "87", "141", "KR"));
//			session.save(new Region("01800250", "강원도 양구군", "78", "139", "KR"));
//			session.save(new Region("01830250", "강원도 양양군", "87", "138", "KR"));
//			session.save(new Region("01750250", "강원도 영월군", "85", "119", "KR"));
//			session.save(new Region("01130101", "강원도 원주시", "78", "121", "KR"));
//			session.save(new Region("01810250", "강원도 인제군", "81", "139", "KR"));
//			session.save(new Region("01770250", "강원도 정선군", "91", "122", "KR"));
//			session.save(new Region("01780250", "강원도 철원군", "67", "142", "KR"));
//			session.save(new Region("01110101", "강원도 춘천시", "73", "134", "KR"));
//			session.save(new Region("01190101", "강원도 태백시", "95", "119", "KR"));
//			session.save(new Region("01760250", "강원도 평창군", "85", "126", "KR"));
//			session.save(new Region("01720250", "강원도 홍천군", "77", "131", "KR"));
//			session.save(new Region("01790250", "강원도 화천군", "72", "139", "KR"));
//			session.save(new Region("01730250", "강원도 횡성군", "77", "125", "KR"));
//			session.save(new Region("02820250", "경기도 가평군", "67", "131", "KR"));
//			session.save(new Region("02281101", "경기도 고양시 덕양구", "57", "128", "KR"));
//			session.save(new Region("02285101", "경기도 고양시 일산동구", "57", "129", "KR"));
//			session.save(new Region("02287101", "경기도 고양시 일산서구", "56", "129", "KR"));
//			session.save(new Region("02290101", "경기도 과천시", "60", "124", "KR"));
//			session.save(new Region("02210101", "경기도 광명시", "58", "124", "KR"));
//			session.save(new Region("02610101", "경기도 광주시", "65", "123", "KR"));
//			session.save(new Region("02310101", "경기도 구리시", "62", "127", "KR"));
//			session.save(new Region("02410101", "경기도 군포시", "59", "122", "KR"));
//			session.save(new Region("02570101", "경기도 김포시", "54", "129", "KR"));
//			session.save(new Region("02360101", "경기도 남양주시", "63", "129", "KR"));
//			session.save(new Region("02250101", "경기도 동두천시", "61", "134", "KR"));
//			session.save(new Region("02197101", "경기도 부천시 소사구", "57", "125", "KR"));
//			session.save(new Region("02199101", "경기도 부천시 오정구", "57", "126", "KR"));
//			session.save(new Region("02195101", "경기도 부천시 원미구", "57", "125", "KR"));
//			session.save(new Region("02135101", "경기도 성남시 분당구", "62", "123", "KR"));
//			session.save(new Region("02131101", "경기도 성남시 수정구", "62", "124", "KR"));
//			session.save(new Region("02133101", "경기도 성남시 중원구", "63", "124", "KR"));
//			session.save(new Region("02113126", "경기도 수원시 권선구", "61", "120", "KR"));
//			session.save(new Region("02117101", "경기도 수원시 영통구", "61", "120", "KR"));
//			session.save(new Region("02111129", "경기도 수원시 장안구", "60", "121", "KR"));
//			session.save(new Region("02115120", "경기도 수원시 팔달구", "61", "120", "KR"));
//			session.save(new Region("02390101", "경기도 시흥시", "57", "123", "KR"));
//			session.save(new Region("02273101", "경기도 안산시 단원구", "57", "121", "KR"));
//			session.save(new Region("02271101", "경기도 안산시 상록구", "58", "121", "KR"));
//			session.save(new Region("02550101", "경기도 안성시", "65", "115", "KR"));
//			session.save(new Region("02173101", "경기도 안양시 동안구", "59", "123", "KR"));
//			session.save(new Region("02171101", "경기도 안양시 만안구", "59", "123", "KR"));
//			session.save(new Region("02630101", "경기도 양주시", "60", "132", "KR"));
//			session.save(new Region("02830250", "경기도 양평군", "70", "125", "KR"));
//			session.save(new Region("02670101", "경기도 여주시", "71", "121", "KR"));
//			session.save(new Region("02800250", "경기도 연천군", "61", "138", "KR"));
//			session.save(new Region("02370101", "경기도 오산시", "61", "118", "KR"));
//			session.save(new Region("02463101", "경기도 용인시 기흥구", "62", "120", "KR"));
//			session.save(new Region("02465101", "경기도 용인시 수지구", "62", "121", "KR"));
//			session.save(new Region("02461101", "경기도 용인시 처인구", "64", "119", "KR"));
//			session.save(new Region("02430101", "경기도 의왕시", "60", "122", "KR"));
//			session.save(new Region("02150101", "경기도 의정부시", "61", "130", "KR"));
//			session.save(new Region("02500101", "경기도 이천시", "69", "118", "KR"));
//			session.save(new Region("02480101", "경기도 파주시", "56", "133", "KR"));
//			session.save(new Region("02220101", "경기도 평택시", "60", "115", "KR"));
//			session.save(new Region("02650101", "경기도 포천시", "64", "136", "KR"));
//			session.save(new Region("02450101", "경기도 하남시", "64", "126", "KR"));
//			session.save(new Region("02590101", "경기도 화성시", "57", "119", "KR"));
//			session.save(new Region("03310101", "경상남도 거제시", "90", "69", "KR"));
//			session.save(new Region("03880250", "경상남도 거창군", "77", "86", "KR"));
//			session.save(new Region("03820250", "경상남도 고성군", "84", "72", "KR"));
//			session.save(new Region("03250101", "경상남도 김해시", "94", "77", "KR"));
//			session.save(new Region("03840250", "경상남도 남해군", "77", "68", "KR"));
//			session.save(new Region("03270101", "경상남도 밀양시", "91", "83", "KR"));
//			session.save(new Region("03240101", "경상남도 사천시", "80", "73", "KR"));
//			session.save(new Region("03860250", "경상남도 산청군", "76", "81", "KR"));
//			session.save(new Region("03330101", "경상남도 양산시", "97", "79", "KR"));
//			session.save(new Region("03720250", "경상남도 의령군", "83", "78", "KR"));
//			session.save(new Region("03170101", "경상남도 진주시", "81", "75", "KR"));
//			session.save(new Region("03740250", "경상남도 창녕군", "87", "83", "KR"));
//			session.save(new Region("03125101", "경상남도 창원시 마산합포구", "89", "76", "KR"));
//			session.save(new Region("03127101", "경상남도 창원시 마산회원구", "89", "76", "KR"));
//			session.save(new Region("03123101", "경상남도 창원시 성산구", "91", "76", "KR"));
//			session.save(new Region("03121101", "경상남도 창원시 의창구", "90", "77", "KR"));
//			session.save(new Region("03129101", "경상남도 창원시 진해구", "91", "75", "KR"));
//			session.save(new Region("03220101", "경상남도 통영시", "87", "68", "KR"));
//			session.save(new Region("03850250", "경상남도 하동군", "75", "74", "KR"));
//			session.save(new Region("03730250", "경상남도 함안군", "86", "77", "KR"));
//			session.save(new Region("03870250", "경상남도 함양군", "73", "83", "KR"));
//			session.save(new Region("03890250", "경상남도 합천군", "81", "84", "KR"));
//			session.save(new Region("04290101", "경상북도 경산시", "92", "90", "KR"));
//			session.save(new Region("04130101", "경상북도 경주시", "100", "91", "KR"));
//			session.save(new Region("04830250", "경상북도 고령군", "83", "87", "KR"));
//			session.save(new Region("04190101", "경상북도 구미시", "84", "97", "KR"));
//			session.save(new Region("04720250", "경상북도 군위군", "90", "98", "KR"));
//			session.save(new Region("04150101", "경상북도 김천시", "80", "97", "KR"));
//			session.save(new Region("04280101", "경상북도 문경시", "82", "106", "KR"));
//			session.save(new Region("04920250", "경상북도 봉화군", "92", "113", "KR"));
//			session.save(new Region("04250101", "경상북도 상주시", "81", "102", "KR"));
//			session.save(new Region("04840250", "경상북도 성주군", "84", "92", "KR"));
//			session.save(new Region("04170101", "경상북도 안동시", "91", "105", "KR"));
//			session.save(new Region("04770250", "경상북도 영덕군", "102", "103", "KR"));
//			session.save(new Region("04760250", "경상북도 영양군", "97", "108", "KR"));
//			session.save(new Region("04210101", "경상북도 영주시", "89", "110", "KR"));
//			session.save(new Region("04230101", "경상북도 영천시", "95", "94", "KR"));
//			session.save(new Region("04900250", "경상북도 예천군", "85", "108", "KR"));
//			session.save(new Region("04940250", "경상북도 울릉군", "127", "127", "KR"));
//			session.save(new Region("04930250", "경상북도 울진군", "101", "109", "KR"));
//			session.save(new Region("04730250", "경상북도 의성군", "88", "101", "KR"));
//			session.save(new Region("04820250", "경상북도 청도군", "91", "86", "KR"));
//			session.save(new Region("04750250", "경상북도 청송군", "96", "104", "KR"));
//			session.save(new Region("04850250", "경상북도 칠곡군", "85", "93", "KR"));
//			session.save(new Region("04111101", "경상북도 포항시남구", "102", "94", "KR"));
//			session.save(new Region("04113101", "경상북도 포항시북구", "102", "96", "KR"));
//			session.save(new Region("05200101", "광주시 광산구", "57", "74", "KR"));
//			session.save(new Region("05155101", "광주시 남구", "58", "73", "KR"));
//			session.save(new Region("05110101", "광주시 동구", "60", "74", "KR"));
//			session.save(new Region("05170101", "광주시 북구", "59", "75", "KR"));
//			session.save(new Region("05140104", "광주시 서구", "58", "74", "KR"));
//			session.save(new Region("06200101", "대구시 남구", "89", "90", "KR"));
//			session.save(new Region("06290101", "대구시 달서구", "88", "90", "KR"));
//			session.save(new Region("06710250", "대구시 달성군", "87", "89", "KR"));
//			session.save(new Region("06140101", "대구시 동구", "90", "91", "KR"));
//			session.save(new Region("06230101", "대구시 북구", "89", "92", "KR"));
//			session.save(new Region("06170101", "대구시 서구", "88", "90", "KR"));
//			session.save(new Region("06260101", "대구시 수성구", "90", "90", "KR"));
//			session.save(new Region("06110101", "대구시 중구", "89", "90", "KR"));
//			session.save(new Region("07230101", "대전시 대덕구", "68", "100", "KR"));
//			session.save(new Region("07110101", "대전시 동구", "68", "100", "KR"));
//			session.save(new Region("07170101", "대전시 서구", "67", "100", "KR"));
//			session.save(new Region("07200101", "대전시 유성구", "66", "101", "KR"));
//			session.save(new Region("07140101", "대전시 중구", "68", "100", "KR"));
//			session.save(new Region("08440101", "부산시 강서구", "96", "76", "KR"));
//			session.save(new Region("08410101", "부산시 금정구", "96", "77", "KR"));
//			session.save(new Region("08710250", "부산시 기장군", "100", "77", "KR"));
//			session.save(new Region("08290106", "부산시 남구", "98", "75", "KR"));
//			session.save(new Region("08170101", "부산시 동구", "97", "75", "KR"));
//			session.save(new Region("08260101", "부산시 동래구", "98", "76", "KR"));
//			session.save(new Region("08230101", "부산시 부산진구", "97", "75", "KR"));
//			session.save(new Region("08320101", "부산시 북구", "97", "76", "KR"));
//			session.save(new Region("08530101", "부산시 사상구", "97", "75", "KR"));
//			session.save(new Region("08380101", "부산시 사하구", "96", "74", "KR"));
//			session.save(new Region("08140101", "부산시 서구", "97", "74", "KR"));
//			session.save(new Region("08500101", "부산시 수영구", "99", "76", "KR"));
//			session.save(new Region("08470101", "부산시 연제구", "98", "76", "KR"));
//			session.save(new Region("08200101", "부산시 영도구", "98", "74", "KR"));
//			session.save(new Region("08110101", "부산시 중구", "97", "74", "KR"));
//			session.save(new Region("08350101", "부산시 해운대구", "99", "75", "KR"));
//			session.save(new Region("09680101", "서울시 강남구", "61", "125", "KR"));
//			session.save(new Region("09740101", "서울시 강동구", "63", "126", "KR"));
//			session.save(new Region("09305101", "서울시 강북구", "60", "128", "KR"));
//			session.save(new Region("09500101", "서울시 강서구", "58", "126", "KR"));
//			session.save(new Region("09620101", "서울시 관악구", "59", "125", "KR"));
//			session.save(new Region("09215101", "서울시 광진구", "62", "126", "KR"));
//			session.save(new Region("09530101", "서울시 구로구", "58", "125", "KR"));
//			session.save(new Region("09545101", "서울시 금천구", "59", "124", "KR"));
//			session.save(new Region("09350102", "서울시 노원구", "62", "129", "KR"));
//			session.save(new Region("09320105", "서울시 도봉구", "61", "129", "KR"));
//			session.save(new Region("09230101", "서울시 동대문구", "61", "127", "KR"));
//			session.save(new Region("09590101", "서울시 동작구", "59", "125", "KR"));
//			session.save(new Region("09440101", "서울시 마포구", "59", "126", "KR"));
//			session.save(new Region("09410101", "서울시 서대문구", "59", "127", "KR"));
//			session.save(new Region("09650101", "서울시 서초구", "61", "125", "KR"));
//			session.save(new Region("09200101", "서울시 성동구", "61", "126", "KR"));
//			session.save(new Region("09290101", "서울시 성북구", "60", "127", "KR"));
//			session.save(new Region("09710101", "서울시 송파구", "62", "125", "KR"));
//			session.save(new Region("09470101", "서울시 양천구", "58", "126", "KR"));
//			session.save(new Region("09560101", "서울시 영등포구", "59", "126", "KR"));
//			session.save(new Region("09170101", "서울시 용산구", "60", "126", "KR"));
//			session.save(new Region("09380101", "서울시 은평구", "59", "128", "KR"));
//			session.save(new Region("09110101", "서울시 종로구", "60", "127", "KR"));
//			session.save(new Region("09140101", "서울시 중구", "60", "127", "KR"));
//			session.save(new Region("09260101", "서울시 중랑구", "62", "127", "KR"));
//			session.save(new Region("17110101", "세종시", "65", "104", "KR"));
//			session.save(new Region("10140101", "울산시 남구", "102", "83", "KR"));
//			session.save(new Region("10170101", "울산시 동구", "104", "83", "KR"));
//			session.save(new Region("10200101", "울산시 북구", "103", "85", "KR"));
//			session.save(new Region("10710250", "울산시 울주군", "99", "83", "KR"));
//			session.save(new Region("10110101", "울산시 중구", "102", "85", "KR"));
//			session.save(new Region("11710250", "인천시 강화군", "50", "129", "KR"));
//			session.save(new Region("11245101", "인천시 계양구", "56", "126", "KR"));
//			session.save(new Region("11200101", "인천시 남동구", "55", "124", "KR"));
//			session.save(new Region("11140101", "인천시 동구", "54", "125", "KR"));
//			session.save(new Region("11177106", "인천시 미추홀구", "55", "124", "KR"));
//			session.save(new Region("11237101", "인천시 부평구", "56", "126", "KR"));
//			session.save(new Region("11260101", "인천시 서구", "55", "127", "KR"));
//			session.save(new Region("11185101", "인천시 연수구", "54", "123", "KR"));
//			session.save(new Region("11720310", "인천시 옹진군", "51", "120", "KR"));
//			session.save(new Region("11110101", "인천시 중구", "53", "124", "KR"));
//			session.save(new Region("12810250", "전라남도 강진군", "58", "63", "KR"));
//			session.save(new Region("12770250", "전라남도 고흥군", "66", "63", "KR"));
//			session.save(new Region("12720250", "전라남도 곡성군", "66", "77", "KR"));
//			session.save(new Region("12230101", "전라남도 광양시", "73", "71", "KR"));
//			session.save(new Region("12730250", "전라남도 구례군", "70", "75", "KR"));
//			session.save(new Region("12170101", "전라남도 나주시", "57", "71", "KR"));
//			session.save(new Region("12710250", "전라남도 담양군", "60", "78", "KR"));
//			session.save(new Region("12110101", "전라남도 목포시", "50", "67", "KR"));
//			session.save(new Region("12840250", "전라남도 무안군", "52", "71", "KR"));
//			session.save(new Region("12780250", "전라남도 보성군", "62", "66", "KR"));
//			session.save(new Region("12150101", "전라남도 순천시", "69", "70", "KR"));
//			session.save(new Region("12910250", "전라남도 신안군", "49", "68", "KR"));
//			session.save(new Region("12130101", "전라남도 여수시", "72", "66", "KR"));
//			session.save(new Region("12870250", "전라남도 영광군", "53", "76", "KR"));
//			session.save(new Region("12830250", "전라남도 영암군", "56", "67", "KR"));
//			session.save(new Region("12890250", "전라남도 완도군", "58", "58", "KR"));
//			session.save(new Region("12880250", "전라남도 장성군", "57", "77", "KR"));
//			session.save(new Region("12800250", "전라남도 장흥군", "59", "65", "KR"));
//			session.save(new Region("12900250", "전라남도 진도군", "48", "60", "KR"));
//			session.save(new Region("12860250", "전라남도 함평군", "53", "72", "KR"));
//			session.save(new Region("12820250", "전라남도 해남군", "55", "61", "KR"));
//			session.save(new Region("12790250", "전라남도 화순군", "62", "71", "KR"));
//			session.save(new Region("13790250", "전라북도 고창군", "55", "80", "KR"));
//			session.save(new Region("13130101", "전라북도 군산시", "56", "91", "KR"));
//			session.save(new Region("13210101", "전라북도 김제시", "61", "86", "KR"));
//			session.save(new Region("13190101", "전라북도 남원시", "69", "80", "KR"));
//			session.save(new Region("13730250", "전라북도 무주군", "72", "93", "KR"));
//			session.save(new Region("13800250", "전라북도 부안군", "56", "87", "KR"));
//			session.save(new Region("13770250", "전라북도 순창군", "63", "80", "KR"));
//			session.save(new Region("13710250", "전라북도 완주군", "64", "91", "KR"));
//			session.save(new Region("13140101", "전라북도 익산시", "59", "91", "KR"));
//			session.save(new Region("13750250", "전라북도 임실군", "66", "84", "KR"));
//			session.save(new Region("13740250", "전라북도 장수군", "70", "85", "KR"));
//			session.save(new Region("13113102", "전라북도 전주시 덕진구", "63", "90", "KR"));
//			session.save(new Region("13111101", "전라북도 전주시 완산구", "63", "89", "KR"));
//			session.save(new Region("13180101", "전라북도 정읍시", "58", "84", "KR"));
//			session.save(new Region("13720250", "전라북도 진안군", "69", "89", "KR"));
//			session.save(new Region("14130101", "제주도 서귀포시", "54", "33", "KR"));
//			session.save(new Region("14110101", "제주도 제주시", "53", "37", "KR"));
//			session.save(new Region("15250101", "충청남도 계룡시", "65", "99", "KR"));
//			session.save(new Region("15150101", "충청남도 공주시", "63", "100", "KR"));
//			session.save(new Region("15710250", "충청남도 금산군", "69", "96", "KR"));
//			session.save(new Region("15230101", "충청남도 논산시", "62", "96", "KR"));
//			session.save(new Region("15270101", "충청남도 당진시", "54", "112", "KR"));
//			session.save(new Region("15180101", "충청남도 보령시", "54", "100", "KR"));
//			session.save(new Region("15760250", "충청남도 부여군", "59", "99", "KR"));
//			session.save(new Region("15210101", "충청남도 서산시", "50", "109", "KR"));
//			session.save(new Region("15770250", "충청남도 서천군", "55", "95", "KR"));
//			session.save(new Region("15200101", "충청남도 아산시", "60", "110", "KR"));
//			session.save(new Region("15810250", "충청남도 예산군", "57", "108", "KR"));
//			session.save(new Region("15131101", "충청남도 천안시 동남구", "63", "110", "KR"));
//			session.save(new Region("15133101", "충청남도 천안시 서북구", "63", "112", "KR"));
//			session.save(new Region("15790250", "충청남도 청양군", "58", "102", "KR"));
//			session.save(new Region("15825250", "충청남도 태안군", "48", "109", "KR"));
//			session.save(new Region("15800250", "충청남도 홍성군", "54", "105", "KR"));
//			session.save(new Region("16760250", "충청북도 괴산군", "74", "110", "KR"));
//			session.save(new Region("16800250", "충청북도 단양군", "84", "115", "KR"));
//			session.save(new Region("16720250", "충청북도 보은군", "73", "103", "KR"));
//			session.save(new Region("16740250", "충청북도 영동군", "74", "96", "KR"));
//			session.save(new Region("16730250", "충청북도 옥천군", "71", "99", "KR"));
//			session.save(new Region("16770250", "충청북도 음성군", "71", "114", "KR"));
//			session.save(new Region("16150101", "충청북도 제천시", "80", "118", "KR"));
//			session.save(new Region("16745250", "충청북도 증평군", "71", "110", "KR"));
//			session.save(new Region("16750250", "충청북도 진천군", "68", "111", "KR"));
//			session.save(new Region("16111101", "충청북도 청주시 상당구", "69", "107", "KR"));
//			session.save(new Region("16112101", "충청북도 청주시 서원구", "69", "106", "KR"));
//			session.save(new Region("16114101", "충청북도 청주시 청원구", "69", "107", "KR"));
//			session.save(new Region("16113250", "충청북도 청주시 흥덕구", "68", "107", "KR"));
//			session.save(new Region("16130101", "충청북도 충주시", "77", "114", "KR"));
//		}
    	
    	//
    	//
    	// [SignCast] ext ------------------------------------------------------------- end
		
		System.out.println("--------------------------------------------------");
		System.out.println("creation finished!!");

		tx.commit();
		session.close();
		
		HibernateUtil.shutdown();
	}
	
	private static Menu getMenu(org.hibernate.Session session, String ukid) {
		@SuppressWarnings("unchecked")
		List<Menu> list = session.createCriteria(Menu.class)
				.add(Restrictions.eq("ukid", ukid)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}
	
	private static Role getRole(org.hibernate.Session session, String ukid) {
		@SuppressWarnings("unchecked")
		List<Role> list = session.createCriteria(Role.class)
				.add(Restrictions.eq("ukid", ukid)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	private static Privilege getPrivilege(org.hibernate.Session session, String ukid) {
		@SuppressWarnings("unchecked")
		List<Privilege> list = session.createCriteria(Privilege.class)
				.add(Restrictions.eq("ukid", ukid)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@SuppressWarnings("unchecked")
	private static List<Menu> getMenuListById(org.hibernate.Session session, Integer id) {
		Criterion rest;
		
		if (id == null) {
			rest = Restrictions.isNull("parent");
		} else {
			rest = Restrictions.eq("parent.id", id);
		}
		
		return session.createCriteria(Menu.class).add(rest).list();
	}
	
	@SuppressWarnings("unchecked")
	private static void syncWithPrivAndRole(org.hibernate.Session session) {
		List<Menu> menuList = session.createCriteria(Menu.class).list();
		
		for (Menu menu : menuList) {
			String ukidKey =  "menu." + menu.getUkid();
			if (getPrivilege(session, ukidKey) == null)
			{
				session.saveOrUpdate(new Privilege(ukidKey, null));
			}

			if (! getMenuListById(session, menu.getId()).isEmpty() &&
					getRole(session, ukidKey) == null) {
				session.saveOrUpdate(new Role(ukidKey, null));
			}
		}
		
		List<Privilege> privList = session.createCriteria(Privilege.class).list();
		List<Role> roleList = session.createCriteria(Role.class).list();

		ArrayList<Privilege> delPrivList = new ArrayList<Privilege>();
		for (Privilege priv : privList) {
			if (priv.getUkid().startsWith("menu.")) {
				if (getMenu(session, priv.getUkid().substring(5)) == null) {
					delPrivList.add(priv);
				}
			}
		}
        for (Privilege privilege : delPrivList) {
            session.delete(session.load(Privilege.class, privilege.getId()));
        }

		ArrayList<Role> delRoleList = new ArrayList<Role>();
		for (Role role : roleList) {
			if (role.getUkid().startsWith("menu.")) {
				Menu menu = getMenu(session, role.getUkid().substring(5));
				if (menu == null || getMenuListById(session, menu.getId()).isEmpty()) {
					delRoleList.add(role);
				}
			}
		}
        for (Role role : delRoleList) {
            session.delete(session.load(Role.class, role.getId()));
        }
	}
	
	@SuppressWarnings("unchecked")
	private static List<Privilege> getPrivilegeListByRoleId(org.hibernate.Session session, int id) {
		ArrayList<Privilege> privList = new ArrayList<Privilege>();
		
		List<RolePrivilege> list = session.createCriteria(RolePrivilege.class)
				.add(Restrictions.eq("role.id", id)).list();
		for (RolePrivilege rolePriv : list) {
			privList.add(rolePriv.getPrivilege());
		}
		
		return privList;
	}

	@SuppressWarnings("unchecked")
	private static List<UserPrivilege> getUserPrivilegeListByUserId(org.hibernate.Session session, 
			int id) {
		return session.createCriteria(UserPrivilege.class).add(Restrictions.eq("user.id", id))
				.list();
	}
	
	private static Menu getMenuByUkid(org.hibernate.Session session, String ukid) {
		@SuppressWarnings("unchecked")
		List<Menu> list = session.createCriteria(Menu.class)
				.add(Restrictions.eq("ukid", ukid)).list();
		
		return (list.isEmpty() ? null : list.get(0));
	}
	
	private static void grantRoleToUser(org.hibernate.Session session, String roleStr, String userStr) {
		User user = (User) session.createCriteria(User.class)
				.add(Restrictions.eq("username", userStr)).list().get(0);
		Role role = getRole(session, roleStr);
		
		if (user != null && role != null) {
			List<UserPrivilege> userPrivList = getUserPrivilegeListByUserId(session, user.getId());
			ArrayList<Integer> privIds = new ArrayList<Integer>();
			for (UserPrivilege userPriv : userPrivList) {
				privIds.add(userPriv.getPrivilege().getId());
			}
			
			if (roleStr.startsWith("menu.")) {
				Menu parentMenu = getMenuByUkid(session, roleStr.substring(5));
				if (parentMenu != null) {
					ArrayList<String> menuUkids = new ArrayList<String>();
					
					menuUkids.add(parentMenu.getUkid());
					List<Menu> menuList = getMenuListById(session, parentMenu.getId());
					for (Menu menu : menuList) {
						menuUkids.add(menu.getUkid());
					}
					
					for (String menuUkid : menuUkids) {
						Privilege priv = getPrivilege(session, "menu." + menuUkid);
						if (priv != null && !privIds.contains(priv.getId())) {
							session.save(new UserPrivilege(user, priv));
							privIds.add(priv.getId());
						}
					}
				}
			} else {
				List<Privilege> privList = getPrivilegeListByRoleId(session, role.getId());
				for (Privilege priv : privList) {
					if (!privIds.contains(priv.getId())) {
						session.save(new UserPrivilege(user, priv));
						privIds.add(priv.getId());
					}
				}
			}
		}
	}
	
	private static void grantMenuGroupsToRole(org.hibernate.Session session,
			List<String> parentMenuUkids, String roleStr) {
		Role role = getRole(session, roleStr);
		
		if (role != null) {
			ArrayList<String> menuUkids = new ArrayList<String>();
			for (String menuUkid : parentMenuUkids) {
				Menu parentMenu = getMenuByUkid(session, menuUkid);
				if (parentMenu != null) {
					
					menuUkids.add(parentMenu.getUkid());
					List<Menu> menuList = getMenuListById(session, parentMenu.getId());
					for (Menu menu : menuList) {
						if (!menuUkids.contains(menu.getUkid())) {
							menuUkids.add(menu.getUkid());
						}
					}
				}
			}
			
			List<Privilege> privList = getPrivilegeListByRoleId(session, role.getId());
			ArrayList<Integer> privIds = new ArrayList<Integer>();
			for (Privilege priv : privList) {
				if (!privIds.contains(priv.getId())) {
					privIds.add(priv.getId());
				}
			}

			for (String ukid : menuUkids) {
				Privilege priv = getPrivilege(session, "menu." + ukid);
				if (priv != null && !privIds.contains(priv.getId())) {
					session.save(new RolePrivilege(role, priv));
					privIds.add(priv.getId());
				}
			}
		}
	}
	
	private static void grantMenusToRole(org.hibernate.Session session,
			List<String> menuUkids, String roleStr) {
		Role role = getRole(session, roleStr);
		
		if (role != null) {
			List<Privilege> privList = getPrivilegeListByRoleId(session, role.getId());
			ArrayList<Integer> privIds = new ArrayList<Integer>();
			for (Privilege priv : privList) {
				if (!privIds.contains(priv.getId())) {
					privIds.add(priv.getId());
				}
			}

			for (String ukid : menuUkids) {
				Privilege priv = getPrivilege(session, "menu." + ukid);
				if (priv != null && !privIds.contains(priv.getId())) {
					session.save(new RolePrivilege(role, priv));
					privIds.add(priv.getId());
				}
			}
		}
	}
}
