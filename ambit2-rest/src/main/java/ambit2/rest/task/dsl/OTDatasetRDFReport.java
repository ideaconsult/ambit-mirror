package ambit2.rest.task.dsl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import net.idea.restnet.c.task.ClientResourceWrapper;
import net.idea.restnet.rdf.ns.OT;

import org.opentox.dsl.OTDataset;
import org.opentox.dsl.OTFeatures;
import org.opentox.dsl.OTObject;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.io.DownloadTool;
import ambit2.rest.OpenTox;

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
		"		 OPTIONAL {\n"+
		"	     ?d ot:values ?v.\n"+
		"	     ?v ot:value ?value.\n"+
		"	     ?v ot:feature ?f.\n"+
		"	     ?f ot:hasSource 'Default'.\n"+
		"	     ?f dc:title ?title.\n"+
		"        }"+
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
	
	
	protected static String queryPredictedFeatures = 
		"PREFIX ot:<http://www.opentox.org/api/1.1#>\n"+
		"	PREFIX ota:<http://www.opentox.org/algorithms.owl#>\n"+
		"	PREFIX owl:<http://www.w3.org/2002/07/owl#>\n"+
		"	PREFIX dc:<http://purl.org/dc/elements/1.1/>\n"+
		"	PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
		"	PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
		"	PREFIX otee:<http://www.opentox.org/echaEndpoints.owl#>\n"+
		"		select DISTINCT ?endpoint ?endpointName ?Model ?mname ?f ?o ?value ?otitle\n"+
		"		where {\n"+
		"	     ?dataset ot:dataEntry ?d.\n"+
		"		 ?d rdf:type ot:DataEntry.\n"+
		"	     ?d ot:compound <%s>.\n"+
		"	     ?d ot:values ?v.\n"+
		"	     ?v ot:value ?value.\n"+
		"	     ?v ot:feature ?f.\n"+
		"	     ?Model rdf:type ot:Model.\n"+
		"	     ?f owl:sameAs ?o.\n"+		
		//"	     ?Model ot:predictedVariables ?f\n"+
		"	     OPTIONAL {?f dc:title ?otitle}.\n"+
		"		 OPTIONAL {?Model dc:title ?mname.}\n"+
		"{\n"+
		"{ ?Model ot:dependentVariables ?f. } UNION { ?Model ot:predictedVariables ?f. }\n"+
		"}\n"+		
		"        OPTIONAL {?f owl:sameAs ?endpoint}.\n"+
		"        OPTIONAL {?endpoint dc:title ?endpointName}.\n"+		
		" }\n"+
		"	ORDER by ?endpoint ?Model ?f";	
	
	protected Model jenaModel;
	public Model getJenaModel() {
		return jenaModel;
	}
	public void setJenaModel(Model jenaModel) {
		this.jenaModel = jenaModel;
	}
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

	public static String getQueryString(OTDataset dataset, 
			boolean showEndpoints,
			OTFeatures endpoints, 
			boolean showModels,
			OTFeatures features, 
			String application, int page, int pageSize, String representation)  throws Exception {
		
		boolean e = showEndpoints && (endpoints!=null) && (endpoints.size()>0);
		boolean f = showModels && (features!=null) && (features.size()>0);
		return
		String.format("%s/query/compound/url%s?search=%s%s%s%s%s&rdfwriter=jena",
				application,
				representation==null?"/names":representation,
				Reference.encode(dataset.getPage(page, pageSize).getUri().toString()),
				f?"&":"",
				f?features.getQuery(null).getQueryString():"",
				e?"&":"",
				e?endpoints.getQuery(null).getQueryString():""						
				);

	}
	
	public OTDatasetRDFReport write(Writer writer) throws Exception {
		writer.write(header());
		Reference ref = getUri().clone();
		Form form = ref.getQueryAsForm();;
		ref.setQuery("");
		ClientResourceWrapper client = new ClientResourceWrapper(ref);
		Representation r = null;
		
		try {

			r = client.post(form.getWebRepresentation(),MediaType.APPLICATION_RDF_XML);
			if (client.getStatus().equals(Status.SUCCESS_OK))
				jenaModel = OT.createModel(jenaModel,r.getStream(),MediaType.APPLICATION_RDF_XML);
			else throw new ResourceException(client.getStatus(),ref.toString());
			
			//writeData(writer);

		} catch (Exception x) {
			throw x;
		} finally {
	
			try {r.release();} catch (Exception x) {}
			try {client.release();} catch (Exception x) {}
		}
	
		if (jenaModel==null) throw  new ResourceException(Status.CLIENT_ERROR_NOT_FOUND,ref.toString());
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
				
				if (title.equals(Property.opentox_IupacName)) title = "IUPAC name";
				else if (title.equals(Property.opentox_Name)) title = "Synonym(s)";
				else title = title.replace("http://www.opentox.org/api/1.1#", "");
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
				"<img src='%s?w=240&h=200&media=image/png' alt='%s' id='%s'>",
				compound,compound,compound));
		
		writer.write("\n</td><td align='left'>\n");
		

		
		writer.write(String.format("<h3><b>%s</b>&nbsp;<label title='%s'>%s</label></h3>",title,value,value));
		
	}
	
	public void endRow(int row,String compound, Writer writer) throws IOException {
		if (compound==null) return;
		writer.write("\n</tr>\n");
		writer.write("\n<tr><td colspan='3'>\n");
		
			

		if (showEndpoints) 
			try  {writeData(row,compound,writer); } catch (Exception x) {x.printStackTrace();}
		else if (showFeatures)
			try { writeModels(row,compound,writer); } catch (Exception x) {x.printStackTrace();}
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
		
		System.out.println("--------------"+ getClass().getName() + " " + requestref.getBaseRef() + " " + requestref);
		b.append(String.format("\n<a href='%s' onClick=\"contentDisp('%s',%d,'%s');\">%s</a>&nbsp;",
				"#",
				requestref.getBaseRef(),
				page,
				getPage(page, pageSize,true,!showEndpoints,showFeatures),
				String.format("Structure(s) %s %s",
						showFeatures?"& Model predictions":"",
						showEndpoints?"":"& Experimental Data")
				));

		b.append(String.format("\n&nbsp;<a href=\"%s?media=chemical/x-mdl-sdf&%s\" title='Download as SDF'>SDF</a>",
				requestref.getBaseRef(),
				getPage(page, 1,true)
				));
		b.append(String.format("\n&nbsp;<a href=\"%s?media=text/csv&%s\" title='Download as CSV'>CSV</a>",
				requestref.getBaseRef(),
				getPage(page, 1,true)
				));	
		b.append(String.format("\n&nbsp;<a href=\"%s?media=application/pdf&%s\" title='Download as PDF'>PDF</a>",
				requestref.getBaseRef(),
				getPage(page, 1,true)
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
	
	 public static String getString(RDFNode node) throws Exception {
		    if (node == null) return "";
			if (node.isLiteral()) return ((Literal)node).getString();
			else if (node.isResource()) return String.format("<a href='%s' target=_blank>%s</a>",((Resource)node).getURI(),((Resource)node).getURI());
			else return node.toString();
	 }
	 
	 
	 
	 protected void writeModels(int row,String cmp, Writer writer) throws Exception {
		 writer.write("</table><table>");
		 renderCompoundFeatures(queryPredictedFeatures,jenaModel,writer, cmp,"",null,new Reference(application));
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
							endpoint==null?"":endpoint,
									(modelName==null)||("".equals(modelName))?src:modelName,title,value,type));
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

	 
		public static int renderCompoundFeatures(
				//IToxPredictSession session,
			    String sparqlQuery,
				Model model, 
				Writer writer,
				String compoundURI, 
				String param,
				String caption,
				Reference rootReference) throws Exception {
		QueryExecution qe = null;
		try {
			//System.out.println(String.format(sparqlQuery,compoundURI,param));
			Query query = QueryFactory.create(String.format(sparqlQuery,compoundURI,param));
			qe = QueryExecutionFactory.create(query,model );
			ResultSet results = qe.execSelect();
			int records = 0;
			String thisModel = null;
			while (results.hasNext()) {
				records++;
				QuerySolution solution = results.next();

				String value = getValueColumn(solution);
				if (value == null) continue;
				
				String modelURI= getModelURI(solution);
				String modelString = getModelColumn(solution,rootReference);
				if (modelString != null) {
					if (!modelString.equals(thisModel)) {
						writer.write("<tr class='predictions'>");
						writer.write("<th align='left' colspan='3' class='predictions'>");
						thisModel = modelString;
						writer.write(modelString);
						
						writer.write("</th>");
						writer.write("</tr>");
					} 
						//predictions
						writer.write("<tr><td width='10%'></td><th colspan='1' align='left' valign='top' width='50%'>");
						writer.write(getNameColumn(solution, caption));
						writer.write("</th><td>");
						if (caption==null) 
							writer.write("<font color='#636bd2'>"); //didn't work with style
						
						String newValue = value;
						if (newValue.indexOf("/hilight?")>=0) 
							newValue = value.replace("/hilight?", String.format("%s?dataset_uri=%s&media=image/png&",modelURI,compoundURI));
						
						if (newValue.indexOf("changeImage('compound_uri',")>=0)
							newValue = newValue.replace("changeImage('compound_uri',", String.format("changeImage('%s',",compoundURI));
								
						writer.write(newValue);
						
						if (caption==null) 
							writer.write("</font>");
						writer.write("</td></tr>");						
			
				} else {
					writer.write("<tr><th colspan='2' align='left' valign='top' width='30%'>");
					writer.write(getNameColumn(solution, caption));
					writer.write("</th><td>");
					if (caption==null) 
						writer.write("<font color='#636bd2'>"); //didn't work with style
					
					String newValue = value;
					if (newValue.indexOf("/hilight?")>=0) 
						newValue = value.replace("/hilight?", String.format("%s?dataset_uri=%s&media=image/png&",modelURI,compoundURI));
					
					if (newValue.indexOf("changeImage('compound_uri',")>=0)
						newValue = newValue.replace("changeImage('compound_uri',", String.format("changeImage('%s',",compoundURI));
							
					writer.write(newValue);
					if (caption==null) 
						writer.write("</font>");
					writer.write("</td></tr>");
				}
				
			}

			return records;
		}catch (Exception x) {
			throw x;
		} finally {
			try {qe.close();} catch (Exception x) {}
		}		
	}	
		protected static String getModelURI( QuerySolution solution) {
			Resource m = solution.getResource("Model");
			return m.getURI();
		}
		protected static String getModelColumn( QuerySolution solution, Reference rootReference) {
			Resource m = solution.getResource("Model");
			Literal mname = solution.getLiteral("mname");
			Resource endpoint = solution.getResource("endpoint");
			Literal ename = solution.getLiteral("endpointName");
			if (m!=null) {
				/*
				String download = ModelTools.getDownloadURI(session,m.getURI(), 
							ChemicalMediaType.CHEMICAL_MDLSDF,rootReference);
							*/
				String download = null;
				return String.format(
						
						"%s&nbsp;<a href='%s' target='_blank' title='%s' alt='%s'><img border='0' src='%s/images/chart_line.png' alt='Model %s' title='Model %s'>%s</a>&nbsp;%s",
						ename==null?(endpoint==null?"":endpoint.getLocalName()):ename.getString(),
						m.getURI(),
						mname==null?m.getURI():mname.getString(),
						mname==null?m.getURI():mname.getString(),
						rootReference.toString(),
						mname==null?m.getURI():mname.getString(),
						mname==null?m.getURI():mname.getString(),
						mname==null?m.getURI():mname.getString(),
						download==null?"":download);
			}
			else return null;
		}	
		
		protected static String getNameColumn(QuerySolution solution,String caption) {
			Resource sameas = solution.getResource("o");
			Literal sameName = solution.getLiteral("otitle");		
			if (caption==null)
				return sameName!=null?sameName.getString():sameas!=null?sameas.getLocalName():"";
			else return caption; 
		}
		protected static String getValueColumn(QuerySolution solution) {
			Literal literal = solution.getLiteral("value");
			if ((literal==null) || literal.getString().equals(".") || literal.getString().equals("")) return null;
			return literal!=null?literal.getString():"";
		}		
		
		public OTDatasetRDFReport download(OutputStream out,MediaType media) throws Exception {
			
			Reference ref = getUri().clone();
			Form form = ref.getQueryAsForm();;
			ref.setQuery("");
			ClientResourceWrapper client = new ClientResourceWrapper(ref);
			Representation r = null;
			
			try {

				r = client.post(form.getWebRepresentation(),media);
				if (client.getStatus().equals(Status.SUCCESS_OK))
					DownloadTool.download(r.getStream(),out);
				else throw new ResourceException(client.getStatus(),ref.toString());
				
				//writeData(writer);
			} catch (ResourceException x) {
				throw x;
			} catch (Exception x) {
				throw x;
			} finally {
		
				try {r.release();} catch (Exception x) {}
				try {client.release();} catch (Exception x) {}
			}

			return this;
		}		
}
