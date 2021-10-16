package userCenter.user.register;

import userCenter.config.Config;
import userCenter.filter.SqlInjectFilter;
import org.json.JSONObject;
import userCenter.tools.security.BanTools;
import utils.MD5Util;
import utils.RSAUtil;
import utils.Request2StringUtil;
import userCenter.utils.SqlUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.UUID;

/**
 * 用户注册接口
 */
public class UserRegister extends HttpServlet{

	private static final long serialVersionUID = -1648558336687154886L;

	@Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		/**
		 * @author lexuan
		 * 
		 * @return status:
		 * 	0成功  1数据库连接失败  2解析请求数据失败  3发现sql注入 4找不到用户名 5用户与登录时的不匹配 7服务器内部错误 8注册功能已停用 11用户已存在
		 */
    	req.setCharacterEncoding("UTF-8");
    	resp.setContentType("text/html;charset=UTF-8");
		PrintWriter out = resp.getWriter();
		JSONObject retj = new JSONObject();
		JSONObject inj = null;
		HttpSession session = req.getSession();
		retj.put("status", 0);
		retj.put("errMsg", "");

		if(!Config.RegCfg.AllowRegister){
			retj.put("status", 8);
			retj.put("errMsg", "注册功能已停用");
			out.print(retj);
			return;
		}
		
		try {
			String reqStr=Request2StringUtil.getRequestPostStr(req);

			String usalt = (String) session.getAttribute("usalt");
			String uname = (String) session.getAttribute("uregname");
			String uemail = (String) session.getAttribute("uregemail");
			
			inj = new JSONObject (reqStr);
			if(inj.getString("uname")==null || inj.getString("password")==null) {
				retj.put("errMsg","非法请求");
				retj.put("status",2);
				BanTools.increaseBanIndex(req, 1);
				return;
			}

			String newPass = RSAUtil.decrypt(inj.getString("password"),(String)session.getAttribute("sRsaPriK"));

			if(newPass==null){
				retj.put("errMsg","数据解析失败, 请重试.");
				retj.put("status",2);
				BanTools.increaseBanIndex(req, 0.1);
				out.print(retj);
				return;
			}

			if(!inj.getString("uname").equals((String) session.getAttribute("uregname"))) {
				retj.put("status",5);
				retj.put("errMsg","请求非法,当前用户不正确,请返回上一步重新输入用户名");
				out.print(retj);
				BanTools.increaseBanIndex(req, 0.5);
				return;
			}

			if(inj.get("token")!=null) {
				String detoken = RSAUtil.decrypt(inj.getString("token"), (String)session.getAttribute("sRsaPriK"));
				System.out.println(newPass+"  "+(String)session.getAttribute("usalt"));
				String realToken = MD5Util.md5((String) session.getAttribute("uregname")+(String)session.getAttribute("uregemail")+MD5Util.md5(newPass+(String)session.getAttribute("usalt"))+(String)session.getAttribute("token"));
				if(detoken==null || !detoken.equals(realToken)) {
					retj.put("errMsg","非法token");
					retj.put("status",2);
					BanTools.increaseBanIndex(req, 1);
					out.print(retj);
					return;
				}



			}else {
				retj.put("errMsg","空token");
				retj.put("status",2);
				BanTools.increaseBanIndex(req, 1);
				out.print(retj);
				return;
			}
			
			
			if(SqlInjectFilter.sqlValidate(newPass)) {
				retj.put("status",3);
				retj.put("errMsg","致命错误:非法请求, 请更换密码重试.");
				BanTools.increaseBanIndex(req, 3);
				out.print(retj);
				return;
			}


			retj.put("retData", userRegister(uname,uemail,newPass,usalt,retj,req,session));
			
		} catch (Exception e) {
			retj.put("status",2);
			retj.put("errMsg","非法请求");
			out.print(retj);
			BanTools.increaseBanIndex(req, 0.5);
			return;
		}



		

		
		out.print(retj);
	}

	/**
	 * 新增注册用户
	 * @param uname 用户名
	 * @param uemail 用户邮件地址
	 * @param newPass 经过 +usalt md5处理的加密密码
	 * @param usalt 密码盐
	 * @param errj 错误json
	 * @param req 客户端的请求
	 * @param session 客户端的session
	 * @return JSONObject 其中包含一个用crsa加密的 regStatus  0为注册成功  -1为注册失败 -2用户已存在
	 */
	private static JSONObject userRegister(String uname,String uemail,String newPass,String usalt, JSONObject errj, HttpServletRequest req, HttpSession session) {
		JSONObject data = new JSONObject();
		SqlUtil sql = null;
		sql = new SqlUtil();
		if(!sql.sqlinit()) {
			errj.put("errMsg","数据库连接失败,请联系管理员");
			errj.put("status",1);
			return data;
		}

		try {
			data.put("regStatus", RSAUtil.encrypt("-1",(String) session.getAttribute("cRsaPubK")));
			ResultSet rs = sql.pullData("uname=\""+uname+"\"","users");
			if(rs.next()){
				//已存在用户
				data.put("regStatus", RSAUtil.encrypt("-2",(String) session.getAttribute("cRsaPubK")));
				rs.close();
				sql.closeDB();
				return data;
			}
			rs.close();

			if(sql.insertData("users","uid,uname,uemail,upass,usalt,ulevel","\""+ UUID.randomUUID().toString() +"\",\""+uname+"\",\""+uemail+"\",\""+newPass+"\",\""+usalt+"\","+ Config.RegCfg.defaultUserLevel)==0){
				//写入数据错误
				data.put("regStatus", RSAUtil.encrypt("-1",(String) session.getAttribute("cRsaPubK")));
				rs.close();
				sql.closeDB();
				return data;
			}
			data.put("regStatus", RSAUtil.encrypt("0",(String) session.getAttribute("cRsaPubK")));
		} catch (Exception e) {
			e.printStackTrace();
			errj.put("status",7);
			errj.put("errMsg","服务器内部错误");
		}
		sql.closeDB();
		
		return data;
	}
}
