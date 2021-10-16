var nowProcess=0;//当前登录页面的进度 0初始状态  1未生成客户端密钥对     2已生成客户端密钥对可以输入用户名  
//3当前输入完用户名但未拉入服务器公钥  4已拉取服务器公钥可以输入密码   5已输入密码正在发送给服务器              6登录完成


function setProcess(num){
	
	if(num<4){
		//username窗格按键刷新
		if(num==2){
			//密钥生成完成允许输用户名 用户名服务器错误 
			if(nowProcess==4){
				//从pw返回
				$('#waitNext').addClass("loginButsHid");
				$('#nextSetp').removeClass("loginButsUndis");
				$('#loginSetp').addClass("loginButsHid");
				setTimeout(function(){
					$('#loginSetp').hide();
					$('#loginSetp').addClass("loginButsUndis");
					$('#loginSetp').removeClass("loginButsHid");
					setTimeout(function(){$('#loginSetp').show();},500);
				},500);
			}else{
				$('#waitNext').addClass("loginButsHid");
				$('#nextSetp').removeClass("loginButsUndis");
				$('#loginSetp').addClass("loginButsUndis");
			}
			
		}else if(nowProcess==2 && num==3){
			//正在发送用户名,请稍候
			$('#waitNext').removeClass("loginButsHid");
			$('#nextSetp').addClass("loginButsUndis");
		}
	}else{
		//passwd窗格刷新
		if(num==4){
			//隐藏用户名框 显示密码框
			$('#usernameBox').hide();
			$('#passwordBox').fadeIn(100);
			$('#uNameDisplay').html($('#uName').val());
			$('#fakeuName').val($('#uName').val());
			if($('#fakeuPass').val()!=""){
				$('#uPass').val($('#fakeuPass').val());
				$('#fakeuPass').val("");
			}

			setTimeout(function(){$('#uPass').focus()},200);

			$('#waitNext').addClass("loginButsHid");
			$('#loginSetp').removeClass("loginButsUndis");
			$('#nextSetp').addClass("loginButsUndis");


		}else if(nowProcess==4 && num==5){
			//正在发送密码,请稍候
			$('#waitNext').removeClass("loginButsHid");
			$('#loginSetp').addClass("loginButsUndis");
		}
	}
	

	nowProcess=num;
	$('#processStatus').html(num);
	dblog("当前加载进度 "+num);

}

function loginNextSetp(){
	//下一步按钮被按下
	if(nowProcess==1){
		var date=new Date();
		if(sessionStorage.getItem("cRsaKGT") == null || Number(sessionStorage.getItem("cRsaKGT"))+10000<date.getTime()){
			var date=new Date();
			sessionStorage.setItem("cRsaKGT",date.getTime());
			dblog("正在重新生成密钥");
			generateKey(function(a){rsaCallback(a)});
			alert("抱歉,出现错误,请重新点击登录键.\n若再次出现此错误,请联系网站管理员.");
		}else{
			dblog("未完成生成密钥等待,2秒后重试");
//			setTimeout(function(){
//				loginNextSetp();
//			},2000);
		}
//		dblog("请稍等密钥生成,flag="+flag);
//		if(flag!=true){
//			alert("正在进行安全检查,请等待2秒...");
//			setTimeout(function(){
//				loginNextSetp(1);
//			}, 2000);
//		}else {
//			alert("正在重新尝试加密连接...");
//			setTimeout(function(){
//				loginNextSetp();
//			}, 2000);
//		}
		return;
	}else if(nowProcess==0){
		dblog("正在加载必要组件,请稍候");
		alert("正在加载必要组件,请稍候...");
		return;
	}else if(nowProcess!=2){
		dblog("当前状态不正确 不允许检查用户名,nowProxess:"+nowProcess);
		alert("登录状态不正确,请刷新页面..");
		return;
	}
	
	setProcess(3);//已输入完用户名正在服务器拉钥
	dblog("正在向查询服务器查询(若已存在也就是说之前返回了,那服务器就不会再生成新的密钥对,直接返回session里面的数据)");

	setTimeout(function(){
		//======================post到服务器查询用户名是否存在请将get换为post
		var username=$('#uName').val();
		var vCode=$('#vCodeuName').val();
		var cRsaPubK = sessionStorage.getItem("cRsaPubK");
		var sendJ={"username":username,"verifyCode":vCode,"cRsaPubKey":cRsaPubK};
		dblog("向服务器 /checkUser 发送:"+JSON.stringify(sendJ));
		$.ajax(
			{
				type:"POST",
				url:"./user/checkUser",
				data:JSON.stringify(sendJ),
				success:function(r){
					dblog("checkUser返回\n"+JSON.stringify(r));
					if(dealRetErr(r)){refreshVCodeuName();setProcess(2);return;}
					var ret=r["retData"];
					console.log(ret);
					if(ret['userStatus']==0){
						//由此用户 并且服务器返回公钥
						dblog("服务器有此用户,返回公钥!\n"+ret['sRsaPubKey']);
						sessionStorage.setItem("sRsaPubK",ret['sRsaPubKey']);
						
						if(ret["usalt"]!=""){
							var usalt = rsaDecrypt(sessionStorage.getItem("cRsaPriK"),ret["usalt"]);
							if(usalt==null){
								alert("错误,获取用户密钥错误");
							}
							sessionStorage.setItem("usalt",usalt);
							dblog("salt:"+usalt);
						}else{
							sessionStorage.setItem("usalt","");
						}
						
						
						var token = rsaDecrypt(sessionStorage.getItem("cRsaPriK"),ret["token"]);
						if(token==null){
							alert("错误,获取用户token错误");
						}
						sessionStorage.setItem("token",token);
						dblog("token:"+token);
						
						setProcess(4);//用户存在 可以登录

						
					}else{
						//不存在该用户
						setProcess(2);//等待输入用户名状态
						dblog("用户名不存在");
						alert("抱歉,该用户不存在");
						refreshVCodeuName();

					}
				},
				dataType:"json",
				error:function(err){
					refreshVCodeuName();
					console.log("检查用户名错误 ["+err.status+"]"+err.statusText);
					dblog("检查用户名错误 ["+err.status+"]"+err.statusText);
					setProcess(2);
					alert("请求失败,请重试.若再次出现此错误请联系管理员");
				}
			}
		);
	},500);
}

