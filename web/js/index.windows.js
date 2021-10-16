var nowWindwos = 0;//当前激活的窗口
var focusOn = true;
var isiOS=false;
var fadeinCount = 0;

//动态调节组件位置
function winResize(resize){
	// if(!focusOn || document.hidden){
	// 	console.log("不聚焦在网页上,自动忽略调整");
	// 	return;
	// }

	//获取页面的大小
	var pageH=document.documentElement.clientHeight;
	var pageW=document.documentElement.clientWidth;

	// console.log("pageH"+pageH+" pageW"+pageW);


	var bigH,smallH,bigW,smallW,div;

	
	//判断左右框高低
	var leftH , leftW , rightH, rightW;
	div=document.getElementById("leftLimitBox");
	leftH=div.offsetHeight;
	leftW=div.offsetWidth;
	// div=document.getElementById("rightLimitBox");
	div=document.getElementsByClassName("contentOutBoxActive")[0];
	rightH=div.offsetHeight;
	rightW=div.offsetWidth;

	div=document.getElementById("centerInBox");
	smallH=div.offsetHeight;
	smallW=div.offsetWidth;
	
	// console.log("LH:"+leftH+" RH:"+rightH);

	// if(leftH>rightH){
	// 	//左边比较高
	// 	// $('#centerBox').css("height",leftH+"px");
	// 	$('#leftBox').css("height",leftH+"px");
	// 	$('#rightBox').css("height",leftH+"px");
	// 	//藏在底部的盒子位置
	// 	$('.contentOutBoxBottom').css("transform","translateY("+leftH+"px)");
	// }else{
	// 	//右边比左边高
	// 	// $('#centerBox').css("height",rightH+"px");
	// 	$('#leftBox').css("height",rightH+"px");
	// 	$('#rightBox').css("height",rightH+"px");
	// 	//藏在底部的盒子位置
	// 	$('.contentOutBoxBottom').css("transform","translateY("+rightH+"px)");
	// }
	
	//藏在底部的盒子位置
	$('.contentOutBoxBottom').css("transform","translateY("+smallH+"px)");

	$('.contentOutBoxActive').css("transform","translateY(0px)");
	$('.contentOutBoxTop').css("transform","translateY(-100%)");
	$('#rightInBox').css("width",rightW+"px");
	$('#rightInBox').css("height",rightH+"px");


	




	//旧版js控制窗口居中
	// if(resize==1){
	// 	setTimeout(function(){
	// 		winResize(0);
	// 	},400);
	// 	return;
	// }
	// setTimeout(function(){
	// 	//center框居中
		
	// 	// if(resize==0){
	// 	// 	$("#centerBox").css("transition","");
	// 	// }else{
	// 	// 	$("#centerBox").css("transition","all 0.2s ease");
	// 	// }

	// 	// div=document.getElementById("centerBox");
	// 	// smallH=div.offsetHeight;
	// 	// smallW=div.offsetWidth;


	// 	smallH=leftH>rightH?leftH:rightH;
	// 	smallW=rightW+leftW;


	// 	div=document.getElementById("mainBackground");
	// 	bigH=div.offsetHeight;
	// 	bigW=div.offsetWidth;

	// 	var topt =(bigH*0.5-smallH*0.5);
	// 	var leftt = (bigW*0.5-smallW*0.5);
	// 	if(topt<1){
	// 		topt=0;
	// 	}
	// 	if (leftt<1) {
	// 		leftt=0;
	// 	}

	// 	// if()

	// 	$('#centerBox').css("top",topt+"px");
	// 	$('#centerBox').css("left",leftt+"px");
	// 	// console.log("resize:"+resize);
	// 	if(resize==0){
	// 		setTimeout(function(){
	// 			winResize(-1);
	// 		},100);
	// 	}else{
	// 		if(fadeinCount<25){
	// 			$('#centerBox').css("transform","translateX(0)");
				
	// 			function fadeinLoop(){
	// 				fadeinCount++;
	// 				$('#centerBox').css("filter","alpha（Opacity = "+fadeinCount*4+")");
	// 				$('#centerBox').css("-moz-opacity",fadeinCount*0.04);
	// 				$('#centerBox').css("opacity",fadeinCount*0.04);
	// 				if(fadeinCount<25){
	// 					setTimeout(function(){
	// 						fadeinLoop();
	// 					},10);
	// 				}else{
	// 					$('#loadingBox').hide();
	// 				}
					
	// 			}

	// 			fadeinLoop();
					
	// 		}
			

	// 	}

		
			
	// },50);

}

