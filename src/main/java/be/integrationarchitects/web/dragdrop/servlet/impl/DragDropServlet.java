package be.integrationarchitects.web.dragdrop.servlet.impl;

 import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.MultipartStream;

import be.integrationarchitects.web.dragdrop.servlet.DragDropContext;
import be.integrationarchitects.web.dragdrop.servlet.DragDropMimeFile;
import be.integrationarchitects.web.dragdrop.servlet.DragDropMimeHandlerResponse;
import be.integrationarchitects.web.dragdrop.servlet.DragDropServletConfig;
import be.integrationarchitects.web.dragdrop.servlet.DragDropMimeHandlerRequest;
import be.integrationarchitects.web.dragdrop.servlet.Logger;



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

/**
 * Servlet for upload/download through drag and drop. It can return html or JSON, by default JSON so all UI rendering is done at client.
 * 
 * For Upload , process has 2 steps:
 * 1. Prepare: upload 1 multipart stream with multiple bodyparts (files). Return a jsp/html with the files details
 * 2. Submit: process the files selected in the selection the user made after step 1
 * 
 * Designed with a minimum of external dependencies in mind, to avoid version clashes, eg. for logging, json/jaxb, Spring, jee, etc...
 *  minimal jars:  commons-io, commons-codec, commons-file-upload
 *  all other jars are for Runjetty testcase only
 *
 * Servlet 2.0 api container compatible (although 3.0 getParts would be nice)
 *  
 * Completely pluggable using servlet config startup parameter
 * 
 * Uses Http response code 501 so Browser rest client can check on http 501 to check functional errors
 * 
 * @author tony nys
 *
 */
public class DragDropServlet extends HttpServlet {

	protected DragDropServletUtils utils;
	protected DragDropServletConfig cfg;
	protected SecureRandom random;
	protected Logger logger;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException{
		random=new SecureRandom();
		    String str_cfg = servletConfig.getInitParameter("cfg");
		    Class c=null;
		    try {
				c = Class.forName(str_cfg);
				cfg=(DragDropServletConfig)c.newInstance();
				logger=cfg.getLogger();
				
				//used in 500.jsp for error logging
				servletConfig.getServletContext().setAttribute("mycfg", cfg);
				
			} catch (Exception e) {
				System.err.println(e.getMessage());
				throw new ServletException(e);
			}
			utils=new DragDropServletUtils(cfg.getFolder(),cfg.checkHash(),logger);
			logger.logDebug(".....................................Init drag drop servlet ok:"+str_cfg+":"+cfg.getHandler()+":"+cfg.getFolder());
	}
	
