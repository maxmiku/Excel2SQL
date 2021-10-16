// 上传文件框

function initUploadBox(){
    $('#uploadNotic').removeClass("alert-danger");
    $('#uploadNotic').addClass("alert-info");
    $('#uploadNotic span').addClass("glyphicon-info-sign");
    $('#uploadNotic span').removeClass("glyphicon-warning-sign");
    $('#uploadNotic font').html(" 目前仅支持 .xls 与 .xlsx 文件的导入");
}


$(function(){
	$("#uploadButton").click(function(){
    	$("#fileuploadtor").trigger("click");
        $(this).button('loading').delay(1000).queue(function() {
            $(this).button('reset');
            $(this).dequeue(); 
        });
    });

    $("#fileuploadtor").change(function(){
    	if($('#fileuploadtor')[0].files[0]!=undefined){
    		console.log("有文件");
    		// uploadFile();
            nextPress();
    	}
    });

    $('#getExampleExcel').click(function(){
        var $eleForm = $("<form method='get'></form>");

        $eleForm.attr("action","./files/example.xls");

        $(document.body).append($eleForm);

        //提交表单，实现下载
        $eleForm.submit();
       
        
    })
	
});