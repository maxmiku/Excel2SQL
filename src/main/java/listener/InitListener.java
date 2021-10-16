package listener;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import tools.tools;
import utils.JSONConfigFileUtil;

public class InitListener implements ServletContextListener {  
	  
    @Override  
    public void contextDestroyed(ServletContextEvent context) {  
          excelEditor.thread.ImportThreadCtrlTool.stopDthread();
    }  
  
    @Override  
    public void contextInitialized(ServletContextEvent context) {  
		// 上下文初始化执行  
    	String path = context.getServletContext().getRealPath("uploadExcel");

		if(JSONConfigFileUtil.appconfig==null){
			JSONConfigFileUtil.getConfig();
		}
    	System.out.println("上传文件夹路径:"+path);
		System.out.println("================>[ServletContextListener]excel2sql初始化开始");
		if(!config.Config.skipInitDelette) {
			path = context.getServletContext().getRealPath("uploadExcel");
			File uploadPath = new File(path);
			if(!uploadPath.exists()) {
				uploadPath.mkdir();
			}
			tools.cleanDistory(uploadPath,false);
		}else {
			System.out.println("================>[ServletContextListener]excel2sql初始化删除被跳过  config.skipInitDelette");
		}
		excelEditor.thread.ImportThreadCtrlTool.startDThread();
		System.err.println("context:"+context.getServletContext().getContextPath());
//		UserAuth.InitAPI(config.Config.login_appid, config.Config.login_appsecret, context.getServletContext().getContextPath());
		System.out.println("================>[ServletContextListener]excel2sql初始化完成");
    }
}
