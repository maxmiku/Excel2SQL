package excelEditor.api;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

/**
 * 确定字段配对并返回
 * @author lexuan
 */
@WebServlet("/confirmField")
public class ConfirmField extends HttpServlet{

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/**
		 * 
		 * 传入JSON字符串  并带有fieldPair键的数组
		 * 
		 * @return -1解json失败  -2获取参数时出错  -3找不到之前上传的字段对   -4内部错误
		 * 
		 * confirmStatus  0成功   1取消 
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
    	if(!inj.has("confirm")) {
    		retj.put("status", -2);
			retj.put("errMsg", "非法请求,获取请求参数时错误");
			out.print(retj);
			return;
    	}
    	JSONObject retData = new JSONObject();
    	String sePath = null;
    	String fileid = null;
    	String fieldPairid = null;
    	boolean update = true;
    	boolean errInterrupt = false;
    	int datamonth = -1;
    	try {
	    	sePath = (String)se.getAttribute("exedit_uploadPath");
	    	fileid = inj.getString("fileid");
	    	fieldPairid = inj.getString("fieldPairid");
	    	update = inj.getBoolean("update");
	    	errInterrupt = inj.getBoolean("errInterrupt");
	    	datamonth = inj.getInt("month");
    	}catch (Exception e) {
    		retj.put("status", -2);
			retj.put("errMsg", "非法请求,获取请求参数时错误");
			out.print(retj);
			return;
		}
    	try {
    		File fpfile = new File(sePath+"/"+fileid+"."+fieldPairid+".fPair");
    		if(!fpfile.exists()) {
    			retj.put("status", -3);
    			retj.put("errMsg", "找不到您先前设置的 字段匹配数据,请重新设置.");
    			out.print(retj);
    			return;
    		}
        	if(inj.getBoolean("confirm")) {
        		//确定字段配置 开始分配排队导入
        		
        		//附加信息
        		JSONObject additionalData = new JSONObject();
        		additionalData.put("month", datamonth);
        		additionalData.put("operator_id", req.getSession().getAttribute("uid"));
        		additionalData.put("operation_id", fileid.replace(".xlsx", "").replace(".xls", ""));
        		
        		retData.put("confirmStatus",excelEditor.thread.ImportThreadCtrlTool.addToQueue(fileid, sePath, fieldPairid, additionalData, update, errInterrupt));
        		
        	}else {
        		fpfile.delete();
        		retData.put("confirmStatus",1);
        	}
			
		} catch (Exception e) {
			e.printStackTrace();
			retj.put("status", -4);
			retj.put("errMsg", "确认字段 时服务器内部错误");
			out.print(retj);
			return;
		}
    	
    	
    	
    	retj.put("retData",retData);
		
		out.print(retj);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
