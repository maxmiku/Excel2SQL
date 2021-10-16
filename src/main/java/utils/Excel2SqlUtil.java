package utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import config.Config;
import org.json.JSONArray;
import org.json.JSONObject;
import tools.ProcessCtrlTools;
import tools.tools;

/**
 * Excel导入到SQL的工具类
 * @version 1.2
 * @author lexuan
 * April 2nd 2020
 * 1.1
 * 	加入多线程和状态机制
 * 1.2
 * 	加入默认数据
 *
 */
public class Excel2SqlUtil extends SQLImportUtil{
	
	/**
	 * 导入的所有错误将会显示在这里
	 */
	public JSONArray importAllErr = new JSONArray();
	
	//废弃@param updatePrimaryKeyNames JSONArray [可选] 主键的字符串数组 如果不为空则代表存在时更新  JSONArray 里面为字符串  为null或空时重复时不导入
	/**
	 * excel导入数据到SQL
	 * @param exfile excel文件的路径
	 * @param fieldPArray JSONArray 配对表
	 * @param additionalData 每条数据附加的字段数据 一般操作用户数据月份之类的统一字段使用  JSON的 键为字段名 值为对应字段名的值
	 * @param updateIfExist 存在时更新
	 * @param errInterrupt 错误即中断,默认不中断继续
	 * @param fileid 用于设置进度数据
	 * @return 0成功 -1表中无数据 -2处理时出错  -3指向了不存在的字段 -4数据库连接失败 -5工作簿打开失败 -6表格数据获取失败 -7additional数据获取失败 -8ad数据中发现注入
	 */
	public int excel2sql(String exfile, JSONArray fieldPArray, JSONObject additionalData, boolean updateIfExist, boolean errInterrupt, String fileid){
		return excel2sql(exfile,fieldPArray,additionalData,updateIfExist,errInterrupt,fileid,false);
	}
	/**
	 * excel导入数据到SQL
	 * @param exfile excel文件的路径
	 * @param fieldPArray JSONArray 配对表
	 * @param additionalData 每条数据附加的字段数据 一般操作用户数据月份之类的统一字段使用  JSON的 键为字段名 值为对应字段名的值
	 * @param updateIfExist 存在时更新
	 * @param errInterrupt 错误即中断,默认不中断继续
	 * @param fileid 用于设置进度数据
	 * @param allowDefault 允许使用默认数据
	 * @return 0成功 -1表中无数据 -2处理时出错  -3指向了不存在的字段 -4数据库连接失败 -5工作簿打开失败 -6表格数据获取失败 -7additional数据获取失败 -8ad数据中发现注入
	 */
	public int excel2sql(String exfile,JSONArray fieldPArray,JSONObject additionalData,boolean updateIfExist,boolean errInterrupt,String fileid,boolean allowDefault) {
		
		SqlUtil sql = new SqlUtil();
		Workbook wb = null;
		try {
			//临时的获取错误信息的json
			JSONObject tmperrj = new JSONObject();
			
			
			//打开工作簿
			wb = openExcel(exfile,tmperrj);
			if (wb==null) {
				//文件打开失败
				importError(-1, "打开文件时错误 错误码:"+tmperrj.getInt("status")+" 信息:"+tmperrj.getInt("errMsg"), importAllErr);
				return -1;
			}
			//只获取第一张表
			Sheet et = wb.getSheetAt(0);
			if(et!=null) {
				//表不为空
				
				//打开数据库连接
				if(!sql.sqlinit()) {
					importError(-1, "数据库连接失败", importAllErr);
					wb.close();
					return -4;
				}
				
				
				//报文中的项目数
				int requestCols=fieldPArray.length();
				
				// 获得行数
				int rows = et.getPhysicalNumberOfRows();
				excelRows=rows;
				
				if(rows<2) {
					//行数小于2 无法获取列类型
					importError(-1, "工作表中没有数据 (有效数据行数<2) ,请添加数据后再导入", importAllErr);
					wb.close();
					sql.closeDB();
					return -1;
				}
				
				//附加字段信息
				String addiField = "";
				String addiData = "";
				
				if(additionalData!=null) {
					@SuppressWarnings("unchecked")
					Iterator<String> iter = additionalData.keys();
					if(iter==null) {
						importError(-1, "服务器内部发生严重错误,请记录下错误编码并联系管理员", importAllErr);
						wb.close();
						sql.closeDB();
						return -7;
					}
					while(iter.hasNext()) {
						String key = iter.next();
						if(SqlUtil.sql_injCheck(key)) {
							importError(-1, "服务器内部发生严重错误,请记录下错误编码并联系管理员", importAllErr);
							wb.close();
							sql.closeDB();
							return -8;
						}
						addiField=addiField+","+key;
						Object data = additionalData.get(key);
						if(SqlUtil.sql_injCheck(data.toString())) {
							importError(-1, "服务器内部发生严重错误,请记录下错误编码并联系管理员", importAllErr);
							wb.close();
							sql.closeDB();
							return -8;
						}
						if(data instanceof String) {
							//data是字符串
							addiData=addiData+",\""+data.toString()+"\"";
						}else {
							addiData=addiData+","+data;
						}
						
					}
					
					
					
//					System.out.println("附加数据处理结果 \n字段:"+addiField+"\n字段内容:"+addiData);
				}
				
				
				
				//首列
//				Row headerRow = et.getRow(0);
				
				// 获得列数
//				int cols = headerRow.getPhysicalNumberOfCells();
				
				
				//列类序号
				List<Integer> colid = new ArrayList<Integer>();
				//字段序号
				List<Integer> fieldid = new ArrayList<Integer>();
				//字段类型  0-VARCHAR   1-DOUBLE 2-INT    3-TIMESTAMP
				List<Integer> fieldType = new ArrayList<Integer>();
				//字段名
				List<String> fieldName = new ArrayList<String>();
				//默认数据
				List<String> fieldDefault = new ArrayList<String>();
				
				for (int i = 0; i < requestCols; i++) {
					JSONObject itemj =  fieldPArray.getJSONObject(i);
					if(itemj.getInt("tid")==-1) {
						//不导入该项
						continue;
					}
					
					JSONObject nowField = getEditableField(itemj.getInt("tid"));
					
					if(nowField==null) {
						importError(-1, "列id "+itemj.getInt("sid")+" 指向了不存在的字段id,请检查", importAllErr);
						wb.close();
						sql.closeDB();
						return -3;
					}
					
					
					colid.add(itemj.getInt("sid"));
					fieldid.add(itemj.getInt("tid"));
					
					String targetType = nowField.getString("t");
					if(targetType.equalsIgnoreCase("VARCHAR")) {
						fieldType.add(0);
					}else if(targetType.equalsIgnoreCase("DOUBLE")){
						fieldType.add(1);
					}else if(targetType.equalsIgnoreCase("INT")){
						fieldType.add(2);
					}else if(targetType.equalsIgnoreCase("TIMESTAMP")){
						fieldType.add(3);
					}else {
						//未知类型
						fieldType.add(-1);
					}
					fieldName.add(nowField.getString("n"));
					if(allowDefault){
						if(nowField.has("d")){
							fieldDefault.add(nowField.getString("d"));
						}else{
							fieldDefault.add(null);
						}
					}

					
//					System.out.printf("si:%d,fi:%d,ft:%s,fn:%s\n",itemj.getInt("sid"),itemj.getInt("tid"),nowField.getString("t"),nowField.getString("n"));
					
				}
				
				//需要操作的列数
				int changeCols=colid.size();
				
				//要改变的行数获取的行数
				int changeRows = excelRows-1;
//				int changeRows = 10;
				
				//excel表头
				JSONArray dataHeader = new JSONArray();
				//获取表头
				Row r = et.getRow(0);
				for (int j = 0; j < changeCols; j++) {
					Cell cell = r.getCell(colid.get(j));
					Object tmp = getCellContent(cell);
					dataHeader.put(tmp);
				}
				
				
				
				String field = fieldName.get(0);
				//生成sqlfield模板
				for (int i = 1; i < changeCols; i++) {
					field=field+","+fieldName.get(i);
				}
//				System.out.println("字段字符串为:"+field);
				
				
				
				//开始获取表格数据
				for (int row = 1; row <= changeRows; row++) {
//					Thread.sleep(100);
//更新进度模块
if(row%20==0) {
	ProcessCtrlTools.setProcess(fileid, (Double) ((row*1.00/changeRows)*100),importAllErr.length());
}
					
					
//					System.out.println("当前第"+row+"行");
					String rowData = null;
					r = et.getRow(row);
					
					if(r==null) { 
						//跳过无法获取的行
//						System.err.println("行 "+row+" 获取为null");
						continue;
					}
					JSONArray rowErrj = new JSONArray();
					boolean suspectedNullRow = true;//疑似空行标志
					for (int j = 0; j < changeCols; j++) {
						Cell cell = r.getCell(colid.get(j));
						
						
						String rawtmp = getCell2String(cell);

						if(rawtmp!=null) {
							if(SqlUtil.sql_injCheck(rawtmp)) {
//								System.err.println("导入错误,发现SQL非法字符:行:"+(row+1)+" 发现非法字符");
								rowErrj.put(importError(row+1, "列:"+dataHeader.get(j)+" 发现非法字符  请检查该行单元格中有无一下字符串(字符):"+SqlUtil.inj_str));
								break;
							}
						}else{
							if(allowDefault && fieldDefault.get(j)!=null){
								//启动默认值  该列有默认数据
								rawtmp = fieldDefault.get(j);
								if(SqlUtil.sql_injCheck(rawtmp)) {
//								System.err.println("导入错误,发现SQL非法字符:行:"+(row+1)+" 发现非法字符");
									rowErrj.put(importError(row+1, "列:"+dataHeader.get(j)+" 发现非法字符  请检查该行单元格中有无一下字符串(字符):"+SqlUtil.inj_str));
									break;
								}
							}
						}
						
						String tmp = getCellContent(rawtmp,fieldType.get(j));
						if(tmp==null) {

								rowErrj.put(importError(row + 1, "列:" + dataHeader.get(j) + " 可能原因:对应数字字段的单元格为空 或 存在公式"));
								break;
						}else {
							if(!(cell!=null?(cell.getCellType()==CellType.BLANK):true)){
								//单元格不为空
								suspectedNullRow = false;
							}
						}
						
						if(rowData==null) {
							rowData=tmp;
						}else {
							rowData=rowData+","+tmp;
						}
					}
					
					if(rowErrj.length()>0) {
						if(!suspectedNullRow) {
							//又有错误 又不是空行 跳过
							importAllErr.put(importError(row+1, "此行有错误且不为空行"));
							importAllErr=tools.joinJSONArray(importAllErr,rowErrj);
							if(errInterrupt) {
								wb.close();
								sql.closeDB();
								return -6;
							}
							continue;
						}else {
							//空行
//							System.out.println("行:"+(row+1)+" 为空行");
							continue;
						}
					}
					
//					System.out.println("当前行 字段对应的数据:"+rowData);
					try {
						
						if(updateIfExist) {
							sql.replaceData(Config.EXCEL_EDITFORM, field+addiField, rowData+addiData);
						}else {
							sql.insertData(Config.EXCEL_EDITFORM, field+addiField, rowData+addiData);
						}
						
					}catch (Exception e) {
//						e.printStackTrace();
						System.err.println(e.getMessage().split(";")[0]);
						if(e.getMessage().indexOf("PRIMARY")!=-1 && !updateIfExist) {
							//键重复冲突
//							System.err.println("写入数据库错误:行:"+(row+1));
							importError(row+1, "[此行已跳过] 写入数据库错误,有重复数据   提示:请注意 导入数据的主键 是否和 数据库中的条目冲突    报错信息:"+e.getMessage(), importAllErr);
						}else {
//							System.err.println("导入错误,写入数据库错误:行:"+(row+1));
							importError(row+1, "写入数据库错误 报错信息:"+e.getMessage().split(";")[0], importAllErr);
						}
						
						if(errInterrupt) {
							wb.close();
							sql.closeDB();
							return -6;
						}else {
							continue;
						}
					}
					
					
				}
				
				
				
			}else {
				//表为空
				importError(-1, "工作簿的表1无法打开", importAllErr);
				wb.close();
				return -2;
			}

//刷新进度
ProcessCtrlTools.setProcess(fileid, 100.00);
			
			wb.close();
			sql.closeDB();
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			importError(-1, "服务器内部错误,请联系管理员", importAllErr);
			sql.closeDB();
			try {
				wb.close();
			} catch (IOException e1) {
			}
			return -9;
		}
		return 0;
	}
	
