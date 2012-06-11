package ambit2.rest.model;

import java.io.StringWriter;
import java.io.Writer;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.AmbitResource;
import ambit2.rest.OpenTox;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;
import ambit2.rest.structure.CompoundHTMLReporter;
import ambit2.rest.structure.DisplayMode;

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
			output.write("<table class='datatable' id='models' border='0' cellpadding='0' cellspacing='1' >");
			output.write("<thead>\n");
			output.write("<tr align=\"left\">\n");
			output.write("<th >Model name</th>\n");
			output.write("<th width='30%'>Algorithm</th>\n");
			output.write("<th width='10%'>Dataset</th>\n");
			output.write("<th width='10%'>Independent variables</th>\n");
			output.write("<th width='10%'>Dependent</th>\n");
			output.write("<th width='10%'>Predicted</th>\n");
			output.write("</tr></thead><tbody>\n");
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
			output.write("<tr>\n");
			StringWriter w = new StringWriter();
			uriReporter.setOutput(w);
			uriReporter.processItem(model);

			output.write(String.format(
						"<td><a href=\"%s\">%s</a>",
						w.toString(),
						model.getName()));
			
			output.write(String.format(
					"<br><a href=\"%s?media=text/plain\" target='_model'>%s</a>",
					w.toString(),
					"txt"));			
			output.write(String.format(
					"&nbsp;<a href=\"%s?media=image/png\" target='_model'>%s</a></td>",
					w.toString(),
					"legend"));
			
			output.write(String.format(
					"<td><a href=\"%s\">%s</a>&nbsp;%s</td>",
					model.getAlgorithm(),
					model.getAlgorithm(),
					String.format("<a href='%s/%s?algorithm=%s' title='%s'>%s</a>", 
							uriReporter.getBaseReference(),
							OpenTox.URI.model,
							Reference.encode(model.getAlgorithm()),
							"All models created by this algorithm",
							"<br>Search"
							)
			));
			
			if (model.getTrainingInstances()==null)
				output.write("<td></td>");
			else
				output.write(String.format(
						"<td><img src=\"%s/images/table.png\" alt=\"Compounds\" title=\"Browse compounds\" border=\"0\"/>&nbsp;<a href=\"%s\">Browse</a>&nbsp;%s</td>",
						uriReporter.getBaseReference(),
						model.getTrainingInstances(),
						String.format("<a href='%s/%s?dataset=%s' title='%s'>%s</a>", 
								uriReporter.getBaseReference(),
								OpenTox.URI.model,
								Reference.encode(model.getTrainingInstances()),
								"All models using this training set",
								"<br>Search"
								)
						));
			
			output.write(String.format(
					"<td><img src=\"%s/images/feature.png\" alt=\"Features\" title=\"Browse features\" border=\"0\"/>&nbsp;<a href=\"%s/independent\">%s</a></td>",
					uriReporter.getBaseReference(),
					uriReporter.getURI(model),
					"Independent variables"	
			));						
			output.write(String.format(
					"<td><img src=\"%s/images/16x16_toxicological_endpoints.png\" alt=\"Dependent variable(s)\" title=\"Dependent variable(s)\" border=\"0\"/>&nbsp;<a href=\"%s/dependent\">%s</a></td>",
					uriReporter.getBaseReference(),
					uriReporter.getURI(model),
					"Dependent"		
			));				
			output.write(String.format(
					"<td><img src=\"%s/images/16x16_toxicological_endpoints.png\" alt=\"Predicted variable(s)\" title=\"Predicted variable(s)\" border=\"0\"/>&nbsp;<a href=\"%s/predicted\">%s</a><h5>%s</h5></td>",
					uriReporter.getBaseReference(),
					uriReporter.getURI(model),
					"Predicted",
					model.getEndpoint()==null?"":
						String.format("<a href='%s/%s?endpoint=%s' title='%s'>%s</a>", 
								uriReporter.getBaseReference(),
								OpenTox.URI.model,
								Reference.encode(model.getEndpoint()),
								"All models for this endpoint",
								model.getEndpoint()
								)						
						
			));	
		
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
			writeMoreColumns( model,output);
			output.write("</tr>\n");
			if (_dmode.isCollapsed()) {//single model
				output.write(String.format(
						"<tr ><form action=\"\" method=\"POST\"><th>Dataset URI</th><td colspan='6'><input type='text' size='80' name='dataset_uri' value=''><input type=\"submit\" value=\"Predict\"></td></form></tr>"
						));
			}
		} catch (Exception x) {
			
		}
		return null;
	}
	protected void writeMoreColumns(ModelQueryResults model, Writer output) {
	}

}
