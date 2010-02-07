package ambit2.fastox;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.fastox.steps.FastoxStepResource.params;
import ambit2.rest.ChemicalMediaType;

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
	public static final MediaType[] mimes = {
		ChemicalMediaType.CHEMICAL_MDLSDF,
		ChemicalMediaType.CHEMICAL_CML,
		ChemicalMediaType.CHEMICAL_SMILES,					
		MediaType.APPLICATION_PDF,
		MediaType.TEXT_CSV,
		ChemicalMediaType.WEKA_ARFF,
		MediaType.APPLICATION_RDF_XML
		};
		public static final String[] image = {
				"sdf.jpg",
				"cml.jpg",
				"smi.png",					
				"pdf.png",
				"excel.png",
				"weka.jpg",
				"rdf.gif"
				
		};
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
	
	public static int renderModels(Model rdf, Form form, Writer writer, boolean status,Reference rootReference) throws IOException {
		   int running = 0;
			String[] models = form.getValuesArray(params.model.toString());
			if ((models==null) || (models.length==0)) {
				writer.write("<div class='message'>No models</div>");
				return 0;
			}
			try {
				renderRDFModels(rdf, writer, form, status,rootReference);
			} catch (Exception x) {
				x.printStackTrace();
				writer.write("<table class='models'>");
				writer.write("<tr><th align='left'>Model</th>");
				if (status)	writer.write("<th align='left'>Status</th><th align='left'>Results</th>");
				writer.write("</tr>");				
				for (String model:models) {
					writer.write("<tr>");
					writer.write("<td>");
					writer.write(String.format("<a href='%s' alt='%s' title='%s' target='_blank'><img border='0' src='%s/images/chart_line.png' alt='%s' alt='%s'></a>",
							model,model,model,rootReference.toString(),model,model));
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
						writer.write(String.format("<a href='%s'><img src='%s/images/%s' alt='%s' title='%s'></a>",
								uri,
								rootReference.toString(),
								(isRunning==0)?"tick.png":"24x24_ambit.png",
								(isRunning==0)?"Completed":"Processing",
								(isRunning==0)?"Completed":"Processing")
								);
						writer.write(String.format("<input type='hidden' name='%s' value='%s'>", model,uri));	
						writer.write("</td>");					

					}
					writer.write("</tr>");
				}	
				writer.write("</table>");
			}
			return running;
	}	
	
	public static int renderRDFModels(Model rdf,Writer writer, Form form, boolean status,Reference rootReference) throws Exception {
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
			writer.write("<table class='results'>");
			writer.write("<tr>");
			writer.write("<th>Model</th>");
			writer.write("<th>Creator</th>");
			writer.write("<th>Algorithm</th>");
			writer.write("<th></th>");
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
				writer.write("<tr class='results_even'>");					
				writer.write("<td>");
				writer.write(String.format("<a href='%s' alt='%s' title='%s' target='_blank' ><img src='%s/images/chart_line.png' border='0' alt='%s' title='%s'></a>",
						modelUri,modelUri,modelUri,rootReference.toString(),modelUri,modelUri));				
				writer.write(params.model.htmlInputCheckbox(modelUri,name==null?modelUri:name.getString()));
				writer.write("</td><td>");
				writer.write(creator==null?"":creator.getString());
				writer.write("</td><td>");
				writer.write(algo==null?"":algo.getURI());
				writer.write("</td>");
				writer.write("<td>");
				/**
				 * Results URL and status
				 */
				String[] uris = form.getValuesArray(modelUri);
				if (!status)  {
					for (String uri:uris) {
						if ("on".equals(uri)) continue;
						
						String q= new Reference(uri).getQuery();
						for (int i=0;i<mimes.length;i++) {
							MediaType mime = mimes[i];
							writer.write("&nbsp;");
							writer.write(String.format(
									"<a href=\"%s%smedia=%s\"  ><img src=\"%s/images/%s\" alt=\"%s\" title=\"%s\" border=\"0\"/></a>",
									uri,
									q==null?"?":"&",
									Reference.encode(mime.toString()),
									rootReference,
									image[i],
									mime,
									mime));	
						}			
					
						//writer.write(String.format("<a href='%s' target='_blank'>Save</a>", uri));
					}
					continue;
				}
				writer.write("</td>");
				
				for (String uri:uris) {
					if ("on".equals(uri)) continue;
					String[] tasks = form.getValuesArray(uri);
					writer.write("<td>");
					int isRunning = 0;
					for (String task:tasks) {
						if (task.equals(Status.SUCCESS_ACCEPTED.getName()) || task.equals(Status.REDIRECTION_SEE_OTHER.getName()))
							isRunning++;
						writer.write(String.format("%s",task.equals("OK")?"Completed":"Processing"));
					}
					running += isRunning;
					writer.write("</td>");
					writer.write("<td>");
					writer.write(String.format("<a href='%s'><img src='%s/images/%s' border='0' alt='%s' title='%s'></a>",
							uri,
							rootReference.toString(),
							(isRunning==0)?"tick.png":"24x24_ambit.gif",
							(isRunning==0)?"Completed":"Processing",
							(isRunning==0)?"Completed":"Processing"
								)
							);
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
