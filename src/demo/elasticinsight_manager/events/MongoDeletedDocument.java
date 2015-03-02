package demo.elasticinsight_manager.events;

import com.mongodb.BasicDBObject;

import demo.elasticinsight_manager.server.EsServer;
import demo.elasticinsight_manager.server.MongoServer;

public class MongoDeletedDocument {

	String[] _db_collection;
	BasicDBObject _deleting_id_query;
	
	public MongoDeletedDocument(String[] db_collection, BasicDBObject op_obj_id) {
		_db_collection = db_collection;
		_deleting_id_query = op_obj_id;
	}

	public void async_execute() {
		StringBuffer index_sb = new StringBuffer();
		if (1 == _db_collection.length) {
			index_sb.append(MongoServer.getDefaultDbName()).append('.').append(_db_collection[0]);
		}
		else {
			index_sb.append(_db_collection[0]).append('.').append(_db_collection[1]);			
		}
		String index_name = index_sb.toString();
		BasicDBObject index_stylesheet = EsServer.getStylesheet(index_name);
		
		if (null == index_stylesheet) {
			//TODO: this is some sort of internal error because we now don't know the object's type
		}
		String type_name = index_stylesheet.getString("name", "unknown_type");
		
		Object id = _deleting_id_query.get("_id");
		if (null == id) {
			return; // nothing to be done
		}
		EsServer.deleteObjectById(index_name, type_name, id);
		//TODO: Titan is slightly more complex - ignore that for now
	}

}
