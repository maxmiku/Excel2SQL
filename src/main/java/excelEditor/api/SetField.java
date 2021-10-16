package excelEditor.api;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import utils.SQLImportUtil;
import utils.ExcelUtil;

@WebServlet("/setField")
public class SetField extends HttpServlet{
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/**
		 * 
		 * 传入JSON字符串  并带有fieldPair键的数组
		 * 
		 * @return -1解json失败   -2没有fieldPair键  -3excel方法抛出错误  -4filePair保存错误  -11表无数据  -12空表   -19内部错误  -20文件不支持
		 * 
		 * 
		 */
		
		req.setCharacterEncoding("UTF-8");
    	resp.setContentType("text/html;charset=UTF-8");
    	HttpSession se = req.getSession();
    	JSONObject retj = new JSONObject();
    	retj.put("status", 0);
		retj.put("errMsg", "");
		retj.put("retData", "");
		PrintWriter out = resp.getWriter();
		
		if(!CheckAuth.check(req, resp, retj)) {
			out.print(retj);
			return;
		}
    	
    	JSONObject inj = null;
    	try {
			inj = new JSONObject(utils.Request2StringUtil.getRequestPostStr(req));
		} catch (Exception e) {
			retj.put("status", -1);
			retj.put("errMsg", "非法请求");
			out.print(retj);
			return;
		}
    	if(!inj.has("fieldPair") || !inj.has("fileid") || !inj.has("allData") || !inj.has("allowDefault")) {
    		retj.put("status", -2);
			retj.put("errMsg", "非法请求");
			out.print(retj);
			return;
    	}
    	JSONArray fieldPair = inj.getJSONArray("fieldPair");
    	String fieldPairid = tools.tools.getRandomString(8);
    	boolean allData = inj.getBoolean("allData");//获取全部数据
		boolean allowDefault = inj.getBoolean("allowDefault");//允许使用默认值
    	JSONObject retData = new JSONObject();
    	
		
		
		String sePath = (String)se.getAttribute("exedit_uploadPath");
		String fileid = inj.getString("fileid");
		
		//存fieldPair到文件
		try {
			File fpfile = new File(sePath+"/"+fileid+"."+fieldPairid+".fPair");
			if(!fpfile.exists()) {
				fpfile.createNewFile();
			}
			
			FileWriter fw = new FileWriter(sePath+"/"+fileid+"."+fieldPairid+".fPair");
			fw.write(fieldPair.toString());
			fw.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			retj.put("status", -4);
			retj.put("errMsg", "服务器无法保存你的字段设置,请重试或联系管理员");
			out.print(retj);
			return;
		}
		
		
		JSONArray expData = new JSONArray();
		JSONArray headData = new JSONArray();
		
		SQLImportUtil ex = new SQLImportUtil();
		
		expData = ex.getExampleImport(fieldPair, sePath+"/"+fileid, headData,allData, retj, allowDefault);
		
		if(expData == null) {
			retj.put("status", retj.getInt("status")-10);
			out.print(retj);
			return;
		}
		
		retData.put("headData", headData);
		retData.put("formData", expData);
		retData.put("fileid", fileid);
		
		retData.put("fieldPairid", fieldPairid);
		se.setAttribute("exedit_fieldPairid", fieldPairid);
		
//		//开始解excel的头
//		ExcelUtil ex = new ExcelUtil();
//		JSONArray fheader = null;
//		
//		try {
//			fheader = ex.getExcelHeader(sePath+"/"+fileid, retj);
//		} catch (Exception e) {
//			e.printStackTrace();
//			retj.put("status", -3);
//			retj.put("errMsg","服务器内部错误:获取表头时出错");
//			out.print(retj);
//			return;
//		}
		
		
		
		
		
		retj.put("retData",retData);
		
		out.print(retj);
	}
}
