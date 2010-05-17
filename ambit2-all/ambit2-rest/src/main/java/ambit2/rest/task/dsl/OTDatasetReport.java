package ambit2.rest.task.dsl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import ambit2.core.io.QuotedTokenizer;
import ambit2.rest.OpenTox;

public abstract class OTDatasetReport extends OTObject {
	protected List<String> header;
	protected List<String> values;
	protected String application;
	protected OTDataset dataset;
	protected int page;
	protected int pageSize;
	
	protected Reference requestref;
	public Reference getRequestref() {
		return requestref;
	}
	public OTDatasetReport setRequestref(Reference requestref) {
		this.requestref = requestref;
		return this;
	}
	
	protected OTDatasetReport(OTDataset dataset,String application, int page, int pageSize) throws Exception {
		super(String.format("%s/query/compound/url/all?search=%s",
				application,
				Reference.encode(dataset.getPage(page, pageSize).uri.toString())));
		this.application = application;
		this.dataset = dataset;
		header = new ArrayList<String>();
		values = new ArrayList<String>();
		this.page = page;
		this.pageSize = pageSize;
	}

	
	public OTDatasetReport write(Writer writer) {
		
		ClientResource client = new ClientResource(uri);
		Representation r = null;
		try {
			r = client.get(MediaType.TEXT_CSV);
			BufferedReader reader = new BufferedReader(new InputStreamReader(r.getStream()));
			String line = null;
			int row= 0;
			writer.write(header());
			while ((line = reader.readLine())!=null) {
				extractRowKeyAndData(line,row,row==0?header:values);
				if (row>0)
					writeRow(row,values,writer);
				else 
					for (int i=0;i < header.size();i++)
						header.set(i, header.get(i).replace("http://www.opentox.org/api/1.1#", ""));
				row++;
			}

		} catch (Exception x) {
			
		} finally {
			try { writer.write(footer()); }catch (Exception x) {}
			try {r.release();} catch (Exception x) {}
			try {client.release();} catch (Exception x) {}
		}
		return this;
	}

	
	public void extractRowKeyAndData(String line, int row, List<String> values) throws Exception {
		values.clear();
		QuotedTokenizer st = new QuotedTokenizer(line,',');
			int fieldIndex = 0;
			while (st.hasMoreTokens()) {
				//if (fieldIndex>=values.length) break;
				String next = st.nextToken();
				if (next != null)
					values.add(removeStringDelimiters(next));
				else values.add("");
				fieldIndex ++;
			}
	}
	protected String removeStringDelimiters(String key) {
		char textDelimiter = '"';
		String k = key.trim();
		if (k.length() == 0)
			return "";
		if (k.charAt(0) == textDelimiter) {
			k = k.substring(1);
		}
		if (k.charAt(k.length() - 1) == textDelimiter) {
			k = k.substring(0, k.length() - 1);
		}
		return k;
	}
	
	public abstract String header();

	public abstract String footer();

	public abstract void writeRow(int row,List<String> values, Writer writer) throws IOException ;
	
	public String pageNavigator()  throws Exception  {
		
		StringBuilder b = new StringBuilder();
		b.append(String.format("<a href='#' onClick=\"contentDisp('%s',%d);\">&laquo;</a>&nbsp;",
				prev(true),page-1));
		for (int i=0; i < page;i++) {
		b.append(String.format("<a href='%s' onClick=\"contentDisp('%s',%d);\">%d</a>&nbsp;",
				"#",
				getPage(i, pageSize,true),
				i+1,i+1
				));
	   }		
		b.append(String.format("Page&nbsp;<label id='page' title='page'>%d</label> Records per page <label id='pagesize' title='items per page'>%d</label> &nbsp;<a href='#' onClick=\"contentDisp('%s',%d);\">&raquo;</a>",
				page+1,pageSize,next(true),page+2));		
		return b.toString();
	}
	public String prev( boolean removeHeader) throws Exception {
		if (page==0) return getRequestref().getQueryAsForm().getQueryString();
		else return getPage(page-1, pageSize,  removeHeader);
	}
	public String next( boolean removeHeader) throws Exception {
		return getPage(page+1, pageSize,  removeHeader);
	}	
	 public String getPage(int page,int pageSize, boolean removeHeader) throws Exception {
		 Reference ref = getRequestref();
		 Form form = ref.getQueryAsForm();
		 form.removeAll(OpenTox.params.page.toString());
		 form.removeAll(OpenTox.params.pagesize.toString());
		 form.add(OpenTox.params.page.toString(),Integer.toString(page));
		 form.add(OpenTox.params.pagesize.toString(),Integer.toString(pageSize));
		 if (removeHeader) {
			form.removeAll("header");
			form.add("header",Boolean.FALSE.toString());
		 }
		 
		 return "?"+form.getQueryString();
	 }	
	
}
