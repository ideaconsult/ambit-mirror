package ambit.processors;

import java.io.Serializable;
import java.io.Writer;

import ambit.exceptions.AmbitException;

/**
 * Provides means of accumulating results from processing multiple compounds by  {@link ambit.processors.IAmbitProcessor}
 * TODO verify whether we need all of the methods here.  
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 29, 2006
 */
public interface IAmbitResult extends Serializable {
    /**
     * 
     * @param object
     * @throws AmbitException
     */
	void update(Object object) throws AmbitException;
	/**
	 * Clears te object
	 *
	 */
	void clear();
	/**
	 * Writes the object
	 * @param writer
	 * @throws AmbitException
	 */
	void write(Writer writer) throws AmbitException;
	void setTitle(String title);
	String getTitle();
}
