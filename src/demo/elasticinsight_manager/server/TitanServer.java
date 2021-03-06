package demo.elasticinsight_manager.server;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.thinkaurelius.titan.core.PropertyKey;
import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanTransaction;
import com.thinkaurelius.titan.core.schema.ConsistencyModifier;
import com.thinkaurelius.titan.core.schema.TitanGraphIndex;
import com.thinkaurelius.titan.core.schema.TitanManagement;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.ElementHelper;

public class TitanServer {

	protected static TitanGraph _graph = null;
	protected static long _last_graph_update = 0L; 
	public static final long TITAN_TIMEOUT_MS = 30000L;
	
	static class TitanMonitorThread extends Thread {
		public void run() {
			for (;;) {
				synchronized (_monitor_thread) {
					long now = new Date().getTime();
					if ((0 != _last_graph_update) && 
							((now - _last_graph_update) > TITAN_TIMEOUT_MS))
					{
						if (null != _graph) {
							//DEBUG
							System.out.println("(released global Titan lock)");
							
							try {
								_graph.shutdown();
							}
							catch (Exception e) {}
							_graph = null;
						}
					}
				}
				try { Thread.sleep(10000L); } catch (Exception e) {};
			}
		}
	}
	protected static TitanMonitorThread _monitor_thread = new TitanMonitorThread();
	protected static boolean _monitor_thread_running = false;

	public static void deleteGraphFromCollection(String index_name) {
		synchronized (_monitor_thread) {
			createGraphIfNeeded();
			
			TitanTransaction tx = _graph.newTransaction();
			Iterable<Vertex> l_v = tx.getVertices("bucket", index_name);
			if (null != l_v) for (Vertex v: l_v) {
				v.remove();
			}
			tx.commit();
		}		
	}
	
	public static void addObjectGraphables(String index_name, BasicDBObject obj) {
		BasicDBObject annotations = (BasicDBObject) obj.get("_@");
		if (null == annotations) {
			//TODO: in practice this should check the schema etc etx
			return; // (nothing to graph)
		}
		_last_graph_update = new Date().getTime();
		
		synchronized (_monitor_thread) {
			createGraphIfNeeded();
			
			//TODO: this is very demo code, limitations that need to be fixed include:
			// - indexes need to be name/type/bucket
			// - need a caching layer for vertexes to avoid hitting the DB each time
			// - general in-code efficiency
			
			HashMap<String, Vertex> saved_curr_vertices = new HashMap<String, Vertex>();
			HashMap<String, Vertex> saved_new_vertices = new HashMap<String, Vertex>();
			BasicDBObject vertices = (BasicDBObject) annotations.get("e");
			if (null != vertices) {
				TitanTransaction tx = _graph.newTransaction();
				
				for (String type: vertices.keySet()) {
					BasicDBList names = (BasicDBList) vertices.get(type);
					if (null != names) for (Object name_o: names) {
						String name = name_o.toString();								
						Iterable<Vertex> l_v = tx.getVertices("name", name.toString());
						boolean found = false;
						if (null != l_v) {
							Iterator<Vertex> it_v = l_v.iterator();
							if (it_v.hasNext()) {
								saved_curr_vertices.put(name, it_v.next());
								found = true;
							}
						}
						if (!found) {
							Vertex new_vertex = tx.addVertexWithLabel(name);
							ElementHelper.setProperties(new_vertex, "name", name, "type", type, "bucket", index_name);
							
							saved_new_vertices.put(name, new_vertex);
						}
					}//(end loop over names)
				}//(end loop over types/vertices)
				
				// OK if there are vertices then going to look for edges also:

				//TODO: IDEA: (handle the case where if 'o' is not a vertex then attach the text as an attribute instead?)
			
				BasicDBList assocs = (BasicDBList) annotations.get("a");
				if (null != assocs) for (Object assoc_o: assocs) {
					boolean must_be_new_link = false;
					BasicDBObject assoc = (BasicDBObject) assoc_o;
					String o = assoc.getString("o", null);
					String s = assoc.getString("s", null);
					String v = assoc.getString("v", null);
					if ((null == o) || (null == v) || (null == s)) {
						continue;
					}
					Vertex vertex_s = saved_curr_vertices.get(s);
					if (null == vertex_s) {
						must_be_new_link = true;
						vertex_s = saved_new_vertices.get(s);
						if (null == vertex_s) continue;								
					}
					Vertex vertex_o = saved_curr_vertices.get(o);
					if (null == vertex_o) {
						must_be_new_link = true;
						vertex_o = saved_new_vertices.get(o);
						if (null == vertex_o) continue;								
					}
					if (!must_be_new_link) { // need to check if the edge already exists
						must_be_new_link = true;
																	
						Iterable<Edge> l_e = vertex_s.getEdges(Direction.OUT, v);
						
						if (null != l_e) for (Edge edge: l_e) {
							Vertex other_end = edge.getVertex(Direction.IN);							
							if (other_end.getId().equals(vertex_o.getId())) {
								must_be_new_link = false;
								break;
							}
						}
					}
					
					if (must_be_new_link) {
						vertex_s.addEdge(v, vertex_o); 
					}
				}//(end loop over edges)
				
				tx.commit();
				
			}//(end if there are vertices)
		}		
	}
	
