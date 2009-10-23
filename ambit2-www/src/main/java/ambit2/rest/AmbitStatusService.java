package ambit2.rest;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.service.StatusService;

public class AmbitStatusService extends StatusService {
	public AmbitStatusService() {
		super();
		setContactEmail("nina@acad.bg");
		
	}
	@Override
	public Representation getRepresentation(Status status, Request request,
			Response response) {
		try {
			StringWriter w = new StringWriter();
			AmbitResource.writeHTMLHeader(w, status.getName(), request);
			
			w.write(String.format("Error :<br>Code: %d<br>Name: %s<br>URI: %s<br>Description: %s<br>",
					status.getCode(),
					status.getName(),
					status.getUri(),
					status.getDescription()
					));

			AmbitResource.writeHTMLFooter(w, status.getName(), request);
	
			return new StringRepresentation(w.toString(),MediaType.TEXT_HTML); 
		} catch (Exception x) {
			return new StringRepresentation(status.getDescription(),MediaType.TEXT_PLAIN); 
		}
	}
	@Override
	public Status getStatus(Throwable throwable, Request request,
			Response response) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		throwable.printStackTrace(printWriter);
		return response.getStatus(); //new Status(Status.SERVER_ERROR_INTERNAL, result.toString());
	}
}
