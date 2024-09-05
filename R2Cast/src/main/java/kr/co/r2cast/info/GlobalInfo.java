package kr.co.r2cast.info;

import java.security.KeyPair;
import java.util.Date;
import java.util.HashMap;

public class GlobalInfo {
	
	public static String AppId = "r2cast";
	public static KeyPair RSAKeyPair = null;
	public static String RSAKeyMod = "";
	
	public static Date ServerStartDt = new Date();
	
	// 현재 사이트 속성을 과도하게 호출하는 것을 방지하기 위해 메모리 이용
	// 둘 이상의 서버에서 이용할 경우 불일치 문제가 발생할 수 있으므로 주의
	public static HashMap<String, String> SiteOptionMap = new HashMap<String, String>();
}
