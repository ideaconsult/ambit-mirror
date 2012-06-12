package ambit2.rest.model;

import java.io.StringWriter;
import java.io.Writer;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Reference;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.DisplayMode;
import ambit2.rest.OpenTox;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;
import ambit2.rest.structure.CompoundHTMLReporter;

public class ModelHTMLReporter  extends QueryHTMLReporter<ModelQueryResults, IQueryRetrieval<ModelQueryResults>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959033048710547839L;
	//only necessary if applying the model
	protected CompoundHTMLReporter<IQueryRetrieval<IStructureRecord>> cmp_reporter;
	
	public ModelHTMLReporter(ResourceDoc doc) {
		this(null,DisplayMode.table,doc);
	}
	public ModelHTMLReporter(Request request,DisplayMode _dmode,ResourceDoc doc) {
		this(request,request,_dmode,doc);
	}
	public ModelHTMLReporter(Request request,Request originalRef,DisplayMode _dmode,ResourceDoc doc) {
		super(request,_dmode,doc);
		cmp_reporter = new CompoundHTMLReporter<IQueryRetrieval<IStructureRecord>>(request,doc,_dmode,true);
	}
	@Override
	protected QueryURIReporter createURIReporter(Request request, ResourceDoc doc) {
		return new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(request,doc);
	}
	@Override
	public void header(Writer w, IQueryRetrieval<ModelQueryResults> query) {
		super.header(w, query);
		try { 
			output.write("<table class='datatable' id='models'>");
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
	}
	@Override
	public void footer(Writer output, IQueryRetrieval<ModelQueryResults> query) {
		try { output.write("</tbody></table>");} catch (Exception x) {}
		super.footer(output, query);
	}
	
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
			/*
			if (!collapsed) {
				output.write("&nbsp;");
				output.write(String.format(
						"<a href=\"%s%s\"><img src=\"%s/images/structures.gif\" alt=\"compounds\" title=\"Browse compounds\" border=\"0\"/></a>",
						w.toString(),
						CompoundResource.compound,
						uriReporter.getBaseReference().toString()));	
				
				output.write("&nbsp;");
				output.write(String.format(
						"<a href=\"%s%s\"><img src=\"%s/images/search.png\" alt=\"query\" title=\"Search compounds\" border=\"0\"/></a>",
						w.toString(),
						QueryResource.query_resource,
						uriReporter.getBaseReference().toString()));	
			}
			*/


		} catch (Exception x) {
			
		}
		return null;
	}
	protected void writeMoreColumns(ModelQueryResults model, Writer output) {
	}

}
