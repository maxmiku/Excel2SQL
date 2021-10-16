var gobal_nextSubmit=false;//下一步提交成功标志
var gobal_finalSubmit=false;//最终提交成功标志
var gobal_rsaGen=false;//rsa生成成功标志

// var debug=false;
// var cRsaPriK="MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJ6uvFD6YbnTtjL6aIC3TBVm/Nxp318t95a2NNM6Dh0VKyYSU6V10hc/1wyNDBI48BjPE9kww6eCSnH3SKaHwpfUNSZKoTgqmQ1SwvkteFCWkZ8E5FY7sqZVRWEgW8Dbwt7mvhM+pdJzXbYvwTLP2vUZ514ogZobHg7ggXRSYZkdAgMBAAECgYANiqJZ88AOvGU2TP3JjDrjMa5+/c7436KbGzH/W5s0Oqf7wAEg4+DZqJJIySmgTXkZ2OQb+wgbXahOOPjbc0b1DRjHnJ5jCN5Vb1PS7tC/WjR/GzowgTlZgy9lR6S395UX/QmDHquVVZdNAAG6mfWMv9SGKFO8O3UPaP4mc2Y6oQJBAMyjn1y7+5+5VItbinndGsl+3jmvL3Qph6Z5Qg2HzLu3WR8tOkGyKCVOXYYmVJN/r6iBiWRUOdbYoUl6eSoJ/n0CQQDGglUHgX0pvK4A2XQwIuvJlBhKhk8Ne/jWgpZ42Iah7La3iJnnXyoKGlnIBb6QNO0muoYw/1BOSD/NcwJdWechAkEAt5mJzlq+PCS8LLf5urJcaTHRUbHSgocMNJkQYTFYx4aNiEI8xVqBWE9B660SLq/duvJVgVVr+02rD1C5yrTixQJAcf64W5YVzVkHx38YxOfY5aqBBJL6ZLDzpiPZy030ippIFz9/uimhF6ooMsBxvZhXM61bkFmdwUamNRI9omQVYQJAdGFaFiflhPq9U7SP9IKqg83b9pMlF/IP/pO1F9da0fAKjcqbYPiHadRA/bBs176dh4GQIssyeUG05BaJeqIXWw==";
// var cRsaPubK="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCerrxQ+mG507Yy+miAt0wVZvzcad9fLfeWtjTTOg4dFSsmElOlddIXP9cMjQwSOPAYzxPZMMOngkpx90imh8KX1DUmSqE4KpkNUsL5LXhQlpGfBORWO7KmVUVhIFvA28Le5r4TPqXSc122L8Eyz9r1GedeKIGaGx4O4IF0UmGZHQIDAQAB";

