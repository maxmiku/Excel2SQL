
<%@page import="utils.MaxMikuUtils"%>
<% if(!MaxMikuUtils.getRemoteIP(request).equals("127.0.0.1")&&!MaxMikuUtils.getRemoteIP(request).equals("0:0:0:0:0:0:0:1")){response.sendError(403); return;}%>
<%@ page import="java.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>User Center</title>
</head>
<body>
当前IP:<%=MaxMikuUtils.getRemoteIP(request) %>

<br/>
<a href="./login.html">登录界面</a>
<br/>
<a href="./changePass.html">修改密码界面</a>
<br/>
<a href="./register.html">注册界面</a>
<br/>
<a href="./user/userLogout">登出</a>
<br/>
当前session储存:<br/>
<textarea style="width:100%; height: 80vh;">
<%
//获取session中所有的键值  
Enumeration<String> attrs = session.getAttributeNames();  
//遍历attrs中的
while(attrs.hasMoreElements()){
	//获取session键值  
	String name = attrs.nextElement().toString();
	//根据键值取session中的值  
	Object value = session.getAttribute(name);
	//打印结果 
	out.println("------\n\n" + name + ":" + value +"\n");
}
 %>
 </textarea>
</body>
</html>