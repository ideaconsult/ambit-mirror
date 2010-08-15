package ambit2.rest.admin;

import java.io.InputStream;
import java.util.Properties;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.security.Authorizer;

public class DBCreateAllowedGuard extends Authorizer {
	protected static Properties properties = null;
	@Override
	protected boolean authorize(Request arg0, Response arg1) {
		loadProperties();
		String ok = properties.getProperty("database.create");
		return (ok != null) && ok.toLowerCase().equals("true");
	}

	protected synchronized void loadProperties()  {
		try {
		if (properties == null) {
			properties = new Properties();
			InputStream in = this.getClass().getClassLoader().getResourceAsStream("ambit2/rest/config/ambit2.pref");
			properties.load(in);
			in.close();		
		}
		} catch (Exception x) {
			properties = null;
		}
	}	
}