$(function(){
   
    //注册(用户名窗口)的刷新按钮样式 
    $("#regNameBox input").bind("input",function(){

    	//邮件地址有效性检查
    	var emailCheck = true;
		var atpos=$('#inputEmail').val().indexOf("@");
		var dotpos=$('#inputEmail').val().lastIndexOf(".");
		if (atpos<1 || dotpos<atpos+2 || dotpos+2>=$('#inputEmail').val().length)emailCheck=false;

		if(this == $("#inputUsername")[0]){
			if($('#inputUsername').val()!=""){
				$("#usernameBox").addClass("has-success");
				$("#usernameBox").removeClass("has-warning");
			}else{
				$("#usernameBox").removeClass("has-success");
				$("#usernameBox").addClass("has-warning");
			}
		}else if(this==$("#inputEmail")[0]){
			if(emailCheck){
				$("#emailBox").addClass("has-success");
				$("#emailBox").removeClass("has-warning");
			}else{
				$("#emailBox").removeClass("has-success");
				$("#emailBox").addClass("has-warning");
			}
		}else if(this==$("#inputVCode")[0]){
			if(($("#inputVCode").val()).length>3){
				$("#vCodeOutBox").addClass("has-success");
				$("#vCodeOutBox").removeClass("has-warning");
			}else{
				$("#vCodeOutBox").removeClass("has-success");
				$("#vCodeOutBox").addClass("has-warning");
			}
		}

    	checkNextCondition(emailCheck);
    });

    // $('#inputVCode').bind("focus",function(){
    // 	if($('#inputVCode').val()=="")
    // 		refreshVCode();
    // });

	$("#inputEmail").bind("blur",function () {
		if($('#inputVCode').val()=="")
			refreshVCode();
	});

    //密码输入框开始
    $("#inputPassword2").bind("blur",function(){
    	if($("#inputPassword2").val()!=$("#inputPassword1").val() && $("#inputPassword2").val()!=""){
    		$('#confirmPassBox').addClass("has-error");
    		$('#passNoSame').slideDown("fast");
    	}
    });
    $("#inputPassword1").bind("blur",function(){
    	$("#passStrengthBox").slideUp("fast");
    	if($("#inputPassword2").val()!=$("#inputPassword1").val() && $("#inputPassword2").val()!=""){
    		$("#inputPassword2").val("");
    	}
    });
    $("#inputPassword2").bind("focus",function(){
    	setTimeout(function(){
    		$('#confirmPassBox').removeClass("has-error");
    		$('#passNoSame').slideUp("fast");
    	},1000);
    	
    });
    $("#inputPassword1").bind("focus",function(){
    	$("#passStrengthBox").slideDown("fast");
    })
    //密码输入框结束
    


    //下一步上传按钮
    $("#nextSubmitBtn").bind("click",function(e){
    	if($(this).hasClass("disabled"))return;
    	if($('#inputUsername').val()!="" && $('#inputEmail').val()!="" && $("#inputVCode").val()!="" && $("#agreeRule").is(':checked')){
    		//上传
    		e.preventDefault();
			e.stopPropagation();
			if(!validateEmail($('#inputEmail').val()))return;

			gobal_nextSubmit=false;

			$("#nextSubmitBtn").html("正在提交");
			$("#nextSubmitBtn").removeClass("btn-success btn-danger");
    		$("#nextSubmitBtn").addClass("btn-default disabled");

    		//10秒后取消禁用 防止运行出错
    		setTimeout(function(){
    			$("#nextSubmitBtn").removeClass("disabled");

    			if(!gobal_nextSubmit){
    				//卡壳了
    				$("#nextSubmitBtn").html("提交超时 请重试");
					$("#nextSubmitBtn").removeClass("btn-success btn-default");
					$("#nextSubmitBtn").addClass("btn-danger");
    			}
    		},10000);

    		postRegsiterNameData();


    	}
   
    });

    //上一步按钮
    $("#prevBtn").bind("click",function(){
    	if($(this).hasClass("disabled"))return;
    	console.log("上一步按钮");

    	$("#regPassBox").hide();
    	$("#regNameBox").fadeIn(300);
    	resetRegPassBox();
    });

    //注册(密码窗口)的刷新按钮样式 
    $("#regPassBox input").bind("input",function(){
    	if(this==$("#inputPassword2")[0]){
    		if($("#inputPassword2").val()==$("#inputPassword1").val()){
    			$("#confirmPassBox").addClass("has-success");
				$("#confirmPassBox").removeClass("has-warning");
			}else{
				if($("#inputPassword2").val()!=""){
					$("#confirmPassBox").removeClass("has-success");
					$("#confirmPassBox").addClass("has-warning");
				}else{
					$("#confirmPassBox").removeClass("has-success has-warning");
				}
				
			}
    	}

    	if($('#inputPassword1').val()!="" && $("#inputPassword2").val()!="" && $("#inputPassword2").val()==$("#inputPassword1").val() && checkPassword()>1){
    		$("#finishSubmitBtn").addClass("btn-success");
    		$("#finishSubmitBtn").removeClass("btn-default");
    	}else{
    		$("#finishSubmitBtn").removeClass("btn-success");
    		$("#finishSubmitBtn").addClass("btn-default");
    	}
    });


    //密码强度计算
    $("#inputPassword1").bind("input",function(){
    	checkPassword();
    });

    //最终提交按钮
    $("#finishSubmitBtn").bind("click",function(e){
    	if($(this).hasClass("disabled"))return;
    	if($("#inputPassword1").val()!="" && $("#inputPassword2").val()!=""){
	    	if($("#inputPassword2").val()!=$("#inputPassword1").val()){
	    		e.preventDefault();
				e.stopPropagation();
				$('#confirmPassBox').addClass("has-error");
	    		$('#passNoSame').slideDown("fast");
				$("#inputPassword2").val("");
				$("#inputPassword2").focus();
				setTimeout(function(){
		    		$('#confirmPassBox').removeClass("has-error");
		    		$('#passNoSame').slideUp("fast");
		    	},3000);
		    	return;
	    	}else if(checkPassword()<2){
	    		e.preventDefault();
				e.stopPropagation();
				$('#passBox').addClass("has-error");
	    		
				$("#inputPassword1").focus();
				$("#finishSubmitBtn").html("密码太弱 请重试");
				$("#finishSubmitBtn").removeClass("btn-success");
				$("#finishSubmitBtn").addClass("btn-danger");
				setTimeout(function(){
					$("#finishSubmitBtn").html("注册");
		    		$('#passBox').removeClass("has-error");
		    		$("#finishSubmitBtn").removeClass("btn-danger");
		    	},3000);
		    	return;

	    	}else{
	    		console.log("验证通过,即将提交密码");

	    		gobal_finalSubmit=false;

				$("#finishSubmitBtn").html("正在提交");
				$("#finishSubmitBtn").removeClass("btn-success btn-danger");
	    		$("#finishSubmitBtn").addClass("btn-default disabled");
	    		$("#prevBtn").addClass("disabled");

	    		//10秒后取消禁用 防止运行出错
	    		setTimeout(function(){
	    			$("#finishSubmitBtn").removeClass("disabled");

	    			if(!gobal_finalSubmit){
	    				//卡壳了
	    				$("#finishSubmitBtn").html("提交超时 请重试");
						$("#finishSubmitBtn").removeClass("btn-success btn-default");
						$("#prevBtn").removeClass("disabled");
						$("#finishSubmitBtn").addClass("btn-danger");
	    			}
	    		},10000);



	    		postRegsiterPassData();


	    	}
	    }
    });

    resetRegNameBox();
    resetRegPassBox();

	$.post(
		"./user/register/canRegister",
		function (ret) {
			if(ret['status']==0){
				//注册开放
				$("#regStopBox").hide();
			}else{
				$("#regStopBox").slideDown("fast");
				$("input, button").attr("disabled","disabled");
				$("#regNameBox form").hide();
			}
		},"json"
	);

});

