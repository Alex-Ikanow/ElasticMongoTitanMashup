package demo.elasticinsight_manager.services;

import com.mongodb.BasicDBObject;

public interface GraphDbService {
	void deleteGraphFromCollection(String index_name);
	void addObjectGraphables(String index_name, BasicDBObject obj);	
}
