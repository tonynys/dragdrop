/**
Two-Phase File Upload Html5 Drag & Drop - (c) Integration Architects - 2014a

two-phase upload files:
1. Prepare files using drag & drop. This will upload all files in 1 form/multipart POST request to the server
   File sizes, and allowed extensions are checked on client and server. 
   MD5 hash is calculated per file and sent/verified to/on server.
2. Complete document type for all files and submit to the server. When user clicks the submit button, the files already on the 
   server will be processed.
   
In case of functional errors, the server will return http 501 with an error message
In case of server crash/error 500 will be logged on the server.   

JQuery is used for dom manipulation/query ,javascript Promises, json parsing
Html5 is used for drag/drop
Mordernizr is used for checking browser compatibility

Tested on 
Chrome 36.0.1985.143
Firefox 31.0
Safari 7.0.5
IE 10

var obj = jQuery.parseJSON( '{ "name": "John" }' );
alert( obj.name === "John" );

"employees":[
    {"firstName":"John", "lastName":"Doe"}, 
    {"firstName":"Anna", "lastName":"Smith"}, 
    {"firstName":"Peter", "lastName":"Jones"}
]

**/

var doctypes=[];
var fileextensions=[];
var prepareURL ="/dragdrop/upload/prepare"; //Upload URL for step 1: prepare
var submitURL ='/dragdrop/upload/submit'; //Upload URL for step 2: submit
var htmlCheckOK="<img src='vcheck.png' height='80' width='80'/>";
var htmlRedCross="<img src='redcross.png' height='20' width='20'/>";
var htmlDownload="<img src='downloadicon.jpg'></img>";
//var htmlSpinner="<img src='netvaluator-com.gif'/>";
var htmlSpinner="<img src='ajax-loader.gif'/>";//http://ajaxload.info/  Bert2 8ED551 000000 transparent
var includeNotes=true;
var maxFileSizePerFile=10*1024*1024;
var maxFileSizeTotal=maxFileSizePerFile*5;


//HTML5 Drag and Drop events
$(document).ready(function()
{
var obj = $("#dragandrophandler");
obj.on('dragenter', function (e) 
{
    e.stopPropagation();
    e.preventDefault();
    $(this).css('border', '2px solid #0B85A1');
});
obj.on('dragover', function (e) 
{
     e.stopPropagation();
     e.preventDefault();
});
obj.on('drop', function (e) 
{
 
     $(this).css('border', '2px dotted orange');
     e.preventDefault();
     var files = e.originalEvent.dataTransfer.files;
 
     //We need to send dropped files to Server
     handleFileUpload(files,obj);
});
$(document).on('dragenter', function (e) 
{
    e.stopPropagation();
    e.preventDefault();
});
$(document).on('dragover', function (e) 
{
  e.stopPropagation();
  e.preventDefault();
  obj.css('border', '2px dotted #0B85A1');
});
$(document).on('drop', function (e) 
{
    e.stopPropagation();
    e.preventDefault();
});
 
});

function setError(msg){
	console.log(msg);
	$("#errorpanel").append(htmlRedCross);
	$("#errorpanel").append(msg+'<br/>');
}
function doError(obj,errormsg){
	obj.css('border','3px solid red');
	setError(errormsg);
}

function getUrlParameter(paramName){
	var str_url=''+document.URL;
	var i=str_url.indexOf(paramName+'=');
	if(i>=0){
		var paramVal=str_url.substring(i+paramName.length+1);
		var j=str_url.indexOf('&',i);
		if(j>0){
			paramVal=str_url.substring(i+paramName.length+1,j);
		}
		return paramVal;
	}
	return '';
}
function doOnload(){
//	jQuery.each( jQuery.browser,function(i,val){
//	console.log('Browser'+i+':'+val);
//	});

	if (Modernizr.draganddrop) {
		//$("#errorpanel").append("HTML5 ok");
		console.log("HTML5 dnd ok");
	}else{
		console.log("HTML5 dnd error");
		$("#errorpanel").append("HTML5 Not supported");
		$("#dragandrophandler").hide();
		//TODO: add file upload button 

	}	
	doctypes=getDocTypes();
	//getDocTypesFromServer();//get using ajax
	fileextensions=getFileExtensions();
}

