//package userCenter.user.api;
//
//import filter.SqlInjectFilter;
//import utils.MD5Util;
//import utils.MaxMikuUtils;
//import utils.SqlUtil;
//
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//@WebServlet("/api/authorizeStart")
//public class AuthorizeStart extends HttpServlet{
//	@Override
//	/**
//	 * 验证开始接口
//	 *
//	 */
//	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		req.setCharacterEncoding("UTF-8");
//    	resp.setContentType("text/html;charset=UTF-8");
//    	PrintWriter out = resp.getWriter();
//    	HttpSession session = req.getSession();
//
//    	String accessToken = req.getParameter("accesstoken");
//    	String intoken = req.getParameter("token");
//
//    	if(SqlInjectFilter.sqlValidate(accessToken)) {
//    		System.err.println("authorizeStart 发现sql注入");
//    		out.println("服务器发生严重错误 0");
//    		return;
//    	}
//
//    	String clientIp = MaxMikuUtils.getRemoteIP(req)+"+"+req.getRemotePort();
//
//    	SqlUtil sql = new SqlUtil();
//    	if(!sql.sqlinit()) {
//    		System.err.println("authorizeStart 初始化数据库连接失败");
//    		out.println("服务器开小差了,请返回页面重新操作 0");
//    		return;
//    	}
//
//    	ResultSet rs = null;
//    	try {
//			rs = sql.pullData("access_token=\""+accessToken+"\"", "app_login");
//
//			rs.next();
//
//			String token = MD5Util.md5(rs.getString("appid")+rs.getString("token")+accessToken);
//
////			if(!clientIp.equals(rs.getString("ip"))) {
////				System.err.println("authorizeStart 访问ip不相同");
////	    		out.println("服务器发生错误,访问过期,请返回上页重试 1");
////	    		rs.close();
////	    		sql.closeDB();
////	    		return;
////			}
//
//			if(!token.equals(intoken)) {
//				System.err.println("authorizeStart token验证失败");
//	    		out.println("服务器发生错误,访问过期,请返回上页重试 0");
//	    		rs.close();
//	    		sql.closeDB();
//	    		return;
//			}
//
//			//设置回调地址
//			session.setAttribute("skipUrl", "/userCenter/api/authBack");
//
//			session.setAttribute("access_token", accessToken);
//
//			sql.closeDB();
//
//		} catch (SQLException e) {
//			System.err.println("authorizeStart 获取指定accesstoken失败");
//    		out.println("服务器开小差了,请返回页面重新操作 1");
//			e.printStackTrace();
//			try {
//				if(rs!=null)rs.close();
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//			}
//			sql.closeDB();
//			return;
//		}
//
//
//
//    	resp.sendRedirect("/userCenter/login.html");
//
//    	Authorize.clearTimeoutData();
//
//	}
//}