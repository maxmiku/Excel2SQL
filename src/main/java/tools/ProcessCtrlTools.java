package tools;


import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 转换进度控制类
 * @author lexuan
 *
 */
public class ProcessCtrlTools{
	/**
	 * 全局处理进度
	 * 里面是jsonObject
	 * 	{
			"i":"文件id",
			"s":0,//状态 0缺省
			"t":0,//开始时间 缺省0 long
			"p":0,//最大100已完成
			"ec":0,//当前错误数量
			"e":[{"r":12,"msg":"第12行错误"},""]//错误信息字符串数组
		}
	 * 
	 */
	
	public static JSONArray gobalStatus = new JSONArray();
	
	/**
	 * 最长status存在时间 毫秒 30分钟
	 */
	final static int MAX_STATUSTIME = 1800000;
	
	/**
	 * 往进度数组添加成员
	 * @param fileid
	 * @return true成功 false已存在项目
	 */
	public static boolean addStatus(String fileid) {
		Long nowTime = System.currentTimeMillis();
		for (int i = gobalStatus.length()-1; i >= 0; i--) {
			JSONObject tmpj = gobalStatus.getJSONObject(i);
			if(tmpj.getString("i").equals(fileid)) {
				return false;
			}
			if((nowTime - tmpj.getLong("t"))>MAX_STATUSTIME) {
				//项目超时
				gobalStatus.remove(i);
			}
		}
		JSONObject newItem = new JSONObject();
		newItem.put("i", fileid);
		newItem.put("s", 0);
		newItem.put("t", System.currentTimeMillis());
		newItem.put("p", 0);
		newItem.put("ec", 0);
		newItem.put("e", new JSONArray());
		gobalStatus.put(newItem);
		return true;
	}
	
	/**
	 * 删除在列表中的项目
	 * @param fileid
	 * @return 是否成功 false 找不到
	 */
	public static boolean removeStatus(String fileid) {
		JSONObject retj = null;
		Long nowTime = System.currentTimeMillis();
		for (int i = gobalStatus.length()-1; i >= 0; i--) {
			JSONObject tmpj = gobalStatus.getJSONObject(i);
			if(tmpj.getString("i").equals(fileid)) {
				gobalStatus.remove(i);
				return true;
			}
			if((nowTime - tmpj.getLong("t"))>MAX_STATUSTIME) {
				//项目超时
				gobalStatus.remove(i);
			}
		}
		return false;
	}
	
	
	/**
	 * 设置项目状态值
	 * @param fileid
	 * @param status STATUS_COMPLETE时若error中有数据则会变成STATUS_ERROR   STATUS_CONVERTING时会刷新存活时间
	 * @return boolean
	 */
	public static boolean setStatus(String fileid,int status) {
		Long nowTime = System.currentTimeMillis();
		boolean found = false;
		for (int i = gobalStatus.length()-1; i >= 0; i--) {
			JSONObject tmpj = gobalStatus.getJSONObject(i);
			if(tmpj.getString("i").equals(fileid)) {
				if(status==STATUS_COMPLETE) {
					if(tmpj.optJSONArray("e")==null || tmpj.getJSONArray("e").length()!=0) {
						tmpj.put("s",STATUS_ERROR);
					}else {
						tmpj.put("s",status);
					}
				}else if(status==STATUS_CONVERTING){
					tmpj.put("s",status);
					tmpj.put("t",System.currentTimeMillis());
				}else{
					tmpj.put("s",status);
				}
				
				gobalStatus.put(i, tmpj);
			}
			if((nowTime - tmpj.getLong("t"))>MAX_STATUSTIME) {
				//项目超时
				gobalStatus.remove(i);
			}
		}
		return found;
	}
	
	/**
	 * 获取项目状态值
	 * @param fileid
	 * @return int 
	 */
	public static int getStatus(String fileid) {
		Long nowTime = System.currentTimeMillis();
		for (int i = gobalStatus.length()-1; i >= 0; i--) {
			JSONObject tmpj = gobalStatus.getJSONObject(i);
			if(tmpj.getString("i").equals(fileid)) {
				return tmpj.getInt("s");
			}
			if((nowTime - tmpj.getLong("t"))>MAX_STATUSTIME) {
				//项目超时
				gobalStatus.remove(i);
			}
		}
		return STATUS_NOTFOUND;
	}
	
	
	/**
	 * 设置项目错误信息
	 * @param fileid
	 * @param error jsonarray
	 * @return boolean
	 */
	public static boolean addError(String fileid,JSONArray error) {
		Long nowTime = System.currentTimeMillis();
		boolean found = false;
		for (int i = gobalStatus.length()-1; i >= 0; i--) {
			JSONObject tmpj = gobalStatus.getJSONObject(i);
			if(tmpj.getString("i").equals(fileid)) {
				JSONArray errs = tmpj.getJSONArray("e");
				if(errs==null) errs=new JSONArray();
				errs=tools.joinJSONArray(errs,error);
				tmpj.put("ec", error.length());
				System.out.println("添加错误后:"+errs+"\n");
				tmpj.put("e", errs);
				gobalStatus.put(i, tmpj);
			}
			if((nowTime - tmpj.getLong("t"))>MAX_STATUSTIME) {
				//项目超时
				gobalStatus.remove(i);
			}
		}
		return found;
	}
	public static boolean addError(String fileid,JSONObject error) {
		JSONArray a = new JSONArray();
		a.put(error);
		return addError(fileid, a);
	}
	
	
	
