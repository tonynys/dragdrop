package be.integrationarchitects.web.dragdrop.servlet;

import java.io.File;

import javax.servlet.ServletConfig;

public interface DragDropServletConfig {

	public DragDropServletConfig getInstance(ServletConfig c);
	public boolean checkHash();
	public File getFolder();
	public DragDropFileEventHandler getHandler();
	public boolean deleteFileAfterSubmit();
	public Logger getLogger();
	public int getOldFilesCleanupAgeDays();
	public int getMaxFileSizePerFile();//max size per file
	public int getMaxFileSizeTotal();//for all files in upload
	public int getFileUploadSpeed();
	public int getFunctionErrorHttpErrorCode();//500, 501 must match with error page in web.xml
	public boolean includeNotes();
	
	public DragDropFileUploader getFileUploader();
	public DocumentTypeHandler getDocumentTypeHandler();
	public ResponseContentEnum getResponseContentType();
	
}
