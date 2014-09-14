package be.integrationarchitects.web.dragdrop.servlet.impl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Hex;

import be.integrationarchitects.web.dragdrop.servlet.DragDropConstants;
import be.integrationarchitects.web.dragdrop.servlet.DragDropMimeFile;
import be.integrationarchitects.web.dragdrop.servlet.DragDropMimeHandlerResponse;
import be.integrationarchitects.web.dragdrop.servlet.DragDropMimeHandlerRequest;
import be.integrationarchitects.web.dragdrop.servlet.Logger;

public class DragDropServletUtils {
	protected File folder;
	protected boolean checkHash=true;
	protected Logger logger;

	public DragDropServletUtils(File folder,boolean checkHash, Logger logger){
		this.folder=folder;
		this.checkHash=checkHash;
		this.logger=logger;
	}
	protected void getHeadersParams(HttpServletRequest request, DragDropMimeHandlerRequest mr, boolean readFormDataPost) throws IOException{

        Map<String, String> reqparams= new HashMap<String, String>();
        Map<String, String> reqheaders= new HashMap<String, String>();
            
        mr.setRequestHeaders(reqheaders);
        mr.setRequestParams(reqparams);
        
        if(readFormDataPost){
    		// servlet api <3.0 will return null values for formdata post, from 3.0 servlet api use request.getParts()
    		//using 2.0 api and parsing ourselves parts
        	
        	// in the javascript we use jquery object.serialize() to submit the form with ajax
        	//this format is used:
        	/**FORM DATA will be:
        	 * 
        	 * ------WebKitFormBoundary3UAOvASvkiQACgBY
Content-Disposition: form-data; name="formsubmitdata"

dropId=1406409768959&md5_1=5b9f16527d8d27f541cbb3fabf432eb6&filename_1=17.csv&doctype_1=
------WebKitFormBoundary3UAOvASvkiQACgBY--
        	 */
    		InputStream in=request.getInputStream();
    		ByteArrayOutputStream bout=new ByteArrayOutputStream();
    		byte[] buff=new byte[10000];
    		int bytesread=0;
    		BufferedReader br=new BufferedReader(new InputStreamReader(in));
    		String line=null;
    		//while((line=br.readLine()).length()>0){
    		while((bytesread=in.read(buff))>0){
    			bout.write(buff,0,bytesread);
		
    			//System.out.println("FORMDATA:"+new String(buff,0,bytesread));
       //			System.out.println("FORMDATA:"+line);
	    			//bout.write(buff,0,bytesread);
       			
//       			if(!line.startsWith("Content-Disposition")){
 //      		    			//bout.write(buff,0,bytesread);
   //    		    			bout.write(line.getBytes());
     //  			}
    		}
    		String formparamsStr=new String(bout.toByteArray());
    		logger.logTrace("FORMDATA:"+formparamsStr);
    		int jj=formparamsStr.indexOf("name=\"formsubmitdata\"");
    		formparamsStr=formparamsStr.substring(jj+21);
    		jj=formparamsStr.indexOf("---");
    		formparamsStr=formparamsStr.substring(0,jj);
    		
    		String[] formParamPairs=formparamsStr.split("&");
    		if(formParamPairs!=null){
    			for(String pair:formParamPairs){
    				
					int ii=pair.indexOf("=");
					if(ii<=0){
						continue;
					}
					String key=pair.substring(0,ii).trim();
					String value=pair.substring(ii+1).trim();
					value=URLDecoder.decode(value, "UTF-8");
		        	reqparams.put(key, value);
		        	logger.logTrace("FORM PARAM:"+key+":"+value);

    			}
    		}
    		logger.logTrace("FORM PARAMZZ:"+reqparams);

        }
        
        Enumeration<String> headers=request.getHeaderNames();
        while(headers.hasMoreElements()){
        	String header=headers.nextElement();
        	String val=request.getHeader(header);
        	logger.logTrace("HEADER:"+header+"="+val);
        	reqheaders.put(header, val);
        }
        
        Enumeration<String> params=request.getParameterNames();
        if(params==null)
        	return;
        
        while(params.hasMoreElements()){
        	String param=params.nextElement();
        	if(param==null)
        		continue;
        	
        	String val=request.getHeader(param);
        	logger.logTrace("PARAM:"+param+"="+val);
        	reqparams.put(param, val);

        }
        
	}
	
