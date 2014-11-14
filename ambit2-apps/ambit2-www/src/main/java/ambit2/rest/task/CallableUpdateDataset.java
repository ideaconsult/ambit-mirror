package ambit2.rest.task;

import java.sql.Connection;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.batch.IBatchStatistics;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.processors.ProcessorsChain;
import net.idea.modbcum.p.batch.AbstractBatchProcessor;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.Template;
import ambit2.db.DbReaderStructure;
import ambit2.db.processors.RepositoryWriter;
import ambit2.db.search.structure.AbstractStructureQuery;
import ambit2.rest.OpenTox;
import ambit2.rest.dataset.DatasetURIReporter;
import ambit2.rest.dataset.RDFStructuresReader;
import ambit2.rest.uri.URIStructureIterator;

public class CallableUpdateDataset<USERID> extends	CallableQueryProcessor<Object, String,USERID> {
	protected String[] compounds = null;
	protected SourceDataset dataset;
	protected boolean clearPreviousContent = false;
	protected Template template;
	protected Reference applicationRootReference;
	protected DatasetURIReporter<IQueryRetrieval<ISourceDataset>,ISourceDataset> datasetUriReporter;
	
	public boolean isClearPreviousContent() {
		return clearPreviousContent;
	}

	public void setClearPreviousContent(boolean clearPreviousContent) {
		this.clearPreviousContent = clearPreviousContent;
	}
	public CallableUpdateDataset(
			Form form,
			Reference applicationRootReference, 
			Context context,
			SourceDataset dataset,
			DatasetURIReporter<IQueryRetrieval<ISourceDataset>,ISourceDataset> datasetUriReporter,
			USERID token
			) throws ResourceException {
		super(form, context,token);
		this.applicationRootReference = applicationRootReference;
		compounds = form.getValuesArray(OpenTox.params.compound_uris.toString());

		String datasetURI = form.getFirstValue(OpenTox.params.dataset_uri.toString());
		if ((datasetURI==null) && ((compounds==null) || (compounds.length==0))) 
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
						OpenTox.params.dataset_uri.getDescription());

		sourceReference = datasetURI==null?null:new Reference(datasetURI);
		this.dataset = dataset;
		this.template = null;
		this.datasetUriReporter = datasetUriReporter;
	}
	
	@Override
	protected ProcessorsChain<String, IBatchStatistics, IProcessor> createProcessors()
			throws Exception {
		final RepositoryWriter writer = new RepositoryWriter();
		writer.setDataset(dataset);
		final ProcessorsChain<String, IBatchStatistics, IProcessor> chain = new ProcessorsChain<String, IBatchStatistics, IProcessor>();
		chain.add(writer);
		return chain;
	}

	@Override
	protected TaskResult createReference(Connection connection) throws Exception {
		return new TaskResult(datasetUriReporter.getURI(dataset));
	}

	@Override

	protected Object createTarget(Reference reference) throws Exception {
		
		return reference==null?compounds:getQueryObject(reference, applicationRootReference,context);
	}
	@Override
	protected AbstractBatchProcessor createBatch(Object target)
			throws Exception {
		if (target == null) throw new Exception("");
		if (target instanceof String[]) return 
				new URIStructureIterator((String[]) target,applicationRootReference);
		else if (target instanceof AbstractStructureQuery) {
			DbReaderStructure reader = new DbReaderStructure();
			reader.setHandlePrescreen(true);
			return reader;
		} else
			return new RDFStructuresReader(target.toString());
	}	
}
