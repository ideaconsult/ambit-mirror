package ambit2.rest.task.dsl;

import java.io.IOException;
import java.io.Writer;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import ambit2.rest.OpenTox;
import ambit2.rest.rdf.OT;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public class OTDatasetRDFReport extends OTObject {

	protected static String queryCompounds = 
		"PREFIX ot:<http://www.opentox.org/api/1.1#>\n"+
		"	PREFIX ota:<http://www.opentox.org/algorithms.owl#>\n"+
		"	PREFIX owl:<http://www.w3.org/2002/07/owl#>\n"+
		"	PREFIX dc:<http://purl.org/dc/elements/1.1/>\n"+
		"	PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
		"	PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
		"	PREFIX otee:<http://www.opentox.org/echaEndpoints.owl#>\n"+
		"		select DISTINCT ?c ?title ?value\n"+
		"		where {\n"+
		"	     ?dataset ot:dataEntry ?d.\n"+
		"		 ?d rdf:type ot:DataEntry.\n"+
		"	     ?d ot:compound ?c.\n"+
		"	     ?d ot:values ?v.\n"+
		"	     ?v ot:value ?value.\n"+
		"	     ?v ot:feature ?f.\n"+
		"	     ?f ot:hasSource 'Default'\n"+
		"	     {?f dc:title ?title}.\n"+
		" }\n"+
		"	ORDER by ?c ?title";
	

	protected static String queryData = 
		"PREFIX ot:<http://www.opentox.org/api/1.1#>\n"+
		"	PREFIX ota:<http://www.opentox.org/algorithms.owl#>\n"+
		"	PREFIX owl:<http://www.w3.org/2002/07/owl#>\n"+
		"	PREFIX dc:<http://purl.org/dc/elements/1.1/>\n"+
		"	PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
		"	PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
		"	PREFIX otee:<http://www.opentox.org/echaEndpoints.owl#>\n"+
		"		select DISTINCT ?endpoint ?src ?title ?value ?type ?modelName \n"+
		"		where {\n"+
		"	     ?dataset ot:dataEntry ?d.\n"+
		"		 ?d rdf:type ot:DataEntry.\n"+
		"	     ?d ot:compound <%s>.\n"+
		"	     ?d ot:values ?v.\n"+
		"	     ?v ot:value ?value.\n"+
		"	     ?v ot:feature ?f.\n"+
		"	     ?f ot:hasSource ?src.\n"+
		"	     OPTIONAL {?src rdf:type ?type}.\n"+
		"	     OPTIONAL {?src dc:title ?modelName}.\n"+
		"	     {?f dc:title ?title}.\n"+
		"        OPTIONAL {?f owl:sameAs ?endpoint}.\n"+
		" }\n"+
		"	ORDER by ?endpoint ?src ?title";
	
	
	protected Model jenaModel;
	protected String application;
	protected OTDataset dataset;
	protected int page;
	protected int pageSize;
	protected boolean showEndpoints = false;
	protected boolean showFeatures = false;
	
	protected Reference requestref;
	public Reference getRequestref() {
		return requestref;
	}
	public OTDatasetRDFReport setRequestref(Reference requestref) {
		this.requestref = requestref;
		return this;
	}
	protected OTDatasetRDFReport(Reference  uri) {
		super(uri);
		
	}
	

	public static OTDatasetRDFReport report(OTDataset dataset, 
			boolean showEndpoints,
			OTFeatures endpoints, 
			boolean showModels,
			OTFeatures features, 
			String application, 
			int page, 
			int pageSize) throws Exception {
		return new OTDatasetRDFReport(dataset,showEndpoints,endpoints, showModels, features, application,page,pageSize,"/all");
	}	
	
	protected OTDatasetRDFReport(OTDataset dataset,
			boolean showEndpoints,
			OTFeatures endpoints, 
			boolean showModels,
			OTFeatures features, 
			String application, int page, int pageSize, String representation) throws Exception {
		super(getQueryString(dataset, showEndpoints, endpoints, showModels, features, application, page, pageSize, representation));
		this.application = application;
		this.dataset = dataset;
		jenaModel = null;
		this.page = page;
		this.pageSize = pageSize;
		this.showEndpoints = showEndpoints;
		this.showFeatures = showModels;

	}

	protected static String getQueryString(OTDataset dataset, 
			boolean showEndpoints,
			OTFeatures endpoints, 
			boolean showModels,
			OTFeatures features, 
			String application, int page, int pageSize, String representation)  throws Exception {
		
		boolean e = showEndpoints && (endpoints!=null) && (endpoints.size()>0);
		boolean f = showModels && (features!=null) && (features.size()>0);
		return
		String.format("%s/query/compound/url%s?search=%s%s%s%s%s",
				application,
				representation==null?"/all":representation,
				Reference.encode(dataset.getPage(page, pageSize).uri.toString()),
				f?"&":"",
				f?features.getQuery(null).getQueryString():"",
				e?"&":"",
				e?endpoints.getQuery(null).getQueryString():""						
				);

	}
	
	public OTDatasetRDFReport write(Writer writer) throws Exception {
		writer.write(header());
		Reference ref = uri.clone();
		Form form = ref.getQueryAsForm();;
		ref.setQuery("");
		ClientResource client = new ClientResource(ref);
		Representation r = null;
		
		try {
			
			r = client.post(form.getWebRepresentation(),MediaType.APPLICATION_RDF_XML);
			if (client.getStatus().equals(Status.SUCCESS_OK))
				jenaModel = OT.createModel(null,r.getStream(),MediaType.APPLICATION_RDF_XML);
			else throw new ResourceException(client.getStatus(),ref.toString());
			
			//writeData(writer);

		} catch (Exception x) {
			x.printStackTrace();
		} finally {
	
			try {r.release();} catch (Exception x) {}
			try {client.release();} catch (Exception x) {}
		}
	
		
		try { writeIdentifiers(writer); }catch (Exception x) {x.printStackTrace();}
		

		try { writer.write(footer()); }catch (Exception x) {x.printStackTrace();}
		return this;
	}

	protected void writeIdentifiers(Writer writer) throws Exception {
		QueryExecution qe = null;
		try {
			Query query = QueryFactory.create(queryCompounds);
			qe = QueryExecutionFactory.create(query,jenaModel);
			ResultSet results = qe.execSelect();
			String compoundURI = null;
			int records = 0;
			int columns = 0;
			String cmp = null;
			while (results.hasNext()) {
	
				QuerySolution solution = results.next();
				Resource compound = solution.getResource("c");
				String title = getString(solution.get("title"));
				String value = getString(solution.get("value"));		
				
				title = title.replace("http://www.opentox.org/api/1.1#", "");
				compoundURI = compound.getURI();
				if (cmp!= compoundURI) {
					endRow(records,cmp,writer);
					cmp = compoundURI;
					newRow(records,cmp,title,value,writer);
					records++;
					columns = 0;
				}
				else {
					

					rowValue(records, columns, cmp,title,value,writer);
					columns++;
				}
			}
			endRow(records,cmp,writer);
		}catch (Exception x) {
			x.printStackTrace();
			throw x;
		} finally {
			try {qe.close();} catch (Exception x) { x.printStackTrace();}
		}				
	}

	
	public String header() {
		try {
			return	String.format("\n<div id=\"BROWSER\">%s\n",pageNavigator());
		} catch (Exception x) {
			return	String.format("\n<div id=\"BROWSER\">%s\n","");	
		}
	}

	public String footer() {
		return "\n</div><!-- browser -->";
	}

	public void newRow(int row, String compound,String title, String value, Writer writer) throws IOException {
		
		writer.write("\n<table width='100%' border='0'>\n");
		
		writer.write("\n<tr>\n");
		writer.write("\n<td width='20px' align='left' valign='top'>\n");
		writer.write(String.format(
				"<label>%d.</label></td>",
				row+page*pageSize+1));
		writer.write("\n<td width='200px'>\n");
		
		writer.write(String.format(
				"<img src='%s?w=240&h=200&media=image/png' alt='%s'>",
				compound,compound));
		
		writer.write("\n</td><td align='left'>\n");
		

		
		writer.write(String.format("<h3><b>%s</b>&nbsp;<label title='%s'>%s</label></h3>",title,value,value));
		
	}
	
	public void endRow(int row,String compound, Writer writer) throws IOException {
		if (compound==null) return;
		writer.write("\n</tr>\n");
		writer.write("\n<tr><td colspan='3'>\n");
		
		
		try { if (showEndpoints||showFeatures) writeData(row,compound,writer); }catch (Exception x) {x.printStackTrace();}
		writer.write("\n</td></tr></table>\n");
		
	}	
	public void rowValue(int row, int col, String compound, String title, String value, Writer writer) throws IOException {

			writer.write("<b>\n");
			writer.write(title);
			writer.write("</b>&nbsp;&nbsp;");
			writer.write("&nbsp;&nbsp;");
			String v = value;
			//writer.write(String.format("<label title='%s'>%s%s</label>",v,v.length()>100?v.substring(0,100):v,v.length()>100?"...":""));
			writer.write(String.format("<label title='%s'>%s</label>",v,v));
			writer.write("<br>\n");

	}	
	
	public String pageNavigator()  throws Exception  {
		
		StringBuilder b = new StringBuilder();
		//b.append(String.format("\n<input type='hidden' value='%s'/>\n",dataset.uri));
		//b.append(String.format("\n<input type='hidden' value='%s'/>\n",uri));
		
		b.append("<table width='100%' border='0'>");
		b.append("<tr>");
		b.append("<td align='left'>");
		b.append(String.format("\n<a href='#' onClick=\"contentDisp('%s',%d,'%s');\">&laquo;</a>&nbsp;",
				requestref.getBaseRef(),
				page<=1?1:page-1,prev(true)));
		
		for (int i=0; i < page;i++) {
		b.append(String.format("\n<a href='%s' onClick=\"contentDisp('%s',%d,'%s');\">%d</a>&nbsp;",
				"#",
				requestref.getBaseRef(),
				i+1,
				getPage(i, pageSize,true),
				i+1
				));
	   }		
		b.append(String.format("\nPage&nbsp;<label id='page' title='page'>%d</label> Records per page <label id='pagesize' title='items per page'>%d</label> &nbsp;\n<a href='#' onClick=\"contentDisp('%s',%d,'%s');\">&raquo;</a>",
				page+1,pageSize,
				requestref.getBaseRef(),
				page+1,next(true)));
		b.append("</td><td align='right'>");
		
		
		b.append(String.format("\n<a href='%s' onClick=\"contentDisp('%s',%d,'%s');\">%s</a>&nbsp;",
				"#",
				requestref.getBaseRef(),
				page,
				getPage(page, pageSize,true,!showEndpoints,showFeatures),
				String.format("Structure(s) %s %s",
						showFeatures?"& Model predictions":"",
						showEndpoints?"":"& Experimental Data")
				));

		
	
		b.append("</td></tr>");
		b.append("</table>");
		return b.toString();
	}
	public String prev( boolean removeHeader) throws Exception {
		if (page==0) return getRequestref().getQueryAsForm().getQueryString();
		else return getPage(page-1, pageSize,  removeHeader);
	}
	public String next( boolean removeHeader) throws Exception {
		return getPage(page+1, pageSize,  removeHeader);
	}	
	 public String getPage(int page,int pageSize, boolean removeHeader) throws Exception {
		 return getPage(page, pageSize, removeHeader,showEndpoints,showFeatures);
	 }	
	 public String getPage(int page,int pageSize, boolean removeHeader, boolean endpoints, boolean models) throws Exception {
		 Reference ref = getRequestref();
		 Form form = ref.getQueryAsForm();
		 form.removeAll(OpenTox.params.page.toString());
		 form.removeAll(OpenTox.params.pagesize.toString());
		 
		 form.add(OpenTox.params.page.toString(),Integer.toString(page));
		 form.add(OpenTox.params.pagesize.toString(),Integer.toString(pageSize));
		 
		 form.removeAll("endpoints");
		 form.add("endpoints",Boolean.toString(endpoints).toUpperCase());

		 form.removeAll("models");
		 form.add("models",Boolean.toString(models).toUpperCase());		 
		 
		 if (removeHeader) {
			form.removeAll("header");
			form.add("header",Boolean.FALSE.toString());
		 }
		 return form.getQueryString();
	 }		 
	
	 protected String getString(RDFNode node) throws Exception {
		    if (node == null) return "";
			if (node.isLiteral()) return ((Literal)node).getString();
			else if (node.isResource()) return String.format("<a href='%s' target=_blank>%s</a>",((Resource)node).getURI(),((Resource)node).getURI());
			else return node.toString();
	 }
	 protected void writeData(int row,String cmp, Writer writer) throws Exception {
		 	writer.write(String.format("<script type=\"text/javascript\">$(document).ready(function() {  $(\"#data_%s\").tablesorter({widgets: ['zebra'] }); } );</script>",row));	
		    writer.write(String.format("\n<table class=\"tablesorter\" id=\"data_%s\" border=\"0\" cellpadding=\"0\" cellspacing=\"1\">\n",row));
		    writer.write("\n<thead><th>Endpoint</th><th>Dataset/Model</th><th>Name</th><th>Value</th><th>Predicted/Experimental</th></thead>\n");
		    writer.write("\n<tbody>\n");
			QueryExecution qe = null;
			
			try {
				Query query = QueryFactory.create(String.format(queryData,cmp));
				qe = QueryExecutionFactory.create(query,jenaModel);
				ResultSet results = qe.execSelect();
				String oldEndpoint = null;
				while (results.hasNext()) {
		
					QuerySolution solution = results.next();
					String src = getString(solution.get("src"));
					
					if ("Default".equals(src)) continue;
					
					String endpoint = getString(solution.get("endpoint"));
					
					endpoint = endpoint==null?"":endpoint.replace("http://www.opentox.org/echaEndpoints.owl#", "");
					
					if ((oldEndpoint==null) || !oldEndpoint.equals(endpoint)) {
						
						oldEndpoint = endpoint;
						//if (oldEndpoint != null) writer.write("\n</thead>\n<tbody>");
						//writer.write("\n</tbody>\n<thead>\n");
						//writer.write(String.format("\n<tr><th colspan='4'>%s</th></tr>\n",endpoint==null?"":endpoint));
					}
					String title = getString(solution.get("title"));
					String value = getString(solution.get("value"));
					String type = getString(solution.get("type"));	
					
					type = type==null?"":type.replace("http://www.opentox.org/api/1.1#Algorithm", "Predicted").
								replace("http://www.opentox.org/api/1.1#Model","Predicted");


					title = title==null?"":title.replace("http://www.opentox.org/api/1.1#", "");

					String modelName = getString(solution.get("modelName"));	
					
					writer.write(String.format("\n<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>\n",
							endpoint==null?"":endpoint,src,title,value,type));
				}
				//if (oldEndpoint != null) writer.write("\n</tbody>\n");
			}catch (Exception x) {
				x.printStackTrace();
				throw x;
			} finally {
				try {qe.close();} catch (Exception x) { x.printStackTrace();}
			}
			writer.write("\n</tbody>");
			writer.write("\n</table>");
		}

}
