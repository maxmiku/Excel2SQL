//package tools;
//
//import java.util.HashMap;
//import java.util.Map;
//
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSetMetaData;
//import java.sql.SQLException;
//
//
//public class SQLTools {
//	/**
//     * 获取表中所有字段类型
//     * @param tableName
//     * @return
//     */
//    public static Map<String,String> getColumnTypes(String tableName) {
//        Map <String,String> columnTypes = new HashMap<>();
//		try {
//			Class.forName("com.mysql.jdbc.Driver");
//		} catch (ClassNotFoundException e1) {
//			// TODO Auto-generated catch block
//			System.out.println("引入jdbc库失败");
//			e1.printStackTrace();
//			return null;
//		}
//        //与数据库的连接
//        Connection conn = null;
//		try {
//			conn = DriverManager.getConnection("jdbc:mysql://"+SqlCfg.url, SqlCfg.user, SqlCfg.pwd);
//		} catch (SQLException e1) {
//			// TODO Auto-generated catch block
//			System.out.println("连接数据库失败");
//			e1.printStackTrace();
//		}
//        PreparedStatement pStemt = null;
//        String tableSql = "SELECT * FROM "+tableName+" LIMIT 1";
//        try {
//            pStemt = conn.prepareStatement(tableSql);
//            //结果集元数据
//            ResultSetMetaData rsmd = pStemt.getMetaData();
//            //表列数
//            int size = rsmd.getColumnCount();
//            for (int i = 0; i < size; i++) {
//                columnTypes.put(rsmd.getColumnName(i+1),rsmd.getColumnTypeName(i + 1));
//            }
//        } catch (SQLException e) {
//            System.out.println("getColumnTypes failure");
//            e.printStackTrace();
//        } finally {
//            if (pStemt != null) {
//                try {
//                    pStemt.close();
//                    conn.close();
//                } catch (SQLException e) {
//                    System.out.println("getColumnTypes close pstem and connection failure");
//                    e.printStackTrace();
//                }
//            }
//        }
//        return columnTypes;
//    }
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		System.out.println(getColumnTypes("worktimedata"));
//	}
//
//}
