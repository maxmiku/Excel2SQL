/*
前置库jsencrypt.min.js

使用方法
//生成密钥
generateKey(function(retj){
	//retj={"status":0,"errMsg":"","pubKey":"","priKey":""};
	if(retj['status']!=2){
		alert("加密密钥生成失败.错误代码:"+retj['status']+" \n错误信息:"+retj['errMsg']);
	}
	console.log("密钥生成成功");
	//下面就写处理的代码
});

//加密
var enData=rsaEncrypt(pubKey,data);

//解密 当私钥错误时返回null
var deData=rsaDecrypt(priKey,data);

*/

function bin2text(bin){return btoa(String.fromCharCode(...new Uint8Array(bin)));}

function generateKey(callBack) {
	//status 0未知错误  1只有一个密钥生成 2成功 -1私钥生成错误 -2公钥生成错误 -3生成密钥对大函数错误 
	// 参数 callback 回调函数 并传递一个如下格式的json
	var retj={"status":0,"errMsg":"","pubKey":"","priKey":""};
	function dealKey(isPri,keydata) {
		if(isPri){
			//是私钥
			retj['status']=retj['status']+1;
			retj['priKey']=bin2text(keydata);
			// console.log(retj);
		}else{
			//是公钥
			retj['status']=retj['status']+1;
			retj['pubKey']=bin2text(keydata);
		}
	}
	function dealErr(err,pot) {
		retj['status']=pot;//生成私钥时出现错误
		retj['errMsg']=err;
	}
	function finalExcute(){
		callBack(retj);
	}
	if(window.crypto.subtle==null){
		var ishttps = 'https:' == document.location.protocol ? true : false;
		if(!ishttps){
			if(!confirm("密钥生成失败, 可能原因: 当前不为安全连接, 是否跳转到安全链接?")){
				alert("用户取消操作, 请尝试使用firefox访问该网页");
				return;
			}
			window.location.href=window.location.href.replace("http://","https://")
			return;
		}
		alert("你的浏览器不支持生成密钥,请更换浏览器");
		return;
	}

	window.crypto.subtle.generateKey(
		{
			name: "RSA-OAEP",
			modulusLength: 1024, //can be 1024, 2048, or 4096
			publicExponent: new Uint8Array([0x01, 0x00, 0x01]),
			hash: {name: "SHA-256"}, //can be "SHA-1", "SHA-256", "SHA-384", or "SHA-512"
		},
		true, //whether the key is extractable (i.e. can be used in exportKey)
		["encrypt", "decrypt"] //must be ["encrypt", "decrypt"] or ["wrapKey", "unwrapKey"]
	).then(function(key){


		//returns a keypair object
		//私钥生成
		window.crypto.subtle.exportKey(
			"pkcs8", //can be "jwk" (public or private), "spki" (public only), or "pkcs8" (private only)
			key.privateKey //can be a publicKey or privateKey, as long as extractable was true
		).then(function(keydata){
			dealKey(true,keydata);
		})
		.catch(function(err){
			dealErr(err,-1);
			console.error(err);
		});



		//公钥生成
		window.crypto.subtle.exportKey(
			"spki", //can be "jwk" (public or private), "spki" (public only), or "pkcs8" (private only)
			key.publicKey //can be a publicKey or privateKey, as long as extractable was true
		).then(function(keydata){
			dealKey(false,keydata);
			finalExcute();
		})
		.catch(function(err){
			dealErr(err,-2);
			console.error(err);
		});

	})
	.catch(function(err){
		dealErr(err,-3);
		// retj['status']=-3;//生成密钥对大函数出现错误
		// retj['errMsg']=err;
		console.error(err);
	});
}

function rsaEncrypt(pubKey,data){
	var encrypt = new JSEncrypt();
	encrypt.setPublicKey(pubKey);
	var enData=encrypt.encrypt(data);
	return enData;
}

function rsaDecrypt(priKey,data) {
	var encrypt = new JSEncrypt();
	encrypt.setPrivateKey(priKey);
	var deData=encrypt.decrypt(data);
	return deData;
}




$(function() {
	$("#bt").click(
		function() {

			var encrypt = new JSEncrypt();
			encrypt.setPublicKey($("#publickey").val());
			encrypt.setPrivateKey($("#privatekey").val());
			var password = $("#password").val();
			var uname = $("#uname").val();
			password = encrypt.encrypt(password);

			uname = encrypt.encrypt(uname);
			$("#jmName").val(uname);
			$("#jmPasswrod").val(password);
			var jsonData = {
				"password" : password,
				"uname" : uname
			};

			$.post("jiemi.do", jsonData, function(result) {
				alert("解密的用户名：" + result.uname + "\n" + "解密的密码："
						+ result.password);

			}, 'json');
		}
	);
});