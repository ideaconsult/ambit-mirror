package ambit2.rest.tuple;

import java.io.Writer;

import org.restlet.data.Request;

import ambit2.base.data.SourceDataset;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.PropertiesTuple;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.update.tuple.QueryTuple;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.dataset.DatasetURIReporter;
import ambit2.rest.structure.CompoundHTMLReporter;

/**
 * 
 * @author nina
 *
 */
public class TupleHTMLReporter extends QueryHTMLReporter<PropertiesTuple, QueryTuple> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -221066858588150887L;
	protected DatasetURIReporter<IQueryRetrieval<SourceDataset>> ds_reporter;
	protected CompoundHTMLReporter<IQueryRetrieval<IStructureRecord>> cmp_reporter;
	public TupleHTMLReporter(Request ref,IStructureRecord record) {
		super(ref,true);
		((TupleURIReporter) uriReporter).setRecord(record);
		ds_reporter = new DatasetURIReporter<IQueryRetrieval<SourceDataset>>(ref);
		cmp_reporter = new CompoundHTMLReporter<IQueryRetrieval<IStructureRecord>>(ref,true);
		
	}
	@Override
	protected QueryURIReporter createURIReporter(Request request) {
		return  new TupleURIReporter(request,null);
	}
	@Override
	public void header(Writer w, QueryTuple query) {
		super.header(w, query);
		try {
			cmp_reporter.setOutput(w);
			cmp_reporter.processItem(query.getFieldname());
			w.write("<table><th>Dataset</th><th>Browse data</th>");
			
		} catch (Exception x) {};
	}
	@Override
	public void footer(Writer output, QueryTuple query) {
		try {output.write("</table>");} catch (Exception x) {};
		super.footer(output, query);
	}

	@Override
	public void processItem(PropertiesTuple item) throws AmbitException  {
		try {
		output.write(String.format("<tr><td><a href=\"%s\">%s</a></td><td><a href=\"%s\">Browse</a> </td></tr>", 
				ds_reporter.getURI(item.getDataset()),
				item.getDataset().getName(),
				uriReporter.getURI(item))
				);
		} catch (Exception x) {logger.error(x);}
	}

}
