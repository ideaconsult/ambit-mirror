package ambit2.rest.test.substance;

import java.net.URL;
import java.util.UUID;

import org.junit.Test;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.representation.FileRepresentation;

import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Params;
import ambit2.base.data.substance.SubstanceProperty;
import ambit2.rest.substance.property.SubstancePropertyResource;
import ambit2.rest.test.ResourceTest;

public class SubstancePropertyresourceTest extends ResourceTest {
    @Override
    public String getTestURI() {
	return String.format("http://localhost:%d/%s", port, SubstancePropertyResource.property);
    }


    @Test
    public void testCreateEntry2() throws Exception {

	EffectRecord<String,IParams,String> effect = new EffectRecord<String,IParams,String>();
	effect.setConditions(new Params());
	effect.setEndpoint("EC10");
	effect.setUnit("mg/L");
	IParams conditions = new Params();
	
	effect.setConditions(conditions);
	/*
	Value v = new Value();
	v.setUnits("h");
	v.setLoValue(48.0);
	effect.getConditions().put("EC_DAPHNIATOX",v);
	*/
	//System.out.println(effect);
	SubstanceProperty prop = new SubstanceProperty(null,null,effect.getEndpoint(),effect.getUnit(),null);
	
	String key =  prop.createHashedIdentifier(effect.getConditions());

	UUID protocol_uuid = UUID.nameUUIDFromBytes("http://localhost:8080/ambit2/dataset/Method%3A+other%3A+Am.+Soc.+Test.+Mater.+%281980%29+and+U.S.+EPA+%281975%29".getBytes());
	

	URL url = getClass().getClassLoader().getResource("feature.rdf");
	FileRepresentation rep = new FileRepresentation(url.getFile(), MediaType.APPLICATION_RDF_XML, 0);

	testAsyncPoll(new Reference(getTestURI()), MediaType.TEXT_URI_LIST, rep, Method.POST,
		new Reference(String.format("http://localhost:%d/property/ECOTOX/EC_DAPHNIATOX_SECTION/EC10/%s/NOTSPECIFIED/%s",
				port,key.toUpperCase(),protocol_uuid)));
	
    }
    @Override
    public void testGetJavaObject() throws Exception {

    }
}
