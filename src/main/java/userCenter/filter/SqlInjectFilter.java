package userCenter.filter;
 
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

public class SqlInjectFilter implements Filter {
	/**
	 * @author https://blog.csdn.net/peter_qyq/article/details/89206541
	 */
    public void destroy() {
    }
 
    public void init(FilterConfig arg0) throws ServletException {
    }
 
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        // 获得所有请求参数名
        Enumeration<?> params = request.getParameterNames();
        String sql = "";
        while (params.hasMoreElements()) {
            // 得到参数名
            String name = params.nextElement().toString();
            // 得到参数对应值
            String[] value = request.getParameterValues(name);
            for (int i = 0; i < value.length; i++) {
                sql = sql + value[i];
            }
        }
        if (sqlValidate(sql)) {
            throw new IOException("您发送请求中的参数中含有非法字符");
        } else {
            chain.doFilter(request, response);
        }
    }
 
    /**
     * 参数校验
     * @param str
     * 找到为true 没找到为false
     */
    public static boolean sqlValidate(String str) {
        str = str.toLowerCase();//统一转为小写
        String badStr = "select|update|delete|exec|count|'|\\\"|=|;|>|<|%";
        String[] badStrs = badStr.split("\\|");
//        System.out.println(badStrs);
        for (int i = 0; i < badStrs.length; i++) {
//        	System.out.println(badStrs[i]);
            //循环检测，判断在请求参数当中是否包含SQL关键字
            if (str.indexOf(badStrs[i]) >= 0) {
            	System.out.println(""+i+"匹配到"+badStrs[i]);
                return true;
            }
        }
        return false;
    }
}