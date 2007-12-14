/*
 * Created on 2006-2-16
 *
 */
package ambit.io.batch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.TimeZone;



/**
 * Default implementation of {@link ambit.io.batch.IBatchConfig}
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-2-16
 */
public class DefaultBatchConfig implements IBatchConfig {
    protected boolean deleteOnSuccess;
    protected long saveFrequency;
    protected File configFile;
 
    /**
     * 
     */
    public DefaultBatchConfig() throws BatchProcessingException {
        super();
        deleteOnSuccess = true;
        saveFrequency = 100;
		try {
			configFile = File.createTempFile(DefaultBatchProcessing.class.getName(), ".tmp");
		} catch (IOException x) {
		    throw new BatchProcessingException(x);
		}
        
    }
    public DefaultBatchConfig(File file) throws BatchProcessingException {
        super();
        deleteOnSuccess = false;
        saveFrequency = 100;
		configFile = file;
    }
    /* (non-Javadoc)
     * @see ambit.io.batch.IBatchConfig#deleteConfigOnSuccess()
     */
    public boolean deleteConfigOnSuccess() {
        return deleteOnSuccess;
    }

    /* (non-Javadoc)
     * @see ambit.io.batch.IBatchConfig#getSaveFrequency()
     */
    public long getSaveFrequency() {
        return saveFrequency;
    }

    /* (non-Javadoc)
     * @see ambit.io.batch.IBatchConfig#setSaveFrequency(long)
     */
    public void setSaveFrequency(long records) {
        saveFrequency = records;
    }

    /* (non-Javadoc)
     * @see ambit.io.batch.IBatchConfig#delete()
     */
    public void delete() {
       configFile.delete();

    }
	public void save(OutputStream out, IBatch batch) throws BatchProcessingException {
		if (out == null)
			throw new BatchProcessingException("Can't save batch state!");
		try {
			//logger.debug("Save state\t" + configFile.toString());
			ObjectOutputStream os = new ObjectOutputStream(out);
			os.writeObject(batch);
			os.close();
		} catch (IOException x) {
			throw new BatchProcessingException(x);
		}
	}
	/* (non-Javadoc)
     * @see ambit.io.batch.IBatchConfig#save(ambit.io.batch.IBatch)
     */
    public void save(IBatch batch) throws BatchProcessingException {
        //TODO verify -originally inputState/outputState are used
		if ((batch.getInput() == null) || (batch.getOutput() == null))
			throw new BatchProcessingException("Empty input/output files!");
		batch.setDateLastSaved(Calendar.getInstance(TimeZone.getDefault()).getTime());
		FileOutputStream configStream = null;
		try {
			configStream = new FileOutputStream(configFile);
			save(configStream,batch);
			configStream.close();
		} catch (FileNotFoundException x) {
			configStream = null;
			//logger.error(x);
		} catch (IOException x) {
			configStream = null;
			//logger.error(x);
		}
		batch.getStatus().setModified(false);

    }
	public IBatch load() throws BatchProcessingException {
		try {
			
			ObjectInputStream is = new ObjectInputStream(
			        new FileInputStream(configFile));
			IBatch newBP = (IBatch) is.readObject();
			is.close();
			newBP.setConfig(this);
			newBP.openInput();
			newBP.openOutput();
			if (newBP.getStatus().isCancelled()) newBP.pause(); //otherwise starting will not be possible
			
			
			newBP.setProcessor(null);
			return newBP;
		} catch (IOException x) {
			throw new BatchProcessingException(x);
		} catch (ClassNotFoundException x) {
			throw new BatchProcessingException(x);
		}
	}	
	public static String generateOutputFileName(File inputFile) {
		String n = inputFile.getAbsolutePath();
		return n.substring(0,n.lastIndexOf(".")) + "_ambit.sdf";
	}
}
