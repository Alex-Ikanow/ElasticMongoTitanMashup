package demo.elasticinsight_manager.server;

import org.mockito.Mockito;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

import demo.elasticinsight_manager.services.DatastoreService;

public class MockMongoServer implements DatastoreService {

	protected DBCursor _op_log_cursor = null;
	
	@Override
	public DBCursor getOperationsCursor(BasicDBObject query) {
		if (null == _op_log_cursor) {
			_op_log_cursor = Mockito.mock(DBCursor.class);
			Mockito.when(_op_log_cursor.hasNext()).thenReturn(false);
			Mockito.when(_op_log_cursor.getCursorId()).thenReturn(0L);
		}
		
		System.out.println("getOperationsCursor: " + query);
		return _op_log_cursor;
	}

}
