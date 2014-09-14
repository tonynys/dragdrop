<%@ page import="java.util.*" %>
<%@ page import=" be.integrationarchitects.web.dragdrop.servlet.*" %>
<%--
Do not show stacktrace of 500 errors on client, only simple message, and use logger to 
log error internally
--%>
<%

		Throwable throwable = (Throwable) request
                .getAttribute("javax.servlet.error.exception");
        Integer statusCode = (Integer) request
                .getAttribute("javax.servlet.error.status_code");
        String servletName = (String) request
                .getAttribute("javax.servlet.error.servlet_name");
                
                
                System.out.println("ERR...."+throwable.getMessage());
 				DragDropServletConfig cfg=null;
  				cfg=(DragDropServletConfig)request.getSession().getServletContext().getAttribute("mycfg");
				cfg.getLogger().logError(throwable.getMessage(),throwable);             
   %>
Server 500 error:<%=throwable.getMessage()%>
