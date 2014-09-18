package be.integrationarchitects.web.dragdrop.servlet;

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
		this.ctxScope=requestParams.get(CTX_SCOPE);
		this.ctxName=requestParams.get(CTX_NAME);
		this.ctxRef=requestParams.get(CTX_REF);
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
