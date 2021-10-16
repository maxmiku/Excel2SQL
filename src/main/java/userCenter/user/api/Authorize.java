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
//import java.sql.SQLException;
//
//@WebServlet("/api/authorize")
//public class Authorize extends HttpServlet{
//
//	@Override
//	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		resp.sendError(404);
//	}
//	@Override
//	/**
//	 * 提出验证请求并获取本次登陆的码
//	 * 传入json
//	 * {"appid":"123123123213","appsecret":"dfsdfsdfdfsf","token":"随机16位"}
//	 *
//	 * status -3sql连接失败 -2服务器内部错误 -1 不为本机  1SQL注入 2id密码不对应  3写入applogin表失败
//	 */
//	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		req.setCharacterEncoding("UTF-8");
//    	resp.setContentType("text/html;charset=UTF-8");
//		PrintWriter out = resp.getWriter();
//		JSONObject retj = new JSONObject("{\"status\":-1,\"retData\":{}}");
//		JSONObject retData = new JSONObject();
//		if(!MaxMikuUtils.getRemoteIP(req).equals("127.0.0.1")) {
////			resp.sendRedirect("/");
////			out.println(retj);
//			resp.sendError(404);
//			System.err.println("[Warning] userCenter getUserInfo 被非法请求 -1 '"+MaxMikuUtils.getRemoteIP(req)+"'");
//			return;
//		}
//		SqlUtil sql = new SqlUtil();
//		try {
//			String inString = utils.Request2StringUtil.getRequestPostStr(req);
//			JSONObject inj = new JSONObject(inString);
//			if(SqlInjectFilter.sqlValidate(inj.getString("appid"))) {
//				retj.put("status", 1);
//				out.println(retj);
//				System.err.println("[Warning] userCenter getUserInfo 被非法请求 '"+MaxMikuUtils.getRemoteIP(req)+"'");
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
//				System.err.println("[Warning] userCenter getUserInfo 数据库链接失败");
//				return;
//			}
//
//			ResultSet rs = sql.pullData("appid=\""+inj.getString("appid")+"\"", "app_info");
//			if(!rs.next()) {
//				retj.put("status", 2);
//				out.println(retj);
//				System.err.println("[Warning] userCenter getUserInfo 验证失败  '"+MaxMikuUtils.getRemoteIP(req)+"'");
//				rs.close();
//				sql.closeDB();
//				return;
//			}
//			if(!rs.getString("appid").equals(inj.getString("appid")) || !rs.getString("appsecret").equals(inj.getString("appsecret"))){
//				//验证不通过
//				retj.put("status", 2);
//				out.println(retj);
//				System.err.println("[Warning] userCenter getUserInfo 验证失败  '"+MaxMikuUtils.getRemoteIP(req)+"'");
//				rs.close();
//				sql.closeDB();
//				return;
//			}
//			String redirectUrl = rs.getString("redirect_url");
//			rs.close();
//
//			int time = (int)(System.currentTimeMillis()/1000);
//			String accessToken = MaxMikuUtils.getRandomString(32);
//			try {
//				sql.replaceData("app_login", "appid,token,access_token,ip,create_time,redirect_url", "\""+inj.getString("appid")+"\",\""+inj.getString("token")+"\",\""+accessToken+"\",\""+inj.getString("ip")+"\","+time+",\""+redirectUrl+"\"");
//			} catch (SQLException e) {
//				//错误
//				retj.put("status", 3);
//				out.println(retj);
//
//				System.err.println("[Warning] userCenter getUserInfo 写数据表失败");
//				e.printStackTrace();
//				sql.closeDB();
//				return;
//			}
//
//			retData.put("access_token",accessToken);
//
//
//
//
//
//			sql.closeDB();
//
//			clearTimeoutData();
//		} catch (Exception e) {
//			retj.put("status", -2);
//			out.println(retj);
//			sql.closeDB();
//			System.err.println("[Warning] userCenter getUserInfo 内部错误");
//			e.printStackTrace();
//			return;
//		}
//		retj.put("retData",retData);
//
//		out.println(retj);
//	}
//
//	/**
//	 * 清除15分钟前的超时数据
//	 */
//	public static void clearTimeoutData() {
//		try {
//			SqlUtil sql = new SqlUtil();
//			if(!sql.sqlinit()) {
//				System.err.println("[Warning] userCenter clearTimeoutData 数据库链接失败");
//				return;
//			}
//			//15分钟之前为删除时间
//			int deleteTime = ((int)(System.currentTimeMillis()/1000))-900;
//
//			int delitems = sql.deleteData("create_time<"+deleteTime, "app_login");
//
//			if(delitems!=0){
//				System.out.println("已清除 "+delitems+" 条过期数据数据 app_login");
//			}
//			sql.closeDB();
//
//		} catch (Exception e) {
//			System.err.println("clearTimeoutData 时出错:");
//			e.printStackTrace();
//		}
//	}
//}
