package userCenter.config;

import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;
import utils.JSONConfigFileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

public final class Config {
	public static class SqlCfg{
		static JSONObject db_uc = JSONConfigFileUtil.appconfig.getJSONObject("database").getJSONObject("userCenter");
		public static String user=db_uc.getString("sql_user");//sql数据库用户名Yigongshi
	    public static String pwd=db_uc.getString("sql_pwd");//sql数据库密码yibanygs
	    public static String url=db_uc.getString("sql_url");//sql数据库地址 eg.47.97.122.17/Yigongshi
	    
//	    public static String user="root";//sql数据库用户名Yigongshi
//	    public static String pwd="a112233";//sql数据库密码yibanygs
//	    public static String url="127.0.0.1/user_center";//sql数据库地址 eg.47.97.122.17/Yigongshi
	    
	    public static String driver=JSONConfigFileUtil.appconfig.getJSONObject("database").getString("sql_driver");//数据库连接驱动class名

//		SqlCfg(){
//			if(JSONConfigFileUtil.appconfig==null){
//				JSONConfigFileUtil.getConfig();
//			}
//
//
//			user = db_e2.getString("sql_user");
//			pwd = db_e2.getString("sql_pwd");
//			url = db_e2.getString("sql_url");
//
//			driver = JSONConfigFileUtil.appconfig.getJSONObject("database").getString("sql_driver");
//			System.out.println("userCenter database Config 初始化完成");
//		}
	}

	public static class RegCfg{
		static JSONObject  regcfg = JSONConfigFileUtil.appconfig.getJSONObject("userCenterConfig").getJSONObject("regCfg");
		/**
		 * 默认注册后初始用户等级
		 * 0受限  1普通  2管理员  3超级管理员  4root
		 */
		public static int defaultUserLevel = regcfg.getInt("defaultUserLevel");

		/**
		 * 是否允许注册
		 */
		public static boolean AllowRegister = regcfg.getBoolean("AllowRegister");


	}





}