/**
 * 刷新注册(用户名)窗格 
 */
function resetRegNameBox(){
	$("#nextSubmitBtn").html("下一步");
	$("#nextSubmitBtn").removeClass("btn-success btn-danger disabled");
	$("#nextSubmitBtn").addClass("btn-default");
	// $("#usernameBox").removeClass("has-warning has-success");
	// $("#emailBox").removeClass("has-warning has-success");
	$("#vCodeOutBox").removeClass("has-warning has-success");
	$("#inputVCode").val("");
	// refreshVCode();
}

/**
 * 刷新注册(密码)窗格
 */
function resetRegPassBox(){
	$("#finishSubmitBtn").html("注册");
	$("#finishSubmitBtn").removeClass("btn-success btn-danger disabled");
	$("#finishSubmitBtn").addClass("btn-default");
	$("#inputPassword1").val("");
	$("#inputPassword2").val("");

	$("#passStrengthBox").removeClass("alert-success alert-warning");
	$("#passStrengthBox").addClass("alert-info");

	$("#passStrength").css("width","0%");
	$("#passStrength").removeClass("progress-bar-success progress-bar-warning progress-bar-danger");
	$("#passStrength").addClass("progress-bar-info");

	$("#passBox").removeClass("has-warning has-success");
	$("#confirmPassBox").removeClass("has-warning has-success");

}

/**
 * 检查条件并更改next按钮的样式
 * @param emailCheck
 */
function checkNextCondition(emailCheck){

	// //邮件地址有效性检查
	// var emailCheck = true;
	// var atpos=$('#inputEmail').val().indexOf("@");
	// var dotpos=$('#inputEmail').val().lastIndexOf(".");
	// if (atpos<1 || dotpos<atpos+2 || dotpos+2>=$('#inputEmail').val().length)emailCheck=false;

	if($('#inputUsername').val()!="" && emailCheck && $("#inputVCode").val()!="" && $("#agreeRule").is(':checked') && gobal_rsaGen){
		$("#nextSubmitBtn").addClass("btn-success");
		$("#nextSubmitBtn").removeClass("btn-default");
	}else{
		$("#nextSubmitBtn").removeClass("btn-success");
		$("#nextSubmitBtn").addClass("btn-default");
	}
}

/**
 * 设置密码强度等级进度条
 * @param  {int} level 等级 最小为-1(为空) 最大为4
 * @return {[type]}       [description]
 */
