package ambit2.rest.test.algorithm;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;

import ambit2.rest.OpenTox;
import ambit2.rest.test.ResourceTest;

public class AlgorithmResourceTest extends ResourceTest {
	@Override
	public String getTestURI() {
		return String.format("http://localhost:%d/algorithm", port);
	}
	@Test
	public void testRDFXML() throws Exception {
		testGet(getTestURI(),MediaType.APPLICATION_RDF_XML);
	}	
	@Test
	public void testURI() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_URI_LIST);
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
		return count == 19;
	}	
	
	@Test
	public void testHTML() throws Exception {
		testGet(getTestURI(),MediaType.TEXT_HTML);
	}
	@Override
	public boolean verifyResponseHTML(String uri, MediaType media, InputStream in)
			throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		int count=0;
		while ((line = reader.readLine())!=null) {
		//	System.out.println(line);
			count++;
		}
		return count > 0;
	}	
	@Test
	public void testPost() throws Exception {
		Form headers = new Form();  
		testAsyncTask(
				String.format("http://localhost:%d/algorithm/toxtreecramer", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"ToxTree%3A+Cramer+rules"));
		testAsyncTask(
				String.format("http://localhost:%d/algorithm/toxtreecramer2", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/3", port));
		
	}
	
	@Test
	public void testCalculateLogP() throws Exception {
		Form headers = new Form();  
		headers.add("dataset_uri",String.format("http://localhost:%d/dataset/1", port));
		testAsyncTask(
				String.format("http://localhost:%d/algorithm/xlogp", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/dataset/%s", port,"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2FXLogP%2Fpredicted"));
	}	
	@Test
	public void testCalculateBCUT() throws Exception {
		Form headers = new Form();  
		headers.add("dataset_uri",String.format("http://localhost:%d/dataset/1", port));
		testAsyncTask(
				String.format("http://localhost:%d/algorithm/BCUT", port),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/dataset/%s", port,"1?feature_uris[]=http%3A%2F%2Flocalhost%3A8181%2Fmodel%2FBCUT%2Bdescriptors%2Fpredicted"));
	}	
	public void testLoad() throws Exception {
		for (int i=0; i < 100;i++) {
			Form headers = new Form();  
			headers.add(OpenTox.params.dataset_uri.toString(), 
					"http://ambit.uni-plovdiv.bg:8080/ambit2/dataset/6?feature_uris[]=http://ambit.uni-plovdiv.bg:8080/ambit2/feature/11938&feature_uris[]=http://ambit.uni-plovdiv.bg:8080/ambit2/feature/11937&max=100000&feature_uris[]=http://ambit.uni-plovdiv.bg:8080/ambit2/feature/11948&max=10");
			headers.add(OpenTox.params.target.toString(),
					"http://ambit.uni-plovdiv.bg:8080/ambit2/feature/11948");
			testPost(
					String.format("http://localhost:%d/algorithm/J48", port),
					MediaType.TEXT_URI_LIST,
							//Reference.encode(String.format("http://localhost:%d/dataset/1",port))),
					headers.getWebRepresentation());
			
		}
	}
	@Test
		public void testClassifier() throws Exception {
			Form headers = new Form();  
			headers.add(OpenTox.params.dataset_uri.toString(), 
					"http://ambit.uni-plovdiv.bg:8080/ambit2/dataset/6?feature_uris[]=http://ambit.uni-plovdiv.bg:8080/ambit2/feature/11938&feature_uris[]=http://ambit.uni-plovdiv.bg:8080/ambit2/feature/11937&max=100000&feature_uris[]=http://ambit.uni-plovdiv.bg:8080/ambit2/feature/11948&max=10");
			headers.add(OpenTox.params.target.toString(),
					"http://ambit.uni-plovdiv.bg:8080/ambit2/feature/11948");
			testAsyncTask(
					String.format("http://localhost:%d/algorithm/J48", port),
							//Reference.encode(String.format("http://localhost:%d/dataset/1",port))),
					headers, Status.SUCCESS_OK,
					String.format("http://localhost:%d/model/%s", port,"3"));

			
	}	

	@Test
	public void testClustering() throws Exception {
		Form headers = new Form();  
		headers.add(OpenTox.params.dataset_uri.toString(), 
				"http://ambit.uni-plovdiv.bg:8080/ambit2/dataset/6?feature_uris[]=http://ambit.uni-plovdiv.bg:8080/ambit2/feature/11938&feature_uris[]=http://ambit.uni-plovdiv.bg:8080/ambit2/feature/11937&max=100000");
		testAsyncTask(
				String.format("http://localhost:%d/algorithm/SimpleKMeans", port),
						//Reference.encode(String.format("http://localhost:%d/dataset/1",port))),
				headers, Status.SUCCESS_OK,
				String.format("http://localhost:%d/model/%s", port,"3"));

		
	}	
	@Override
	public void testGetJavaObject() throws Exception {
	}
}
