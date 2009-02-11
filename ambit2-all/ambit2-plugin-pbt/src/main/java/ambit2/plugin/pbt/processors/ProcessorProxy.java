package ambit2.plugin.pbt.processors;

import nplugins.core.Introspection;
import ambit2.core.exceptions.AmbitException;
import ambit2.core.processors.DefaultAmbitProcessor;
import ambit2.core.processors.IProcessor;

public class ProcessorProxy<Target, Result> extends DefaultAmbitProcessor<Target, Result> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3147531660980879127L;
	protected IProcessor<Target, Result> processor = null;
	protected String className = null;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Result process(Target target) throws AmbitException {
		try {
			if (processor!= null)  return processor.process(target);
			else {
				if (className == null) throw new AmbitException("Undefined class name");
				Object o = Introspection.loadCreateObject(className);
				if (o instanceof IProcessor) {
					processor = (IProcessor) o;
					return processor.process(target);
				} else
					throw new AmbitException("Expected IProcessor type instead of "+o.getClass().getName());
					
			}
		} catch (Throwable x) {
			throw new AmbitException(x);
		}
	}

}
