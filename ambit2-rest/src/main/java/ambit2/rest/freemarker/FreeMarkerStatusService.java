package ambit2.rest.freemarker;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.service.StatusService;

public class FreeMarkerStatusService  extends StatusService implements IFreeMarkerSupport {
	protected IFreeMarkerSupport freeMarkerSupport = new FreeMarkerSupport(); 
	protected FreeMarkerApplication app;

	public FreeMarkerStatusService(FreeMarkerApplication app) {
		super();
		this.app = app;
		setHtmlbyTemplate(true);
	}

	@Override
	public Status getStatus(Throwable throwable, Request request,
			Response response) {
		if (throwable == null) return response.getStatus();
		else if (throwable instanceof ResourceException) {
			return ((ResourceException)throwable).getStatus();
		} else return new Status(Status.SERVER_ERROR_INTERNAL,throwable);
	}
	

	public String getTemplateName() {
		return "status_body.ftl";
	}

	public boolean isHtmlbyTemplate() {
		return freeMarkerSupport.isHtmlbyTemplate();
	}

	public void setHtmlbyTemplate(boolean htmlbyTemplate) {
		freeMarkerSupport.setHtmlbyTemplate(htmlbyTemplate);
	}
	@Override
	public void configureTemplateMap(Map<String, Object> map, Request request,
			FreeMarkerApplication app) {
	
		freeMarkerSupport.configureTemplateMap(map,request,app);
        map.put("creator","IdeaConsult Ltd.");
        
	}
	
	protected Representation getHTMLByTemplate(Status status,String errName,String errDescription,String details,Request request) throws ResourceException {
        Map<String, Object> map = new HashMap<String, Object>();
        if (request.getClientInfo().getUser()!=null) 
        	map.put("username", request.getClientInfo().getUser().getIdentifier());
        map.put("ambit_root",request.getRootRef().toString());
        map.put("status_code",status.getCode());
        map.put("status_uri",status.getUri());
        map.put("status_name",status.getName());
        map.put("status_error_name",errName);
        map.put("status_error_description",errDescription);
        map.put("status_details",details);
        configureTemplateMap(map,request, app);
        return toRepresentation(map, getTemplateName(), MediaType.TEXT_PLAIN);
	}
	
	protected Representation toRepresentation(Map<String, Object> map,
            String templateName, MediaType mediaType) {
        
		
        return new TemplateRepresentation(
        		templateName,
        		app.getConfiguration(),
        		map,
        		MediaType.TEXT_HTML);
	}	
	

	@Override
	public Representation getRepresentation(Status status, Request request,
			Response response) {
		StringWriter details = null;
		if (status.getThrowable()!= null) {
			 details = new StringWriter();
			status.getThrowable().printStackTrace(new PrintWriter(details) {
				@Override
				public void print(String s) {
					super.print(String.format("%s<br>", s));
					
				}
			});
		} 		
		return getHTMLByTemplate(status,status.getName(),status.getDescription(), details==null?null:details.toString(),request);
	}
	
	@Override
	protected void finalize() throws Throwable {
		app = null;
		super.finalize();
	}
}

