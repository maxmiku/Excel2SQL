var fileid = "";
var dfield = [];
var fheader = [];
var fieldPairid="";
var excelRows = 0;//数据行数
var progress = 0;//当前状态 0未上传文件 1正在上传文件 2已上传
var dealStartTime = 0;//导入开始时间

//验证登录
$(function() {
	setNowWindow(0);
	$.ajax({
		url:"./checkAuth",
		type:"POST",
		dataType:"json",        
		success:function(r){
			if(dealRetErr(r))return;
			$('#user_logout').html(r['retData']['uname']+" 登出");
		},
		error:function(e){
			alert("获取用户登录状态失败, 请重试\nstatus:"+e.status+"\nstatusText:"+e.statusText);
			console.log(e);
			$('#user_logout').html("登入");
		}
	});


})



//文件上传
function uploadFile(errorCallback){
	if(progress==1){
		$('#uploadNotic font').html("文件正在上传,请稍候重试.");
		return;
	}
	if(fileid!="" && $("#fileuploadtor")[0].files[0]==undefined){
		setNowWindow(1);
	}
	if($("#fileuploadtor")[0].files[0]==undefined){
		$('#uploadNotic').removeClass("alert-info");
		$('#uploadNotic').addClass("alert-danger");
		$('#uploadNotic span').removeClass("glyphicon-info-sign");
		$('#uploadNotic span').addClass("glyphicon-warning-sign");
		$('#uploadNotic font').html(" 请上传文件后继续");
		setTimeout(function(){
			initUploadBox();
		},3000);
		if(errorCallback!=undefined)errorCallback();
		return;
	}
	if($("#fileuploadtor")[0].files[0].size>20971520){
		alert("文件过大,最大允许上传20M");
		if(errorCallback!=undefined)errorCallback();
		return;
	}
	var formData = new FormData();
	formData.append('myfile',$('#fileuploadtor')[0].files[0]);
	formData.append('test1','testtext');
	progress=1;
	$.ajax({
		url:"upload",
		type:"POST",
		async:true,
		processData: false,
		contentType: false,
		data:formData,
		dataType:"json",
		xhr:function(){                        
			myXhr = $.ajaxSettings.xhr();
			if(myXhr.upload){ // check if upload property exists
				myXhr.upload.addEventListener('progress',function(e){                            
					var loaded = e.loaded;//已经上传大小情况 
					var total = e.total;//附件总大小 
					var percent = Math.floor(100*loaded/total)+"%";//已经上传的百分比  
					console.log("已经上传了："+percent);
					// $('#uploadProcess').html("已经上传了："+percent);

					$('#uploadNotic font').html("正在上传： "+percent);


				}, false); // for handling the progress of the upload
			}
			return myXhr;
		},         
		success:function(r){
			// console.log(r);
			if(dealRetErr(r)){
				progress=0;
				if(errorCallback!=undefined)errorCallback();
				return;
			}
			progress=2;
			var data = r['retData'];
			if(data['uploadStatus']==0){
				//文件上传成功
				$('#uploadNotic font').html("文件上传成功, 正在处理数据, 请稍候");
				fileid=data["fileid"];
				getHeaderData(errorCallback,function(){setNowWindow(2)});
			}else{
				//文件上传失败
				alert("文件上传失败,请重试");
			}
		},
		error:function(e){
			alert("上传失败\nstatus:"+e.status+"\nstatusText:"+e.statusText);
			$('#uploadNotic font').html("文件上传失败");
			$('#fileuploadtor').val("");
			console.log(e);
			progress=0;
			if(errorCallback!=undefined)errorCallback();
		}


	});

	$('#fileuploadtor').val("");
}


//拉取表头
function getHeaderData(errorCallback,fullPairCallback){
	var sendj = {"fileid":fileid};
	$.ajax({
		url:"./getExHeader",
		type:"POST",
		data:JSON.stringify(sendj),
		dataType:"json",        
		success:function(r){
			// console.log(r);
			if(dealRetErr(r)){
				if(errorCallback!=undefined)
					errorCallback();
				return;
			}

			var data=r['retData'];
			dblog("拉取成功");
			if(data['fileid']!=fileid){
				alert("服务器与本地的文件不匹配,请重新上传");
				if(errorCallback!=undefined)
					errorCallback();
				return;
			}
			dfield = data['dfield'];
			fheader = data['fheader'];
			excelRows = data['excelRows'];

			setDatapairBox(fheader,dfield,function(){setNowWindow(1)},fullPairCallback);
			

		},
		error:function(e){
			alert("拉取表头信息失败\nstatus:"+e.status+"\nstatusText:"+e.statusText);
			console.log(e);
			if(errorCallback!=undefined)
				errorCallback();
		}


	});
}

