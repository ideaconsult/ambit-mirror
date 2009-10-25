package ambit2.rest.dataset;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.restlet.data.Request;

import ambit2.db.SourceDataset;
import ambit2.db.readers.IQueryRetrieval;

/**
 * List of datasets in HTML
 * TODO make use of template engine
 * @author nina
 *
 */
public class DatasetHTMLReporter extends DatasetsHTMLReporter {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959033048710547839L;
	public DatasetHTMLReporter() {
		this(null,true);
	}
	public DatasetHTMLReporter(Request baseRef,boolean collapsed) {
		super(baseRef,collapsed);
	}
	@Override
	public void processItem(SourceDataset dataset, Writer output) {
		try {
			StringWriter up = new StringWriter();
			uriReporter.processItem(null, up);
			output.write(String.format(
					"<a href=\"%s\">Datasets</a>",
					up.toString()));
			
			output.write("<hr>");
			StringWriter w = new StringWriter();
			uriReporter.processItem(dataset, w);
			
		output.write(String.format(
					"<h3><a href=\"%s\">%s</a></h3>",
					w.toString(),
					dataset.getName()));
		

		
		output.write(String.format(
				"<a href=\"%s/features\">Properties</a><br>",
				w.toString(),
				dataset.getName()));	
		
		output.write(String.format(
				"<a href=\"%s/compound\">Compounds</a><br>",
				w.toString(),
				dataset.getName()));				
		} catch (Exception x) {
			x.printStackTrace();
		}		

		
	}
	@Override
	public void header(Writer w, IQueryRetrieval<SourceDataset> query) {
		super.header(w, query);
		try {
			w.write(collapsed?"<h3>Datasets</h3>":"<h3>Dataset</h3>");
			} catch (IOException x) {}
	}	

}
