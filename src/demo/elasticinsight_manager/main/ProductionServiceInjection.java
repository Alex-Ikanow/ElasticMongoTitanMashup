package demo.elasticinsight_manager.main;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import demo.elasticinsight_manager.events.DatastoreEventFactory;
import demo.elasticinsight_manager.events.EsDeletedDocument;
import demo.elasticinsight_manager.events.EsMappingChanged;
import demo.elasticinsight_manager.events.EsNewDocument;
import demo.elasticinsight_manager.events.EsNewIndex;
import demo.elasticinsight_manager.events.IndexEventFactory;
import demo.elasticinsight_manager.events.MongoDeletedCollection;
import demo.elasticinsight_manager.events.MongoDeletedDocument;
import demo.elasticinsight_manager.events.MongoNewDocument;
import demo.elasticinsight_manager.events.MongoStylesheetChanged;
import demo.elasticinsight_manager.events.MongoUpdatedDocument;
import demo.elasticinsight_manager.server.EsServer;
import demo.elasticinsight_manager.server.MongoServer;
import demo.elasticinsight_manager.server.TitanServer;
import demo.elasticinsight_manager.services.DatastoreService;
import demo.elasticinsight_manager.services.GraphDbService;
import demo.elasticinsight_manager.services.IndexingService;

public class ProductionServiceInjection extends AbstractModule {

	@Override
	protected void configure() {
		this.bind(GraphDbService.class).to(TitanServer.class).in(Scopes.SINGLETON);
		this.bind(IndexingService.class).to(EsServer.class).in(Scopes.SINGLETON);
		this.bind(DatastoreService.class).to(MongoServer.class).in(Scopes.SINGLETON);
		
		// Factories:
		this.install(new FactoryModuleBuilder()
				.implement(EsDeletedDocument.class, EsDeletedDocument.class)
				.implement(EsMappingChanged.class, EsMappingChanged.class)
				.implement(EsNewDocument.class, EsNewDocument.class)
				.implement(EsNewIndex.class, EsNewIndex.class)
				.build(IndexEventFactory.class)
		);
		this.install(new FactoryModuleBuilder()
				.implement(MongoDeletedCollection.class, MongoDeletedCollection.class)
				.implement(MongoDeletedDocument.class, MongoDeletedDocument.class)
				.implement(MongoNewDocument.class, MongoNewDocument.class)
				.implement(MongoStylesheetChanged.class, MongoStylesheetChanged.class)
				.implement(MongoUpdatedDocument.class, MongoUpdatedDocument.class)
				.build(DatastoreEventFactory.class)
		);
	}

}
