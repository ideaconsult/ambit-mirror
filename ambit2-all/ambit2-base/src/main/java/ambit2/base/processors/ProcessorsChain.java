package ambit2.base.processors;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;

/**
 * A list or {@link IProcessor} to be executed sequentially.
 * A processor is executed only if it is enabled. <br>
 * The result from one processor is passes as a parameter to the next.<b>
 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Dec 13, 2008
 */
public class ProcessorsChain<Target, Result, P extends IProcessor> extends ArrayList<P> implements IProcessor<Target,Result> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1289381455884215893L;
	protected boolean abortOnError = false;
	public boolean isAbortOnError() {
		return abortOnError;
	}


	public void setAbortOnError(boolean abortOnError) {
		this.abortOnError = abortOnError;
	}


	public ProcessorsChain() {
		super();
	}
	

	public Result process(Target object) throws AmbitException {
		Object o = object;
		for (int i=0; i < size(); i++) {
			try {
		    if (get(i).isEnabled()) {
		    	preprocess(get(i),o);
		        o = get(i).process(o);
		        postprocess(get(i),o);
		    }    
			} catch (Exception x) {
				x.printStackTrace();
				if (abortOnError)
					throw new AmbitException(x);
			}
		}
		try {
			return (Result)o;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
		
	}

	protected void preprocess(P p,Object object) {}
	protected void postprocess(P p,Object object) {}
	
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
    public long getID() {
    	return 0;
    }

}


