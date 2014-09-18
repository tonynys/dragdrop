package be.integrationarchitects.web.dragdrop.servlet;

import java.util.List;
import java.util.Map;

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
