package userCenter.utils;

import userCenter.config.Config.SqlCfg;

import java.sql.*;


public class SqlUtil {
	public Connection conn=null;//数据库连接
	public Statement st=null;
	public boolean sqlinit(String url,String user,String pwd){
		/**
		 * @author lexuan
		 * @param url 数据库链接
		 * @param user 数据库用户名
		 * @param pwd 数据库密码
		 * 
		 * @return Boolean 是否连接成功
		 */
		
		try {
			//1.初始化jdbc驱动器 注意一定要在方法中 throws Exception
			Class.forName(SqlCfg.driver);
			//2.连接数据库
			conn = DriverManager.getConnection("jdbc:mysql://"+url, user, pwd);
			if (conn==null)
				return false;
			//3.获取用于向数据库发送指令的statement(声明)
			st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);//获取对象 可以前后滚 只读
			return true;
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean sqlinit(){
		/**
		 * 用默认数据初始化连接
		 * @author lexuan
		 * 
		 * @return Boolean 是否连接成功
		 */
		try {
			//1.初始化jdbc驱动器 注意一定要在方法中 throws Exception
			Class.forName(SqlCfg.driver);
			//2.连接数据库
			conn = DriverManager.getConnection("jdbc:mysql://"+SqlCfg.url, SqlCfg.user, SqlCfg.pwd);
			if (conn==null)
				return false;
			//3.获取用于向数据库发送指令的statement(声明)
			st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);//获取对象 可以前后滚 只读
			return true;
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	public ResultSet pullData(String condition,String form) throws SQLException {//查询数据
//		System.out.println("SELECT * FROM "+form+" WHERE "+condition);
		return st.executeQuery("SELECT * FROM "+form+" WHERE "+condition);
	}
	
	public ResultSet pullData(String condition,String form,String field) throws SQLException {//查询数据 仅指定字段名
//		System.out.println("SELECT "+field+" FROM "+form+" WHERE "+condition);
		return st.executeQuery("SELECT "+field+" FROM "+form+" WHERE "+condition);
	}
	
	public int updateData(String condition,String form,String newData) throws SQLException {//更新数据 newData->eg. userId=123
//		System.out.println("UPDATE "+form+" SET "+newData+" WHERE "+condition);
		return st.executeUpdate("UPDATE "+form+" SET "+newData+" WHERE "+condition);
	}
	
	public int deleteData(String condition,String form) throws SQLException {//删除数据
		return st.executeUpdate("DELETE FROM "+form+" WHERE "+condition);
	}
	
	public int insertData(String form,String field,String value) throws SQLException {//插入数据
//		System.out.println("INSERT INTO "+form+" ("+field+") VALUE ("+value+")");
		return st.executeUpdate("INSERT INTO "+form+" ("+field+") VALUE ("+value+")");
	}
	
	public void createDB(String dbName) throws SQLException {
		st.executeUpdate("CREATE DATABASE if not exists "+dbName+";");
	}
	
	/**
	 * 替换数据否则插入
	 * INSERT INTO table_name (列1, 列2,...) VALUES (值1, 值2,....)
	 * @param form 表名
	 * @param field 要插入数据的字段
	 * @param value 对应的值
	 * @return
	 * @throws SQLException
	 */
	public int replaceData(String form,String field,String value) throws SQLException {//插入数据
//		System.out.println("REPLACE INTO "+form+" ("+field+") VALUE ("+value+")");
		return st.executeUpdate("REPLACE INTO "+form+" ("+field+") VALUE ("+value+")");
	}
	
	public void createForm(String name,String field) throws SQLException {
		st.executeUpdate("CREATE TABLE if not exists "+name+" ("+field+")");
	}
	public void useDB(String name) throws SQLException {//指定使用的数据库
		st.execute("USE "+name);
	}
	public void closeDB(){//指定使用的数据库
		try {
			st.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
}
