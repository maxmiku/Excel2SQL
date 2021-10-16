package excelEditor.api;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import userCenter.user.login.UserLogout;

import org.json.JSONObject;

/**
 * 检查用户是否登录
 * @author lexuan
 */
@WebServlet("/checkAuth")
public class CheckAuth extends HttpServlet{
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
    	resp.setContentType("text/html;charset=UTF-8");
    	JSONObject retj = new JSONObject();
    	retj.put("status", 0);
		retj.put("errMsg", "");
		retj.put("retData", "");
		PrintWriter out = resp.getWriter();
		
		String inString = utils.Request2StringUtil.getRequestPostStr(req);
		
		if(inString.equals("logout")) {

			UserLogout.userLogout(req,resp);
			out.print(retj);
			return;
		}
		
		CheckAuth.check(req, resp, retj);
		out.print(retj);
	}
	/**
	 * 检测用户是否有权限
	 * @param req
	 * @param resp
	 * @param retj
	 * @return 有 true    没有 false
	 * @throws IOException
	 */
	public static boolean check(HttpServletRequest req,HttpServletResponse resp,JSONObject retj) throws IOException {
		HttpSession session = req.getSession();

		if(session.getAttribute("ulogin")==null || (int)session.getAttribute("ulogin")==0){
			//未登录
			retj.put("status", -401);
			retj.put("errMsg", "尚未登录");
			return false;
		}else{
			//用户等级 0受限  1普通  2管理员  3超级管理员  4root
			//管理员以上才可以编辑
			if(session.getAttribute("ulevel")==null || Integer.parseInt((String)session.getAttribute("ulevel"))<2) {
				retj.put("status", -403);
				retj.put("errMsg", "权限不足");
				return false;
			}
		}

		JSONObject retData = new JSONObject();
		retData.put("uname", session.getAttribute("uname"));
		retj.put("retData", retData);
		return true;
	}
}