function passStrengthProcessSet(level) {
	$("#passStrengthBox").removeClass("alert-success alert-warning alert-info");
	$("#passStrength").removeClass("progress-bar-success progress-bar-warning progress-bar-danger progress-bar-info");
	$("#passStrength").css("width",((level+1)*20)+"%");
	$("#passBox").removeClass("has-warning has-success");

	switch(level){
		case 0:
			$("#passBox").addClass("has-warning");
			$("#passStrength").addClass("progress-bar-danger");
			$("#passStrengthBox").addClass("alert-warning");
			break;
		case 1:
			$("#passBox").addClass("has-warning");
			$("#passStrength").addClass("progress-bar-danger");
			$("#passStrengthBox").addClass("alert-warning");
			break;
		case 2:
			$("#passBox").addClass("has-success");
			$("#passStrength").addClass("progress-bar-warning");
			$("#passStrengthBox").addClass("alert-success");
			break;
		case 3:
			$("#passBox").addClass("has-success");
			$("#passStrength").addClass("progress-bar-success");
			$("#passStrengthBox").addClass("alert-success");
			break;
		case 4:
			$("#passBox").addClass("has-success");
			$("#passStrength").addClass("progress-bar-success");
			$("#passStrengthBox").addClass("alert-success");
			break;
		default:
			$("#passStrength").addClass("progress-bar-info");
			$("#passStrengthBox").addClass("alert-info");
			break;
	}
}

/**
 * 编辑框改变时更新密码强度指示条
 * @return {int} 返回强度
 */
function checkPassword() {
	var pw = $("#inputPassword1").val();

	if(pw.length==0){
		passStrengthProcessSet(-1);
		return;
	}

	var strength = checkStrong(pw);

	console.log("密码强度:"+strength);

	passStrengthProcessSet(strength);

	return strength;
}


/**
 * 刷新验证码
 */
function refreshVCode() {
	$('.vCodeBox').show();
    $('#inputVCode').val("");
    $('#vCodeImg').attr("src","data:image/ico;base64,AAABAAEAAQEAAAEAIAAwAAAAFgAAACgAAAABAAAAAgAAAAEAIAAAAAAACAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAAA==");
    var date = new Date();
    $('#vCodeImg').attr("src","./user/getv?"+date.getTime());
} 

/**
 * 检测是否为邮件地址
 * @param  {[type]} x [邮件地址文本]
 * @return {boolean}   [是否为邮件地址]
 */
function validateEmail(x){
  var atpos=x.indexOf("@");
  var dotpos=x.lastIndexOf(".");
  if (atpos<1 || dotpos<atpos+2 || dotpos+2>=x.length){
    // alert("不是一个有效的 e-mail 地址");
    $('#emailBox').addClass("has-error");
    $('#emailError').slideDown("fast");
    $('#inputEmail').focus();
    setTimeout(function(){
		$('#emailBox').removeClass("has-error");
		$('#emailError').slideUp("fast");
	},3000);
    return false;
  }
  return true;
}

/**
 * 发送用户名查重 并 拉key
 */
