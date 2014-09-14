package be.integrationarchitects.web.dragdrop.servlet;


public abstract class DragDropMimeHandlerReqResp {
	private String dropID;//random ID
	private String user;
	
	
	
	
	public DragDropMimeHandlerReqResp(){
		
	}
	
	public void cloneMe(DragDropMimeHandlerReqResp r){
		this.dropID=r.dropID;
		this.user=r.user;
		//do not reference original files, response must set it's own files
		//this.files=r.files;
	}
	
	public String getDropID() {
		return dropID;
	}
	public void setDropID(String dropID) {
		this.dropID = dropID;
	}
	

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}


	
	


}
