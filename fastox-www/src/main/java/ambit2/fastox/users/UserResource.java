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
		writer.write("<div>");
		writer.write(session.getUser().toString());
		writer.write("<h4>Dataset</h4>");
		writer.write(session.getDatasetURI()==null?"No dataset selected":session.getDatasetURI());
		writer.write("<h4>Models</h4><ul>");

		Iterator<String> models = session.getModels();
		if (models != null) 
			while (models.hasNext()) {
				writer.write("<li>");
				String model = models.next();
				writer.write(model);
				writer.write("&nbsp;");
				writer.write(session.getModelStatus(model).toString());
			}
		writer.write("</ul>");
		writer.write("</div>");
		writer.write(String.format("<h2><a href='%s'>%s</a></h2>",getRequest().getRootRef(),"Go to ToxPredict"));
	}
	@Override
	public void renderResults(Writer writer, String key) throws IOException {
	}
}
