package ambit.io.batch;

import java.io.Serializable;

/**
 * 
 * Statistics of an {@link ambit.io.batch.IBatch} process
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 29, 2006
 */
public interface IBatchStatistics extends Serializable {
    final int RECORDS_READ = 0;
    final int RECORDS_PROCESSED = 1;
    final int RECORDS_WRITTEN = 2;
    final int RECORDS_ERROR = 3;
    /**
     * Returns number of records of a type   
     * @param recordType RECORDS_READ,RECORDS_PROCESSED,RECORDS_WRITTEN,RECORDS_ERROR
     * @return number of records of the specified type
     */
    long getRecords(int recordType);
    /**
     * Sets number of records of a type   
     * @param recordType RECORDS_READ,RECORDS_PROCESSED,RECORDS_WRITTEN,RECORDS_ERROR
     * @param number number of records of the specified type
     */
    void setRecords(int recordType, long number);
    /**
     * Increments number of records of a type   
     * @param recordType RECORDS_READ,RECORDS_PROCESSED,RECORDS_WRITTEN,RECORDS_ERROR
     */
    void increment(int recordType);
    /**
     * Increments number of records of a type and passes the record in question (for example for logging)   
     * @param recordType RECORDS_READ,RECORDS_PROCESSED,RECORDS_WRITTEN,RECORDS_ERROR
     */    
    void increment(int recordType, Object o);
    
    /**
     * Increments number of errors , occured when processing of record type   
     * @param recordType RECORDS_READ,RECORDS_PROCESSED,RECORDS_WRITTEN
     */    
    void incrementError(int recordType, Object o, Exception x);        
    /**
     * Return  time elapsed for of records of a type
     * @param recordType  RECORDS_READ,RECORDS_PROCESSED,RECORDS_WRITTEN,RECORDS_ERROR
     * @return time elapsed
     */
    long getTimeElapsed(int recordType);
    /**
     * Sets time elapsed for of records of a type
     * @param recordType RECORDS_READ,RECORDS_PROCESSED,RECORDS_WRITTEN,RECORDS_ERROR
     * @param milliseconds
     */
    void setTimeElapsed(int recordType, long milliseconds);
    /**
     * Increments time elapsed for of records of a type
     * @param recordType RECORDS_READ,RECORDS_PROCESSED,RECORDS_WRITTEN,RECORDS_ERROR
     * @param milliseconds
     */
    void incrementTimeElapsed(int recordType, long milliseconds);
    /**
     * Clears records
     *
     */
    void clear();
    /**
     * 
     *
     */
    void completed();
    /**
     * 
     * @return caption
     */
	public String getResultCaption();
	/**
	 * 
	 * @param resultCaption
	 */
	public void setResultCaption(String resultCaption);
    
}
