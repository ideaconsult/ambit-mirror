package ambit2.processors;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import ambit2.exceptions.AmbitException;
import ambit2.log.AmbitLogger;
import ambit2.processors.results.AmbitResultsList;



/**
 * A list or {@link ambit.processors.IAmbitProcessor} to be executed sequentially.
 * A processor is executed only if it is enabled. <br>
 * The result from one processor is passes as a parameter to the next.<b>
 * See exapmle at {@link ambit.database.writers.DbSubstanceWriter}.
 
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> Aug 29, 2006
 */
public class ProcessorsChain extends ArrayList<IAmbitProcessor> implements IAmbitProcessor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9155387313476732543L;
	protected static AmbitLogger logger = new AmbitLogger(ProcessorsChain.class);
	
	public void addProcessor(IAmbitProcessor processor) {
			super.add(processor);
			
	}
	public void addProcessor(int index,IAmbitProcessor processor) {
		super.add(index,processor);
		
	}
	
	public IAmbitProcessor getProcessor(int index) {
		return get(index);
	}

	public Object process(Object object) throws AmbitException {
		Object o = object;
		for (int i=0; i < size(); i++) {
			try {
			    if (getProcessor(i).isEnabled()) {
			        o = getProcessor(i).process(o);
			    }    
			} catch (Exception x) {
				logger.error(x);
			}
		}
		return o;
	}

	public IAmbitResult createResult() {
		return new AmbitResultsList();
	}
	public IAmbitResult getResult() {
		AmbitResultsList results = (AmbitResultsList)createResult();
		for (int i=0; i < size(); i++) {
			results.add(getProcessor(i).getResult());
		}	
		return results;
	}
	public void setResult(IAmbitResult result) {
	    
	}
	/* (non-Javadoc)
     * @see ambit.processors.IAmbitProcessor#close()
     */
    public void close() {
    	for (int i=0; i < size(); i++) {
			getProcessor(i).close();
		}

    }
    public String toString() {
    	StringBuffer b = new StringBuffer();
    	for (int i=0; i < size();i++) {
    		b.append(getProcessor(i));
    		b.append("\n");
    	}	
    	return b.toString();
    }
    public boolean isEnabled() {
    	for (int i=0; i < size();i++) {
    		if (getProcessor(i).isEnabled()) {
    			return true;
    		}
    	}	
    	return false;
    }
    public void setEnabled(boolean value) {
    	for (IAmbitProcessor p : this) { 
    		p.setEnabled(value);
    	}	
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
/*
class ProcessorResultPair {
	IAmbitProcessor processor = null;
	IAmbitResult result = null;
	public ProcessorResultPair() {
		this(null,null);
	}
	public ProcessorResultPair(IAmbitProcessor processor) {
		this(processor,processor.createResult());
	}	
	public ProcessorResultPair(IAmbitProcessor processor, IAmbitResult result) {
		super();
		this.processor = processor;
		this.result = result;
	}	
	
	public IAmbitProcessor getProcessor() {
		return processor;
	}
	public void setProcessor(IAmbitProcessor processor) {
		this.processor = processor;
	}
	public IAmbitResult getResult() {
		return result;
	}
	public void setResult(IAmbitResult result) {
		this.result = result;
	}
}
*/

