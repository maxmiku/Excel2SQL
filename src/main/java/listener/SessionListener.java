package listener;


import java.io.File;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener,HttpSessionAttributeListener{
	// 参数
	ServletContext sc;

	static ArrayList<String> list = new ArrayList<String>();
	
	public static int getSessionNum() {
		return list.size();
	}
	public static ArrayList<String> getSessionList() {
		return list;
	}
	static boolean delFile(File file) {
        if (!file.exists()) {
            return false;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                delFile(f);
            }
        }
        return file.delete();
    }

	// 新建一个session时触发此操作
	@Override
	public void sessionCreated(HttpSessionEvent se) {
	    sc = se.getSession().getServletContext();
	    list.add(se.getSession().getId());

	    System.out.println("新建一个session" + se.getSession().getId()+"\n当前剩余session:"+list.size());
	    for(String a:list) {
	    	System.out.println("leave session:"+a);
	    }
	}
	

	// 销毁一个session时触发此操作
	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
	    String sessionId = se.getSession().getId();
	    list.remove(se.getSession().getId());
	    
	    if(se.getSession().getAttribute("exedit_uploadPath")!=null) {
	    	String uploadPath = (String)se.getSession().getAttribute("exedit_uploadPath");
		    boolean delResult = delFile(new File(uploadPath));
		    System.out.println("sessionid:"+sessionId+"被摧毁\n移除目录结果:"+delResult+"\n"+uploadPath+"\n当前剩余session:"+list.size());
	    }else {
	    	System.out.println("sessionid:"+sessionId+"被摧毁\n当前剩余session:"+list.size());
	    }
	    
	    

	    
//	    deleteFile.deleteFile(sessionId);//在DeleteFile里调用traverseFolderAndDelete();
	    for(String a:list) {
	    	System.out.println("leave session:"+a);
	    }
	}

	@Override
	public void attributeAdded(HttpSessionBindingEvent arg0) {
	    // TODO Auto-generated method stub

	}

	@Override
	public void attributeRemoved(HttpSessionBindingEvent arg0) {
	    // TODO Auto-generated method stub

	}

	@Override
	public void attributeReplaced(HttpSessionBindingEvent arg0) {
	    // TODO Auto-generated method stub

	}

	
	
//	public void sessionCreated(HttpSessionEvent se) {
//		HttpSession session = se.getSession();
//		System.out.println("session创建!"+session.getLastAccessedTime());
//	}
// 
//    public void sessionDestroyed(HttpSessionEvent se) {
//    	HttpSession session = se.getSession();
//    	System.out.println("session销毁!");
//    	
//    }
}