function clearAll(){
	$("#submitform").empty(); //clear previous drop        
	//$("#filelist").empty(); //clear previous drop        
	$("#fileUploadStatus").empty(); //clear previous drop        
	//$("#submitresultheader").empty(); //clear previous drop        
	$("#submitresult").empty(); //clear previous drop        
	$("#errorpanel").empty(); //clear previous drop        
	//$("#errorpanel").hide();

}



function sendFileToServer(formData,status){
    $("#fileUploadStatus").append(htmlSpinner);
    $("#fileUploadStatus").append(" Uploading files...");    
   
    var extraData ={}; //Extra Data.
    var jqXHR=$.ajax({
            xhr: function() {
            var xhrobj = $.ajaxSettings.xhr();
            if (xhrobj.upload) {
                    xhrobj.upload.addEventListener('progress', function(event) {
                    }, false);
                }
            return xhrobj;
        },
    url: prepareURL,
    type: "POST",
    contentType:false,
    processData: false,
        cache: false,
        data: formData,
        success: function(data,  textStatus,  jqXHR){
        console.log('content type:::'+jqXHR.getResponseHeader("Content-Type"));
     		sendFileToServerOK(data,jqXHR.getResponseHeader("Content-Type"));
        },
         error: function(jqXHR, textStatus, errorThrown){
            $("#fileUploadStatus").empty();//remove 'uploading'
			setError('Server Error prepare:'+errorThrown);
        }
        
    }); 
 
    //status.setAbort(jqXHR);
}


/* sendFileToServer() was ok
* response can be html part of table/form or just json array with file items incl. dropId
* in the latter case, we construct all html at client side, so server is pure rest/json
*/
function sendFileToServerOK(data, contenttype){
      // status.setProgress(100);
            $("#fileUploadStatus").empty();  
            //$("#fileUploadStatus").append(htmlDownload);   
            $("#dragandrophandler").empty();
            $("#dragandrophandler").append(htmlCheckOK);
            $("#dragandrophandler").append("<p>Upload done, confirm document types</p>");
      

           // $("#submitformheader").empty();         
           // $("#submitformheader").append("2.Submit: Confirm Files and Document Type<br>");         
            $("#submitformheader").show();         
            $("#submitform").empty();    


            var html="<form name='processFilesForm' method='POST'>";
            html=html+"<table>";
            
            html=html+"<tr>";
            html=html+"<td> </td><td><b>File</b></td><td><b>Document Type</b></td>";
            if(includeNotes){
            	html=html+"<td><b>Note</b></td>";
            }
            html=html+"<td><b>Size</b></td>";
            html=html+"</tr>";

            
            if(contenttype.indexOf('application/json')>=0){
            	//JSON
            	console.log('Json response:');
            	console.log(data);
            	var dropId='';
		        //no need to parse, jquery data object is automatically Array object
				//var files = jQuery.parseJSON(data);
				var files=data;
				for(var index = 0; index < files.length; index++){
					var file=files[index];
					dropId=file.dropId;
					console.log('DropID:'+dropId);
					var i=index+1;
					var id='filecheck_'+i;
					html=html+"<tr>";
	                html=html+"<td> <input type='hidden' name='md5_"+i+"' value='"+file.hash+"'/>";
					html=html+"<div class='roundedOne'>";
	                html=html+"<input type='checkbox'  name='"+id+"' id='"+id+ "' checked />";
	                html=html+"<label for='"+id+"'></label></div>";
	                html=html+"</td>";
	                html=html+"<td> <input type='text' class='biginput' size='60' name='filename_"+i+"' value='"+file.fileName+"'/> </td>";
					html=html+"<td> <input type='text' class='biginput'  name='doctype_"+i+"' value='"+file.documentType+"'/> </td>";
		            if(includeNotes){
	                	html=html+"<td> <input type='text' class='biginput' size='15' name='note_"+i+"' value=''/> </td>";
	                }
	                html=html+"<td>"+file.size+" </td>";
	      
	                html=html+"</tr>";
					
					
				}
		        html=html+"<input type='hidden' name='dropId' value='"+dropId+"'/>";
		
				
            }else{
            	console.log('html response');
	            html=html+data;
           }
            html=html+"<tr><td><div class='roundedOne' ><input type='checkbox' id='waitfor' name='waitfor' checked /><label for='waitFor'/></div></td><td>Wait for Upload</td></tr>";
            html=html+"<tr><td></td><td><a href='#' id='submitbutton' class='myButton' onclick='submitForm();'>Submit</a></td></tr>";
            html=html+"</table>";
            html=html+"</form>";     
                
       
            
            console.log(html);
            $("#submitform").append(html); 

        	//attach autocomplete input handlers for doctype
            attachAutoComplete();
            //intercept form returned in data
            //interceptSubmitForm();
}

