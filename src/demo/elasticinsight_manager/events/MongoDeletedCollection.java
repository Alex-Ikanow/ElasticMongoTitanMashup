package demo.elasticinsight_manager.events;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.mongodb.BasicDBObject;

import demo.elasticinsight_manager.services.GraphDbService;
import demo.elasticinsight_manager.services.IndexingService;

public class MongoDeletedCollection {

	String _db_collection[];
	String _drop_collection;
	
	@Inject
	protected GraphDbService _graphDbService;
	
	@Inject 
	protected IndexingService _indexService;
	
	@Inject
	protected MongoDeletedCollection(@Assisted String[] db_collection,
			@Assisted BasicDBObject op_coll_cmd) {	
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
		_indexService.deleteIndex(index_name);	
		
		_graphDbService.deleteGraphFromCollection(index_sb.toString());
	}

}
