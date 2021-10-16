package config;

import org.json.JSONObject;
import utils.JSONConfigFileUtil;

/**
 * 配置类 用于配置这个强大的导入程序
 * 注意: 除了配置此处    若每次导入需要有附加字段 (操作者id等等) 请在控制线程添加指令中设置(暂定)
 * @author lexuan
 *
 */
public class Config {
	    static JSONObject cfg_ex = JSONConfigFileUtil.appconfig.getJSONObject("excel2sqlConfig");
		static JSONObject db_e2 = JSONConfigFileUtil.appconfig.getJSONObject("database").getJSONObject("excel2sql");

		//sql设置部分
		public static String sql_user=db_e2.getString("sql_user");//sql数据库用户名Yigongshi
	    public static String sql_pwd=db_e2.getString("sql_pwd");//sql数据库密码yibanygs
	    public static String sql_url=db_e2.getString("sql_url");//sql数据库地址 eg.47.97.122.17/Yigongshi
	    public static String sql_driver=JSONConfigFileUtil.appconfig.getJSONObject("database").getString("sql_driver");//数据库连接驱动class名
	    
	    public static boolean skipInitDelette = cfg_ex.getBoolean("skipInitDelette");//跳过初始化删除文件
	    
	    public static int maxAllowUploadSize = cfg_ex.getInt("maxAllowUploadSize");//最大允许上传字节数 单位:KB  1MB=1024KB
	    
//	    public static String login_appid="12qBr9VQqItAqDWK";//用于登录系统的appid
//	    public static String login_appsecret="OAIrFUWA1tNC7IqLvRoGvIw8PGBsivlL";//用于登录系统的appsecret
//	    public static String login_homeurl="/excel2sql";//用于登录系统的成功后的回到主页地址 已于启动时自动获取
	    
	    /**
	     * 可编辑的表名
	     */
	    public static String EXCEL_EDITFORM = cfg_ex.getString("EXCEL_EDITFORM");
	    
	    /**
	     * 最大导入线程数
	     */
	    public static int EXCEL_MAX_IMPORT_THREADS = cfg_ex.getInt("EXCEL_MAX_IMPORT_THREADS");
	    
	    
	    /* 
	     * excel_editable
	     * excel导入中数据库可编辑的字段 ,可从SqlUtil.getColumnTypes(表名) 获取 请注意引号转义
	     * Type:JSONArray字符串
	     * 		其中字段类型可以为:类型 TIMESTAMP  INT  VARCHAR  DOUBLE
	     * 
	     * [  {"id":INT 该字段在表中的编号,"n":String 字段名,"t":String 字段类型,"c":String 中文字段类型(用于给客户展示),"cn":String 字段解释
	     * 		,"d(可选)":"默认值 可选 读取表格出错时会用默认值  当没有该项是则没有默认值"
	     * },{.. 可编辑字段2以此类推 ..}  ]
	     * 
	     * eg.
	     * "[{\"id\":0,\"n\":\"real_name\",\"t\":\"VARCHAR\",\"c\":\"文本\",\"cn\":\"姓名\",\"d\":\"err\"}]"
	     * 
	     */
	    public static String excel_editable = cfg_ex.getJSONArray("excel_editable").toString();
	    
	    /*
	     * excel_recommand
	     * 字段推荐列表
	     * ks:关键字keys的数组
	     * rid:推荐的字段id 与 上方editable相对应
	     * ks为关键字 越往后越优先
			[
				{"ks":["姓名"],"rid":0},
				{"ks":["学号"],"rid":1},
				{"ks":["部门"],"rid":2},
				{"ks":["岗位"],"rid":3},
				{"ks":["工资"],"rid":4},
				{"ks":["工时"],"rid":5},
				{"ks":["考核老师"],"rid":6}
			]
	     */
	    public static String excel_recommand = cfg_ex.getJSONArray("excel_recommand").toString();

//	Config(){
//		if(JSONConfigFileUtil.appconfig==null){
//			JSONConfigFileUtil.getConfig();
//		}
//
//
//		skipInitDelette=;
//		maxAllowUploadSize=;
//
//		EXCEL_EDITFORM=cfg_ex.getString("EXCEL_EDITFORM");
//		EXCEL_MAX_IMPORT_THREADS=;
//
//		excel_editable=cfg_ex.getJSONArray("excel_editable").toString();
//		excel_recommand=cfg_ex.getJSONArray("excel_recommand").toString();
//
//		System.out.println("Excel2sql Config 初始化完成");
//	}
}
