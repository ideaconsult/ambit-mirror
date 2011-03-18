package ambit2.rest.task.dsl;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.opentox.dsl.OTDataset;
import org.opentox.dsl.OTFeatures;

public class OTDatasetScrollableReport extends OTDatasetReport {

	protected OTDatasetScrollableReport(OTDataset dataset,OTFeatures features, String application, int page, int pageSize) throws Exception {
		super(dataset,features, application,page,pageSize,"/all");
	}
	public static OTDatasetScrollableReport report(OTDataset dataset, OTFeatures features, String application, int page, int pageSize) throws Exception {
		return new OTDatasetScrollableReport(dataset,features,application,page,pageSize);
	}
	@Override
	public String footer() {
		return "\n</div></div>";
	}

	@Override
	public String header() {
		/*
		return  
		"\n<div id=\"actions\">"+
		"<a class=\"prev\">&laquo; Back</a>\n"+
		"<a class=\"next\">More structures &raquo;</a></div>\n"+
		*/
		try {
			return	String.format("<div id=\"BROWSER\">%s<div id=\"items\">\n",pageNavigator());
		} catch (Exception x) {
			return	String.format("<div id=\"BROWSER\">%s<div id=\"items\">\n","");	
		}
		//return	"<div class=\"scrollable vertical\"><div class=\"items\">\n";
	}

	@Override
	public void writeRow(int row, List<String> values, Writer writer)
			throws IOException {
		writer.write("\n<div><div class=\"item\">\n");
		writer.write(String.format(
				"%d.<img src='%s?w=240&h=200&media=image/png' alt='%s'>",
				row+page*pageSize,values.get(0),values.get(0)));
		writer.write(String.format("<h3><b>%s</b>&nbsp;<label title='%s'>%s</label></h3>",header.get(1),values.get(1),values.get(1)));
		
		writer.write("<strong>\n");
		for (int i=2; i< 8; i++) {
			writer.write("<b>\n");
			writer.write(header.get(i));
			writer.write("</b>&nbsp;&nbsp;");
			writer.write("&nbsp;&nbsp;");
			String v = values.get(i);
			writer.write(String.format("<label title='%s'>%s%s</label>",v,v.length()>100?v.substring(0,100):v,v.length()>100?"...":""));
			writer.write("<br>\n");
		}
		writer.write("</strong>\n");
		
		writeData(row,values,writer);
	
		//writer.write(String.format("<p><b>%s</b>&nbsp;%s</p>",header.get(header.size()-1),values.get(header.size()-1)));
		//writer.write("<a href=\"#\">More info</a> &nbsp; <a href=\"#\">Yet more info</a>");
		writer.write("\n</div></div>\n");
		
	}
	public void writeData(int row, List<String> values, Writer writer)
	throws IOException {
		writer.write("<table class='tablesorter'>");
		/*
		writer.write("<thead>");
		writer.write("<th></th><th>Name</th><th>Value</th>");
		writer.write("</thead>");
		*/
		writer.write("<tbody>");
		for (int i=8; i< header.size(); i++) {
			//writer.write(i%3==0?"<thead>":""); //test
			writer.write("<tr>");

			writer.write("<td></td>"); //test
			writer.write("<td>");
			//writer.write("<h3>\n");
			writer.write(header.get(i));
			//writer.write("</h3>");
			writer.write("</td>");
			writer.write("<td>");
			String v = values.get(i);
			writer.write(String.format("<label title='%s'>%s%s</label>",v,v.length()>100?v.substring(0,100):v,v.length()>100?"...":""));
			writer.write("</td>");
			writer.write("</tr>");
			//writer.write(i%3==0?"</thead>":""); //test
		}	
		writer.write("</tbody>");
		writer.write("</table>");

	}
}
