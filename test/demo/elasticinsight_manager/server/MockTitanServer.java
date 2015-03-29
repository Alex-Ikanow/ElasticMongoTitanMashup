package demo.elasticinsight_manager.server;

import com.mongodb.BasicDBObject;

import demo.elasticinsight_manager.services.GraphDbService;

public class MockTitanServer implements GraphDbService {

	@Override
	public void deleteGraphFromCollection(String index_name) {
		// TODO
		System.out.println("deleteGraphFromCollection: " + index_name);
	}

	@Override
	public void addObjectGraphables(String index_name, BasicDBObject obj) {
		// TODO
		System.out.println("addObjectGraphables: " + index_name + " , " + obj);
	}

}