	protected static void createGraphIfNeeded() {
		if (!_monitor_thread_running) {
			_monitor_thread_running = true;
			_monitor_thread.start();
		}
		if (null == _graph) {
			//TODO: (temp graph name for demo purposeS)
			_graph = create("/tmp/berkeley-insight");
		}
	}
	
	protected static TitanGraph create(final String directory) {
        final String INDEX_NAME = "search";	
    	
        TitanFactory.Builder config = TitanFactory.build();
        config.set("storage.backend", "berkeleyje");
        config.set("storage.directory", directory);
        config.set("index."+INDEX_NAME+".backend","elasticsearch");
        config.set("index."+INDEX_NAME+".hostname","127.0.0.1");
        config.set("index."+INDEX_NAME+".elasticsearch.client-only",true);
        
        TitanGraph graph = config.open();
        
        // Set up indexes:
        try {
	        TitanManagement mgmt = graph.getManagementSystem();
	        final PropertyKey name = mgmt.makePropertyKey("name").dataType(String.class).make();
	        //final PropertyKey type = mgmt.makePropertyKey("type").dataType(String.class).make();
	        final PropertyKey bucket = mgmt.makePropertyKey("bucket").dataType(String.class).make();
	        //TODO (this wants to be name+index(+bucket?))
	        TitanGraphIndex namei = mgmt.buildIndex("name",Vertex.class).addKey(name).unique().buildCompositeIndex();
	        mgmt.setConsistency(namei, ConsistencyModifier.LOCK);
	        @SuppressWarnings("unused")
			TitanGraphIndex bucketi = mgmt.buildIndex("bucket",Vertex.class).addKey(bucket).buildCompositeIndex();
	        //TODO: need to build edge properties and indexes, example:
	        //final PropertyKey time = mgmt.makePropertyKey("time").dataType(Integer.class).make();
	        //final PropertyKey reason = mgmt.makePropertyKey("reason").dataType(String.class).make();
	        //final PropertyKey place = mgmt.makePropertyKey("place").dataType(Geoshape.class).make();
	        //TitanGraphIndex eindex = mgmt.buildIndex("reason", Edge.class).addKey(reason).buildMixedIndex(INDEX_NAME);
	        mgmt.commit();
        }
        catch (Exception e) {
        	// (this just means that it's already been built)
        }
        //TEST
        //com.thinkaurelius.titan.example.GraphOfTheGodsFactory.load(graph);
        return graph;
    }

    // Example
	public static void main(String[] args) throws Exception {
		System.out.println("Creating graph:");
		TitanGraph g = create("/tmp/berkeley-insight/");
		System.out.println("Created graph:");
		g.shutdown();
		System.out.println("Shutting down:");
	}    
}
