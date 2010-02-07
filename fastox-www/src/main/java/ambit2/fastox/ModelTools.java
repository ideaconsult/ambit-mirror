package ambit2.fastox;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.fastox.steps.FastoxStepResource.params;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class ModelTools {
	
	public static Model retrieveModels(Model rdf,Form form,MediaType mediaType) throws Exception {
		String[] modeluri = form.getValuesArray(params.model.toString());
		for (String uri:modeluri) {
			try {
				if (rdf==null) rdf = ModelFactory.createDefaultModel();
				rdf.read(uri);
				
			} catch (ResourceException x) {
				x.printStackTrace();
	
			} catch (Exception x) {
				x.printStackTrace();
			} finally {
				
			}			
		}
		return  rdf;
	}
	
	public static int renderModels(Model rdf, Form form, Writer writer, boolean status) throws IOException {
		   int running = 0;
		   writer.write(String.format("<h4>%s</h4>","Models"));
			String[] models = form.getValuesArray(params.model.toString());
			if ((models==null) || (models.length==0)) {
				writer.write("<div class='message'>No models</div>");
				return 0;
			}
			try {
				renderRDFModels(rdf, writer, form, status);
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
						writer.write(String.format("<a href='%s'>%s</a>",uri,(isRunning==0)?"Results":"Processing"));
						writer.write(String.format("<input type='hidden' name='%s' value='%s'>", model,uri));	
						writer.write("</td>");					

					}
					writer.write("</tr>");
				}	
				writer.write("</table>");
			}
			return running;
	}	
	
	public static int renderRDFModels(Model rdf,Writer writer, Form form, boolean status) throws Exception {
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
		}		
		return running;
	}		
}
