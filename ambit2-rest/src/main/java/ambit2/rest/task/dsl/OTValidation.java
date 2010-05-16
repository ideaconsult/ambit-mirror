package ambit2.rest.task.dsl;

import java.util.List;

import org.restlet.data.Reference;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.PrintUtil;

public class OTValidation extends OTObject {
	protected static String validationPerModel = 
		"PREFIX ot:<http://www.opentox.org/api/1.1#>\n"+
		"select  DISTINCT ?validation\n"+
		"where {\n"+
		"    ?validation ot:validationModel <%s>.\n"+
		"}";


	protected OTValidation(Reference ref) {
		super(ref);
	}
	protected OTValidation(String ref) {
			super(ref);
	}
	
	 public static OTValidation validation(Reference datasetURI) throws Exception  { 
			    return new OTValidation(datasetURI);
	}
	
	public static OTValidation validation(String datasetURI) throws Exception  { 
			    return new OTValidation(datasetURI);
	}   

	public static OTValidation forModel(String ontologyURU,OTModel model) throws Exception  { 
	    OTOntologyService<OTValidation> ontology = new OTOntologyService<OTValidation>(ontologyURU) {
	    	protected OTValidation validation = null;
	    	@Override
	    	public OTValidation processSolution(QuerySolution row) throws Exception{
	    		String url = null;
	    		RDFNode node = row.get("validation");
	    		if (node.isURIResource()) url = ((Resource)node).getURI();
	    		else if (node.isLiteral()) url = ((Literal)node).getString();
	    		else url = node.toString();
	    		return validation(url);
	    	}
	    	@Override
	    	public OTValidation createObject() throws Exception {
	    		return validation;
	    	};
	    };
	    return ontology.query(validationPerModel);
   }   
	
	public String report(String ontologyURI) throws Exception  { 
		String a = String.format("<%s>", uri);
		String query = String.format(OTModel.getSparql("sparql/ValidationReport.sparql").toString(),a,a,a,a,a);
				
	    OTOntologyService<String> ontology = new OTOntologyService<String>(ontologyURI);
	    
	    return ontology.report(query);		
	}
	
	
}
