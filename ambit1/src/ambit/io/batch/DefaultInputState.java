package ambit.io.batch;

import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit.exceptions.AmbitIOException;
import ambit.io.IInputState;

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
