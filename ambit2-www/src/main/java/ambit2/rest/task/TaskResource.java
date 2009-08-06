package ambit2.rest.task;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.Variant;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.rest.AbstractResource;
import ambit2.rest.AmbitApplication;
import ambit2.rest.StatusException;
import ambit2.rest.StringConvertor;
import ambit2.rest.algorithm.AlgorithmHTMLReporter;
import ambit2.rest.algorithm.AlgorithmURIReporter;

/**
 * Provide (read)access to running tasks  under URL http://host/application/task. 
 * Provide (read)access to a single task  under URL http://host/application/task/{taskid}. 
 * Task identifiers are unique, generated via {@link UUID} class. 
 * <br>
 * An URL with a task identifier is returned when an asynchronous job is submitted via {@link AsyncJobResource}
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
public class TaskResource extends AbstractResource<Iterator<Task<Reference>>,Task<Reference>,IProcessor<Iterator<Task<Reference>>, Representation>> {
	public static final String resource = "task";
	public static final String resourceKey = "idtask";
	public static final String resourceID = String.format("/%s/{%s}", resource,resourceKey);
	
	public TaskResource(Context context, Request request, Response response) {
		super(context, request, response);
		this.getVariants().add(new Variant(MediaType.TEXT_URI_LIST));
		this.getVariants().add(new Variant(MediaType.TEXT_HTML));			
	}
	@Override
	public Representation getRepresentation(Variant variant) {
		return super.getRepresentation(variant);
	}
	@Override
	protected Iterator<Task<Reference>> createQuery(Context context, Request request,
			Response response) throws StatusException {
		Object id = request.getAttributes().get(resourceKey);
		try {
			ArrayList<Task<Reference>> list = new ArrayList<Task<Reference>>();
			if (id == null) {
				status = Status.SUCCESS_OK;
				return ((AmbitApplication)getApplication()).getTasks();
			} else {
				Task<Reference> task = ((AmbitApplication)getApplication()).findTask(Reference.decode(id.toString()));
				if (task==null) throw new StatusException(Status.CLIENT_ERROR_NOT_FOUND);
				if (task.isDone()) {
					status = Status.REDIRECTION_SEE_OTHER;
					getResponse().setStatus(status);
					Form responseHeaders = (Form) getResponse().getAttributes().get("org.restlet.http.headers");  
					if (responseHeaders == null) {
						responseHeaders = new Form();  
						getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders);  
					}  					
					getResponse().setLocationRef(task.getReference());
 					
				} else {
					status = Status.SUCCESS_ACCEPTED;
				}
				
				if (task != null) list.add(task);
				return list.iterator();
			}
		} catch (Exception x) {
			throw new StatusException(
					new Status(Status.CLIENT_ERROR_BAD_REQUEST,x,String.format("Invalid task id %d",id))
					);
		}
		
	}
	
	@Override
	public IProcessor<Iterator<Task<Reference>>, Representation> createConvertor(
			Variant variant) throws AmbitException {
		if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			return new StringConvertor(
					new AlgorithmHTMLReporter(getRequest().getRootRef()),MediaType.TEXT_HTML);
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
		
			return new StringConvertor(	new AlgorithmURIReporter(getRequest().getRootRef()) {
				@Override
				public void processItem(Object src, Writer output) {
					super.processItem(src,output);
					try {output.write('\n'); } catch (Exception x) {}
				}
			},MediaType.TEXT_URI_LIST);
			
		} else //html 	
			return new StringConvertor(
					new AlgorithmHTMLReporter(getRequest().getRootRef()),MediaType.TEXT_HTML);
	}

}
