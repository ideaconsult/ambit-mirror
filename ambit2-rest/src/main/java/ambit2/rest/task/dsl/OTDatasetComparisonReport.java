package ambit2.rest.task.dsl;

import java.io.StringWriter;
import java.io.Writer;

import org.opentox.dsl.OTDataset;
import org.opentox.dsl.OTDatasets;
import org.opentox.dsl.OTObject;
import org.opentox.dsl.OTOntologyService;
import org.restlet.data.Reference;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public class OTDatasetComparisonReport extends OTObject {
	protected Reference ontology_service;
	protected Reference application;
	protected OTDatasets datasets1;
	protected OTDatasets datasets2; 
	protected Reference datasetReportRef;
	public Reference getDatasetReportRef() {
		return datasetReportRef;
	}
	public void setDatasetReportRef(Reference datasetReportRef) {
		this.datasetReportRef = datasetReportRef;
	}
	protected static String queryDatasets = 
	"PREFIX ot:<http://www.opentox.org/api/1.1#>\n"+
	"PREFIX owl:<http://www.w3.org/2002/07/owl#>\n"+
	"PREFIX dc:<http://purl.org/dc/elements/1.1/>\n"+
	"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
	"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
	"		select ?Dataset ?Title ?Source\n"+
	"		where {\n"+
	"		?Dataset rdf:type ot:Dataset.\n"+
	"	   OPTIONAL {?Dataset dc:title ?Title}.\n"+
	"	   OPTIONAL {?Dataset dc:source ?Source}.\n"+
	"}\n"+
	"      ORDER by ?Dataset\n";

	public static OTDatasetComparisonReport report(Reference application, OTDataset dataset) throws Exception {
		return new OTDatasetComparisonReport(application,dataset);
	}	
	public OTDatasetComparisonReport(Reference application, OTDataset dataset) throws Exception {
		super();
		this.application = application;
		if ((dataset!=null) && (dataset.getUri()!=null)) {
			datasets2 = OTDatasets.datasets();
			datasets2.add(dataset);
		}
	}
	
	 public OTDatasetComparisonReport withOntologyService(String uri) throws Exception { 
		  this.ontology_service = new Reference(uri);
		  this.ontology_service.setQuery("");
		  return this; 
	 }	
	 public String report(String ontologyURI) throws Exception  { 
		 try {
			 datasets1 = withOntologyService(ontologyURI).retrieveDatasets();
			 StringWriter writer = new StringWriter();
			 report(writer,datasets1,datasets2==null?datasets1:datasets2);
			 return writer.toString();
		 } catch (Exception x) {
			 throw x;
		 }
	 }
	 
	 protected void report(Writer writer, OTDatasets datasets1, OTDatasets datasets2) throws Exception {
			writer.write(String.format("<script type=\"text/javascript\">$(document).ready(function() {  $(\"#datacomparison\").tablesorter({widgets: ['zebra'] }); } );</script>"));
			writer.write("<table class='tablesorter' id='datacomparison' border=\"0\" cellpadding=\"0\" cellspacing=\"1\">");
			writer.write("<thead><tr><th width='30%'>Number of compounds</th>");
			for (int i=0; i < datasets2.size();i++) {
				OTDataset dataset2 = datasets2.getItem(i);
				writer.write(String.format("<th>%d.&nbsp;%s</th>",i+1,
							dataset2.getName()==null?dataset2.getUri():dataset2.getName()
											));
			}
			writer.write("</tr></thead><tbody>");
			
			String[][] count = new String[datasets1.size()][datasets2.size()];
			for (int i=0; i < datasets1.size();i++) {
				OTDataset dataset1 = datasets1.getItem(i);
				writer.write("\n<tr>");
				writer.write(String.format("\n<th align='left'>&nbsp;%d.<a href='%s'>%s</a></th>",i+1,dataset1.getUri(),
						dataset1.getName()==null?dataset1.getUri():dataset1.getName()));
				for (int j=0; j < datasets2.size();j++) {
					OTDataset dataset2 = datasets2.getItem(j);
					boolean same= dataset1.getUri().equals(dataset2.getUri());
					Reference link;
					Reference link_count=null;
					if (dataset1.getUri().equals(dataset2.getUri())) { //same dataset
						//http://apps.ideaconsult.net:8080/ambit2/stats/chemicals_in_dataset?dataset_uri=http://apps.ideaconsult.net:8080/ambit2/dataset/1
						link_count = new Reference(String.format("%s/stats/chemicals_in_dataset",application));
						link_count.addQueryParameter("dataset_uri",dataset1.getUri().toString());

						OTObject o = OTObject.object(link_count.toString()).readTextLineAsName();
						count[i][j] = o.getName();
						link = dataset1.getUri().clone();
						link.addQueryParameter("page", "0");
						link.addQueryParameter("pagesize", "25");
						
					} else  {
						//if (count[i][j] == null) {
							link_count = new Reference(String.format("%s/stats",application));
							link_count.addQueryParameter("dataset_uri",dataset1.getUri().toString());
							link_count.addQueryParameter("dataset_uri",dataset2.getUri().toString());
	
						//	OTObject o = OTObject.object(link_count.toString()).readTextLineAsName();
						//	count[i][j] = o.getName();
						//} 
	
						
						link = dataset1.getUri().clone();
						link.addQueryParameter("common",dataset2.getUri().toString());	
						link.addQueryParameter("page", "0");
						link.addQueryParameter("pagesize", "25");						
					}

						
						/*
						String link = String.format("onClick=\"contentDisp('%s',%d,'%s');\"",
								datasetReportRef,
								1,
								ref);
								*/
					/*		
					writer.write(String.format("\n<t%s><a href='%s'  title='Common structures \"%s\" and \"%s\"' target='_blank'>%s</a></t%s>",
								same?"h":"d",
								link,
								dataset1.name==null?dataset1.uri:dataset1.name,
								dataset2.name==null?dataset2.uri:dataset2.name,
								count[i][j],
								same?"h":"d"));
					*/
					
					String cell = String.format("td%d_%d",i,j);
					writer.write(String.format(
							"<script type=\"text/javascript\">$(document).ready(function() {  stats(\"%s\",\"#%s\"); } );</script>",
							link_count,
							cell));					
					writer.write(String.format("\n<t%s><a href='%s' title='Common structures \"%s\" and \"%s\"' target='_blank'><label id='%s'>#</label></a>",
								same?"h":"d",
								link,
								dataset1.getUri(),
								dataset2.getUri(),
								cell));

					writer.write(String.format("</t%s>",same?"h":"d"));
					//}
				}
				writer.write("\n</tr>\n");
			}
			writer.write("</tbody></table>");
		}	 
	 /*
	protected void report(Writer writer) throws Exception {
		writer.write(String.format("<script type=\"text/javascript\">$(document).ready(function() {  $(\"#datacomparison\").tablesorter({widgets: ['zebra'] }); } );</script>"));
		writer.write("<table class='tablesorter' id='datacomparison' border=\"0\" cellpadding=\"0\" cellspacing=\"1\">");
		writer.write("<thead><tr><th>Number of compounds</th>");
		for (int i=0; i < datasets.size();i++) {
			OTDataset dataset1 = datasets.getItem(i);
			writer.write(String.format("<th>%d.<a href='%s' title='%s'>%s</a></th>",i+1,dataset1.getUri(),dataset1.getName(),dataset1.getName().substring(0,6)));
		}
		writer.write("</tr></thead><tbody>");
		
		String[][] count = new String[datasets.size()][datasets.size()];
		for (int i=0; i < datasets.size();i++) {
			OTDataset dataset1 = datasets.getItem(i);
			writer.write("<tr>");
			writer.write(String.format("<th align='left'>&nbsp;%d.<a href='%s'>%s</a></th>",i+1,dataset1.uri,dataset1.getName()));
			for (int j=0; j < datasets.size();j++) {
				OTDataset dataset2 = datasets.getItem(j);
				if (count[i][j] == null) {
					Reference ref = new Reference(String.format("%s/stats",application));
					ref.addQueryParameter("dataset_uri",dataset1.uri.toString());
					ref.addQueryParameter("dataset_uri",dataset2.uri.toString());

					OTObject o = OTObject.object(ref.toString()).readTextLineAsName();
					count[i][j] = o.getName();
				} 
				Reference ref = dataset1.uri.clone();
				ref.addQueryParameter("intersection",dataset2.uri.toString());
				writer.write(String.format("<t%s><a href='%s' title='Common structures \"%s\" and \"%s\"' target='_blank'>%s</a></t%s>",
						j==i?"h":"d",
						ref,
						dataset1.name,
						dataset2.name,
						count[i][j],j==i?"h":"d"));
			}
			writer.write("</tr>");
		}
		writer.write("</tbody></table>");
	}
	*/
	
	 public static String getString(RDFNode node) throws Exception {
		    if (node == null) return "";
			if (node.isLiteral()) return ((Literal)node).getString();
			else if (node.isResource()) return ((Resource)node).getURI();
			//String.format("<a href='%s' target=_blank>%s</a>",((Resource)node).getURI(),((Resource)node).getURI());
			else return node.toString();
	 }
	protected OTDatasets retrieveDatasets() throws Exception {
		 final OTDatasets datasets = OTDatasets.datasets();
		 OTOntologyService<String> ontology = new OTOntologyService<String>(ontology_service) {
			 @Override
			public String createObject() throws Exception {
				return "";
			}
			 @Override
			public String processSolution(QuerySolution solution) throws Exception {
					String title = getString(solution.get("Title"));
					String uri = getString(solution.get("Dataset"));		
					datasets.add(OTDataset.dataset(uri).withName(title));
					return "";
			}
		 };
		 datasets.clear();
		 ontology.query(queryDatasets);
		 return datasets;
	}	
}
