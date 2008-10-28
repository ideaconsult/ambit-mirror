package ambit2.core.processors.batch;

import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.core.exceptions.AmbitException;
import ambit2.core.io.IInputState;
import ambit2.core.processors.DefaultAmbitProcessor;
import ambit2.core.processors.IProcessor;

public class BatchProcessor extends DefaultAmbitProcessor<IInputState,IBatchStatistics> implements IBatchProcessor {
	public static String PROPERTY_BATCHSTATS="ambit2.core.processors.batch.IBatchStatistics";
	protected IProcessor<IChemObject, IChemObject> processor;
	/**
	 * 
	 */
	private static final long serialVersionUID = -5659435501205598414L;
	public BatchProcessor() {
		this(null);
	}
	public BatchProcessor(IProcessor<IChemObject, IChemObject> processor) {
		super();
		setProcessor(processor);
	}	
	public IProcessor<IChemObject, IChemObject> getProcessor() {
		return processor;
	}
	public void setProcessor(IProcessor<IChemObject, IChemObject> processor) {
		this.processor = processor;
	}

	public IBatchStatistics process(IInputState target) throws AmbitException {
		try {
			DefaultBatchStatistics stats = new DefaultBatchStatistics();
			stats.setResultCaption("Read");
			stats.frequency = 1;
			IIteratingChemObjectReader reader = target.getReader();
			IProcessor<IChemObject, IChemObject> processor = getProcessor();
			if (processor == null)
				throw new AmbitException("Processor not defined");
			while (reader.hasNext()) {
				if ((stats.getRecords(IBatchStatistics.RECORDS_READ) % stats.frequency)==0)
					propertyChangeSupport.firePropertyChange(PROPERTY_BATCHSTATS,null,stats);				
				Object object= null;
				long ms = System.currentTimeMillis();
				try {
					object = reader.next();
					stats.increment(IBatchStatistics.RECORDS_READ);
					stats.incrementTimeElapsed(IBatchStatistics.RECORDS_READ, System.currentTimeMillis()-ms);
				} catch (Exception x) {
					stats.increment(IBatchStatistics.RECORDS_ERROR);
					stats.incrementTimeElapsed(IBatchStatistics.RECORDS_ERROR, System.currentTimeMillis()-ms);					
					continue;					
				}
				ms = System.currentTimeMillis();
				try {
					processor.process((IChemObject)object);
					stats.increment(IBatchStatistics.RECORDS_PROCESSED);
					stats.incrementTimeElapsed(IBatchStatistics.RECORDS_PROCESSED, System.currentTimeMillis()-ms);
				} catch (Exception x) {
					stats.increment(IBatchStatistics.RECORDS_ERROR);
					stats.incrementTimeElapsed(IBatchStatistics.RECORDS_ERROR, System.currentTimeMillis()-ms);					
					continue;
				}				
								
			}
			propertyChangeSupport.firePropertyChange(PROPERTY_BATCHSTATS,null,stats);			
			reader.close();
			return stats;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}


}