$(function(){
	var u = navigator.userAgent;
	isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端

	$('#user_logout').click(function(){
		$.ajax({
			url:"./userCenter/user/userLogout",
			type:"POST",
			dataType:"json",        
			success:function(r){
				if(dealRetErr(r))return;
				$('#user_logout').html("登入");
				r['status']=-401;
				dealRetErr(r);
			},
			error:function(){
				alert("登出失败, 请重试\nstatus:"+e.status+"\nstatusText:"+e.statusText);
				console.log(e);
			}
		});
	}); 


	$("#processlist>ul>li").click(function(){
		// console.log("表项被点击 位置:"+this.value);
		if(!$(this).hasClass("before")){
			console.log("无法向前跳,仅允许向后跳");
			return;
		}
		if(nowWindwos==4){
			if(!confirm("切换窗口不会导致导入中止,但会导致无法查看导入是否成功\n是否继续切换窗口?")){
				return;
			}
		}
		setNowWindow(this.value);
	});

	autoSetFontSize();
	setTimeout(function () {
		winResize();
	},300);
	
	setTimeout(function () {
		winResize();
	},50);
	// window.onresize();

})
window.onresize = function(){
	
	// console.log("窗口大小改变触发");
	if(!focusOn || document.hidden){
		console.log("不聚焦在网页上,自动忽略调整");
		return;
	}
	//动态设置根字体大小
	// document.documentElement.style.fontSize = document.documentElement.clientWidth/55 + "px";
	// $('#centerBox').css("height","");
	

	// $('#leftBox').css("height","");
	// $('#rightBox').css("height","");
	


	// $("#centerBox").css("transition","");
	// if(!isiOS){
	// 	autoSetFontSize();
	// }
	autoSetFontSize();
	
	setTimeout(function () {
		winResize();
	},50);
}




/**
 * 设置当前在前台的窗格
 * @param {int} now 当前要显示的窗格的对应的位置
 */
function setNowWindow(now){
	nowWindwos = now;
	var divs = document.getElementsByClassName("contentOutBox");
	if(divs.length<=now){
		alert("页面错误,找不到指定的窗格"+now);
	}

	//前面的
	for (var i = 0; i < now; i++) {
		divs[i].classList.remove("contentOutBoxBottom");
		divs[i].classList.remove("contentOutBoxActive");
		divs[i].classList.add("contentOutBoxTop");
	}

	//当前
	divs[now].classList.remove("contentOutBoxBottom");
	divs[now].classList.remove("contentOutBoxTop");
	divs[now].classList.add("contentOutBoxActive");
	
	//后面的
	for (var i = now+1; i < divs.length; i++) {
		divs[i].classList.remove("contentOutBoxTop");
		divs[i].classList.remove("contentOutBoxActive");
		divs[i].classList.add("contentOutBoxBottom");
	}


	//导航栏
	var navis = document.getElementById("processlist").getElementsByTagName("li");
	var found=false;
	for (var i = 0; i < navis.length; i++) {
		$(navis[i]).removeClass("before");
		$(navis[i]).removeClass("after");
		if(i==now){
			$(navis[i]).addClass("active");
			found=true;
		}else{
			$(navis[i]).removeClass("active");
			if(found){
				$(navis[i]).addClass("after");
			}else{
				$(navis[i]).addClass("before");
			}
		}
	}



	//按钮栏 和 初始化
	switch(now){
		case 0:
			setDisplayButton(0,0,1);
			initUploadBox();
			break;
		case 1:
			setDisplayButton(1,"重置","确定");

			break;
		case 2:
			setDisplayButton("修改配对数据",0,1);
			initpreimportBox();
			getExampleData();
			break;
		case 3:
			setDisplayButton(1,"取消导入","确认导入");

			break;
		case 4:
			setDisplayButton(0,0,0);
			initImportingBox();
			getProcess(true,function(data){setNowWindow(5); dealImportReport(data);},function(){setNowWindow(3)});
			break;
		case 5:
			setDisplayButton("首页",0,0);

			break;
	}



	setTimeout(function () {
		winResize();
	},50);
	
}

/**
 * 设置显示哪个按钮 0为不显示 1显示默认文本 其他为设置按钮文本
 * @return {[type]} [description]
 */
function setDisplayButton(but1,but2,but3){
	if(but1!=0){
		if(but1!="" && but1!=1)
			$('#previousBut font').html(but1);
		else
			$('#previousBut font').html("上一步");
		$('#previousBut').show();
	}else
		$('#previousBut').hide();

	if(but2!=0){
		if(but2!="" && but2!=1)
			$('#resetBut font').html(but2);
		else
			$('#resetBut font').html("重置");
		$('#resetBut').show();
	}else
		$('#resetBut').hide();

	if(but3!=0){
		if(but3!="" && but3!=1)
			$('#nextBut font').html(but3);
		else
			$('#nextBut font').html("下一步");
		$('#nextBut').show();
	}else
		$('#nextBut').hide();
}

