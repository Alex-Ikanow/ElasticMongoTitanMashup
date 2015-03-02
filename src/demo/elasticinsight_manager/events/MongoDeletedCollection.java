package demo.elasticinsight_manager.events;

import com.mongodb.BasicDBObject;

import demo.elasticinsight_manager.server.EsServer;
import demo.elasticinsight_manager.server.TitanServer;

public class MongoDeletedCollection {

	String _db_collection[];
	String _drop_collection;
	
	public MongoDeletedCollection(String[] db_collection,
			BasicDBObject op_coll_cmd) {	
		_db_collection = db_collection;
		_drop_collection = op_coll_cmd.getString("drop", null);
	}

	public void async_execute() {
		StringBuffer index_sb = new StringBuffer();
		if (null == _drop_collection) {
			return; // (ill formed - nothing to do)
		}
		index_sb.append(_db_collection[0]).append('.').append(_drop_collection);			

		String index_name = index_sb.toString();
		EsServer.deleteIndex(index_name);	
		
		TitanServer.deleteGraphFromCollection(index_sb.toString());
	}

}
