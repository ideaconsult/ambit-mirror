package ambit2.io.batch;

import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.io.IInputState;
import ambit2.exceptions.AmbitIOException;

public class DefaultInputState extends DefaultIOState implements IInputState {
	protected transient IIteratingChemObjectReader reader = null;
	public DefaultInputState(IIteratingChemObjectReader reader) {
		super();
		this.reader = reader;
	}

	public IIteratingChemObjectReader getReader()
			throws AmbitIOException {
		return reader;
	}

}
