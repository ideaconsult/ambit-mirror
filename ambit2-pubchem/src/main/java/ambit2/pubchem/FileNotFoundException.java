package ambit2.pubchem;

import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.ProcessorException;

public class FileNotFoundException extends ProcessorException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8657450659186925103L;
    public FileNotFoundException(IProcessor processor,Exception x) {
        super(processor,x);
    }
}
