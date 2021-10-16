package excelEditor.thread;

import java.awt.AWTException;
import java.awt.Robot;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import tools.ProcessCtrlTools;

/**
 * 导入多线程操作类 
 * 整合 导入类 状态类 线程类
 * @author lexuan
 *
 */
public class ImportThreadCtrlTool {
	
	static DistributeThread dt = new DistributeThread();
	/**
	 * 分发线程
	 */
	static Thread dctl = new Thread(dt);
	
	/**
	 * 暂停服务标志
	 */
	static boolean pause = true;
	
	
	/**
	 * 将请求添加到队列
	 * @param fileid 文件id
	 * @param sePath session目录的路径
	 * @param fieldPairid 
	 * @param additionalData 每条数据附加的字段数据 一般操作用户数据月份之类的统一字段使用  JSON的 键为字段名 值为对应字段名的值
	 * @param update 存在时更新
	 * @param errInterrupt 错误即中断,默认不中断继续
	 * @return -1 fileid已在转换或者已转换 --> 前端显示进度    -2删不掉进度-->建议重试  -3 处理停止
	 */
	public static int addToQueue(String fileid, String sePath, String fieldPairid, JSONObject additionalData, boolean update, boolean errInterrupt) {
		if(pause) {
			return -3;
		}
		
		if(!ProcessCtrlTools.addStatus(fileid)) {
			//已存在项目
			if(!(ProcessCtrlTools.getProcess(fileid)==ProcessCtrlTools.STATUS_INQUEUE)) {
				//不在队列 错误
				return -1;
			}
			//还在队列中
			if(!ProcessCtrlTools.removeStatus(fileid))return -2;
			ProcessCtrlTools.addStatus(fileid);
		}
		
		ProcessCtrlTools.setStatus(fileid, ProcessCtrlTools.STATUS_INQUEUE);
		
		JSONObject inj = new JSONObject();
		
		if(additionalData!=null) 
			inj.put("additionalData", additionalData);
		inj.put("fileid", fileid);
		inj.put("sePath", sePath);
		inj.put("fieldPairid", fieldPairid);
		inj.put("update", update);
		inj.put("errInterrupt", errInterrupt);
		
		dt.addQueue(inj);
		return 0;
	}
	
	/**
	 * 获取指定文件id的进度
	 * @param fileid
	 * @return 100为最大 -1.0为找不到 -2.0队列列中未开始
	 */
	public static Double getProcess(String fileid) {
		if(ProcessCtrlTools.getStatus(fileid)==ProcessCtrlTools.STATUS_INQUEUE) {
			return -2.0;
		}
		return ProcessCtrlTools.getProcess(fileid);
	}
	
	
	/**
	 * 启动分发线程
	 */
	public static void startDThread() {
		System.out.println("分发线程启动");
		dctl.start();
		pause=false;
	}
	
	/**
	 * 终止分发线程
	 */
	public static void stopDthread() {
		System.out.println("分发线程正在终止");
		pause=true;
		dt.stopDistribute();
		dctl.interrupt();
	}

	public static void main(String[] args) throws AWTException {
//		DistributeThread ctl = new DistributeThread();
//		Thread ctlThread = new Thread(ctl);

		Robot a = new Robot();
		System.out.println("启动线程");
		startDThread();
		
		a.delay(1000);
		System.out.println("分配任务");
		for (int i = 0; i < 4; i++) {
			System.out.println(""+i+"加入结果:"+addToQueue(""+i, "", "", null, true, false));
		}
		a.delay(3000);
		System.out.println("终止线程");
		stopDthread();
	}
}


