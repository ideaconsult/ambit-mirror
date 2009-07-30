package ambit2.rest.reference;

import java.io.StringWriter;
import java.io.Writer;

import org.restlet.data.Reference;

import ambit2.base.data.LiteratureEntry;
import ambit2.db.SourceDataset;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;

public class ReferenceHTMLReporter extends QueryHTMLReporter<LiteratureEntry, IQueryRetrieval<LiteratureEntry>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959033048710547839L;
	public ReferenceHTMLReporter() {
		this(null);
	}
	public ReferenceHTMLReporter(Reference baseRef) {
		super(baseRef);
	}
	@Override
	protected QueryURIReporter createURIReporter(Reference reference) {
		return new ReferenceURIReporter<IQueryRetrieval<LiteratureEntry>>(reference);
	}
	@Override
	public void processItem(LiteratureEntry item, Writer output) {
		try {
			output.write(String.format(
						"<a href=\"%s\">%s</a><br>",
						uriReporter.getURI(item),
						item.getName()));
				
		} catch (Exception x) {
			
		}
	}

}
