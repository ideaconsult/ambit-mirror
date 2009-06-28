package ambit2.base.interfaces;

import java.io.Serializable;

/**
 * 
 * Statistics of an {@link IBatchProcessor} process
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 29, 2006
 */
public interface IBatchStatistics extends Serializable {
	public enum RECORDS_STATS {
		RECORDS_READ {
			@Override
			public String toString() {
				return "read";
			}
		},
		RECORDS_PROCESSED {
			@Override
			public String toString() {
				return "processed";
			}
		},
		RECORDS_ERROR {
			@Override
			public String toString() {
				return "error";
			}
		}
		};
    /**
     * Returns number of records of a type   
     * @param recordType RECORDS_READ,RECORDS_PROCESSED,RECORDS_WRITTEN,RECORDS_ERROR
     * @return number of records of the specified type
     */
    long getRecords(RECORDS_STATS recordType);
    /**
     * Sets number of records of a type   
     * @param recordType RECORDS_READ,RECORDS_PROCESSED,RECORDS_WRITTEN,RECORDS_ERROR
     * @param number number of records of the specified type
     */
    void setRecords(RECORDS_STATS recordType, long number);
    /**
     * Increments number of records of a type   
     * @param recordType RECORDS_READ,RECORDS_PROCESSED,RECORDS_WRITTEN,RECORDS_ERROR
     */
    void increment(RECORDS_STATS recordType);
    /**
     * Return  time elapsed for of records of a type
     * @param recordType  RECORDS_READ,RECORDS_PROCESSED,RECORDS_WRITTEN,RECORDS_ERROR
     * @return time elapsed
     */
    long getTimeElapsed(RECORDS_STATS recordType);
    /**
     * Sets time elapsed for of records of a type
     * @param recordType RECORDS_READ,RECORDS_PROCESSED,RECORDS_WRITTEN,RECORDS_ERROR
     * @param milliseconds
     */
    void setTimeElapsed(RECORDS_STATS recordType, long milliseconds);
    /**
     * Increments time elapsed for of records of a type
     * @param recordType RECORDS_READ,RECORDS_PROCESSED,RECORDS_WRITTEN,RECORDS_ERROR
     * @param milliseconds
     */
    void incrementTimeElapsed(RECORDS_STATS recordType, long milliseconds);
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
	long getFrequency();
	boolean isTimeToPrint(long silentInterval) ;
    
}
