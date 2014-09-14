/**
(c) Integration Architects - 2014
**/


function getFileExtensions(){
	var dt = ["pdf","docx","doc","rtf","txt", "xls","xlsx","eml","msg","zip","jpeg","png","ppt","pptx"];
	return dt;
}


//faster, and static in any case
function getDocTypes(){
	var dt = new Array();
	dt.push({data:"Boxi", value:"Boxi"});
	dt.push({data:"CommitteePresentation", value:"CommitteePresentation"});
	dt.push({data:"Contract", value:"Contract"});
	dt.push({data:"ContractSigned", value:"ContractSigned"});
	dt.push({data:"Divers", value:"Divers"});
	dt.push({data:"Quote", value:"Quote"});
	dt.push({data:"Supplier", value:"Supplier"});
	return dt;
}

//using ajax
function getDocTypesFromServer(){
	 var uploadURL ="/dragdrop/upload/doctypes"; 
    var extraData ={}; //Extra Data.
    var jqXHR=$.ajax({
            xhr: function() {
            var xhrobj = $.ajaxSettings.xhr();
            return xhrobj;
        },
    url: uploadURL,
    type: "POST",
    contentType:false,
    processData: false,
        cache: false,
        data: 'doctypes',
        success: function(data){
			console.log('doctps:'+data);


			var doctypes2=data.split(',');   
			for(var index = 0; index < doctypes2.length; index++){
				//console.log('XX '+doctypes2[index]);
 				doctypes.push({data:doctypes2[index],value:doctypes2[index]});
			}         
            //$("#doctypes").empty();         
            //$("#doctypes").append("doctypes ok "+data);         
            //console.log("doctypes response:"+data);
        },
         error: function(jqXHR, textStatus, errorThrown){
			console.log('error:'+textStatus+':'+jqXHR.responseText,errorThrown);
			setError(jqXHR.responseText);
        }
        
    }); 
}
