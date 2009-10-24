package ambit2.rest.test.similarity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.Test;
import org.restlet.Client;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

import ambit2.rest.test.ResourceTest;

public class ModelResourceTest extends ResourceTest {
	//calculates pka
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/model/2", port);
	}
	@Test
	public void testPost() throws Exception {
		Form headers = new Form();  
		headers.add("dataset-id", "1");
		Response response  =  testPost(getTestURI(),MediaType.TEXT_URI_LIST,headers);
		Status status = response.getStatus();
		Assert.assertEquals(Status.REDIRECTION_SEE_OTHER,status);
		
		Assert.assertNotNull(response.getLocationRef());
		Request request = new Request();
		Client client = new Client(Protocol.HTTP);
		request.setResourceRef(response.getLocationRef());
		request.setMethod(Method.GET);
		request.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(MediaType.TEXT_URI_LIST));
		while (status.equals(Status.SUCCESS_CREATED)) {
			System.out.println(status);
			System.out.println("poll");
			Response response1 = client.handle(request);
			if (response1.getStatus().equals(Status.REDIRECTION_SEE_OTHER)) {
				Assert.assertEquals(String.format("http://localhost:%d/template/1", port),response1.getLocationRef());
				System.out.println(status);
				return;
			} 

			status = response1.getStatus();
			
		}
		System.out.println(status);
	}
	@Override
	public boolean verifyResponseURI(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count=0;
		while ((line = reader.readLine())!=null) {
			System.out.println(line);
			count++;
		}
		return count ==1;
	}		

}
