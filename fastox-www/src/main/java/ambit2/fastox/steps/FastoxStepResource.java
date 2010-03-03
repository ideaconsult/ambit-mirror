package ambit2.fastox.steps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Iterator;

import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

import ambit2.base.exceptions.NotFoundException;
import ambit2.fastox.DatasetTools;
import ambit2.fastox.ModelTools;
import ambit2.fastox.users.IToxPredictSession;
import ambit2.fastox.users.UserResource;
import ambit2.fastox.wizard.WizardResource;
import ambit2.fastox.wizard.Wizard.SERVICE;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

public abstract class FastoxStepResource extends WizardResource {
	protected Model store = null;
	public enum params {
		search,
		max,
		mode,
		file,
		text,
		threshold,
		dataset,
		condition,
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
		public String htmlInputCheckbox(String value,String title,boolean checked) {
			return
				String.format("<input type='checkbox' %s name='%s'><b>%s</b>\n<input type='hidden' name='%s' value='%s'>",
						checked?"checked='checked'":"",value,title==null?value:title,toString(),value);
		}			
		public String htmlInputCheckbox(String value,String title) {
			return htmlInputCheckbox(value, title,true);
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
	
	protected int renderCompounds1(Writer writer,String key) throws IOException {
		try {
			writer.write("<br style='clear:both;' clear='all' />\n"); // Safari is not happy otherwise with floating elements
			writer.write("<table class='results'>");
			store = DatasetTools.retrieveDataset(null,session.getDatasetURI());
			int records = DatasetTools.renderDataset1(store,writer,"",getRequest().getRootRef(),session.getSearch(),session.getCondition());
			writer.write("</table>");
			if (records ==0) session.setError(key,new NotFoundException("No compounds found"));
			return records;
		} catch (Exception x) {
			session.setError(key,x);
			throw new IOException(x.getMessage());
		} 		
	}	

	protected int renderCompounds(Writer writer,String key) throws IOException {
		try {
			writer.write("<br style='clear:both;' clear='all' />\n"); // Safari is not happy otherwise with floating elements
			writer.write("<table class='results'>");
			store = DatasetTools.retrieveDataset(null,session.getDatasetURI());
			int records = DatasetTools.renderDataset(store,writer,"",getRequest().getRootRef());
			writer.write("</table>");
			if (records ==0) session.setError(key,new NotFoundException("No compounds found"));
			return records;
		} catch (Exception x) {
			session.setError(key,x);
			throw new IOException(x.getMessage());
		} 		
	}

	@Override
	protected Representation processForm(Representation entity, Variant variant)
			throws ResourceException {
		return super.processForm(entity, variant);
	}
	
	public int renderRDFModels(Writer writer, IToxPredictSession session,
			 boolean status,Reference rootReference, boolean all) throws Exception {
		int running = 0;
		//session.clearModels();
		ModelTools.renderModelTableCaption(writer,status);
		try {
			if ((session.getNumberOfModels()==0) || all)  {
				String q = "?url";
				Query query = QueryFactory.create(String.format(ModelTools.sparql,q,q,q,q, q,q,q,q));
				running += retrieveModels(null,query,writer,status,rootReference);
			} else {
				Iterator<String> models = session.getModels();
				while (models.hasNext()) {
					String model = models.next();
					String q = String.format("<%s>",model);
					Query query = QueryFactory.create(String.format(ModelTools.sparql,"",q,q,q, q,q,q,""));
					running += retrieveModels(model,query,writer,status,rootReference);
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			writer.write("</table>");
		}
		
		return running;
	}		
	protected int retrieveModels(String modelURI,Query query,Writer writer,boolean status,Reference rootReference) throws Exception {
		QueryExecution qe = null;
		int running = 0;
		try {
			
			qe = QueryExecutionFactory.sparqlService(wizard.getService(SERVICE.ontology).toString(), query);
			ResultSet results = qe.execSelect();
			
			int record = 0;
			while (results.hasNext()) {
				record++;
				QuerySolution solution = results.next();
				writer.write(String.format("<tr class='%s'>",(record % 2)==0?"results_even":"results_odd"));
				running+=ModelTools.renderModelTableRow(modelURI,writer,solution,session,status,rootReference,record);
				writer.write("</tr>");
			}
			
		}catch (Exception x) {
			throw x;
		} finally {
			try {qe.close();} catch (Exception x) {}
		}		
		return running;
	}
	
	@Override
	public void renderResults(Writer writer, String key) throws IOException {
	}
	
}
