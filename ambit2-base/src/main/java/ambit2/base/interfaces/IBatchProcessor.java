package ambit2.base.interfaces;

import java.util.Iterator;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.processors.ProcessorsChain;

public interface IBatchProcessor<Target,ItemInput,Result> {
	ProcessorsChain<ItemInput,Result,IProcessor> getProcessorChain();
	void setProcessorChain(ProcessorsChain<ItemInput,Result,IProcessor> processor);
	Iterator<ItemInput> getIterator(Target target) throws AmbitException;
	void beforeProcessing(Target target) throws AmbitException;
	void afterProcessing(Target target,Iterator<ItemInput>  iterator) throws AmbitException;
	Result getResult(Target target);
	void onError(ItemInput input, Object output ,Result result, Exception x);
	void onItemRead(ItemInput input, Result result);
	void onItemProcessed(ItemInput input, Object output, Result result);

}
