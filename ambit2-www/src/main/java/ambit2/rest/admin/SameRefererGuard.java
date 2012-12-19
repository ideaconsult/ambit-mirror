package ambit2.rest.admin;

import java.util.logging.Logger;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;

/**
 * A simple guard, succeeds only if the HTTP referer is in the list
 * @author nina
 *
 */
public class SameRefererGuard  extends SimpleGuard {

	public SameRefererGuard(Logger logger) {
		this(new String[] {"http://localhost","https://localhost"},logger);
	}
	public SameRefererGuard(String[] ipAllowed, Logger logger) {
		super(ipAllowed,logger);
	}

	@Override
	protected boolean authorize(Request request, Response response) {
		try {
			Form headers = (Form) request.getAttributes().get("org.restlet.http.headers");  
			String referer = headers.getFirstValue("referer");
			if (referer != null) 
				for (String r : allowed) 
					if (referer.startsWith(r)) return true;
		} catch (Exception x) {
			x.printStackTrace();
		}
		return false;
	}

}