	@Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)  throws IOException, ServletException    {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("</head>");
        out.println("<body> GET Not supported");
        out.println("</body>");
        out.println("</html>");
        logger.logDebug("GET NOT ALLOWED");
        
    }

	
	
	@Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
    	logger.logDebug("Do post..."+new Date().toString());
    	boolean prepare=false;
    	boolean submit=false;
    	String url=request.getRequestURI();
    			logger.logTrace("context path:"+request.getContextPath()+":"+request.getSession().getServletContext().getContextPath()+":"+request.getRequestURL()+":"+request.getRequestURI());
    			
    	prepare=url.endsWith("/upload/prepare");		
    	submit=url.endsWith("/upload/submit");		

    	if(prepare){
    		//Step 1:prepare files
    		doPrepare(request,response);
    	}else if(submit){
    		//Step 2:submit files
    		doSubmit(request,response);
    		
    	}else if(url.endsWith("/upload/doctypes")){
    		//ajax request for doctypes
    			doDocTypes(request,response);
    	}else{
        	logger.logError("ERROR:"+"invalid action:"+url+"...");
        	response.sendError(405,"Invalid server action");
        	return;
    		
    	}
    	
	
    }

	protected void doPrepare(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		
		Map<String, Map<String, String>> p=utils.getHeadersParams(request,false);
		String ddropId=getRandom();
		p.get("params").put(DragDropContext.CTX_ID,ddropId );
		DragDropContext ctx=new DragDropContext(cfg.getHandler().getUserForRequest(request),p.get("params"),p.get("headers"));
		if(!ctx.validateContext()){
        	logger.logError("ERROR:context params missing");
        	setServerError(request,response,"ERROR:context params missing");
        	return;
		}
		
		DragDropMimeHandlerRequest mimeRequest=new DragDropMimeHandlerRequest(p.get("params"),p.get("headers"),ctx);
		
		//mimeRequest.getCtx().setDropID(getRandom());
		
		//test server side functional error
//		if(1==1){
  //      	setServerError(request,response,"test server errorrrr");
    //    	return;
		//}
		
        File f=null;
        //String user=cfg.getHandler().getUserForRequest(request);
        try{
        	f=utils.serialize(mimeRequest.getCtx(),request.getInputStream(),cfg.getFileUploadSpeed());
        }catch(IllegalArgumentException e){
        	logger.logError("ERROR:"+e.getMessage(),e);
        	setServerError(request,response,"Error saving prepare request");
        	return;
        }

        cleanUpOldFiles();
        
        if(cfg.getMaxFileSizeTotal()>0){
	        if(f.length()>cfg.getMaxFileSizeTotal()){
	        	//total prepare size for all files check size
	        	logger.logError("File too big:"+f.length()+":"+f.getName());
	        	f.delete();
	        	setServerError(request,response,"File prepare too big");
	        	return;
	        }
	     }
        
        //http header
        //content-type=multipart/form-data; boundary=----WebKitFormBoundaryoPekYfIk57uBF67C

        String boundary=null;
        String sb=request.getHeader("content-type");
        int i=sb.indexOf("boundary=");
        if(i>0){
        	boundary=sb.substring(i+9);
        	logger.logTrace("Boundary::"+boundary);
        }
        mimeRequest.setMimeBoundary(boundary);
       // mimeRequest.getCtx().setUser(cfg.getHandler().getUserForRequest(request));
        try{
        	prepareMultiPartFile(mimeRequest,f,response);
        }catch(Throwable e){
        	logger.logError("ERROR:"+e.getMessage(),e);
        	setServerError(request,response,"Error prepareMultiPartFile");
        	throw new RuntimeException(e);
        }
        logger.logTrace(("------------------"));
        logger.logTrace((new String(""+f.getName()+":"+f.length())));
        logger.logTrace(("------------------"));
        logger.logDebug("...Post done #bytes:"+f.length());
	}
	
	
	
	
	
	protected synchronized void cleanUpOldFiles() {
		if(cfg.getOldFilesCleanupAgeDays()<=0)
			return;
		
		String[] files=cfg.getFolder().list();
		if(files==null)
			return;
		
		
		
		Calendar now=Calendar.getInstance();
		now.add(Calendar.DATE,cfg.getOldFilesCleanupAgeDays()*-1);
		
		for(String fileName:files){
			File f=new File(cfg.getFolder(),fileName);
			if(! f.isFile()){
				continue;
			}
			if(f.getName().endsWith(".dat") || f.getName().endsWith(".inf")|| f.getName().endsWith(".mime")){
				Date d=new Date(f.lastModified());
				Calendar fcal=Calendar.getInstance();
				fcal.setTime(d);
				if(now.after(fcal)){
					logger.logDebug("Delete old file:"+f.getName());
					f.delete();
				}
			}
			
		}
		
	}

	protected void doSubmit(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		
		logger.logDebug("do submit");

		
		Map<String, Map<String, String>> p=utils.getHeadersParams(request,true);
		DragDropContext ctx=new DragDropContext(cfg.getHandler().getUserForRequest(request),p.get("params"),p.get("headers"));
		
		DragDropMimeHandlerRequest mr=new DragDropMimeHandlerRequest(p.get("params"),p.get("headers"),ctx);
	
		if(!ctx.validateContext()){
        	logger.logError("ERROR:context params missing");
        	setServerError(request,response,"ERROR:context params missing");
        	return;
		}

		
		//mr.getCtx().setDropID(mr.getRequestParams().get("dropId"));
		//mr.getCtx().setUser(cfg.getHandler().getUserForRequest(request));
		
		//get files prepared, now submitting
		utils.getFilesToSubmitForPreparedRequest(mr);
		
        if(cfg.getHandler()==null || mr.getFiles().size()==0){
        	return;
        }
        logger.logDebug("Submitting files:"+mr.getFiles());
    	DragDropMimeHandlerResponse mimeResponse=cfg.getHandler().submit(mr);
    	if(mimeResponse.getResponseContent()!=null){
    		logger.logTrace("setting html response content...");
            response.setContentType(mimeResponse.getResponseContentType().getMimeType());
            response.getWriter().println(mimeResponse.getResponseContent());
    	}else{
    		
    		//TODO check redirects
    		
    	}
   
    	deleteSubmittedFiles(mr);
		
	}
	private void deleteSubmittedFiles(DragDropMimeHandlerRequest mr) {
	 	//clean up submitted files
    	if(cfg.deleteFileAfterSubmit()){
			String name=null;
    		for(DragDropMimeFile fr:mr.getFiles()){
    			logger.logDebug("Deleting submitted file:"+fr.getFile().getName());
    			name=fr.getFile().getName();
    			fr.getFile().delete();
    			
    			//now delete also .inf file
    			int i=name.lastIndexOf(".dat");
    			if(i>0){
    				String s=name.substring(0,i)+".inf";
    				File f=new File(cfg.getFolder(),s);
    				f.delete();
    			}
    		}  
    		if(name!=null){
    			//also delete .mime file
    			int i=name.lastIndexOf(".mime");
    			if(i>0){
					String s=name.substring(0,i)+".mime";
					File f=new File(cfg.getFolder(),s);
					f.delete();
    			}
    		}
    	}		
	}

	/**
	 * 
	 * set server error 501, this will trigger a JSP page which will read our custom SERVER_ERROR attribute,
	 * used in the ajax response javascript
	 */
	protected void setServerError(HttpServletRequest request, HttpServletResponse response, String msg) throws IOException{
		cfg.getLogger().logDebug("Server functional error:"+msg);
    	response.sendError(cfg.getFunctionErrorHttpErrorCode(),msg);
    	request.setAttribute("SERVER_ERROR", msg);
	}
	
	
	public void doDocTypes( HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		if(cfg.getHandler()==null){
        	setServerError(request,response,"NO HANDLER");
			return;
		}
		
		
		Map<String, Map<String, String>> p=utils.getHeadersParams(request,false);
		DragDropContext ctx=new DragDropContext(cfg.getHandler().getUserForRequest(request),p.get("params"),p.get("headers"));
		
	
		
		String html="";
		for(String dt:cfg.getDocumentTypeHandler().getDocumentTypes(ctx)){
			html+=dt+",";
		}
		response.getWriter().write(html);
	}
	
	protected void prepareMultiPartFile(DragDropMimeHandlerRequest mimeRequest, File f, HttpServletResponse response) throws IOException{
		logger.logTrace("process multipart");
		mimeRequest.setFiles(new ArrayList<DragDropMimeFile>());
		
        PrintWriter out = response.getWriter();
        
        FileInputStream fin=new FileInputStream(f);
        
        int partcount=0;
        		
        try {
        	
            MultipartStream multipartStream = new MultipartStream(fin,mimeRequest.getMimeBoundary().getBytes() ,1000,null);
            boolean nextPart = multipartStream.skipPreamble();
            int filecount=0;
            while(nextPart) {
            	partcount++;
            	String header = multipartStream.readHeaders();
            	logger.logTrace("PART HEADER:"+header);
            	if(header.startsWith("Content-Disposition: form-data")){
              		filecount++;

              		//params
              		Map<String, String> params=utils.getFilePartParams(header);
              		
              		//also save request params to .inf file
              		for(String key:mimeRequest.getRequestParams().keySet()){
              			params.put(key,mimeRequest.getRequestParams().get(key));
              		}
              		params.put("user", mimeRequest.getCtx().getUser());
              		
            		ByteArrayOutputStream bout=new ByteArrayOutputStream();
        			bout.write(params.toString().getBytes());
                	File f2=new File(cfg.getFolder(),f.getName()+"."+filecount+".inf");
                    FileOutputStream output=new FileOutputStream(f2);
                    output.write(bout.toByteArray());
                    output.close();
                    
                    
                    //file
                	File f3=new File(cfg.getFolder(),f.getName()+"."+filecount+".dat");
                    FileOutputStream output3=new FileOutputStream(f3);
                    multipartStream.readBodyData(output3);
                    output3.close();
                    String hash=utils.getHash(f3);
                    logger.logTrace("hash:"+hash+":"+params.get("md5")+", equals:"+hash.trim().equalsIgnoreCase(params.get("md5").trim()));
                    if(cfg.checkHash()){
                    	if(! hash.trim().equalsIgnoreCase(params.get("md5").trim())){
                    		logger.logError("Invalid hash:"+params.get("md5"),null);
                    		throw new IllegalArgumentException("Invalid hash:"+params.get("md5"));
                    	}
                    }
                    
                    if(cfg.getMaxFileSizePerFile()>0 && f3.length()>cfg.getMaxFileSizePerFile()){
                    		logger.logError("File too big hash:"+f3.length(),null);
                    }else{
	                    DragDropMimeFile tf=new DragDropMimeFile();
	                    tf.setFile(f3);
	                    tf.setHash(hash);
	                    tf.setPrepareParams(params);
	                    tf.setFileName(params.get("filename"));
	                    mimeRequest.getFiles().add(tf);
                    }
            	}else{
            		System.err.println("skipping part:"+header);
                    multipartStream.readBodyData(new ByteArrayOutputStream());

            	}

            	nextPart = multipartStream.readBoundary();
            }
           // f.delete();//delete multipart upload file since already splitted in info and data
          } catch(MultipartStream.MalformedStreamException e) {
        	  logger.logError(e.getMessage(),e);
        	  e.printStackTrace();
      		throw new IllegalArgumentException(e);
          } catch(IOException e) {
        	  logger.logError(e.getMessage(),e);
        	  e.printStackTrace();
        		throw new IllegalArgumentException(e);

          }

        
        if(cfg.getHandler()!=null){
        	DragDropMimeHandlerResponse mimeResponse=cfg.getHandler().prepare(mimeRequest);
        	if(mimeResponse.getResponseContent()!=null){
        		logger.logTrace("setting html response content...");
                response.setContentType(mimeResponse.getResponseContentType().getMimeType());
                out.println(mimeResponse.getResponseContent());
        	}else{
        		
        		//TODO check redirects
        		
        	}
        	//handler.handleFile(f2,params,reqparams,reqheaders);
        }


	}
	
	
	protected String getRandom(){
		return ""+(System.currentTimeMillis() | random.nextInt(1000000));
		
	}
}
