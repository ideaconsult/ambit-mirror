package ambit2.fastox.task;

import java.io.Writer;
import java.util.Iterator;
import java.util.UUID;

import org.restlet.Request;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.Reporter;
import ambit2.fastox.users.UserResource;
import ambit2.rest.ResourceDoc;
import ambit2.rest.SimpleTaskResource;
import ambit2.rest.TaskApplication;
import ambit2.rest.task.FactoryTaskConvertor;
import ambit2.rest.task.ITaskStorage;

public class ToxPredictTaskResource<IToxPredictUser> extends SimpleTaskResource<IToxPredictUser> {

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		
	}
	
	protected String getUserKey() throws ResourceException {
		Object object = getRequest().getAttributes().get(UserResource.resourceKey);
		if (object!=null) return Reference.decode(object.toString());
		return null;
	}	
	
	@Override
	public synchronized IProcessor<Iterator<UUID>, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {

		ITaskStorage<IToxPredictUser> storage = ((TaskApplication)getApplication()).getTaskStorage();
		
		FactoryTaskConvertor<IToxPredictUser> tc = new FactoryTaskConvertor<IToxPredictUser>(storage) {
			@Override
			public synchronized Reporter<Iterator<UUID>, Writer> createTaskReporterHTML(
					Request request,ResourceDoc doc) throws AmbitException, ResourceException {
				return new TaskHTMLReporter(getRequest(),doc);
			}
		};
	
		return tc.createTaskConvertor(variant, getRequest(),getDocumentation());

	}
	/*
	public synchronized IProcessor<Iterator<Task<Reference,IToxPredictUser>>, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
		if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			return new StringConvertor(
					new TaskHTMLReporter(getRequest()) ,MediaType.TEXT_HTML);
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
		
			return new StringConvertor(	new CatalogURIReporter(getRequest()) {
				@Override
				public void processItem(Object src, Writer output) {
					super.processItem(src,output);
					try {output.write('\n'); } catch (Exception x) {}
				}
			},MediaType.TEXT_URI_LIST);
		}
		else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML) ||
				variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE) ||
				variant.getMediaType().equals(MediaType.TEXT_RDF_N3) ||
				variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES)
				) {
			return new StringConvertor(
					new TaskRDFReporter(getRequest(),variant.getMediaType())
					,variant.getMediaType());			
		} else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	}
	*/
}
