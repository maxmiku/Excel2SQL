package excelEditor.thread;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import tools.ProcessCtrlTools;
import tools.tools;
import utils.Excel2SqlUtil;

/**
 * 工作线程
 * @author lexuan
 *
 */
class WorkThread implements Runnable{
	

	@Override
	public void run() {
		String name = Thread.currentThread().getName();
		System.out.println("工作线程启动-"+name);
		String fileid = null;
		try {
			JSONObject inj = new JSONObject(name);
			System.out.println("参数"+inj);
			
			
			
			//解开传入数据
			fileid = inj.getString("fileid");
			String sePath = inj.getString("sePath");
			String fieldPairid = inj.getString("fieldPairid");
			boolean update = inj.getBoolean("update");
			boolean errIntreeupt = inj.getBoolean("errInterrupt");
			JSONObject additionalData = (inj.get("additionalData")!=null) ? inj.getJSONObject("additionalData") : null;
			
			
			
			//正在转换状态
			ProcessCtrlTools.setStatus(fileid, ProcessCtrlTools.STATUS_CONVERTING);
			
			
			
			
			
			
			
			
			
			
			//导入excel主体
			JSONObject tmpretj = new JSONObject("{\"status\":0,\"errMsg\":\"\",\"retData\":\"\"}");
			
			JSONArray fPair = tools.getJSONArrayFromFile(sePath+"/"+fileid+"."+fieldPairid+".fPair", tmpretj);
			if(fPair==null) {
				//打不开fPair文件
				ProcessCtrlTools.addError(fileid,Excel2SqlUtil.importError(-1, "服务器内部错误: 无法获取您上传的配对字段对的信息,请尝试重新上传文件. 错误信息:"+tmpretj.getString("errMsg")));
			}else {
				//可以打开
				Excel2SqlUtil ex = new Excel2SqlUtil();
				
				int rs = ex.excel2sql(sePath+"/"+fileid, fPair, additionalData,update,errIntreeupt,fileid);
				
				
				System.out.println("运行结果:"+rs);
				
				if(rs!=0) {
					ProcessCtrlTools.addError(fileid, Excel2SqlUtil.importError(-1, "[错误] 程序返回"+rs+" 请记录下错误码并联系管理员"));
				}
				
				JSONArray importErr = ex.getErrorArray();
				
				if(importErr!=null) {
					//有错误
					System.err.println("导入有错误 数量:"+importErr.length());
					
					
					ProcessCtrlTools.addError(fileid, importErr);
					
				}else {
//					System.out.println("本次导入无错误");
				}
				
			}
			
			

			//刷新进度
			ProcessCtrlTools.setProcess(fileid, 100.00);
			ProcessCtrlTools.setStatus(fileid, ProcessCtrlTools.STATUS_COMPLETE);
			
			
			System.out.println("工作线程正常结束-"+name);
		} catch (Exception e) {
			e.printStackTrace();
			ProcessCtrlTools.addError(fileid, Excel2SqlUtil.importError(-99, "[错误] 程序错误"+e.getMessage()+" 请记录下错误码与信息并联系管理员"));
			ProcessCtrlTools.setProcess(fileid, 100.00);
			ProcessCtrlTools.setStatus(fileid, ProcessCtrlTools.STATUS_ERROR);
		}finally {
			System.out.println("工作线程被移除-"+name);
		}
		
	}
	
	
	
}
