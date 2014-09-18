package be.integrationarchitects.web.dragdrop.servlet.impl;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import be.integrationarchitects.web.dragdrop.servlet.DragDropConstants;
import be.integrationarchitects.web.dragdrop.servlet.DragDropFileEventHandler;
import be.integrationarchitects.web.dragdrop.servlet.DragDropMimeFile;
import be.integrationarchitects.web.dragdrop.servlet.DragDropMimeFileResponse;
import be.integrationarchitects.web.dragdrop.servlet.DragDropMimeHandlerRequest;
import be.integrationarchitects.web.dragdrop.servlet.DragDropMimeHandlerResponse;
import be.integrationarchitects.web.dragdrop.servlet.DragDropServletConfig;
import be.integrationarchitects.web.dragdrop.servlet.ResponseContentEnum;

/**
 * This is a working demo implementation, but for real , subclass and add custom uploader and doctypehandler
 * 
 

 * 
 * @author tony
 *
 */
public class DragDropFileEventHandlerDefaultImpl implements DragDropFileEventHandler{


	
	protected DragDropServletConfig cfg;
	
	
	public DragDropFileEventHandlerDefaultImpl(DragDropServletConfig cfg){
		this.cfg=cfg;
	}
	
	
	@Override
	public DragDropMimeHandlerResponse prepare(DragDropMimeHandlerRequest request) {
		DragDropMimeHandlerResponse resp=new DragDropMimeHandlerResponse(request);
		resp.setResponseContentType(cfg.getResponseContentType());
	
		if(cfg.getResponseContentType()==ResponseContentEnum.HTML){
			prepareAndReturnHtml(request,resp);
		}else if(cfg.getResponseContentType()==ResponseContentEnum.JSON){
			prepareAndReturnJson(request,resp);
		}else{
			throw new RuntimeException("Cannnot submit, unknown contenttype:"+cfg.getResponseContentType());
		}
		return resp;
	}

	private void prepareAndReturnHtml(DragDropMimeHandlerRequest request,DragDropMimeHandlerResponse resp) {
		String html="";
	       // String html="<form name='"+DragDropConstants.FORM_PROCESS_FILES_NAME+"' method='POST'>";
	        html+="<input type='hidden' name='"+DragDropConstants.FORM_PROCESS_FILES_DROPID+"' value='"+request.getCtx().getDropID()+"'/>";
	        
	        if(request.getFiles()!=null){
	            //html+="<table>";
	            int i=0;
	        	for(DragDropMimeFile f:request.getFiles()){
	        		i++;
	        		String id=DragDropConstants.FORM_PROCESS_FILES_CHECK+i;
	                html+="<tr>";
	                html+="<td> <input type='hidden' name='"+DragDropConstants.FORM_PROCESS_FILES_MD5+i+"' value='"+f.getHash()+"'/>";
	                html+="<div class='roundedOne'>";
	                html+="<input type='checkbox'  name='"+id+"' id='"+id+ "' checked />";
	                html+="<label for='"+id+"'></label></div>";
	                html+="</td>";
	                html+="<td> <input type='text' class='"+DragDropConstants.CSS_BIGINPUT+"' size='60' name='"+DragDropConstants.FORM_PROCESS_FILES_FILENAME+i+"' value='"+f.getFileName()+"'/> </td>";

	                //name of inputfield must be doctype_xx : javascript will take all form input elements with name doctype_ and attach autocomplete handler
	                html+="<td> <input type='text' class='"+DragDropConstants.CSS_BIGINPUT+"'  name='"+DragDropConstants.FORM_PROCESS_FILES_DOCTYPE+i+"' value='"+cfg.getDocumentTypeHandler().getDocumentTypeForFileName(request.getCtx(),f.getFileName())+"'/> </td>";
	                if(cfg.includeNotes()){
	                	html+="<td> <input type='text' class='"+DragDropConstants.CSS_BIGINPUT+"' size='15' name='"+DragDropConstants.FORM_PROCESS_FILES_NOTE+i+"' value=''/> </td>";
	                }
	                html+="<td>"+f.getFile().length()+" </td>";
	      
	                html+="</tr>";
	        		
	        	}

	            html+="<tr><td>";
	            //html+="<button type='button' id='submitbutton' class='"+DragDropConstants.CSS_BUTTON+"' onclick='"+DragDropConstants.FORM_PROCESS_FILES_SUBMIT_JS+"'>Confirm Files</button>";
	            html+="</td></tr>";
	            
	           // html+="</table>";
	        }
	       // html+="</form>";
			resp.setResponseContent(html);
			
			//prepareAndReturnJson(request,resp);
	}
	private void prepareAndReturnJson(DragDropMimeHandlerRequest request,DragDropMimeHandlerResponse resp) {
		resp.setFiles(new ArrayList<DragDropMimeFileResponse>());
		
	        
	        if(request.getFiles()!=null){
	    		int i=0;
	        	for(DragDropMimeFile f:request.getFiles()){
	        		i++;
		    		DragDropMimeFileResponse file=new DragDropMimeFileResponse(f);
		    		resp.getFiles().add(file);
		    		file.setDropId(request.getCtx().getDropID());
		    		file.setChecked(true);
		    		file.setDocumentType(cfg.getDocumentTypeHandler().getDocumentTypeForFileName(request.getCtx(),f.getFileName()));
	        		
	        	}
	        }
	        resp.setResponseContent(resp.toJson());
	        //System.out.println(resp.toJson());
	}
	
