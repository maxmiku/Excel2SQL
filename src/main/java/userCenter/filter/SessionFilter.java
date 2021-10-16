package userCenter.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;


public class SessionFilter implements Filter {
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		chain.doFilter(request,response);
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		req.setCharacterEncoding("UTF-8");
    	resp.setContentType("text/html;charset=UTF-8");
		HttpSession session = req.getSession();
		
		
		String requestUrl = ""
//				+ req.getScheme() //当前链接使用的协议
//			    +"://" + req.getServerName()//服务器地址 
//			    + ":" + req.getServerPort() //端口号 
//			    + req.getContextPath() //应用名称，如果应用名称为
			    + req.getServletPath() //请求的相对url 
//			    + "?" + req.getQueryString() //请求参数
				;
		String clientIP = request.getRemoteHost();
		System.out.println(clientIP+" - "+requestUrl);
		
		
		
		
		if(requestUrl.equalsIgnoreCase("/yibanAuth")) {
			
			
			chain.doFilter(request,response);
			
		}else {
//			out.println("dealing");
			
			if(session==null) {
				System.out.println("[Filter]session为空,重定向到认证");
//				PrintWriter out = resp.getWriter();
//				out.println("[Filter]session为空,重定向到认证<br/>");
				resp.sendRedirect("/checkLaborHour/yibanAuth");
//				chain.doFilter(request,response);
				return;
			}
			
			if(session.getAttribute("userId")!=null) {
	    		System.out.println("[Filter]已有session");
//	    		out.println("[Filter]验证通过<br/>");
//	    		if((System.currentTimeMillis()/1000<Integer.parseInt((String) session.getAttribute("token_expires")))||Config.debugging) {
//	    			System.out.println("[Filter]session验证通过");
	    			chain.doFilter(request,response);
	    			return;
//	    		}
	    	}
			System.out.println("[Filter]session验证失败,重定向到认证");
			PrintWriter out = resp.getWriter();
//			out.println("[Filter]session验证失败,重定向到认证<br/>");
//			chain.doFilter(request,response);
			resp.sendRedirect("/checkLaborHour/yibanAuth");
//			
		}
		
		// TODO Auto-generated method stub
		
	}
}
