package ambit2.fastox;

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;

import org.restlet.resource.ResourceException;

import ambit2.fastox.users.IToxPredictUser;
import ambit2.fastox.users.MemoryUsersStorage;
import ambit2.fastox.wizard.Wizard.SERVICE;
import ambit2.fastox.wizard.WizardResource;

public class AdminResource extends WizardResource {
	
	public AdminResource() throws ResourceException {
		super(0);
	}

	@Override
	public void renderFormContent(Writer writer, String key) throws IOException {
		writer.write("<h2>ToxPredict services configuration</h2>");
		writer.write("ToxPredict uses remote OpenTox services to retrieve and store data and perform QSAR estimations.");
		
		writer.write(String.format("<table>"));
		writer.write(String.format("<caption>Services</caption>"));
		for (SERVICE service : SERVICE.values()) {
			writer.write(String.format("<tr><th>%s</th><td><a href='%s' target='_blank'>%s</a></td></tr>",
					service.toString(),
					wizard.getService(service),
					wizard.getService(service)));	
		}
		writer.write(String.format("</table>"));
		
		writer.write(String.format("<h4>This user</h4> %s<br>",session));
		
		writer.write(String.format("<h4>%s</h4>","All users"));
		Enumeration<IToxPredictUser> users = MemoryUsersStorage.getInstance().users();
		while (users.hasMoreElements()) {
			IToxPredictUser user = users.nextElement();	
			writer.write(String.format("<a href='%s/user/%s' target='blank'>%s</a>%s<br>",getRootRef(),
					user.getId(),
					user.getName(),
					MemoryUsersStorage.getInstance().getSession(user).toString()));
		}
		
		
		writer.write(String.format("<h2><a href='%s'>%s</a></h2>",getRequest().getReferrerRef(),"Back"));
		
	}
	@Override
	public void renderResults(Writer writer, String key) throws IOException {
	}
	@Override
	public String toString() {

		return "ToxPredict configuration";
	}
	
}
