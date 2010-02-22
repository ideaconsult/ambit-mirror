package ambit2.fastox.task;

import java.io.Writer;
import java.util.Iterator;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.rest.SimpleTaskResource;
import ambit2.rest.StringConvertor;
import ambit2.rest.reporters.CatalogURIReporter;
import ambit2.rest.reporters.TaskRDFReporter;
import ambit2.rest.task.Task;

public class ToxPredictTaskResource<IToxPredictUser> extends SimpleTaskResource<IToxPredictUser> {

	@Override
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
}
