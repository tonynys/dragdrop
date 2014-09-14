package be.integrationarchitects.web.dragdrop.servlet.impl;

import be.integrationarchitects.web.dragdrop.servlet.DragDropFileUploader;
import be.integrationarchitects.web.dragdrop.servlet.DragDropMimeFile;
import be.integrationarchitects.web.dragdrop.servlet.DragDropMimeHandlerRequest;
import be.integrationarchitects.web.dragdrop.servlet.Logger;
/**
 * 
 * sample dummy upload as example
 * 
 * @author tony
 *
 */
public class DragDropFileUploaderHello implements DragDropFileUploader {

	@Override
	public String uploadFile(Logger logger, DragDropMimeHandlerRequest req,
			DragDropMimeFile fr) {
		String html="<a href='"+fr.getNewfileName()+"'>"+fr.getNewfileName()+"</a>";
		logger.logDebug("...Uploading submitted file:"+html);
		/**
		try {
			System.out.println("Sleppppp submit");
			Thread.currentThread().sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		**/
		return html;
	}

}
