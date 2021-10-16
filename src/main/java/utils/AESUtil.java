package utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;

/**
 * AES的加密和解密
 * @author libo
 * <dependency>
 *             <groupId>org.apache.commons</groupId>
 *             <artifactId>commons-lang3</artifactId>
 *             <version>3.9</version>
 *         </dependency>
 *         <dependency>
 *             <groupId>commons-codec</groupId>
 *             <artifactId>commons-codec</artifactId>
 *             <version>1.14</version>
 *         </dependency>
 */
public class AESUtil {
    //密钥 (需要前端和后端保持一致)
    private static final String KEY = "abcdefgabcdefg12";
    //算法
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";
 
    /**
     * aes解密
     * @param encrypt   内容
     * @return
     * @throws Exception
     */
    static String decrypt(String encrypt) {
        try {
            return decrypt(KEY,encrypt);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }  
 
    /**
     * aes加密
     * @param content
     * @return
     * @throws Exception
     */
    static String encrypt(String content) {
        try {
            return encrypt(KEY,content);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }  
 
    /**
     * 将byte[]转为各种进制的字符串
     * @param bytes byte[]
     * @param radix 可以转换进制的范围，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制
     * @return 转换后的字符串
     */
    static String binary(byte[] bytes, int radix){
        return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数
    }  
 
    /**
     * base 64 encode
     * @param bytes 待编码的byte[]
     * @return 编码后的base 64 code
     */
    static String base64Encode(byte[] bytes){
        return Base64.encodeBase64String(bytes);
    }  
 
    /**
     * base 64 decode
     * @param base64Code 待解码的base 64 code
     * @return 解码后的byte[]
     * @throws Exception
     */
    static byte[] base64Decode(String base64Code) throws Exception{
        return StringUtils.isEmpty(base64Code) ? null : new BASE64Decoder().decodeBuffer(base64Code);
    }  
 
    /**
     * AES加密
     * @param content 待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的byte[]
     * @throws Exception
     */
    static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));  
 
        return cipher.doFinal(content.getBytes("utf-8"));
    }  
 
    /**
     * AES加密为base 64 code
     * @param content 待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的base 64 code
     * @throws Exception
     */
    public static String encrypt(String encryptKey,String content) throws Exception {
        return base64Encode(aesEncryptToBytes(content, encryptKey));
    }  
 
    /**
     * AES解密
     * @param encryptBytes 待解密的byte[]
     * @param decryptKey 解密密钥
     * @return 解密后的String
     * @throws Exception
     */
    static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);  
 
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));
        byte[] decryptBytes = cipher.doFinal(encryptBytes);
        return new String(decryptBytes);
    }  
 
    /**
     * 将base 64 code AES解密
     * @param encryptStr 待解密的base 64 code
     * @param decryptKey 解密密钥
     * @return 解密后的string
     * @throws Exception
     */
    public static String decrypt(String decryptKey, String encryptStr) throws Exception {
        return StringUtils.isEmpty(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr), decryptKey);
    }  
 
    /**
     * 测试
     */
    public static void main(String[] args) throws Exception {
        String content = "123";
        System.out.println("加密前：" + content);
        System.out.println("加密密钥和解密密钥：" + KEY);
        String encrypt = encrypt(KEY,content);
        
//        encrypt="YS4xH9BfEfRrtmwjC2GtZR8cxn/vskjkEP7QO1rQMGh0M+UKiBwVDFyUSPFr7PzjY93dcniBrM/5wrceOmuX4nF2xK64WyCyadhS2qsUGBxhLjEf0F8R9Gu2bCMLYa1lHxzGf++ySOQQ/tA7WtAwaHQz5QqIHBUMXJRI8Wvs/ONj3d1yeIGsz/nCtx46a5ficXbErrhbILJp2FLaqxQYHGEuMR/QXxH0a7ZsIwthrWUfHMZ/77JI5BD+0Dta0DBodDPlCogcFQxclEjxa+z842Pd3XJ4gazP+cK3Hjprl+JxdsSuuFsgsmnYUtqrFBgcYS4xH9BfEfRrtmwjC2GtZR8cxn/vskjkEP7QO1rQMGh0M+UKiBwVDFyUSPFr7PzjY93dcniBrM/5wrceOmuX4nF2xK64WyCyadhS2qsUGBxhLjEf0F8R9Gu2bCMLYa1lHxzGf++ySOQQ/tA7WtAwaHQz5QqIHBUMXJRI8Wvs/ONj3d1yeIGsz/nCtx46a5ficXbErrhbILJp2FLaqxQYHGEuMR/QXxH0a7ZsIwthrWUfHMZ/77JI5BD+0Dta0DBodDPlCogcFQxclEjxa+z842Pd3XJ4gazP+cK3Hjprl+JxdsSuuFsgsmnYUtqrFBgcYS4xH9BfEfRrtmwjC2GtZR8cxn/vskjkEP7QO1rQMGh0M+UKiBwVDFyUSPFr7Pzj0eaydxRRga/EMlbo1gQ2Rg==";
        
        System.out.println("加密后：" + encrypt);
        String decrypt = decrypt(KEY,encrypt);
        System.out.println("解密后：" + decrypt);
    }
}