package ambit2.rest.test.substance;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.junit.Test;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.data.SubstanceRecord;
import ambit2.core.io.json.SubstanceStudyParser;
import ambit2.rest.substance.SubstanceLookup;
import ambit2.rest.test.ResourceTest;
import junit.framework.Assert;

/**
 * Test for /query/substance/study/uuid
 * 
 * @author nina
 *
 */
public class SubstanceLookupTest extends ResourceTest {
	private final String testUUID = "IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734";

	@Override
	protected void setDatabase() throws Exception {
		setUpDatabaseFromResource("descriptors-datasets.xml");
	}

	@Test
	public void testGet_UUIDS() throws Exception {
		String q = String.format("http://localhost:%d/query%s/study/uuid?search=%s", port, SubstanceLookup.resource,
				Reference.encode(testUUID));
		testGet(q, MediaType.APPLICATION_JSON);
	}

	@Test
	public void testPost_UUIDS() throws Exception {
		String q = String.format("http://localhost:%d/query%s/study/uuid", port, SubstanceLookup.resource);
		Form form = new Form();
		form.add("search", testUUID);
		Response r = testPost(q, MediaType.APPLICATION_JSON, form);
		//System.out.println(r.getEntityAsText());
		//verifyResponseJSON(q, MediaType.APPLICATION_JSON, new StringReader(r.getEntityAsText()));
	}

	@Override
	public boolean verifyResponseJSON(String uri, MediaType media, InputStream in) throws Exception {
		return verifyResponseJSON(uri,media,new InputStreamReader(in,"UTF-8"));
	}

	public boolean verifyResponseJSON(String uri, MediaType media, InputStreamReader reader) throws Exception {
		//JsonNode node = parseResponseJSON(uri, media, in);
		SubstanceStudyParser p = new SubstanceStudyParser(reader);
		int i=0;
		while (p.hasNext()) {
			Object o = p.next();
			Assert.assertTrue(o instanceof SubstanceRecord);
			Assert.assertEquals(testUUID,  ((SubstanceRecord)o).getSubstanceUUID());
			System.out.println(o);
			i++;
		}
		return i==1;
	}
	@Override
	public void testGetJavaObject() throws Exception {
	}
}