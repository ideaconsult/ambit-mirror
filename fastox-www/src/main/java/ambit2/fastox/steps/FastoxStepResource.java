package ambit2.fastox.steps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import ambit2.fastox.steps.step1.Step1Resource;
import ambit2.fastox.wizard.WizardResource;
import ambit2.rest.rdf.OT;
import ambit2.rest.rdf.RDFModelIterator;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileUtils;

public abstract class FastoxStepResource extends WizardResource {
	protected String dataset= null;
	public enum params {
		search,
		text,
		dataset,
		compound,
		query,
		parentendpoint,
		parentendpoint_name,
		endpoint,
		endpoint_name,
		subendpoint,
		model,
		model_name,
		errors;
		public String htmlInputHidden(String value) {
			return String.format("<input name='%s' type='hidden' value='%s'>\n",toString(),value);
		}
		public String htmlInputText(String value) {
			return String.format("<input name='%s' type='text' value='%s'>\n",toString(),value);
		}		
		public String htmlInputCheckbox(String value,String title) {
			return
				String.format("<input type='checkbox' checked name='%s'>%s\n<input type='hidden' name='%s' value='%s'>",
						value,title==null?value:title,toString(),value);
		}		
	};			
		
	protected String queryString = 
	"PREFIX ot:<http://www.opentox.org/api/1.1#>\n"+
	"	PREFIX ota:<http://www.opentox.org/algorithms.owl#>\n"+
	"	PREFIX owl:<http://www.w3.org/2002/07/owl#>\n"+
	"	PREFIX dc:<http://purl.org/dc/elements/1.1/>\n"+
	"	PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
	"	PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
	"	PREFIX otee:<http://www.opentox.org/echaEndpoints.owl#>\n"+
	//"		select DISTINCT ?c ?o ?f ?name ?value ?dataset\n"+
	"		select DISTINCT ?c ?o ?value\n"+
	"		where {\n"+
	"	     OPTIONAL {  ?dataset ot:dataEntry ?d}.\n"+
	"		 OPTIONAL { ?d rdf:type ot:DataEntry}.\n"+
	"	     ?d ot:compound ?c.\n"+
	"	     OPTIONAL {?d ot:values ?v}.\n"+
	"	     OPTIONAL {?v ot:value ?value}.\n"+
	"	     OPTIONAL {?v ot:feature ?f}.\n"+
	"	     OPTIONAL {?f owl:sameAs ?o}.\n"+
	//"	     OPTIONAL {?f dc:title ?name}.\n"+
	"	OPTIONAL {\n"+
	"	{ ?f owl:sameAs ot:ChemicalName.}\n"+
	"	UNION\n"+
	"	{ ?f owl:sameAs ot:CASRN.}\n"+
	"	UNION\n"+
	"	{ ?f owl:sameAs ot:EINECS.}\n"+	
	"	UNION\n"+
	"	{ ?f owl:sameAs ot:InChI.}\n"+
	"	UNION\n"+
	"	{ ?f owl:sameAs ot:MolecularFormula.}\n"+
	"	UNION\n"+
	"	{ ?f owl:sameAs ot:SMILES.}\n"+
		
	"%s"+
	/*
	"	UNION\n"+
	"	{ ?f owl:sameAs otee:Carcinogenicity.}\n"+
	*/
	"	}\n"+
	
