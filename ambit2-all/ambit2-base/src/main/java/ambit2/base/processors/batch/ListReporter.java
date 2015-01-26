package ambit2.base.processors.batch;

import java.util.Iterator;

import net.idea.modbcum.i.batch.IBatchStatistics;
import net.idea.modbcum.i.exceptions.AmbitException;

public abstract class ListReporter<Item, Output> extends BatchReporter<Item, Iterator<Item>, Output> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 3018737752170422730L;

    @Override
    protected BatchProcessor<Iterator<Item>, Item> createBatch() {

	return new BatchProcessor<Iterator<Item>, Item>() {
	    /**
		     * 
		     */
	    private static final long serialVersionUID = 3219592873552511753L;

	    public Iterator<Item> getIterator(Iterator<Item> target) throws AmbitException {
		return target;
	    };

	    public void afterProcessing(Iterator<Item> target, Iterator<Item> iterator) throws AmbitException {
		footer(output, target);

	    }

	    public void beforeProcessing(Iterator<Item> target) throws AmbitException {
		header(output, target);

	    }

	    public void onItemRead(Item input, IBatchStatistics result) {

	    };

	    public void onItemProcessed(Item input, Object output, IBatchStatistics result) {

	    };

	    public void onError(Item input, Object output, IBatchStatistics result, Exception x) {

	    };

	    @Override
	    protected void closeIterator(Iterator iterator) throws AmbitException {

	    }

	    public IBatchStatistics getResult(Iterator<Item> target) {

		return null;
	    }
	};

    }

}
