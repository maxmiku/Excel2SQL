package userCenter.user.manage;

import userCenter.filter.SqlInjectFilter;
import org.json.JSONArray;
import org.json.JSONObject;
import userCenter.tools.security.BanTools;
import utils.*;

import userCenter.utils.SqlUtil;

import javax.crypto.BadPaddingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChangePassword extends HttpServlet{
	
	@Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		/**
		 * @author lexuan
		 * 
		 * @return status:0成功  1数据库连接失败  2解析请求数据失败  3发现sql注入 4找不到用户名  5与当前用户不匹配  6新密码解密失败 7服务器内部错误 8session不存在用户名
		 */
    	req.setCharacterEncoding("UTF-8");
    	resp.setContentType("text/html;charset=UTF-8");
		PrintWriter out = resp.getWriter();
		JSONObject retj = new JSONObject();
		JSONObject inj = null;
		HttpSession session = req.getSession();

		retj.put("status", 0);
		retj.put("errMsg", "");
		
		
		try {
			String reqStr=Request2StringUtil.getRequestPostStr(req);
			
			
			inj = new JSONObject(reqStr);
			if(inj.get("username")==null || inj.get("oPass")==null) {
				retj.put("errMsg","非法请求,参数不完整");
				retj.put("status",2);
				BanTools.increaseBanIndex(req, 1);
				return;
			}
			if(inj.get("token")!=null) {
				String detoken = RSAUtil.decrypt(inj.getString("token"), (String)session.getAttribute("sRsaPriK"));
				String realToken = MD5Util.md5(inj.getString("username")+inj.getString("oPass")+inj.getString("newPass")+(String)session.getAttribute("token"));
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
			
			
			if(SqlInjectFilter.sqlValidate(inj.getString("username"))) {
				retj.put("status",3);
				retj.put("errMsg","致命错误:非法请求");
				BanTools.increaseBanIndex(req, 3);
				out.print(retj);
				return;
			}
			if(!inj.getString("username").equals((String) session.getAttribute("uname"))) {
				retj.put("status",5);
				retj.put("errMsg","请求非法,当前登录用户不正确");
				out.print(retj);
				BanTools.increaseBanIndex(req, 0.5);
				return;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			retj.put("status",2);
			retj.put("errMsg","非法请求");
			out.print(retj);
			BanTools.increaseBanIndex(req, 0.5);
			return;
		}
		
		
		
		
		retj.put("retData", changePass(inj,retj,req,session));
		
		out.print(retj);
	}
	
	private static JSONObject changePass(JSONObject inj,JSONObject errj,HttpServletRequest req,HttpSession session) {
		/**
		 * @author lexuan
		 * 更改密码的接口
		 * 
		 */
		JSONObject data = new JSONObject();
		SqlUtil rsql = new SqlUtil();
		SqlUtil wsql = new SqlUtil();
		if(!rsql.sqlinit() || !wsql.sqlinit()) {
			errj.put("errMsg","数据库连接失败,请联系管理员");
			errj.put("status",1);
			return data;
		}
		data.put("changeStatus", 0);
		data.put("usalt","");
		String uname = (String)session.getAttribute("uname");
		int ulogin = (int)session.getAttribute("ulogin");
		if(uname==null || ulogin!=1) {
			errj.put("status",8);
			errj.put("errMsg","非法登录,请重新登录");
			return data;
		}
		try {
			ResultSet rs=rsql.pullData("uname=\""+uname+"\"", "users");
			int count=0;
			String inPass = RSAUtil.decrypt(inj.getString("oPass"), (String)session.getAttribute("sRsaPriK"));
			while(rs.next()) {
				count++;
				if(count>1) {
					wsql.updateData("uname=\""+uname+"\"", "users", "unotic=\"你的账户有重名,请联系管理员修改.\"");
				}
				String dbPass = MD5Util.md5(rs.getString("upass")+(String)session.getAttribute("sRsaPubK"));//用户经过md5加密的密码
				
				
				if(inPass.equals(dbPass)) {
					//密码正确 开始判断新密码
					BanTools.cleanBanIndex(req);
					String deRsaNew = RSAUtil.decrypt(inj.getString("newPass"), (String)session.getAttribute("sRsaPriK"));
					
					//新密码原文
					String deNew = null;
					try {
						deNew = AESUtil.decrypt(rs.getString("upass"), deRsaNew);
					} catch (BadPaddingException e) {
						// TODO: handle exception
						e.printStackTrace();
						System.err.println("加密新密码的key错误");
						errj.put("status",6);
						errj.put("errMsg","新密码数据损坏,请重试");
					}
					
					if(MaxMikuUtils.checkPassStrong(deNew)<2) {
						//密码太简单
						data.put("changeStatus", RSAUtil.encrypt("-2",(String) session.getAttribute("cRsaPubK")));
					}else {
						//密码正常
						System.out.println("新密码:"+deNew);
						//用于校验新密码是否完整
						System.out.println("旧盐:"+rs.getString("usalt"));
						String deNewsMd5 = null;
						if(rs.getString("usalt")==null) {
							deNewsMd5= MD5Util.md5(deNew);
						}else {
							deNewsMd5= MD5Util.md5(deNew+rs.getString("usalt"));
						}
						
						String newsMd5 = RSAUtil.decrypt(inj.getString("npmd"), (String)session.getAttribute("sRsaPriK"));
						
						if(deNewsMd5.equals(newsMd5)) {
							//验证通过
							
							String saltNew = MaxMikuUtils.getRandomString(16);
							
							//新密码+新盐的md5
							String newPass = MD5Util.md5(deNew+saltNew);
							
							//更新密码
							wsql.updateData("uname=\""+uname+"\"", "users", "upass=\""+ newPass +"\"");
							//更新盐
							wsql.updateData("uname=\""+uname+"\"", "users", "usalt=\""+ saltNew +"\"");
							
							
							
							data.put("changeStatus", RSAUtil.encrypt("1",(String) session.getAttribute("cRsaPubK")));
							data.put("usalt", RSAUtil.encrypt(saltNew,(String) session.getAttribute("cRsaPubK")));
						}else {
							//验证失败
							errj.put("status",6);
							errj.put("errMsg","新密码数据损坏,请重试");
						}
					}
				}else {
					//密码错误
					data.put("changeStatus", RSAUtil.encrypt("-1",(String) session.getAttribute("cRsaPubK")));
					BanTools.increaseBanIndex(req, 1);
					String nowIP = MaxMikuUtils.getRemoteIP(req); 
					
					
					int beforWrongCount = rs.getInt("upass_wrong_count");
					++beforWrongCount;
					if(beforWrongCount>19) {
						wsql.updateData("uname=\""+uname+"\"", "users", "upass_wrong_count="+ (beforWrongCount) +", unotic=\"自上次成功登陆以来,你的账号被错误输入密码"+beforWrongCount+"次,你可以到登录失败历史查看详情.\"");
					}else {
						wsql.updateData("uname=\""+uname+"\"", "users", "upass_wrong_count="+ (beforWrongCount) +"");
					}
					
					//获取数据库中以前输入错误数据
					String wData=rs.getString("upass_wrong");
					
					boolean insFlag = false;//成功插入数据标志 即无需新增
					
					Date i_date=new Date();   //这里的时util包下的                          
					SimpleDateFormat dateTransferFull=new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");  //这是24时
					String date=dateTransferFull.format(i_date);
					
					JSONArray erList = null;
					try {
						erList = new JSONArray( (wData==null)? "[]" : wData );
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						erList = new JSONArray();
					}
					
//					System.out.println("错误记录条数:"+erList.size()+" raw:"+rs.getString("upass_wrong"));
					
					for (int i = erList.length()-1; i >= 0 ; i--) {
						String now = erList.getString(i);
						String[] nowArr = now.split("\\_");
						if(nowArr.length!=4) {
							//该条数据损坏
							erList.remove(i);//移除
						}
					}
					
					if(erList.length()<5) {
						//还有空间新增数据
						for (int i = erList.length()-1; i >= 0 ; i--) {
							String now = erList.getString(i);
							String[] nowArr = now.split("\\_");
							if(nowIP.equals(nowArr[2])) {
								//找到ip
								String tmpMix = date+"_"+nowIP+"_"+(Integer.parseInt(nowArr[3])+1);
								erList.put(i,tmpMix);
								insFlag=true;
								break;
							}
						}
						if(!insFlag) {
							//没有在原来的数据中找到数据
							
							String tmpMix = date+"_"+nowIP+"_1";
							erList.put(tmpMix);
							
						}
					}else {
						//没有空间新增数据
						
						Map<Integer,Integer> idCount = new HashMap<Integer,Integer>();//用于储存数量相对的位置
						for (int i = erList.length()-1; i >= 0 ; i--) {
							String now = erList.getString(i);
							String[] nowArr = now.split("\\_");
							if(nowIP.equals(nowArr[2])) {
								//找到ip
								
								String tmpMix = date+"_"+nowIP+"_"+(Integer.parseInt(nowArr[3])+1);
								erList.put(i,tmpMix);
								insFlag=true;
								break;
							}
							idCount.put(i, Integer.parseInt(nowArr[3]));
						}
						if(!insFlag) {
							//没有在原来的数据中找到数据
							int minIndex = 0;
							int minVal = 99999;
							for(int i=idCount.size();i>=0;i--) {
								int nowint = idCount.get(i);
								if(nowint<minVal)
									minIndex=i;
							}
							String tmpMix = date+"_"+nowIP+"_1";
							erList.put(minIndex,tmpMix);
							
						}
						
					}
					
					//将新的密码错误信息存进数据库
					wsql.updateData("uname=\""+uname+"\"", "users", "upass_wrong='"+erList+"'");
					
					//============密码错误else结束
				}
				
			}
			if(count==0){
				data.put("loginStatus", 1);
				errj.put("status",4);
				errj.put("errMsg","找不到用户名");
				BanTools.increaseBanIndex(req, 1);
				
			}
			
			
			rs.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			errj.put("status",7);
			errj.put("errMsg","服务器内部错误");
		}
		rsql.closeDB();
		wsql.closeDB();
		
		return data;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
