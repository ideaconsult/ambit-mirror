package ambit2.fastox.steps.step3;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.restlet.data.Form;
import org.restlet.data.Parameter;

import ambit2.fastox.ModelTools;
import ambit2.fastox.steps.FastoxStepResource;
import ambit2.fastox.wizard.Wizard.SERVICE;
import ambit2.rest.OpenTox;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Select endpoints and models
 * @author nina
 *
 */
public class Step3Resource extends FastoxStepResource {
	
	protected Hashtable<String,String> queryString;
	protected String model = "http://www.opentox.org/echaEndpoints.owl#Model";
	protected String model_name = "Model";
	
	protected String endpointsSparql = 
	"PREFIX ot:<http://www.opentox.org/api/1.1#>\n"+
	"	PREFIX ota:<http://www.opentox.org/algorithms.owl#>\n"+
	"	PREFIX owl:<http://www.w3.org/2002/07/owl#>\n"+
	"	PREFIX dc:<http://purl.org/dc/elements/1.1/>\n"+
	"	PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
	"	PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
	"	PREFIX otee:<http://www.opentox.org/echaEndpoints.owl#>\n"+
	"		select ?url ?title\n"+
	"		where {\n"+
	"		?url rdfs:subClassOf <%s>.\n"+
	"		?url dc:title ?title.\n"+
	"		}\n";
	
	protected String modelsByEndpointSparql = 
	"PREFIX ot:<http://www.opentox.org/api/1.1#>\n"+
	"	PREFIX ota:<http://www.opentox.org/algorithms.owl#>\n"+
	"	PREFIX owl:<http://www.w3.org/2002/07/owl#>\n"+
	"	PREFIX dc:<http://purl.org/dc/elements/1.1/>\n"+
	"	PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
	"	PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
	"	PREFIX otee:<http://www.opentox.org/echaEndpoints.owl#>\n"+
	"		select DISTINCT ?url ?id\n"+
	"		where {\n"+
	"	        ?url rdf:type ot:Model.\n"+
	"	        {\n"+
	"	        { ?url ot:dependentVariables ?vars. } UNION { ?url ot:predictedVariables ?vars. }\n"+
	"	        }\n"+
	"	        ?vars owl:sameAs <%s>.\n"+
	"}\n";
	
	protected String modelsAll = 

		"PREFIX ot:<http://www.opentox.org/api/1.1#>\n"+
		"	PREFIX ota:<http://www.opentox.org/algorithms.owl#>\n"+
		"	PREFIX owl:<http://www.w3.org/2002/07/owl#>\n"+
		"	PREFIX dc:<http://purl.org/dc/elements/1.1/>\n"+
		"	PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
		"	PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
		"	PREFIX otee:<http://www.opentox.org/echaEndpoints.owl#>\n"+
		"		select DISTINCT ?url ?endpoint\n"+
		"		where {\n"+
		"	        ?url rdf:type ot:Model.\n"+
		"	        OPTIONAL {{\n"+
		"	        { ?url ot:dependentVariables ?vars. } UNION { ?url ot:predictedVariables ?vars. }\n"+
		"	        }\n"+
		"	        ?vars owl:sameAs ?endpoint}.\n"+
		"} ORDER BY ?endpoint\n";	
	
	public Step3Resource() {
		super(3);
		queryString= new Hashtable<String, String>();
		queryString.put("Endpoints",modelsByEndpointSparql);
		queryString.put("Models",modelsAll);
		helpResource = "step3.html";
	}

	@Override
	protected List<String> createTabs() {
		List<String> tabs = new ArrayList<String>();
		tabs.add("Models");
		Form form = getRequest().getResourceRef().getQueryAsForm();
		if (form.getFirstValue(OpenTox.params.model_uri.toString()) != null) { //usually hidden,currently for testing only 
			session.addModel(form.getFirstValue(OpenTox.params.model_uri.toString()),Boolean.TRUE);
		} 			
		if (form.getFirstValue(OpenTox.params.dataset_uri.toString()) != null) { //usually hidden,currently for testing only 
			session.setDatasetURI(form.getFirstValue(OpenTox.params.dataset_uri.toString()));
		} 		
		return tabs;
	}	


