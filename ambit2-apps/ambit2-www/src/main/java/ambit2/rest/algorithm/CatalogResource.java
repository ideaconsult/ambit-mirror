package ambit2.rest.algorithm;

import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.reporter.Reporter;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;
import net.idea.restnet.i.task.ICallableTask;
import net.idea.restnet.i.task.ITask;
import net.idea.restnet.i.task.ITaskApplication;
import net.idea.restnet.i.task.ITaskResult;
import net.idea.restnet.i.task.ITaskStorage;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.config.AMBITConfig;
import ambit2.rest.AbstractResource;
import ambit2.rest.OpenTox;
import ambit2.rest.StringConvertor;
import ambit2.rest.aa.opensso.OpenSSOUser;
import ambit2.rest.reporters.CatalogURIReporter;
import ambit2.rest.task.CallablePOST;
import ambit2.rest.task.FactoryTaskConvertor;
import ambit2.user.rest.resource.AMBITDBRoles;
import ambit2.user.rest.resource.DBRoles;

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
				MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_JAVASCRIPT,
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
		String filenamePrefix = getRequest().getResourceRef().getPath();
		if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			return new StringConvertor(
					createHTMLReporter(),MediaType.TEXT_HTML);
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
		    CatalogURIReporter reporter = new CatalogURIReporter<T>(getRequest()) {
			private static final long serialVersionUID = 6849465718621669311L;

			@Override
			public void processItem(T src, Writer output) {
				super.processItem(src, output);
				try {
				output.write('\n');
				} catch (Exception x) {}
			}
		    };
		    return new StringConvertor(reporter,MediaType.TEXT_HTML,filenamePrefix);
			
		} else //html 	
			return new StringConvertor(createHTMLReporter(),MediaType.TEXT_HTML);
		
	}
	
	protected Reporter createHTMLReporter() {
		return new CatalogHTMLReporter(getRequest());
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
				ITask<ITaskResult,String> task =  ((ITaskApplication)getApplication()).addTask(
						String.format("Apply %s %s %s",model.toString(),reference==null?"":"to",reference==null?"":reference),
						callable,
						getRequest().getRootRef(),
						callable instanceof CallablePOST, 
						getToken()
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
				
				ITaskStorage storage = ((ITaskApplication)getApplication()).getTaskStorage();
				FactoryTaskConvertor<Object> tc = new FactoryTaskConvertor<Object>(storage);
				if (tasks.size()==1)
					return tc.createTaskRepresentation(tasks.get(0), variant, getRequest(),getResponse(),null);
				else
					return tc.createTaskRepresentation(tasks.iterator(), variant, getRequest(),getResponse(),null);				
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
			getLogger().log(Level.WARNING,x.getMessage(),x);
			setPage(0);
		}
		try {
			setPageSize(Long.parseLong(pageSize));
		} catch (Exception x) {
			getLogger().log(Level.WARNING,x.getMessage(),x);
			setPageSize(1000);
		}			
	}

	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request,
			IFreeMarkerApplication app) {
        
		map.put(AMBITDBRoles.ambit_admin.name(), Boolean.FALSE);
		map.put(AMBITDBRoles.ambit_datasetmgr.name(), Boolean.FALSE);
		if (getClientInfo()!=null) {
			if (getClientInfo().getUser()!=null)
				map.put("username", getClientInfo().getUser().getIdentifier());
			if (getClientInfo().getRoles()!=null) {
				if (DBRoles.isAdmin(getClientInfo().getRoles()))
					map.put(AMBITDBRoles.ambit_admin.name(),Boolean.TRUE);
				if (DBRoles.isDatasetManager(getClientInfo().getRoles()))
					map.put(AMBITDBRoles.ambit_datasetmgr.name(), Boolean.TRUE);
				if (DBRoles.isUser(getClientInfo().getRoles()))
					map.put(AMBITDBRoles.ambit_user.name(), Boolean.TRUE);	
			}
		}
		
        map.put(AMBITConfig.creator.name(),"IdeaConsult Ltd.");
        map.put(AMBITConfig.ambit_root.name(),getRequest().getRootRef().toString());
        map.put(AMBITConfig.ambit_version_short.name(),app.getVersionShort());
	    map.put(AMBITConfig.ambit_version_long.name(),app.getVersionLong());
	    map.put(AMBITConfig.googleAnalytics.name(),app.getGACode());
	    map.put(AMBITConfig.menu_profile.name(),app.getProfile());

	    
        //remove paging
        Form query = getRequest().getResourceRef().getQueryAsForm();
        //query.removeAll("page");query.removeAll("pagesize");query.removeAll("max");
        query.removeAll("media");
        Reference r = cleanedResourceRef(getRequest().getResourceRef());
        r.setQuery(query.getQueryString());
        
        map.put(AMBITConfig.ambit_request.name(),r.toString()) ;
        if (query.size()>0)
        	map.put(AMBITConfig.ambit_query.name(),query.getQueryString()) ;
        //json
        query.removeAll("media");query.add("media", MediaType.APPLICATION_JSON.toString());
        r.setQuery(query.getQueryString());
        map.put(AMBITConfig.ambit_request_json.name(),r.toString());
        //csv
        query.removeAll("media");query.add("media", MediaType.TEXT_CSV.toString());
        r.setQuery(query.getQueryString());
        map.put(AMBITConfig.ambit_request_csv.name(),r.toString());
 
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
	
	protected Representation getHTMLByTemplate(Variant variant) throws ResourceException {
        Map<String, Object> map = new HashMap<String, Object>();
        if (getClientInfo().getUser()!=null) 
        	map.put("username", getClientInfo().getUser().getIdentifier());
        else {
			OpenSSOUser ou = new OpenSSOUser();
			ou.setUseSecureCookie(useSecureCookie(getRequest()));
			getClientInfo().setUser(ou);
		}
        setTokenCookies(variant, useSecureCookie(getRequest()));
        configureTemplateMap(map,getRequest(),(IFreeMarkerApplication)getApplication());
        return toRepresentation(map, getTemplateName(), MediaType.TEXT_PLAIN);
	}


}
