package demo.elasticinsight_manager.events;

import com.mongodb.BasicDBObject;

public interface DatastoreEventFactory {

	MongoDeletedCollection onDeletedCollection(String[] db_collection, BasicDBObject op_coll_cmd);
	MongoDeletedDocument onDeletedDocument(String[] db_collection, BasicDBObject op_obj_id);
	MongoNewDocument onNewDocument(String[] db_collection, BasicDBObject inserted_obj);
	MongoStylesheetChanged onStylesheetChanged();
	MongoUpdatedDocument onUpdatedDocument(String[] db_collection, BasicDBObject op_obj_id, BasicDBObject op_update_info);
}
