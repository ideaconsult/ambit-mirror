package ambit2.fastox.steps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.fastox.DatasetTools;
import ambit2.fastox.users.UserResource;
import ambit2.fastox.wizard.WizardResource;

import com.hp.hpl.jena.rdf.model.Model;

public abstract class FastoxStepResource extends WizardResource {
	protected Model store = null;
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
		model_name;
		
		public String htmlInputHidden(String value) {
			return String.format("<input name='%s' type='hidden' value='%s'>\n",toString(),value);
		}
		public String htmlInputText(String value) {
			return String.format("<input name='%s' type='text' value='%s'>\n",toString(),value);
		}		
		public String htmlInputCheckbox(String value,String title) {
			return
				String.format("<input type='checkbox' checked name='%s'>%s\n<input type='hidden' name='%s' value='%s'>",
						value,title==null?value:title,toString(),value);
		}		
	};			
		

		
	public FastoxStepResource(int stepIndex) {
		super(stepIndex);
	}
	protected boolean isMandatory(String param) {
		return params.dataset.toString().equals(param);
	}
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
	}
	@Override
	protected void doRelease() throws ResourceException {
		try {
			if (store != null) store.close();
		} catch (Exception x) {
		}
		super.doRelease();
	}
	@Override
	protected Representation get(Variant variant) throws ResourceException {
		if (session.getDatasetURI() == null) {
			if (isMandatory(params.dataset.toString())) {
				
				redirectTemporary(String.format("%s/%s/%s/%s%s",
					getRequest().getRootRef(),
					UserResource.resource,
					Reference.encode(session.getUser().getId()),
					mode,
					wizard.getStep(1).getResource()
					));
				//todo error
			}
		}
		return super.get(variant);
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
			writer.write("<br style='clear:both;' clear='all' />\n"); // Safari is not happy otherwise with floating elements
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
	

	protected void renderCompounds(Writer writer) {
		Form form = getRequest().getResourceRef().getQueryAsForm();
		try {
			writer.write("<br style='clear:both;' clear='all' />\n"); // Safari is not happy otherwise with floating elements
			writer.write("<table class='results'>");
			store = DatasetTools.retrieveDataset(null,session.getDatasetURI());
			DatasetTools.renderDataset(store,writer,"",getRequest().getRootRef());
			writer.write("</table>");
		} catch (Exception x) {
			session.setError(x);
		} 		
	}

	@Override
	protected Representation processForm(Representation entity, Variant variant)
			throws ResourceException {
		return super.processForm(entity, variant);
	}
	
	
}
