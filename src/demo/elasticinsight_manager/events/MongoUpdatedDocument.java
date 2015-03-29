package demo.elasticinsight_manager.events;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

import demo.elasticinsight_manager.server.MongoServer;

public class MongoUpdatedDocument {

	protected String[] _db_collection;
	protected BasicDBObject _updating_id_query;
	
	@Inject
	protected MongoUpdatedDocument(@Assisted String[] db_collection,
			@Assisted BasicDBObject op_obj_id, @Assisted BasicDBObject op_update_info) {
		_db_collection = db_collection;
		_updating_id_query = op_obj_id;
	}

	public void async_execute() {
		
		// Get collection:
		DBCollection update_collection = null;
		
		//TODO: move this across to dependency injection
		Mongo driver = MongoServer.getDriver();
		if (1 == _db_collection.length) {
			update_collection = driver.getDB(MongoServer.getDefaultDbName()).getCollection(_db_collection[0]);
		}
		else {
			update_collection = driver.getDB(_db_collection[0]).getCollection(_db_collection[1]);
		}
		
		// Get the doc
		BasicDBObject obj_to_update = null;
		if (null != update_collection) {
			obj_to_update = (BasicDBObject) update_collection.findOne(_updating_id_query);
		}
		
		//TODO: in practice would need to check if the type had changed and copy across if so...
		
		// Now just run the same logic as for a new doc
		if (null != obj_to_update) {
			new MongoNewDocument(_db_collection, obj_to_update).async_execute();
		}
	}

}