	/**
	 * 导出到sql专用
	 * 按照指定的类型获取表格中的数据 
	 * @param outString getCell2String(cell)中提取出来的文本 
	 * @param targetType 0字符串 1双精度小数 2整数 3时间戳
	 * @return 按照类型返回指定的对象 错误一律返回null
	 */
	public static String getCellContent(String outString,int targetType) {
		if(outString==null) return null;
		switch (targetType) {
		case 0:
			//String
			try {
				return "\""+outString+"\"";
			}catch(Exception e) {
				e.printStackTrace();
				return null;
			}
		case 1:
			//Double

			case 2:
				//int

			case 3://timestamp
				if(outString.equals(""))return null;
			return outString;

			
		default:
			System.err.println("getCellContentgetCellContent Error 找不到制定的typeid:"+targetType);
			break;
		}
		
		
		return null;
	}
	/**
	 * 导出到sql专用
	 * 按照指定的类型获取表格中的数据 
	 * @param cell
	 * @param targetType 0字符串 1双精度小数 2整数 3时间戳
	 * @return 按照类型返回指定的对象 错误一律返回null
	 */
	public static String getCellContent(Cell cell,int targetType) {
		return getCellContent(getCell2String(cell), targetType);
	}
	
	/**
	 * 获取错误的列表
	 * @return JSONArray 没有错误时为null  成员为JSONObject数据  {"r":行,"m":信息}
	 */
	public JSONArray getErrorArray() {
		if(importAllErr.length()==0)
			return null;
//		System.err.println("正在导出错误条数:"+importAllErr.length());
		return importAllErr;
	}
	