/**
 * 获取示范数据
 * @param  {int} pressButton 当为1时代表为全部数据按钮按下触发,获取全部状态的按钮,状态会取反   2为遇错用默认数据
 * @return {[type]}             [description]
 */
function getExampleData(successCallback,errorCallback){
	// var fileid = "";
	var sendj = {};
	// var fieldPair=[];
	var getWhole = $("#getAllDataBtn input").is(":checked");
	var allowDefault = $("#allowDefault input").is(":checked");

	sendj['allData']=getWhole;
	sendj['allowDefault']=allowDefault;
	
	var userPair = getDatapair();
	if(userPair==null){
		return;
	}
	sendj['fieldPair']=userPair;

	sendj['fileid']=fileid;
	dblog("即将发送:\n"+JSON.stringify(sendj));
	$.ajax({
		url:"./setField",
		type:"POST",
		data:JSON.stringify(sendj),
		dataType:"json",
		success:function(r){
			// console.log(r);
			if(dealRetErr(r)){
				if(errorCallback!=undefined)
					errorCallback();
				return;
			}
			var data=r['retData'];
			if(data['fileid']!=fileid){
				alert("服务器与本地的文件不匹配,请重新上传");
			}
			dblog("拉取成功");
			displayForm2Table("preimportTable",data["headData"],data["formData"],function(a){tableHasNull(a)},function () {tableNothasNull()});
			

			fieldPairid = data["fieldPairid"];
			dblog("fieldPairid:"+fieldPairid);
			
			if(successCallback!=undefined)
				successCallback();
		},
		error:function(e){
			alert("设置字段信息失败\nstatus:"+e.status+"\nstatusText:"+e.statusText);
			console.log(e);
			if(errorCallback!=undefined)
				errorCallback();
		}
	});
}


//确认字段 开始导入
function confirmField(confirmImport,successCallback,errorCallback){
	// var fileid = "";
	var isUpdate = $('#updateDataBtn').hasClass("active");
	var isErrInterrupt=$('#errInterruptBtn').hasClass("active");
	var datamonth=0;


	if(!confirmImport){
		//取消导入
		if(!confirm("取消导入会清空先前您上传的设置,是否继续?")){
			if(errorCallback!=undefined)errorCallback();
			return;
		}
		datamonth = 0;
	}else{
		var monthStr = trim($('#datamonth').val());
		if(monthStr.length!=6){
			alert("你输入的内容不为六位数,请重新输入");
			if(errorCallback!=undefined)errorCallback();
			return;
		}

		datamonth = Number(monthStr);
	}
	
	var sendj = {
		"fileid":fileid,
		"fieldPairid":fieldPairid,
		"confirm":confirmImport,
		"update":isUpdate,
		"errInterrupt":isErrInterrupt,
		"month":datamonth
	};
	dblog("即将发送:\n"+JSON.stringify(sendj));
	$.ajax({
		url:"./confirmField",
		type:"POST",
		data:JSON.stringify(sendj),
		dataType:"json",        
		success:function(r){
			// console.log(r);
			if(dealRetErr(r)){
				if(errorCallback!=undefined)
					errorCallback();
				return;
			}

			var data=r['retData'];
			dblog("提交成功,返回数据"+JSON.stringify(data));
			if(data["confirmStatus"]<0){
				var errStr = "";
				switch(data["confirmStatus"]){
					case -1:
						errStr="当前文件正在转换中,请查看进度";
						break;
					case -2:
						errStr="当前文件已在队列中,但更新队列中的数据失败,建议重试."
						break;
					case -3:
						errStr="服务器已停止服务,请联系管理员";
						break;
					case 1:
						errStr="导入已取消";
						break;
					default:
						errStr="服务器出现未知错误 错误码:"+data["confirmStatus"]+" 请联系管理员";
						break;			
				}
				alert("确认导入失败! "+errStr);
				if(errorCallback!=undefined)
					errorCallback();
				return;
			}else{
				if(confirmImport){
					//文件开始进入队列转换
					
					dealStartTime=new Date().getTime();

					if(successCallback!=undefined){
						successCallback();
					}


					

				}else{
					//文件取消导入成功
					alert("文件取消导入成功");
					if(successCallback!=undefined)
						successCallback();
				}
			}
		},
		error:function(e){
			alert("确定字段失败\nstatus:"+e.status+"\nstatusText:"+e.statusText);
			console.log(e);
			if(errorCallback!=undefined)
				errorCallback();
		}


	});
}


/**
 * 获取当前转换进度
 * @param  {[type]} loop            循环获取标志
 * @param  {[type]} successCallback 转换完成时的回调 附带json对象{"i":"文件id","s":当前状态,"t",开始时间,"p":进度最大100,"e":[{"r":行,"m":"错误文本"},{...}]}
 * @param  {[type]} errorCallback   [description]
 * @return {[type]}                 [description]
 */
