package utils;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

public class MaxMikuUtils {
	public static String getRemoteIP(HttpServletRequest req) {
		/**
		 * 用于获取用户IP地址,稍后会修改成取真实地址
		 */
		
		return req.getRemoteAddr();
	}
	
	 public static String getRandomString(int length){
	     String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	     Random random=new Random();
	     StringBuffer sb=new StringBuffer();
	     for(int i=0;i<length;i++){
	       int number=random.nextInt(62);
	       sb.append(str.charAt(number));
	     }
	     return sb.toString();
	 }
	public static int checkPassStrong(String pass) {
		/**
		 * 检查密码是否达到安全要求
		 * @author lexuan
		 * @return int 1弱 2中  3强
		 */
		String regexZ = "\\d*";
		String regexS = "[a-zA-Z]+";
		String regexT = "\\W+$";
		String regexZT = "\\D*";
		String regexST = "[\\d\\W]*";
		String regexZS = "\\w*";
		String regexZST = "[\\w\\W]*";
		 
		if (pass.matches(regexZ)) {
			return 1;
		}
		if (pass.matches(regexS)) {
		    return 1;
		}
		if (pass.matches(regexT)) {
		    return 1;
		}
		if (pass.matches(regexZT)) {
		    return 2;
		}
		if (pass.matches(regexST)) {
		    return 2;
		}
		if (pass.matches(regexZS)) {
		    return 2;
		}
		if (pass.matches(regexZST)) {
		    return 3;
		}
		return 0;
	}
	
	
}
