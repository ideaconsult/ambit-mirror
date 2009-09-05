package ambit2.rest.model;

import java.io.StringWriter;
import java.io.Writer;

import org.restlet.data.Reference;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.structure.CompoundHTMLReporter;
import ambit2.rest.template.OntologyURIReporter;

public class ModelHTMLReporter  extends QueryHTMLReporter<ModelQueryResults, IQueryRetrieval<ModelQueryResults>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959033048710547839L;
	protected OntologyURIReporter templateReporter ;
	//only necessary if applying the model
	protected CompoundHTMLReporter<IQueryRetrieval<IStructureRecord>> cmp_reporter;
	
	public ModelHTMLReporter() {
		this(null,true);
	}
	public ModelHTMLReporter(Reference baseRef,boolean collapsed) {
		this(baseRef,baseRef,collapsed);
	}
	public ModelHTMLReporter(Reference baseRef,Reference originalRef,boolean collapsed) {
		super(baseRef,collapsed);
		templateReporter = new OntologyURIReporter(baseRef);
		cmp_reporter = new CompoundHTMLReporter<IQueryRetrieval<IStructureRecord>>(baseRef,true);
	}
	@Override
	protected QueryURIReporter createURIReporter(Reference reference) {
		return new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(reference);
	}
	@Override
	public void header(Writer w, IQueryRetrieval<ModelQueryResults> query) {
		super.header(w, query);
		try { 
			output.write("<table width=\"90%\" >");
			output.write("<tr align=\"left\">\n");
			output.write("<th>Model name</th>\n");
			output.write("<th>Dataset</th>\n");
			output.write("<th>Predictors</th>\n");
			output.write("<th>Dependent variables</th>\n");
			output.write("</tr>\n");
		} catch (Exception x) {}		
	}
	@Override
	public void footer(Writer output, IQueryRetrieval<ModelQueryResults> query) {
		try { output.write("</table>");} catch (Exception x) {}
		super.footer(output, query);
	}
	
	@Override
	public void processItem(ModelQueryResults model, Writer output) {
		try {
			output.write("<tr>\n");
			StringWriter w = new StringWriter();
			uriReporter.processItem(model, w);

			output.write(String.format(
						"<td><a href=\"%s\">%s</a></td>",
						w.toString(),
						model.getName()));
			
			if ((model.getTrainingInstances()==null) || (model.getTrainingInstances().getFieldname()==null))
				output.write("<td></td>");
			else
				output.write(String.format(
						"<td><img src=\"%s/images/structures.gif\" alt=\"Compounds\" title=\"Browse compounds\" border=\"0\"/>&nbsp;<a href=\"%s/query/results/%d\">Browse</a></td>",
						uriReporter.getBaseReference(),
						uriReporter.getBaseReference(),
						model.getTrainingInstances().getFieldname().getId()
						));
						
			output.write(String.format(
					"<td><img src=\"%s/images/feature.png\" alt=\"Features\" title=\"Browse features\" border=\"0\"/>&nbsp;<a href=\"%s/template/%s\">%s</a></td>",
					uriReporter.getBaseReference(),
					uriReporter.getBaseReference(),
					model.getPredictors()==null?"":Reference.encode(model.getPredictors().getName()),
					model.getPredictors()==null?"":model.getPredictors().getName()		
			));
			output.write(String.format(
					"<td><img src=\"%s/images/16x16_toxicological_endpoints.png\" alt=\"Dependent variable(s)\" title=\"Dependent variable(s)\" border=\"0\"/>&nbsp;<a href=\"%s/template/%s\">%s</a></td>",
					uriReporter.getBaseReference(),
					uriReporter.getBaseReference(),
					model.getDependent()==null?"":Reference.encode(model.getDependent().getName()),
					model.getDependent()==null?"":model.getDependent().getName()		
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
		} catch (Exception x) {
			
		}
	}
	protected void writeMoreColumns(ModelQueryResults model, Writer output) {
	}

}
