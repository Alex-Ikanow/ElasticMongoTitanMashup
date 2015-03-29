package demo.elasticinsight_manager.server;

import com.mongodb.BasicDBObject;
import com.mongodb.Bytes;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;

import demo.elasticinsight_manager.services.DatastoreService;

public class MongoServer implements DatastoreService {

	////////////////////////////////////////////////
	
	// Implementation

	protected MongoServer() {} // (can only be created by DI)
	
	protected static Mongo _driver;
	
	//TODO: remove these public static, move to Guice DI
	
	public static String getDefaultDbName() {
		//TODO: take this from a config file?
		return "elasticdata";
	}
	
	public static Mongo getDriver() {
		if (null == _driver) {
			try {
				_driver = new Mongo();
			}
			catch (Exception e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}
		return _driver;
	}
	
	// OP LOG SUPPORT
	
	protected DBCollection _op_log = null;
	protected final int options = Bytes.QUERYOPTION_TAILABLE | Bytes.QUERYOPTION_AWAITDATA | Bytes.QUERYOPTION_NOTIMEOUT | Bytes.QUERYOPTION_OPLOGREPLAY;	
	
	/* (non-Javadoc)
	 * @see demo.elasticinsight_manager.server.DatastoreService#getOperationsCursor(com.mongodb.BasicDBObject)
	 */
	@Override
	public DBCursor getOperationsCursor(BasicDBObject query) {
		if (null == _op_log) {
			_op_log = getDriver().getDB("local").getCollection("oplog.rs");			
		}		
		DBCursor dbc = _op_log.find(query).setOptions(options);
		return dbc;
	}
	
}
