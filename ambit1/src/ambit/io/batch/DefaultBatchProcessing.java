package ambit.io.batch;

import java.io.File;
import java.util.Date;
import java.util.Hashtable;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit.exceptions.AmbitException;
import ambit.exceptions.AmbitIOException;
import ambit.io.FileInputState;
import ambit.io.FileOutputState;
import ambit.io.IAmbitResultWriter;
import ambit.io.IInputState;
import ambit.io.IOutputState;
import ambit.io.MDLWriter;
import ambit.log.AmbitLogger;
import ambit.misc.AmbitCONSTANTS;
import ambit.processors.IAmbitProcessor;

/**
 * Default implementation of {@link ambit.io.batch.IBatch}. See examples for {@link ambit.database.search.DbSimilarityByFingerprintsReader},
 * {@link ambit.database.search.DbSimilarityByAtomenvironmentsReader}, {@link ambit.database.search.DbExactSearchReader},
 * {@link ambit.ui.actions.BatchAction} descendants. See exapmle at {@link ambit.database.writers.DbSubstanceWriter}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 29, 2006
 */
public class DefaultBatchProcessing implements IBatch {

    public synchronized IIteratingChemObjectReader getReader() {
        return reader;
    }
    public synchronized IChemObjectWriter getWriter() {
        return writer;
    }
    protected transient IIteratingChemObjectReader reader;
    protected transient IChemObjectWriter writer;
    protected transient IAmbitProcessor processor;
    protected transient IBatchConfig configFile;
    
    protected IJobStatus status;
    protected IInputState inputState;
    protected IOutputState outputState;
	protected Date dateCreated = null;
	protected Date dateLastSaved = null;   
    
    protected IBatchStatistics statistics;
    protected long recordsLimit = Integer.MAX_VALUE;
    
	protected static transient AmbitLogger logger = new AmbitLogger(
	        DefaultBatchProcessing.class);

	/**
	 * Creates {@link DefaultBatchProcessing} which reads compounds from a file and writes results in a file.
	 * @param inputFile  {@link File}
	 * @param outputFile  {@link File}
	 * @param processor  {@link IAmbitProcessor}
	 * @param config {@link IBatchConfig}
	 * @throws BatchProcessingException
	 */
	public DefaultBatchProcessing(
			File inputFile,
	        File outputFile,
	        IAmbitProcessor processor,
	        IBatchConfig config
	        )  throws BatchProcessingException {
		this(new FileInputState(inputFile),
			 new FileOutputState(outputFile),processor,config);
	}
	/**
	 * Creates {@link DefaultBatchProcessing} which reads compounds from an {@link IIteratingChemObjectReader} and writes results by {@link IChemObjectWriter}
	 * @param reader  {@link IIteratingChemObjectReader}
	 * @param writer  {@link IChemObjectWriter}
	 * @param processor  {@link IAmbitProcessor}
	 * @param config {@link IBatchConfig}
	 * @throws BatchProcessingException
	 */
	public DefaultBatchProcessing(
			IIteratingChemObjectReader reader,
	        IChemObjectWriter writer,
	        IAmbitProcessor processor,
	        IBatchConfig config
	        )  throws BatchProcessingException {
		this(new DefaultInputState(reader),new DefaultOutputState(writer),processor,config);
	}
	/**
	 * Creates {@link DefaultBatchProcessing} which reads compounds from an {@link IIteratingChemObjectReader} and writes results by {@link IChemObjectWriter}
	 * @param reader  {@link IIteratingChemObjectReader}
	 * @param writer  {@link IChemObjectWriter}
	 * @param processor  {@link IAmbitProcessor}
	 * @param config {@link IBatchConfig}
	 * @param statistics {@link IBatchStatistics}
	 * @throws BatchProcessingException
	 */
	public DefaultBatchProcessing(
			IIteratingChemObjectReader reader,
	        IChemObjectWriter writer,
	        IAmbitProcessor processor,
	        IBatchConfig config,
	        IBatchStatistics statistics
	        )  throws BatchProcessingException {
		this(new DefaultInputState(reader),new DefaultOutputState(writer),processor,config,statistics);
	}
	public DefaultBatchProcessing(
			IInputState input,
	        IOutputState output,
	        IAmbitProcessor processor,
	        IBatchConfig config
	        
	        )  throws BatchProcessingException {
		this(input,output,processor,config,new DefaultBatchStatistics());
	}	

