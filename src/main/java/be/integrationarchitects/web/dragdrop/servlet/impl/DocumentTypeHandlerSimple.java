package be.integrationarchitects.web.dragdrop.servlet.impl;

import be.integrationarchitects.web.dragdrop.servlet.DocumentTypeHandler;
import be.integrationarchitects.web.dragdrop.servlet.DragDropContext;

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
