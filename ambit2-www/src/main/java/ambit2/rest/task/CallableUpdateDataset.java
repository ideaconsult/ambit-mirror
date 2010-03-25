package ambit2.rest.task;

import java.sql.Connection;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.SourceDataset;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.ProcessorsChain;
import ambit2.db.processors.RepositoryWriter;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.OpenTox;
import ambit2.rest.dataset.DatasetURIReporter;

public class CallableUpdateDataset extends	CallableQueryProcessor<Object, String> {
	protected SourceDataset dataset;
	protected boolean clearPreviousContent = false;
	protected Template template;
	protected Reference applicationRootReference;
	protected DatasetURIReporter<IQueryRetrieval<SourceDataset>> datasetUriReporter;
	
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
			DatasetURIReporter<IQueryRetrieval<SourceDataset>> datasetUriReporter
			) throws ResourceException {
		super(form, context);
		this.applicationRootReference = applicationRootReference;
		String datasetURI = form.getFirstValue(OpenTox.params.dataset_uri.toString());
		if (datasetURI==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,OpenTox.params.dataset_uri.getDescription());
		sourceReference = new Reference(datasetURI);
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
	protected Reference createReference(Connection connection) throws Exception {
		return new Reference(datasetUriReporter.getURI(dataset));
	}

	@Override

	protected Object createTarget(Reference reference) throws Exception {
		return getQueryObject(reference, applicationRootReference);
	}
}
