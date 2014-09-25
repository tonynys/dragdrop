package be.integrationarchitects.web.dragdrop.servlet;

import java.util.List;
import java.util.Map;


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

public class DragDropMimeHandlerRequest extends DragDropMimeHandlerReqResp{
	private String mimeBoundary;
	
	private Map<String, String> requestParams;
	private Map<String, String> requestHeaders;

	private List<DragDropMimeFile> files;

	
	public DragDropMimeHandlerRequest(Map<String, String> requestParams, Map<String, String> requestHeaders, DragDropContext contx){
		this.setRequestParams(requestParams);
		this.setRequestHeaders(requestHeaders);
		setCtx(contx);
		
	}
	

	public Map<String, String> getRequestParams() {
		return requestParams;
	}
	public void setRequestParams(Map<String, String> requestParams) {
		this.requestParams = requestParams;
	}
	public Map<String, String> getRequestHeaders() {
		return requestHeaders;
	}
	public void setRequestHeaders(Map<String, String> requestHeaders) {
		this.requestHeaders = requestHeaders;
	}


	public String getMimeBoundary() {
		return mimeBoundary;
	}

	public void setMimeBoundary(String mimeBoundary) {
		this.mimeBoundary = mimeBoundary;
	}
	
	public List<DragDropMimeFile> getFiles() {
		return files;
	}
	public void setFiles(List<DragDropMimeFile> files) {
		this.files = files;
	}

}
