package ambit2.rest.dataset;

import java.io.StringWriter;
import java.io.Writer;

import org.restlet.data.Reference;

import ambit2.db.SourceDataset;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.query.QueryResource;
import ambit2.rest.query.XMLTags;
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
					"<a href=\"%s%s\"><img src=\"/images/structures.gif\" alt=\"compounds\" title=\"Browse compounds\"/></a>",
					w.toString(),
					CompoundResource.compound));	
			
			output.write("&nbsp;");
			output.write(String.format(
					"<a href=\"%s%s\"><img src=\"/images/search.png\" alt=\"query\" title=\"Search compounds\"/></a>",
					w.toString(),
					QueryResource.query_resource));	
		
		} catch (Exception x) {
			
		}
	}


}
