package be.integrationarchitects.web.dragdrop.servlet.impl;

import java.io.File;

import javax.servlet.ServletConfig;

import be.integrationarchitects.web.dragdrop.servlet.DocumentTypeHandler;
import be.integrationarchitects.web.dragdrop.servlet.DragDropFileEventHandler;
import be.integrationarchitects.web.dragdrop.servlet.DragDropFileUploader;
import be.integrationarchitects.web.dragdrop.servlet.DragDropServletConfig;
import be.integrationarchitects.web.dragdrop.servlet.Logger;
import be.integrationarchitects.web.dragdrop.servlet.ResponseContentEnum;

public class DragDropServletConfigImpl  implements DragDropServletConfig,Logger{
	protected boolean checkHash=true;
	protected File folder;
	protected DragDropFileEventHandler handler;
	protected boolean deleteAfterSubmit=true;
	protected boolean logTrace=false;
	protected boolean includeNotes=true;
	protected ResponseContentEnum responseContentType=ResponseContentEnum.JSON;
	protected int deleteAge=7;//7days
	protected int maxFileSizePerFile=10*1024*1024;
	protected int maxFileSizeTotal=maxFileSizePerFile*5;
	protected int fileUploadSpeed=5*1024*1024;//bytes per second max. upload speed
	//protected int fileUploadSpeed=30*1024;//bytes per second max. upload speed
	protected int functionalErrorHttpErrorCode=501;//do not use 500 , so we can distinguish server crashes (npe,...) and functional server err
	protected DragDropFileUploader uploader;
	protected DocumentTypeHandler docTypeHandler;

	

	public DragDropServletConfigImpl(){
		folder=new File(".");
		uploader=new DragDropFileUploaderHello();
		docTypeHandler=new DocumentTypeHandlerSimple();
		handler=new DragDropFileEventHandlerDefaultImpl(this);
	}

	public boolean checkHash() {
		return checkHash;
	}

	public void setCheckHash(boolean checkHash) {
		this.checkHash = checkHash;
	}

	public File getFolder() {
		return folder;
	}

	public void setFolder(File folder) {
		this.folder = folder;
	}

	


	public DragDropFileEventHandler getHandler() {
		return handler;
	}

	public void setHandler(DragDropFileEventHandler handler) {
		this.handler = handler;
	}

	@Override
	public DragDropServletConfig getInstance(ServletConfig cc) {
		return this;
	}

	@Override
	public boolean deleteFileAfterSubmit() {
		return deleteAfterSubmit;
	}

	@Override
	public void logDebug(String msg) {
		System.out.println(msg);		
	}
	@Override
	public void logTrace(String msg) {
		if(logTrace)
			System.out.println(msg);		
	}

	@Override
	public void logError(String msg, Throwable e) {
		System.err.println(msg);
		if(e!=null)
			e.printStackTrace();
		
	}
	@Override
	public void logError(String msg) {
		logError(null);
	}

	@Override
	public Logger getLogger() {
		return this;
	}

	@Override
	public int getOldFilesCleanupAgeDays() {
		return deleteAge;
	}


	@Override
	public int getFileUploadSpeed(){
		return fileUploadSpeed;
	}

	@Override
	public int getFunctionErrorHttpErrorCode() {
		return functionalErrorHttpErrorCode;
	}

	@Override
	public DragDropFileUploader getFileUploader() {
		return uploader;
	}

	@Override
	public DocumentTypeHandler getDocumentTypeHandler() {
		return docTypeHandler;
		
	}

	@Override
	public int getMaxFileSizePerFile() {
		return maxFileSizePerFile;
	}

	@Override
	public int getMaxFileSizeTotal() {
		return maxFileSizeTotal;
	}

	@Override
	public boolean includeNotes() {
		return includeNotes;
	}

	@Override
	public ResponseContentEnum getResponseContentType() {
		return responseContentType;
	}

}
