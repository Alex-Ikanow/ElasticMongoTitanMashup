package demo.elasticinsight_manager.server;

import com.mongodb.BasicDBObject;

import demo.elasticinsight_manager.services.IndexingService;

public class MockEsServer implements IndexingService {

	@Override
	public BasicDBObject getStylesheet(String index_name) {
		// TODO 
		System.out.println("getStylesheet: " + index_name);
		return null;
	}

	@Override
	public void deleteIndex(String index_name) {
		// TODO 
		System.out.println("deleteIndex: " + index_name);
	}

	@Override
	public void deleteObjectById(String index_name, String type_name, Object id) {
		// TODO 
		System.out.println("deleteObjectById: " + index_name + " , " + type_name + " , " + id);
	}

	@Override
	public void indexObject(String index_name, String type_name,
			BasicDBObject obj) {
		// TODO 
		System.out.println("deleteObjectById: " + index_name + " , " + type_name + " , " + obj);
	}

}
