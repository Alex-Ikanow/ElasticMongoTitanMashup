package demo.elasticinsight_manager.server;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;

public class TestTitanServer {

	public TitanServer _graph = new TitanServer();	
	
	@Before
	public void setupTitan() {
		// Set Titan into test mode
		TitanServer.setTestMode(true);
		
		// Delete the existing test DB
		try { FileUtils.forceDelete(new File(TitanServer._TEST_PATH)); } catch (Exception e) {}
		
		//TODO: there seems to be some other state where the config is held
		// currently when switching between the test/non-test mode (though bizarrely
		// main-test/non-test mode is fine?!) I get an error telling me I can't override the
		// es configruation - but it works when I delete _OPERATION_PATH so that must be cached somewhere?!
	}
	
	//TODO JUnit testing

	@Test 
	public void basicInsert() {
		
		BasicDBObject docWithEntsAndAssocs = 
			(BasicDBObject) BasicDBObjectBuilder.start()
				.add("_@"
					,BasicDBObjectBuilder.start()	
						.add("e", 
							BasicDBObjectBuilder.start()
								.add("type1", 
									Arrays.asList("node1_type1", "node2_type1")
									)
								.add("type2",
									Arrays.asList("node1_type2", "node2_type2")
									)
								.get()
							)
						.add("a", 
								Arrays.asList(
									BasicDBObjectBuilder.start()
										.add("s", "node1_type1")
										.add("o", "node2_type1")
										.add("v", "type1_assoc")
										.get(),
									BasicDBObjectBuilder.start()
										.add("s", "node1_type2")
										.add("o", "node2_type2")
										.add("v", "type2_assoc")
										.get()
									)
								)
						.get()
				).get();
		
		_graph.addObjectGraphables("bucket1", docWithEntsAndAssocs);
		
		//TODO: ugh need to build up some graph db string 
		//String graph_info = TitanServer._graph.XXX;
		String graph_info = "TBD";
		
		assertEquals("Graph should look like this", "", graph_info);
	}
	
	//TODO: delete data from one bucket, check the other bucket is still there
	
	//TODO: check invalid associations (null == o/s/v) are ignored but don't preclude adding valid ones
	
	//TODO: associations with old and new nodes
	
	//TODO: at the end, check if TitanServer._graph is null after 30 seconds
}
