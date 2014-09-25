package be.integrationarchitects.web.dragdrop.servlet;

import java.io.File;

import javax.servlet.ServletConfig;


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
