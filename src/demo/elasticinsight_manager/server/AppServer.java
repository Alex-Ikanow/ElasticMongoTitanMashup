package demo.elasticinsight_manager.server;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;
import org.restlet.routing.Template;

import com.google.inject.Inject;

import demo.elasticinsight_manager.actions.EsProxyInterface;
import demo.elasticinsight_manager.threads.EsIndexMappingMonitor;
import demo.elasticinsight_manager.threads.MongoOpLogMonitor;

public class AppServer extends Application {
	
	////////////////////////////////////////////////
	
	// Debug
	
	//DEBUG
	public static boolean DEBUG = true;
	
	////////////////////////////////////////////////
	
	// Injections
	
	@Inject
	protected EsIndexMappingMonitor _mappingMonitor;

	@Inject
	protected MongoOpLogMonitor _databaseMonitor;	
	
	////////////////////////////////////////////////
	
	// Implementation
	
	protected AppServer() {} // (can only be created by DI)
	
    @Override  
    public Restlet createRoot() {
    	// Setup threads
    	_mappingMonitor.start();
    	_databaseMonitor.start();
    	
    	// Setup interfaces:
        Router router = new Router(getContext());
        router.attach("/{proxyterms}", EsProxyInterface.class).setMatchingMode(Template.MODE_STARTS_WITH);
        return router;  
    }    
}