	@Override
	public DragDropMimeHandlerResponse submit(DragDropMimeHandlerRequest request) {
		DragDropMimeHandlerResponse resp=new DragDropMimeHandlerResponse(request);
		resp.setResponseContentType(cfg.getResponseContentType());
		if(cfg.getResponseContentType()==ResponseContentEnum.HTML){
			submitAndReturnHtml(request,resp);
		}else if(cfg.getResponseContentType()==ResponseContentEnum.JSON){
			submitAndReturnJson(request,resp);
		}else{
			throw new RuntimeException("Cannnot submit, unknown contenttype:"+cfg.getResponseContentType());
		}
	
		return resp;
	}
	private void submitAndReturnJson(DragDropMimeHandlerRequest request,DragDropMimeHandlerResponse resp) {
		resp.setFiles(new ArrayList<DragDropMimeFileResponse>());
		for(DragDropMimeFile fr:request.getFiles()){
			if(fr.isChecked() && fr.getDocumentType()!=null && fr.getDocumentType().trim().length()>0){
				fr.setFileName(cfg.getFileUploader().uploadFile(cfg.getLogger(),request, fr));
			}else{
				fr.setFileName("Skipped file");
				
			}
			DragDropMimeFileResponse z=new DragDropMimeFileResponse(fr);
			resp.getFiles().add(z);
		}
        resp.setResponseContent(resp.toJson());
        //System.out.println(resp.toJson());
	}
	
	private void submitAndReturnHtml(DragDropMimeHandlerRequest request,DragDropMimeHandlerResponse resp) {
		String html="";
        html+="<table>";
        html+="<tr><td><b>File</b></td><td><b>Document Type</b></td><td><b>Size</b></td></tr>";

		for(DragDropMimeFile fr:request.getFiles()){
	        html+="<tr>";
			if(fr.isChecked() &&fr.getDocumentType()!=null &&  fr.getDocumentType().trim().length()>0){
				html+="<td class='filelist'>"+cfg.getFileUploader().uploadFile(cfg.getLogger(),request, fr)+"</td>";
				html+="<td class='filelist'>"+fr.getDocumentType()+"</td>";
				html+="<td class='filelist'>"+fr.getFile().length()+"</td>";
			}else{
				html+="<td>Skipped:"+fr.getFileName()+"</td>";
				
			}
	        html+="</tr>";
		}
        html+="</table>";
		//html+="<p>"+request.getFiles().size()+" File(s) Submitted for request "+request.getDropID();
		resp.setResponseContent(html);	
	}


	


	@Override
	public String getUserForRequest(HttpServletRequest req) {
		return "UNKNOWN";
	}




}
