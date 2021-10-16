package excelEditor.api;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import utils.ExcelUtil;

@WebServlet("/getExHeader")
public class GetExcelHrader extends HttpServlet{
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/**
		 * 
		 * 传入JSON字符串  并带有fileid键
		 * 
		 * @return -1解json失败   -2没有fileid键  -3excel方法抛出错误   -11表无数据  -12空表   -19内部错误  -20文件不支持
		 * 
		 * 
		 */
		
		req.setCharacterEncoding("UTF-8");
    	resp.setContentType("text/html;charset=UTF-8");
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
    	if(!inj.has("fileid") || inj.getString("fileid").equals("") || inj.getString("fileid").indexOf("/")!=-1 || inj.getString("fileid").indexOf("\\")!=-1) {
    		retj.put("status", -2);
			retj.put("errMsg", "非法请求");
			out.print(retj);
			return;
    	}
    	
    	JSONObject retData = new JSONObject();
    	
		
		
		String sePath = (String)req.getSession().getAttribute("exedit_uploadPath");
		String fileid = inj.getString("fileid");
		
		//开始解excel的头
		ExcelUtil ex = new ExcelUtil();
		JSONArray fheader = null;
		
		try {
			fheader = ex.getExcelHeader(sePath+"/"+fileid, retj);
		} catch (Exception e) {
			e.printStackTrace();
			retj.put("status", -3);
			retj.put("errMsg","服务器内部错误:获取表头时出错");
			out.print(retj);
			return;
		}
		
		
		
		if(fheader==null) {
			retj.put("status", retj.getInt("status")-10);
			out.print(retj);
			return;
		}
		retData.put("fileid", fileid);
		retData.put("fheader",fheader);
		retData.put("excelRows", ex.excelRows-1);
		retData.put("dfield", ex.getEditableField());
		
		retj.put("retData",retData);
		
//		System.out.println(retj);
		
		out.print(retj);
	}
}
