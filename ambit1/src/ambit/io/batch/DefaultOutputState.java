package ambit.io.batch;

import org.openscience.cdk.io.IChemObjectWriter;

import ambit.io.IOutputState;

public class DefaultOutputState extends DefaultIOState implements IOutputState {
	protected transient IChemObjectWriter writer = null;
	public DefaultOutputState(IChemObjectWriter writer) {
		super();
		this.writer = writer;
	}

	public IChemObjectWriter getWriter() throws BatchProcessingException {
		return writer;
	}

}
