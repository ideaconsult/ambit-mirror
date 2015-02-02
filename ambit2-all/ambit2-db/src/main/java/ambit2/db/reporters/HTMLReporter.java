package ambit2.db.reporters;

import java.io.Writer;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.processors.ProcessorStructureRetrieval;

/**
 * Very simple html reporter
 * 
 * @author nina
 * 
 * @param <Q>
 */
@Deprecated
public class HTMLReporter<Q extends IQueryRetrieval<IStructureRecord>> extends QueryStructureReporter<Q, Writer> {
    /**
	 * 
	 */
    private static final long serialVersionUID = 2931123688036795689L;

    public HTMLReporter() {
	getProcessors().clear();
	getProcessors().add(new ProcessorStructureRetrieval());
	getProcessors().add(new DefaultAmbitProcessor<IStructureRecord, IStructureRecord>() {
	    public IStructureRecord process(IStructureRecord target) throws AmbitException {
		processItem(target);
		return target;
	    };
	});
    }

    @Override
    public Object processItem(IStructureRecord item) throws AmbitException {

	try {
	    // TODO generate PDFcontent
	    getOutput().write("<p>");
	    getOutput().write(item.getContent());
	} catch (Exception x) {
	    logger.log(java.util.logging.Level.SEVERE, x.getMessage(), x);
	}
	return null;
    }

    public void open() throws DbAmbitException {
	// TODO Auto-generated method stub

    }

    public void footer(Writer output, Q query) {
	try {
	    output.write(String.format("<html><head><title>%s</title><body>", query.toString()));
	} catch (Exception x) {

	}
    };

    public void header(Writer output, Q query) {
	try {
	    output.write("</body></html>");
	    output.flush();
	} catch (Exception x) {

	}
    };

}