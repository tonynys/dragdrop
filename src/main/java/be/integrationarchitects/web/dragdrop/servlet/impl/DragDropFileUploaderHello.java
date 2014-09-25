package be.integrationarchitects.web.dragdrop.servlet.impl;


/*
 * Copyright (C) 2014 Integration Architects
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
