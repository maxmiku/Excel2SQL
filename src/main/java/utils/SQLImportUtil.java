package utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.sun.org.apache.bcel.internal.generic.ARRAYLENGTH;
import org.json.JSONArray;
import org.json.JSONObject;


public class SQLImportUtil extends ExcelUtil{
	
	/**
	 * 获取示例数据 默认十条 少的话可以更少
	 * @param fieldPArray 字段名匹配表
	 * @param exfile excel文件路径
	 * @param dataHeader (可选)回调当前示例数据的表头 为null时不获取  
	 * @param wholeForm 获取整块表的示例数据
	 * @param errj 回调错误代码
	 * @param allowDefault 允许使用默认值
	 * @return JSONArray 返回数据的列表
	 * 
	 */
	public JSONArray getExampleImport(JSONArray fieldPArray, String exfile , JSONArray dataHeader, boolean wholeForm, JSONObject errj, boolean allowDefault) {
		
		
		JSONArray retj = new JSONArray();
		
		try {
			//打开工作簿
			Workbook wb = openExcel(exfile,errj);
			if (wb==null) {
				//文件打开失败
				return null;
			}
			//只获取第一张表
			Sheet et = wb.getSheetAt(0);
			if(et!=null) {
				//表不为空
				//报文中的项目数
				int requestCols=fieldPArray.length();
				
				
				
				// 获得行数
				int rows = et.getPhysicalNumberOfRows();
				excelRows=rows;
				
				if(rows<2) {
					//行数小于2 无法获取列类型
					errj.put("status", -1);
					errj.put("errMsg", "工作表中没有数据,请添加数据后再导入");
					wb.close();
					return null;
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
				
				if(dataHeader!=null)
					dataHeader.put("行号");
				
				for (int i = 0; i < requestCols; i++) {
					JSONObject itemj =  fieldPArray.getJSONObject(i);
					if(itemj.getInt("tid")==-1) {
						//不导入该项
						continue;
					}
					
					JSONObject nowField = getEditableField(itemj.getInt("tid"));
					
					if(nowField==null) {
						errj.put("status", -3);
						errj.put("errMsg", "列id "+itemj.getInt("sid")+" 指向了不存在的字段id,请检查");
						wb.close();
						return null;
					}
					
					if(dataHeader!=null) {
						//获取表头
						dataHeader.put(nowField.getString("n"));
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

				//要获取的行数
				int getRows = 10;
				
				getRows = excelRows-1;

				//现在已有正常行数
				int nowDisRow = 0;
				
				for (int row = 1; row <= getRows; row++) {
//					System.out.println("当前第"+row+"行");
					JSONArray rowj = new JSONArray();
					
					boolean suspectedNullRow = true;//疑似空行标志
					
					boolean rowError = false;//错误行标志
					
					//加入行号
					Row r = null;

					//获取整张表的数据
					r = et.getRow(row);
					rowj.put(""+(row+1));
				
					if(r==null) {
						//跳过无法获取的行
						System.err.println("行"+row+" 为null");
						continue;
					}
					
					for (int j = 0; j < changeCols; j++) {
//						System.out.println(
//								"当前行:"+row+
//								" 当前列id:"+j+
//								" 当前列:"+colid.get(j)+
//								" 当前列对应字段类型:"+fieldType.get(j)+
//								" 当前列对应的字段id:"+fieldid.get(j)+
//								" 当前字段名:"+fieldName.get(j));
						Cell cell = null;
						
						cell = r.getCell(colid.get(j));
						
						
						Object tmp = getCellContent(cell,fieldType.get(j));
						if(tmp==null) {
//							System.out.println("tmp为null break行:"+rowj.getString(0));


							if(allowDefault && fieldDefault.get(j)!=null){
								//该列有默认数据
								tmp = getCellContent(cell,fieldType.get(j),fieldDefault.get(j));
								if(tmp==null){
									rowError=true;
									break;
								}
							}else{
								rowError=true;
								break;
							}

						}else {
//							System.out.println("cell=null:"+(cell==null)+" type!=blank:"+(cell.getCellType()!=CellType.BLANK)+" tmp:"+tmp);
							if(!(cell!=null?(cell.getCellType()==CellType.BLANK):true)){
								//单元格不为空
								suspectedNullRow = false;
//								System.out.println("单元格不为空");
							}
						}
						
						rowj.put(tmp);
					}
//					System.out.println("行:"+row+" 疑似空行状态:"+suspectedNullRow);
					
					
					if(!suspectedNullRow) {
						//不为空行
						
						if(wholeForm) {
							retj.put(rowj);
						}else {
							if(rowError) {
								//行有错误
								retj.put(rowj);
							}else {
								//行没有错误
								if(nowDisRow<11) {
									nowDisRow++;
									retj.put(rowj);
								}
							}
						}
						
//						System.out.println("不为空行 "+rowj.getString(1)+" 加入列表:"+rowj.getString(0));
					}
					
				}
				
				
			}else {
				//表为空
				errj.put("status", -2);
				errj.put("errMsg", "工作簿的表1无法打开");
				wb.close();
				
				return null;
			}
			
			wb.close();
		} catch (Exception e) {
			e.printStackTrace();
			errj.put("status", -9);
			errj.put("errMsg","内部错误:获取excel示例时出错");
		}
		
		
		return retj;
	}
	
	/**
	 * 获取表格中的数据数据字符串
	 * @param cell
	 * @return 按照字符串处理单元格, 当错误时返回null
	 */
	public static Object getCellContent(Cell cell) {
		return getCellContent(cell,0,null);
	}
	/**
	 * 获取表格中的数据数据字符串
	 * @param cell
	 * @param targetType 0字符串 1双精度小数 2整数 3时间戳
	 * @return 当错误时返回null
	 */
	public static Object getCellContent(Cell cell,int targetType) {
		return getCellContent(cell,targetType,null);
	}
	/**
	 * 按照指定的类型获取表格中的数
	 * @param cell
	 * @param targetType 0字符串 1双精度小数 2整数 3时间戳
	 * @param defaultValue 默认值 不启用使用null
	 * @return 按照类型返回指定的对象, 当错误时返回默认值
	 */
	public static Object getCellContent(Cell cell,int targetType,String defaultValue) {

		try {
			switch (targetType) {
				case 0:
					//String
					try {
						return getCell2String(cell);
					} catch (Exception e) {
//						e.printStackTrace();
						return defaultValue;
					}
				case 1:
					//Double
					try {
						String get = getCell2String(cell);
						return get.equals("") ? defaultValue==null?null:Double.parseDouble(defaultValue) : Double.parseDouble(get);
					} catch (Exception e) {
//						e.printStackTrace();
//						System.err.println("Double parse Error");
						if(defaultValue==null)return null;
						return Double.parseDouble(defaultValue);
					}
				case 2:
				case 3:
					//timestamp
					//int
					try {
						String get = getCell2String(cell);
						return get.equals("") ? defaultValue==null?null: Integer.parseInt(defaultValue) : Integer.parseInt(get);
					} catch (Exception e) {
//						e.printStackTrace();
						if(defaultValue==null)return null;
						return Integer.parseInt(defaultValue);
					}

				default:
					System.err.println("getCellContentgetCellContent Error 找不到制定的typeid:" + targetType);
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	public static String getCell2String(Cell cell) {
		try {
			if(cell==null){
//				System.err.println("cell为null");
				return "";
			}
//			System.out.println("类型:"+cell.getCellType());
			
			switch (cell.getCellType()) {
			case STRING:

//				System.out.println("s当前模式为字符串");
				return cell.getStringCellValue();
			case NUMERIC://整数 小数
//				System.out.println("s当前模式为数字");
				//解决科学计数法
//				CellStyle cellStyle=wb.createCellStyle();
//				DataFormat format = wb.createDataFormat();
//				
//				cellStyle.setDataFormat(format.getFormat("@"));
//				cell.setCellStyle(cellStyle);

				double a = cell.getNumericCellValue();
//				System.out.println( formatDouble(a) );
//				cell.setCellFormula(arg0);
//				cell.setCellType(CellType.STRING);
				return formatDouble(a);//解决科学计数法
			case BOOLEAN://布尔

//				System.out.println("s当前模式为布尔");
				return String.valueOf(cell.getBooleanCellValue());
			case FORMULA://公式

//				System.out.println("s当前模式为公式");
				return String.valueOf(cell.getNumericCellValue());
			default:

//				System.out.println("s当前模式为默认");
				return "";
			}
		}catch (Exception e) {

//			System.err.println("getCell2String 报错");
//			e.printStackTrace();
			
			return null;
		}
	}
	
	/**
	 * 解决double科学计数法的问题
	 * @param d 需要解除科学计数法的
	 * @return 经过去除科学计数法后的double字符串
	 */
	private static String formatDouble(double d) {
        NumberFormat format = NumberFormat.getInstance();
        //设置保留多少位小数
        format.setMaximumFractionDigits(20);
         // 取消科学计数法
        format.setGroupingUsed(false);
        //返回结果
        return format.format(d);
    }

	public static void main(String[] args) {
		JSONObject retj = new JSONObject();
		retj.put("status", 0);
		retj.put("errMsg", "");
		retj.put("retData", "");
		
		JSONArray fieldPair = new JSONArray("[{\"sid\":0,\"n\":\"姓 名\",\"tid\":0},{\"sid\":1,\"n\":\"学 号\",\"tid\":1},{\"sid\":2,\"n\":\"部 门\",\"tid\":2},{\"sid\":3,\"n\":\"岗 位\",\"tid\":3},{\"sid\":5,\"n\":\"工 资\",\"tid\":4},{\"sid\":4,\"n\":\"工 时\",\"tid\":5},{\"sid\":6,\"n\":\"考核老师\",\"tid\":6}]");
		
		JSONObject retData = new JSONObject();
		JSONArray expData = new JSONArray();
		JSONArray headData = new JSONArray();
		
		SQLImportUtil ex = new SQLImportUtil();
		
		expData = ex.getExampleImport(fieldPair, "a.xlsx", headData, false, retj,false);
		
		retData.put("headData", headData);
		retData.put("formData", expData);
		
		retj.put("retData", retData);
		
		
		System.out.println(retj);
//		System.out.println(Config.excel_editable);
		
	}

}
