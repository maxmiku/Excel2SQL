//数据导入框

function initImportingBox(){
	$('#importing_fileid').html(fileid);
	$('#importing_rows').html(excelRows);

	$('#importing_status').html("正在获取进度");
	$('#importing_progress').html("-");
	$('#importing_time').html("-");

	$('#importProgressBar').css("width","0%");
	
	$('#importing_errorCount').html("-");

}