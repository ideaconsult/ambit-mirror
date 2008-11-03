package ambit2.core.processors.batch;

import ambit2.core.io.IInputState;
import ambit2.core.processors.IProcessor;

public interface IBatchProcessor<Target,Result> extends IProcessor<IInputState,IBatchStatistics>{
	IProcessor<Target,Result> getProcessor();
}
