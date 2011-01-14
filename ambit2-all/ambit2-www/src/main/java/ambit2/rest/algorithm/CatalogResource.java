package ambit2.rest.algorithm;

import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import org.opentox.aa.OTAAParams;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.rest.AbstractResource;
import ambit2.rest.AmbitApplication;
import ambit2.rest.OpenTox;
import ambit2.rest.StringConvertor;
import ambit2.rest.TaskApplication;
import ambit2.rest.reporters.CatalogURIReporter;
import ambit2.rest.task.AmbitFactoryTaskConvertor;
import ambit2.rest.task.CallablePOST;
import ambit2.rest.task.FactoryTaskConvertor;
import ambit2.rest.task.ICallableTask;
import ambit2.rest.task.ITaskStorage;
import ambit2.rest.task.Task;
import ambit2.rest.task.TaskResult;

/**
 * Algorithms as per http://opentox.org/development/wiki/Algorithms
 * @author nina
 *
 */
public abstract class CatalogResource<T extends Serializable> extends AbstractResource<Iterator<T>,T,IProcessor<Iterator<T>, Representation>> {
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
				MediaType.TEXT_URI_LIST,
				MediaType.APPLICATION_RDF_XML,
				MediaType.APPLICATION_RDF_TURTLE,
				MediaType.TEXT_RDF_N3,
				MediaType.TEXT_RDF_NTRIPLES,
				MediaType.APPLICATION_JAVA_OBJECT,
				MediaType.TEXT_HTML,
				MediaType.APPLICATION_WADL
				});
		
	}


	public static String getAlgorithmURI(String category) {
		return String.format("%s%s/{%s}",AllAlgorithmsResource.algorithm,category,AllAlgorithmsResource.algorithmKey);
	}
	@Override
	public IProcessor<Iterator<T>, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {

		if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			return new StringConvertor(
					new CatalogHTMLReporter<T>(getRequest(),getDocumentation()),MediaType.TEXT_HTML);
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			return new StringConvertor(	new CatalogURIReporter<T>(getRequest(),getDocumentation()) {
				@Override
				public void processItem(T src, Writer output) {
					super.processItem(src, output);
					try {
					output.write('\n');
					} catch (Exception x) {}
				}
			},MediaType.TEXT_URI_LIST);
			
		} else //html 	
			return new StringConvertor(
					new CatalogHTMLReporter<T>(getRequest(),getDocumentation()),MediaType.TEXT_HTML);
		
	}
	
	
	protected ICallableTask createCallable(Form form, T item) throws ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
	}
	
	protected Reference getSourceReference(Form form,T model) throws ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
	}

	
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		synchronized (this) {
			
			Form form = entity.isAvailable()?new Form(entity):new Form();
			
			//models
			Iterator<T> query = queryObject;
			if (query==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			
			ArrayList<UUID> tasks = new ArrayList<UUID>();
			while (query.hasNext()) 
			try {
				T model = query.next();
				Reference reference = getSourceReference(form,model);
				ICallableTask callable= createCallable(form,model);
				Task<TaskResult,String> task =  ((AmbitApplication)getApplication()).addTask(
						String.format("Apply %s %s %s",model.toString(),reference==null?"":"to",reference==null?"":reference),
						callable,
						getRequest().getRootRef(),
						callable instanceof CallablePOST, 
						getUserToken(OTAAParams.subjectid.toString())
						);	
				task.update();
				setStatus(task.isDone()?Status.SUCCESS_OK:Status.SUCCESS_ACCEPTED);
				tasks.add(task.getUuid());

			} catch (ResourceException x) {
				throw x;
			} catch (Exception x) {
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x.getMessage(),x);
			}
			if (tasks.size()==0)
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			else {
				
				ITaskStorage storage = ((TaskApplication)getApplication()).getTaskStorage();
				FactoryTaskConvertor<Object> tc = new AmbitFactoryTaskConvertor<Object>(storage);
				if (tasks.size()==1)
					return tc.createTaskRepresentation(tasks.get(0), variant, getRequest(),getResponse(),getDocumentation());
				else
					return tc.createTaskRepresentation(tasks.iterator(), variant, getRequest(),getResponse(),getDocumentation());				
			}
		}
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
			x.printStackTrace();
			setPage(0);
		}
		try {
			setPageSize(Long.parseLong(pageSize));
		} catch (Exception x) {
			x.printStackTrace();
			setPageSize(1000);
		}			
	}
	@Override
	protected Representation describe() {
        setTitle(getClass().getName());
		return super.describe();
	}
	@Override
	protected void describeGet(MethodInfo info) {
		super.describeGet(info);
	}

	/*
	@Override
	public void describe(String arg0, ResourceInfo info) {
		// TODO Auto-generated method stub
		super.describe(arg0, info);
	}


	@Override
	protected void describePost(MethodInfo info) {
		// TODO Auto-generated method stub
		super.describePost(info);
	}
	@Override
	protected void describePut(MethodInfo info) {
		// TODO Auto-generated method stub
		super.describePut(info);
	}
	@Override
	protected void describeOptions(MethodInfo info) {
		info.setDocumentation("Not implemented");
		super.describeOptions(info);
	}
	@Override
	protected Representation describeVariants() {
		//TODO 
		return super.describeVariants();
	}
	@Override
	protected void describeDelete(MethodInfo info) {
        info.setDocumentation("Delete the current item.");

        RepresentationInfo repInfo = new RepresentationInfo();
        repInfo.setDocumentation("No representation is returned.");
        repInfo.getStatuses().add(Status.SUCCESS_NO_CONTENT);
        info.getResponse().getRepresentations().add(repInfo);
	}
	*/
}