function postRegsiterNameData() {
	var sendJ = {};
	sendJ["uname"]=$('#inputUsername').val();
	sendJ["email"]=$("#inputEmail").val();
	sendJ["verifyCode"]=$('#inputVCode').val();
	sendJ["token"]=hex_md5(sendJ["uname"]+sendJ["email"]+sendJ["verifyCode"]);
	if(!gobal_rsaGen){
		//密钥未生成完成
		setTimeout(function () {
			startGenerateKey();
		},10);
		alert("密钥还在准备中, 请5秒后重试");
		return;
	}
	sendJ["cRsaPubKey"]=sessionStorage.getItem("cRsaPubK");
	$.ajax({
		type:"POST",
		url:"./user/register/checkUser",
		data:JSON.stringify(sendJ),
		success:function(r){
			dblog("checkUser返回\n"+JSON.stringify(r));
			if(dealRetErr(r)){
				refreshVCode();
				gobal_nextSubmit=true;

				$("#nextSubmitBtn").html("提交失败 请重试");
				$("#nextSubmitBtn").removeClass("btn-success btn-default disabled");
				$("#nextSubmitBtn").addClass("btn-danger");
				return;
			}
			var ret=r["retData"];
			console.log(ret);
			if(ret['regUserStatus']==0){
				//由此用户 并且服务器返回公钥
				dblog("服务器无此用户,返回公钥!\n"+ret['sRsaPubKey']);
				sessionStorage.setItem("sRsaPubK",ret['sRsaPubKey']);
				
				if(ret["usalt"]!=""){
					var usalt = rsaDecrypt(sessionStorage.getItem("cRsaPriK"),ret["usalt"]);
					if(usalt==null){
						alert("错误,获取加密密钥错误");
						refreshVCode();
						gobal_nextSubmit=true;

						$("#nextSubmitBtn").html("提交失败 请重试");
						$("#nextSubmitBtn").removeClass("btn-success btn-default disabled");
						$("#nextSubmitBtn").addClass("btn-danger");
						return;
					}
					sessionStorage.setItem("usalt",usalt);
					dblog("salt:"+usalt);
				}else{
					sessionStorage.setItem("usalt","");
				}
				
				
				var token = rsaDecrypt(sessionStorage.getItem("cRsaPriK"),ret["token"]);
				if(token==null){
					alert("错误,获取用户token错误");
					refreshVCode();
					gobal_nextSubmit=true;

					$("#nextSubmitBtn").html("提交失败 请重试");
					$("#nextSubmitBtn").removeClass("btn-success btn-default disabled");
					$("#nextSubmitBtn").addClass("btn-danger");
					return;
				}
				sessionStorage.setItem("token",token);
				dblog("token:"+token);
				
				resetRegPassBox();
		    	$("#regNameBox").hide();
		    	$("#regPassBox").fadeIn(300);
		    	$("#inputPassword1").focus();
		    	gobal_nextSubmit=true;
		  //   	$("#nextSubmitBtn").html("下一步");
				// $("#nextSubmitBtn").removeClass("btn-danger btn-default disabled");
				// $("#nextSubmitBtn").addClass("btn-success");
				// $("#inputVCode").val("");
				resetRegNameBox();
				
			}else{
				//存在该用户
				dblog("用户名已存在");
				refreshVCode();
				gobal_nextSubmit=true;

				$("#nextSubmitBtn").html("用户名已存在 请更换用户名后重试");
				$("#nextSubmitBtn").removeClass("btn-success btn-default disabled");
				$("#nextSubmitBtn").addClass("btn-danger");

				$("#inputUsername").focus();

				
			}
		},
		dataType:"json",
		error:function(err){
			console.log("检查用户名错误 ["+err.status+"]"+err.statusText);
			dblog("检查用户名错误 ["+err.status+"]"+err.statusText);
			alert("请求失败(Code:"+err.status+"),请重试.若再次出现此错误请联系管理员");
			refreshVCode();
			gobal_nextSubmit=true;
			$("#nextSubmitBtn").html("提交失败 请重试");
			$("#nextSubmitBtn").removeClass("btn-success btn-default disabled");
			$("#nextSubmitBtn").addClass("btn-danger");
			
		}
	});
}

/**
 * 上传注册用户的密码
 */
function postRegsiterPassData() {
	var sendJ={};
	sendJ["uname"]=$('#inputUsername').val();

	var sRsaPubK = sessionStorage.getItem("sRsaPubK");
	var passmd5 = hex_md5($("#inputPassword1").val()+sessionStorage.getItem("usalt"));

	sendJ["password"]=rsaEncrypt(sRsaPubK,passmd5);
	if(sendJ["password"]==false){
		//加密密码失败
		gobal_finalSubmit=true;
		
		$("#finishSubmitBtn").html("密码加密失败 请重试");
		$("#finishSubmitBtn").removeClass("btn-success btn-default disabled");
		$("#prevBtn").removeClass("disabled");
		$("#finishSubmitBtn").addClass("btn-danger");
		alert("密码加密失败 请重试. \n如多次出现此错误, 请使用页面底部的联系方式求助.");
		return;
	}
	var tokenmd5=hex_md5(sendJ["uname"]+$("#inputEmail").val()+hex_md5(passmd5+sessionStorage.getItem("usalt"))+sessionStorage.getItem("token"));
	sendJ["token"]=rsaEncrypt(sRsaPubK,tokenmd5);

	console.log(passmd5+"  "+sessionStorage.getItem("usalt"));
	
	dblog("向服务器 /regUser 发送:"+JSON.stringify(sendJ));
	$.ajax(
		{
			type:"POST",
			url:"./user/register/registerUser",
			data:JSON.stringify(sendJ),
			success:function(r){
				dblog("checkUser返回\n"+JSON.stringify(r));
				if(dealRetErr(r)){
					gobal_finalSubmit=true;
					$("#finishSubmitBtn").html("提交错误 请重试");
					$("#finishSubmitBtn").removeClass("btn-success btn-default disabled");
					$("#prevBtn").removeClass("disabled");
					$("#finishSubmitBtn").addClass("btn-danger");
					return;
				}

				var ret = r['retData'];
				var regStatusRaw = rsaDecrypt(sessionStorage.getItem("cRsaPriK"),ret["regStatus"]);
				if(regStatusRaw==undefined){
					gobal_finalSubmit=true;
					$("#finishSubmitBtn").html("注册错误");
					$("#finishSubmitBtn").removeClass("btn-success btn-default disabled");
					$("#prevBtn").removeClass("disabled");
					$("#finishSubmitBtn").addClass("btn-danger");
					alert("抱歉,未知注册状态. 请尝试登录或重新注册.");
					return;
				}
				var regStatus = parseInt(regStatusRaw);
				if(regStatus==0){
					//注册成功
					alert("注册成功,正在跳转到登录页面...");
					window.location.href="login.html";

				}else{
					//注册失败
					alert("抱歉,注册失败. (状态码:"+regStatus+")");
					
					gobal_finalSubmit=true;
					$("#finishSubmitBtn").html("注册错误");
					$("#finishSubmitBtn").removeClass("btn-success btn-default disabled");
					$("#prevBtn").removeClass("disabled");
					$("#finishSubmitBtn").addClass("btn-danger");
				}
			},
			dataType:"json",
			error:function(err){
				console.log("上传用户密码错误 ["+err.status+"]"+err.statusText);
				alert("请求失败,请重试.若再次出现此错误请联系管理员");
				gobal_finalSubmit=true;
				$("#finishSubmitBtn").html("提交错误 请重试");
				$("#finishSubmitBtn").removeClass("btn-success btn-default disabled");
				$("#prevBtn").removeClass("disabled");
				$("#finishSubmitBtn").addClass("btn-danger");
			}
		}
	);
}


