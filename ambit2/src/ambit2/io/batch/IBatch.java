package ambit2.io.batch;

import java.io.Serializable;
import java.util.Date;

import org.openscience.cdk.io.IChemObjectWriter;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.processors.IAmbitProcessor;

/**
 * This interface is an abstraction of a batch process.
 * A batch process consists of a 
 * reader {@link IIteratingChemObjectReader} - used to read compounds one by one from somewhere (file, database, memory) <br>  
 * processor {@link ambit2.processors.IAmbitProcessor} - which process compounds one by one (e.g. calculates descriptors, generates SMILES, etc.) <br>
 * and a writer {@link IChemObjectWriter} - used to write the result somewhere (file, database, memory) <br>
 * The object that is passed from reader through processor to writer is usually {@link org.openscience.cdk.interfaces.IAtomContainer} <br>
 * The processor generally changes /assigns some property of {@link org.openscience.cdk.interfaces.IAtomContainer}
 * and the writer writes the changed object or particular property.
 * <br>
 * Therefore, a batch is normally a loop
 * <code>
 * while (has_more_records) { <br>
 * 	Object object = readRecord(); <br>
 * 	object = processRecord(Object object); <br>
 * 	writeRecord(Object object);<br>
 * }<br>
 * </code>
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 29, 2006
 */
public interface IBatch extends Serializable {
	
	/**
	 * This is the input of the batch process
	 * @return {@link IIteratingChemObjectReader}
	 */
	IIteratingChemObjectReader getInput();
	/**
	 * Sets the input of the batch process
	 * @param input
	 */
	void setInput(IIteratingChemObjectReader input);
	/**
	 * This is the output of the batch process
	 * @return {@link IChemObjectWriter}
	 */
	IChemObjectWriter getOutput();
	/**
	 * Sets the output of the batch process {@link IChemObjectWriter}
	 */
	void setOutput(IChemObjectWriter output);
	/**
	 * This is where the actual processing will take place
	 * @return {@link IAmbitProcessor}
	 */
	IAmbitProcessor getProcessor();
	/**
	 * Sets the processor where the actual processing will take place
	 * @param processor {@link IAmbitProcessor}
	 */
	void setProcessor(IAmbitProcessor processor);
	
	/**
	 * starts batch processing 
	 */
	void start() throws BatchProcessingException;
	
	/**
	 * pause batch processing 
	 * @throws BatchProcessingException
	 */
	void pause() throws BatchProcessingException;
	/**
	 * aborts batch processing 
	 */
	void cancel() throws BatchProcessingException;

	/**
	 * batch completes normally 
	 */
	void finish() throws BatchProcessingException;
	
	/**
	 * Batch status 
	 * @return {@link IJobStatus}
	 */
	IJobStatus getStatus();	
	
	/**
	 * Initialization of the input (e.g. file open) 
	 */
	void openInput() throws BatchProcessingException;
	/**
	 * Initialization of the output (e.g. file open)
	 * @throws BatchProcessingException
	 */
	void openOutput() throws BatchProcessingException;
	/**
	 * Finalization of the input (e.g. file close)
	 * @throws BatchProcessingException
	 */
	void closeInput() throws BatchProcessingException;
	/**
	 * Finalization of the output (e.g. file close)
	 * @throws BatchProcessingException
	 */
	void closeOutput() throws BatchProcessingException;
	/**
	 * Finalization of the processor (anything that needs to be finalized when the processor finishes its work)
	 * @throws BatchProcessingException
	 */
	void closeProcessor() throws BatchProcessingException;
	/**
	 * Batch configuration
	 * @return {@link IBatchConfig}
	 */
	IBatchConfig getConfig();
	/**
	 * Sets batch configuration {@link IBatchConfig} 
	 * @param config
	 */
	void setConfig(IBatchConfig config);
	
	/**
	 * Batch statistics  
	 * @return {@link IBatchStatistics}
	 */
	IBatchStatistics getBatchStatistics();
	
	/**
	 * Reads a record and returns it as an object
	 * @return {@link Object}
	 * @throws BatchProcessingException
	 */
	Object readRecord() throws BatchProcessingException;
	/**
	 * Writes a record
	 * @param object
	 * @throws BatchProcessingException
	 */
	void writeRecord(Object object) throws BatchProcessingException;
	/**
	 * Process a record and returns processed record
	 * @param object  
	 * @return processed record (can be the same object)
	 * @throws BatchProcessingException
	 */
	Object processRecord(Object object) throws BatchProcessingException;
	/**
	 * TODO refactor this
	 * @param dateLastSaved
	 */
	public void setDateLastSaved(Date dateLastSaved);
	/**
	 *   Max records allowed to be processed.
	 * Used in for example in {@link ambit2.servlets.AmbitServlet}
	 * @return Max records allowed to be processed. 
	 */
	long getRecordsLimit();
	/**
	 * Sets max records allowed to be processed.
	 * @param records
	 */
	void setRecordsLimit(long records);
}
