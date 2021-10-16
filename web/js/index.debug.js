


$(function(){
	// console.log(sessionStorage.getItem('eruda'));
	if (/eruda=true/.test(window.location) || sessionStorage.getItem('eruda') == 'true'){
		console.log("调试模式 启动");
		var script = document.createElement('script');
		//创建js组件
		document.body.appendChild(script);
		script.type = 'text/javascript';//设置
		script.src='./js/eruda.js';
		script.onload = script.onreadystatechange = function(){
			if(  ! this.readyState     //这是FF的判断语句，因为ff下没有readyState这人值，IE的readyState肯定有值
		          || this.readyState=='loaded' || this.readyState=='complete'   // 这是IE的判断语句
		    ){
				eruda.init();
			}
		}
	   
	}
   

});

var erduaCount = 0;

function showEruda () { 
	erduaCount++;
    var erdua = sessionStorage.getItem('eruda');
    if (erdua != "true"){
    	if(erduaCount>2){
    		erduaCount=-1;
    		sessionStorage.setItem('eruda', 'true');
    		location.reload();
    	}
    } else {
        sessionStorage.setItem('eruda', 'false')
    }
    
}
