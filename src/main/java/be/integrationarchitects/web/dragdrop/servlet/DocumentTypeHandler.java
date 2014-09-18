package be.integrationarchitects.web.dragdrop.servlet;

public interface DocumentTypeHandler {
	public String[] getDocumentTypes(DragDropContext ctx);
	public String[] getFileExtensions(DragDropContext ctx);
	public String getDocumentTypeForFileName(DragDropContext ctx,String fileName);
	
}
