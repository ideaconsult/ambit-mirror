package ambit.io.batch;

import java.util.Observable;

/**
 * 
 * Default implementation of {@link ambit.io.batch.IBatchStatistics}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 29, 2006
 */
public class DefaultBatchStatistics extends Observable implements
		IBatchStatistics {
	protected long[] records = {0,0,0,0};
	protected long[] time_elapsed = {0,0,0,0};
	protected long frequency = 50;
	protected static final String blank = "";
	protected boolean inProgress = true;
	protected String resultCaption = "Found";

	public DefaultBatchStatistics() {
		super();
	}
	public void clear() {
	    for (int i=0; i < records.length; i++) {
	        records[i] = 0; time_elapsed[i] = 0;
	    }
	    inProgress = true;
        setChanged();
        notifyObservers();        
	    
	}
	/* (non-Javadoc)
     * @see ambit.io.batch.IBatchStatistics#completed()
     */
    public void completed() {
        inProgress = false;
        setChanged();
        notifyObservers();        

    }
    public long getRecords(int recordType) {
        return records[recordType];
    }
    public void setRecords(int recordType, long number) {
        records[recordType] = number;
        setChanged();
        notifyObservers();        
    }
    public void increment(int recordType) {
        records[recordType]++;
        setChanged();
        notifyObservers();
    }
    public long getTimeElapsed() {
        return time_elapsed[IBatchStatistics.RECORDS_READ]+
        time_elapsed[IBatchStatistics.RECORDS_PROCESSED]+
        time_elapsed[IBatchStatistics.RECORDS_WRITTEN]+
        time_elapsed[IBatchStatistics.RECORDS_ERROR];

    }
    public long getTimeElapsed(int recordType) {
    	return  time_elapsed[recordType];
    }
    public void setTimeElapsed(int recordType, long milliseconds) {
    	time_elapsed[recordType] = milliseconds;
        setChanged();
        notifyObservers();            	
    }
    public  void incrementTimeElapsed(int recordType, long milliseconds) {
    	time_elapsed[recordType] += milliseconds;
        setChanged();
        notifyObservers();    	
    }
    
    public String toString() {
        if (!inProgress ||
                (records[IBatchStatistics.RECORDS_PROCESSED] % frequency) == 0) {
            long t = getTimeElapsed();
            
            StringBuffer b = new StringBuffer();
            //b.append("<html> ");
            b.append(resultCaption);
            b.append(" ");
            b.append(Long.toString(records[IBatchStatistics.RECORDS_WRITTEN]));
            b.append(" records in ");
            b.append(Long.toString(t/1000));
            b.append(" seconds ");
            if (records[IBatchStatistics.RECORDS_WRITTEN] > 0) {
                b.append("<b>(");
                long s = t/records[IBatchStatistics.RECORDS_WRITTEN];
                if (s > 1000) {
		            b.append(s/1000);
		            b.append(" s per record)</b>");                	
                } else {	
		            b.append(s);
		            b.append(" ms per record)</b>");
                }
            }
            //b.append("</html>");
            return b.toString();
        }	
        else return blank;
    }
	public String getResultCaption() {
		return resultCaption;
	}
	public void setResultCaption(String resultCaption) {
		this.resultCaption = resultCaption;
	}
	/**
	 * Ignores the object
	 */
	public void increment(int recordType, Object o) {
		increment(recordType);
		
	}
	public void incrementError(int recordType, Object o, Exception x) {
		increment(IBatchStatistics.RECORDS_ERROR);
		
	}
}
