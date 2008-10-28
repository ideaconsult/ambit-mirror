package ambit2.core.processors.batch;

import org.openscience.cdk.interfaces.IChemObject;

import ambit2.core.io.IInputState;
import ambit2.core.processors.IProcessor;

public interface IBatchProcessor extends IProcessor<IInputState,IBatchStatistics>{
	IProcessor<IChemObject,IChemObject> getProcessor();
}