	protected void getFilesToSubmitForPreparedRequest(DragDropMimeHandlerRequest mr) throws IOException{
		if(mr.getDropID()==null || mr.getDropID().equals("")){
			throw new RuntimeException("dropId form submit param missing");
		}
		
		String[] files=folder.list();
		if(files==null)
			return;
		
		
		mr.setFiles(new ArrayList<DragDropMimeFile>());
		
		
		for(String fileName:files){
			File f=new File(folder,fileName);
			if(! f.isFile()){
				continue;
			}
			if(!fileName.startsWith(mr.getUser()+"."+mr.getDropID())){
				continue;
			}
			if(fileName.endsWith(".inf")){
				DragDropMimeFile mf=new DragDropMimeFile();
				
				
				
				//TODO: Check if file was selected with checkbox and documenttype was selected
				//TODO: set corrected filename and documenttype
				mr.getFiles().add(mf);
				
				int j=fileName.lastIndexOf(".inf");
				String dataFileName=fileName.substring(0,j)+".dat";
				mf.setFile(new File(folder,dataFileName));
				parseInfoFile(mf,new File(folder,fileName));
				mf.setFileName(mf.getPrepareParams().get("filename"));

				setSubmtFileDetails(mr,mf);
				
			}
		}
		
		
	}
	
	protected void setSubmtFileDetails(DragDropMimeHandlerRequest mr, DragDropMimeFile mf){
		//set doctype, selection & new filename from urlformat params fileparam_1=...
		//	 *  FORM PARAM:md5_1:5b9f16527d8d27f541cbb3fabf432eb6
		
		//search file in total params map based on mf hash
		String fileIndex=null;
		for(String key:mr.getRequestParams().keySet()){
			if(key.startsWith("md5_") && mr.getRequestParams().get(key).equals(mf.getHash())){
				fileIndex=key.substring(4);
				break;
			}
		}
		logger.logTrace("FILE_INDEX:"+fileIndex);
		if(fileIndex!=null){
			mf.setNewfileName( mr.getRequestParams().get(DragDropConstants.FORM_PROCESS_FILES_FILENAME+fileIndex));
			mf.setDocumentType(mr.getRequestParams().get(DragDropConstants.FORM_PROCESS_FILES_DOCTYPE+fileIndex));
			mf.setNote(mr.getRequestParams().get(DragDropConstants.FORM_PROCESS_FILES_NOTE+fileIndex));
			String checked=mr.getRequestParams().get(DragDropConstants.FORM_PROCESS_FILES_CHECK+fileIndex);
			mf.setChecked(checked.equalsIgnoreCase("checked") || checked.equalsIgnoreCase("on") || checked.equalsIgnoreCase("true"));
			logger.logTrace("+++"+mf.getNewfileName());
			logger.logTrace("+++"+mf.getDocumentType());
			logger.logTrace("+++"+mf.isChecked());
			
		}
		
	}
	private void parseInfoFile(DragDropMimeFile mf, File infoFile) throws IOException{
		InputStream in=new FileInputStream(infoFile);
		mf.setPrepareParams(new HashMap<String,String>());
		
   		ByteArrayOutputStream bout=new ByteArrayOutputStream();
		byte[] buff=new byte[10000];
		int bytesread=0;
		while((bytesread=in.read(buff))>0){
			bout.write(buff,0,bytesread);
		}
		in.close();
		
		String ss=new String(bout.toByteArray());
		if(ss.startsWith("{")){
			ss=ss.substring(1);
		}
		if(ss.endsWith("}")){
			ss=ss.substring(0,ss.length()-1);
		}
		
		String[] pairs=ss.split(",");

		for(String param:pairs){
			int ii=param.indexOf("=");
			if(ii<=0){
				continue;
			}
			String key=param.substring(0,ii).trim();
			String value=param.substring(ii+1).trim();
			if(value.endsWith("\"")){
				value=value.substring(0,value.lastIndexOf("\""));
			}
			if(value.startsWith("\"")){
				value=value.substring(1);
			}
			mf.getPrepareParams().put(key, value);
		}
		mf.setHash(mf.getPrepareParams().get("md5"));
		
	}

	
	
