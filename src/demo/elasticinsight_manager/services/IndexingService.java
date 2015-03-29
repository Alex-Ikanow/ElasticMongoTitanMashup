package demo.elasticinsight_manager.services;

import com.mongodb.BasicDBObject;

public interface IndexingService {

	public abstract BasicDBObject getStylesheet(String index_name);

	public abstract void deleteIndex(String index_name);

	public abstract void deleteObjectById(String index_name, String type_name,
			Object id);

	public abstract void indexObject(String index_name, String type_name,
			BasicDBObject obj);

}