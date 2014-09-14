package be.integrationarchitects.web.dragdrop.servlet;

public enum ResponseContentEnum {
	HTML("text/html"),JSON("application/json");
	
	public String getMimeType() {
		return mimeType;
	}

	private String mimeType;
	
	ResponseContentEnum(String mimeType){
		this.mimeType=mimeType;
	 }
}
