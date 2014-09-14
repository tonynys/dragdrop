package be.integrationarchitects.web.dragdrop.servlet;

import javax.servlet.http.HttpServletRequest;


public interface DragDropFileEventHandler {
	public String getUserForRequest(HttpServletRequest req);
	public DragDropMimeHandlerResponse prepare(DragDropMimeHandlerRequest request);
	public DragDropMimeHandlerResponse submit(DragDropMimeHandlerRequest request);
}
