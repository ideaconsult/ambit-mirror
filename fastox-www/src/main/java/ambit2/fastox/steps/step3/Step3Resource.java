package ambit2.fastox.steps.step3;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Parameter;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.fastox.ModelTools;
import ambit2.fastox.steps.FastoxStepResource;
import ambit2.fastox.steps.step2.Step2Resource;
import ambit2.fastox.steps.step4.Step4Resource;

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
	
	public static final String resource = "/step3";
	public static final String resourceTab = String.format("%s/{%s}",resource,tab);
	protected String endpoint = "http://www.opentox.org/echaEndpoints.owl#Endpoints";
	protected String endpoint_name = "Endpoints";
	protected String parentendpoint = null;
	protected String parentendpoint_name = null;
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
		"		select DISTINCT ?url ?title ?endpoint\n"+
		"		where {\n"+
		"	        ?url rdf:type ot:Model.\n"+
		"}\n"+
		"  LIMIT 100\n";
	
	public Step3Resource() {
		super("Endpoints",Step2Resource.resource,Step4Resource.resource);
		queryString= new Hashtable<String, String>();
		queryString.put("Endpoints",modelsByEndpointSparql);
		queryString.put("Models",modelsAll);
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		Form form = getRequest().getResourceRef().getQueryAsForm();
		Object o = form.getFirstValue(params.endpoint.toString());
		endpoint = (o==null)?endpoint:Reference.decode(o.toString());
		
		o = form.getFirstValue(params.endpoint_name.toString());
		endpoint_name = (o==null)?endpoint_name:Reference.decode(o.toString());
		
		o = form.getFirstValue(params.parentendpoint.toString());
		parentendpoint = (o==null)?parentendpoint:Reference.decode(o.toString());

		o = form.getFirstValue(params.parentendpoint_name.toString());
		parentendpoint_name = (o==null)?parentendpoint_name:Reference.decode(o.toString());
		
		forms.put("Endpoints",form);
	}
	protected Hashtable<String, Form> createForms() {
		Hashtable<String, Form> forms = new Hashtable<String, Form>();
		forms.put("Endpoints",new Form());
		forms.put("Models",new Form());
		forms.put("Help",new Form());
		return forms;
	}
	protected String getTopRef() {
		return resource;
	}
	@Override
	protected String getDefaultTab() {
		return "Endpoints";
	}
	@Override
	protected Representation processForm(Representation entity, Variant variant)
			throws ResourceException {
		Form form = new Form(entity);
		form.add(params.endpoint.toString(),endpoint);
		dataset = form.getFirstValue(params.dataset.toString());
		getRequest().getResourceRef().setQuery(form.getQueryString());
		return get(variant);	
	}
	@Override
	public void renderFormContent(Writer writer, String key) throws IOException {
		Form form = retrieveModels(key);
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
			*/
			writer.write(String.format("<img src='%s/images/16x16_toxicological_endpoints.png'>",
					getRootRef().toString()));			
			writer.write(endpoint_name);
			writer.write("</h4>");
			try {
				Model rdf = ModelTools.retrieveModels(null,form, MediaType.APPLICATION_RDF_XML);
				ModelTools.renderModels(rdf, form, writer, false,getRequest().getRootRef());
			} catch (Exception x) {
				form.add(params.errors.toString(),x.getMessage());
			}
				
			retrieveEndpoints(form,key);
			renderEndpoints(form,writer);
			form.removeAll(params.subendpoint.toString());
		} else if ("Models".equals(key)) {
			try {
				Model rdf = ModelTools.retrieveModels(null,form, MediaType.APPLICATION_RDF_XML);
				ModelTools.renderModels(rdf, form, writer, false,getRequest().getRootRef());
			} catch (Exception x) {
				form.add(params.errors.toString(),x.getMessage());
			}
		}

		form.removeAll(params.dataset.toString());
		form.add(params.dataset.toString(),dataset);
		
		writer.write(params.dataset.htmlInputHidden(dataset));
		super.renderFormContent(writer, key);
	}
	public Form retrieveModels(String key) throws IOException {

		Form form = forms.get(key);
		QueryExecution ex = null;
		try {
			
			String query = String.format(queryString.get(key),endpoint);
			
			ex = QueryExecutionFactory.sparqlService(ontology_service, query);
			ResultSet results = ex.execSelect();

			form.removeAll(params.model.toString());
			while (results.hasNext()) {
				QuerySolution solution = results.next();
				Literal id = solution.getLiteral("id");
				Resource resource = solution.getResource("url");
				if (resource.getURI()!=null)
					form.add(params.model.toString(), resource.getURI());
				else if (id!= null)
					form.add(params.model.toString(), id.getString());
			}

		} catch (Exception x) {
			form.add(params.errors.toString(),x.getMessage());
		} finally {
			try { ex.close();} catch (Exception x) {} ;
		}
		return form;		
	}
	public void retrieveEndpoints(Form form,String key) throws IOException {
		
		form.removeAll(params.subendpoint.toString());
		List<Parameter> parameters = new ArrayList<Parameter>();
		QueryExecution ex = null;
		try {
			String query = String.format(endpointsSparql,endpoint);
			ex = QueryExecutionFactory.sparqlService(ontology_service, query);
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
				form.add(params.parentendpoint.toString(), endpoint);
				form.add(params.parentendpoint_name.toString(), endpoint_name);
				form.removeAll(params.dataset.toString());
				form.add(params.dataset.toString(), dataset);
				
				parameters.add(new Parameter(params.subendpoint.toString(),
						String.format("<a href='%s%s/%s?%s'>%s</a>",
						getRequest().getRootRef(),
						Step3Resource.resource,
						key,
						form.getQueryString(),
						literal.getString())));
				
			}
		} catch (Exception x) {
			form.add(params.errors.toString(),x.getMessage());
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
			writer.write(String.format("<h5>Specific %s</h5>",endpoint_name));		
			for (String subendpoint:subendpoints) {
				writer.write(String.format("<img src='%s/images/folder.png'>",
						getRootRef().toString()));
				writer.write(subendpoint);
				writer.write("<br>");
			}

		
	}
	@Override
	public void renderResults(Writer writer, String key) throws IOException {
	}
}
