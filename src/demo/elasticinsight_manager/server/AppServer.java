package demo.elasticinsight_manager.server;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;
import org.restlet.routing.Template;

import demo.elasticinsight_manager.actions.EsProxyInterface;
import demo.elasticinsight_manager.threads.EsIndexMappingMonitor;
import demo.elasticinsight_manager.threads.MongoOpLogMonitor;

public class AppServer extends Application {
	
	//DEBUG
	public static boolean DEBUG = true;
	
    @Override  
    public Restlet createRoot() {
    	// Setup threads
    	new EsIndexMappingMonitor();
    	new MongoOpLogMonitor();
    	
    	// Setup interfaces:
        Router router = new Router(getContext());
        router.attach("/{proxyterms}", EsProxyInterface.class).setMatchingMode(Template.MODE_STARTS_WITH);
        return router;  
    }
    
	public static void main(String[] args) throws Exception {
		
		Component component = new Component();

		int port = 9920;
		component.getServers().add(Protocol.HTTP, port);
		
        // Attach the sample application.  
        component.getDefaultHost().attach(new AppServer());  
          
        //DEBUG
        if (AppServer.DEBUG) System.out.println("AppServer: Starting AppServer on port=" + port);
        
        // Start the component.  
        component.start();
	}

}