function loginPrevSetp(){
	//上一步按钮按下 重置状态
	setProcess(2);
	refreshVCodeuName();
	dblog("等待用户重新输入用户名");
	$("#uPass").val("");

//=================隐藏密码框 显示用户名框
	$('#usernameBox').fadeIn(100);
	$('#passwordBox').hide();


}




function loginButPress(){
	//登录按钮按下

	if(nowProcess>4){
		//正在登录中
		dblog("当前正在登陆中,请耐心等待");
		alert("当前正在登陆中,请耐心等待");
		return;

	}else if(nowProcess<4){
		dblog("当前状态不正确,非可登录状态.");
		alert("当前状态不正确,非可登录状态.请检查是否填写用户名");
		return;
	}
	setProcess(5);
	dblog("用户已输入密码,正在发送密码");

	setTimeout(function(){
		var sendJ={};
		sendJ["username"]=$('#uName').val();
		dblog("加盐后的密码"+$('#uPass').val()+sessionStorage.getItem("usalt"));
		sendJ["password"]=rsaEncrypt(sessionStorage.getItem("sRsaPubK"),hex_md5(hex_md5($('#uPass').val()+sessionStorage.getItem("usalt"))+sessionStorage.getItem("sRsaPubK")));
	//	sendJ["cRsaPubKey"]=sessionStorage.getItem("cRsaPubK");
		sendJ["token"]=rsaEncrypt(sessionStorage.getItem("sRsaPubK"),hex_md5(sendJ["username"]+sendJ["password"]+sessionStorage.getItem("token")));

		dblog("加密后要发出的数据:\n"+JSON.stringify(sendJ));

		//=======================post发送密码=======
		$.ajax(
			{
				type:"POST",
				url:"./user/userLogin",
				data:JSON.stringify(sendJ),
				success:function(r){
					if(dealRetErr(r)){setProcess(4);return;}
					var ret = r['retData'];
					var loginStatus = parseInt(rsaDecrypt(sessionStorage.getItem("cRsaPriK"),ret["loginStatus"]));
					if(loginStatus==0){
						//登陆成功
						dblog("密码正确");
						var aesKey=rsaDecrypt(sessionStorage.getItem("cRsaPriK"),ret["key"]);
						
						dblog("解密得到的aeskey:"+aesKey);

						if(aesKey==false){
							//解密失败
							dblog("aes密钥解密失败");
							alert("抱歉,登陆失败:密钥失效.\n请刷新页面重新登录");
							setProcess(2);
							return;
						}

						

						sessionStorage.setItem("aesKey",aesKey);
						setProcess(6);
						dblog("登陆成功");
//						alert("登陆成功");
						sessionStorage.setItem("uname",$('#uName').val());
						
						if(ret["loginNotic"]!=""){
							var logNotic = rsaDecrypt(sessionStorage.getItem("cRsaPriK"),ret["loginNotic"]);
							alert("登陆系统通知:\n"+logNotic);
						}
						
						console.log(ret["skipUrl"]);
						if(ret["skipUrl"]==undefined || ret["skipUrl"]==""){
							dblog("无跳转地址");
							alert("登陆成功,但无跳转地址");
						}else{
							dblog("即将在0.1秒后跳转地址:"+ret["skipUrl"]);
							setTimeout(function(){
								self.location=ret["skipUrl"];
							}, 100);
						}

					}else{
						//登录失败
						dblog("登录失败,密码错误");
						alert("抱歉,账号与密码不匹配,请重试.");
						setProcess(4);
					}
				},
				dataType:"json",
				error:function(err){

					console.log("登录错误 ["+err.status+"]"+err.statusText);
					dblog("登录错误 ["+err.status+"]"+err.statusText);
					setProcess(4);
					alert("请求失败,请重试.若再次出现此错误请联系管理员");
				}
			}
		);
	},500);
	// setProcess(3);


}

