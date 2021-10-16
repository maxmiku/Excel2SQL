package userCenter.tools.security;

import utils.MaxMikuUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 禁止ip访问工具类,用于阻挡大规模的请求攻击
 * @author lexuan
 *
 */
public class BanTools {
	public static void increaseBanIndex(HttpServletRequest req,double d) {
		//可以通过查session的ulogin确定是否已登录,如果已登录超过指定次数之后将被登出
		System.out.println("BanIndex increase "+MaxMikuUtils.getRemoteIP(req)+" add "+d);
	}
	
	public static void cleanBanIndex(HttpServletRequest req) {
		System.out.println("BanIndex clean "+MaxMikuUtils.getRemoteIP(req));
	}
}
