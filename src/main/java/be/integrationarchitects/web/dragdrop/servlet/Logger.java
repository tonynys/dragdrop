package be.integrationarchitects.web.dragdrop.servlet;

public interface Logger{
	public void logTrace(String msg);//to avoid dependency on log framework
	public void logDebug(String msg);//to avoid dependency on log framework
	public void logError(String msg);
	public void logError(String msg, Throwable e);

}
