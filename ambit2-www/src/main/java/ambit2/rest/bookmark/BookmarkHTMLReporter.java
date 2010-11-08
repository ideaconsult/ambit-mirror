package ambit2.rest.bookmark;

import java.io.Writer;

import org.restlet.Request;

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
			w.write(String.format("<h3>Bookmarks</h3>",""));
			output.write("<div xmlns:dc=\"http://purl.org/dc/elements/1.1/\">");
		} catch (Exception x) {}
	}
	@Override
	public void footer(Writer output, IQueryRetrieval<Bookmark> query) {
		try {
			output.write("</div>");
		} catch (Exception x) {}
		super.footer(output, query);
	}
	@Override
	public Object processItem(Bookmark item) throws AmbitException  {
		try {
			output.write(String.format("<div about='%s'>",uriReporter.getURI(item)));
			output.write("Title: ");
			output.write(String.format(
						"<a href=\"%s\"><span property=\"dc:title\">%s</span></a>",
						uriReporter.getURI(item),
						item.getTitle()));
			
				output.write("&nbsp;");
				output.write("URL: ");
				output.write(String.format(
						"<a href=\"%s\" target='_blank'><span property=\"dc:description\">%s</span></a><br>",
						item.getRecalls(),
						item.getDescription()));	
			output.write(String.format("</div>"));
		} catch (Exception x) {
			x.printStackTrace();
		}
		return null;
	}

}