	@Override
	protected String getDefaultTab() {
		return "Models";
	}

	@Override
	public void renderFormContent(Writer writer, String key) throws IOException {
		try {
			renderRDFModels(writer, session, false, getRequest().getRootRef(),true);
		} catch (Exception x) {
			writer.write(x.getMessage());
		}
		super.renderFormContent(writer, key);
	}

	

	public void retrieveEndpoints(Form form,String key) throws IOException {
		
		form.removeAll(params.subendpoint.toString());
		List<Parameter> parameters = new ArrayList<Parameter>();
		QueryExecution ex = null;
		try {
			String query = String.format(endpointsSparql,session.getEndpoint());
			ex = QueryExecutionFactory.sparqlService(wizard.getService(SERVICE.ontology).toString(), query);
			ResultSet results = ex.execSelect();
			while (results.hasNext()) {
				QuerySolution solution = results.next();
				Literal literal = solution.getLiteral("title");
				Resource resource = solution.getResource("url");
				form.removeAll(params.endpoint.toString());
				form.removeAll(params.endpoint_name.toString());	
				form.removeAll(params.parentendpoint.toString());
				form.removeAll(params.parentendpoint_name.toString());
				form.add(params.endpoint.toString(), resource.getURI());
				form.add(params.endpoint_name.toString(), literal.getString());
				form.add(params.parentendpoint.toString(), session.getEndpoint());
				form.add(params.parentendpoint_name.toString(), session.getEndpointName());
				form.removeAll(params.dataset.toString());
				
						
				parameters.add(new Parameter(params.subendpoint.toString(),
						String.format("<form name='select_e' method='POST' action='' value='Find models for %s'><img src='%s/images/folder.png'>" +
								"<input type='hidden' name='endpoint_name' value='%s'>" +
								"<input type='hidden' name='endpoint' value='%s'>" +
								"<input type='submit' name='find' class='small_button' value='%s' title='Find models for %s'></form>",
						literal.getString(),
						getRootRef().toString(),
						literal.getString(),resource.getURI(),literal.getString(),literal.getString())));								

				
				/*
				parameters.add(new Parameter(params.subendpoint.toString(),
						String.format("<a href='%s/%s/%s/%s%s/%s?%s'>%s</a>",
						getRequest().getRootRef(),
						UserResource.resource,
						Reference.encode(session.getUser().getId()),
						mode,
						step.getResource(),
						key,
						form.getQueryString(),
						literal.getString())));
						*/
				
			}
		} catch (Exception x) {
			session.setError(key,x);
		} finally {
			try { ex.close();} catch (Exception x) {} ;
		}
		form.removeAll(params.endpoint.toString());
		form.removeAll(params.endpoint_name.toString());				
		form.addAll(parameters);

	}	
	public void renderEndpoints(Form form,Writer writer) throws IOException {

			String[] subendpoints = form.getValuesArray(params.subendpoint.toString());
			if (subendpoints.length==0) return;

			if (!session.getEndpointName().equals("Endpoints")) { 
				writer.write(String.format("<form name='select_e' method='POST' action='' value='Find models for %s'>" +
						"<img src='%s/images/resultset_next.png'>" +
						"<input type='hidden' name='endpoint_name' value='%s'>" +
						"<input type='hidden' name='endpoint' value='%s'>" +
						"<input type='submit' name='find' class='small_button' value='%s' title='Find models for %s'></form>",
						"Endpoints",
						getRootRef().toString(),
						"Endpoints",
						"http://www.opentox.org/echaEndpoints.owl#Endpoints",
						"Endpoints","Endpoints (top level)"));
			 }
			 writer.write(String.format("<img src='%s/images/resultset_next.png'><img src='%s/images/resultset_next.png'>%s",
					 getRootRef().toString(),getRootRef().toString(),session.getEndpointName()));
	
			for (String subendpoint:subendpoints) {
				writer.write(subendpoint);
				
			}



		
	}
	
	@Override
	public void renderResults(Writer writer, String key) throws IOException {
	}
	@Override
	public void footer(Writer output) throws IOException {
		output.write(ModelTools.jsIFrame());
		super.footer(output);
	}
}

