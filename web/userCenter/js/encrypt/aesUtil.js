/**
 * 加密（需要先加载lib/aes/aes.min.js文件）
 * @param word
 * @returns {*}
 */
function aesEncrypt(key,word){
    var key = CryptoJS.enc.Utf8.parse(key);
    var srcs = CryptoJS.enc.Utf8.parse(word);
    var encrypted = CryptoJS.AES.encrypt(srcs, key, {mode:CryptoJS.mode.ECB,padding: CryptoJS.pad.Pkcs7});
    return encrypted.toString();
}
 
/**
 * 解密
 * @param word
 * @returns {*}
 */
function aesDecrypt(key,word){
    var key = CryptoJS.enc.Utf8.parse(key);
    var decrypt = CryptoJS.AES.decrypt(word, key, {mode:CryptoJS.mode.ECB,padding: CryptoJS.pad.Pkcs7});
    return CryptoJS.enc.Utf8.stringify(decrypt).toString();
}



function aesDemo(){
	var key="abcdefgabcdefg12";
	console.log("原文:123");
	console.log("密码:"+key);
	var en=encrypt(key,"123");
	console.log("密文:"+en);
	var de=decrypt(key,en);
	console.log("解密后的明文:"+de);
}
