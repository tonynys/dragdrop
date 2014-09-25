package be.integrationarchitects.web.dragdrop.servlet;


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

import java.util.Map;

/**
 * 
 * context allows same servlet installation be used for multi purposes
 * 
 * @author tony
 *
 */
public class DragDropContext {
	
	public static final String CTX_ID="dropId";
	public static final String CTX_SCOPE="ctxScope";
	public static final String CTX_NAME="ctxName";
	public static final String CTX_REF="ctxRef";
	
	private String ctxID;//random context ID, some kind of conversation id = dropID
	private String user;
	private String ctxScope;//eg.Quote 
	private String ctxName;//eg. Customer ABC
	private String ctxRef;//eg.Dossier 123

	public DragDropContext(String user, Map<String, String> requestParams, Map<String, String> requestHeaders){
		this.ctxID=requestParams.get(CTX_ID);
		this.user=user;
		this.ctxScope=requestHeaders.get(CTX_SCOPE);
		this.ctxName=requestHeaders.get(CTX_NAME);
		this.ctxRef=requestHeaders.get(CTX_REF);
	}
	
	public boolean validateContext(){
		if(ctxID==null || ctxID.trim().equals(""))
			return false;
		if(user==null || user.trim().equals(""))
			return false;
		if(ctxScope==null || ctxScope.trim().equals(""))
			return false;
		if(ctxName==null || ctxName.trim().equals(""))
			return false;
		if(ctxRef==null || ctxRef.trim().equals(""))
			return false;
		return true;
			
	}
	
	public String getCtxScope() {
		return ctxScope;
	}

	public void setCtxScope(String ctxScope) {
		this.ctxScope = ctxScope;
	}

	public String getDropID() {
		return ctxID;
	}

	public void setDropID(String dropID) {
		this.ctxID = dropID;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getCtxName() {
		return ctxName;
	}

	public void setCtxName(String ctxName) {
		this.ctxName = ctxName;
	}
	public String getCtxRef() {
		return ctxRef;
	}

	public void setCtxRef(String ctxRef) {
		this.ctxRef = ctxRef;
	}


}
