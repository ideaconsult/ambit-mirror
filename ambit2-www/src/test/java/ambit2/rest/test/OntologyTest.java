package ambit2.rest.test;

import java.io.InputStream;

import org.junit.Test;

import ambit2.rest.OT;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
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
}
