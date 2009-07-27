package ambit2.rest.test;

import org.restlet.Client;
import org.restlet.data.Protocol;
import org.restlet.data.Response;

public class ClientTest {
	public static void main(String[] args) {
		try {
			Client client = new Client(Protocol.HTTP);
			Response response =	client.get("http://localhost:8080/ambit2-www");
			System.out.println(response.getStatus());
			String out = response.getEntity().getText();
			System.out.println(out);

		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}