/**
 * 自动设置字体大小
 */
function autoSetFontSize(){
	// $('#mainBackground').css("font-size",document.documentElement.clientWidth/57 + "px");
	
	var p = 0.159741806;
	var ftsize = 0;
	var pageWidth = document.documentElement.clientWidth;
	var pageHeight = document.documentElement.clientHeight;
	var scale = pageWidth/pageHeight;

	if((pageHeight*1.42) > pageWidth){
		//窄屏幕
		console.log("窄屏幕");
		ftsize = pageWidth/55;
	}else{
		//宽屏幕
		console.log("宽屏幕");
		ftsize = pageHeight/40;
	}

	// ftsize = 9;

	// ftsize = Math.sqrt(2*p*(pageWidth+pageHeight)/2);
	// console.log("w:"+pageWidth+" h:"+pageHeight+" 宽高比:"+scale+" 字体大小:"+ftsize+"px");
	$('#mainBackground').css("font-size",ftsize + "px");
}


// 监听是否在当前页，并置为已读
// document.addEventListener("visibilitychange", function () {
//   if (!document.hidden) {   //处于当前页面
//     console.log("你回来了");
// 	focusOn=true;
    
// 	winResize();
//   }else{
//  	console.log("你离开了");
//  	focusOn=false;
//   }
// });

/**
 * 上一步按钮被点击
 * @return {[type]} [description]
 */
function previousPress(){
	switch (nowWindwos){
		case 1:
			setNowWindow(0);
			break;
		case 2:
			setNowWindow(1);
			break;
		case 3:
			setNowWindow(2);
			break;
		case 5:
			setNowWindow(0);
			break;
	}
}

/**
 * 下一个按钮点击
 * @return {[type]} [description]
 */
function nextPress(){
	
	
	switch (nowWindwos){
		case 0:
			$("#nextBut font").html("Loading...");
			uploadFile(function(){$("#nextBut font").html("下一步");});
			break;
		case 1:
			
			// var tmp = $("#nextBut font").html();
			$("#nextBut font").html("Loading...");
			getExampleData(function(){setNowWindow(2);},function(){$("#nextBut font").html("确定");});

			// setTimeout(function() {
			// 	$("#nextBut font").html(tmp);
			// },1000);
			
			break;
		case 2:
			setNowWindow(3);
			break;
		case 3:
			$("#nextBut font").html("Loading...");

			confirmField(true,function(){setNowWindow(4);},function(){$("#nextBut font").html("确认导入");});

			break;
	}
}


/**
 * 重置按钮点击
 * @return {[type]} [description]
 */
function resetPress(){
	switch (nowWindwos){
		case 1:
			resetDatapairBox();
			break;
		case 3:
			confirmField(false,function(){setNowWindow(0)},function(){setNowWindow(0)});
			break;
	}
}



// var baseHeight=0;//基准高度
// function winResize(){
//	console.log("宽度："+document.documentElement.clientWidth+"，高度："+document.documentElement.clientHeight);
//	var pageH=document.documentElement.clientHeight;
//	var pageW=document.documentElement.clientWidth;

//	var bigH,smallH,div;

	

//	//================================行高标题高动态
//	//最大可用宽高比=0.53994
//	//最小=0.742424
//	//30分钟计算成果（x：高度  y：1-宽高比）x^2=100745*y
//	// console.log("比例："+(1-(pageW/pageH)));
//	if((pageW/pageH)>0.742424){ //过窄
//		baseHeight=161;
//		 $(".lineBox").css("height","161px");
//		 $(".Title").css("height","370.5px");
//	}else if((pageW/pageH)<0.53994){ //过宽
//		baseHeight=215.2;
//		$(".lineBox").css("height","215.2px");
//		$(".Title").css("height","495.1px");
//	}else{ //可自动调节范围
//		baseHeight=Math.sqrt(100745*(1-(pageW/pageH)));
//		$(".lineBox").css("height",baseHeight);
//		$(".Title").css("height",baseHeight*2.3);
//	}

	

//	//============================================标题动态居中
//	$('#titInBox').css("left",(document.documentElement.clientWidth*0.12)+"px");
//	div=document.getElementsByClassName("Title")[0];
//	bigH=div.offsetHeight;//标题外背景高度
//	div=document.getElementById("titInBox");
//	smallH=div.offsetHeight;//标题内框高度
//	// console.log(bigH*0.5-smallH*0.5);
//	$('#titInBox').css("top",(bigH*0.5-smallH*0.5)+"px");

// }