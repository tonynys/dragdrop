
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.resource.FileResource;



/*
 * Starter Class for running  webapp inside eclipse without plugin, remote debug,...
 * .
 * The purpose is to efficiently develop/deploy without the need for creating war files,
 * copying to tomcat/jboss,... since we loose too much time, even when copying class files,...
 * we need to restart the container, and also, hot redeployment of classes is wanted without restart
 * 
 * The issue is that 
 *  - struts-config.xml is generated dynamically with xdoclet/ant
 *  - taglibs are generated with ant
 * Plugns for eclipse (jboss, tomcat,jetty) cannot handle this by default,
 
 *
 * for jsp settings : http://docs.codehaus.org/display/JETTY/Jsp+Configuration
 *  other system props: http://docs.codehaus.org/display/JETTY/SystemProperties
 * 
 * Note: jetty compiles by default jsps in the system temp folder (do SET on cmd line...)
 * eg. "C:\Documents and Settings\CORPID\Local Settings\Temp\Jetty___
 * 
 * @auth tnys
 */
/**
 * For runjetty/jsp add these jars to classpath
 * commons-logging-1.2.jar
el-api.jar
jasper-6.0.20.jar
jasper-el.jar
jasper-jdt-6.0.29.jar
jetty-6.1.25.jar
jetty-util-6.1.25.jar
jsp-2.1-jetty-6.1.16.jar
jsp-api.jar
servlet-api-2.5.jar
tomcat-6.0.36-tomcat-juli.jar
 * @author tony
 *
 */
public class RunJetty {

	
    private static final int PORT = 9080;
	private Server server;
    private String webappFolder = null;

    public void init() throws Exception {
        System.out.println("Starting Jetty...., tempdir:"+System.getProperty("java.io.tmpdir"));

        // 1. determin root folder
        URL mainURL = Thread.currentThread().getContextClassLoader().getResource("WEB-INF/web.xml");
        if (mainURL == null) {
            throw new IllegalArgumentException("main web.xml not found, make sure src/main/webapp is a class folder classpath resource");
        }
       
        //String NAME_FOLDER="DragDropServlet";
        String NAME_FOLDER="dragdrop";
        
        String _s = mainURL.toString();
        System.out.println(_s);
       // _s = StringUtils.replace(_s, "%20", " ");
        int i = _s.lastIndexOf(NAME_FOLDER);
        if (i <= 0) {
            throw new IllegalArgumentException("web.xml DragDropServlet not found");
        }
        webappFolder = _s.substring(0, i + NAME_FOLDER.length());
        System.out.println("Folder Webapp module:" + webappFolder);
     
        server = new Server(PORT);
        final Server server2=server;
        // server.setStopAtShutdown(true);

        WebAppContext context = new WebAppContext(server, "src/main/webapp", "/dragdrop");
        context.getInitParams().put("keepGenerated", "true"); // http://docs.codehaus.org/display/JETTY/Jsp+Configuration
       FileResource fileResource = new FileResource(new URL(webappFolder + "/bin"));
        context.setBaseResource(fileResource);

        
        Handler[] handlers=context.getChildHandlers();
        
        for(Handler h:handlers){
        	if(h instanceof ServletHandler){
        		ServletHandler sh=(ServletHandler) h;
     
        		
        	}
        }
//        
// 
        server.start();

        
        int uu=1;
    }

    public void tearDown() throws Exception {
        server.stop();

    }


    public static void main(final String[] args) throws Exception {
    	
    	//set jvm to force oracle client in american mode since clob from xmltype mixes "." and "," related to the regional (NLS_LANG) setting of the client
    	//Note: too late here, need to be -D parameter !!!
    	//System.setProperty("user.country", "US");
    	
    	
        RunJetty r = new RunJetty();
        try {
            r.init();
            System.out.println("=======================================================");
            System.out.println("Jetty started, press 'x<CRLF>' in the console to stop");
            System.out.println("connect with your browser to http://localhost:"+PORT+"/dragdrop");
            System.out.println("=======================================================");

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String line = null;

            do {

                line = in.readLine();

            } while (line != null && !"x".equals(line));
            System.out.println("Stopping Jetty...");
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            r.tearDown();
        }
    }
}

