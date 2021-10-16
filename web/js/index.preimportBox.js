/* 预导入窗格js */
function initpreimportBox(){
	var alert = $('#preimportBox .alert');
	alert.removeClass("alert-danger");
	alert.addClass("alert-info");
	$('#preimportBox .alert span').removeClass("glyphicon-warning-sign");
	$('#preimportBox .alert span').addClass("glyphicon-info-sign");
	$('#preimportBox .alert font').html("将数据模拟导入一次,并返回 <b>示例数据</b> 和 <b>错误数据</b> ,以此来检查列是否匹配.");
	$('#preimportTable').html("");
}

$(function(){
	$('#getAllDataBtn input').click(function(){
		//当前选中状态  注意这是在改变之前执行的 所以要取反
		// var nowToggle = !$('#getAllDataBtn').hasClass("active");
		// console.log("获取全部数据的按钮按下了,选择是:"+nowToggle);
		// console.log("当前选中状态"+);

		getExampleData();



	});

	$("#allowDefault input").click(function () {
		if(!$("#allowDefault").hasClass("disabled")) {
			if ($("#allowDefault input[type='checkbox']").is(':checked')) {

				$("#allowDefaultCirm").addClass("active");

			} else {
				$("#allowDefaultCirm").removeClass("active");
			}
			getExampleData();
		}
	});

	$("#allowDefaultCirm").click(function () {
		//注意取反
		if($("#allowDefaultCirm").hasClass("active")){
			//实际上是取消该按钮
			$("#allowDefault input")[0].checked=false;
		}else{
			//实际上是激活该按钮
			$("#allowDefault input")[0].checked=true;
		}

	})
	// var indata = JSON.parse(testData);
	// displayForm2Table("preimportTable",indata["retData"]["headData"],indata["retData"]["formData"],function(a){tableHasNull(a)});

	//帮助按钮
	// $("#preimportQuestionBtn").popover();
	$("#allowDefaultOutBox").mouseenter(function () {
		$("#allowDefaultOutBox").popover('show');
	})
	$("#allowDefaultOutBox").mouseleave(function () {

		// setTimeout(function () {
			$("#allowDefaultOutBox").popover('hide');
		// },5000);
	});
	// $("#preimportQuestionBtn").bind("click",function () {
	//
	// 	setTimeout(function () {
	// 		$("#preimportQuestionBtn").popover('hide');
	// 	},10000);
	// });
	//提示框隐藏后执行
	// $("#allowDefaultOutBox").on("hidden.bs.popover",function () {
	// 	$(".popover").remove();
	// });

})

function tableHasNull(nullRows) {
	var nullRowsStr = "";
	for(var i=0;i<nullRows.length;i++){
		nullRowsStr=nullRowsStr+"["+nullRows[i]+"] ";
	}
	var errorIcon = ".glyphicon .glyphicon-warning-sign"
	var alert = $('#preimportBox .alert');
	alert.removeClass("alert-info");
	alert.addClass("alert-danger");
	$('#preimportBox .alert span').removeClass("glyphicon-info-sign");
	$('#preimportBox .alert span').addClass("glyphicon-warning-sign");


	
	$('#preimportBox .alert font').html("请检查Excel文件中<b>红色</b>所对应的行号查看错误!<br/>注意: 数据库中<b>除了文本列外类型</b>的列在Excel表中的<b>不能为空</b><br/>错误行号: <b>"+nullRowsStr+"</b> 请处理后重新上传导入");


	// alert("请检查 Excel表中 示例数据红色null所对应的行号 查看数据错误!\n注意: 数据库中除了文本列外 其他类型的列在Excel表中的都不能为空\n行号:"+nullRowsStr+"\n请处理后重新上传导入");
	// console.log("请检查 Excel表中 示例数据红色null所对应的行号 查看数据错误!\n注意: 数据库中除了文本列外 其他类型的列在Excel表中的都不能为空\n行号:"+nullRowsStr+"\n请处理后重新上传导入");
}

function tableNothasNull() {
	var alert = $('#preimportBox .alert');
	alert.removeClass("alert-danger");
	alert.addClass("alert-info");
	$('#preimportBox .alert span').removeClass("glyphicon-warning-sign");
	$('#preimportBox .alert span').addClass("glyphicon-info-sign");
	$('#preimportBox .alert font').html("将数据模拟导入一次,并返回 <b>示例数据</b> 和 <b>错误数据</b> ,以此来检查列是否匹配.");
}











var testData = '{"status":0,"errMsg":"","retData":{"headData":["行号","real_name","student_id","department","post","hour","wage","teacher"],"formData":[["2","***","3201001147","财务处",null,22,292.6,"KKK"],["3","***","3201003621","财务处","会计档案及票据整理岗",23,305.9,"KKK"],["4","***","3100004720","财务处","会计档案及票据整理岗",21,279.3,"KKK"],["5","***","3202003488","财务处","会计档案及票据整理岗",29.5,392.35,"KKK"],["6","***","3201002092","财务处","会计档案及票据整理岗",40,532,"KKK"],["7","***","3102002920","财务处","会计档案及票据整理岗",15,29.5,"KKK"],["8","***","3100000286","财务处","会计档案及票据整理岗",40,532,"KKK"],["9","***","3200003015","财务处","会计档案及票据整理岗",28.5,379.05,"KKK"],["10","***","3201003250","财务处","会计档案及票据整理岗",22.5,299.25,"KKK"],["11","***","3102000783","财务处","会计档案及票据整理岗",7.5,99.75,"KKK"],["12","***","3201004411","财务处","会计档案及票据整理岗",40,532,"KKK"],["9","***","3200003015","财务处","会计档案及票据整理岗",28.5,379.05,"KKK"],["10","***","3201003250","财务处","会计档案及票据整理岗",22.5,299.25,"KKK"],["11","***","3102000783","财务处","会计档案及票据整理岗",7.5,99.75,"KKK"],["12","***","3201004411","财务处","会计档案及票据整理岗",40,532,"KKK"]],"fileid":"bc7c1634-c93f-4bf8-8c6f-a991d8fcfa4d.xlsx","fieldPairid":"jSoCy2Py"}}';