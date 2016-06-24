package ambit2.rest.substance;

import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.junit.Test;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.test.TestSubstanceFactory;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class TestSubstanceRDFExport {
	@Test
	public void test() throws Exception {
		// List<SubstanceRecord> sr = new ArrayList<SubstanceRecord>();
		// sr.add(TestSubstanceFactory.getTestSubstanceRecord());

		SubstanceRecord record = TestSubstanceFactory.getTestSubstanceRecord();
		// TestSubstanceFactory.getTestSubstanceEndpointsBundle());
		// todo refactror the reporter to accept url string, not request
		Request hack = new Request();
		hack.setRootRef(new Reference("http://localhost/ambit2"));
		SubstanceRDFReporter r = new SubstanceRDFReporter(hack,
				MediaType.TEXT_RDF_N3);
		Model model = ModelFactory.createDefaultModel();
		r.header(model, null);
		r.setOutput(model);

		// loop for several records
		r.processItem(record);

		r.footer(model, null);
		RDFDataMgr.write(System.out, model, RDFFormat.TURTLE);
	}

}
