package be.integrationarchitects.web.dragdrop.servlet;

import java.util.List;



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

public class DragDropMimeHandlerResponse extends DragDropMimeHandlerReqResp{
	private String responseContent;//html or json content
	private ResponseContentEnum responseContentType;//text/html or application/json: if html table with file rows, if json, json object of files
	
	private List<DragDropMimeFileResponse> files;
	
	public DragDropMimeHandlerResponse(){
		
	}
	public DragDropMimeHandlerResponse(DragDropMimeHandlerRequest req){
		super.cloneMe(req);
		
	}



	public boolean isHtml(){
		return responseContentType.equals("text/html");
	}
	public boolean isJson(){
		return responseContentType.equals("application/html");
	}
	public String getResponseContent() {
		return responseContent;
	}

	public void setResponseContent(String responseContent) {
		this.responseContent = responseContent;
	}
	
	

	public ResponseContentEnum getResponseContentType() {
		return responseContentType;
	}
	public void setResponseContentType(ResponseContentEnum responseContentType) {
		this.responseContentType = responseContentType;
	}

	public List<DragDropMimeFileResponse> getFiles() {
		return files;
	}
	public void setFiles(List<DragDropMimeFileResponse> files) {
		this.files = files;
	}
	/**
	 * * json format eg.
 * 
"[
    {"firstName":"John", "lastName":"Doe"}, 
    {"firstName":"Anna", "lastName":"Smith"}, 
    {"firstName":"Peter", "lastName":"Jones"}
]
in javascript: eg. var obj = jQuery.parseJSON( '{ "name": "John" }' );
(jquery does this automatically so no need to do this in the success function data object)
 

     * normally should use JAXB with json serializer, but here manually to keep dependencies to a minimum
     * see http://jsonviewer.stack.hu/ 
     * for a visualizer
	 */
	public String toJson(){
		String js="";
		int i=0;
		js+="[";
		if(files!=null){
			for(DragDropMimeFileResponse f:files){
				i++;
				js+=f.toJson();
				if(i<files.size()){
					js+=",";
				}
			}
		}
		js+="]";
		
		return js;
		//this.responseContent=js;
		//this.responseContentType=ResponseContentEnum.JSON;
		
	}
}
