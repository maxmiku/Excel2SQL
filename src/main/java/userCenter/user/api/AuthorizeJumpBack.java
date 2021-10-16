//package userCenter.user.api;
//
//import filter.SqlInjectFilter;
//import org.json.JSONObject;
//import utils.MD5Util;
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
//@WebServlet("/api/authBack")
//public class AuthorizeJumpBack extends HttpServlet{
//
//	@Override
//	/**
//	 * 登录成功返回
//	 * 成功 重定向    不成功显示错误信息
//	 */
//	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		req.setCharacterEncoding("UTF-8");
//    	resp.setContentType("text/html;charset=UTF-8");
//    	HttpSession session = req.getSession();
//    	PrintWriter out = resp.getWriter();
//		JSONObject userInfo = new JSONObject();
//		String accessToken = (String) session.getAttribute("access_token");
//
//		if(accessToken==null || SqlInjectFilter.sqlValidate(accessToken)) {
//			System.err.println("AuthorizeJumpBack session找不到accessToken");
//			out.println("非法请求,请返回登录前一页重试");
//			return;
//		}
//
//		userInfo.put("uid",String.valueOf(session.getAttribute("uid")));
//		userInfo.put("ulevel",String.valueOf(session.getAttribute("ulevel")));
//		userInfo.put("ulogin",String.valueOf(session.getAttribute("ulogin")));
//		userInfo.put("uname",String.valueOf(session.getAttribute("uname")));
//
//		SqlUtil sql = new SqlUtil();
//		if(!sql.sqlinit()) {
//			System.err.println("AuthorizeJumpBack 数据库连接失败");
//			out.println("服务器开小差了,请返回上一页重试");
//			return;
//		}
//
//
//		String redirectUrl = null;
//		String backtoken = null;
//
//		try {
//			sql.updateData("access_token=\""+accessToken+"\"", "app_login", "uinfo='"+userInfo+"'");
//
//			ResultSet rs = sql.pullData("access_token=\""+accessToken+"\"", "app_login");
//			rs.next();
//			redirectUrl = rs.getString("redirect_url");
//			backtoken = MD5Util.md5(accessToken+MD5Util.md5(rs.getString("token")+rs.getString("appid")));
////			System.out.printf("==center===\n%s\n%s\n%s\n=========",accessToken,rs.getString("token"),rs.getString("appid"));
//		} catch (SQLException e) {
//			System.err.println("AuthorizeJumpBack 数据库操作条目失败 access:"+accessToken);
//			out.println("服务器开小差了,请返回上一页重试");
//			e.printStackTrace();
//			sql.closeDB();
//			return;
//		}
//
//		System.out.println(redirectUrl+"?status=0&token="+backtoken);
//
//		resp.sendRedirect(redirectUrl+"?status=0&token="+backtoken);
//
//		Authorize.clearTimeoutData();
//	}
//}
