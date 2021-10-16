//package userCenter.user.api;
//
//import filter.SqlInjectFilter;
//import org.json.JSONObject;
//import utils.MaxMikuUtils;
//import utils.SqlUtil;
//
//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.sql.ResultSet;
//
//@WebServlet("/api/getUserInfo")
//public class GetUserInformation extends HttpServlet{
//	@Override
//	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		resp.sendError(404);
//	}
//	@Override
//	/**
//	 * 获取用户信息接口
//	 * status -3sql连接失败 -2服务器内部错误 -1 不为本机  1SQL注入 2id密码不对应  3没有指定的数数据
//	 * 返回用户信息的json retData中 "uinfo":'{"uid":"10001","ulevel":"0","ulogin":1,"uname":"admin"}'
//	 */
//	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		req.setCharacterEncoding("UTF-8");
//    	resp.setContentType("text/html;charset=UTF-8");
//		PrintWriter out = resp.getWriter();
//		JSONObject retj = new JSONObject("{\"status\":-1,\"retData\":{}}");
//		if(!MaxMikuUtils.getRemoteIP(req).equals("127.0.0.1")) {
////			out.println(retj);
//			resp.sendError(404);
//			System.err.println("[Warning] userCenter GetUserInformation 被非法请求 '"+MaxMikuUtils.getRemoteIP(req)+"'");
//			return;
//		}
//		retj.put("status", 0);
//		JSONObject retData = new JSONObject();
//
//		SqlUtil sql = new SqlUtil();
//		try {
//			String inString = utils.Request2StringUtil.getRequestPostStr(req);
//			JSONObject inj = new JSONObject(inString);
//			if(SqlInjectFilter.sqlValidate(inj.getString("appid"))) {
//				retj.put("status", 1);
//				out.println(retj);
//				System.err.println("[Warning] userCenter GetUserInformation 被非法请求 '"+MaxMikuUtils.getRemoteIP(req)+"'");
//				return;
//			}
//
//
//			retj.put("status", 0);
//
//
//			if(!sql.sqlinit()) {
//				retj.put("status", -3);
//				out.println(retj);
//				System.err.println("[Warning] userCenter GetUserInformation 数据库链接失败");
//				return;
//			}
//
//			ResultSet rs = sql.pullData("appid=\""+inj.getString("appid")+"\"", "app_info");
//			rs.next();
//			if(!rs.getString("appid").equals(inj.getString("appid")) || !rs.getString("appsecret").equals(inj.getString("appsecret"))){
//				//验证不通过
//				retj.put("status", 2);
//				out.println(retj);
//				System.err.println("[Warning] userCenter GetUserInformation 验证失败 '"+MaxMikuUtils.getRemoteIP(req)+"'");
//				rs.close();
//				sql.closeDB();
//				return;
//			}
//			String redirectUrl = rs.getString("redirect_url");
//			rs.close();
//
//
//			rs = sql.pullData("access_token=\""+inj.getString("access_token")+"\" AND token=\""+inj.getString("token")+"\" AND appid=\""+inj.getString("appid")+"\"", "app_login");
//			if(!rs.next()) {
//				retj.put("status", 3);
//				out.println(retj);
//				System.err.println("[Warning] userCenter GetUserInformation 没有找到指定的accesstoken  '"+inj.getString("access_token")+"'");
//				rs.close();
//				sql.closeDB();
//				return;
//			}
//
//			String uinfo = rs.getString("uinfo");
//			rs.close();
//
//			//拉取后删除
//			sql.deleteData("access_token=\""+inj.getString("access_token")+"\" AND token=\""+inj.getString("token")+"\" AND appid=\""+inj.getString("appid")+"\"", "app_login");
//
//			retData.put("uinfo", uinfo);
//
//			sql.closeDB();
//
//			Authorize.clearTimeoutData();
//		} catch (Exception e) {
//			retj.put("status", -2);
//			out.println(retj);
//			sql.closeDB();
//			System.err.println("[Warning] userCenter GetUserInformation 内部错误");
//			e.printStackTrace();
//			return;
//		}
//		retj.put("retData",retData);
//
//		out.println(retj);
//	}
//}
