package ambit2.rest.model;

import java.io.IOException;
import java.io.Writer;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.ChemicalMediaType;
import ambit2.rest.DisplayMode;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;
import ambit2.rest.query.QueryResource;
import ambit2.rest.structure.CompoundHTMLReporter;

public class ModelHTMLReporter  extends QueryHTMLReporter<ModelQueryResults, IQueryRetrieval<ModelQueryResults>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959033048710547839L;
	//only necessary if applying the model
	protected CompoundHTMLReporter<IQueryRetrieval<IStructureRecord>> cmp_reporter;
	protected ModelJSONReporter modelJson_reporter;
	
	public ModelHTMLReporter(ResourceDoc doc) {
		this(null,DisplayMode.table,doc);
	}
	public ModelHTMLReporter(Request request,DisplayMode _dmode,ResourceDoc doc) {
		this(request,request,_dmode,doc);
	}
	public ModelHTMLReporter(Request request,Request originalRef,DisplayMode _dmode,ResourceDoc doc) {
		super(request,_dmode,doc);
		cmp_reporter = new CompoundHTMLReporter<IQueryRetrieval<IStructureRecord>>(request,doc,_dmode,true);
		modelJson_reporter = new ModelJSONReporter(request);
	}
	@Override
	protected QueryURIReporter createURIReporter(Request request, ResourceDoc doc) {
		return new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(request,doc);
	}
	@Override
	public void setOutput(Writer output) throws AmbitException {
		super.setOutput(output);
		modelJson_reporter.setOutput(output);
	}
	@Override
	public void header(Writer w, IQueryRetrieval<ModelQueryResults> query) {
		super.header(w, query);
		try { 
			String page = "";
			Form form = uriReporter.getResourceRef().getQueryAsForm();
			try {

				page = form.getFirstValue("page");
			} catch (Exception x) {
				page = "0";
			}	
			String maxhits = "";
			try {

				maxhits = form.getFirstValue(QueryResource.max_hits);
			} catch (Exception x) {
				maxhits = "1000";
			}					
			String pagesize = "";
			try {

				pagesize = form.getFirstValue("pagesize");
			} catch (Exception x) {
				pagesize = maxhits; 
			}				
			output.write(String.format("<hr><div class='results'><a href='%s' title='%s'>This model</a> | %s&nbsp; | Download as %s&nbsp;</div>%s",
					uriReporter.getResourceRef(),
					query.toString(),
					String.format("<a href='%s' title='%s'>License</a>",getLicenseURI(),getLicenseURI()),
					downloadLinks(),
					String.format("<form method='GET' action=''>Page&nbsp;<input name='page' type='text' title='Page' size='10' value='%s'>&nbsp;"+
							"Page size<input name='pagesize' type='text' title='Page size' size='10' value='%s'><input type='image' src='%s/images/search.png' value='Refresh'></form>",
							page==null?"0":page,
							pagesize==null?"100":pagesize,
							uriReporter.getBaseReference())			
									));
						
			output.write("<table class='modeltable' id='models' width='100%'></table>");
			output.write("\n<script type=\"text/javascript\">modelArray = \n");
			modelJson_reporter.header(w, query);
		} catch (Exception x) {} 
		/*
		try { 
			output.write("<table class='modeltable' id='models'>");
			output.write("<caption CLASS='results'>");
			
			if (!headless && _dmode.isCollapsed())  {
				String page = Long.toString(query.getPage());
				final Form form = uriReporter.getResourceRef().getQueryAsForm();
				try {
					page = form.getFirstValue("page")==null?page:form.getFirstValue("page");
				} catch (Exception x) {
					page = Long.toString(query.getPage());
				}			
				String pageSize =  Long.toString(query.getPageSize());
			
				try {
					pageSize = form.getFirstValue("pagesize")==null? Long.toString(query.getPageSize()):form.getFirstValue("pagesize");
				} catch (Exception x) {
					pageSize = "100";
				}	
				String search = "";
				try {
					search = form.getFirstValue("search");
				} catch (Exception x) {	search = ""; }	
				
				try {
				output.write("<form method='GET' action=''>");
				output.write("<b>Models </b>");
				output.write(String.format("Page:<input name='page' type='text' title='Page' size='10' value='%s'>\n",page==null?"0":page));
				if (search !=null) output.write(String.format("<input name='search' type='hidden' value='%s'>\n",search));
				output.write(String.format("<b>Page size:</b><input name='pagesize' type='text' title='Page size' size='10' value='%s'>\n",pageSize==null?"50":pageSize));
				output.write(String.format("<input type='image' src='%s/images/page_go.png' onsubmit='submit-form();' value='Refresh' title='Refresh'>",uriReporter.getBaseReference()));			
				output.write("</form>");
	
				} catch (Exception x) {
					
				} finally {
					
				}
		    }
			output.write("</caption>");	

			switch (_dmode) {
			case table: {
				output.write("<thead>\n");
				output.write("<tr align=\"left\">\n");
				output.write("<th >Model name</th>\n");
				output.write("<th width='30%'>Algorithm</th>\n");
				output.write("<th width='10%'>Dataset</th>\n");
				output.write("<th width='10%'>Independent variables</th>\n");
				output.write("<th width='10%'>Dependent</th>\n");
				output.write("<th width='10%'>Predicted</th>\n");
				output.write("</tr></thead>\n");
				break;
			}
			default : {
			}
			}

			output.write("<tbody>\n");
			cmp_reporter.setOutput(w);
			uriReporter.setOutput(w);

		} catch (Exception x) {}		
		*/
	}
	
	@Override
	public void footer(Writer output, IQueryRetrieval<ModelQueryResults> query) {
		try { 
			modelJson_reporter.footer(output, query);
			output.write(";</script>\n");
		
		} catch (Exception x) {
			x.printStackTrace();
		} 
		super.footer(output, query);
	}
	
	@Override
	public Object processItem(ModelQueryResults item) throws AmbitException {
		try { 
			modelJson_reporter.processItem(item);
			
		} catch (Exception x) {}
		return item;
	}
	/*
	@Override
	public Object processItem(ModelQueryResults model) {
		try {
			switch (_dmode) {
			case table: {
				output.write("<tr>\n");
				StringWriter w = new StringWriter();
				uriReporter.setOutput(w);
				uriReporter.processItem(model);
	
				output.write(String.format("<td><a href=\"%s\">%s</a>",w.toString(),model.getName()));
				
				output.write(String.format("<br><a href=\"%s?media=text/plain\" target='_model'>%s</a>",w.toString(),"txt"));			
				output.write(String.format(
						"&nbsp;<a href=\"%s?media=image/png\" target='_model'>%s</a></td>",
						w.toString(),"legend"));
				if (model.getAlgorithm()==null)	output.write("<td></td>");
				else
					output.write(String.format(
							"<td><a href=\"%s\" title='View algorithm details'>%s</a>%s</td>",
							model.getAlgorithm(),
							model.getAlgorithm().substring(model.getAlgorithm().lastIndexOf("/")+1),
							String.format("&nbsp;| <a href='%s/%s?algorithm=%s' title='%s'>%s</a>", 
									uriReporter.getBaseReference(),
									OpenTox.URI.model,
									Reference.encode(model.getAlgorithm()),
									"Browse all models created by this algorithm",
									"Search"
									)
					));
				
				if (model.getTrainingInstances()==null) output.write("<td></td>");
				else
					output.write(String.format(
							"<td><img src=\"%s/images/table.png\" alt=\"Compounds\" title=\"Browse compounds\" border=\"0\"/>&nbsp;<a href=\"%s\">Browse</a>&nbsp;|&nbsp;%s</td>",
							uriReporter.getBaseReference(),
							model.getTrainingInstances(),
							String.format("<a href='%s/%s?dataset=%s' title='%s'>%s</a>", 
									uriReporter.getBaseReference(),
									OpenTox.URI.model,
									Reference.encode(model.getTrainingInstances()),
									"All models using this training set",
									"Search"
									)
							));
				
				output.write(String.format(
						"<td><img src=\"%s/images/feature.png\" alt=\"Features\" title=\"Browse features\" border=\"0\"/>&nbsp;<a href=\"%s/independent\">%s</a></td>",
						uriReporter.getBaseReference(),
						uriReporter.getURI(model),
						"Browse"	
				));						
				output.write(String.format(
						"<td><img src=\"%s/images/16x16_toxicological_endpoints.png\" alt=\"Dependent variable(s)\" title=\"Dependent variable(s)\" border=\"0\"/>&nbsp;<a href=\"%s/dependent\">%s</a></td>",
						uriReporter.getBaseReference(),
						uriReporter.getURI(model),
						"Browse"		
				));				
				output.write(String.format(
						"<td><img src=\"%s/images/16x16_toxicological_endpoints.png\" alt=\"Predicted variable(s)\" title=\"Predicted variable(s)\" border=\"0\"/>&nbsp;<a href=\"%s/predicted\">%s</a><h5>%s</h5></td>",
						uriReporter.getBaseReference(),
						uriReporter.getURI(model),
						"Browse",
						model.getEndpoint()==null?"":
							String.format("<a href='%s/%s?endpoint=%s' title='%s'>%s</a>", 
									uriReporter.getBaseReference(),
									OpenTox.URI.model,
									Reference.encode(model.getEndpoint()),
									"All models for this endpoint",
									model.getEndpoint()
									)						
							
				));	
				writeMoreColumns( model,output);
				output.write("</tr>\n");
				break;
			} 
			case singleitem: {
				output.write(String.format(
						"<tr ><form action=\"\" method=\"POST\"><th>Dataset URI</th><td colspan='6'><input type='text' size='80' name='dataset_uri' value=''><input type=\"submit\" value=\"Predict\"></td></form></tr>"
						));
				break;
			}
			default: {
				
			}
			}


		} catch (Exception x) {
			
		}
		return null;
	}
	*/
	protected void writeMoreColumns(ModelQueryResults model, Writer output) {
	}
	
	protected String downloadLinks() throws IOException {
		StringBuilder w = new StringBuilder();
		MediaType[] mimes = {
				MediaType.TEXT_URI_LIST,
				MediaType.APPLICATION_RDF_XML,
				MediaType.APPLICATION_JSON
				};
		String[] image = {
				"link.png",
				"rdf.gif",
				"json.png"
		};
		String q=uriReporter.getResourceRef().getQuery();
		for (int i=0;i<mimes.length;i++) {
			MediaType mime = mimes[i];
			w.append("&nbsp;");
			w.append(String.format(
					"<a href=\"?%s%smedia=%s\"  ><img src=\"%s/images/%s\" alt=\"%s\" title=\"%s\" border=\"0\"/></a>",
					q==null?"":q,
					q==null?"":"&",
					Reference.encode(mime.toString()),
					uriReporter.getBaseReference().toString(),
					image[i],
					mime,
					mime));	

		}			
		return w.toString();
	}

}
