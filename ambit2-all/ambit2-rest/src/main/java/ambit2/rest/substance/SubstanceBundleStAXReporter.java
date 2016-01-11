package ambit2.rest.substance;

import javax.xml.stream.XMLStreamWriter;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;

import ambit2.base.data.SubstanceRecord;
import ambit2.rest.QueryStaXReporter;

public class SubstanceBundleStAXReporter
		extends
		QueryStaXReporter<SubstanceRecord, IQueryRetrieval<SubstanceRecord>, SubstanceRecordStaxWriter> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4249582934025698175L;

	public SubstanceBundleStAXReporter(Request request) {
		super("", request);
		initProcessors();
	}
	
	protected void initProcessors() {
		
	}

	
	@Override
	public void setOutput(XMLStreamWriter output) throws Exception {
		super.setOutput(output);
		recordWriter.setOutput(output);
	}

	@Override
	protected QueryURIReporter<SubstanceRecord, IQueryRetrieval<SubstanceRecord>> createURIReporter(
			Request req, ResourceDoc doc) {
		return new SubstanceURIReporter<>(req);
	}

	@Override
	protected SubstanceRecordStaxWriter createRecordWriter(Request request,
			ResourceDoc doc) {
		return new SubstanceRecordStaxWriter(getUriReporter());
	}

	@Override
	public Object processItem(SubstanceRecord item) throws Exception {
		recordWriter.process(item);
		return item;
	}

	@Override
	public void close() throws Exception {
		try {
			recordWriter.setOutput(null);
		} catch (Exception x) {
		}
		super.close();
	}
}
