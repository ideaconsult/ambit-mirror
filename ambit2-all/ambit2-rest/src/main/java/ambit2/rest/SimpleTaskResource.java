package ambit2.rest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.UUID;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.rest.task.FactoryTaskConvertor;
import ambit2.rest.task.FilteredTasksIterator;
import ambit2.rest.task.SingleTaskIterator;
import ambit2.rest.task.Task;
import ambit2.rest.task.Task.TaskStatus;

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
public class SimpleTaskResource<USERID> extends AbstractResource<Iterator<Task<Reference,USERID>>,
							Task<Reference,USERID>,IProcessor<Iterator<Task<Reference,USERID>>, Representation>> {
	public static final String resource = "/task";
	public static final String resourceKey = "idtask";
	public static final String resourceID = String.format("/{%s}", resourceKey);
	
	protected String searchStatus = null;
	protected int max = 10;
	

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] {
				MediaType.TEXT_HTML,
				MediaType.TEXT_URI_LIST,
				MediaType.TEXT_PLAIN,
				MediaType.APPLICATION_RDF_XML,
				MediaType.APPLICATION_RDF_TURTLE,
				MediaType.TEXT_RDF_N3,
				MediaType.APPLICATION_JSON,
				MediaType.TEXT_RDF_NTRIPLES,
				MediaType.APPLICATION_JAVA_OBJECT});
		Form form = getRequest().getResourceRef().getQueryAsForm();
		try {
			max = Integer.parseInt(Reference.decode(form.getFirstValue(AbstractResource.search_param)));
		} catch (Exception x) {
			max = 0;
		}		
		try {
			searchStatus = Reference.decode(form.getFirstValue(AbstractResource.search_param));
		} catch (Exception x) { searchStatus = null; }
		
		try {
			if (searchStatus != null) TaskStatus.valueOf(searchStatus);
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,String.format("Allowed status values: %s %s, %s, %s", 
							TaskStatus.Running, TaskStatus.Completed, TaskStatus.Cancelled));
		}		
	}

	protected boolean filterTask(Task<Reference, USERID> task, int taskNumber) {
		if ((max > 0) && (taskNumber>=max)) return false;
		else return searchStatus==null?true:searchStatus.equals(task.getStatus());
	}
	protected Iterator<Task<Reference, USERID>> getTasks() {

		return new FilteredTasksIterator<USERID>(((TaskApplication)getApplication()).getTaskStorage()){
			@Override
			protected boolean accepted(Task<Reference, USERID> task) {
				task.update();
				if (!task.isDone()) getResponse().setStatus(Status.SUCCESS_ACCEPTED);
				return filterTask(task,getNum());
			}
		};
	}
	@Override
	protected synchronized Iterator<Task<Reference,USERID>> createQuery(Context context, Request request,
			Response response) throws ResourceException {
		
		Object id = request.getAttributes().get(resourceKey);

		try {
			/*
			Form responseHeaders = (Form) getResponse().getAttributes().get("org.restlet.http.headers");  
			if (responseHeaders == null) {
				responseHeaders = new Form();  
				getResponse().getAttributes().put("org.restlet.http.headers", responseHeaders);  
			} 			
			responseHeaders.add("Cache-Control", "no-store, no-cache, must-revalidate"); //HTTP 1.1
			responseHeaders.add("Cache-Control", "post-check=0, pre-check=0");
			responseHeaders.add("Pragma", "no-cache"); //HTTP 1.0
			responseHeaders.add("Expires", "0"); //prevents caching at the proxy server
			//responseHeaders.add("Refresh",String.format("60; url=%s",task.getReference()));
			//response.setLocationRef(task.getReference());			
			 * 
			 */
			ArrayList<Task<Reference,USERID>> list = new ArrayList<Task<Reference,USERID>>();
			if (id == null) {
				response_status = Status.SUCCESS_OK;
				return getTasks();

					/*
					Iterator<Task<Reference,USERID>> tasks = ((TaskApplication)getApplication()).getTasks();
					while (tasks.hasNext()) {
						Task<Reference,USERID> task = tasks.next();
						task.update();
						if (search.equals(task.getStatus())) 
							list.add(task);
						
					}
					Collections.sort(list, new TaskComparator<USERID>());
					return list.iterator();
					*/

			} else {

				Task<Reference,USERID> task = ((TaskApplication<USERID>)getApplication()).findTask(Reference.decode(id.toString()));
				
				if (task==null) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
				task.update();
				
				if (task.getError()==null)
					response_status = task.isDone()?Status.SUCCESS_OK:Status.SUCCESS_ACCEPTED;
				else 
					response_status = task.getError().getStatus();
				
				return new SingleTaskIterator<USERID>(task);
			}
		} catch (ResourceException x) {
			throw x;
		} catch (Exception x) {
			throw new ResourceException(
					Status.CLIENT_ERROR_BAD_REQUEST,
					String.format("Task id %s",id),
					x
					);
		} catch (Throwable x) {
			throw new ResourceException(
					Status.SERVER_ERROR_INTERNAL,
					x
					);			
		}
		
	}
	
	@Override
	public synchronized IProcessor<Iterator<Task<Reference,USERID>>, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {

		FactoryTaskConvertor<USERID> tc = new FactoryTaskConvertor<USERID>();
	
		return tc.createTaskConvertor(variant, getRequest());

	}
	
}



class TaskComparator<USERID> implements Comparator<Task<Reference,USERID>>{

	public int compare(Task<Reference,USERID> o1, Task<Reference,USERID> o2) {
		return (int) (o2.getStarted()-o1.getStarted());
	}
	
}