var requireJs=[""];//需求加载的js列表

var frontJs=["./js/jquery-2.1.1.min.js","./js/index.js","./js/index.window.js","./js/index.uploadBox.js"];

var asJsStatus=-1;//异步加载js进度 -1未开始 -2已完成 

var asJsLoadProcess = [false];

/**
 * 加载完成时会执行该指令
 * @return {[type]} [description]
 */
function asJsLoadComp(){

}





function asJsLoadRefreash(callback) {
	//异步加载js的进度处理函数
	var notLoadComp=0;
	
	for (var i = 0; i < asJsLoadProcess.length; i++) {
		if(asJsLoadProcess[i]){
			
		}else{
			
			notLoadComp++;
		}
	}
	console.log("当前未加载完的项目:"+notLoadComp);
	if(notLoadComp==0){
		//js加载完成
		
		asJsStatus=-2;

		asJsLoadComp();//调用完成加载函数
		
	}else{
		asJsStatus=notLoadComp;
	}
	
}

document.onreadystatechange = function () {//即在加载的过程中执行下面的代码
	console.log("页面加载中:"+document.readyState);
	if(document.readyState=="complete"){//complete加载完成
		//页面加载完成
		// $(".loading").fadeOut();

		//开始加载其它加密的js
		
		function asLoadJs(num,callback){
			setTimeout(() => {
				
		        if(num<requireJs.length-1)
		        	callback(++num,callback);
		        else{
		        	asJsLoadProcess[0]=true;
		        	asJsLoadRefreash();
		        }
		    
			}, 10);
			var nowNum = asJsLoadProcess.push(false)-1;
			console.log("获取js列表中的js[%d] %s",num,requireJs[num]);
			var script = document.createElement('script');

			//创建js组件
			document.body.appendChild(script);
			script.type = 'text/javascript';//设置

			script.src=requireJs[num];
			
			
			script.onload = script.onreadystatechange = function(){
				asJsLoadProcess[nowNum]=true;
				asJsLoadRefreash();
				//dblog("[init] 异步JS-"+requireJs[num]+" [OK]");
			}
			
		}
		console.log("开始加载其它加密的js");
		asLoadJs(0,function (n,a){asLoadJs(n,a)});
		
	}else if(document.readyState=="interactive"){
		//异步加载js
		console.log("开始异步加载jq");
		var script = document.createElement('script');//创建js组件
		document.body.appendChild(script);
		script.type = 'text/javascript';//设置
		// script.src="https://cdnjs.gtimg.com/cdnjs/libs/jquery/2.1.1/jquery.min.js";
		script.src="./js/jquery-2.1.1.min.js";
		script.onload = script.onreadystatechange = function(){
			//判断js是否加载完成
			
		    if(  ! this.readyState     //这是FF的判断语句，因为ff下没有readyState这人值，IE的readyState肯定有值
		          || this.readyState=='loaded' || this.readyState=='complete'   // 这是IE的判断语句
		    ){
		        //jq加载完成,开始加载自己的js
		        console.log("jq加载完成,开始加载页面js");

		    }
		};
	}
}