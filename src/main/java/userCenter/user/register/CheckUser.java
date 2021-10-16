package userCenter.user.register;

import userCenter.config.Config;
import userCenter.filter.SqlInjectFilter;
import org.json.JSONObject;
import userCenter.tools.security.BanTools;
import utils.*;
import userCenter.utils.SqlUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * 注册用检测用户是否存在
 */
public class CheckUser extends HttpServlet{

	private static final long serialVersionUID = -652147906401084540L;

	@Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		/**
		 * @author lexuan
		 * 
		 * @return status:0成功  1数据库连接失败  2解析请求数据失败  3发现sql注入 4验证码错误  5密钥对生成错误 6服务器内部错误 7数据验证失败  8服务器关闭了注册功能
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
//			System.out.println(reqStr+" ["+SqlInjectFilter.sqlValidate(reqStr)+"]");
			inj = new JSONObject (reqStr);
			dbout("用户传入数据 "+inj);
			if(inj.get("uname")==null || inj.get("email")==null || inj.get("token")==null) {
				retj.put("status",2.1);
				retj.put("errMsg","非法请求");
				out.print(retj);
				BanTools.increaseBanIndex(req, 1);
				return;
			}
			if(inj.get("cRsaPubKey")==null) {
				retj.put("errMsg","非法请求");
				retj.put("status",2);
				out.print(retj);
				BanTools.increaseBanIndex(req, 1);
				return;
			}
			
			if(SqlInjectFilter.sqlValidate(inj.getString("uname")+inj.getString("email"))) {
				retj.put("status",3);
				retj.put("errMsg","致命错误:非法请求,请更换用户名 或 email");
				BanTools.increaseBanIndex(req, 3);
				out.print(retj);
				return;
			}

			if(!inj.getString("token").equals(MD5Util.md5(inj.getString("uname")+inj.getString("email")+inj.getString("verifyCode")))){
				retj.put("status",7);
				retj.put("errMsg","数据在传输途中损坏, 请重试");
				BanTools.increaseBanIndex(req, 0.1);
				out.print(retj);
				return;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			retj.put("status",2);
			retj.put("errMsg","服务器错误,非法请求");
			out.print(retj);
			BanTools.increaseBanIndex(req, 0.1);
			return;
		}
		
//		//验证码验证块 区分大小写
//		if(!inj.get("verifyCode").equals(session.getAttribute("verifyCode")) || session.getAttribute("verifyCode")==null) {
//			retj.put("status",4);
//			retj.put("errMsg","验证码错误,请区分大小写重新输入.");
//			out.print(retj);
//			session.setAttribute("verifyCode",null);
//			BanTools.increaseBanIndex(req, 0.5);
//			return;
//		}else {
//			session.setAttribute("verifyCode",null);
//		}
		//验证码验证块 不区分大小写
		if(!(inj.get("verifyCode")==null)) {
			//存在验证码
			if(!(session.getAttribute("verifyCode")==null)) {
				//session里面有验证码
				if(inj.getString("verifyCode").equalsIgnoreCase((String) session.getAttribute("verifyCode"))) {
					//验证码正确
					session.setAttribute("verifyCode",null);
				}else {
					//验证码错误
					retj.put("status",4);
					retj.put("errMsg","验证码错误,请重新输入.");
					out.print(retj);
					session.setAttribute("verifyCode",null);
					BanTools.increaseBanIndex(req, 0.5);
					return;
				}
			}else {
				//session里面没有验证码
				retj.put("status",3);
				retj.put("errMsg","致命错误:非法请求,请刷新验证码.");
				BanTools.increaseBanIndex(req, 3);
				out.print(retj);
				return;
			}
		}else {
			//无验证码
			retj.put("status",3);
			retj.put("errMsg","致命错误:非法请求,请输入验证码.");
			BanTools.increaseBanIndex(req, 3);
			out.print(retj);
			return;
		}
		
		
		
		retj.put("retData",checkUser(inj, retj, req, session));
		
		
//		System.out.println(retj.get("errMsg"));
		out.println(retj);
//		out.println("{\"status\":0,\"errMsg\":\"success\",\"retData\":{\"regUserStatus\":0,\"sRsaPubKey\":\"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC7TJYW4ImnDflYNNHzVq0Istt/iNBeUWIjyMJVsSaC/0O62VnUeBmaZ42ZtrkVSRMxr/9XHTOaZwmyA43Jp2/cMzuJ81fq+uX0JEBMJhJmoyjNIutHLklpt4V2ZyOMEmYcElhqORCGkGyR5znO/fPxor5QX4lINOUwuCUpMym6IQIDAQAB\"}}");
		return;
    	
	}
	
	private JSONObject checkUser(JSONObject inj,JSONObject errj,HttpServletRequest req,HttpSession session) {
		JSONObject data = new JSONObject();
		SqlUtil sql = new SqlUtil();
		
		if(!sql.sqlinit()) {
			errj.put("errMsg","数据库连接失败,请联系管理员");
			errj.put("status",1);
			return data;
		}

		data.put("regUserStatus", 0);
		data.put("sRsaPubKey", "");
		try {
			ResultSet rs = sql.pullData("uname=\""+inj.get("uname")+"\"", "users");
			if(rs.next()) {
				//有用户数据
				data.put("regUserStatus",-1);
				BanTools.increaseBanIndex(req, 0.3);
			}else {
				//没有用户数据
				data.put("regUserStatus", 0);
				
				session.setAttribute("cRsaPubK", inj.getString("cRsaPubKey"));
				session.setAttribute("uregname", inj.getString("uname"));
				session.setAttribute("uregemail", inj.getString("email"));

				//生成新盐
				String saltNew = MaxMikuUtils.getRandomString(16);

				//写salt
				String usalt = saltNew;

				if(usalt==null) {
					data.put("usalt","");
				}else {
					try {
						data.put("usalt", RSAUtil.encrypt(usalt, inj.getString("cRsaPubKey")));
					} catch (Exception e) {
						e.printStackTrace();
						errj.put("errMsg", "服务器内部错误:加密失败.");
						errj.put("status",6);
					}
				}
				session.setAttribute("usalt", usalt);


				//写token
				String token = MaxMikuUtils.getRandomString(16);
				System.out.println("token:"+token);
				session.setAttribute("token", token);
				try {
					data.put("token", RSAUtil.encrypt(token, inj.getString("cRsaPubKey")));
				} catch (Exception e) {
					e.printStackTrace();
					errj.put("errMsg", "服务器内部错误:token盐加密失败.");
					errj.put("status",6);
				}
				
				
				
				if(session.getAttribute("sRsaPriK")!=null && session.getAttribute("sRsaPubK")!=null) {
					//无需重新生成rsa密钥对
					data.put("sRsaPubKey",session.getAttribute("sRsaPubK"));
				}else {
					//需要生成rsa密钥对
					BanTools.increaseBanIndex(req, 1);
					Map<String,String> keyPair = RSAUtil.genKeyPair();
					if(keyPair == null) {
						data.put("sRsaPubKey","");
						errj.put("errMsg", "服务器内部错误:密钥生成失败.");
						errj.put("status",5);
						return data;
					}
					String sRsaPubK = keyPair.get("pub");
					String sRsaPriK = keyPair.get("pri");
					
					session.setAttribute("sRsaPubK", sRsaPubK);
					session.setAttribute("sRsaPriK", sRsaPriK);
					
					data.put("sRsaPubKey",sRsaPubK);
				}
			}
			rs.close();
			sql.closeDB();
		} catch (SQLException e) {
			e.printStackTrace();
			sql.closeDB();
			errj.put("status",6);
			errj.put("errMsg","服务器内部错误");
		}
		
		return data;
	}
	
	public static void dbout(Object a) {
//		System.out.println(a);
	}
}
