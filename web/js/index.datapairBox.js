// 配对数据框

//现在数据
var now_dfield = [];
var now_fheader = [];

//临时全局变量
var t_dfield = [];

function setDatapairBox(fheader,dfield,successCallback,fullPairCallback){
	var EXCEL_TR = '<tr data-excelid="#id#">#d#</tr>';
	var EXCEL_TD_NORMAL = '<td>#d#</td>';
	var EXCEL_TD_DRAG = '<td draggable="true" data-fieldid="#id#">#d#</td>';
	var EXCEL_TD_EMPTYDRAG = '<td data-fieldid="-1">&#12288;</td>';
	var FIELD_TR = '<tr><td draggable="true" data-fieldid="#id#">#d#</td></tr>';
	
	now_dfield=dfield;
	now_fheader=fheader;
	t_dfield=dfield;

	var ex = $("#table_excel");
	var fd = $("#table_field");

	ex.html("");
	fd.html("");

	//获取字段的默认数据
	var defaultField = "";
	for (var i = 0; i < dfield.length; i++){
		var tmpj = dfield[i];
		// console.log(tmpj);
		//判断是否有默认数据
		if(tmpj.hasOwnProperty("d")){
			//有默认值
			defaultField=defaultField+"列["+tmpj['n']+"]值["+tmpj["d"]+"]　　";
			// console.log("这个有默认数据, 字段名:"+tmpj['n']+" 默认值:"+tmpj["d"]);
			// console.log(tmpj);
		}
	}
	if(defaultField!=""){
		$("#allowDefaultOutBox").attr("data-content",defaultField);
		$("#allowDefaultOutBox").removeClass("disabled");
	}else{
		$("#allowDefaultOutBox").attr("data-content","该数据库的没有默认数据");
		$("#allowDefaultOutBox").removeClass("active");
		$("#allowDefaultOutBox").addClass("disabled");
	}


	for (var i = 0; i < fheader.length; i++) {
		var tmpj = fheader[i];
		var tr = EXCEL_TR.replace("#id#",tmpj['id']);
		var nametd = EXCEL_TD_NORMAL.replace("#d#",tmpj['n']+" ("+tmpj['c']+")");
		var dragtd = "";



		if(tmpj['r']!=-1){
			//有推荐数据
			var dfj = getdfield(tmpj['r']);
			if (dfj==undefined) {
				// console.log("getdfield返回未定义 tmpj:"+JSON.stringify(tmpj)+" t_dfield:"+JSON.stringify(tmpj));
				dragtd = EXCEL_TD_EMPTYDRAG;
			}else{
				dragtd = EXCEL_TD_DRAG.replace("#id#",tmpj['r']);
				dragtd = dragtd.replace("#d#",dfj['n']+" ("+dfj['c']+")");
			}
			
		}else{
			//没有推荐数据
			dragtd = EXCEL_TD_EMPTYDRAG;
		}

		tr=tr.replace("#d#",nametd+dragtd);

		ex.append(tr);

	}



	if(t_dfield.length!=0){
		for (var i = 0; i < t_dfield.length; i++) {
			var dfj = t_dfield[i];
			var tr = FIELD_TR.replace("#id#",dfj['id']);
			tr = tr.replace("#d#",dfj['n']+" ("+dfj['c']+")");

			r=tr.replace("#d#",nametd+dragtd);

			fd.append(tr);
		}
	}else{
		fd.append("<tr>"+EXCEL_TD_EMPTYDRAG+"</tr>");
		if(fullPairCallback!=undefined)
			fullPairCallback();
		return;
	}
	if(successCallback!=undefined)
		successCallback();

}

function getdfield(id){
	var newdfield = [];
	var retj = null;
	for (var i = 0; i < t_dfield.length; i++) {
		if(t_dfield[i]['id']==id){
			retj=t_dfield[i];
		}else{
			newdfield.push(t_dfield[i]);
		}
	}
	t_dfield=newdfield;
	return retj;
}

/**
 * 获取用户datapair的选择
 * @return {[type]} [description]
 */
