package ambit2.rest.task;

import java.io.PrintWriter;
import java.sql.Connection;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.ProcessorsChain;
import ambit2.db.SessionID;
import ambit2.db.UpdateExecutor;
import ambit2.db.processors.ProcessorCreateQuery;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.db.update.assessment.CreateAssessment;
import ambit2.rest.DBConnection;
import ambit2.rest.OpenTox;
import ambit2.rest.dataset.DatasetStructuresResource;

public class CallableQueryResultsCreator< Result> extends CallableQueryProcessor<Object, Result> {
	protected IStoredQuery storedQuery;
	protected boolean clearPreviousContent = false;
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
			IStoredQuery storedQuery) throws ResourceException {
		super(form, applicationRootReference, context);
		String dataset = form.getFirstValue(OpenTox.params.dataset_uri.toString());
		if (dataset==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,OpenTox.params.dataset_uri.getDescription());
		sourceReference = new Reference(dataset);
		this.storedQuery = storedQuery;
	}
	
	@Override
	public Reference call() throws Exception {
		target = createTarget(sourceReference);
		if (target == null) throw new Exception("");
		if (target instanceof AbstractStructureQuery) {

			return createQueryResults((AbstractStructureQuery)target);
		} else {
			//return new RDFStructuresReader(target.toString());
			throw new Exception("Not implemented");
		}
			
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
