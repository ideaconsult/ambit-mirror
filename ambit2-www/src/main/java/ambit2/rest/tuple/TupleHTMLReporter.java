package ambit2.rest.tuple;

import java.io.Writer;

import org.restlet.data.Reference;

import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.update.tuple.QueryTuple;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;

/**
 * 
 * @author nina
 *
 */
public class TupleHTMLReporter extends QueryHTMLReporter<Integer, QueryTuple> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -221066858588150887L;
	public TupleHTMLReporter(Reference ref,IStructureRecord record) {
		super(ref,true);
		((TupleURIReporter) uriReporter).setRecord(record);
	}
	@Override
	protected QueryURIReporter createURIReporter(Reference reference) {
		return  new TupleURIReporter(reference,null);
	}

	@Override
	public void processItem(Integer item, Writer output) {
		try {
		output.write(String.format("<a href=\"%s\">%d</a><br>", uriReporter.getURI(item),item));
		} catch (Exception x) {}
	}

}
