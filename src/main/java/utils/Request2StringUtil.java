package utils;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

public class Request2StringUtil {
	public static String getRequestPostStr(HttpServletRequest request) throws IOException {//将post过来的数据转换为字符串
		byte buffer[] = getRequestPostBytes(request);
		String charEncoding = request.getCharacterEncoding();
		if (charEncoding == null) {
		    charEncoding = "UTF-8";
		}
		return new String(buffer, charEncoding);
	}
	
	
	static byte[] getRequestPostBytes(HttpServletRequest request) throws IOException {//将post过来的数据转换为byte
	    int contentLength = request.getContentLength();
	    if(contentLength<0){
	        return null;
	    }
	    byte buffer[] = new byte[contentLength];
	    for (int i = 0; i < contentLength;) {
	
	        int readlen = request.getInputStream().read(buffer, i,
	                contentLength - i);
	        if (readlen == -1) {
	            break;
	        }
	        i += readlen;
	    }
	    return buffer;
	}
}
