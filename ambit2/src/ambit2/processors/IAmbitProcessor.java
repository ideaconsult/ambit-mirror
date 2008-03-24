package ambit2.processors;

import java.beans.PropertyChangeListener;
import java.io.Serializable;

import ambit2.exceptions.AmbitException;


/**
 * Interface allowing compound-by-compound processing. 
 * All functionality calculating something of a/from a molecule should implement
 * this interface. A core part of batch processing {@link ambit.io.batch.IBatch} 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 29, 2006
 */
public interface IAmbitProcessor extends Serializable {
	Object process(Object object) throws AmbitException;
	/**
	 * Creates a new instance of {@link IAmbitResult} specific to the processor
	 * TODO returns class name instead of instance !
	 * @return {@link IAmbitResult}
	 */
	IAmbitResult createResult();
	/**
	 * returns an {@link IAmbitResult} attached to the processor
	 * @return {@link IAmbitResult}
	 */
	IAmbitResult getResult();
	/**
	 * attaches an {@link IAmbitResult} to the processor
	 * @param result
	 */
	void setResult(IAmbitResult result);
	/**
	 * 
	 * @return true if enabled
	 */
	boolean isEnabled();
	/**
	 * Enables/disables the processor
	 * @param value
	 */
	void setEnabled(boolean value);
	/**
	 * finalization 
	 *
	 */
	void close();
	/**
	 * Adds property change listener
	 * @param listener
	 */
	void addPropertyChangeListener(final PropertyChangeListener listener);	
	/**
	 * Adds property change listener
	 * @param propertyName
	 * @param listener
	 */
    void addPropertyChangeListener(final String propertyName,  final PropertyChangeListener listener);
    /**
     * Removes property change listener
     * @param propertyName
     * @param listener
     */
    void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener);
    void removePropertyChangeListener(final PropertyChangeListener listener);
	
}
