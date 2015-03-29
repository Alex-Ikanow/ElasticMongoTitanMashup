package demo.elasticinsight_manager.server;

import java.util.Date;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.google.inject.Singleton;
import com.mongodb.BasicDBObject;

import demo.elasticinsight_manager.services.IndexingService;

@Singleton
public class EsServer implements IndexingService {

	////////////////////////////////////////////////
	
	// Implementation

	protected EsServer() {} // (can only be created by DI)
	
	static protected Client _client = null;
	static protected void createClientIfNeeded() {
		if (null == _client) {
			_client = new TransportClient().addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		}		
	}
	
	/* (non-Javadoc)
	 * @see demo.elasticinsight_manager.server.IndexingService#getStylesheet(java.lang.String)
	 */
	@Override
	public synchronized BasicDBObject getStylesheet(String index_name) {
		//TODO return a pojo in fact? or a Map?
		return new BasicDBObject("name", "type1"); //TODO: will just treat every object the same
	}

	/* (non-Javadoc)
	 * @see demo.elasticinsight_manager.server.IndexingService#deleteIndex(java.lang.String)
	 */
	@Override
	public void deleteIndex(String index_name) {
		createClientIfNeeded();
		//org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse delete = 
		_client.admin().indices().delete(new DeleteIndexRequest(index_name)).actionGet();
	}

	/* (non-Javadoc)
	 * @see demo.elasticinsight_manager.server.IndexingService#deleteObjectById(java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public void deleteObjectById(String index_name, String type_name, Object id) {
		createClientIfNeeded();		
		_client.prepareDelete(index_name, type_name, id.toString()).execute().actionGet();
	}
	
	/* (non-Javadoc)
	 * @see demo.elasticinsight_manager.server.IndexingService#indexObject(java.lang.String, java.lang.String, com.mongodb.BasicDBObject)
	 */
	@Override
	public void indexObject(String index_name, String type_name, BasicDBObject obj) {
		createClientIfNeeded();
		
		// Handle _id - ObjectId is not parsed by elasticsearch:
		//TODO: in practice there's a bunch of objects that aren't handled in a standard way,
		// need a custom deserializer
		Object id = obj.get("_id");
		if (null == id) {
			return;
		}
		String id_str = id.toString();
		if (!(id instanceof String)) {			
			obj.put("_id", id_str);
		}
		//TODO: logstash special case/workaround:
		Object ts = obj.get("@timestamp");
		if (null != ts) {
			if (ts instanceof Date) {
				obj.put("@timestamp", ((Date)ts).getTime());
			}
			else {
				obj.put("@timestamp", ts.toString());
			}
		}
		
		//org.elasticsearch.action.index.IndexResponse response = 
		_client.prepareIndex(index_name, type_name)
				.setId(id_str)
		        .setSource(obj)
		        .execute()
		        .actionGet();
	}	
}
