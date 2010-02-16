package ambit2.rest.task;

import java.io.PrintWriter;
import java.sql.Connection;
import java.util.Iterator;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.ProcessorsChain;
import ambit2.db.SessionID;
import ambit2.db.UpdateExecutor;
import ambit2.db.processors.ProcessorCreateQuery;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.db.update.assessment.CreateAssessment;
import ambit2.rest.AbstractResource;
import ambit2.rest.DBConnection;
import ambit2.rest.OpenTox;
import ambit2.rest.dataset.DatasetStructuresResource;
import ambit2.rest.rdf.RDFPropertyIterator;

public class CallableQueryResultsCreator< Result> extends CallableQueryProcessor<Object, Result> {
	protected IStoredQuery storedQuery;
	protected boolean clearPreviousContent = false;
	protected Template template;
	
	public boolean isClearPreviousContent() {
		return clearPreviousContent;
	}

	public void setClearPreviousContent(boolean clearPreviousContent) {
		this.clearPreviousContent = clearPreviousContent;
	}

	public CallableQueryResultsCreator(
			Form form,
			Reference applicationRootReference, 
			Context context,
			IStoredQuery storedQuery
			) throws ResourceException {
		super(form, applicationRootReference, context);
		String dataset = form.getFirstValue(OpenTox.params.dataset_uri.toString());
		if (dataset==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,OpenTox.params.dataset_uri.getDescription());
		sourceReference = new Reference(dataset);
		this.storedQuery = storedQuery;
		this.template = null;
	}
	
	/**
	 * This is a hack to retrieve features only, 
	 * so we tell the dataset service to return only the first structure
	 * @return
	 * @throws Exception
	 */
	protected Template retrieveTemplate() throws Exception {
		Form form = sourceReference.getQueryAsForm();
		String max[] = form.getValuesArray(AbstractResource.max_hits);
		if ((max != null) && (max.length>0)) 
			form.removeAll(AbstractResource.max_hits);
		
		form.add(AbstractResource.max_hits,"1");
		Reference ref = new Reference(sourceReference);
		ref.setQuery(form.getQueryString());
		
		if(template == null) template = new Template(null);
		RDFPropertyIterator.readFeaturesRDF(ref.toString(), template, applicationRootReference);
	
		Iterator<Property> properties = template.getProperties(true);
		while (properties.hasNext()) {
			System.out.println(properties.next());
		}
		return template;
	}
	
	@Override
	public Reference call() throws Exception {
		target = createTarget(sourceReference);
		if (target == null) throw new Exception("");
		
		if (applicationRootReference.isParent(sourceReference)) {
			if (target instanceof AbstractStructureQuery) {
				template = retrieveTemplate();
				return createQueryResults((AbstractStructureQuery)target);
			} else {
				//return new RDFStructuresReader(target.toString());
				throw new Exception("Not implemented");
			}
		} else throw new Exception(String.format(
				"Copy of external datasets not allowed, use POST to %s/dataset instead",
				applicationRootReference
				));
			
	}
	protected Reference createQueryResults(AbstractStructureQuery target) throws Exception {
		Connection connection = null;
		UpdateExecutor xx = new UpdateExecutor();
		try {
			DBConnection dbc = new DBConnection(context);
			connection = dbc.getConnection();
			SessionID session = new SessionID();
			session.setName("temp");
			
			CreateAssessment assessment = new CreateAssessment();
			assessment.setObject(session);

			xx.setConnection(connection);
			xx.process(assessment);
			 
			ProcessorCreateQuery p = new ProcessorCreateQuery();
			p.setProfile(template);
			p.setDelete(clearPreviousContent);
			p.setCopy(true);
			p.setStoredQuery(storedQuery);
			p.setSession(session);
			p.setConnection(connection);
			IStoredQuery q = p.process((AbstractStructureQuery)target);
			
			return new Reference(String.format("%s/dataset/%s%d",
					applicationRootReference,DatasetStructuresResource.QR_PREFIX,q.getId()));
		} catch (Exception x) {

            java.io.StringWriter stackTraceWriter = new java.io.StringWriter();
            x.printStackTrace(new PrintWriter(stackTraceWriter));
			Context.getCurrentLogger().severe(stackTraceWriter.toString());
			throw x;
		} finally {
			Context.getCurrentLogger().info("Done");
			try { xx.close(); } catch (Exception x) {}
			try { connection.close(); } catch (Exception x) {Context.getCurrentLogger().warning(x.getMessage());}
		}			
	}
	@Override
	protected ProcessorsChain<Result, IBatchStatistics, IProcessor> createProcessors()
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Reference createReference(Connection connection) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object createTarget(Reference reference) throws Exception {
		return getQueryObject(reference, applicationRootReference);
	}

}
