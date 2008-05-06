/**
 * Created on 2005-2-16
 *
 */
package ambit2.io;

import java.io.InputStream;
import java.io.OutputStream;

import ambit2.exceptions.AmbitIOException;

/**
 * @deprecated
 * This is an interface to be used in data reading and writing
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 * See also {@link ambit2.io.MyIOUtilities} and {@link ambit2.domain.AllData}
 */
public interface IReadWriteStream {
	public void setStreamName(String name);
	public boolean save(OutputStream out) throws AmbitIOException;
	public boolean load(InputStream in) throws AmbitIOException;	
	public void setType(String type);
	public String getType() ;
	
}
