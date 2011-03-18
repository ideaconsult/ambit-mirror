package ambit2.rest.task.dsl;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.opentox.dsl.OTDataset;
import org.opentox.dsl.OTFeatures;

public class OTDatasetTableReport extends OTDatasetReport {
	protected OTDatasetTableReport(OTDataset dataset,OTFeatures features, String application, int page, int pageSize) throws Exception {
			super(dataset.getUri());
			this.application = application;
			this.dataset = dataset;
			header = new ArrayList<String>();
			values = new ArrayList<String>();
			this.page = page;
			this.pageSize = pageSize;
			this.features = features;

	}
	public static OTDatasetTableReport report(OTDataset dataset,OTFeatures features, String application, int page, int pageSize) throws Exception {
		return new OTDatasetTableReport(dataset,features,application,page,pageSize);
	}
	
	public String header() {
		StringBuilder b = new StringBuilder();

		return b.toString();
	}
	public String footer() {
		return "";
	}
	public void writeRow(int row,List<String> values, Writer writer) throws IOException {
		for (int i=1; i< header.size(); i++) {
			writer.write(String.format("<tr><td>%s</td><td>%s</td></tr>",header.get(i),values.get(i)));
		}
	}
	
	@Override
	public void writeHeader(Writer writer) throws IOException {
	}
}