function validateForm(){
	var ok=true;
	//console.log('DT2'+doctypes.length);

	$(":input[type=text]").each(function() {
		
		$(this).css('border','1px orange');//reset previous errors to ok
		
		if (this.name.substring(0, 9) == "filename_") {
			if(this.value==""){
				//console.log('Missing filename val'+this.value);
				ok=false;
				doError($(this),'Missing filename value');
			}else{
				if(this.value.length<6){
					ok=false;
					doError($(this),'Invalid filename:'+this.value);
				}
				
				
				var li=this.value.lastIndexOf(".");
				if(li<1){
					ok=false;
					doError($(this),'no filename extension:'+this.value);
				}else{
					var ext=this.value.substring(li+1);
					ext=ext.toLowerCase();
					var found=false;
					for(var index = 0; index < doctypes.length; index++){
						if(fileextensions[index]==ext){
							found=true;
						}
					}
					if(!found){
						ok=false;
						setError($(this),'invalid filename extension:'+this.value);
						
					}
				}
			}
		
		}

		if (this.name.substring(0, 8) == "doctype_") {
			if(this.value==""){
				ok=false;
				doError($(this),'Missing document-type value');

			}else{
				found=false;
				//	console.log('AA'+this.value);
				for(var index = 0; index < doctypes.length; index++){
					//console.log('BB'+index);
					if(doctypes[index].value==this.value){
						found=true;
					}
				}
				if(!found){
					doError($(this),'Invalid document-type value');
					ok=false;
				}
			}
	
		}
    });
    return ok;
}
function submitForm(){
//intercept normal form submit and do it with ajax, read the response and put it in the div

var form=$('form[name="processFilesForm"]');
//var form=document.forms['processFilesForm'];
//console.log('form=');
//console.log(form);

//console.log("AA");
	$("#errorpanel").empty(); //clear previous drop        

//validate form
var ok=validateForm();
if(!ok){
	console.log('VALIDATE ERRORS');
	return false;
}



$('#submitbutton').hide();//avoid users clicking twice the submit button

//now post form manually with ajax using jquery object serialization (jquery uses url querystring format)
//var formData = new FormData(form.serialize());
var formData = new FormData();
formData.append('formsubmitdata',form.serialize());
console.log(form.serialize()); 
 
console.log("BB");

    var extraData ={}; //Extra Data.
    var jqXHR=$.ajax({
            xhr: function() {
            var xhrobj = $.ajaxSettings.xhr();
            return xhrobj;
        },
    url: submitURL,
    type: "POST",
    contentType:false,
    processData: false,
        cache: false,
        data: formData,
        success: function(data,  textStatus,  jqXHR){
        	submitFormOK(data,jqXHR.getResponseHeader("Content-Type"));
          },
         error: function(jqXHR, textStatus, errorThrown){
			setError('Server Error prepare:'+errorThrown);
        }
        
    }); 

    //$("#submitresultheader").empty();         
    //$("#submitresultheader").append("3. Submit result<br>");         
    $("#submitresultheader").show();       
    
	$("#submitresult").append("<br/>SUBMITTING FILES...");    
    $("#submitresult").append(htmlSpinner);


}
// submitFormOK() ajax ok
function submitFormOK(data,contenttype){
			console.log("got submit response");
            $("#submitresult").empty();   
            var html='';  
            if(contenttype.indexOf('application/json')>=0){
 	       		html=html+"<table>";
        		html=html+"<tr><td><b>File</b></td><td><b>Document Type</b></td><td><b>Size</b></td></tr>";

		        //no need to parse, jquery data object is automatically Array object
				//var files = jQuery.parseJSON(data);
				var files=data;
				for(var index = 0; index < files.length; index++){
					var file=files[index];
					html=html+"<tr>";
					html=html+"<td>"+file.fileName+"</td>";
					html=html+"<td>"+file.documentType+"</td>";
					html=html+"<td>"+file.size+"</td>";
					html=html+"</tr>";
					
				}
				html=html+"</table>";
				            	
            }else{
            	html=data;
            }
                
            $("#submitresult").append(html); 
			$("#submitresult").append(htmlCheckOK);
			$("#submitresult").append("<p>Files Submitted to server</p>");
			
            //also empty submitform
			//$("#submitform").empty();
}

 
var rowCount=0;

