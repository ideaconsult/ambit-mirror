package ambit2.rest.dataset;

import java.io.StringWriter;
import java.io.Writer;

import org.restlet.data.Reference;

import ambit2.db.SourceDataset;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.structure.CompoundResource;

/**Generates html page for {@link QueryDatasetResource}
 * @author nina
 *
 */
public class DatasetsHTMLReporter extends QueryHTMLReporter<SourceDataset, IQueryRetrieval<SourceDataset>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959033048710547839L;
	public DatasetsHTMLReporter() {
		this(null);
	}
	public DatasetsHTMLReporter(Reference baseRef) {
		super(baseRef);
	}
	@Override
	protected QueryURIReporter createURIReporter(Reference reference) {
		return new DatasetURIReporter<IQueryRetrieval<SourceDataset>>(reference);
	}
	@Override
	public void processItem(SourceDataset dataset, Writer output) {
		try {
			StringWriter w = new StringWriter();
			uriReporter.processItem(dataset, w);
			output.write("<br>");
			output.write(String.format(
						"<a href=\"%s\">%s</a>",
						w.toString(),
						dataset.getName()));
			output.write("&nbsp;");
			output.write(String.format(
					"<a href=\"%s%s\">compounds</a>",
					w.toString(),
					CompoundResource.compound));			
		
		} catch (Exception x) {
			
		}
	}
	@Override
	public void header(Writer w, IQueryRetrieval<SourceDataset> query) {
		try {
			output.write(query.toString());
		} catch (Exception x) {
			
		}
	}

}
