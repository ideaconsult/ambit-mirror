package ambit2.rest.task;

import java.io.Writer;
import java.util.Iterator;
import java.util.UUID;

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

/**
 * http://opentox.org/wiki/opentox/Asynchronous_jobs
 * Provide (read)access to running tasks  under URL http://host/application/task. 
 * Provide (read)access to a single task  under URL http://host/application/task/{taskid}. 
 * Task identifiers are unique, generated via {@link UUID} class. 
 * <br>
 * An URL with a task identifier is returned when an asynchronous job is submitted via {@link AsyncJobResource}
 * If accepted, the status code is 201 and the URI of the task resource in the Location header /task/{id} 
 * <br>
 * If a list of tasks has been requested, returns list of URLs of the tasks TODO - introduce Guards for protecting sensitive resources
 * <br>
 *  If a single task is requested and the task is not completed,returns Status code 202  (accepted, processing has not been completed). 
 * <br>
 * If a single task is requested and the task is completed, returns Status code 303 and the new URL in the "Location" header
<pre>
HTTP/1.1 303 See Other
Location: http://example.org/thenewurl
</pre
 * @author nina
 *
 */
public class TaskResource<USERID> extends SimpleTaskResource<USERID> {

	
	@Override
	public synchronized IProcessor<Iterator<Task<Reference,USERID>>, Representation> createConvertor(
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
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML) ||
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