	/**
	 * 获取项目对象并删除在列表中的项目
	 * @param fileid
	 * @param del 是否删除 布尔
	 * @return JSONObject
	 */
	public static JSONObject getStatusObject(String fileid,boolean del) {
		JSONObject retj = null;
		Long nowTime = System.currentTimeMillis();
		for (int i = gobalStatus.length()-1; i >= 0; i--) {
			JSONObject tmpj = gobalStatus.getJSONObject(i);
			if(tmpj.getString("i").equals(fileid)) {
				retj = tmpj;
				if(del)gobalStatus.remove(i);
			}
			if((nowTime - tmpj.getLong("t"))>MAX_STATUSTIME) {
				//项目超时
				gobalStatus.remove(i);
			}
		}
		return retj;
	}
	/**
	 * 获取项目对象并删除在列表中的项目
	 * @param fileid
	 * @return JSONObject
	 */
	public static JSONObject getStatusObject(String fileid) {
		return getStatusObject(fileid, true);
	}
	
	/**
	 * 获取指定项目前面的队列长度
	 * @param fileid
	 * @return
	 */
	public static int getQqueueLength(String fileid) {
		int queueLength = 0;
		boolean found = false;
		JSONObject retj = null;
		for (int i = 0; i < gobalStatus.length(); i++) {
			JSONObject tmpj = gobalStatus.getJSONObject(i);
			if(tmpj.getString("i").equals(fileid)) {
				found=true;
				break;
			}else if(tmpj.getInt("s")==STATUS_INQUEUE){
				queueLength++;
			}
		}
		return found ? queueLength+1 : -1;
	}
	
	/**
	 * 获取当前转换进度
	 * @param fileid
	 * @return int 最大为100  -1为未找到
	 */
	public static Double getProcess(String fileid) {
		Long nowTime = System.currentTimeMillis();
		for (int i = gobalStatus.length()-1; i >= 0; i--) {
			JSONObject tmpj = gobalStatus.getJSONObject(i);
			if(tmpj.getString("i").equals(fileid)) {
//				System.out.println("获取当前进度:"+tmpj.getDouble("p"));
				return tmpj.getDouble("p");
			}
			if((nowTime - tmpj.getLong("t"))>MAX_STATUSTIME) {
				//项目超时
				gobalStatus.remove(i);
			}
		}
		return -1.0;
	}
	
	/**
	 * 获取当前转换进度和错误数量
	 * @param fileid
	 * @return JSONObject null为未找到  "p":进度DOuble ->最大为100        "ec":错误计数int
	 */
	public static JSONObject getProcessNErrCount(String fileid) {
		Long nowTime = System.currentTimeMillis();
		for (int i = gobalStatus.length()-1; i >= 0; i--) {
			JSONObject tmpj = gobalStatus.getJSONObject(i);
			if(tmpj.getString("i").equals(fileid)) {
//				System.out.println("获取当前进度:"+tmpj.getDouble("p"));
				JSONObject retj = new JSONObject();
				retj.put("p",tmpj.getDouble("p"));
				retj.put("ec",tmpj.getInt("ec"));
				return retj;
			}
			if((nowTime - tmpj.getLong("t"))>MAX_STATUSTIME) {
				//项目超时
				gobalStatus.remove(i);
			}
		}
		return null;
	}
	
	
	
	
	/**
	 * 设置项目转换进度
	 * @param fileid
	 * @return boolean
	 */
	public static boolean setProcess(String fileid,Double process) {
		Long nowTime = System.currentTimeMillis();
		boolean found = false;
		for (int i = gobalStatus.length()-1; i >= 0; i--) {
			JSONObject tmpj = gobalStatus.getJSONObject(i);
			if(tmpj.getString("i").equals(fileid)) {
				tmpj.put("p",(Double) process);
//				System.out.println("设置成功  设置进度 id:"+fileid+"  进度:"+process);
				gobalStatus.put(i, tmpj);
				found=true;
			}
			if((nowTime - tmpj.getLong("t"))>MAX_STATUSTIME) {
				//项目超时
				gobalStatus.remove(i);
			}
		}
		return found;
	}
	
	/**
	 * 设置项目转换进度
	 * @param fileid
	 * @param process 进度
	 * @param errorCount 错误个数
	 * @return boolean
	 */
	public static boolean setProcess(String fileid,Double process,int errorCount) {
		Long nowTime = System.currentTimeMillis();
		boolean found = false;
		for (int i = gobalStatus.length()-1; i >= 0; i--) {
			JSONObject tmpj = gobalStatus.getJSONObject(i);
			if(tmpj.getString("i").equals(fileid)) {
				tmpj.put("p",process);
				tmpj.put("ec",errorCount);
//				System.out.println("设置成功  设置进度 id:"+fileid+"  进度:"+process);
				gobalStatus.put(i, tmpj);
				found=true;
			}
			if((nowTime - tmpj.getLong("t"))>MAX_STATUSTIME) {
				//项目超时
				gobalStatus.remove(i);
			}
		}
		return found;
	}
	
	/**
	 * 获取当前所有转换信息的列表
	 * @return
	 */
	public static JSONArray getGobalStatus() {
		return gobalStatus;
	}
	
	/**
	 * 状态 缺省
	 */
	public final static int STATUS_DEFAULT = 0;
	/**
	 * 状态 队列中
	 */
	public final static int STATUS_INQUEUE = 1;
	/**
	 * 状态 转换中
	 */
	public final static int STATUS_CONVERTING = 2;
	/**
	 * 状态 转换成功
	 */
	public final static int STATUS_COMPLETE = 3;
	/**
	 * 状态 错误
	 */
	public final static int STATUS_ERROR = -1;
	/**
	 * 状态 找不到项目
	 */
	public final static int STATUS_NOTFOUND = -2;
	
}