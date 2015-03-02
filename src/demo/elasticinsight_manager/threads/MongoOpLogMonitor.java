package demo.elasticinsight_manager.threads;

import java.util.Date;

import org.bson.types.BSONTimestamp;

import com.mongodb.BasicDBObject;
import com.mongodb.Bytes;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import demo.elasticinsight_manager.events.MongoDeletedCollection;
import demo.elasticinsight_manager.events.MongoDeletedDocument;
import demo.elasticinsight_manager.events.MongoNewDocument;
import demo.elasticinsight_manager.events.MongoUpdatedDocument;
import demo.elasticinsight_manager.server.MongoServer;

public class MongoOpLogMonitor implements Runnable {

	public static boolean DEBUG = true;
	
	protected Thread _thread;
	public MongoOpLogMonitor() {
		_thread = new Thread(this);
		_thread.start();
	}
	protected long WAIT_TIME_MS = 1000L;
	
	@Override
	public void run() {
		try {
			DBCollection oplog = MongoServer.getDriver().getDB("local").getCollection("oplog.rs");
			
			DBCursor dbc = null;
			BasicDBObject op_dbo = null;
			BSONTimestamp start_ts = new BSONTimestamp((int)(new Date().getTime()/1000L), 0);

			int options = Bytes.QUERYOPTION_TAILABLE | Bytes.QUERYOPTION_AWAITDATA | Bytes.QUERYOPTION_NOTIMEOUT | Bytes.QUERYOPTION_OPLOGREPLAY;
			
			for (;;) {
				if ((null == dbc) || (0 == dbc.getCursorId())) { // Need to create a new tailable cursor on MongoDB oplog
					if (null != op_dbo) { // take the TS of the last element processed:
						Object ts = op_dbo.get("ts");
						if (ts instanceof BSONTimestamp) {
							start_ts = (BSONTimestamp)ts;
						}
					}
					BasicDBObject query = new BasicDBObject("ts", new BasicDBObject("$gte", start_ts));
					
					//DEBUG
					if (MongoOpLogMonitor.DEBUG) System.out.println("MongoOpLogMonitor: (Re)Starting oplog monitor from=" + start_ts);
					
					dbc = oplog.find(query).setOptions(options);
				}
				
				boolean found = false;
				while (dbc.hasNext()) {
					try {
						found = true;
						op_dbo = (BasicDBObject) dbc.next();
	
						//DEBUG
						if (MongoOpLogMonitor.DEBUG) System.out.println("MongoOpLogMonitor: oplog=" + op_dbo.toString());
						
						String op_type = op_dbo.getString("op", "?");
						String namespace = op_dbo.getString("ns", "?");
	
						// Any non-trivial namespace:
						//TODO (other special db names specific to this app)
						if (!namespace.startsWith("admin.") && !namespace.startsWith("config.") && 
								!namespace.startsWith("local.") && !namespace.equals("?"))
						{
							String[] db_collection = namespace.split("[.]", 2);
							
							//TODO: did I ever figure out how to map db/coll -> index/mapping?
							//TODO: (including object type mismatches ... do db.coll->index, mapping is some transient thing that can change)
							//TODO: for updates, how does synchronization work? (Always grab from primary?)
							
							char op_type_code = op_type.charAt(0);
							if ('i' == op_type_code) { // Insert "o"
								//TODO: don't forget to check if this is _created_ by es, in which case just bypass
								//TODO: ^^^ (current incarnation will key off MongoDB, so ignore this comment for now)
								
								BasicDBObject op_obj = (BasicDBObject) op_dbo.get("o");
								new MongoNewDocument(db_collection, op_obj).async_execute();
							}
							else if ('u' == op_type_code) { // Update obj id'd by "o", update command == "o2"
								BasicDBObject op_obj_id = (BasicDBObject) op_dbo.get("o2");
								BasicDBObject op_update_cmd = (BasicDBObject) op_dbo.get("o");
								new MongoUpdatedDocument(db_collection, op_obj_id, op_update_cmd).async_execute();
							}
							else if ('c' == op_type_code) { // Command ({ drop: "collection_to_drop" })
								BasicDBObject op_coll_cmd = (BasicDBObject) op_dbo.get("o");
								if (op_coll_cmd.containsField("drop")) {
									new MongoDeletedCollection(db_collection, op_coll_cmd).async_execute();
								}
							}
							else if ('d' == op_type_code) { // Delete aka Remove obj id'd by "o"
								BasicDBObject op_obj_id = (BasicDBObject) op_dbo.get("o");
								new MongoDeletedDocument(db_collection, op_obj_id).async_execute();
							}
							//(ignore other operation types)
						}
						
						//TODO: Handle
						// - updates to the stylesheets
					}
					catch (Exception e) {
						//DEBUG
						if (MongoOpLogMonitor.DEBUG) System.out.println("ERROR: ");
						if (MongoOpLogMonitor.DEBUG) e.printStackTrace();
					}
				}
				if (!found) { // (don't ever seem to reach here)
					try { Thread.sleep(WAIT_TIME_MS); } catch (Exception e) { }
				}
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
