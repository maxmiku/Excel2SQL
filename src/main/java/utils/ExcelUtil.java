package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import config.Config;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;


public class ExcelUtil {
	
	public ExcelUtil() {
		//使用默认的数据
		
//		System.out.println("excel 构造");
		this.recommandList = new JSONArray(Config.excel_recommand);
		this.editableField = new JSONArray(Config.excel_editable);
//		this.recommandList = new JSONArray(recommand);
//		this.editableField = new JSONArray(editable);
	}
	
	
	//推荐列表
	/*
	 * 格式:每个成员{"ks":["学号","号"],"rid":2} 
	 * ks:关键字keys的数组
	 * rid:推荐的字段id
	 * 
	 */
	public JSONArray recommandList = new JSONArray();
	
	//数据库可编辑字段列表
	/*
	 * 格式:每个成员{"id":0,"n":"student_num","type":"VARCHAR"}
	 * 类型 TIMESTAMP  INT  VARCHAR  DOUBLE
[
	{"id":0,"n":"real_name","t":"VARCHAR","c":"文本"},
	{"id":1,"n":"student_id","t":"VARCHAR","c":"文本"},
	{"id":2,"n":"department","t":"VARCHAR","c":"文本"},
	{"id":3,"n":"post","t":"VARCHAR","c":"文本"},
	{"id":4,"n":"wage","t":"DOUBLE","c":"双精度小数"},
	{"id":5,"n":"hour","t":"DOUBLE","c":"双精度小数"},
	{"id":6,"n":"teacher","t":"VARCHAR","c":"文本"},
	{"id":7,"n":"month","t":"INT","c":"整数"}
]
	 */
	public JSONArray editableField = new JSONArray();
	
	
	//excel表格的行数
	public int excelRows = 0;
	
	
	public ExcelUtil(JSONArray recommandList,JSONArray editableField) {
		/**
		 * 传入键值推荐列表 和 可编辑字段名列表
		 * ks越往后越优先
[
	{"ks":["姓名"],"rid":0},
	{"ks":["学号"],"rid":1},
	{"ks":["部门"],"rid":2},
	{"ks":["岗位"],"rid":3},
	{"ks":["工资"],"rid":4},
	{"ks":["工时"],"rid":5},
	{"ks":["考核老师"],"rid":6}
]

		 * 
		 */
		this.recommandList=recommandList;
		this.editableField=editableField;
	}
	
	
	
	
	public JSONArray getEditableField() {
		return editableField;
	}
	
	public JSONObject getEditableField(int id) {
		int max = editableField.length();
		for(int i=0;i<max;i++) {
			int now = i+id;
			if(now>max-1) {
				now=now%max;
			}
			JSONObject tj = editableField.getJSONObject(now);
			if(tj.getInt("id")==id) {
				return tj;
			}
			
		}
		return null;
	}

