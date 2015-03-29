package demo.elasticinsight_manager.main;

import org.restlet.Component;
import org.restlet.data.Protocol;

import com.google.inject.Guice;
import com.google.inject.Injector;

import demo.elasticinsight_manager.server.AppServer;

public class ApplicationMain {

	public static void main(String[] args) throws Exception {
		
		Injector serverInjector = Guice.createInjector(new ProductionServiceInjection());		
		AppServer appServer = serverInjector.getInstance(AppServer.class);
		
		Component component = new Component();

		int port = 9920;
		component.getServers().add(Protocol.HTTP, port);
		
        // Attach the sample application.  
        component.getDefaultHost().attach(appServer);  
          
        //DEBUG
        if (AppServer.DEBUG) System.out.println("AppServer: Starting AppServer on port=" + port);
        
        // Start the component.  
        component.start();
	}
}