function getProcess(loop,successCallback,errorCallback){
	// var fileid = "";
	var usedTime = parseInt((new Date().getTime()-dealStartTime)/1000);
	$('#importing_time').html(usedTime);

	var sendj = {"fileid":fileid};
	$.ajax({
		url:"./getProcess",
		type:"POST",
		data:JSON.stringify(sendj),
		dataType:"json",        
		success:function(r){
			// console.log(r);
			if(dealRetErr(r))return;

			var data=r['retData'];
			dblog("拉取成功");
			$('#fheader').html("");
			$('#dfield').html("");
			if(data['fileid']!=fileid){
				alert("服务器与本地的文件不匹配,请重新上传文件.");
				if(errorCallback!=undefined)errorCallback();
				return;
			}
			var processData = data['process'];
			$('#importing_errorCount').html(data['ec']);
			if(processData==-1.0){
				// $('#nowProcessNotic').html('找不到指定的文件所对应的进度.');
				alert("找不到指定的文件所对应的进度,请重新开始导入.");
				if(errorCallback!=undefined)errorCallback();
				return;
			}else if(processData==-2.0){
				$('#importing_status').html("等候处理队列中, 队列长度: "+data['ql']);
				// $('#nowProcessNotic').html('当前项目在队列中.');
			}else if(processData==-3.0){
				$('#importing_status').html(" 导入结束, 正在生成报告");
				// console.log(data['importResult']);

				if(successCallback!=undefined)
					successCallback(data['importResult']);


				// dblog("转换结束返回对象:"+JSON.stringify(data['importResult']));
				// var errArray = data['importResult']['e'];
				// if(errArray.length==0){
				// 	$('#importResult').html("导入成功,没有错误");
				// }else{
				// 	$('#importResult').html("导入失败,有"+errArray.length+"个错误");
				// 	var errList = [];
				// 	for (var i = 0; i < errArray.length; i++) {
				// 		var errRowObject = errArray[i];
				// 		var errRow = [errRowObject["r"],errRowObject["m"]];
				// 		errList.push(errRow);
				// 	}
				// 	displayForm2Table("errResult",["行","报错内容"],errList);

				// }

				return;
			}else if(processData===-4.0){
				if(data['importResult']["e"]===undefined)
					data['importResult']["e"]=[{"r":-1,"m":"导入过程中发生致命错误, 请联系管理员"}];
				successCallback(data['importResult']);
				return;
			}else{
				$('#importing_status').html("导入中");
				$('#importing_progress').html(processData.toFixed(2)+"%");
				$('#importProgressBar').css("width",processData+"%");
				// $('#nowProcessNotic').html('转换中');
			}

			if(loop){
				setTimeout(function(){
					getProcess(true,successCallback,errorCallback);
				},1000);
			}
		},
		error:function(e){
			alert("拉取进度信息失败\nstatus:"+e.status+"\nstatusText:"+e.statusText);
			console.log(e);
			if(errorCallback!=undefined)errorCallback();
		}
	});
}



//处理导入结果 传入data['importResult']
function dealImportReport(data){
	$("#resultErrorTable").html("");
	if(data['e'].length==0){
		$("#result_success").css("display","block");
		$("#result_error").css("display","none");
		
	}else{

		$("#result_success").css("display","none");
		$("#result_error").css("display","block");
		$('#result_errcount').html(data['e'].length);
		dealError(data['e']);
	}
}
function dealError(errArray) {
	var errList = [];
	for (var i = 0; i < errArray.length; i++) {
		var errRowObject = errArray[i];
		var errRow = [errRowObject["r"],errRowObject["m"]];
		errList.push(errRow);
	}
	displayForm2Table("resultErrorTable",["行","报错内容"],errList);
}
//处理导入结果结束





function dealRetErr(dat){
	if(dat["status"]!=0){
		//服务器出错
		if(dat["status"]==-401){
			//未登录
			$('#uploadNotic font').html("正在跳转到登录界面, 请稍候");
			var alertBox = $('#uploadNotic .alert');
			alertBox.removeClass("alert-info");
			alertBox.addClass("alert-warning");
			$('#uploadNotic .alert span').removeClass("glyphicon-info-sign");
			$('#uploadNotic .alert span').addClass("glyphicon-warning-sign");
			window.location.href="./userCenter/login.html";
			return true;
		}else if(dat["status"]==-403){
			//无权限
			if(confirm("当前账号权限不足,是否更换账号?")){
				window.location.href="./userCenter/login.html";
			}
			return true;
		}
		alert("服务器出错:"+dat["errMsg"]+" (错误码:"+dat["status"]+")");
		return true;
	}
	return false;
}
function dblog(text){
	// return;
	var date=new Date();
	var timeStr=date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
	console.log("["+timeStr+"] "+text);
	// $('#debugLog').html($('#debugLog').html()+"\n["+timeStr+"] "+text);
	// $("#debugLog").scrollTop($("#debugLog")[0].scrollHeight);
}
function setCookie(c_name,value,expiredays)  
{
	var exdate=new Date();
	exdate.setDate(exdate.getDate()+expiredays);
	document.cookie=c_name+ "=" +escape(value)+((expiredays==null) ? "" : ";expires="+exdate.toGMTString());
}

