package demo.elasticinsight_manager.services;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

public interface DatastoreService {

	public abstract DBCursor getOperationsCursor(BasicDBObject query);

}