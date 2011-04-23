package ambit2.rest.bookmark;

import java.io.Writer;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;

import ambit2.base.data.Bookmark;
import ambit2.base.exceptions.AmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.ResourceDoc;

public class BookmarkHTMLReporter extends QueryHTMLReporter<Bookmark, IQueryRetrieval<Bookmark>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959033048710547839L;

	public BookmarkHTMLReporter() {
		this(null,true,null);
	}
	public BookmarkHTMLReporter(Request baseRef, boolean collapsed, ResourceDoc doc) {
		super(baseRef,collapsed,doc);
	}
	@Override
	protected QueryURIReporter createURIReporter(Request request, ResourceDoc doc) {
		return new BookmarkURIReporter<IQueryRetrieval<Bookmark>>(request,doc);
	}
	@Override
	public void header(Writer w, IQueryRetrieval<Bookmark> query) {
		super.header(w, query);
		try {
			w.write(String.format("<h3>Bookmarks </h3>",""));
			
			Reference ref = getUriReporter().getRequest().getResourceRef().clone();
			ref.setQuery(null);
			w.write(String.format("<a href='%s/topics'>Topics</a><br>",ref));
			
			w.write(String.format("<a href='?media=%s'>RDF/XML</a>&nbsp;",URLEncoder.encode(MediaType.APPLICATION_RDF_XML.toString())));
			w.write(String.format("<a href='?media=%s'>RDF N3</a>&nbsp;",URLEncoder.encode(MediaType.TEXT_RDF_N3.toString())));
			w.write(String.format("<a href='?media=%s'>CSV</a><br>",URLEncoder.encode(MediaType.TEXT_CSV.toString())));
			//rdfa
			output.write("<div xmlns:dc=\"http://purl.org/dc/elements/1.1/\">");
			output.write("<div xmlns:a=\"http://www.w3.org/2000/10/annotation-ns#\">");
			w.write("<table width='99%'>");
			w.write("<tr>");
			w.write("<th>Bookmark URI</th><th>Topic</th><th>Title</th><th>Description</th><th>URI</th><th>Created</th><th>Creator</th>");
			w.write("</tr>");
		} catch (Exception x) {}
	}
	@Override
	public void footer(Writer output, IQueryRetrieval<Bookmark> query) {
		try {
			output.write("</table>");
		} catch (Exception x) {}
		super.footer(output, query);
	}
	@Override
	public Object processItem(Bookmark item) throws AmbitException  {
		try {
			String uri = uriReporter.getURI(item);

			output.write(String.format("<tr><div about='%s'>",uri));

			output.write(String.format(
						"<td><a href=\"%s\"><span property=\"dc:title\">%s</span></a></td>",
						uriReporter.getURI(item),
						uriReporter.getURI(item)));
			
			output.write(String.format(
					"<td><div><span property=\"a:hasTopic\">%s</span></div></td>",
					item.getHasTopic()));	
			
			output.write(String.format(
					"<td><div><span property=\"dc:title\">%s</span></div></td>",
					item.getTitle()));					
			
			output.write(String.format(
					"<td><div><span property=\"dc:description\">%s</span></div></td>",
					item.getDescription()));					
			
			output.write(String.format(
						"<td><a href=\"%s\" target='_blank'><span property=\"a:recalls\">%s</span></a></td>",
						item.getRecalls(),
						item.getRecalls()));	
			
			output.write(String.format(
					"<td>%s</td>",
					SimpleDateFormat.getInstance().format(new Date(item.getCreated()))));	

			output.write(String.format(
					"<td>%s</td>",
					item.getCreator()));	
			
				
				output.write("</div><tr>");
		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			
		}
		return null;
	}

}