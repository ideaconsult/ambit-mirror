package ambit2.rest.test;

import java.io.InputStream;

import net.idea.restnet.rdf.ns.OT;

import org.junit.Test;
import org.opentox.rest.HTTPClient;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.util.PrintUtil;

public class OntologyTest {
	@Test
	public void test1() throws Exception {
		
	}
	public void test() throws Exception {
		OntModel jenaModel = OT.createModel(OntModelSpec.OWL_DL_MEM_RULE_INF);
		InputStream in = getClass().getClassLoader().getResourceAsStream("ambit2/rest/config/opentox.owl");
		jenaModel.read(in,null);
		in.close();
		Reasoner reasoner = ReasonerRegistry.getOWLMiniReasoner();
 
			//ReasonerRegistry.getOWLMicroReasoner();
		reasoner.bindSchema (jenaModel);
		
		OntModel instances = OT.createModel();
		instances.createIndividual(OT.OTClass.Algorithm.getOntClass(instances));
		/*
Model instances = ModelFactory.createDefaultModel();
instances.read ("file:art.n3", "N3");
		 */
		
		/*
FileManager.get().loadModel("file:art.n3");
		 */

		InfModel model = ModelFactory.createInfModel (reasoner, instances);
		
		Resource r = model.getResource ("http://www.opentox.org/api/1.1#" + "Algorithm");
		StmtIterator sti = model.listStatements(r, null, (RDFNode) null);
		while (sti.hasNext()) {
		     Statement stmt = sti.nextStatement();
		     System.out.println(" - " + PrintUtil.print(stmt));
		}
		/*
		ExtendedIterator<AnnotationProperty> p = jenaModel.listAnnotationProperties();
		while (p.hasNext()) {
			System.out.println(p.next());
		}
		ExtendedIterator<ObjectProperty> po = jenaModel.listObjectProperties();
		while (po.hasNext()) {
			System.out.println(po.next());
		}		
		ExtendedIterator<DatatypeProperty> pd = jenaModel.listDatatypeProperties();
		while (pd.hasNext()) {
			System.out.println(pd.next());
		}		
		*/
		/*
QueryExecution qe = QueryExecutionFactory.create (
                   "SELECT ?opus "+
                   "WHERE { "  +
                   "   <http://www.whatever.com/#VanGogh> " +
                   "   <http://www.whatever.com/#hasCreated> ?opus}",
           model);
		 */
	}
	
	@Test
	public void testOntology() throws Exception {
		  HTTPClient cli = new HTTPClient("http://apps.ideaconsult.net:8080/ontology");
		  String sparql = 
				"PREFIX ot:<http://www.opentox.org/api/1.1#>\n"+
				"PREFIX owl:<http://www.w3.org/2002/07/owl#>\n"+
				"PREFIX dc:<http://purl.org/dc/elements/1.1/>\n"+
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
				"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
				"		select ?Dataset ?Title ?Source\n"+
				"		where {\n"+
				"		?Dataset rdf:type ot:Dataset.\n"+
				"	   OPTIONAL {?Dataset dc:title ?Title}.\n"+
				"	   OPTIONAL {?Dataset dc:source ?Source}.\n"+
				"}\n"+
				"      ORDER by ?Dataset\n";		  
		  cli.setHeaders(new String[][] {
				  {"Accept","application/sparql-query"}	  
		  });
		  cli.postWWWForm(new String[][] {
		    {"query",sparql},
		    });
		  InputStream in = cli.getInputStream();
		  ResultSet rs = ResultSetFactory.fromXML(in);
		  while (rs.hasNext()) {
			  QuerySolution qs = rs.next();
			  System.out.println(qs.getLiteral("Title"));
		  }
		  in.close();
		  cli.release();
	}
}
