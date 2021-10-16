//package recycleBin;
//
//import net.sf.json.JSONObject;
//
//public class exportExcel {
//	public boolean exportExcel(String exfile, String jsfile, int limit, JSONObject jsout, JSONObject errj) {
//		/**
//		 * 导出excel到json
//		 *
//		 * @Param 	exfile excel文件路径     limit 读取的行数
//		 * 			jsfile 导出到的json文件路径
//		 * 			jsout 返回存有导出数据的json对象
//		 * 			errj 报错时的文本
//		 *
//		 * @return  errj value 0成功  -1参数错误  -2kb
//		 */
//		if((jsfile.equals("")||jsfile==null) && jsout==null) {
//			System.out.println("传入参数错误,无法确定使用哪种方式导出数据");
//			errj.put("status", -1);
//			errj.put("errMsg", "程序内部错误,导出方式为空");
//			return false;
//		}
//		return false;
//	}
//}
