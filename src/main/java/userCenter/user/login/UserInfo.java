package userCenter.user.login;

/**
 * 用户信息类
 * @author lexuan
 *
 */
public class UserInfo {
	/**
	 * 用户登陆状态
	 * 0未登录   1已登录
	 */
	public int ulogin = 0;
	
	/**
	 * 用户名
	 */
	public String uname = null;
	
	/**
	 * 用户id
	 */
	public String uid = "";
	
	/**
	 * 客户等级
	 */
	public int ulevel = 0;
	
	/**
	 * 当前会话的token
	 */
	public String token = "";
	
	
	
	
	
	
	
	
	/**
	 * 没有登录 for 用户登陆状态
	 */
	public final static int ULOGIN_NOTLOGIN = 0;
	
	/**
	 * 已登录 for 用户登陆状态
	 */
	public final static int ULOGIN_LOGIN = 1;
}
