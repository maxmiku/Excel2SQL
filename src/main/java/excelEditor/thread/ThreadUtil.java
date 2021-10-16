package excelEditor.thread;

public class ThreadUtil implements Runnable{
	//用于唤醒线程时使用
	private final Object lock = new Object();
	//标志线程阻塞情况
    private boolean pause = false;
    
    
	/**
	 * 设置线程堵塞标志,线程将会在下次检测时堵塞
	 */
	public void pauseThread() {
		this.pause=true;
	}
	
	/**
	 * 唤醒线程
	 */
	public void resumeThread() {
        this.pause = false;
        //一定要用lock和synchronized配合唤醒
        synchronized (lock) {
            //唤醒线程
            lock.notify();
        }
    }
	
	/**
     * 这个方法只能在run 方法中实现，不然会阻塞主线程，导致页面无响应
     */
    void onPause() {
        synchronized (lock) {
            try {
                //线程 等待/阻塞
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    
//    void checkPause() {
//    	if (pause) {
//            //线程 阻塞/等待
//            onPause();
//        }
//    }
    @Override
	public void run() {
		init();
		while (true) {
			if (pause) {
	            //线程 阻塞/等待
	            onPause();
	        }
            try {
                Thread.sleep(500);
                
                loop();
                
            }catch(InterruptedException e) {
            	break;
            }catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
		
	}
    
    /**
     * 重写这个函数以此在线程中重复执行
     */
    void loop() {
    	
    }
    
    
    /**
     * 重写这个函数   线程中初始化时执行
     */
    void init() {
		

	}
    
}
