package ambit2.rest.task.dsl;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.restlet.data.Reference;

public class OTDatasetTableReport extends OTDatasetReport {
	protected OTDatasetTableReport(OTDataset dataset,OTFeatures features, String application, int page, int pageSize) throws Exception {
		super(dataset,features, application,page,pageSize);
	}
	public static OTDatasetTableReport report(OTDataset dataset,OTFeatures features, String application, int page, int pageSize) throws Exception {
		return new OTDatasetTableReport(dataset,features,application,page,pageSize);
	}
	public String prev() throws Exception {
		if (page==0) return uri.toString();
		else return String.format("%s/query/compound/url/all?search=%s",
				application,
				Reference.encode(dataset.getPage(page-1, pageSize).uri.toString()));
	}
	
	public String header() {
		return "<table class='tablesorter'>";
	}
	public String footer() {
		return "</table>";
	}
	public void writeRow(int row,List<String> values, Writer writer) throws IOException {
		writer.write("<tr>");
		writer.write(String.format("<td><img src='%s?w=240&h=200&media=image/png' alt='%s'></td>",values.get(0),values.get(0)));
		writer.write("<td>");
		writer.write("<table>");
		for (int i=1; i< header.size(); i++) {
			writer.write("<tr>");
			writer.write("<th align='right'>");
			writer.write(header.get(i));
			writer.write("</th>");
			writer.write("<td>");
			writer.write(values.get(i));
			writer.write("</td>");
			writer.write("</tr>");
		}
		writer.write("</table>");
		writer.write("</td>");
		writer.write("</tr>");
	}

}
