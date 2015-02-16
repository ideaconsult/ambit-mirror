package ambit2.rest.test.substance;

import java.io.File;
import java.net.URL;

import junit.framework.Assert;
import net.idea.restnet.db.CreateDatabaseProcessor;
import net.idea.restnet.db.test.DbUnitTest;
import net.idea.restnet.i.task.TaskResult;

import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;

import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Params;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolEffectRecord;
import ambit2.base.data.study.Value;
import ambit2.base.data.substance.SubstanceProperty;
import ambit2.rest.property.PropertyURIReporter;
import ambit2.rest.substance.ProtocolEffectRecord2SubstanceProperty;
import ambit2.rest.substance.property.CallableSubstancePropertyCreator;
import ambit2.rest.test.CreateAmbitDatabaseProcessor;

public class CallableSubstancePropertyCreatorTest extends DbUnitTest {

    @Test
    public void testCreateSubstanceProperty() throws Exception {

	Form form = new Form();
	form.add("endpointcategory", Protocol._categories.ZETA_POTENTIAL_SECTION.name());
	form.add("name", "ZETA POTENTIAL");
	form.add("unit", "mV");
	form.add("protocol", "Method: other: no data");
	Params p = new Params();
	Value v = new Value();
	v.setLoValue(4.26);
	p.put("pH", v);
	Assert.assertEquals("{\"pH\":{\"loValue\":4.26}}", p.toString());
	try {
	    PropertyURIReporter reporter = new PropertyURIReporter(new Reference("http://localhost:8081/ambit2"));
	    CallableSubstancePropertyCreator callable = new CallableSubstancePropertyCreator(reporter, Method.POST,
		    form, null, null);
	    TaskResult task = callable.call();
	    Assert.assertTrue(task
		    .getUri()
		    .startsWith(
			    "http://localhost:8081/ambit2/property/P-CHEM/ZETA_POTENTIAL_SECTION/ZETA+POTENTIAL/A597EBC063D7A5A605B9535656E09149A72F0C23"));
	    // "http://localhost:8081/ambit2/property/P-CHEM/ZETA_POTENTIAL_SECTION/ZETA+POTENTIAL/945DEEC24F876DEE6116F92646EF729107F5B5FD"
	} catch (Exception x) {
	    throw x;
	} finally {

	}
    }

    /**
     * @throws Exception
     */
    @Test
    public void testCreateSubstanceProperty_TO_BIODEG_WATER_SCREEN_SECTION() throws Exception {

	Form form = new Form();
	form.add("endpointcategory", Protocol._categories.TO_BIODEG_WATER_SCREEN_SECTION.name());
	form.add("name", "% Degradation");
	form.add("unit", "%");
	form.add("protocol", "Method: other: no data");
	Params p = new Params();
	Value v = new Value();
	v.setUnits("d");
	v.setLoValue(7.0);
	p.put("Sampling time", v);
	form.add("conditions", p.toString());
	Assert.assertEquals("{\"Sampling time\":{\"loValue\":7.0,\"unit\":\"d\"}}", p.toString());

	try {
	    PropertyURIReporter reporter = new PropertyURIReporter(new Reference("http://localhost:8081/ambit2"));
	    CallableSubstancePropertyCreator callable = new CallableSubstancePropertyCreator(reporter, Method.POST,
		    form, null, null);
	    TaskResult task = callable.call();
	    Assert.assertTrue(task
		    .getUri()
		    .startsWith(
			    "http://localhost:8081/ambit2/property/ENV+FATE/TO_BIODEG_WATER_SCREEN_SECTION/%25+Degradation/3CF77D9EFE4E74744E691BF4880B4EF728B16FAE"));
	    // /d4cd0dab-cf4c-3a22-ad92-fab40844c786

	    /*
	     * "http://localhost:8080/ambit2/property/ENV+FATE/TO_BIODEG_WATER_SCREEN_SECTION/%25+Degradation/5FA6EFD4E83BEC47B626C6149DBBD3907084187F/1f255895-eb7d-37dc-8eb8-bc9580d8be25"
	     * :{
	     * 
	     * "type":"Feature", "title":"% Degradation", "units":"%",
	     * "isNominal":false, "isNumeric":false, "isMultiValue":true,
	     * "sameAs"
	     * :"http://www.opentox.org/echaEndpoints.owl#TO_BIODEG_WATER_SCREEN"
	     * , "isModelPredictionFeature":false,
	     * "creator":"Method: other: see below", "order":1087, "source":{
	     * "URI"
	     * :"http://localhost:8080/ambit2/dataset/Method%3A+other%3A+see+below"
	     * , "type":"Dataset" }, "annotation":[ { "p" : "Sampling time", "o"
	     * : "7.0 d"}]
	     * 
	     * },
	     */

	    // http://localhost:8081/ambit2/property/P-CHEM/TO_BIODEG_WATER_SCREEN_SECTION/%25+Degradation/8C9DF1AF60CA3D8706C0156361E97E0DF4391C10/1f255895-eb7d-37dc-8eb8-bc9580d8be25
	} catch (Exception x) {
	    throw x;
	} finally {

	}
    }

    @Test
    public void testCreateSubstancePropertyFromRDF() throws Exception {

	URL url = getClass().getClassLoader().getResource("feature.rdf");
	File file = new File(url.getFile());
	Assert.assertTrue(file.exists());

	try {
	    PropertyURIReporter reporter = new PropertyURIReporter(new Reference("http://localhost:8081/ambit2"));
	    CallableSubstancePropertyCreator callable = new CallableSubstancePropertyCreator(reporter, Method.POST,
		    file, MediaType.APPLICATION_RDF_XML, null, null);
	    TaskResult task = callable.call();
	    
	    ProtocolEffectRecord2SubstanceProperty p = new ProtocolEffectRecord2SubstanceProperty();
	    ProtocolEffectRecord<String, IParams, String> effect = new ProtocolEffectRecord<String, IParams, String>();
	    Protocol protocol = new Protocol(null);
	    Protocol._categories category = Protocol._categories.EC_DAPHNIATOX_SECTION;
	    protocol.setCategory(category.name());
	    protocol.setTopCategory(category.getTopCategory());
	    protocol.addGuideline("http://localhost:8080/ambit2/dataset/Method%3A+other%3A+Am.+Soc.+Test.+Mater.+%281980%29+and+U.S.+EPA+%281975%29");
	    effect.setProtocol(protocol);
	    effect.setEndpoint("EC10");
	    effect.setUnit("mg/L");
	    effect.setConditions(new Params());
	    /*
	    Value value = new Value();
	    value.setLoValue(48.0);
	    value.setUnits("h");
	    effect.getConditions().put("Exposure", value);
	    */
	    SubstanceProperty prop = p.process(effect);
	    prop.setIdentifier(prop.createHashedIdentifier(effect.getConditions()));
	    Assert.assertEquals(reporter.getURI(prop),task.getUri());

	} catch (Exception x) {
	    throw x;
	} finally {

	}
    }

    protected String dbFile = "src/test/resources/descriptors-datasets.xml";

    @Override
    protected CreateDatabaseProcessor getDBCreateProcessor() {
	return new CreateAmbitDatabaseProcessor();
    }

    @Override
    public String getDBTables() {
	return "src/test/resources/tables.xml";
    }

    @Override
    protected String getConfig() {
	return "ambit2/rest/config/ambit2.pref";
    }

}
