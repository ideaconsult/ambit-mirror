package ambit2.rest.test.substance;

import junit.framework.Assert;
import net.idea.restnet.db.CreateDatabaseProcessor;
import net.idea.restnet.db.test.DbUnitTest;
import net.idea.restnet.i.task.TaskResult;

import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Reference;

import ambit2.base.data.study.Params;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.Value;
import ambit2.rest.property.PropertyURIReporter;
import ambit2.rest.substance.property.CallableSubstancePropertyCreator;
import ambit2.rest.test.CreateAmbitDatabaseProcessor;

public class CallableSubstancePropertyCreatorTest extends DbUnitTest {

    @Test
    public void testCreateSubstanceProperty() throws Exception {

	Form form = new Form();
	form.add("endpointcategory", Protocol._categories.ZETA_POTENTIAL_SECTION.name());
	form.add("name", "ZETA POTENTIAL");
	form.add("unit", "mV");
	form.add("protocol" , "Method: other: no data");
	Params p = new Params();
	Value v = new Value();
	v.setLoValue(4.26);
	p.put("pH",v);
	Assert.assertEquals("{\"pH\":{\"loValue\":4.26}}",p.toString());
	try {
	    PropertyURIReporter reporter = new PropertyURIReporter(new Reference("http://localhost:8081/ambit2"));
	    CallableSubstancePropertyCreator callable = new CallableSubstancePropertyCreator(reporter, Method.POST,
		    form,null, null);
	    TaskResult task = callable.call();
	    Assert.assertTrue(task.getUri().startsWith("http://localhost:8081/ambit2/property/P-CHEM/ZETA_POTENTIAL_SECTION/ZETA+POTENTIAL/A597EBC063D7A5A605B9535656E09149A72F0C23"));
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
	form.add("protocol" , "Method: other: no data");
	Params p = new Params();
	Value v = new Value();
	v.setUnits("d");
	v.setLoValue(7.0);
	p.put("Sampling time",v);
	form.add("conditions", p.toString());
	Assert.assertEquals("{\"Sampling time\":{\"loValue\":7.0,\"unit\":\"d\"}}",p.toString());
	
	try {
	    PropertyURIReporter reporter = new PropertyURIReporter(new Reference("http://localhost:8081/ambit2"));
	    CallableSubstancePropertyCreator callable = new CallableSubstancePropertyCreator(reporter, Method.POST,
		    form,null, null);
	    TaskResult task = callable.call();
	    Assert.assertTrue(task.getUri().startsWith("http://localhost:8081/ambit2/property/ENV+FATE/TO_BIODEG_WATER_SCREEN_SECTION/%25+Degradation/3CF77D9EFE4E74744E691BF4880B4EF728B16FAE"));
	    ///d4cd0dab-cf4c-3a22-ad92-fab40844c786

	 
	    /*
"http://localhost:8080/ambit2/property/ENV+FATE/TO_BIODEG_WATER_SCREEN_SECTION/%25+Degradation/5FA6EFD4E83BEC47B626C6149DBBD3907084187F/1f255895-eb7d-37dc-8eb8-bc9580d8be25":{

	"type":"Feature",
	"title":"% Degradation",
	"units":"%",
	"isNominal":false,
	"isNumeric":false,
	"isMultiValue":true,
	"sameAs":"http://www.opentox.org/echaEndpoints.owl#TO_BIODEG_WATER_SCREEN",
	"isModelPredictionFeature":false,
	"creator":"Method: other: see below",
	"order":1087,
	"source":{
		"URI":"http://localhost:8080/ambit2/dataset/Method%3A+other%3A+see+below",
		"type":"Dataset"
	},
	"annotation":[
	{	"p" : "Sampling time",	"o" : "7.0 d"}]

},
	     */
	    
	    //http://localhost:8081/ambit2/property/P-CHEM/TO_BIODEG_WATER_SCREEN_SECTION/%25+Degradation/8C9DF1AF60CA3D8706C0156361E97E0DF4391C10/1f255895-eb7d-37dc-8eb8-bc9580d8be25
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
