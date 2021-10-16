package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import config.Config;
import org.json.JSONObject;


/**
 * Sql工具类
 * Ver 2.0
 * @author lexuan
 *
 */
class SqlCfg {
	public static String user= Config.sql_user;//sql数据库用户名Yigongshi
    public static String pwd=Config.sql_pwd;//sql数据库密码yibanygs
    public static String url=Config.sql_url;//sql数据库地址 eg.47.97.122.17/Yigongshi
   
    public static String driver=Config.sql_driver;//数据库连接驱动class名


}

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
	
	/**
	 * 插入数据
	 * INSERT INTO table_name (列1, 列2,...) VALUES (值1, 值2,....)
	 * @param form 表名
	 * @param field 要插入数据的字段
	 * @param value 对应的值
	 * @return
	 * @throws SQLException
	 */
	public int insertData(String form,String field,String value) throws SQLException {//插入数据
//		System.out.println("INSERT INTO "+form+" ("+field+") VALUE ("+value+")");
		return st.executeUpdate("INSERT INTO "+form+" ("+field+") VALUE ("+value+")");
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
	
	public void createDB(String dbName) throws SQLException {
		st.executeUpdate("CREATE DATABASE if not exists "+dbName+";");
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
	
	/**
	 * 获取每列的类型
	 * 注:只有prepareStatement才可以?
	 * 返回 字段名与类型的键值对
	 * 类型 TIMESTAMP  INT  VARCHAR  DOUBLE
	 */
	public Map<String,String> getColumnTypes(String tableName) {
		
        Map <String,String> columnTypes = new HashMap<>();
	        
        PreparedStatement pStemt = null;
        
        String tableSql = "SELECT * FROM "+ tableName +" LIMIT 1";
        try {
            pStemt = conn.prepareStatement(tableSql);
            //结果集元数据
            ResultSetMetaData rsmd = pStemt.getMetaData();
            //表列数
            int size = rsmd.getColumnCount();
            for (int i = 0; i < size; i++) {
                columnTypes.put(rsmd.getColumnName(i+1),rsmd.getColumnTypeName(i + 1));
            }
        } catch (SQLException e) {
            System.out.println("getColumnTypes failure");
            e.printStackTrace();
        } finally {
            if (pStemt != null) {
                try {
                    pStemt.close();
                } catch (SQLException e) {
                    System.out.println("getColumnTypes close pstem and connection failure");
                    e.printStackTrace();
                }
            }
        }
        return columnTypes;
    }
	
	//sql字符串规则
	public static final String inj_str = "select|update|delete|exec|count|'|\"|=|;|>|<|%";
	static String[] inj_stra = null;
	/**
	 * 检测是否有sql注入
	 * @param str
	 * @return boolean 有注入为true 没有为false
	 */
	public static boolean sql_injCheck(String str){
		if(inj_stra==null) {
			inj_stra = inj_str.split("\\|");
		}
		
		for (String tmp:inj_stra){
			
			if (str.indexOf(tmp)>=0){
				System.out.println("违反规则:"+tmp);
				return true;
			}
		}
		
		return false;
	}
}
