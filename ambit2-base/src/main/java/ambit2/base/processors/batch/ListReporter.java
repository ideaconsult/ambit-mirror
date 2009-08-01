package ambit2.base.processors.batch;

import java.util.Iterator;
import java.util.List;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IBatchStatistics;

public abstract class ListReporter<Item,Output> extends BatchReporter<Item, List<Item>, Output> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3018737752170422730L;

	@Override
	protected BatchProcessor<List<Item>, Item> createBatch() {

		return new BatchProcessor<List<Item>, Item>() {
			public Iterator<Item> getIterator(List<Item> target)
					throws AmbitException {
				return target.iterator();
			};
			public void afterProcessing(List<Item> target,
					Iterator<Item> iterator) throws AmbitException {
				footer(output, target);
				
			}
			public void beforeProcessing(List<Item> target)
					throws AmbitException {
				header(output, target);
				
			}
			public void onItemRead(Item input, ambit2.base.interfaces.IBatchStatistics result) {
				
			};
			public void onItemProcessed(Item input, Object output, ambit2.base.interfaces.IBatchStatistics result) {
				
			};
			public void onError(Item input, Object output, ambit2.base.interfaces.IBatchStatistics result, Exception x) {
				
			};
			@Override
			protected void closeIterator(Iterator iterator)
					throws AmbitException {
				
			}
			public IBatchStatistics getResult(List<Item> target) {

				return null;
			}
		};
		
	}

}
