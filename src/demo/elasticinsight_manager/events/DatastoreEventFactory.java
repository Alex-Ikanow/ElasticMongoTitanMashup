package demo.elasticinsight_manager.events;

import com.google.inject.assistedinject.Assisted;
import com.mongodb.BasicDBObject;

public interface DatastoreEventFactory {

	MongoDeletedCollection onDeletedCollection(String[] db_collection, BasicDBObject op_coll_cmd);
	MongoDeletedDocument onDeletedDocument(String[] db_collection, BasicDBObject op_obj_id);
	MongoNewDocument onNewDocument(String[] db_collection, BasicDBObject inserted_obj);
	MongoStylesheetChanged onStylesheetChanged();
	MongoUpdatedDocument onUpdatedDocument(@Assisted("db_collection") String[] db_collection, @Assisted("op_obj_id") BasicDBObject op_obj_id, @Assisted("op_update_info") BasicDBObject op_update_info);
}
