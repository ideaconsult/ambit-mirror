package org.opentox.aa.test;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Test;



public class TaskTest {
	@Test
	public void test() throws Exception {
		HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod("http://apps.ideaconsult.net:8080/ambit2/task/485dbe4c-4e96-4d6a-9fa6-663edb07257e");
        method.setRequestHeader("Accept", "text/uri-list");
        client.executeMethod(method);
        
        int status = method.getStatusCode();
        //logger.debug("Task status: " + status);
        System.out.println(status);
        System.out.println();

        switch (status) {
        case 404:
                
                break;
        case 200:
                /*
                InputStream in = method.getResponseBodyAsStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line  = reader.readLine();
                while (line != null) {
                	System.out.println(line);
                	line  = reader.readLine();
                }
                */
                String result = method.getResponseBodyAsString();
                if (result == null || result.length() == 0)
                        throw new IOException("Missing dataset URI for finished (200) Task.");
                else System.out.println(result);
                
        default :{
        	
        }
        }
        method.releaseConnection();
                
	}
}
