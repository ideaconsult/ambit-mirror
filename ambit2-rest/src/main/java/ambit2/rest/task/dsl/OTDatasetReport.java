package ambit2.rest.task.dsl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import net.idea.restnet.c.task.ClientResourceWrapper;

import org.opentox.dsl.OTDataset;
import org.opentox.dsl.OTFeatures;
import org.opentox.dsl.OTObject;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;

import ambit2.core.io.QuotedTokenizer;
import ambit2.rest.OpenTox;

public abstract class OTDatasetReport extends OTObject {
	protected List<String> header;
	protected List<String> values;
	protected String application;
	protected OTDataset dataset;
	protected OTFeatures features;
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
	protected OTDatasetReport(Reference  uri) {
		super(uri);
		
	}
	
	protected OTDatasetReport(OTDataset dataset,OTFeatures features, String application, int page, int pageSize, String representation) throws Exception {
		super(String.format("%s/query/compound/url%s?search=%s%s%s",
				application,
				representation==null?"/all":representation,
				Reference.encode(dataset.getPage(page, pageSize).getUri().toString()),
				features==null?"":"&",
				features==null?"":features.getQuery(null).getQueryString()
				));
		this.application = application;
		this.dataset = dataset;
		header = new ArrayList<String>();
		values = new ArrayList<String>();
		this.page = page;
		this.pageSize = pageSize;
		this.features = features;
	}

	
	public OTDatasetReport write(Writer writer) {
		Reference ref = getUri().clone();
		Form form = ref.getQueryAsForm();;
		ref.setQuery("");
		ClientResourceWrapper client = new ClientResourceWrapper(ref);
		Representation r = null;
		try {
			
			r = client.post(form.getWebRepresentation(),MediaType.TEXT_CSV);
			BufferedReader reader = new BufferedReader(new InputStreamReader(r.getStream()));
			String line = null;
			int row= 0;
			writer.write(header());
			while ((line = reader.readLine())!=null) {
				extractRowKeyAndData(line,row,row==0?header:values);
				if (row>0)
					writeRow(row,values,writer);
				else {
					for (int i=0;i < header.size();i++)
						header.set(i, header.get(i).replace("http://www.opentox.org/api/1.1#", ""));
					writeHeader(writer);
				}
				row++;
			}

		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			try { writer.write(footer()); }catch (Exception x) {}
			try {r.release();} catch (Exception x) {}
			try {client.release();} catch (Exception x) {}
		}
		return this;
	}

	public void writeHeader(Writer writer) throws IOException {}
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
		//b.append(String.format("\n<input type='hidden' value='%s'/>\n",dataset.uri));
		//b.append(String.format("\n<input type='hidden' value='%s'/>\n",uri));
		
		b.append(String.format("\n<a href='#' onClick=\"contentDisp('%s',%d,'%s');\">&laquo;</a>&nbsp;",
				requestref.getBaseRef(),
				page<=1?1:page-1,prev(true)));
		
		for (int i=0; i < page;i++) {
		b.append(String.format("\n<a href='%s' onClick=\"contentDisp('%s',%d,'%s');\">%d</a>&nbsp;",
				"#",
				requestref.getBaseRef(),
				i+1,
				getPage(i, pageSize,true),
				i+1
				));
	   }		
		b.append(String.format("\nPage&nbsp;<label id='page' title='page'>%d</label> Records per page <label id='pagesize' title='items per page'>%d</label> &nbsp;\n<a href='#' onClick=\"contentDisp('%s',%d,'%s');\">&raquo;</a>",
				page+1,pageSize,
				requestref.getBaseRef(),
				page+1,next(true)));		
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
		 
		 return form.getQueryString();
	 }	
	
}
