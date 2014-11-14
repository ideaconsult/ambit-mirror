package ambit2.rest.task;

import java.sql.Connection;
import java.util.UUID;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryObject;
import net.idea.modbcum.i.batch.IBatchStatistics;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.processors.ProcessorsChain;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.SessionID;
import ambit2.db.UpdateExecutor;
import ambit2.db.processors.ProcessorCreateQuery;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.db.search.structure.QueryCombinedStructure;
import ambit2.db.update.queryfolder.CreateQueryFolder;
import ambit2.rest.AbstractResource;
import ambit2.rest.DBConnection;
import ambit2.rest.OpenTox;
import ambit2.rest.dataset.DatasetStructuresResource;
import ambit2.rest.rdf.RDFPropertyIterator;

public class CallableQueryResultsCreator< Result,USERID> extends CallableQueryProcessor<Object, Result,USERID> {
	protected IStoredQuery storedQuery;
	protected boolean clearPreviousContent = false;
	protected Template template;
	protected Reference applicationRootReference;
	protected String[] datasets;
	protected String[] features;
	protected String folder;
	protected String queryName;
	
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
			IStoredQuery storedQuery,
			USERID token
			) throws ResourceException {
		super(form, context,token);
		this.applicationRootReference = applicationRootReference;
		datasets = form.getValuesArray(OpenTox.params.dataset_uri.toString());
		features = form.getValuesArray(OpenTox.params.feature_uris.toString());
		if ((datasets==null) || (datasets[0]==null)) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,OpenTox.params.dataset_uri.getDescription());
		sourceReference = new Reference(datasets[0]);
		this.storedQuery = storedQuery;
		
		this.template = null;
	}
	
	@Override
	protected void processForm(Reference applicationRootReference, Form form) {
		super.processForm(applicationRootReference, form);
		folder = form.getFirstValue("folder");	if (folder==null) folder = "temp";
		queryName = form.getFirstValue("title"); if ("".equals(queryName)) queryName= null;
	}
	/**
	 * This is a hack to retrieve features only, 
	 * so we tell the dataset service to return only the first structure
	 * @return
	 * @throws Exception
	 */
	protected Template retrieveTemplate(Reference reference) throws Exception {
		Form form = reference.getQueryAsForm();
		String max[] = form.getValuesArray(AbstractResource.max_hits);
		if ((max != null) && (max.length>0)) 
			form.removeAll(AbstractResource.max_hits);
		
		form.add(AbstractResource.max_hits,"1");
		Reference ref = new Reference(reference);
		ref.setQuery(form.getQueryString());
		
		if(template == null) template = new Template(null);
		RDFPropertyIterator.readFeaturesRDF(ref.toString(), template, applicationRootReference);
		
		if ((features!=null) && features.length>0) {
			for (String feature : features)
				RDFPropertyIterator.readFeaturesRDF(feature, template, applicationRootReference);
		}

		return template;
	}
	
	@Override
	public TaskResult call() throws Exception {
		TaskResult ref  = null;
		for (String dataset:datasets)
			ref = call(new Reference(dataset));
		return ref;
	}

	public TaskResult call(Reference uri) throws Exception {
		final String msg = "Error when trying to copy the dataset at %s. Is this OpenTox dataset URI?";
		Object query = null;
		try {
			query = getQueryObject(uri, applicationRootReference,context);
		} catch (ResourceException x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,String.format(msg,uri),x);
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,String.format(msg,uri),x);
		};
		if (query == null) 
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,String.format(msg,uri));
		
		if (applicationRootReference.isParent(uri)) {
			if (query instanceof AbstractStructureQuery) {
				template = retrieveTemplate(uri);
				return createQueryResults((AbstractStructureQuery)query);
			} else if (query instanceof QueryCombinedStructure) {
					template = retrieveTemplate(uri);
					return createQueryResults((QueryCombinedStructure)query);				
			} else {
				//return new RDFStructuresReader(target.toString());
				throw new Exception(String.format(" Not implemented %s %s", query.getClass().getName(),uri));
			}
		} else throw new Exception(String.format(
				"Copy of external datasets not implemented yet, use POST to %s/dataset instead",
				applicationRootReference
				));
			
	}
	protected TaskResult createQueryResults(IQueryObject<IStructureRecord> target) throws Exception {
		Connection connection = null;
		UpdateExecutor xx = new UpdateExecutor();
		try {
			DBConnection dbc = new DBConnection(context);
			connection = dbc.getConnection();
			SessionID session = new SessionID();
			session.setName(folder);
			
			CreateQueryFolder qfolder = new CreateQueryFolder();
			qfolder.setObject(session);

			xx.setConnection(connection);
			xx.process(qfolder);
			 
			ProcessorCreateQuery p = new ProcessorCreateQuery();
			p.setQueryName(queryName==null?UUID.randomUUID().toString():queryName);
			p.setProfile(template);
			p.setDelete(clearPreviousContent);
			p.setCopy(true);
			p.setStoredQuery(storedQuery);
			p.setSession(session);
			p.setConnection(connection);
			IStoredQuery q = p.process((IQueryObject<IStructureRecord>)target);
			
			return new TaskResult(String.format("%s/dataset/%s%d",
					applicationRootReference,DatasetStructuresResource.QR_PREFIX,q.getId()));
		} catch (Exception x) {
			Context.getCurrentLogger().log(Level.SEVERE,x.getMessage(),x);
			throw x;
		} finally {
			Context.getCurrentLogger().fine("Done");
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
	protected TaskResult createReference(Connection connection) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object createTarget(Reference reference) throws Exception {
		return getQueryObject(reference, applicationRootReference,context);
	}

}