	/**
	 * 导出excel到json
	 *
	 * @Param 	exfile excel文件路径     limit 读取的行数
	 * 			jsfile 导出到的json文件路径
	 * 			jsout 返回存有导出数据的json对象
	 * 			errj 报错时的文本
	 *
	 * @return  errj value 0成功  -1表无数据  -2无法打开表  -3无法读取列数据   -9内部错误  -10文件不支持
	 */
	public JSONArray getExcelHeader(String exfile, JSONObject errj) {

		
		JSONArray retj = new JSONArray();
		
		
		try {
			//打开工作簿
			Workbook wb = openExcel(exfile,errj);
			if (wb==null) {
				//文件打开失败
				errj.put("status", -9);
				errj.put("errMsg", "内部错误:文件打开失败");
				return null;
			}
			
			//只获取第一张表
			Sheet et = wb.getSheetAt(0);

			if(et!=null) {
				//表不为空
				// 获得行数
				int rows = et.getPhysicalNumberOfRows();
				excelRows=rows;
				//首行
				Row headerRow = et.getRow(0);
				// 获得列数
				int cols = headerRow.getPhysicalNumberOfCells();
				if(rows<2) {
					//行数小于2 无法获取列类型
					errj.put("status", -1);
					errj.put("errMsg", "工作表中没有数据,请添加数据后再导入");
				}else {
					Row contentRow = et.getRow(1);
					
					for (int i = 0; i < cols; i++) {
						JSONObject tmpj = new JSONObject();
						Cell headCell = headerRow.getCell(i);
						Cell contentCell = contentRow.getCell(i);
						if(headCell==null && contentCell==null) {
							continue;
						}

//						System.out.println("第"+i+"列 名称:"+headCell.getStringCellValue()+"  类型:"+getCellTypeChs(contentCell));
						try {
							tmpj.put("id", i);
							tmpj.put("n",headCell.getStringCellValue());
							if(contentCell==null){
								//内容格为空
								tmpj.put("t","Unknown");
								tmpj.put("c","未知");
							}else{
								tmpj.put("t",contentCell.getCellType().toString());
								tmpj.put("c",getCellTypeChs(contentCell));
							}
							tmpj.put("r",-1);
							retj.put(tmpj);
						} catch (Exception e) {
							e.printStackTrace();
							errj.put("status", -3);
							errj.put("errMsg","获取excel表头时出错: 无法获取第 "+(i+1)+" 列的数据,请检查excel表中该列的 前两行 \n确保不为空 或 一二行全为空(不导入该行)");
							return null;
						}
						
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
			
//			System.out.println("获取表头完成:"+retj);
					
					
					
			setRecommandID(retj);
			
			
//			System.out.println("获取推荐完成:"+retj);
					
//			System.out.println();
//			System.out.println("最终成果:"+retj);	
			
		}catch(Exception e) {
			e.printStackTrace();
			errj.put("status", -9);
			errj.put("errMsg","内部错误:获取excel表头时出错");
		}
		
		
		
		return retj;
	}
	
	/**
	 * 获取推荐字段
	 * @param formFiled 需要添加推荐字段JSONArray
	 * @return 已经添加了推荐数据的JSONArray
	 */
	public void setRecommandID(JSONArray formFiled) {
		
		//推荐数
		int recoNum = recommandList.length();
		//表项数
		int formNum = formFiled.length();
//		System.out.println("正在匹配推荐,推荐项目数:"+recoNum+" 表项数:"+formNum);
		
		
		
		for(int i = 0; i < recoNum; i++) {
//			System.out.println(recommandList);
			JSONObject nowReco = recommandList.getJSONObject(i);
			JSONArray keys = nowReco.getJSONArray("ks");
			
//			System.out.println("正在搜索匹配:第"+i+"项推荐列表 共"+recoNum+"项");
			
			for(int i1 = keys.length()-1;i1>=0;i1--) {
				boolean found = false;
//				System.out.println("正在搜索匹配:第"+i1+"项ks 共"+keys.length()+"项  "+"第"+i+"项推荐列表 共"+recoNum+"项");
				int leave = formFiled.length();
				for (int j = 0; j < leave; j++) {
					JSONObject now = formFiled.getJSONObject(j);
					if(now.getInt("r")!=-1) {
						//已有数据
						continue;
					}
					if(now.getString("n").replace(" ","").indexOf(keys.getString(i1))!=-1){
						//找到
						// TODO 这里可以到时候加个开关模糊选项
						found=true;
						now.put("r", nowReco.getInt("rid"));
						formFiled.put(j,now);//更新第j项
						break;
					}	
				}
				if(found) {
					break;
				}
			}
			
		}
	}
	
	/**
	 * 按照不同类型的excel打开excel文件workbook
	 */
    public static Workbook openExcel(String filePath,JSONObject errj){
    	
        if(filePath==null){
        	errj.put("status ", -9);
        	errj.put("errMsg", "内部错误,未传入文件名");
            return null;
        }
        String extString = filePath.substring(filePath.lastIndexOf("."));
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
            if(".xls".equals(extString)){
                return new HSSFWorkbook(is);
            }else if(".xlsx".equals(extString)){
                return new XSSFWorkbook(is);
            }else{
            	errj.put("status ", -10);
            	errj.put("errMsg", "文件不支持");
                return null;
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        errj.put("status ", -9);
    	errj.put("errMsg", "内部错误:打开excel文件时出错");
        return null;
    }
    
    /**
	 * 获取cell类型的中文文本
	 */
    public static String getCellTypeChs(Cell cell) {
    	
    	CellType type = cell.getCellType();

    	switch(type){
			case STRING:return "文本";
			case NUMERIC:return "数值";
			case BLANK:return "空白未知";
			case FORMULA:return "公式";
			case _NONE:return "未知";
			case BOOLEAN:return "布朗值";
			case ERROR:return "错误";
			default:return type.toString();
		}

//    	if(type.equals(CellType.STRING)) {
//			return "文本";
//    	}else if(type.equals(CellType.NUMERIC)) {
//    		return "数值";
//    	}case "FORMULA":
//		return "公式";
//		case "BLANK":
//		return "未知";
//
//    	return type.toString();
    }
    
    public static String getFieldTypeChs(String type) {
    	switch (type) {
		case "VARCHAR":
			return "文本";
		case "INT":
			return "整数";
		case "DOUBLE":
			return "双精度小数";
		case "TIMESTAMP":
			return "时间戳";
		default:
			return type;
		}
    	
    }
	
	
	public static void main(String[] args) throws IOException {
//		intest("a.xlsx");

	}
	
	/**
	 * @author lexuan
	 * 测试方法
	 */
	static JSONObject intest(String fileid,JSONObject retj,HttpServletRequest req) {
		
//		JSONObject retj = new JSONObject();
//		retj.put("status", 0);
//		retj.put("errMsg", "");
//		retj.put("retData", "");
		
		
		ExcelUtil ex = new ExcelUtil();
		
		
		JSONObject retData = new JSONObject();
		retData.put("fileid", fileid);
		
		
		retData.put("fheader",ex.getExcelHeader(tools.tools.fileid2path(fileid,req), retj));
		retData.put("excelRows", ex.excelRows);
		retData.put("dheader", ex.getEditableField());
		
		retj.put("retData",retData);
		
		System.out.println("Finally:\n"+retj);
		return retj;
	}

}
