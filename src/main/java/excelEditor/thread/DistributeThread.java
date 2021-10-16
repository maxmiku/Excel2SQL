package excelEditor.thread;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.List;

import config.Config;
import org.json.JSONObject;

/**
 * 分发任务控制线程
 * @author lexuan
 *
 */
class DistributeThread extends ThreadUtil implements Runnable {
	
	/**
	 * 最大线程数量
	 */
	final int MAX_THREAD = Config.EXCEL_MAX_IMPORT_THREADS;
	
	/**
	 * 最大运行时间 单位 毫秒  ms
	 */
	final int MAX_TIMEOUT = 300000;
	
	/**
	 * 休眠时间 单位 毫秒  ms
	 */
	final int SLEEP_TIME = 10000;
	
	/**
	 * 模板线程
	 */
	WorkThread TEMPLATE = new WorkThread();
	
	
	/**
	 * 各线程对象
	 */
	public List<Thread> threads = new ArrayList<Thread>();
	
	/**
	 * 各线程开始时间
	 */
	public List<Long> threadTime = new ArrayList<Long>();
	
	/**
	 * 请求队列
	 */
	List<JSONObject> threadQueue = new ArrayList<JSONObject>();
	
	/**
	 * 空闲开始时间
	 */
	Long freeTimeStart = 0L;
	
	/**
	 * 停止信号
	 */
	boolean stop = false;
	
	
    /**
     * 添加请求到队列
     * @param data
     */
	public void addQueue(JSONObject data) {
		threadQueue.add(data);
		resumeThread();
	}
	
	/**
	 * 中断所有子线程
	 */
	public void stopDistribute() {
		System.out.println("分发线程正在终止 - 准备结束工作线程");
//		pauseThread();
		stop=true;
		for(int i=threads.size()-1;i>=0;i--) {
			Thread nthread = threads.get(i);
			if(nthread.getState()==State.TERMINATED) {
				//程序已终止
//				System.out.println("线程"+i+" 已终止");
				threads.remove(i);
				threadTime.remove(i);
				continue;
			}else {
				//正在运行中
				System.err.println("分发线程即将停止,正在终止线程 "+i+" ,已发出终止信号");
				nthread.interrupt();//发出终止信号
			}
		}
		System.out.println("已结束所有工作线程");
	}
	
	
	//主循环体
	@Override
	void loop() {
		if(stop) {
			pauseThread();
			return;
		}
		if(threads.size()!=threadTime.size()) {
			//两个列表不匹配
			threadTime.clear();//清空
			for(int i=0;i<threads.size();i++) {
				threadTime.add(System.currentTimeMillis());
			}
		}
		
		if(threadQueue.size()>0 && threads.size()<MAX_THREAD) {
			//有活干
			Thread nthread = new Thread(TEMPLATE);
			nthread.setName(threadQueue.get(0).toString());
			threadQueue.remove(0);
			threads.add(nthread);
			threadTime.add(System.currentTimeMillis());
//			System.out.println("线程"+(threads.size()-1)+"启动");
			nthread.start();
		}
		
		if(threads.size()!=0) {
			//检查是否超时
			if(freeTimeStart!=0L)
				freeTimeStart=0L;//重置时间
			for(int i=threads.size()-1;i>=0;i--) {
				Thread nthread = threads.get(i);
				if(nthread.getState()==State.TERMINATED) {
					//程序已终止
//					System.out.println("线程"+i+" 已终止");
					threads.remove(i);
					threadTime.remove(i);
					continue;
				}else {
					//正在运行中
					if((System.currentTimeMillis()-threadTime.get(i))>MAX_TIMEOUT) {
						//超时
						System.err.println("线程"+i+" 超时了,已发出终止信号");
						nthread.interrupt();//发出终止信号
					}
				}
			}
		}else {
			if(threadQueue.size()==0) {
				//程序空闲
				if(freeTimeStart==0L) {
					freeTimeStart=System.currentTimeMillis();
				}else {
					if((System.currentTimeMillis()-freeTimeStart)>SLEEP_TIME) {
						//已达到线程休眠标准 重置时间 休眠
//						System.out.println("分配线程进入休眠模式");
						freeTimeStart=0L;
						pauseThread();
					}
				}
			}else {
				if(freeTimeStart!=0L)
					freeTimeStart=0L;//重置时间
			}
		}
	}
}