function rsaCallback(ret){
	//生成rsa密钥回调函数
	console.log(ret);
	if(ret["status"]==2){
		var date=new Date();
		dblog("密钥生成成功");
		dblog("公钥\n"+ret["pubKey"]);
		dblog("私钥\n"+ret["priKey"]);
		sessionStorage.setItem("cRsaPubK",ret["pubKey"]);
		sessionStorage.setItem("cRsaPriK",ret["priKey"]);
		sessionStorage.setItem("cRsaKTime",date.getTime());
		setProcess(2);//完成客户端密钥生成
		dblog("客户端的密钥对生成完成.");
	}else{
		dblog("密钥生成失败:"+ret["errMsg"]);
	}
}


function statup(){
	//启动时运行
	if(!window.sessionStorage){
		alert("抱歉,你的浏览器不支持'sessionStorage'功能,将影响系统正常使用.\n请更换到最新的 谷歌Chrome浏览器.");
	}else{
		dblog("[Init] SessionStorage支持---ok");
	}
	$.post(
		"./user/register/canRegister",
		function (ret) {
			if(ret['status']==0){
				//注册开放
				$("#regLink").show();
			}else{
				$("#regLink").hide();
			}
		},"json"
	);
	var date = new Date();
	refreshVCodeuName();

}

function refreshVCodeuName(){
	$('#vCodeuName').val("");
	$('#vCodeImguName').attr("src","data:image/ico;base64,AAABAAEAAQEAAAEAIAAwAAAAFgAAACgAAAABAAAAAgAAAAEAIAAAAAAACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAAA==");
	var date = new Date();
	$('#vCodeImguName').attr("src","./user/getv?"+date.getTime());
}
function refreshVCodeuPass(){
	$('#vCodeuPass').val("");
	$('#vCodeImguPass').attr("src","data:image/ico;base64,AAABAAEAAQEAAAEAIAAwAAAAFgAAACgAAAABAAAAAgAAAAEAIAAAAAAACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAAA==");
	var date = new Date();
	$('#vCodeImguPass').attr("src","./user/getv?"+date.getTime());
}

function dealRetErr(dat){
	if(dat["status"]!=0){
		//服务器出错
		alert("服务器出错:"+dat["errMsg"]+" (错误码:"+dat["status"]+")");
		return true;
	}
	return false;
}

function asJsLoadComp() {
	setProcess(1);
	if(typeof(generateKey)!="function"){
		alert("您的浏览器过旧不支持加密, 请使用新版的 谷歌浏览器 或 火狐浏览器.");
		return;
	}
	if(sessionStorage.getItem("cRsaPubK")!=null && sessionStorage.getItem("cRsaPriK")!=null && sessionStorage.getItem("cRsaKTime")!=null){
		//已存在密钥
		var date=new Date();
		if (Number(sessionStorage.getItem("cRsaKTime"))+7200000 < date.getTime()) {
			//客户端密钥对过时
			dblog("密钥已存在,但超过2小时,正在重新生成");
			var date=new Date();
			sessionStorage.setItem("cRsaKGT",date.getTime());
			setTimeout(function(){
			//延时1秒生成密钥,防止浏览器卡顿
				generateKey(function(a){rsaCallback(a)});
			},1000);
		}else{
			//客户端密钥未过时
			setProcess(2);
			dblog("密钥已存在,未过期,无需生成");
		}
		
	}else{
		//未生成密钥
		dblog("客户端的密钥对开始生成.");
		var date=new Date();
		sessionStorage.setItem("cRsaKGT",date.getTime());
		setTimeout(function(){
		//延时1秒生成密钥,防止浏览器卡顿
			generateKey(function(a){rsaCallback(a)});
		},1000);
	}
}

function clearss(){
	try{
		sessionStorage.clear();
		dblog("清空ss");
	}catch(err){
		dblog(err);
	}
	
}
function verCodeKeypressuName(event) {  
    if (event.keyCode == 13) {  
    	loginNextSetp();
    	// $("#uPass").focus();
    }  
}
function verCodeKeypressuPass(event) {  
    if (event.keyCode == 13) {  
    	loginNextSetp();
    	// $("#uPass").focus();
    }  
}
function passKeypress(event) {  
    if (event.keyCode == 13 || event.keyCode==9) {  
    	if($('#uName').val()==""){
    		$('#uName').focus();
    	}else{
    		loginButPress();
    	}
    	
    } 
}

function uNameKeypress(event){
	if (event.keyCode == 13) {  
    	$('#vCodeuName').focus();
    } 
}
statup();