	public DefaultBatchProcessing(
			IInputState input,
	        IOutputState output,
	        IAmbitProcessor processor,
	        IBatchConfig config,
	        IBatchStatistics statistics
	        )  throws BatchProcessingException {
		super();
		this.reader = null;
		this.writer = null;
		this.processor = processor;
		inputState = input;
		outputState = output;		
		if (config !=null)  this.configFile = config;
		else
			try {
			    configFile = new DefaultBatchConfig();
			} catch (BatchProcessingException x) {
			    configFile = null;
			}
		status = new DefaultStatus();
		this.statistics = statistics;
		openInput();
		openOutput();
	}
	
	public IIteratingChemObjectReader getInput() {
		return reader;
	}

	public void setInput(IIteratingChemObjectReader input) {
		reader = input;
	}

	public IChemObjectWriter getOutput() {
		return writer;
	}

	public void setOutput(IChemObjectWriter output) {
		writer = output;

	}

	public IAmbitProcessor getProcessor() {
		return processor;
	}

	public void setProcessor(IAmbitProcessor processor) {
		this.processor = processor;

	}

	public void start() throws BatchProcessingException {
		IJobStatus status = getStatus();
		if (status.isStatus(IJobStatus.STATUS_NOTSTARTED) || 
				getStatus().isPaused()) {

			if (status.isPaused())
				logger.info("Resume batch processing after pause");

			status.setStatus(IJobStatus.STATUS_RUNNING);
			long startRecord = 0;
			long record = 0;
			Object object = null;
			while (reader.hasNext() && (getStatus().isRunning())) {
				try {
					object = readRecord();
				} catch (Exception x) {
					x.printStackTrace();
					getStatus().setError(x);
				}
				try {
					object = processRecord(object);
				} catch (BatchProcessingException x) {
					getStatus().setError(x);
					logger.error(x);
				}
				inputState.setCurrentRecord(inputState.getCurrentRecord() + 1);
				writeRecord(object);
				outputState.setCurrentRecord(outputState.getCurrentRecord() + 1);

				record++;
				if (((record - startRecord) % configFile.getSaveFrequency()) == 0) {
					configFile.save(this);
					startRecord = 0;
					status.setModified(false);
				} else status.setModified(true);
				
				if (record > recordsLimit) status.setStatus(IJobStatus.STATUS_ABORTED);
			}
			
			if (status.isRunning()) {
				finish();
				configFile.save(this);
				closeInput();
				closeOutput();
				closeProcessor();
			} else if (!status.isPaused()) {
				finish();
				configFile.save(this);
				closeInput();
				closeOutput();
				closeProcessor();
			}	
			// save state finally
			

			if (configFile.deleteConfigOnSuccess() && (status.isStatus(IJobStatus.STATUS_DONE))) {
				configFile.delete();
			}
		} else
			throw new BatchProcessingException(status.toString());

	}

	public void pause() throws BatchProcessingException {
		getStatus().setStatus(IJobStatus.STATUS_PAUSED);
	}

	public void cancel() throws BatchProcessingException {
		getStatus().setStatus(IJobStatus.STATUS_ABORTED);

	}
	/**
	 * override to do whatever work needed before closing the streams
	 */
	public void finish() throws BatchProcessingException {
		if ((writer != null) && (writer instanceof IAmbitResultWriter))
			try {
			((IAmbitResultWriter)writer).writeResult(processor.getResult());
			} catch (AmbitException x) {
				x.printStackTrace();
			}
		getStatus().setStatus(IJobStatus.STATUS_DONE);
		statistics.completed();

	}
	public IJobStatus getStatus() {
		return status;
	}

