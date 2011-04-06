package ambit2.fastox.steps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Iterator;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import ambit2.fastox.ModelTools;
import ambit2.fastox.users.IToxPredictSession;
import ambit2.fastox.users.UserResource;
import ambit2.fastox.wizard.Wizard.SERVICE;
import ambit2.fastox.wizard.WizardResource;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.Syntax;

public abstract class FastoxStepResource extends WizardResource {

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
	/*
	protected int renderCompounds1(Writer writer,String key) throws IOException {
		try {
			writer.write("<br style='clear:both;' clear='all' />\n"); // Safari is not happy otherwise with floating elements
			writer.write("<table class='results'>");
			store = DatasetTools.retrieveDataset(null,session.getSearchQuery());
			int records = DatasetTools.renderDataset1(session,store,writer,"",getRequest().getRootRef(),session.getSearch(),session.getCondition());
			writer.write("</table>");
			if (records ==0) session.setError(key,new NotFoundException("No compounds found"));
			return records;
		} catch (Exception x) {
			session.setError(key,x);
			throw new IOException(x.getMessage());
		} 		
	}	
	*/
	protected void renderCompoundsNew(Writer writer,String key, boolean showPredictedResults,boolean showEndpoints) throws IOException {
		ClientResource client = null;
		Representation r = null;
		try {
			Reference ref = new Reference(
					String.format("%s/user/%s/report/Dataset?header=false&page=0&pagesize=1&endpoints=%s&models=%s&search=%s",
							getRequest().getRootRef(),
							session.getUser().getId(),
							showEndpoints?"TRUE":"FALSE",
							showPredictedResults?"TRUE":"FALSE",
							Reference.encode(session.getDatasetURI()))
			);
			System.out.println(ref);
			client = new ClientResource(ref);
			r = client.get(MediaType.TEXT_HTML);
			if (Status.SUCCESS_OK.equals(client.getStatus()) && (r.isAvailable()))
				writer.write(r.getText());
			else throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

		} catch (Exception x) {
			session.setError(key,x);
			throw new IOException(x.getMessage());
		} finally {
			try { r.release();} catch (Exception x) {}
			try { client.release();} catch (Exception x) {}
		}
		
	}
	/*
	protected int renderCompounds(Writer writer,String key) throws IOException {
		try {
			writer.write("<br style='clear:both;' clear='all' />\n"); // Safari is not happy otherwise with floating elements
			writer.write("<table class='results'>");
			store = DatasetTools.retrieveDataset(null,session.getSearchQuery());
			int records = DatasetTools.renderDataset(store,writer,"",getRequest().getRootRef());
			writer.write("</table>");
			if (records ==0) session.setError(key,new NotFoundException("No compounds found"));
			return records;
		} catch (Exception x) {
			session.setError(key,x);
			throw new IOException(x.getMessage());
		} 		
	}
	*/
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
				Query query = QueryFactory.create(String.format(ModelTools.sparql,q,q,q,q, q,q,q,q,q));
				running += retrieveModels(null,query,writer,status,rootReference);
			} else {
				Iterator<String> models = session.getModels();
				while (models.hasNext()) {
					String model = models.next();
					String q = String.format("<%s>",model);
					Query query = QueryFactory.create(String.format(ModelTools.sparql,"",q,q,q,q, q,q,""),null,Syntax.syntaxARQ);

					running += retrieveModels(model,query,writer,status,rootReference);
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			writer.write("</tbody></table>");
			//writer.write("</form>");
			//writer.write(jsPager("mpager",new String[]{"10","20","30","40"}));
		}
		
		return running;
	}		
	protected int retrieveModels(String modelURI,Query query,Writer writer,boolean status,Reference rootReference) throws Exception {
		//QueryExecution qe = null;
		ClientResource resource = null;
		Representation r = null;
		int running = 0;
		try {
			resource = new ClientResource(new Reference(wizard.getService(SERVICE.ontology).toString()));
			Form form = new Form();
			form.add("query", query.toString());
			r = resource.post(form.getWebRepresentation(),MediaType.APPLICATION_SPARQL_RESULTS_XML);
//			qe = QueryExecutionFactory.sparqlService(wizard.getService(SERVICE.ontology).toString(), query);
			//engine = QueryExecutionFactory.createServiceRequest(wizard.getService(SERVICE.ontology).toString(), query);
			
			//ResultSet results = engine.execSelect(); //qe.execSelect();

			ResultSet results = ResultSetFactory.fromXML(r.getText());
			//ResultSet results = ResultSetFactory.fromXML(r.getStream());
			int record = 0;
			while (results.hasNext()) {
				record++;
				QuerySolution solution = results.next();
				writer.write(String.format("<tr class='%s'>",(record % 2)==0?"results_even":"results_odd"));
				running+=ModelTools.renderModelTableRow(modelURI,writer,solution,session,status,rootReference,record);
				writer.write("</tr>");
			}
			
		}catch (Exception x) {
			System.err.println(query);
			x.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,x.getMessage()+ wizard.getService(SERVICE.ontology).toString(),x);
		} finally {
			try {r.release();} catch (Exception x) {}
		}		
		return running;
	}
	
	@Override
	public void renderResults(Writer writer, String key) throws IOException {
	}
	
}
