package excelEditor.api;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import tools.ProcessCtrlTools;

@WebServlet("/getProcess")
public class GetProcess extends HttpServlet{
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/**
		 * 
		 * 传入JSON字符串  并带有fileid键
		 * 
		 * @return -1解json失败  -2获取参数时出错
		 * 
		 * process 100为最大 -1.0为找不到 -2.0队列列中未开始  -3.0已结束
		 * importResult 结束后会有结果对象
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
    	if(!inj.has("fileid")) {
    		retj.put("status", -2);
			retj.put("errMsg", "非法请求");
			out.print(retj);
			return;
    	}
    	String fileid = inj.getString("fileid");
//    	Double rs = ProcessCtrlTools.getProcess(fileid);
    	JSONObject retData = new JSONObject();
    	JSONObject rs = ProcessCtrlTools.getProcessNErrCount(fileid);
    	retData.put("ec",-1);
    	retData.put("process",-2.0);
    	if(rs==null) {
    		retData.put("process",-1.0);
    	}else {
			int nowStatus = ProcessCtrlTools.getStatus(fileid);


    		if(rs.getDouble("p")==100.0) {
        		if(nowStatus==ProcessCtrlTools.STATUS_COMPLETE) {
        			retData.put("importResult", ProcessCtrlTools.getStatusObject(fileid, true));
        			retData.put("process",-3.0);
        			retData.put("ec",rs.getDouble("ec"));
        		}else if(nowStatus==ProcessCtrlTools.STATUS_ERROR){
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					retData.put("process",-4.0);
					retData.put("ec",rs.getDouble("ec"));
					retData.put("importResult", ProcessCtrlTools.getStatusObject(fileid, true));

				}else{
        			retData.put("process",rs.getDouble("p"));
        			retData.put("ec",rs.getDouble("ec"));
        		}
        	}else if(rs.getDouble("p")==0.0){
        		if(nowStatus==ProcessCtrlTools.STATUS_INQUEUE) {
        			retData.put("process",-2.0);
            		retData.put("ec",0);
            		retData.put("ql",ProcessCtrlTools.getQqueueLength(fileid));
        		}else {
        			retData.put("process",rs.getDouble("p"));
            		retData.put("ec",rs.getDouble("ec"));
        		}
        		
        	}else{
        		retData.put("process",rs.getDouble("p"));
        		retData.put("ec",rs.getDouble("ec"));
        	}
    	}
    	
    	retData.put("fileid", fileid);
    	
    	
    	retj.put("retData",retData);
    	
    	out.println(retj);
	}
}
