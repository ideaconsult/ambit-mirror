package ambit2.fastox;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.opentox.dsl.task.RemoteTask;
import org.opentox.rdf.OT;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.fastox.steps.FastoxStepResource.params;
import ambit2.fastox.users.IToxPredictSession;
import ambit2.fastox.wizard.WizardResource;
import ambit2.rest.ChemicalMediaType;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public class ModelTools {
	public static final String sparql = 
		"PREFIX ot:<http://www.opentox.org/api/1.1#>\n"+
		"PREFIX ota:<http://www.opentox.org/algorithms.owl#>\n"+
		"PREFIX owl:<http://www.w3.org/2002/07/owl#>\n"+
		"PREFIX dc:<http://purl.org/dc/elements/1.1/>\n"+
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
		"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
		"PREFIX otee:<http://www.opentox.org/echaEndpoints.owl#>\n"+
		"select DISTINCT ?endpoint ?endpointName %s ?title ?validation ?algorithm ?algName\n"+
		"where {\n"+
		"%s rdf:type ot:Model.\n"+
		"OPTIONAL {%s dc:title ?title}.\n"+
		"OPTIONAL {?validation ot:validationModel %s}.\n"+
		"OPTIONAL {%s ot:algorithm ?algorithm}.\n"+
		"OPTIONAL {?algorithm dc:title ?algName}.\n"+		
		"OPTIONAL {?algorithm ot:isA ?type}.\n"+
		"OPTIONAL { {\n"+
		"{ %s ot:dependentVariables ?vars. } UNION { %s ot:predictedVariables ?vars. }\n"+
		"}\n"+
		"{?vars owl:sameAs ?endpoint}.\n"+
		"{?endpoint dc:title ?endpointName}.\n"+
		"}\n"+
		"}\n"+		
		"ORDER BY ?endpointName %s\n";
	

	
	public static final String queryCount = 
		"PREFIX ot:<http://www.opentox.org/api/1.1#>\n"+
		"PREFIX owl:<http://www.w3.org/2002/07/owl#>\n"+
		"PREFIX dc:<http://purl.org/dc/elements/1.1/>\n"+
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
		"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
		"		select count(distinct ?o)\n"+
		"		where {\n"+
		"		?o rdf:type %s.\n"+
		"		}";

	public static final String queryCountEndpoint =
		"PREFIX ot:<http://www.opentox.org/api/1.1#>\n"+
		"PREFIX owl:<http://www.w3.org/2002/07/owl#>\n"+
		"PREFIX dc:<http://purl.org/dc/elements/1.1/>\n"+
		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
		"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
		"		select count(distinct ?endpoint)\n"+
		"		where {\n"+
		"		?o rdf:type ot:Model.\n"+
		"{ {\n"+
		"{?o ot:dependentVariables ?vars. } UNION { ?o ot:predictedVariables ?vars. }\n"+
		"  }\n"+
		" {?vars owl:sameAs ?endpoint}.\n"+
		"		}.\n"+
		"}	\n";
	
	public static final MediaType[] mimes = {
		ChemicalMediaType.CHEMICAL_MDLSDF,
		ChemicalMediaType.CHEMICAL_CML,
		ChemicalMediaType.CHEMICAL_SMILES,					
		MediaType.APPLICATION_PDF,
		MediaType.TEXT_CSV,
		ChemicalMediaType.WEKA_ARFF,
		MediaType.APPLICATION_RDF_XML,
		MediaType.TEXT_RDF_N3,
		};
	
	public static final String[] mimesDescription = {
		"SDF file",
		"CML file",
		"SMILES",					
		"PDF *.pdf",
		"Comma delimited *.csv",
		"Weka ARFF file *.arff",
		"W3C Resource Description Framework (RDF/XML)",
		"W3C Resource Description Framework (RDF N3)",
		};	
		public static final String[] image = {
				"sdf.jpg",
				"cml.jpg",
				"smi.png",					
				"pdf.png",
				"excel.png",
				"weka.jpg",
				"rdf.gif",
				"rdf.gif"
				
		};
	public static OntModel retrieveModels(OntModel rdf,IToxPredictSession session,MediaType mediaType) throws Exception {
		Iterator<String> models = session.getModels();
		if (models != null)
		while (models.hasNext()) {
			String uri = models.next();
			try {
				//System.out.println(uri);
				if (rdf==null) rdf = OT.createModel();
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
	/*
	public static int renderModels(Model rdf, IToxPredictSession session, Writer writer, boolean status,Reference rootReference) throws IOException {
		   int running = 0;
			
			if (session.getNumberOfModels() == 0) {
				writer.write("<div class='message'>No models</div>");
				return 0;
			} 

			try {
				renderRDFModels(rdf, writer, session ,status,rootReference);
			} catch (Exception x) {
				writer.write(x.getLocalizedMessage());
				x.printStackTrace();
				writer.write("<table class='models'>");
				writer.write("<tr><th align='left'>Model</th>");
				if (status)	writer.write("<th align='left'>Status</th><th align='left'>Results</th>");
				writer.write("</tr>");		
				
				
				Iterator<String> models = session.getModels();
				while (models.hasNext()) {
					writer.write("<tr><td>");
					writer.write(models.next());

					writer.write("</td></tr>");
				}	
				writer.write("</table>");
			}
			return running;
	}	
	*/
	public static void renderModelTableCaption(Writer writer,boolean status) throws IOException {
		writer.write("<br style='clear:both;' clear='all' />\n"); // Safari is not happy otherwise with floating elements
		writer.write(WizardResource.jsTableSorter("models"));
		writer.write("<table class='tablesorter' id='models' border='0' cellpadding='0' cellspacing='1'>");
		writer.write("<thead><tr>");
		writer.write("<th width='25%'>Model</th>");
		writer.write("<th width='15%'>Endpoint</th>");
		writer.write("<th>Algorithm</th>");		
		writer.write("<th>Validation</th>");

		/*	
		writer.write("<th></th>");
		*/
		writer.write(String.format("<th>%s</th>",status?"Status":""));
		writer.write("</tr></thead><tbody>");
	}
	 protected static String getString(RDFNode node)  {
		    if (node == null) return "";
			if (node.isLiteral()) return ((Literal)node).getString();
			else if (node.isResource()) return ((Resource)node).getURI();
			else if (node.isAnon()) return null;
			else return node.toString();
	 }

	public static int renderModelTableRow(String modelURI,Writer writer, QuerySolution solution,
			 IToxPredictSession session,
			 boolean status,Reference rootReference, int record)  throws IOException {
		int running = 0;
		RDFNode url = solution.get("url");
		String modelUri = url==null?modelURI:url.isResource()?((Resource) url).getURI():url.toString();
		if (modelUri==null) return 0;

		RDFNode algo = solution.getResource("algorithm");
		
		String algName = getString(solution.get("algName"));
		
		
		Literal name = solution.getLiteral("title");
		Resource dataset = solution.getResource("TrainingDataset");
		
		//Resource endpoint = solution.getResource("endpoint");
		Literal endpointName = solution.getLiteral("endpointName");

		//add the model
		boolean selected = false; 
		if (modelUri!=null)	{
			if (session.getModelStatus(modelUri) == null) {
				session.addModel(modelUri,Boolean.FALSE);
			} else {
				if (session.getModelStatus(modelUri) instanceof RemoteTask)
					selected = true;
				else if (session.getModelStatus(modelUri) instanceof Boolean)
					selected = ((Boolean) session.getModelStatus(modelUri)).booleanValue();
			}
	
		}
		

		writer.write("<td>");
		
		
		writer.write(params.model.htmlInputCheckbox(modelUri,
				jsJumpTo(
						String.format("%s/user/%s/report/Model?search=%s",
						rootReference,
						session.getUser().getId(),
						Reference.encode(modelUri)),	
						name==null?modelUri:name.getString())
				//jsJumpTo(String.format("%s?media=text/plain",modelUri), name==null?modelUri:name.getString())
				
				,selected));
			
		


		
		writer.write("</td>");
		writer.write("<td>");
		writer.write(endpointName==null?"":endpointName.getString());
		writer.write("</td>");
		
		writer.write("<td>"); //algorithm
		//?media makes use of restlet Tunnel service
		if (algo!= null)
		writer.write(jsJumpTo(
				String.format("%s/user/%s/report/Algorithm?search=%s",
				rootReference,
				session.getUser().getId(),
				Reference.encode(algo.isURIResource()?((Resource)algo).getURI():algo.toString()))
				,algName!=null?algName:"Algorithm"));


		writer.write("</td>");
		
		writer.write("<td>"); //validation
		
			RDFNode node = solution.get("validation");
			if (node != null)
				if (node.isURIResource())
					writer.write(jsJumpTo(
							String.format("%s/user/%s/report/Validation?search=%s",
							rootReference,
							session.getUser().getId(),
							Reference.encode(((Resource)node).getURI()))
							,"Model validation report"));
		
		writer.write("</td>"); //validation

		
		Object uris = session.getModelStatus(modelUri);
		/*
		writer.write("<td>");

		
		if (!status)  {
			if ((uris != null) && (uris instanceof RemoteTask)) {
				RemoteTask task = ((RemoteTask)uris);
				Reference uri = task.getResult();
				
				String q= uri==null?null:uri.getQuery();
				if (q!=null)
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
							mimesDescription[i]));	
				}
			}
		}
		writer.write("</td>");
		*/
		if (uris instanceof RemoteTask)  {
			
			RemoteTask task = ((RemoteTask) uris);
			
			int isRunning = 0;
			isRunning += task.isDone()?0:1;
			writer.write("<td>");
			writer.write((isRunning==0)?
					(task.getStatus().equals(Status.SUCCESS_OK))?"Completed":task.getStatus().toString():
					"Processing");

			running += isRunning;

			writer.write(String.format("<a href='%s'><img src='%s/images/%s' border='0' alt='%s' title='%s'></a>",
					task.getResult(),
					rootReference.toString(),
					(isRunning==0)?"tick.png":"24x24_ambit.gif",
					(isRunning==0)?"Completed":"Processing",
					(isRunning==0)?"Completed":"Processing"
						)
					);

			writer.write("</td>");					

		}	else 	writer.write("<td></td>");		
		return running;
	}
		
	public static String getDownloadURI(IToxPredictSession session,String modelUri, MediaType mediaType,Reference rootReference) {
		Object uris = session.getModelStatus(modelUri);
		
			if ((uris != null) && (uris instanceof RemoteTask)) {
				RemoteTask task = ((RemoteTask)uris);
				Reference uri = task.getResult();
				
				String q= uri==null?null:uri.getQuery();
				if (q!=null)
				for (int i=0;i<mimes.length;i++) {
					if (mimes[i].equals(mediaType))
					return
					String.format(
							"<a href=\"%s%saccept-header=%s\"  ><img src=\"%s/images/%s\" alt=\"%s\" title=\"%s\" border=\"0\"/></a>",
							uri,
							q==null?"?":"&",
							Reference.encode(mimes[i].toString()),
							rootReference,
							image[i],
							mimes[i],
							mimesDescription[i]);	
				}
			} 
			return null;
	}
	
	public static String getDatasetDownloadUri(IToxPredictSession session,Reference dataset, 
					MediaType mediaType,Reference rootReference) {
		


				if (dataset!=null)
				for (int i=0;i<mimes.length;i++) {
					Reference d = dataset.clone();
					d.addQueryParameter("accept-header", mimes[i].toString());
					if (mimes[i].equals(mediaType))
					return
					String.format(
							"<a href='%s'  ><img src=\"%s/images/%s\" alt=\"%s\" title=\"%s\" border=\"0\"/></a>",
							d,
							rootReference,
							image[i],
							mimes[i],
							mimesDescription[i]);	
				}

			return null;
	}
	
	protected static String jsJumpTo(String uri,String title) {
		return String.format("<a href=\"javascript:jumpto('%s')\">%s</a>",uri,title);
	}
	public static String jsIFrame() {
		return
		"<script language=\"javascript\">\n"+
		"var displaymode=0\n"+
		"var iframecode='<iframe id=\"external\" style=\"width:95%;height:200px;border:none\" src=\"\"></iframe>'\n"+
		"if (displaymode==0) document.write(iframecode)\n"+
		"function jumpto(inputurl){\n"+
		"if (document.getElementById&&displaymode==0)\n"+
		"document.getElementById(\"external\").src=inputurl\n"+
		"else if (document.all&&displaymode==0)\n"+
		"document.all.external.src=inputurl\n"+
		"else{\n"+
		"if (!window.win2||win2.closed)\n"+
		"win2=window.open(inputurl)\n"+
		"else{\n"+
		"win2.location=inputurl\n"+
		"win2.focus()}}}		//-->	</script>\n";
	}
}
