package utils;

import org.json.JSONObject;
import sun.misc.BASE64Encoder;

import java.io.InputStream;
import java.nio.charset.Charset;

public class JSONConfigFileUtil {

    public static JSONObject appconfig = null;


    public static void main(String[] args) {
        getConfig();
    }

    public static JSONObject getConfig(){
        InputStream in = JSONConfigFileUtil.class.getResourceAsStream("/config.json");
        String config = readIoToString(in);

//        System.out.println(config.length());

        appconfig=new JSONObject(config);

//        System.out.println(appconfig);

        return appconfig;
    }

    /**
     * 把文件流转成字符串
     * @param is
     * @return
     */
    public static String readIoToString(InputStream is) {
        String result = null;
        byte[] data = null;
        try {
            data = new byte[is.available()];
            is.read(data);
//            BASE64Encoder encoder = new BASE64Encoder();
            result = new String(data, Charset.forName("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(is != null) {
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(data != null) {
                data = null;
            }
        }
        return result;
    }
}
