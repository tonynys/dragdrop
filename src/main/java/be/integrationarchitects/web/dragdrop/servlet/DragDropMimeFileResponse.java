package be.integrationarchitects.web.dragdrop.servlet;

import java.io.File;
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

/**
 * used for JSON response
 * @author tony
 *
 */
public class DragDropMimeFileResponse extends DragDropMimeFile{
	private String dropId;
	private String errorMessage;//error prepare/submit
	
	public DragDropMimeFileResponse(DragDropMimeFile requestfile){
		super.cloneMe(requestfile);
	}

	public String getDropId() {
		return dropId;
	}

	public void setDropId(String dropId) {
		this.dropId = dropId;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	/**
	 *     {"firstName":"John", "lastName":"Doe"}, 

	 */
	public String toJson(){
		String js="";
		js+="{";
		js+=super.toJson()+",";
		js+="\"dropId\":"+toJson(dropId)+",";
		js+="\"errorMessage\":"+toJson(errorMessage);
		js+="}";
		return js;

		
	}

}
