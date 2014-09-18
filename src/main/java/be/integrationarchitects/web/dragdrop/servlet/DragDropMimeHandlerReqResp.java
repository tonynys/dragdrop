package be.integrationarchitects.web.dragdrop.servlet;


public abstract class DragDropMimeHandlerReqResp {
	private DragDropContext ctx;
	
	
	

	public DragDropMimeHandlerReqResp(){
		
	}
	
	public void cloneMe(DragDropMimeHandlerReqResp r){
		this.ctx=r.ctx;
		//do not reference original files, response must set it's own files
		//this.files=r.files;
	}
	


	
	
	public DragDropContext getCtx() {
		return ctx;
	}

	public void setCtx(DragDropContext ctx) {
		this.ctx = ctx;
	}
	


}
