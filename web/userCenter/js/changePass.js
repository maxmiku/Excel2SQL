var extraUrl="/userCenter";
var gobal_finishSubmit = false;
function changePass(){
	//提交按钮按下

	var sendJ = {};
	sendJ["username"] = sessionStorage.getItem("uname");
	sendJ["oPass"] = rsaEncrypt(sessionStorage.getItem("sRsaPubK"),hex_md5(hex_md5($('#inputPasswordOld').val()+sessionStorage.getItem("usalt"))+sessionStorage.getItem("sRsaPubK")));
	var aesKey=hex_md5($('#inputPasswordOld').val()+sessionStorage.getItem("usalt"));
	dblog(aesKey);
	sendJ["newPass"] = rsaEncrypt(sessionStorage.getItem("sRsaPubK"), aesEncrypt(aesKey,$('#inputPassword1').val()));
	sendJ["npmd"] = rsaEncrypt(sessionStorage.getItem("sRsaPubK"), hex_md5($('#inputPassword1').val()+sessionStorage.getItem("usalt")) );
	sendJ["token"] = rsaEncrypt(sessionStorage.getItem("sRsaPubK"),hex_md5(sendJ["username"]+sendJ["oPass"]+sendJ["newPass"]+sessionStorage.getItem("token")));
	dblog("即将发出的数据:\n"+JSON.stringify(sendJ));


	$.ajax(
		{
			type:"POST",
			url:extraUrl+"/user/manage/changePass",
			data:JSON.stringify(sendJ),
			success:function(r){
				if(dealRetErr(r)){return;}
				var ret = r['retData'];
				var changeStatus = parseInt(rsaDecrypt(sessionStorage.getItem("cRsaPriK"),ret["changeStatus"]));
				if(changeStatus==1){
					//改密码成功
					gobal_finishSubmit=true;
					dblog("改密码成功");
					$("#finishSubmitBtn").html("修改密码");
					$("#finishSubmitBtn").removeClass("disabled");
					resetRegPassBox();
					alert("密码修改成功");
					var newSalt=rsaDecrypt(sessionStorage.getItem("cRsaPriK"),ret["usalt"]);
					sessionStorage.setItem("usalt",newSalt);


				}else if(changeStatus==-1){
					//原密码错误
					dblog("登录失败,密码错误");
					alert("抱歉,原密码不正确,请重试.");
				}else if(changeStatus==-2){
					//新密码过于简单
					dblog("新密码过于简单");
					alert("新密码过于简单,请使用强密码.");
				}else{
					dblog("服务器错误changeStatus:"+changeStatus);
					alert("服务器错误");
				}
			},
			dataType:"json",
			error:function(err){

				console.log("更改密码时错误 ["+err.status+"]"+err.statusText);
				dblog("更改密码时错误 ["+err.status+"]"+err.statusText);
				alert("请求失败,请重试.若再次出现此错误请联系管理员");
			}
		}
	);

	return false;
}



function startup(){
	$("#unameBox").html(sessionStorage.getItem("uname"));
}
startup();
function dblog(text){
	var date=new Date();
	var timeStr=date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
	$('#debugLog').html($('#debugLog').html()+"\n["+timeStr+"] "+text);
	$("#debugLog").scrollTop($("#debugLog")[0].scrollHeight);
}
function dealRetErr(dat){
	if(dat["status"]!=0){
		//服务器出错
		alert("服务器出错:"+dat["errMsg"]+" (错误码:"+dat["status"]+")");
		return true;
	}
	return false;
}

startup();

$(function(){
	//上一步按钮
	$("#prevBtn").bind("click",function(){
		window.history.back();
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

	//修改密码(密码窗口)的刷新按钮样式
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

		if($('#inputPasswordOld').val()!=""){
			$("#oldPassBox").addClass("has-success");
		}else{
			$("#oldPassBox").removeClass("has-success");
		}
	});


	//密码强度计算
	$("#inputPassword1").bind("input",function(){
		checkPassword();
	});

	resetRegPassBox();

	//最终提交按钮
	$("#finishSubmitBtn").bind("click",function(e){
		if($(this).hasClass("disabled"))return;
		if($("#inputPasswordOld").val()!="" &&$("#inputPassword1").val()!="" && $("#inputPassword2").val()!=""){
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
					$("#finishSubmitBtn").html("修改密码");
					$('#passBox').removeClass("has-error");
					$("#finishSubmitBtn").removeClass("btn-danger");
				},3000);
				return;

			}else{
				console.log("验证通过,即将提交密码");

				gobal_finishSubmit=false;

				$("#finishSubmitBtn").html("正在提交");
				$("#finishSubmitBtn").removeClass("btn-success btn-danger");
				$("#finishSubmitBtn").addClass("btn-default disabled");


				//10秒后取消禁用 防止运行出错
				setTimeout(function(){
					$("#finishSubmitBtn").removeClass("disabled");

					if(!gobal_finishSubmit){
						//卡壳了
						$("#finishSubmitBtn").html("提交超时 请重试");
						$("#finishSubmitBtn").removeClass("btn-success btn-default");

						$("#finishSubmitBtn").addClass("btn-danger");
					}
				},10000);



				changePass();


			}
		}
	});
})

/**
 * 刷新修改密码(密码)窗格
 */
function resetRegPassBox(){
	$("#finishSubmitBtn").html("修改密码");
	$("#finishSubmitBtn").removeClass("btn-success btn-danger disabled");
	$("#finishSubmitBtn").addClass("btn-default");
	$("#inputPasswordOld").val("");
	$("#inputPassword1").val("");
	$("#inputPassword2").val("");

	$("#passStrengthBox").removeClass("alert-success alert-warning");
	$("#passStrengthBox").addClass("alert-info");

	$("#passStrength").css("width","0%");
	$("#passStrength").removeClass("progress-bar-success progress-bar-warning progress-bar-danger");
	$("#passStrength").addClass("progress-bar-info");

	$("#passBox").removeClass("has-warning has-success");
	$("#confirmPassBox").removeClass("has-warning has-success");
	$("#oldPassBox").removeClass("has-success")

}


/**
 * 设置密码强度等级进度条
 * @param  {int} level 等级 最小为-1(为空) 最大为4
 * @return {[type]}       [description]
 */
function passStrengthProcessSet(level) {
	$("#passStrengthBox").removeClass("alert-success alert-warning alert-info");
	$("#passStrength").removeClass("progress-bar-success progress-bar-warning progress-bar-danger progress-bar-info");
	$("#passStrength").css("width", ((level + 1) * 20) + "%");
	$("#passBox").removeClass("has-warning has-success");

	switch (level) {
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