	/*
	 * Stream will start with ... (content-type can be different)
	 * note that every formdata.add will w-be new stream 
	 * 
------WebKitFormBoundaryBniKJXj8uBw05BXM
Content-Disposition: form-data; name="fileSize"

3528
------WebKitFormBoundaryBniKJXj8uBw05BXM
Content-Disposition: form-data; name="file"; filename="DragDropServlet.class"
Content-Type: application/octet-stream

	* ...
	* 
	* Stream will end with
	* 
	* ------WebKitFormBoundaryRBrfsAQYjBh5Rlfm--
	 * 
	 */
	protected File serialize(String user,String randomId, ServletInputStream instream,int fileUploadSpeed) throws IOException{
		
        byte[] buff=new byte[1024];
        int bytesread=0;
        int totalBytes=0;
        int totalBytes2=0;
        //ByteArrayOutputStream bout=new ByteArrayOutputStream();
        File f=new File(folder,user+"."+randomId+".upload.mime");
        FileOutputStream fout=new FileOutputStream(f);
        while((bytesread=instream.read(buff))>0){
        	totalBytes+=bytesread;
        	totalBytes2+=bytesread;
        	fout.write(buff,0,bytesread);
        	
        	//approximite timing ,assume stream reading/writing takes no time, just wait 1 second when limit per/sec is reached
        	if(fileUploadSpeed>0){
        		if(totalBytes2>fileUploadSpeed){
        			try {
        				System.out.println("Sleeping...");
						Thread.currentThread().sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
        			totalBytes2=0;
        		}
        	}
        }
        
        return f;
        
	}
	
	protected Map<String,String> getFilePartParams(String header) throws IOException{
		 Map<String,String>  map=new HashMap<String,String>();
		 
		 //header can have CRLF
		 BufferedReader br=new BufferedReader(new StringReader(header));
		 String line=null;
		 while( (line=br.readLine())!=null){
		 		for(String param:line.split(";")){
					int ii=param.indexOf("=");
					if(ii<=0){
						continue;//first param form-data has no = sign
					}
					String key=param.substring(0,ii).trim();
					String value=param.substring(ii+1).trim();
					if(value.endsWith("\"")){
						value=value.substring(0,value.lastIndexOf("\""));
					}
					if(value.startsWith("\"")){
						value=value.substring(1);
					}

					if(key.equalsIgnoreCase("name")){
						//special case: in javascript we've added the params as part of the filename,since no way to set other params in te form data
						//without creating a new form data item
						//name="FILEDATA,filesize=7253,md5=4a827628abf0efcf85f40330f5d5af71"
						String[] pairs=value.split(",");
						for(String pair:pairs){
							 ii=pair.indexOf("=");
							if(ii<=0){
								continue;
							}
							key=pair.substring(0,ii).trim();
							value=pair.substring(ii+1).trim();
							if(value.endsWith("\"")){
								value=value.substring(0,value.lastIndexOf("\""));
							}
							map.put(key, value);

						}
					}else{
						map.put(key, value);
					}
					
					
				}
			 
		 }
		 

		
		 return map;
	}
	protected String getHash(File f3) throws  IOException {
	    MessageDigest m = null;
	    try {
			m=MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	    byte[] buff=new byte[1024];
	    FileInputStream fin=new FileInputStream(f3);
	    int bytesread=0;
	    
	    while((bytesread=fin.read(buff))>0){
	    	m.update(buff,0,bytesread);
	    	
	    }
	    fin.close();
	    
	    byte[] d=m.digest();
	    String str= new String(Hex.encodeHex(d));
	    return str;
	    
	}
}
