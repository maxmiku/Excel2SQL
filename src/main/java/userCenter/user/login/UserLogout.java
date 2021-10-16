package userCenter.user.login;

import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class UserLogout extends HttpServlet{
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		userLogout(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		userLogout(req, resp);
	}

	public static void userLogout(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html;charset=UTF-8");

		PrintWriter out = resp.getWriter();
		JSONObject retj = new JSONObject();
		retj.put("status", 0);
		retj.put("errMsg", "");
		try {
			userLogout(req);
		} catch (Exception e) {
			e.printStackTrace();
			retj.put("status", 1);
			retj.put("errMsg", "登出出错");
		}


		out.print(retj);
	}

	public static void userLogout(HttpServletRequest req) throws IOException{
		req.setCharacterEncoding("UTF-8");

		HttpSession session = req.getSession();
		try {
			session.removeAttribute("token");
			session.removeAttribute("ulogin");
			session.removeAttribute("sRsaPubK");
			session.removeAttribute("cRsaPubK");
			session.removeAttribute("aesKey");
			session.removeAttribute("sRsaPriK");
			session.removeAttribute("uid");
			session.removeAttribute("ulevel");
			session.removeAttribute("uname");
			session.removeAttribute("ulogin");
			session.removeAttribute("verifyCode");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