/**
 * 处理服务器返回的错误 错误时返回真
 * @param  {[type]} dat [description]
 * @return {boolean}     有错误时为真
 */
function dealRetErr(dat){
	if(dat["status"]!=0){
		//服务器出错
		alert("服务器出错:"+dat["errMsg"]+" (错误码:"+dat["status"]+")");
		return true;
	}
	return false;
}

function dblog(msg){
	console.log(msg);
}



//开始创建密钥
function startGenerateKey(){
	// if(debug){
	// 	var ret = {"status":2,"pubKey":cRsaPubK,"priKey":cRsaPriK};
	// 	rsaCallback(ret);
	// 	return;
	// }
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
			gobal_rsaGen=false;
			setTimeout(function(){
				generateKey(function(a){rsaCallback(a)});
			},500);
		}else{
			//客户端密钥未过时
			gobal_rsaGen=true;
			dblog("密钥已存在,未过期,无需生成");
		}
		
	}else{
		//未生成密钥
		dblog("客户端的密钥对开始生成.");
		gobal_rsaGen=false;
		var date=new Date();
		sessionStorage.setItem("cRsaKGT",date.getTime());
		setTimeout(function(){
			generateKey(function(a){rsaCallback(a)});
		},500);
	}
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
		
		gobal_rsaGen=true;


		//邮件地址有效性检查
		var emailCheck = true;
		var atpos=$('#inputEmail').val().indexOf("@");
		var dotpos=$('#inputEmail').val().lastIndexOf(".");
		if (atpos<1 || dotpos<atpos+2 || dotpos+2>=$('#inputEmail').val().length)emailCheck=false;
		checkNextCondition(emailCheck);

		dblog("客户端的密钥对生成完成.");
	}else{
		dblog("密钥生成失败:"+ret["errMsg"]);
	}
}








//CharMode函数
//测试某个字符是属于哪一类.
function CharMode(iN){
	if (iN>=48 && iN <=57) //数字
		return 1;
	if (iN>=65 && iN <=90) //大写字母
		return 2;
	if (iN>=97 && iN <=122) //小写
		return 4;
	else
		return 8; //特殊字符
}
//bitTotal函数
//计算出当前密码当中一共有多少种模式
function bitTotal(num){
	modes=0;
	for (i=0;i<4;i++){
		if (num & 1) modes++;
		num>>>=1;
	}
	return modes;
}
//checkStrong函数
//返回密码的强度级别
function checkStrong(sPW){
	if(sPW.length<6){
		return 0;
	}

	Modes=0;
	for (i=0;i<sPW.length;i++){
		//测试每一个字符的类别并统计一共有多少种模式.
		Modes|=CharMode(sPW.charCodeAt(i));
	}
	return bitTotal(Modes);
}
