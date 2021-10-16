package userCenter.tools.init;

import utils.SqlUtil;

import java.sql.SQLException;

class InitDatabase {
	/**
	 * @author lexuan
	 * 
	 * 初始化类 只用于后端初始化用,请勿在程序中调用,只需运行该类即可
	 */
	private final class sqlAdminAccount{
		/**
		 * 该类用于储存数据库管理员账号,用于初始化,初始化结束后可删除.
		 */
		static final String usr = "root";//用户名
		static final  String pwd = "a112233";//用户密码
		static final String addr = "127.0.0.1";//数据库地址
		
	}
	private static void initDatabase() throws SQLException {

		SqlUtil sql=new SqlUtil();
		
		if(!sql.sqlinit(sqlAdminAccount.addr, sqlAdminAccount.usr, sqlAdminAccount.pwd)) {
			System.out.println("数据库连接失败,请检查 SqlUtil 中的数据库配置");
			return;
		}
		
		//创建数据库
		sql.createDB("user_center");
		
		sql.useDB("user_center");
		
		//创建用户表
		sql.createForm("users", "CREATE TABLE 'users' ( 'uid' varchar(64), 'uname' varchar(64) COLLATE utf8_unicode_ci NOT NULL,  'upass' varchar(64) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,  'ulevel' int(4) DEFAULT 0,  'ustatus' int(4) DEFAULT 0,  'upass_wrong' varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,  'ulast_login' varchar(32) COLLATE utf8_unicode_ci DEFAULT NULL,  'unotic' varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,  PRIMARY KEY ('uid','uname'),  UNIQUE KEY 'uname' ('uname')) ENGINE=InnoDB AUTO_INCREMENT=10003 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;");
		
		//用于设置uname字段不能重复,建表时已设置
//		sql.st.execute("ALTER TABLE user_center.users ADD UNIQUE (uname)");
//		ALTER TABLE user_center.users DROP INDEX uname;  //移出不能重复
		
		//设置uid从10000开始递增
		// sql.st.execute("alter table user_center.users AUTO_INCREMENT=10000;");
		
		
		
		
		
		System.out.println("初始化结束");

	}
	
	public static void main(String[] args) throws SQLException {
		initDatabase();
	}
}
