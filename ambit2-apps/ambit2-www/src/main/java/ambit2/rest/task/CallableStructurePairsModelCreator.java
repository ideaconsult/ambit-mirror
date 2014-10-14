package ambit2.rest.task;

import java.util.UUID;

import net.idea.modbcum.i.batch.IBatchStatistics;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import ambit2.base.data.ILiteratureEntry._type;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.base.processors.ProcessorsChain;
import ambit2.core.data.model.Algorithm;
import ambit2.core.data.model.Algorithm.AlgorithmFormat;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.db.DbReader;
import ambit2.db.processors.AbstractBatchProcessor;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.search.IStoredQuery;
import ambit2.db.search.StoredQuery;
import ambit2.db.search.structure.QueryDatasetByID;
import ambit2.db.search.structure.QueryStoredResults;
import ambit2.db.search.structure.pairwise.PairPropertiesWriter;
import ambit2.db.search.structure.pairwise.ProcessorStructurePairsRetrieval;
import ambit2.db.search.structure.pairwise.QueryStructurePairs;
import ambit2.db.search.structure.pairwise.QueryStructurePairsDataset;
import ambit2.db.search.structure.pairwise.QueryStructurePairsRDataset;
import ambit2.descriptors.pairwise.SMSDProcessor;
import ambit2.rest.OpenTox;
import ambit2.rest.algorithm.AlgorithmURIReporter;
import ambit2.rest.model.ModelURIReporter;
import ambit2.rest.model.builder.AbstractStructuresModelBuilder;

public class CallableStructurePairsModelCreator<USERID> extends	
					CallableModelCreator<ModelQueryResults, IStructureRecord[], AbstractStructuresModelBuilder<ModelQueryResults>,USERID> {
	protected Reference applicationRootReference;
	protected LiteratureEntry prediction;
	
	public CallableStructurePairsModelCreator(Form form,
			Reference applicationRootReference,Context context,
			Algorithm algorithm,
			ModelURIReporter<IQueryRetrieval<ModelQueryResults>> reporter,
			AlgorithmURIReporter alg_reporter,
			AbstractStructuresModelBuilder<ModelQueryResults> builder,
			USERID token) {

		super(form, context,algorithm,builder,token);
		this.applicationRootReference =applicationRootReference;
		builder.setTargetURI(form.getValuesArray(OpenTox.params.dataset_uri.toString()));
		
		String name = String.format("%s.%s",UUID.randomUUID().toString(),algorithm.getName());
		ModelQueryResults m = new ModelQueryResults();
		Template empty = new Template("Empty");
		m.setDependent(empty);
		m.setPredictors(empty);
		m.setId(null);
		m.setContentMediaType(AlgorithmFormat.WWW_FORM.getMediaType());
		m.setName(name);
		m.setAlgorithm(alg_reporter.getURI(algorithm));
		builder.setTrainingData(m);
		
		prediction = new LiteratureEntry(name,reporter.getURI(applicationRootReference.toString(),m));	
		prediction.setType(_type.Model);
	
	}	

	@Override
	protected Object createTarget(Reference reference) throws Exception {
		Object query = getQueryObject(reference, applicationRootReference,context);
		
		if (query instanceof QueryDatasetByID) {
			SourceDataset dataset = new SourceDataset();
			dataset.setId(((QueryDatasetByID) query).getValue());
			QueryStructurePairsDataset q = new QueryStructurePairsDataset();
			q.setFieldname(dataset);
			q.setValue(dataset);
			return q;
		} else if (query instanceof QueryStoredResults) {
			IStoredQuery dataset = new StoredQuery();
			dataset.setId(((QueryDatasetByID) query).getValue());
			QueryStructurePairsRDataset q = new QueryStructurePairsRDataset();
			q.setFieldname(dataset);
			q.setValue(dataset);
			return q;
		} else throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED,reference.toString());
	}

	@Override
	protected AbstractBatchProcessor createBatch(Object target)
			throws Exception {
		if (target == null) throw new Exception("");
		if (target instanceof QueryStructurePairs) {
			DbReader<IStructureRecord[]> reader = new DbReader<IStructureRecord[]>();
			reader.setHandlePrescreen(false);
			return reader;
		} else
			throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED,target.toString());
	}
	protected ProcessorsChain<IStructureRecord[], IBatchStatistics, IProcessor> createProcessors() throws Exception {

		ProcessorsChain<IStructureRecord[],IBatchStatistics,IProcessor> p = 
			new ProcessorsChain<IStructureRecord[],IBatchStatistics,IProcessor>();

		p.setAbortOnError(true);
		p.add(new ProcessorStructurePairsRetrieval());
		p.add(new SMSDProcessor(prediction));
		p.add(new PairPropertiesWriter());
		p.add(new DefaultAmbitProcessor<IStructureRecord[],IStructureRecord[]>() {
			public IStructureRecord[] process(IStructureRecord[] target) throws AmbitException {
				builder.transform(target);
				return target;
			}
		});
		return p;
	}

}
