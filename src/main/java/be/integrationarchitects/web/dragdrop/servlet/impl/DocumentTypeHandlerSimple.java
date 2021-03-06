package be.integrationarchitects.web.dragdrop.servlet.impl;

import be.integrationarchitects.web.dragdrop.servlet.DocumentTypeHandler;
import be.integrationarchitects.web.dragdrop.servlet.DragDropContext;


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

public class DocumentTypeHandlerSimple implements DocumentTypeHandler{
	
	//better put this in a .js file, so it is at runtime changeable, see example
	public static String[] DOCTYPES={"Boxi","CommitteePresentation","Contract","ContractSigned","Divers", "Quote","Supplier"};
	public static String[] FILE_EXTENSIONS={"pdf","docx","doc","rtf","txt", "xls","xlsx","eml","msg","zip","jpeg","png","ppt","pptx"};

	@Override
	public String[] getDocumentTypes(DragDropContext ctx) {
		//clone array
		String[] ss=new String[DOCTYPES.length];
		int i=0;
		for(String dt:DOCTYPES){
			ss[i++]=dt;
		}
		return ss;
	}


	@Override
	public String getDocumentTypeForFileName(DragDropContext ctx,String fileName) {
		String p=fileName.trim().toLowerCase();
		if(p.indexOf("ctr")>=0 || p.indexOf("contract")>=0 || p.indexOf("contrat")>=0 ){
			return "Contract";
		}
		if(p.indexOf("quot")>=0 || p.indexOf("offer")>=0 || p.indexOf("price")>=0 ){
			return "Quote";
		}
		if(p.indexOf("nihil")>=0 || p.indexOf("n.o")>=0 || p.indexOf("obstat")>=0 || p.indexOf("ap_")>=0 ){
			return "CommitteePresentation";
		}
		if(p.indexOf("four")>=0 || p.indexOf("sup")>=0 || p.indexOf("leveran")>=0){
			return "Supplier";
		}
		if(p.indexOf("diver")>=0 ){
			return "Divers";
		}
		
		return "";
	}


	@Override
	public String[] getFileExtensions(DragDropContext ctx) {
		return FILE_EXTENSIONS;
	}
	


}
