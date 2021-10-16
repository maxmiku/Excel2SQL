package utils;

import javax.crypto.Cipher;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Java RSA 加密工具类
 * 参考： https://blog.csdn.net/qy20115549/article/details/83105736
 */
public class RSAUtil {
    /**
     * 密钥长度 于原文长度对应 以及越长速度越慢
     */
    private final static int KEY_SIZE = 1024;
    /**
     * 用于封装随机产生的公钥与私钥
     */
    private static Map<String, String> keyMap = new HashMap<String, String>();
    /**
     * 随机生成密钥对
     * @return Map<String, String> pub-公钥  pri-私钥    失败是为null
     */
    public static Map<String, String> genKeyPair() {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen;
        
		try {
			keyPairGen = KeyPairGenerator.getInstance("RSA");
			// 初始化密钥对生成器
	        keyPairGen.initialize(KEY_SIZE, new SecureRandom());
	        // 生成一个密钥对，保存在keyPair中
	        KeyPair keyPair = keyPairGen.generateKeyPair();
	        // 得到私钥
	        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
	        // 得到公钥
	        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
	        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
	        // 得到私钥字符串
	        String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
	        // 将公钥和私钥保存到Map
	        //0表示公钥
	        keyMap.put("pub", publicKeyString);
	        //1表示私钥
	        keyMap.put("pri", privateKeyString);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
        return keyMap;
    }

    /**
     * RSA公钥加密
     *
     * @param str       加密字符串
     * @param publicKey 公钥
     * @return 密文
     * @throws Exception 加密过程中的异常信息
     */
	public static String encrypt(String str, String publicKey) throws Exception {
    	//将字符转码 方便加密中文
//    	str=URLEncoder.encode(str, "UTF-8");
        //base64编码的公钥
        byte[] decoded = Base64.getDecoder().decode(publicKey);
//        RSAPublicKey pubKey =  ;
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        String outStr = Base64.getEncoder().encodeToString(cipher.doFinal(str.getBytes("UTF-8")));
        return outStr;
    }

    /**
     * RSA私钥解密
     *
     * @param str        加密字符串
     * @param privateKey 私钥
     * @return 明文
     * @throws Exception 解密过程中的异常信息
     */
	public static String decrypt(String str, String privateKey) throws Exception {
        //64位解码加密后的字符串
        byte[] inputByte = Base64.getDecoder().decode(str);
        //base64编码的私钥
        byte[] decoded = Base64.getDecoder().decode(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        String outStr = new String(cipher.doFinal(inputByte),"UTF-8");
        //将字符解码 适配中文
//        outStr=URLDecoder.decode(outStr,"UTF-8");
        return outStr;
    }
    



    public static void main(String[] args) throws Exception {
    	
//    	System.out.println(URLDecoder.decode("e2qVQEwrV3jaaipiAnX8tVu9AHYRHOIOKL7X1PtJK2YywKlr/t3F1gbaePseZfAx+9j1TtxJHBKRv+s9CGrwLc/gX6I4czb8NQMaIePwEMvogUWbCxJT95ew8wLRpzJOIUUBkghfK6gnLA6uXfeEfyhx40E5FM7AiiHBLF1o6Uk=","UTF-8"));
//    	System.out.println(URLEncoder.encode("我丢你妈?!%$#@\"\"", "UTF-8"));
    	
    	
    	
        long temp = System.currentTimeMillis();
        //生成公钥和私钥
        genKeyPair();
        //加密字符串
        System.out.println("公钥:" + keyMap.get("pub"));
        System.out.println("私钥:" + keyMap.get("pri"));
        System.out.println("生成密钥消耗时间:" + (System.currentTimeMillis() - temp) / 1000.0 + "秒");


        String message = "RSA测试ABCD~!@#$";
        @SuppressWarnings("deprecation")
		String urlCodeString=URLEncoder.encode(message);
        System.out.println("原文:" + message);

        temp = System.currentTimeMillis();
        String messageEn = encrypt(urlCodeString, keyMap.get("pub"));
        System.out.println("密文:" + messageEn);
        System.out.println("加密消耗时间:" + (System.currentTimeMillis() - temp) / 1000.0 + "秒");

        temp = System.currentTimeMillis();

        String messageDe = decrypt(messageEn, keyMap.get("pri"));
        
        System.out.println("urlCode:" + messageDe);
        @SuppressWarnings("deprecation")
        String urldecodString=URLDecoder.decode(messageDe);
        
        System.out.println("解密:" + urldecodString);
        System.out.println("解密消耗时间:" + (System.currentTimeMillis() - temp) / 1000.0 + "秒");
    }
}