package demo.elasticinsight_manager.events;

import org.bson.types.ObjectId;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.mongodb.BasicDBObject;

import demo.elasticinsight_manager.server.MongoServer;
import demo.elasticinsight_manager.services.GraphDbService;
import demo.elasticinsight_manager.services.IndexingService;

public class MongoNewDocument {

	protected String[] _db_collection;
	protected BasicDBObject _inserted_obj;
	
	@Inject
	protected GraphDbService _graphDbService;
	
	@Inject 
	protected IndexingService _indexService;
	
	@Inject 
	protected MongoNewDocument(@Assisted String[] db_collection, @Assisted BasicDBObject inserted_obj) {
		_db_collection = db_collection;
		_inserted_obj = inserted_obj;
	}

	//TODO: make the second part of this asynchronous
	public void async_execute() {
		// Check if the object was generated via the ES synchronization logic
		//TODO: hmm what about if someone is generating "_id"s that aren't object ids...
		ObjectId _id = _inserted_obj.getObjectId("_id");
		if (1 == (_id.getMachine() & 0x1)) {
			//TODO: hmm how to enforce something is not true for most of them? Probably need to
			// mod the driver ugh...
			//TODO (^^this isn't really an issue now that we're limiting RW to the MongoDB side)
		}
		
		//TODO: is this is a new index?
		//TODO: build the index name (use MongoServer.getDefaultDbName if necessary)
		StringBuffer index_sb = new StringBuffer();
		if (1 == _db_collection.length) {
			index_sb.append(MongoServer.getDefaultDbName()).append('.').append(_db_collection[0]);
		}
		else {
			index_sb.append(_db_collection[0]).append('.').append(_db_collection[1]);			
		}
		String index_name = index_sb.toString();
		BasicDBObject index_stylesheet = _indexService.getStylesheet(index_name);
		
		if (null == index_stylesheet) {
			//new EsNewIndex(index_name).async_execute().wait();
			//TODO: handle new index
		}
		String type_name = index_stylesheet.getString("name", "unknown_type");
		
		//TODO: OK from here I think we can be async?
		
		//TODO: check if _type is specified, defaults to _notype if not
		//TODO: check stylesheet to see if it contains this type (if not then ???)
		//TODO: index object
		//TODO: on failure, if _notype specified then might need to create a new type (I think we get that error back?)
		//TODO: (longer term might want to compare object vs mappings up front? maybe as part of serialization?!)
		
		_indexService.indexObject(index_name, type_name, _inserted_obj);
		
		//TODO: only do this if graph analysis settings
		//TODO: in practice this needs things like index_name
		_graphDbService.addObjectGraphables(index_name, _inserted_obj);
	}
}