function getDatapair(){
	var retj = [];
	var ex = document.getElementById("table_excel").getElementsByTagName("tr");
	// var field = document.getElementById("table_field").getElementsByTagName("tr");
	
	for (var i = 0; i < ex.length; i++) {
		var tmpj = {};
		tmpj['sid']=ex[i].getAttribute("data-excelid");
		tmpj['tid']=ex[i].getElementsByTagName("td")[1].getAttribute("data-fieldid");
		var exObj=getfheaderObjById(tmpj['sid']);
		if(exObj==null){
			alert("页面内部错误 '在表格中找不到指定的fheader',请重新上传文件.若再次遇到此错误,请联系管理员.");
			return null;
		}

		tmpj['n'] = exObj['n'];
		// console.log("行:"+i+" json:"+JSON.stringify(tmpj));		
		retj.push(tmpj);
	}
	return retj;

}

function getfheaderObjById(id){
	var retj = null;

	for (var i = 0; i < now_fheader.length; i++) {
		if(now_fheader[i]['id'] == id){
			retj=now_fheader[i];
			break;
		}
		
	}

	return retj;
}
























//以下为页面程序
//
function resetDatapairBox(){
	setDatapairBox(now_fheader,now_dfield,undefined,undefined);
}


function moveField(fieldid,frombox,tobox,targetExcelid){
	// console.log("moveField id:"+fieldid+" from:"+frombox+" tobox:"+tobox)
	if((frombox == tobox) && (frombox == "table_field")){
		// console.log("field 同框拖动忽略");
		return;
	}
	var excel_array = document.getElementById("table_excel").getElementsByTagName("tr");
	var field_array = document.getElementById("table_field").getElementsByTagName("tr");

	var from_table = document.getElementById(frombox).getElementsByTagName("tr");

	var tmp_td = gettdFromTable(fieldid,from_table,(frombox=="table_excel"),true);


	if(tmp_td==undefined || tmp_td==null){
		alert("页面错误 找不到对应的组件");
		return;
	}

	// tmp_td=tmp_td.outerHTML;

	// console.log("获取到的td:"+tmp_td);

	settdToTable(tobox,tmp_td,targetExcelid);
	
	winResize(0);

}

function gettdFromTable(fieldid,table,isexcel,del){
	var fieldtdNum = isexcel?1:0;//field框框的位置
	for (var i = 0; i < table.length; i++) {
		//当前格
		var nowtd = table[i].getElementsByTagName("td")[fieldtdNum];
		if(nowtd==undefined){
			$(nowtd).remove();
			continue;
		}
		var nowfieldid = nowtd.getAttribute("data-fieldid");
		
		// console.log("get"+nowfieldid);
		if(fieldid==nowfieldid){
			//匹配到
			var tmp_td = nowtd.outerHTML;

			if(isexcel){
				$(nowtd).remove();
				$(table[i]).append("<td data-fieldid='-1'>&#12288;</td>");
				// return nowtd;
			}else{
				// console.log("彻底删除");
				$(nowtd.parentNode).remove();
				if (table.length<1) {
					//列表没项目了,补一个
					document.getElementById("table_field").innerHTML="<tr><td data-fieldid='-1'>&#12288;<td></tr>"
				}
			}
			return tmp_td;
			
		}
	}
}

/**
 * 设置td
 * @param  {[type]} totableid   [description]
 * @param  {[type]} fieldtdData td的html对象
 * @param  {[type]} excelid     [description]
 * @return {[type]}             [description]
 */
function settdToTable(totableid,fieldtdData,excelid){
	var isexcel = (totableid=="table_excel");
	if(isexcel){
		//excel表
		var to_array = document.getElementById(totableid).getElementsByTagName("tr");
		for (var i = 0; i < to_array.length; i++) {
			//当前格
			var nowtr = to_array[i];

			var nowexcelid = nowtr.getAttribute("data-excelid");
			// console.log("set"+nowexcelid);
			if(excelid==nowexcelid){
				//匹配到
				
				var fieldtd = nowtr.getElementsByTagName("td")[1];
				// console.log(fieldtd);
				if(fieldtd.innerHTML!=""){
					// console.log(fieldtd.getAttribute("data-fieldid"));
					if(fieldtd.getAttribute("data-fieldid")=="-1"){
						$(fieldtd).remove();
					}else{
						settdToTable("table_field",fieldtd.outerHTML);
						$(fieldtd).remove();
					}
					
				}
				$(nowtr).append(fieldtdData);
				break;
			}
		}
	}else{
		//field表
		var to_array = document.getElementById(totableid).getElementsByTagName("tr");
		if(to_array.length==1 && to_array[0].getElementsByTagName("td")[0].getAttribute("data-fieldid")==-1){
			$(to_array[0]).remove();
		}
		
		$('#table_field').append("<tr>"+fieldtdData+"</tr>");
		

	}



	
	
	var fieldtdNum = isexcel?1:0;//field框框的位置
	
}








