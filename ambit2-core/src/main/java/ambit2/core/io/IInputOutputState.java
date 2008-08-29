/*
 * Created on 2006-2-16
 *
 */
package ambit2.core.io;

import java.io.Serializable;

/**
 * Status of the input reader/ output writer. Used in {@link ambit2.test.io.batch.DefaultBatchProcessing}.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-2-16
 */
public interface IInputOutputState extends Serializable {
    public void setCurrentRecord(long currentRecord);
    public long getCurrentRecord();
    public String getResponseType();
    public void setResponseType(String responseType);
}
