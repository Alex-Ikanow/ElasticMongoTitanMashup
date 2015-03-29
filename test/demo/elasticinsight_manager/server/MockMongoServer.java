package demo.elasticinsight_manager.server;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

import demo.elasticinsight_manager.services.DatastoreService;

public class MockMongoServer implements DatastoreService {

	@Override
	public DBCursor getOperationsCursor(BasicDBObject query) {
		// TODO 
		System.out.println("getOperationsCursor: " + query);
		return null;
	}

}
