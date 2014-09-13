package ambit2.base.processors.batch;

import java.util.Observable;

import net.idea.modbcum.i.batch.IBatchStatistics;
import net.idea.modbcum.i.batch.IBatchStatistics.RECORDS_STATS;

/**
 * 
 * Default implementation of {@link IBatchStatistics}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 29, 2006
 */
public class DefaultBatchStatistics extends Observable implements
		IBatchStatistics {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8714835013929249115L;
	protected long[] records = {0,0,0,0};
	protected long[] time_elapsed = {0,0,0,0};
	protected long frequency = 50;
	protected long last_time_print_stats = 0;
	public long getFrequency() {
		return frequency;
	}
	public void setFrequency(long frequency) {
		this.frequency = frequency;
	}
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
    public void completed() {
        inProgress = false;
        setChanged();
        notifyObservers();        

    }
    public long getRecords(IBatchStatistics.RECORDS_STATS recordType) {
        return records[recordType.ordinal()];
    }
    public void setRecords(IBatchStatistics.RECORDS_STATS recordType, long number) {
        records[recordType.ordinal()] = number;
        setChanged();
        notifyObservers();        
    }
    public void increment(IBatchStatistics.RECORDS_STATS recordType) {
        records[recordType.ordinal()]++;
        setChanged();
        notifyObservers();
    }
    public long getTimeElapsed() {
        return time_elapsed[IBatchStatistics.RECORDS_STATS.RECORDS_READ.ordinal()]+
        time_elapsed[IBatchStatistics.RECORDS_STATS.RECORDS_PROCESSED.ordinal()]+
        time_elapsed[IBatchStatistics.RECORDS_STATS.RECORDS_ERROR.ordinal()];

    }
    public long getTimeElapsed(RECORDS_STATS recordType) {
    	return  time_elapsed[recordType.ordinal()];
    }
    public void setTimeElapsed(RECORDS_STATS recordType, long milliseconds) {
    	time_elapsed[recordType.ordinal()] = milliseconds;
        setChanged();
        notifyObservers();            	
    }
    public  void incrementTimeElapsed(RECORDS_STATS recordType, long milliseconds) {
    	time_elapsed[recordType.ordinal()] += milliseconds;
        setChanged();
        notifyObservers();    	
    }
    public boolean isTimeToPrint(long silentInterval) {
    	//(getRecords(RECORDS_STATS.RECORDS_READ) % frequency) == 0)
    	long now = System.currentTimeMillis();
    	if ((now-last_time_print_stats) > silentInterval) {
    		last_time_print_stats = now;
    		return true;
    	} else return false;
    }
    public String toString() {
       // if (!inProgress || isTimeToPrint(100)) {
            long t = getTimeElapsed();
            
            StringBuffer b = new StringBuffer();
            for (RECORDS_STATS stats : RECORDS_STATS.values()) {
	            b.append(stats.toString());
	            b.append(' ');
	            b.append(Long.toString(getRecords(stats)));
	            b.append(' ');	            
            }
            /*
            b.append(" records in ");
            b.append(Long.toString(t));
            b.append(" ms");
            */
            if (getRecords(RECORDS_STATS.RECORDS_READ) > 0) {
                b.append("(");
                long s = t/getRecords(RECORDS_STATS.RECORDS_READ);
                if (s > 1000) {
		            b.append(s/1000);
		            b.append(" s per record)");                	
                } else {	
		            b.append(s);
		            b.append(" ms per record)");
                }
            }
            return b.toString();
       // }	
       // else return blank;
    }
	public String getResultCaption() {
		return resultCaption;
	}
	public void setResultCaption(String resultCaption) {
		this.resultCaption = resultCaption;
	}
}