function getCookie(c_name)
{
	if (document.cookie.length>0)
		{
		c_start=document.cookie.indexOf(c_name + "=");
		if (c_start!=-1)
			{ 
			c_start=c_start + c_name.length+1 ;
			c_end=document.cookie.indexOf(";",c_start);
			if (c_end==-1) c_end=document.cookie.length
				return unescape(document.cookie.substring(c_start,c_end));
			} 
		}
	return ""
}
function clearCookie(name) {  
	setCookie(name,"", -1);  
}  


/**
 * 用于将jsonObject数组中数据显示 注意一定要每个成员的key一致
 * @param  {[type]} inj  要显示的jsonobject数组
 * @param  {[type]} tbid 表格id
 * @param {[type]} hasnullCallback 返回错误列数组
 * @return {[type]}      [description]
 */
function displayJsonInArray2Table(inj,tbid,hasnullCallback,nothasnullcallback){
	//用于获取数组中的json, tbid 为表id
	var keys = [];
	for (var key in inj[0]) {
		keys.push(key);
	}
	// console.log("json所有键");
	// console.log(keys);

	var data=[];
	for (var i = 0; i < inj.length; i++) {
		var tmp = [];
		for (var j = 0; j < keys.length; j++) {
			tmp.push(inj[i][keys[j]]);
		}
		data.push(tmp);
	}

	displayForm2Table(tbid,keys,data,hasnullCallback,nothasnullcallback);



}

/**
 * 将json数组显示到表格
 *
 * 注意数据数组[["12","234","456"],["123","67","234"]]使用此格式
 * 
 * @param  {[type]} tbid   表格id
 * @param  {[type]} header 表头数组
 * @param  {[type]} data   数据数组
 * @param {[type]} hasnullCallback 返回错误列数组
 */
function displayForm2Table(tbid,header,data,hasnullCallback,nothasnullcallback){
	var LINE_THEAD = "<thead>#d#</thead>";
	var LINE_TBODY = "<tbody>#d#</tbody>";
	var LINE_HEADER = "<th>#d#</th>";
	var LINE_TR="<tr>#d#</tr>";
	var LINE_TR_ERR="<tr class='nullRows'>#d#</tr>";
	var LINE_CELL="<td>#d#</td>";

	var headerC = header.length;
	var headerDat_tmp = "";

	var hasNull = false;
	var nullRows = [];
	$('#'+tbid).html("");
	for (var i = 0; i < headerC; i++) {
		headerDat_tmp = headerDat_tmp.concat(LINE_HEADER.replace("#d#",header[i]));
	}
	$('#'+tbid).append(LINE_THEAD.replace("#d#",LINE_TR.replace("#d#",headerDat_tmp)));

	var dataC = data.length;
	var tmp_tbody = "";
	for (var i = 0; i < dataC; i++) {
		var rowDat_tmp = "";
		var rowHasNull =false;
		for (var j = 0; j < headerC; j++) {
			if(data[i][j]=="null" || data[i][j]==null){
				rowDat_tmp = rowDat_tmp.concat(LINE_CELL.replace("#d#","<span class='errorCell'>"+data[i][j])+"</span>");
				if(!rowHasNull){
					nullRows.push(data[i][0]);
					hasNull=true;
					rowHasNull=true;
				}
				
			}else{
				rowDat_tmp = rowDat_tmp.concat(LINE_CELL.replace("#d#",data[i][j]));
			}
			

		}
		if(rowHasNull){
			tmp_tbody = tmp_tbody + LINE_TR_ERR.replace("#d#",rowDat_tmp);
		}else{
			tmp_tbody = tmp_tbody + LINE_TR.replace("#d#",rowDat_tmp);
		}
		
	}

	$('#'+tbid).append(LINE_TBODY.replace("#d#",tmp_tbody));

	if(hasNull && hasnullCallback!=null){
		hasnullCallback(nullRows);
	}else{
		nothasnullcallback();
	}

	winResize(0);
}	


//删除收尾空格
function trim(ins) {
    return ins.replace(/^\s+|\s+$/g, '');
};