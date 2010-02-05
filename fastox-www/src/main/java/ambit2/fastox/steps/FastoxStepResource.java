package ambit2.fastox.steps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

import org.restlet.data.Form;
import org.restlet.data.Status;

import ambit2.fastox.wizard.WizardResource;

public abstract class FastoxStepResource extends WizardResource {
	public enum params {
		search,
		text,
		dataset,
		compound,
		query,
		endpoint,
		endpoint_name,
		model,
		model_name,
		errors;
		public String htmlInputHidden(String value) {
			return String.format("<input name='%s' type='hidden' value='%s'>\n",toString(),value);
		}
		public String htmlInputText(String value) {
			return String.format("<input name='%s' type='text' value='%s'>\n",toString(),value);
		}		
		public String htmlInputCheckbox(String value) {
			return
				String.format("<input type='checkbox' checked name='%s'>%s\n<input type='hidden' name='%s' value='%s'>",
						value,value,toString(),value);
		}		
	};			
		
	public FastoxStepResource(String title, String prevStep, String nextStep) {
		super(title, prevStep, nextStep);
	}
	protected String readUriList(InputStream in) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = reader.readLine()) != null) { return line; }
			
		} catch (Exception x) {
			
		} finally {
			try {in.close(); } catch (Exception x) {}
		}
		return null;
	}
	protected String renderCSV(InputStream in, Writer writer) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = reader.readLine()) != null) { 
				writer.write("<br>");
				writer.write(line);

			}
			
		} catch (Exception x) {
			
		} finally {
			try {in.close(); } catch (Exception x) {}
		}
		return null;
	}	
	
	protected int renderModels(Form form, Writer writer, boolean status) throws IOException {
		   int running = 0;
			String[] models = form.getValuesArray(params.model.toString());
			writer.write("<table class='models'>");
			writer.write("<tr><th align='left'>Model</th>");
			if (status)	writer.write("<th align='left'>Status</th><th align='left'>Results</th>");
			writer.write("</tr>");
			for (String model:models) {
				writer.write("<tr>");
				writer.write("<td>");
				writer.write(params.model.htmlInputCheckbox(model));
				writer.write("</td>");
				if (!status) continue;
				String[] uris = form.getValuesArray(model);
				//writer.write("<table>");
				
				for (String uri:uris) {
					//writer.write("<tr>");
					String[] tasks = form.getValuesArray(uri);
					writer.write("<td>");
					int isRunning = 0;
					for (String task:tasks) {
						if (task.equals(Status.SUCCESS_ACCEPTED.getName()) || task.equals(Status.REDIRECTION_SEE_OTHER.getName()))
							isRunning++;
						writer.write(String.format("%s",task));
					}
					running += isRunning;
					writer.write("</td>");
					writer.write("<td>");
					writer.write(String.format("<a href='%s'>%s</a>",uri,(isRunning==0)?"Results":"Still running"));
					writer.write(String.format("<input type='hidden' name='%s' value='%s'>", model,uri));	
					writer.write("</td>");					
					//writer.write("</tr>");
				}
				//writer.write("</table>");
				writer.write("</tr>");
			}	
			writer.write("</table>");
			return running;
	}
}
