package be.integrationarchitects.web.dragdrop.servlet;

import java.io.File;
import java.util.Map;

public class DragDropMimeFile {
	private File file;
	private String hash;//Md5
	private String fileName;//original filename
	private String newfileName;//filename that user might have changed at submit time
	private String documentType;//at submit time
	private boolean checked=false;//true if file was selected for submit
	private String note;
	
	/**
	 * format "name_index:value"
	 *  FORM PARAM:md5_1:5b9f16527d8d27f541cbb3fabf432eb6
		FORM PARAM:filecheck_1:checked
		FORM PARAM:filename_1:17.csv
		FORM PARAM:doctype_1:ContractSigned
	 */
	private Map<String, String> prepareParams;//params from .inf file at prepare time; at submit time eg. the filename can change

	public boolean isChecked() {
		return checked;
	}
	public void cloneMe(DragDropMimeFile f){
		this.hash=f.hash;
		this.fileName=f.fileName;
		this.newfileName=f.newfileName;
		this.documentType=f.documentType;
		this.checked=f.checked;
		this.note=f.note;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public Map<String, String> getPrepareParams() {
		return prepareParams;
	}

	public void setPrepareParams(Map<String, String> prepareParams) {
		this.prepareParams = prepareParams;
	}

	public DragDropMimeFile(){
		
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}


	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getNewfileName() {
		return newfileName;
	}

	public void setNewfileName(String newfileName) {
		this.newfileName = newfileName;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	
	public String toJson(boolean b){
		return ""+b;//without quotes
	}
	public String toJson(int i){
		return ""+i;
	}
	public String toJson(long i){
		return ""+i;
	}
	
	public String toJson(String s){
		if(s==null)
			return "null";//without quotes

		String js="";
		js="\""+s+"\"";
		return js;
	}
	
	public String toJson(){
		String js="";
		js+="\"hash\":"+toJson(hash)+",";
		js+="\"fileName\":"+toJson(fileName)+",";
		js+="\"newfileName\":"+toJson(newfileName)+",";
		js+="\"documentType\":"+toJson(documentType)+",";
		js+="\"checked\":"+toJson(checked)+",";
		js+="\"note\":"+toJson(note)+",";
		js+="\"size\":"+toJson(file==null ? 0:file.length());
		return js;
	}

}