	"		}\n"+
	"	ORDER by ?c ?o";


		
	public FastoxStepResource(String title, String prevStep, String nextStep) {
		super(title, prevStep, nextStep);
	}
	protected boolean isMandatory(String param) {
		return params.dataset.toString().equals(param);
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		Form form = getRequest().getResourceRef().getQueryAsForm();
		dataset = form.getFirstValue(params.dataset.toString());		
	}
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		if (dataset == null) {
			Form form = getRequest().getResourceRef().getQueryAsForm();
			dataset = form.getFirstValue(params.dataset.toString());
			if ((dataset==null) && (isMandatory(params.dataset.toString()))) {
				redirectSeeOther(String.format("%s%s",
					getRequest().getRootRef(),Step1Resource.resource
					));
				//todo error
			}
		}
		return super.get(variant);
	}
	protected String readUriList(InputStream in) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = reader.readLine()) != null) { return line; }
			
		} catch (Exception x) {
			
		} finally {
			try {in.close(); } catch (Exception x) {}
		}
		return null;
	}
	
	protected void processURI(String line,Writer writer)  throws IOException {
		writer.write("<tr>");
		writer.write("<td>");
		writer.write(line);
		writer.write("</td>");
		writer.write("</tr>");
	}
	protected int processURIList(InputStream in,Writer writer) throws Exception {
		int count = 0;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = reader.readLine()) != null) {
				processURI(line,writer);
				count++;
			}
		} catch (Exception x) {
			throw x;
		} finally {
			try {in.close(); } catch (Exception x) {}
		}
		return count;
	}	
	protected String renderCSV(InputStream in, Writer writer) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			String td = "th";
			writer.write("<table class='results'>");
			while ((line = reader.readLine()) != null) { 
				writer.write("<tr>");
				String[] cols = line.split(",");
				for (String col:cols)
					writer.write(String.format("<%s>%s</%s>",td,col.replace("\"",""),td));
//				writer.write(line);
				writer.write("</tr>");
				td = "td";
			}
			writer.write("</table>");
		} catch (Exception x) {
			
		} finally {
			try {in.close(); } catch (Exception x) {}
		}
		return null;
	}	
	
	protected int renderModels(Form form, Writer writer, boolean status) throws IOException {
		   int running = 0;
		   writer.write(String.format("<h4>%s</h4>","Models"));
			String[] models = form.getValuesArray(params.model.toString());
			if (models.length==0) {
				writer.write("<div class='message'>No models</div>");
				return 0;
			}
			try {
				OntModel rdf = retrieveModel(models, MediaType.APPLICATION_RDF_XML);
				renderRDFModel(rdf, writer, form, status);
			} catch (Exception x) {
				x.printStackTrace();
				writer.write("<table class='models'>");
				writer.write("<tr><th align='left'>Model</th>");
				if (status)	writer.write("<th align='left'>Status</th><th align='left'>Results</th>");
				writer.write("</tr>");				
				for (String model:models) {
					writer.write("<tr>");
					writer.write("<td>");
					writer.write(params.model.htmlInputCheckbox(model,model));
					writer.write("</td>");
					if (!status) continue;
					String[] uris = form.getValuesArray(model);
					
					for (String uri:uris) {
						if ("on".equals(uri)) continue;
						String[] tasks = form.getValuesArray(uri);
						writer.write("<td>");
						int isRunning = 0;
						for (String task:tasks) {
							if (task.equals(Status.SUCCESS_ACCEPTED.getName()) || task.equals(Status.REDIRECTION_SEE_OTHER.getName()))
								isRunning++;
							writer.write(String.format("%s",task));
						}
						running += isRunning;
						writer.write("</td>");
						writer.write("<td>");
						writer.write(String.format("<a href='%s'>%s</a>",uri,(isRunning==0)?"Results":"Still running"));
						writer.write(String.format("<input type='hidden' name='%s' value='%s'>", model,uri));	
						writer.write("</td>");					

					}
					writer.write("</tr>");
				}	
				writer.write("</table>");
			}
			return running;
	}
	
	protected int renderRDFModel(OntModel rdf,Writer writer, Form form, boolean status) throws Exception {
		final String sparql = 
			"PREFIX ot:<http://www.opentox.org/api/1.1#>\n"+
			"	PREFIX ota:<http://www.opentox.org/algorithms.owl#>\n"+
			"	PREFIX owl:<http://www.w3.org/2002/07/owl#>\n"+
			"	PREFIX dc:<http://purl.org/dc/elements/1.1/>\n"+
			"	PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
			"	PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
			"	PREFIX otee:<http://www.opentox.org/echaEndpoints.owl#>\n"+
			"		select DISTINCT ?url ?title ?algorithm ?creator ?id\n"+
			"		where {\n"+
			"	        ?url rdf:type ot:Model.\n"+
			"	        OPTIONAL {?url dc:title ?title}.\n"+
			"	        OPTIONAL {?url dc:creator ?creator}.\n"+
			"	        OPTIONAL {?url ot:algorithm ?algorithm}.\n"+
			"	        OPTIONAL {?url dc:identifier ?id}.\n"+	
			"}\n";		
		QueryExecution qe = null;
		int running = 0;
		try {
			Query query = QueryFactory.create(sparql);
			qe = QueryExecutionFactory.create(query,rdf );
			ResultSet results = qe.execSelect();
			writer.write("<table class='resuts'>");
			writer.write("<tr>");
			writer.write("<th>Model</th>");
			writer.write("<th>Creator</th>");
			writer.write("<th>Algorithm</th>");
			writer.write("</tr>");
			while (results.hasNext()) {
				
				QuerySolution solution = results.next();
				Resource url = solution.getResource("url");
				Resource algo = solution.getResource("algorithm");
				Literal name = solution.getLiteral("title");
				Literal creator = solution.getLiteral("creator");
				Literal id = solution.getLiteral("id");
				String modelUri = url.getURI()==null?(id==null?null:id.getString()):url.getURI();
				if (modelUri==null) continue;
				writer.write("<tr>");					
				writer.write("<td>");
				writer.write(params.model.htmlInputCheckbox(modelUri,name==null?modelUri:name.getString()));
				writer.write("</td><td>");
				writer.write(creator==null?"":creator.getString());
				writer.write("</td><td>");
				writer.write(algo==null?"":algo.getURI());
				writer.write("</td>");
				
				/**
				 * Results URL and status
				 */
				if (!status) continue;
				String[] uris = form.getValuesArray(modelUri);
				
				for (String uri:uris) {
					if ("on".equals(uri)) continue;
					String[] tasks = form.getValuesArray(uri);
					writer.write("<td>");
					int isRunning = 0;
					for (String task:tasks) {
						if (task.equals(Status.SUCCESS_ACCEPTED.getName()) || task.equals(Status.REDIRECTION_SEE_OTHER.getName()))
							isRunning++;
						writer.write(String.format("%s",task));
					}
					running += isRunning;
					writer.write("</td>");
					writer.write("<td>");
					writer.write(String.format("<a href='%s'>%s</a>",uri,(isRunning==0)?"Results":"Still running"));
					writer.write(String.format("<input type='hidden' name='%s' value='%s'>", modelUri,uri));	
					writer.write("</td>");					

				}				
				writer.write("</tr>");
			}
			writer.write("</table>");
		}catch (Exception x) {
			throw x;
		} finally {
			try {qe.close();} catch (Exception x) {}
			try {rdf.close();} catch (Exception x) {}
		}		
		return running;
	}	
	protected OntModel retrieveModel(String[] modeluri,MediaType mediaType) throws Exception {
		
		OntModel rdf = null;
		for (String uri:modeluri) {
			Representation r = null;
			try {
				ClientResource client = new ClientResource(new Reference(uri));
				client.setFollowingRedirects(false);
				r = client.get(mediaType);
	
				Status status = client.getStatus();
				if (status.equals(Status.SUCCESS_OK))  {
					rdf = OT.createModel(rdf, r.getStream(), mediaType);
				}
	
				
			} catch (ResourceException x) {
				x.printStackTrace();
	
			} catch (Exception x) {
				x.printStackTrace();
			} finally {
				try {r.release();} catch (Exception x) {}
				
			}			
		}
		return  rdf;
	}

	protected void renderCompounds(Writer writer) {
		Form form = getRequest().getResourceRef().getQueryAsForm();
		try {
			writer.write(params.dataset.htmlInputHidden(dataset));
			writer.write("<table class='results'>");
			retrieveDataset(dataset,writer);
			writer.write("</table>");
		} catch (Exception x) {
			form.removeAll(params.errors.toString());
			form.add(params.errors.toString(),x.getMessage());
		} 		
	}
	
	public int retrieveDataset(String datasetURI,Writer writer) throws Exception {
		return retrieveDataset(datasetURI, writer,"");
	}
	public int retrieveDataset(String datasetURI,Writer writer,String more) throws Exception {
		QueryExecution qe = null;
		OntModel model = null;
		try {
			model = OT.createModel(null,new Reference(datasetURI),MediaType.APPLICATION_RDF_XML);

			Query query = QueryFactory.create(String.format(queryString,more));
			qe = QueryExecutionFactory.create(query,model );
			ResultSet results = qe.execSelect();
			//ResultSetFormatter.out(System.out, results, query);
			writer.write("<table class='resuts'>");
			//http://ambit.uni-plovdiv.bg:8080/ambit2/feature?sameas=http%3A%2F%2Fwww.opentox.org%2Fapi%2F1.1%23ChemicalName
			String compoundURI = null;
			while (results.hasNext()) {
				QuerySolution solution = results.next();
				//DISTINCT ?c ?o ?f ?name ?value ?dataset
				Resource compound = solution.getResource("c");
				if (!compound.getURI().equals(compoundURI)) {
					if (compoundURI != null) writer.write("</table></td></tr>");
					compoundURI = compound.getURI();
					writer.write("<tr>");
					writer.write("<td>");
					writer.write(String.format("<img src='%s?media=%s&w=400&h=400' width='250' height='250' alt='%s'>",
							compoundURI,
							Reference.encode("image/png"),compoundURI));					
					writer.write("</td>");
					writer.write("<td>");
					writer.write("<table>");
				}
				
				writer.write("<tr>");
				writer.write("<th>");
				Resource same = solution.getResource("o");
				writer.write((same!=null)?same.getLocalName():"");
				/*
				Literal name = solution.getLiteral("name");
				
				writer.write("<br>");
				writer.write(name!=null?name.getString():"");
				*/
				writer.write("</th><td>");
				Literal literal = solution.getLiteral("value");
				writer.write(literal!=null?literal.getString():"");
				writer.write("</td></tr>");
				
			}
			if (compoundURI != null) writer.write("</table></td></tr>");
			writer.write("</table>");
			return 1;
		}catch (Exception x) {
			throw x;
		} finally {
			try {qe.close();} catch (Exception x) {}
			try {model.close();} catch (Exception x) {}
		}		
	}
	
	
}
