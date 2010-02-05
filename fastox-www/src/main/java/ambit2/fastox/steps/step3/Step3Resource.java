package ambit2.fastox.steps.step3;

import java.io.IOException;
import java.io.Writer;
import java.util.Hashtable;

import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.fastox.steps.FastoxStepResource;
import ambit2.fastox.steps.step2.Step2Resource;
import ambit2.fastox.steps.step4.Step4Resource;

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
	
	public static final String resource = "/step3";
	public static final String resourceTab = String.format("%s/{%s}",resource,tab);
	protected String endpoint = "http://www.opentox.org/echaEndpoints.owl#Endpoints";
	protected String endpoint_name = "Toxicological endpoints";
	
	protected String model = "http://www.opentox.org/echaEndpoints.owl#Model";
	protected String model_name = "Model";
	protected String compound = "";
	
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
	"	PREFIX dc:<http://purl.org/dc/elements/1.1/#>\n"+
	"	PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
	"	PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
	"	PREFIX otee:<http://www.opentox.org/echaEndpoints.owl#>\n"+
	"		select DISTINCT ?url ?title\n"+
	"		where {\n"+
	"	        ?url rdf:type ot:Model.\n"+
	"	        {\n"+
	"	        { ?url ot:dependentVariables ?vars. } UNION { ?url ot:predictedVariables ?vars. }\n"+
	"	        }\n"+
	"	        ?vars owl:sameAs <%s>.\n"+
	"}\n";
	
	public Step3Resource() {
		super("Select endpoints and models",Step2Resource.resource,Step4Resource.resource);
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		Form form = getRequest().getResourceRef().getQueryAsForm();
		Object o = form.getFirstValue(params.endpoint.toString());
		endpoint = (o==null)?endpoint:Reference.decode(o.toString());
		o = form.getFirstValue(params.endpoint_name.toString());
		endpoint_name = (o==null)?endpoint_name:Reference.decode(o.toString());
		o = form.getFirstValue(params.compound.toString());
		compound = (o==null)?"":Reference.decode(o.toString());	
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
		return "Select endpoints and models";
	}
	@Override
	protected Representation processForm(Representation entity, Variant variant)
			throws ResourceException {
		Form form = new Form(entity);
		form.add(params.endpoint.toString(),endpoint);
		Object o = form.getFirstValue(params.compound.toString());
		compound = (o==null)?"":Reference.decode(o.toString());	
		getRequest().getResourceRef().setQuery(form.getQueryString());
		return get(variant);	
	}
	@Override
	public void renderFormContent(Writer writer, String key) throws IOException {
		if ("Endpoints".equals(key) || "Select endpoints and models".equals(key)) {
			key = "Endpoints";
			Form form = retrieveModels(key);
			writer.write(String.format("<h3>%s</h3>",endpoint_name));
			renderModels(form, writer,false);
				
			renderEndpoints(writer,key);
		} else if ("Models".equals(key)) {
			Form form = retrieveModels(key);
			renderModels(form,writer,false);
		}
		
		writer.write(String.format("<input type='hidden' name='compound' value='%s'>", compound));
		super.renderFormContent(writer, key);
	}
	public Form retrieveModels(String key) throws IOException {

		Form form = forms.get(key);
		QueryExecution ex = null;
		try {
			
			String query = String.format(modelsByEndpointSparql,endpoint);
			
			ex = QueryExecutionFactory.sparqlService(ontology_service, query);
			ResultSet results = ex.execSelect();

			form.removeAll(params.model.toString());
			while (results.hasNext()) {
				QuerySolution solution = results.next();
				//Literal literal = solution.getLiteral("title");
				Resource resource = solution.getResource("url");
				form.add(params.model.toString(), resource.getURI());

			}

		} catch (Exception x) {
			form.add(params.errors.toString(),x.getMessage());
		} finally {
			try { ex.close();} catch (Exception x) {} ;
		}
		return form;		
	}
	public void renderEndpoints(Writer writer,String key) throws IOException {

		QueryExecution ex = null;
		try {
			writer.write(String.format("<h4>Specific %s</h4>",endpoint_name));		
			Form form = forms.get(key);
			/*
			writer.write(String.format("<h4><a href='%s%s/%s?%s'>%s</a></h4>",
					getRequest().getRootRef(),
					Step3Resource.resource,
					key,
					form.getQueryString(),
					endpoint_name));
			*/
			String query = String.format(endpointsSparql,endpoint);
			ex = QueryExecutionFactory.sparqlService(ontology_service, query);
			ResultSet results = ex.execSelect();
			while (results.hasNext()) {
				QuerySolution solution = results.next();
				Literal literal = solution.getLiteral("title");
				Resource resource = solution.getResource("url");
				form.removeAll(params.endpoint.toString());
				form.removeAll(params.endpoint_name.toString());
				form.add(params.endpoint.toString(), resource.getURI());
				form.add(params.endpoint_name.toString(), literal.getString());
				form.add(params.compound.toString(),compound);
				writer.write(String.format("<a href='%s%s/%s?%s'>%s</a><br>",
						getRequest().getRootRef(),
						Step3Resource.resource,
						key,
						form.getQueryString(),
						literal.getString()));
				
			}
		} catch (Exception x) {
			writer.write("<textarea>");
			x.printStackTrace();
			writer.write("</textarea>");
		} finally {
			try { ex.close();} catch (Exception x) {} ;
		}
		
	}
	@Override
	public void renderResults(Writer writer, String key) throws IOException {
	}
}