	/* (non-Javadoc)
     * @see ambit.io.batch.IBatch#processRecord(java.lang.Object)
     */
    public Object processRecord(Object object) throws BatchProcessingException {
		logger.debug("Process record");
		long now = System.currentTimeMillis();
		if (processor == null) throw new BatchProcessingException("Processing method not assigned");
		try {
		    Object o = processor.process(object);
		    statistics.increment(IBatchStatistics.RECORDS_PROCESSED);
		    statistics.incrementTimeElapsed(IBatchStatistics.RECORDS_PROCESSED, System.currentTimeMillis()-now);
		    return o;
		} catch (AmbitException x) {
			statistics.increment(IBatchStatistics.RECORDS_ERROR);
			statistics.incrementTimeElapsed(IBatchStatistics.RECORDS_ERROR, System.currentTimeMillis()-now);
		    throw new BatchProcessingException(x);
		}
    }
    /* (non-Javadoc)
     * @see ambit.io.batch.IBatch#getBatchStatistics()
     */
    public IBatchStatistics getBatchStatistics() {
        return statistics;
    }
    /* (non-Javadoc)
     * @see ambit.io.batch.IBatch#readRecord()
     */
    public Object readRecord() throws BatchProcessingException {
        long now = System.currentTimeMillis();        
        Object o = reader.next();
        statistics.incrementTimeElapsed(IBatchStatistics.RECORDS_READ, System.currentTimeMillis()-now);        
        statistics.increment(IBatchStatistics.RECORDS_READ);
        return o;
    }
    /* (non-Javadoc)
     * @see ambit.io.batch.IBatch#writeRecord(java.lang.Object)
     */
    public void writeRecord(Object object) throws BatchProcessingException {
    	long now = System.currentTimeMillis();                
    	if (writer == null) return;
    	if (object == null) return;
        if (object instanceof IChemObject)
	        try {
	        	if (writer instanceof MDLWriter) {
	        	    Hashtable props = (Hashtable)((ChemObject)object).getProperties().clone();
	        	    props.remove(CDKConstants.ALL_RINGS);
	        	    props.remove(CDKConstants.SMALLEST_RINGS);
	        	    props.remove(AmbitCONSTANTS.AMBIT_IDSTRUCTURE);
	        	    props.remove(AmbitCONSTANTS.AMBIT_IDSUBSTANCE);
	        		((MDLWriter) writer).setSdFields(props);
	        	}
	            writer.write((ChemObject)object);
	            statistics.incrementTimeElapsed(IBatchStatistics.RECORDS_WRITTEN, System.currentTimeMillis()-now);
	            statistics.increment(IBatchStatistics.RECORDS_WRITTEN);
	        } catch (CDKException x) {
	        	logger.error(x);
	        	status.setError(x);
	        	statistics.incrementError(IBatchStatistics.RECORDS_WRITTEN,object,x);
	        	statistics.incrementTimeElapsed(IBatchStatistics.RECORDS_ERROR, System.currentTimeMillis()-now);
	            //throw new BatchProcessingException(x);
	        }
	    else throw new BatchProcessingException("Attempting to write unsupported object",this);
        
    }
    /* (non-Javadoc)
     * @see ambit.io.batch.IBatch#getConfig()
     */
    public IBatchConfig getConfig() {
        return configFile;
    }
    /* (non-Javadoc)
     * @see ambit.io.batch.IBatch#setConfig(ambit.io.batch.IBatchConfig)
     */
    public void setConfig(IBatchConfig config) {
        this.configFile = config;

    }
    public synchronized Date getDateCreated() {
        return dateCreated;
    }
    public synchronized void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
    public Date getDateLastSaved() {
        return dateLastSaved;
    }
    public void setDateLastSaved(Date dateLastSaved) {
        this.dateLastSaved = dateLastSaved;
    }
    public void closeInput() throws BatchProcessingException {
    	try {
    	    if (reader != null)
    	        reader.close();
    	    reader = null;
    		
    	} catch (Exception x) {
    		throw new BatchProcessingException(x);
    	}
    	
    }
    public void closeOutput() throws BatchProcessingException {
    	try {
    		if (writer != null) {
    			writer.close();
    			writer = null;
    		}
    	} catch (Exception x) {
    		throw new BatchProcessingException(x);
    	}
    	
    }
    public void openInput() throws BatchProcessingException {
        try {
		reader = inputState.getReader();
        } catch (AmbitIOException x) {
            throw new BatchProcessingException("Batch input\t",x);
        }
    }
    public void openOutput() throws BatchProcessingException {
        try {
    	writer = outputState.getWriter();
        } catch (AmbitIOException x) {
            throw new BatchProcessingException("Batch output\t",x);
        }
    	
    }
	public long getRecordsLimit() {
		return recordsLimit;
	}
	public void setRecordsLimit(long records) {
		this.recordsLimit = records;
	}
	/* (non-Javadoc)
     * @see ambit.io.batch.IBatch#closeProcessor()
     */
    public void closeProcessor() throws BatchProcessingException {
       if (processor != null) processor.close();

    }
    public String toString() {
    	StringBuffer b = new StringBuffer();
    	b.append(reader.toString());
    	b.append("\n");
    	b.append(processor.toString());
    	b.append("\n");
    	b.append(writer.toString());
    	return b.toString();
    }
}
