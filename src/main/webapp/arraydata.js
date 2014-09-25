/*
 * Copyright (C) 2014 Integration Architects
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
