package demo.elasticinsight_manager.server;

import com.mongodb.Mongo;

public class MongoServer {

	protected static Mongo _driver;
	
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
}
