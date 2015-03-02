package demo.elasticinsight_manager.actions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Options;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

public class EsProxyInterface extends ServerResource {

	public static boolean DEBUG = true; 
	
	//___________________________________________________________________________________
	
	// Per-call state
	
	protected String _proxy_url;
	protected String _index_or_admin_cmd;
	protected String _index_cmd;
	protected String _request_content;
	protected boolean _delete_mode = false;
	protected Map<String, String> _url_options_map;
	
	//___________________________________________________________________________________
	
	// Constructor/main processing 
	
	@Override
	public void doInit() 
	{		
		 Request request = this.getRequest();
		
		 // If we're in here, then we're in a query call, we don't support any others...
		 Map<String,Object> attributes = request.getAttributes();	
		 _proxy_url = getRequest().getOriginalRef().toUri().getPath();
		 _index_or_admin_cmd = (String) attributes.get("proxyterms");
		 int length = _index_or_admin_cmd.length() + 1; // +1 for the trailing /
		 if (_proxy_url.length() > length) {
			 _index_cmd = _proxy_url.substring(1 + length);
		 }
		 
		 _url_options_map = this.getQuery().getValuesMap();
		 
	}//
	
	
	//___________________________________________________________________________________
	
	/**
	 * Handles an OPTIONS request (automatically)
	 */
	
	@Options
	public Representation options(Representation entity)
	{
		return entity;
	}
	
	//___________________________________________________________________________________
	
	/**
	 * Handles a POST
	 */

	@Post
	public Representation post(Representation entity) 
	{
		if (Method.POST == getRequest().getMethod()) 
		{
			try {
				_request_content = entity.getText();
			} catch (Exception e) {} // do nothing, carry on as far as possible
		}		 
		
		return get();
	}//
	
	//___________________________________________________________________________________
	
	/**
	 * Handles a PUT
	 */

	@Put
	public Representation put(Representation entity) 
	{
		if (Method.PUT == getRequest().getMethod()) 
		{
			try {
				_request_content = entity.getText();
			} catch (Exception e) {} // do nothing, carry on as far as possible
		}		 
		
		return get();
	}//
	
	//___________________________________________________________________________________
	
	/**
	 * Handles a DELETE
	 */

	@Delete
	public Representation delete(Representation entity) 
	{
		if (Method.DELETE == getRequest().getMethod()) 
		{
			_delete_mode = true;
			
			try {
				_request_content = entity.getText();
			} catch (Exception e) {} // do nothing, carry on as far as possible
		}		 
		
		return get();
	}//
	
	//___________________________________________________________________________________
	
	/**
	 * Handles a GET
	 */
	@Get
	public Representation get() 
	{
		//TODO: things we care about:
		// - Index 1 object
		//TODO: hmm ugh handle _id being specified?!
		// - Bulk index
		// - Get 1 object
		// - Delete 1 object
		// - Query
		// - Delete by query - NOT CURRENTLY SUPPORTED (TODO)
		
		// Finally, pass the information onto to elasticsearch
		
		StringBuffer url_str = new StringBuffer("http://localhost:9200").append('/').append(_index_or_admin_cmd);
		if (null != _index_cmd) {
			url_str.append('/').append(_index_cmd);
		}
		if ((null != _url_options_map) && !_url_options_map.isEmpty()) {
			boolean first_cmd = true;
			for (Map.Entry<String, String> entry: _url_options_map.entrySet()) {
				String key = entry.getKey();
				if (first_cmd) {
					url_str.append('?');
				}
				else {
					url_str.append('&');					
	
				}
				url_str.append(key);
				if (null != entry.getValue()) {
					url_str.append('=').append(entry.getValue());
					first_cmd = false;
				}
			}
		}//
		
		String data;
		URL url;
		HttpURLConnection conn = null;
		try {
			url = new URL(url_str.toString());
			//DEBUG
			//System.out.println(getRequest().getMethod().toString() + " " + url_str + " ?? " + url.toString());
			
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(getRequest().getMethod().toString());
			if (null != _request_content) {
				//DEBUG
				//System.out.println("POST/PUT/etc " + _postData.length());
				
				conn.setDoInput(true);
				conn.setDoOutput(true);				
				OutputStream os = conn.getOutputStream();
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
				writer.write(_request_content);
				writer.flush();
				writer.close();
				os.close();
			}
			Scanner s = null; 
			try {
				s = new Scanner(conn.getInputStream(), "UTF-8");
				data = s.useDelimiter("\\A").next();
			}
			catch (IOException fe) {
				s = new Scanner(conn.getErrorStream(), "UTF-8"); 
				data = s.useDelimiter("\\A").next();
			}
			finally {
				if (null != s) {
					s.close();
				}
			}
		}//
		catch (Exception e) {
			//DEBUG
			//e.printStackTrace();
			String err = "Index proxy error - " + _proxy_url + " : " + e.toString();
			return new StringRepresentation(err, MediaType.TEXT_PLAIN);
		}		
		
		//TODO: if we're getting the _source of an object then grab from MongoDB
		
		return new StringRepresentation(data, MediaType.APPLICATION_JSON);
	}//
}
