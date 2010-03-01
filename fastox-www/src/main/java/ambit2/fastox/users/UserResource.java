package ambit2.fastox.users;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.restlet.resource.ResourceException;

import ambit2.fastox.wizard.WizardResource;

public class UserResource extends WizardResource {
	public static final String resource = "user";
	public static final String resourceKey = "user_name";
	public UserResource() throws ResourceException {
		super(0);
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
	}
	@Override
	public void renderFormContent(Writer writer, String key) throws IOException {
		writer.write(String.format("<h2>Welcome, %s</h2>",session.getUser().getName()));
		writer.write("Sorry, ToxPredict application doesn't support user accounts yet, this is currently under development.<br>");
		writer.write("The only available and default user is <b>guest</b><br>");
		writer.write("<br>");
		writer.write("<div class='help'>");
		writer.write(session.getUser().toString());
		writer.write("<h4>Dataset</h4>");
		if (session.getDatasetURI()==null) writer.write("No dataset selected");
		else writer.write(String.format("<a href='%s' target=_blank>%s</a>",session.getDatasetURI(),session.getDatasetURI()));
		writer.write("<h4>Models</h4><table>");

		Iterator<String> models = session.getModels();
		if (models != null) 
			while (models.hasNext()) {
				writer.write("<tr>");
				String model = models.next();

				writer.write(String.format("<th><a href='%s' target=_blank>%s</a></th>",model,model));
				writer.write("<td>");
				writer.write(session.getModelStatus(model).toString());
				writer.write("</td></tr>");
			}
		writer.write("</table>");
		writer.write("<h4>Endpoints</h4><ul>");
		
		writer.write(session.getEndpointName());writer.write("&nbsp;");
		writer.write(session.getEndpoint());
		
		writer.write("<h4>Error</h4>");
		Iterator<String> keys = session.getErrorKeys();
		if (keys!=null)	while (keys.hasNext()) {
				String k = keys.next(); 
				writer.write(String.format("<div class='errors'>%s</div><br>",session.getError(k)==null?"":session.getError(k).getMessage()));
		}
		writer.write("</div>");
		writer.write(String.format("<h2><a href='%s'>%s</a></h2>",getRequest().getRootRef(),"Go to ToxPredict"));
	}
	@Override
	public void renderResults(Writer writer, String key) throws IOException {
	}
}
