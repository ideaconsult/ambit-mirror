package ambit2.rest;

import java.util.Comparator;
import java.util.Iterator;
import java.util.UUID;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.i.task.ITask;
import net.idea.restnet.i.task.ITaskApplication;
import net.idea.restnet.i.task.ITaskResult;
import net.idea.restnet.i.task.ITaskStorage;
import net.idea.restnet.i.task.TaskStatus;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.CacheDirective;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.rest.task.FactoryTaskConvertor;
import ambit2.rest.task.FilteredTasksIterator;
import ambit2.rest.task.SingleTaskIterator;
import ambit2.rest.task.Task;

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
public class SimpleTaskResource<USERID> extends AbstractResource<Iterator<UUID>,
						UUID,IProcessor<Iterator<UUID>, Representation>> {
	public static final String resource = "/task";
	public static final String resourceKey = "idtask";
	public static final String resourceID = String.format("/{%s}", resourceKey);
	
	protected String searchStatus = null;
	protected int max = 10;
	
	public SimpleTaskResource() {
		super();
		setDocumentation(new ResourceDoc("AsyncTask","Task"));
	}
	protected int page = 0;
	public int getPage() {
		return page;
	}


	public void setPage(int page) {
		this.page = page;
	}


	public long getPageSize() {
		return pageSize;
	}


	public void setPageSize(long pageSize) {
		this.pageSize = pageSize;
	}

	protected long pageSize = 100;
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
				MediaType.APPLICATION_JAVASCRIPT,
				MediaType.TEXT_RDF_NTRIPLES,
				MediaType.APPLICATION_JAVA_OBJECT});
		Form form = getResourceRef(getRequest()).getQueryAsForm();
		setPaging(form);
		try {
			searchStatus = Reference.decode(form.getFirstValue(AbstractResource.search_param));
		} catch (Exception x) { searchStatus = null; }
		
		try {
			max = Integer.parseInt(Reference.decode(form.getFirstValue(AbstractResource.max_hits)));
		} catch (Exception x) { max = 10; }		
		try {
			if (searchStatus != null) TaskStatus.valueOf(searchStatus);
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,String.format("Allowed status values: %s %s, %s, %s", 
							TaskStatus.Running, TaskStatus.Completed, TaskStatus.Cancelled));
		}		
	}

	protected boolean filterTask(ITask<ITaskResult, USERID> task, int taskNumber) {
		if ((max > 0) && (taskNumber>=max)) return false;
		else return searchStatus==null?true:searchStatus.equals(task.getStatus().toString());
	}
	
	
	protected Iterator<UUID> getTasks() {

		
		return new FilteredTasksIterator<USERID>(((ITaskApplication)getApplication()).getTaskStorage()){
			@Override
			protected boolean accepted(ITask<ITaskResult, USERID> task) {
				//task.update();
				if (!task.isDone()) getResponse().setStatus(Status.SUCCESS_ACCEPTED);
				return filterTask(task,getNum());
			}
		};
	}
	
	@Override
	protected synchronized Iterator<UUID> createQuery(Context context, Request request,
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
			response.getCacheDirectives().add(CacheDirective.noCache());
			
			if (id == null) {
				response_status = Status.SUCCESS_OK;
				//turn  ((TaskApplication)getApplication()).getTaskStorage().getTasks();
				return getTasks();

			} else {

				ITask<ITaskResult,USERID> task = ((ITaskApplication<USERID>)getApplication()).findTask(Reference.decode(id.toString()));
				
				if (task==null) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			
				
				if (task.getError()==null) {
					
					switch (task.getStatus()) {
					case Cancelled: {
						response_status = Status.SERVER_ERROR_SERVICE_UNAVAILABLE;
						break;						
					}
					case Completed: {
						response_status = Status.SUCCESS_OK;
						break;
					}
					case Error: {
						response_status =  Status.SERVER_ERROR_INTERNAL;
						break;												
					}
					case Queued: {
						response_status = Status.SUCCESS_ACCEPTED;
						break;						
					}
					case Running: {
						response_status = Status.SUCCESS_ACCEPTED;
						break;
					}
					default: {
						response_status = Status.SUCCESS_ACCEPTED;
						break;						
					}
					}
				} else 
					response_status = task.getError().getStatus();
				
				return new SingleTaskIterator<USERID>(task.getUuid());
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
	public synchronized IProcessor<Iterator<UUID>, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {

		ITaskStorage<USERID> storage = ((ITaskApplication)getApplication()).getTaskStorage();
		FactoryTaskConvertor<USERID> tc = new FactoryTaskConvertor<USERID>(storage);
	
		return tc.createTaskConvertor(variant, getRequest(),getDocumentation(),DisplayMode.table);

	}
	
	protected void setPaging(Form form) {
		String max = form.getFirstValue(max_hits);
		String page = form.getFirstValue(OpenTox.params.page.toString());
		String pageSize = form.getFirstValue(OpenTox.params.pagesize.toString());
		if (max != null)
		try {
			setPage(0);
			setPageSize(Long.parseLong(form.getFirstValue(max_hits).toString()));
			return;
		} catch (Exception x) {
			
		}
		try {
			setPage(Integer.parseInt(page));
		} catch (Exception x) {
			setPage(0);
		}
		try {
			setPageSize(Long.parseLong(pageSize));
		} catch (Exception x) {
			setPageSize(1000);
		}			
	}
	

	
}



class TaskComparator<USERID> implements Comparator<Task<Reference,USERID>>{

	public int compare(Task<Reference,USERID> o1, Task<Reference,USERID> o2) {
		return (int) (o2.getStarted()-o1.getStarted());
	}
	
}