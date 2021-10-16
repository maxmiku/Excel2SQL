package userCenter.user.register;

import userCenter.config.Config;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 获取是否允许注册
 */
public class CanRegister extends HttpServlet{

	/**
	 * status 0为开启 1为停用
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html;charset=UTF-8");
    	PrintWriter out = resp.getWriter();
		JSONObject retj = new JSONObject();
		retj.put("status", 0);
		retj.put("errMsg", "");

		if(!Config.RegCfg.AllowRegister){
			retj.put("status", 1);
			retj.put("errMsg", "注册功能已停用");
			out.print(retj);
			return;
		}
		out.print(retj);
	}
}
