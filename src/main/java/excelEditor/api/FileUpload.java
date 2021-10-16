package excelEditor.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.json.JSONObject;
import utils.ExcelUtil;

@WebServlet("/upload")
@MultipartConfig  //使用MultipartConfig注解标注改servlet能够接受文件上传的请求
public class FileUpload extends HttpServlet {

	
	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/**
		 *  @return  -1后缀名出错  -2空表  -3文件过大    -9内部错误  -10文件不支持
		 */
		req.setCharacterEncoding("UTF-8");
    	resp.setContentType("text/html;charset=UTF-8");
    	JSONObject retj = new JSONObject();
    	JSONObject retData = new JSONObject();
    	PrintWriter out = resp.getWriter();
		retj.put("status", 0);
		retj.put("errMsg", "");
		retj.put("retData", "");
		
		if(!CheckAuth.check(req, resp, retj)) {
			out.print(retj);
			return;
		}
    	
		try {
			retData.put("uploadStatus", -1);
			HttpSession session = req.getSession();
	    	String sessionid = session.getId();
	    	
	    	
//	    	System.out.println("数据"+req.getParameter("test1"));
	        Part part = req.getPart("myfile");
	        String disposition = part.getHeader("Content-Disposition");
	        
	        
			String uploadPath = (String)session.getAttribute("exedit_uploadPath");
			File sesPath = null;
			if(uploadPath==null) {
				String rawPath = req.getServletContext().getRealPath("uploadExcel");
				session.setAttribute("exedit_uploadPath", rawPath+"/"+sessionid);
				uploadPath = rawPath+"/"+sessionid;
			}
			
			sesPath=new File(uploadPath);
			if (!sesPath.exists()){//只能建立一层文件夹
				String rawPath = req.getServletContext().getRealPath("uploadExcel");
				File upPath = new File(rawPath);
		        if (!upPath.exists()){
		            upPath.mkdir();
//		            System.out.println("创建上传文件夹："+ upPath.getPath());
		        }
				sesPath.mkdir();
//	            System.out.println("创建session上传文件夹："+ sesPath.getPath());
	        }
			
			
			
			
			String suffix = null;
			try {
				//获取文件后缀名
		        suffix = disposition.substring(disposition.lastIndexOf("."),disposition.length()-1);
			} catch (Exception e) {
				//上传的文件没有后缀名
				System.err.println("获取后缀名出错");
				retj.put("status", -1);
				retj.put("errMsg", "后缀名不存在");
				retj.put("retData",retData);
				out.print(retj);
				return;
			}
			
			if(!suffix.equalsIgnoreCase(".xls") && !suffix.equalsIgnoreCase(".xlsx")) {
				retj.put("status", -1);
				retj.put("errMsg", "文件类型不支持");
				retj.put("retData",retData);
				out.print(retj);
				return;
			}
			
			//随机的生存一个32的字符串
	        String filename = UUID.randomUUID()+suffix;
	        
	        
	        
	        InputStream is = part.getInputStream();
	        

	        
	        FileOutputStream fos = new FileOutputStream(uploadPath+"/"+filename);
//	        System.out.println(uploadPath+"/"+filename);
	        byte[] bty = new byte[1024];
	        int length =0;
	        long totalSize = 0L;
	        long maxSize = config.Config.maxAllowUploadSize*1024;
//	        System.out.println("maxAllow:"+maxSize);
	        while((length=is.read(bty))!=-1){
	        	if((totalSize+=length)>maxSize) {
	        		is.close();
	        		fos.close();
	        		File upFile = new File(uploadPath+"/"+filename);
	        		if(upFile.exists()) {
	        			upFile.delete();
	        		}
	        		retj.put("status", -3);
					retj.put("errMsg", "文件大小超过限制,最大允许大小:"+config.Config.maxAllowUploadSize+"KB");
					retj.put("retData",retData);
					System.err.println("用户上传文件超出限制大小,被丢弃");
					out.print(retj);
					return;
	        	}
	            fos.write(bty,0,length);
	        }
	        fos.close();
	        is.close();
	        
	        session.setAttribute("exedit_fileid", filename);
	        retData.put("uploadStatus", 0);
	        retData.put("fileid", filename);
	        retj.put("retData",retData);
		} catch (Exception e) {
			// TODO: handle exception
			long ms = System.currentTimeMillis();
			retj.put("status", -9);
			retj.put("errMsg", "上传文件时发生内部错误\n检索号:"+ms+"\n请重新上传 或 请联系管理员");
			System.err.println("文件上传错误:"+ms);
			e.printStackTrace();
		}
    	
		out.print(retj);
        //文件保存完成
        
		

		
        
    }

}