	/**
	 * 获取整合后的错误行JSONObject数据  {"r":行,"m":信息}
	 * @param row
	 * @param msg
	 * @return JSONObject
	 */
	public static JSONObject importError(int row, String msg) {
		JSONObject err = new JSONObject();
		err.put("r",row);
		err.put("m", msg);
//		System.err.println("导入错误 行:"+row+" 信息:"+msg);
		return err;
	}
	public static JSONObject importError(int row, String msg,JSONArray json) {
		JSONObject a = importError(row, msg);
		json.put(a);
		return a;
	}


	
	public static void main(String[] args) {
		JSONObject retj = new JSONObject("{\"status\":0,\"errMsg\":\"\",\"retData\":\"\"}");
		
		JSONArray fPair = tools.getJSONArrayFromFile("2134.fPair", retj);
		if(fPair==null) {
			System.err.println("fpair导入失败");
			System.err.println(retj);
			return;
		}
		
		JSONObject additionalData = new JSONObject("{\"month\":201912,\"operator_id\":10001}");
		
		
		
		
		Excel2SqlUtil ex = new Excel2SqlUtil();
		
		int rs = ex.excel2sql("a.xlsx", fPair, additionalData,true,true,"123");
		
		
		System.out.println("运行结果:"+rs);
		
		JSONArray importErr = ex.getErrorArray();
		
		if(importErr!=null) {
			//有错误
			System.err.println("导入有错误 数量:"+importErr.length());
			for (int i = 0; i < importErr.length(); i++) {
				System.err.println("错误["+i+"] "+importErr.getJSONObject(i));
			}
			
		}else {
			System.out.println("本次导入无错误");
		}
		
	}
}
