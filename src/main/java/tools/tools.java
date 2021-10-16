package tools;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


public class tools {
	
	public static String fileid2path(String fileid,HttpServletRequest req) {
		HttpSession se = req.getSession();
		String sePath = null;
		
		sePath = (String)se.getAttribute("exedit_uploadPath");
		if( sePath != null ) {
			sePath = sePath + "fileid";
		}else {
			return null;
		}
		return fileid;
	}
	
	//length用户要求产生字符串的长度
	 public static String getRandomString(int length){
	     String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	     Random random=new Random();
	     StringBuffer sb=new StringBuffer();
	     for(int i=0;i<length;i++){
	       int number=random.nextInt(62);
	       sb.append(str.charAt(number));
	     }
	     return sb.toString();
	 }
	 
	 public static boolean cleanDistory(File path,boolean delRoot) {
		 if(!path.exists())
			 return false;
		 if(path.isDirectory()) {
			 //为目录遍历
			 File[] flist = path.listFiles();
			 for(File tmp : flist) {
				 cleanDistory(tmp,true);				 
			 }
		 }
		 if(!delRoot) {
			 return true;
		 }
		 boolean rs = path.delete();
//		 System.out.println("文件 "+path.getName()+" 删除结果:"+rs);
		 return rs;
	 }
	
	/**
	 * 在指定文件中读取JSONArray
	 * @param path
	 * @param errj  -1文件读取失败       -2处理json失败
	 * @return 失败时为null
	 */
	public static JSONArray getJSONArrayFromFile(String path, JSONObject errj) {
		try {
			String content = "";
			File fpfile = null;
			InputStreamReader reader = null;
			BufferedReader br = null;
			try {
				fpfile = new File(path);
				
				reader=new InputStreamReader(new FileInputStream(fpfile));
				br = new BufferedReader(reader);
				
				String line = br.readLine();
				content = line==null ? "" : line;
				
				while((line=br.readLine())!=null) {
					content=content+"\n"+line;
				}
				br.close();
				reader.close();
			} catch (Exception e) {
				br.close();
				reader.close();
				errj.put("status", -1);
				errj.put("errMsg", "读取字段文件失败,请重试");
				return null;
			}
			
//				System.out.println("读出fieldPair的文本:"+content+"[END]");
			
			JSONArray retj = new JSONArray(content);
			return retj;
		} catch (Exception e) {
			e.printStackTrace();
			errj.put("status", -2);
			errj.put("errMsg", "处理读取JSON文件时出错,请重试");
			return null;
		}
	}

	public static JSONArray joinJSONArray(JSONArray array1, JSONArray array2) {

		for(int i=array2.length()-1;i>=0;i--){
			array1.put(array2.getJSONObject(i));
		}

		return array1;
//		StringBuffer sbf = new StringBuffer();
//		JSONArray jSONArray = new JSONArray();
//		try {
//			int len = array1.length();
//			for (int i = 0; i < len; i++) {
//				JSONObject obj1 = (JSONObject) array1.get(i);
//				if (i == len - 1)
//					sbf.append(obj1.toString());
//				else
//					sbf.append(obj1.toString()).append(",");
//			}
//			len = array2.length();
//			if (len > 0)
//				sbf.append(",");
//			for (int i = 0; i < len; i++) {
//				JSONObject obj2 = (JSONObject) array2.get(i);
//				if (i == len - 1)
//					sbf.append(obj2.toString());
//				else
//					sbf.append(obj2.toString()).append(",");
//			}
//
//			sbf.insert(0, "[").append("]");
//			jSONArray = new JSONArray(sbf.toString());
//			return jSONArray;
//		} catch (Exception e) {
//		}
//		return null;
	}
	
	public static void main(String[] args) {
		int totalSize=0;
		int length=1;
		System.out.println(totalSize+=length);
	}

}
