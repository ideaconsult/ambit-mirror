package ambit2.rest.fastox;

import java.io.Writer;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.OutputStreamConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.model.ModelHTMLReporter;
import ambit2.rest.model.ModelResource;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.structure.CompoundHTMLReporter;

public class FastToxStep2 extends ModelResource {
	public static String resource = String.format("%s/step2",FastToxStep1.resource);
	protected IStructureRecord record = null;
	
	
	public FastToxStep2(Context context, Request request, Response response) {
		super(context,request,response);
		

	}
	
	@Override
	public boolean allowPost() {
		return super.allowPost();
	}
	
	@Override
	public void acceptRepresentation(Representation entity)
			throws ResourceException {
		Form form = new Form(entity);
		try { 
			record = new StructureRecord(
					Integer.parseInt(form.getFirstValue("idchemical")),
					Integer.parseInt(form.getFirstValue("idstructure"))
					,null,null);
		} catch (Exception x) {
			throw new ResourceException(status.CLIENT_ERROR_BAD_REQUEST,x);
		}
		getResponse().setEntity(getRepresentation(new Variant(MediaType.TEXT_HTML)));
	}
	
	
	@Override
	public RepresentationConvertor createConvertor(Variant variant)
			throws AmbitException, ResourceException {
/*
	if (variant.getMediaType().equals(MediaType.TEXT_XML)) {
		return new DocumentConvertor(new ModelXMLReporter(getRequest().getRootRef()));	
	} else 
	*/
	if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
		return new OutputStreamConvertor(
				new ModelHTMLReporter(getRequest().getRootRef(),collapsed) {
					
					@Override
					public void header(Writer w,
							IQueryRetrieval<ModelQueryResults> query) {
						super.header(w, query);
					
						cmp_reporter.processItem(record, w);
					}
					@Override
					protected void writeMoreColumns(ModelQueryResults model,
							Writer writer) {
						try {
							writer.write(String.format("<td><form method=\"POST\" action=\"%s/model/%d\">",getUriReporter().getBaseReference(),model.getId()));
							writer.write(String.format("<input name=\"idmodel\" value=\"%d\" type=\"hidden\">",model.getId()));
							writer.write(String.format("<input name=\"idstructure\" value=\"%d\"type=\"hidden\">",record.getIdstructure()));
							writer.write(String.format("<input name=\"idchemical\" value=\"%d\"type=\"hidden\">",record.getIdchemical()));
							writer.write("<input type=\"submit\" value=\"Predict\">");
							writer.write("</form></td>");
	
						} catch (Exception x) {
							
						}						
					}
				},MediaType.TEXT_HTML) ;
	} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
		return new StringConvertor(	new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(getRequest().getRootRef()) {
			@Override
			public void processItem(ModelQueryResults dataset, Writer output) {
				super.processItem(dataset, output);
				try {
				output.write('\n');
				} catch (Exception x) {}
			}
		},MediaType.TEXT_URI_LIST);
	} else //html 	
		return new OutputStreamConvertor(
				new ModelHTMLReporter(getRequest().getRootRef(),collapsed),MediaType.TEXT_HTML);
	}
	

}
