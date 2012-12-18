package ambit2.rest.admin;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.security.Authorizer;
import org.xbill.DNS.Address;

/**
 * A crude IP based guard, succeeds only if client and server resolve to the same IP address.
 * @author nina
 *
 */
public class SameIPGuard extends Authorizer {
	String[] ipAllowed = null;
	protected Logger logger;
	public SameIPGuard(String[] ipAllowed, Logger logger) {
		super();
		if (ipAllowed==null)
			this.ipAllowed = new String[] {"127.0.0.1","/0:0:0:0:0:0:0:1"};
		else
			this.ipAllowed = ipAllowed;
		Arrays.sort(ipAllowed);
		this.logger = logger;
	}
	@Override
	protected boolean authorize(Request request, Response response) {
		try {
			Form headers = (Form) request.getAttributes().get("org.restlet.http.headers");  
			String accept = headers.getFirstValue("accept");
			if (accept != null) {
				logger.log(Level.INFO,accept);
				if (accept.indexOf("image")>=0) return true;
				if (accept.indexOf("javascript")>=0) return true;
				if (accept.indexOf("uri-list")>=0) return true;
				if (accept.indexOf("text/n3")>=0) return true;
				if (accept.indexOf("application/rdf+xml")>=0) return true;
			}
			
			InetAddress[] ipclient;
			String client = request.getClientInfo().getAddress();
			if (client.contains(":")) {
				ipclient = new InetAddress[] {Address.getByAddress(client,Address.IPv6)};
			} else if (Address.isDottedQuad(client)) {
				ipclient = new InetAddress[] {Address.getByAddress(client,Address.IPv4)};				
			} else {
				ipclient = Address.getAllByName(client);
			}
			
			for (InetAddress cl : ipclient) {
				logger.log(Level.INFO,cl.toString());
				if (Arrays.binarySearch(ipAllowed,cl.toString()) >=0) {
					
					return true;
				}
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
		return false;
	}

}
