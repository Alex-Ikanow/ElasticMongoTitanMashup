package demo.elasticinsight_manager.events;

import com.google.inject.Inject;
import com.mongodb.BasicDBObject;

import demo.elasticinsight_manager.server.EsServer;
import demo.elasticinsight_manager.services.GraphDbService;

public class MongoDeletedCollection {

	String _db_collection[];
	String _drop_collection;
	
	@Inject
	protected GraphDbService _graphDbService;
	
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
		
		_graphDbService.deleteGraphFromCollection(index_sb.toString());
	}

}
