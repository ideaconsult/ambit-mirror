package ambit2.rest.fastox;

import java.io.Writer;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.data.StructureRecord;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.model.ModelQueryResults;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.OutputWriterConvertor;
import ambit2.rest.RepresentationConvertor;
import ambit2.rest.StringConvertor;
import ambit2.rest.model.ModelHTMLReporter;
import ambit2.rest.model.ModelResource;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.structure.ConformerURIReporter;

public class FastToxStep2 extends ModelResource {
	//public static String resource = String.format("%s/step2",FastToxStep1.resource);
	protected IStructureRecord record = null;
	protected ConformerURIReporter<IQueryRetrieval<IStructureRecord>> structureReporter;
	
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		structureReporter = new ConformerURIReporter<IQueryRetrieval<IStructureRecord>>(getRequest());
	}
	@Override
	protected Representation post(Representation entity, Variant variant)
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
		getResponse().setEntity(get(new Variant(MediaType.TEXT_HTML)));
		return getResponse().getEntity();
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
		return new OutputWriterConvertor(
				new ModelHTMLReporter(getRequest(),false) {
					
					@Override
					public void header(Writer w,
							IQueryRetrieval<ModelQueryResults> query) {
						super.header(w, query);
					
						try {cmp_reporter.processItem(record);} catch (AmbitException x) {};
					}
					@Override
					protected void writeMoreColumns(ModelQueryResults model,
							Writer writer) {
						try {
							writer.write(String.format("<td><form method=\"POST\" action=\"%s/model/%d?%s=%s\">",
									getUriReporter().getBaseReference(),model.getId(),
									ModelResource.dataset_uri,
									Reference.encode(structureReporter.getURI(record))
									));
							writer.write("<input type=\"submit\" value=\"Predict\">");
							writer.write("</form></td>");
	
						} catch (Exception x) {
							
						}						
					}
				},MediaType.TEXT_HTML) ;
	} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
		return new StringConvertor(	new ModelURIReporter<IQueryRetrieval<ModelQueryResults>>(getRequest()) {
			@Override
			public Object processItem(ModelQueryResults dataset) throws AmbitException {
				super.processItem(dataset);
				try {
				output.write('\n');
				} catch (Exception x) {}
				return null;
			}
		},MediaType.TEXT_URI_LIST);
	} else //html 	
		return new OutputWriterConvertor(
				new ModelHTMLReporter(getRequest(),collapsed),MediaType.TEXT_HTML);
	}
	

}
