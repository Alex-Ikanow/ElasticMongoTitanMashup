package demo.elasticinsight_manager.services;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

import demo.elasticinsight_manager.server.TitanServer;

public class ProductionServiceInjection implements Module {

	@Override
	public void configure(Binder binder) {
		binder.bind(GraphDbService.class).to(TitanServer.class).in(Scopes.SINGLETON);
	}

}
