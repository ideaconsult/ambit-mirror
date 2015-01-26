package ambit2.base.processors.batch;

import net.idea.modbcum.i.batch.IBatchStatistics;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.processors.ProcessorsChain;
import net.idea.modbcum.i.reporter.Reporter;
import net.idea.modbcum.p.DefaultAmbitProcessor;

/**
 * Batch reporter
 * 
 * @author nina
 * 
 * @param <T>
 * @param <Q>
 * @param <Output>
 */
public abstract class BatchReporter<Item, ItemList, Output> extends DefaultAmbitProcessor<ItemList, Output> implements
	Reporter<ItemList, Output> {
    /**
	 * 
	 */
    private static final long serialVersionUID = -3398613304724941241L;
    protected Output output = null;
    protected BatchProcessor<ItemList, Item> batch;
    protected String licenseURI = null;

    public Output getOutput() throws AmbitException {
	return output;
    }

    public void setOutput(Output output) throws AmbitException {
	this.output = output;
    }

    protected ProcessorsChain<Item, IBatchStatistics, IProcessor> processors;

    public ProcessorsChain<Item, IBatchStatistics, IProcessor> getProcessors() {
	return processors;
    }

    public void setProcessors(ProcessorsChain<Item, IBatchStatistics, IProcessor> processors) {
	this.processors = processors;
    }

    public BatchReporter() {
	super();
	processors = new ProcessorsChain<Item, IBatchStatistics, IProcessor>();
	processors.add(new DefaultAmbitProcessor<Item, Item>() {
	    /**
		     * 
		     */
	    private static final long serialVersionUID = 3764655145202835502L;

	    public Item process(Item target) throws AmbitException {
		processItem(target, output);
		return target;
	    };
	});

    }

    public abstract void header(Output output, ItemList query);

    public abstract void footer(Output output, ItemList query);

    public Output process(ItemList query) throws AmbitException {
	output = getOutput();
	header(output, null);

	batch = createBatch();
	try {
	    // batch.setMaxRecords(maxRecords);
	    batch.setProcessorChain(processors);

	    IBatchStatistics stats = batch.process(query);
	    /*
	     * if
	     * (stats.getRecords(IBatchStatistics.RECORDS_STATS.RECORDS_READ)==
	     * 0) throw new NotFoundException(query.toString());
	     */
	    return output;
	} catch (AmbitException x) {

	    throw x;
	} catch (Exception x) {

	    throw new AmbitException(x);
	} finally {
	    footer(output, null);

	}
    }

    protected abstract BatchProcessor<ItemList, Item> createBatch();

    public abstract void processItem(Item item, Output output);

    public long getTimeout() {
	return batch.getTimeout();
    }

    public void setTimeout(long timeout) {
	batch.setTimeout(timeout);

    }

    @Override
    public String getLicenseURI() {
	return licenseURI;
    }

    @Override
    public void setLicenseURI(String uri) {
	this.licenseURI = uri;
    }

    @Override
    public String getFileExtension() {
	return null;
    }
}