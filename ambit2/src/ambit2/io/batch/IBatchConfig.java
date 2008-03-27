/*
 * Created on 2006-2-16
 *
 */
package ambit2.io.batch;

import java.io.OutputStream;

/**
 * Configuration of an {@link ambit2.io.batch.IBatch}. Used to store status of a batch process into a file for subsequent load. Not used at this moment. 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-2-16
 */
public interface IBatchConfig {
    boolean deleteConfigOnSuccess();
    long getSaveFrequency();
    void setSaveFrequency(long records);
    void delete();
    /**
     * Saves a batch into a stream.
     * @param out
     * @param batch
     * @throws BatchProcessingException
     */
    public void save(OutputStream out, IBatch batch) throws BatchProcessingException ;
    public void save(IBatch batch) throws BatchProcessingException ;
    public IBatch load() throws BatchProcessingException ;
    
}
