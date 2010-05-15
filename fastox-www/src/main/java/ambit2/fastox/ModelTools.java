package ambit2.fastox;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.fastox.steps.FastoxStepResource.params;
import ambit2.fastox.users.IToxPredictSession;
import ambit2.fastox.wizard.WizardResource;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.task.RemoteTask;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
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
		"select DISTINCT ?endpoint ?endpointName %s ?title ?TrainingDataset ?algorithm ?algName\n"+
		"where {\n"+
		"%s rdf:type ot:Model.\n"+
		"OPTIONAL {%s dc:title ?title}.\n"+
		"OPTIONAL {%s ot:trainingDataset ?TrainingDataset}.\n"+
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
	public static Model retrieveModels(Model rdf,IToxPredictSession session,MediaType mediaType) throws Exception {
		Iterator<String> models = session.getModels();
		if (models != null)
		while (models.hasNext()) {
			String uri = models.next();
			try {
				//System.out.println(uri);
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
		writer.write(WizardResource.jsTableSorter("models","mpager"));
		writer.write("<table class='tablesorter' id='models' border='0' cellpadding='0' cellspacing='1'>");
		writer.write("<thead><tr>");
		writer.write("<th width='25%'>Model</th>");
		writer.write("<th width='15%'>Endpoint</th>");
		writer.write("<th>Training dataset</th>");
		writer.write("<th>Algorithm</th>");
		/*	
		writer.write("<th></th>");
		*/
		writer.write(String.format("<th>%s</th>",status?"Status":""));
		writer.write("</tr></thead><tbody>");
	}
	
	public static int renderModelTableRow(String modelURI,Writer writer, QuerySolution solution,
			 IToxPredictSession session,
			 boolean status,Reference rootReference, int record)  throws IOException {
		int running = 0;
		RDFNode url = solution.get("url");
		String modelUri = url==null?modelURI:url.isResource()?((Resource) url).getURI():url.toString();
		if (modelUri==null) return 0;

		RDFNode algo = solution.getResource("algorithm");
		Literal algName = solution.getLiteral("algName");
			
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
		writer.write(String.format("<a href='%s?media=%s' alt='%s' title='%s' target='_blank' ><img src='%s/images/chart_line.png' border='0' alt='%s' title='%s'></a>",
				modelUri,
				Reference.encode(MediaType.APPLICATION_RDF_XML.toString()),
				modelUri,modelUri,rootReference.toString(),modelUri,modelUri));				
		writer.write(params.model.htmlInputCheckbox(modelUri,name==null?modelUri:name.getString(),selected));
		
		writer.write("</td>");
		writer.write("<td>");
		writer.write(endpointName==null?"":endpointName.getString());
		writer.write("</td>");
		writer.write("<td>"); //training dataset

		if (dataset!=null)  
			if (dataset.isURIResource()) {
			for (int i=0;i<mimes.length;i++) {
				MediaType mime = mimes[i];
				writer.write("&nbsp;");
				writer.write(String.format(
						"<a href=\"%s?media=%s\" ><img src=\"%s/images/%s\" alt=\"%s\" title=\"%s\" border=\"0\"/></a>",
						dataset.getURI(),
						Reference.encode(mime.toString()),
						rootReference,
						image[i],
						mime,
						mimesDescription[i]));	
			}
			} else writer.write(dataset.toString());
		

		writer.write("</td><td>"); //algorithm
		//?media makes use of restlet Tunnel service
		writer.write(algo==null?"":algo.isURIResource()?
				String.format("<a href='%s?media=%s' target=_blank'>%s</a>",
						((Resource)algo).getURI(),Reference.encode(MediaType.APPLICATION_RDF_XML.toString()),
						algName==null?"Algorithm":algName.getString())
				:algo.toString());		
		writer.write("</td>");
		
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
}