// 表格动态拖动程序

/*
field拖动开始
 */
function table_field_dragstart(ev){
	// console.log(ev);
	ev.dataTransfer.setData("fieldid",ev.target.getAttribute("data-fieldid"));
	ev.dataTransfer.setData("from","table_field");
	$('#table_excel').addClass("dropTarget");
}

/*
field拖动结束
 */
function table_field_dragend(ev){
	// console.log("拖动结束 fieldBox   data-fieldid:"+ev.target.getAttribute("data-fieldid"));
	$('#table_excel').removeClass("dropTarget");
}

/*
excel拖动开始
 */
function table_excel_dragstart(ev){
	// console.log("拖动开始 "+ev.target.id+"   data-fieldid:"+ev.target.getAttribute("data-fieldid"));
	ev.dataTransfer.setData("fieldid",ev.target.getAttribute("data-fieldid"));
	ev.dataTransfer.setData("from","table_excel");
	$('#table_excel').addClass("dropTarget");
	$('#table_field').addClass("dropTarget");
}

/*
excel拖动结束
 */
function table_excel_dragend(ev){
	// console.log("拖动结束 excelBox   data-fieldid:"+ev.target.getAttribute("data-fieldid"));
	$('#table_excel').removeClass("dropTarget");
	$('#table_field').removeClass("dropTarget");
}

/*
目标对象允许拖动放置
 */
function table_allowDrop(ev){
	// var nowBox = ev.toElement.parentNode.parentNode.id;
	// var frombox = ev.srcElement.parentNode.parentNode.id;

	// console.log(ev);

	// console.log("允许拖放检查 来自:"+frombox+" 当前目标:"+nowBox);
    
 //    if(frombox=="table_field" && nowBox=="table_field")
 //    	return;
	ev.preventDefault();
	
}

/*
拖动 放开 excel
 */
var recObj ,event1;
function table_excel_drop(ev){
    ev.preventDefault();
    var data_fieldid=ev.dataTransfer.getData("fieldid");
    var data_from=ev.dataTransfer.getData("from");
    var targetrow = ev.target.parentNode;

    // console.log("接收到拖动对象  excelBox 对应ExcelID:"+targetrow.getAttribute("data-excelid")+"  fieldid:"+data_fieldid+"  from:"+data_from);
    targetrow.classList.remove("dropTargetActive");
    $('#table_excel').removeClass("dropTarget");
	$('#table_field').removeClass("dropTarget");

    moveField(data_fieldid,data_from,"table_excel",targetrow.getAttribute("data-excelid"));

    // ev.target.appendChild(document.getElementById(data));
}

/*
拖动 放开 field
 */
var recObj ,event1;
function table_field_drop(ev){
    var data_fieldid=ev.dataTransfer.getData("fieldid");
    var data_from=ev.dataTransfer.getData("from");
    var targetrow = ev.target.parentNode;

    // console.log("接收到拖动对象  fieldBox   fieldid:"+data_fieldid+"  from:"+data_from);
    targetrow.classList.remove("dropTargetActive");
    $('#table_excel').removeClass("dropTarget");
	$('#table_field').removeClass("dropTarget");


    moveField(data_fieldid,data_from,"table_field");

}

/*
拖动进入到该元素  视觉效果
 */
function table_drag_enter(ev){

	// var targetBoxID = ev.target.parentNode.parentNode.id;
	// if(targetBoxID=="table_field"){
	// 	//拖进字段表
	// 	var targetrow = ev.target.parentNode.parentNode;
	// }else{
	// 	//拖进excel表
	var targetrow = ev.target.parentNode;
	// }
	// console.log("拖进来了 "+targetrow.getAttribute("data-excelid"));
	// setTimeout(function(){
	
	targetrow.classList.add("dropTargetActive");
	// },20);
	
}

/*
拖动移出到该元素  视觉效果
 */
function table_drag_leave(ev){

	// var targetBoxID = ev.target.parentNode.parentNode.id;
	// if(targetBoxID=="table_field"){
	// 	//拖进字段表
	// 	var targetrow = ev.target.parentNode.parentNode;
	// }else{
		//拖进excel表
	var targetrow = ev.target.parentNode;
	// }
	// console.log("拖出去了 "+targetrow.getAttribute("data-excelid"));
	targetrow.classList.remove("dropTargetActive");
}