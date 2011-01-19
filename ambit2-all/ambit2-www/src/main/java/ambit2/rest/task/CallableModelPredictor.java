package ambit2.rest.task;

import java.sql.Connection;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.Reference;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.SourceDataset;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.ProcessorsChain;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.processors.PropertyValuesWriter;
import ambit2.db.readers.RetrieveStructure;
import ambit2.db.search.property.ValuesReader;
import ambit2.rest.model.predictor.ModelPredictor;

/**
 * 
 * @author nina
 *
 */
public class CallableModelPredictor<ModelItem,Predictor extends ModelPredictor,USERID> 
					extends CallableQueryProcessor<Object, IStructureRecord,USERID> {
	protected Reference applicationRootReference;
	protected Predictor predictor;
	
	public CallableModelPredictor(Form form, 
			Reference appReference,
			Context context,
			Predictor predictor,
			USERID token
				) {
		super(form,context,token);
		this.predictor = predictor;
		this.applicationRootReference = appReference;
	}	

	@Override
	protected Object createTarget(Reference reference) throws Exception {
		try {
			Object q = getQueryObject(reference, applicationRootReference);
			return q==null?reference:q;
		} catch (Exception x) {
			return reference;
		}
	}

	
	protected ProcessorsChain<IStructureRecord, IBatchStatistics, IProcessor> createProcessors() throws Exception {

		ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor> p1 = 
			new ProcessorsChain<IStructureRecord,IBatchStatistics,IProcessor>();

		if (predictor != null) {
			if (predictor.isStructureRequired()) {
				RetrieveStructure r = new RetrieveStructure(true);
				r.setPageSize(1);
				r.setPage(0);
				p1.add(new ProcessorStructureRetrieval(r));
			}
			
			IProcessor<IStructureRecord, IStructureRecord> valuesReader = getValuesReader();
			if (valuesReader != null)
				p1.add(valuesReader);
			
			p1.add(predictor);
		}
		p1.setAbortOnError(true);
		
		IProcessor<IStructureRecord, IStructureRecord> writer = getWriter();
		if (writer != null) p1.add(writer);
		
		return p1;
	}
	
	protected IProcessor<IStructureRecord,IStructureRecord> getValuesReader() {
		if  ((predictor.getModel().getPredictors().size()>0) &&  (predictor.isValuesRequired())) {
			ValuesReader readProfile = new ValuesReader(null);  //no reader necessary
			readProfile.setProfile(predictor.getModel().getPredictors());
			return readProfile;
		} else return null;
	}
	protected IProcessor<IStructureRecord, IStructureRecord> getWriter() {
		PropertyValuesWriter writer = new PropertyValuesWriter();
		writer.setDataset(new SourceDataset(sourceReference.toString(),
				new LiteratureEntry(predictor.getModelReporter().getURI(predictor.getModel()),sourceReference.toString())));
		return writer;
	}
	/**
	 * Returns reference to the same dataset, with additional features, predicted by the model
	 */
	@Override
	protected TaskResult createReference(Connection connection) throws Exception {
		;

			String predicted = predictor.createResultReference();
			String q = sourceReference.toString().indexOf("?")>0?"&":"?";
			return new TaskResult(
					String.format("%s%s%s",
					sourceReference.toString(),
					q,
					predicted)
					);
	}

	

}

