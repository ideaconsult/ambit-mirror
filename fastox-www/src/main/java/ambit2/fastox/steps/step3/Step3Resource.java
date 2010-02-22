package ambit2.fastox.steps.step3;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Parameter;
import org.restlet.resource.ResourceException;

import ambit2.fastox.ModelTools;
import ambit2.fastox.steps.FastoxStepResource;
import ambit2.fastox.users.IToxPredictSession;
import ambit2.fastox.wizard.Wizard.SERVICE;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
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
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		Form form = getRequest().getResourceRef().getQueryAsForm();
		
		forms.put("Endpoints",form);
	}
	protected Hashtable<String, Form> createForms() {
		Hashtable<String, Form> forms = new Hashtable<String, Form>();
		forms.put("Endpoints",new Form());
		forms.put("Models",new Form());

		return forms;
	}

	@Override
	protected String getDefaultTab() {
		return "Endpoints";
	}

	@Override
	public void renderFormContent(Writer writer, String key) throws IOException {
		retrieveModelsFromSparqlService(session,key);
		if ("Endpoints".equals(key) || "Select endpoints and models".equals(key)) {
			key = "Endpoints";
			writer.write("<h4>");
			/*
			if (parentendpoint!= null) {
				writer.write(
					String.format("<a href='%s%s/%s?%s=%s&%s=%s'>%s</a>",
					getRequest().getRootRef(),
					Step3Resource.resource,
					key,
					params.endpoint.toString(),
					parentendpoint==null?"http://www.opentox.org/echaEndpoints.owl#Endpoints":parentendpoint,
					params.endpoint_name.toString(),
					parentendpoint_name==null?"":parentendpoint_name,
					parentendpoint_name==null?"Endpoints":parentendpoint_name
					));
				writer.write("&nbsp;/&nbsp;");
			}
			
			writer.write(String.format("<img src='%s/images/16x16_toxicological_endpoints.png'>",
					getRootRef().toString()));		
					*/	
			writer.write(session.getEndpointName());
			writer.write("</h4>");
			try {
				Model rdf = ModelTools.retrieveModels(null,session, MediaType.APPLICATION_RDF_XML);
				ModelTools.renderModels(rdf, session, writer, false,getRequest().getRootRef());
			} catch (Exception x) {
				session.setError(x);
			}
			writer.write("</form>");
			
			Form form = new Form();
			retrieveEndpoints(form,key);
			renderEndpoints(form,writer);
			form.removeAll(params.subendpoint.toString());
		} else if ("Models".equals(key)) {
			try {
				Model rdf = ModelTools.retrieveModels(null,session, MediaType.APPLICATION_RDF_XML);
				ModelTools.renderModels(rdf, session, writer, false,getRequest().getRootRef());
			} catch (Exception x) {
				session.setError(x);
			}
		}

		super.renderFormContent(writer, key);
	}
	@Override
	public void renderFormFooter(Writer writer, String key) throws IOException {
	}
	public void retrieveModelsFromSparqlService(IToxPredictSession session, String key) throws IOException {

		session.clearModels();
		
		QueryExecution ex = null;
		try {
			
			String query = String.format(queryString.get(key),session.getEndpoint());
			
			ex = QueryExecutionFactory.sparqlService(wizard.getService(SERVICE.ontology).toString(), query);
			ResultSet results = ex.execSelect();

			while (results.hasNext()) {
				QuerySolution solution = results.next();
				Literal id = solution.getLiteral("id");
				Resource resource = solution.getResource("url");
				if (resource.getURI()!=null)
					session.addModel(resource.getURI(),Boolean.TRUE);
				else if (id!= null)
					session.addModel(id.getString(),Boolean.FALSE);
			}

		} catch (Exception x) {
			session.setError(x);
		} finally {
			try { ex.close();} catch (Exception x) {} ;
		}
		return;	
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
			session.setError(x);
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
			/*
				writer.write(String.format("<form name='select_e' method='POST' action='' value='Find models for %s'>" +
						"<img src='%s/images/resultset_next.png'>" +
						"<input type='hidden' name='endpoint_name' value='%s'>" +
						"<input type='hidden' name='endpoint' value='%s'>" +
						"<input type='submit' name='find' class='small_button' value='%s' title='Find models for %s'></form>",
						session.getEndpointName(),
						getRootRef().toString(),
					session.getEndpointName(),session.getEndpoint(),session.getEndpointName(),session.getEndpointName()));
			*/
			
			//writer.write(String.format("Specific %s</h5>",session.getEndpointName()));		
			for (String subendpoint:subendpoints) {
				writer.write(subendpoint);
				
			}



		
	}
	
	@Override
	public void renderResults(Writer writer, String key) throws IOException {
	}
}

