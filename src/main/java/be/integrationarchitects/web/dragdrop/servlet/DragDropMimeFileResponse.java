package be.integrationarchitects.web.dragdrop.servlet;

import java.io.File;
import java.util.Map;

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