function handleFileUpload(files,obj){
	var name='';
	var totalsize=0;
   	var fd = new FormData();
	var promises = [];
	var filesok=true;
	
	//var fhtml="<table><tr><td><b>File</b></td><td><b>Size</b></td></tr>";
	var ctx=getUrlParameter('context');//eg. dossier id as url parameter
	//eg. http://127.0.0.1:8080/dragdrop/index.html?context=123

	clearAll();

   for (var i = 0; i < files.length; i++) {
   	var file=files[i];
   	if(i>0){
   		name=name+', ';
   	}
   	name=name +' ' +file.name;
   	if(file.size <= maxFileSizePerFile){
   		totalsize=totalsize+file.size;
   	
   		 promises.push(formDataItem(fd,file,obj,ctx));
		//fhtml=fhtml+"<tr>";
		//fhtml=fhtml+"<td class='filelist'>"+file.name+"</td><td class='filelist'>"+file.size+"</td>";
		//fhtml=fhtml+"</tr>";
	}else{
		setError(' File too big:'+file.name);
		filesok=false;
	
	}
   }
   if(totalsize>maxFileSizeTotal){
		setError(' Files total too big:'+totalsize);
		filesok=false;
   }
	//fhtml=fhtml+"</table>";

	//document.getElementById("filelist").innerHTML=fhtml 
   if(filesok){
  	 	$.when.apply($, promises).then(function(m) {
    	   //var status = new createStatusbar(obj); //Using this we can set progress.
        	//status.setFileNameSize(files.length+' File(s) Uploading',totalsize);

   		  sendFileToServer(fd,status);
		 //getDocTypesFromServer();
   		});
   	}
}



function hashBinary(file) {
   var md5 = CryptoJS.algo.MD5.create();
   md5.update(CryptoJS.enc.Latin1.parse(file));
   var hash = md5.finalize();
   return hash;
}

//'readAsBinaryString' works in chrome, firefox, safari but not in IE10, need 'readAsByteArrayBuffer'
function formDataItemOLD( fd, file,obj, ctx){
	var md5='';
	var dfd = $.Deferred();
	   	var reader = new FileReader(); 
		reader.onload = function(e) { 
			md5=hashBinary(e.target.result);
			console.log('MD5='+md5);
			//servlet expects FILEDATA prefix for file params
	        fd.append('FILEDATA'+',filesize='+file.size+',md5='+md5+',ctx='+ctx, file);

 
			dfd.resolve();
 	    }; 
    //do not readAsUrl since data will be converted and hash will be wrong
 	  reader.readAsBinaryString(file);
 	  
 	  return dfd.promise();
}


//IE10 compatibility
function formDataItem( fd, file,obj, ctx){
	var md5='';
	var dfd = $.Deferred();
	   	var reader = new FileReader(); 
		reader.onload = function(e) { 
			var binary="";
			var bytes=new Uint8Array(e.target.result);
			var length=bytes.byteLength;
			for(var i=0; i<length;i++){
				binary += String.fromCharCode(bytes[i]);
			}
			md5=hashBinary(binary);
			fd.append('FILEDATA'+',filesize='+file.size+',md5='+md5+',ctx='+ctx, file);

 
			dfd.resolve();
 	    }; 
    //only this works in all browsers incl IE10+
 	  reader.readAsArrayBuffer(file);
 	  
 	  return dfd.promise();
}



//attach jquery autocomplete to all input doctypes in form
function attachAutoComplete(){
	console.log('attaching autocomplete');
	//var form=$('form[name="processFilesForm"]');
	$(":input[type=text]").each(function() {
		//console.log('inputtypetext '+this.name);
		if (this.name.substring(0, 8) == "doctype_") {
			console.log('Doctype ,'+this.name+':'+this.value);
			console.log(this);
	
			$(this).autocomplete({
  				lookup: doctypes,
 				 minChars:0,
  				  onSelect: function (suggestion) {
 				   console.log('SELECTED:'+suggestion.value);
 				   this.value=suggestion.value;
			  }
			});
		}
    });
}
