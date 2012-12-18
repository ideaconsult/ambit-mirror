package ambit2.rest.admin;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.Request;
import org.restlet.Response;
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
				logger.log(Level.FINE,cl.toString());
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
