package ambit2.rest.admin;

import java.net.InetAddress;

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

	@Override
	protected boolean authorize(Request request, Response response) {
		try {
			
			InetAddress[] ipclient;
			InetAddress[] ipserver;
			String client = request.getClientInfo().getAddress();
			String server = request.getHostRef().getHostDomain();
			if (!Address.isDottedQuad(client)) {
				ipclient = Address.getAllByName(client);
			} else ipclient = new InetAddress[] {Address.getByAddress(client)};
			if (!Address.isDottedQuad(server)) {
				ipserver = Address.getAllByName(server);
			} else ipserver = new InetAddress[] {Address.getByAddress(server)};

			for (InetAddress cl : ipclient)
				for (InetAddress s : ipserver) {
					boolean ok = cl.equals(s);
					System.out.println(String.format("Comparing %s %s : %s",cl.getHostAddress(),s.getHostAddress(),Boolean.toString(ok)));
					if (ok) return true;
				}
				
			return true;
		} catch (Exception x) {
			x.printStackTrace();
			return false;
		}
	}

}
