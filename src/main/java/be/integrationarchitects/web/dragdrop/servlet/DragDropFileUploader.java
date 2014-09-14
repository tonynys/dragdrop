package be.integrationarchitects.web.dragdrop.servlet;


public interface DragDropFileUploader {
	public String uploadFile(Logger logger,DragDropMimeHandlerRequest req,
			DragDropMimeFile fr);

}
