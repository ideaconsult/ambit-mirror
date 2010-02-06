package ambit2.fastox.steps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import ambit2.fastox.steps.step1.Step1Resource;
import ambit2.fastox.wizard.WizardResource;

public abstract class FastoxStepResource extends WizardResource {
	protected String dataset= null;
	public enum params {
		search,
		text,
		dataset,
		compound,
		query,
		parentendpoint,
		parentendpoint_name,
		endpoint,
		endpoint_name,
		subendpoint,
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
	protected boolean isMandatory(String param) {
		return params.dataset.toString().equals(param);
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		Form form = getRequest().getResourceRef().getQueryAsForm();
		dataset = form.getFirstValue(params.dataset.toString());		
	}
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		if (dataset == null) {
			Form form = getRequest().getResourceRef().getQueryAsForm();
			dataset = form.getFirstValue(params.dataset.toString());
			if ((dataset==null) && (isMandatory(params.dataset.toString()))) {
				redirectSeeOther(String.format("%s%s",
					getRequest().getRootRef(),Step1Resource.resource
					));
				//todo error
			}
		}
		return super.get(variant);
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
	
	protected void processURI(String line,Writer writer)  throws IOException {
		writer.write("<tr>");
		writer.write("<td>");
		writer.write(line);
		writer.write("</td>");
		writer.write("</tr>");
	}
	protected int processURIList(InputStream in,Writer writer) throws Exception {
		int count = 0;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = reader.readLine()) != null) {
				processURI(line,writer);
				count++;
			}
		} catch (Exception x) {
			throw x;
		} finally {
			try {in.close(); } catch (Exception x) {}
		}
		return count;
	}	
	protected String renderCSV(InputStream in, Writer writer) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			String td = "th";
			writer.write("<table class='results'>");
			while ((line = reader.readLine()) != null) { 
				writer.write("<tr>");
				String[] cols = line.split(",");
				for (String col:cols)
					writer.write(String.format("<%s>%s</%s>",td,col.replace("\"",""),td));
//				writer.write(line);
				writer.write("</tr>");
				td = "td";
			}
			writer.write("</table>");
		} catch (Exception x) {
			
		} finally {
			try {in.close(); } catch (Exception x) {}
		}
		return null;
	}	
	
	protected int renderModels(Form form, Writer writer, boolean status) throws IOException {
		   int running = 0;
			String[] models = form.getValuesArray(params.model.toString());
			if (models.length==0) {
				writer.write("<div class='message'>No models</div>");
				return 0;
			}
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
					if ("on".equals(uri)) continue;
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
	
	protected void renderCompounds(Writer writer) {
		Form form = getRequest().getResourceRef().getQueryAsForm();
		try {
			writer.write(params.dataset.htmlInputHidden(dataset));
			writer.write("<table class='results'>");
			retrieveDataset(dataset,writer);
			writer.write("</table>");
		} catch (Exception x) {
			form.removeAll(params.errors.toString());
			form.add(params.errors.toString(),x.getMessage());
		} 		
	}
	public int retrieveDataset(String datasetURI,Writer writer) throws Exception {
		Representation r = null;
		try {
			ClientResource client = new ClientResource(datasetURI);
			
			r = client.get(MediaType.TEXT_RDF_N3);
			if (r.isAvailable())
				
				//return processURIList(r.getStream(),writer);
			
			throw new Exception("Representation not available");
		}catch (Exception x) {
			throw x;
		} finally {
			try {r.release();} catch (Exception x) {}
		}		
	}
	
	
}
