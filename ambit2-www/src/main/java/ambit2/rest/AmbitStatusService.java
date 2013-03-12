package ambit2.rest;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ResourceException;
import org.restlet.service.StatusService;

import ambit2.rest.exception.RResourceException;

public class AmbitStatusService extends StatusService {
	public AmbitStatusService() {
		super();
		setContactEmail("jeliazkova.nina@gmail.com");
		
	}
	
	


	@Override
	public Representation getRepresentation(Status status, Request request,
			Response response) {
		try {
			boolean wrapInHTML = true;
			
			if (status.equals(Status.CLIENT_ERROR_FORBIDDEN)) 
				wrapInHTML = false;
			else if ((status.getThrowable() !=null) && (status.getThrowable() instanceof RResourceException)) 
				wrapInHTML = ((RResourceException)status.getThrowable()).getVariant().equals(MediaType.TEXT_HTML);
			else {
				Form headers = (Form) request.getAttributes().get("org.restlet.http.headers"); 
				String acceptHeader = headers.getValues("accept");
				if (acceptHeader!=null)
					wrapInHTML = acceptHeader.contains("text/html");
			}
			
			if (wrapInHTML) {
				StringWriter w = new StringWriter();
				
				AmbitResource.writeHTMLHeader(w, status.getName(), request,
						request.getOriginalRef()==null?request.getResourceRef():request.getOriginalRef(),
						null);
				
				StringWriter details = new StringWriter();

				if (status.getThrowable()!= null) {
					status.getThrowable().printStackTrace(new PrintWriter(details) {
						@Override
						public void print(String s) {
							super.print(String.format("%s<br>", s));
							
						}
					});
				} 
				
				
				w.write(
						String.format(		
						"<div class=\"ui-widget \" style=\"margin-top: 20px; padding: 0 .7em;\">\n"+
						"<div class=\"ui-widget-header ui-corner-top\"><p>Error code <a href='%s' target='help'>%d</a>&nbsp;%s</p></div>\n"+
						"<div class=\"ui-widget-content ui-corner-bottom \">\n"+
						"<p>%s</p><p>"+
						"<a href=\"$('#%s').toggle();\" style=\"background-color: #fff; padding: 5px 10px;\">Details</a>\n"+	
						"</p>\n"+
						"<div class=\"ui-widget\" style='display: none;' id='details'><p>%s</p></div>\n"+
						"</div></div>\n",
						status.getUri(),
						status.getCode(),
						status.getName(),
						status.getDescription(),
						"details",
						details
						)
				);
				
				AmbitResource.writeHTMLFooter(w, status.getName(), request);
				return new StringRepresentation(w.toString(),MediaType.TEXT_HTML);
			} else {
				if ((status.getThrowable() !=null) && (status.getThrowable() instanceof RResourceException)) 
					return ((RResourceException)status.getThrowable()).getRepresentation();
			}
			/*
			w.write(String.format("ERROR :<br>Code: %d<br>Name: %s<br>URI: %s<br>Description: %s<br>",
					status.getCode(),
					status.getName(),
					status.getUri(),
					status.getDescription()
					));
	*/

		} catch (Exception x) {
			
		}
		if (status.getThrowable()==null)
			return new StringRepresentation(status.toString(),MediaType.TEXT_PLAIN);
		else {
	    	StringWriter w = new StringWriter();
	    	status.getThrowable().printStackTrace(new PrintWriter(w));

	    	return new StringRepresentation(w.toString(),MediaType.TEXT_PLAIN);	
		}
	}
	/*

	@Override
	public Representation getRepresentation(Status status, Request request,
			Response response) {
		try {
			boolean wrapInHTML = true;
			
			if ((status.getThrowable() !=null) && (status.getThrowable() instanceof RResourceException)) 
				wrapInHTML = ((RResourceException)status.getThrowable()).getVariant().equals(MediaType.TEXT_HTML);
			else {
				Form headers = (Form) request.getAttributes().get("org.restlet.http.headers"); 
				String acceptHeader = headers.getValues("accept");
				if (acceptHeader!=null)
					wrapInHTML = acceptHeader.contains("text/html");
			}
			
			if (wrapInHTML) {
				StringWriter w = new StringWriter();
				AmbitResource.writeHTMLHeader(w, status.getName(), request,
						request.getOriginalRef()==null?request.getResourceRef():request.getOriginalRef(),
						null);
				
				
				w.write(String.format("<h4>%s</h4>",
						status.getDescription()
						));
				if (status.getThrowable()!= null) {
					w.write("<blockquote>");				
					status.getThrowable().printStackTrace(new PrintWriter(w) {
						@Override
						public void print(String s) {
							super.print(String.format("%s<br>", s));
							
						}
					});
					w.write("</blockquote>");
					AmbitResource.writeHTMLFooter(w, status.getName(), request);
					
				 
				} 
				return new StringRepresentation(w.toString(),MediaType.TEXT_HTML);
			} else {
				if ((status.getThrowable() !=null) && (status.getThrowable() instanceof RResourceException)) 
					return ((RResourceException)status.getThrowable()).getRepresentation();
			}


		} catch (Exception x) {
			
		}
		if (status.getThrowable()==null)
			return new StringRepresentation(status.toString(),MediaType.TEXT_PLAIN);
		else {
	    	StringWriter w = new StringWriter();
	    	status.getThrowable().printStackTrace(new PrintWriter(w));

	    	return new StringRepresentation(w.toString(),MediaType.TEXT_PLAIN);	
		}
	}
	*/
	@Override
	public Status getStatus(Throwable throwable, Request request,
			Response response) {
		if (throwable == null) return response.getStatus();
		else if (throwable instanceof ResourceException) {
			return ((ResourceException)throwable).getStatus();
		} else return new Status(Status.SERVER_ERROR_INTERNAL,throwable);
	}
}
