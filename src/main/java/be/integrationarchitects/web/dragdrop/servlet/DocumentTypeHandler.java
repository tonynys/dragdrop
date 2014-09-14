package be.integrationarchitects.web.dragdrop.servlet;

public interface DocumentTypeHandler {
	public String[] getDocumentTypes();
	public String[] getFileExtensions();
	public String getDocumentTypeForFileName(String fileName);
	
}
