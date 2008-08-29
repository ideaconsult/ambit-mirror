package ambit2.core.processors;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import ambit2.core.exceptions.AmbitException;

/**
 * A list or {@link ambit2.processors.IAmbitProcessor} to be executed sequentially.
 * A processor is executed only if it is enabled. <br>
 * The result from one processor is passes as a parameter to the next.<b>
 * See exapmle at {@link ambit2.database.writers.DbSubstanceWriter}.
 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 29, 2006
 */
public class ProcessorsChain<Target, Result, P extends IProcessor> extends ArrayList<P> implements IProcessor<Target,Result> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1289381455884215893L;
	public ProcessorsChain() {
		super();
	}
	

	public Result process(Target object) throws AmbitException {
		Object o = object;
		for (int i=0; i < size(); i++) {
			try {
		    if (get(i).isEnabled())
		        o = get(i).process(o);
			} catch (Exception x) {
				System.out.println(get(i));
				x.printStackTrace();
			}
		}
		try {
			return (Result)o;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
		
	}

	/* (non-Javadoc)
     * @see ambit2.processors.IAmbitProcessor#close()
     */
    public void close() {
    	/**
    	for (int i=0; i < size(); i++) {
			get(i).close();
		}
		**/

    }
    public String toString() {
    	StringBuffer b = new StringBuffer();
    	for (int i=0; i < size();i++) {
    		b.append(get(i));
    		b.append("\n");
    	}	
    	return b.toString();
    }
    
    public boolean isEnabled() {
    	for (int i=0; i < size();i++) 
    		if (get(i).isEnabled()) return true;
    	return false;
    }
    public void setEnabled(boolean value) {
    	for (int i=0; i < size();i++) 
    		get(i).setEnabled(value);        
    }
    public void addPropertyChangeListener( final PropertyChangeListener listener) {
    	//TODO    	
    }
    public void removePropertyChangeListener( final PropertyChangeListener listener) {
    	//TODO
    }    
    public void addPropertyChangeListener(final String propertyName,  final PropertyChangeListener listener) {
    	//TODO    	
    }
    public void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
    	//TODO
    }

}


