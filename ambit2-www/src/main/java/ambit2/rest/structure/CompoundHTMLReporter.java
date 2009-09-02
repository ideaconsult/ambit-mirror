package ambit2.rest.structure;

import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;

import org.openscience.cdk.CDKConstants;
import org.restlet.data.Reference;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveFieldPropertyValue;
import ambit2.rest.AmbitResource;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.QueryURIReporter;
import ambit2.rest.propertyvalue.PropertyValueHTMLReporter;

/**
Generates HTML file with links to structures . TODO - make use of a template engine 
 * @author nina
 *
 * @param <Q>
 */
public class CompoundHTMLReporter<Q extends IQueryRetrieval<IStructureRecord>> 
			extends QueryHTMLReporter<IStructureRecord,Q> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7776155843790521467L;
	protected PropertyValueHTMLReporter valueReporter;
	protected RetrieveFieldPropertyValue fieldQuery;
	
	public CompoundHTMLReporter(Reference reference,boolean collapsed,QueryURIReporter urireporter) {
		super(reference,collapsed);
		if (urireporter != null) this.uriReporter = urireporter;
		valueReporter = null;
		fieldQuery = null;
	}
	public CompoundHTMLReporter(Reference reference,boolean collapsed) {
		this(reference,collapsed,null);

	}
	@Override
	protected QueryURIReporter createURIReporter(Reference reference) {
		return new CompoundURIReporter<IQueryRetrieval<IStructureRecord>>(reference);
	}
	@Override
	public void processItem(IStructureRecord record, Writer writer) {

		try {
			writer.write(toURI(record));
			if (!collapsed) {
/*
				String[] more = new String[] {
						"conformer/all",
						
						"feature/",
						"query/similar/",
						"query/smarts/",
						"model/",
						"dataset/"
						
						};
				for (String m:more)
					output.write(String.format("<a href='%s'>%s</a>&nbsp;",m,m));
*/
			}
		} catch (Exception x) {
			logger.error(x);
		}
	}
	public void header(Writer output, Q query) {
		try {
			AmbitResource.writeHTMLHeader(output,
					collapsed?"Chemical compounds":"Chemical compound"
					,
					uriReporter.getBaseReference()
					);
			output.write("<h4><div class=\"actions\"><span class=\"right\">");
			output.write(String.format("<form method=\"post\" action=\"%s/query/results\">",uriReporter.getBaseReference()));
			output.write(String.format("%s","Query name:&nbsp;"));
			output.write(String.format("<input type=\"text\" name=\"name\" value=\"%s\" size=\"30\">&nbsp;",query.toString()));
			output.write("<input type=\"submit\" value='Save search results'>&nbsp;");
			//output.write("</form>");
			//output.write(String.format("<form method=\"post\" action=\"%s/model\">",uriReporter.getBaseReference()));
			output.write("<input type=\"submit\" value='Predict an endpoint'>&nbsp;");
			//output.write("</form>");	
			//output.write(String.format("<form method=\"post\" action=\"%s/algorithm\">",uriReporter.getBaseReference()));
			output.write("<input type=\"submit\" value='Build a model&nbsp;'>&nbsp;");
			output.write("<input type=\"submit\" value='Find similar compounds&nbsp;'>&nbsp;");
			output.write("<input type=\"submit\" value='Search within results&nbsp;'>&nbsp;");
			output.write("</form>");			
			output.write("</span></div></h4>\n");	
			output.write("<div id=\"div-1\">");
			

		} catch (Exception x) {}		
	};
	public void footer(Writer output, Q query) {
		try {
			output.write("</div>");
			AmbitResource.writeHTMLFooter(output,
					"",
					uriReporter.getBaseReference()
					);
			getOutput().flush();			
		} catch (Exception x) {}		
	};

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
	public String toURI(IStructureRecord record) {
		String w = uriReporter.getURI(record);
		StringBuilder b = new StringBuilder();
		b.append("<div id=\"div-1a\">");
		
		b.append("<div id=\"div-1b1\"><input type=checkbox name=\"structure\"></div>");
		
		b.append(String.format(
				"<a href=\"%s\"><img src=\"%s/diagram/png\" alt=\"%s\" title=\"%d\"/></a>", 
				w, w, 
				w, record.getIdchemical()));
		b.append("<div id=\"div-1d\">");

		/*
		if (getConnection()!= null) {
			if (fieldQuery==null) {
				fieldQuery = new RetrieveFieldPropertyValue();
				fieldQuery.setSearchByAlias(true);
			}
			fieldQuery.setValue(record);
			try {
				String[] features = new String[] {CDKConstants.CASRN};
				for (String feature: features) {
					StringWriter writer = new StringWriter();
					fieldQuery.setFieldname(Property.getInstance(feature,LiteratureEntry.getInstance()));
					valueReporter = new PropertyValueHTMLReporter(getUriReporter().getBaseReference()) ;
					valueReporter.setShowFooter(false);
					valueReporter.setShowHeader(false);				
					valueReporter.setOutput(writer);
					valueReporter.setConnection(getConnection());
					valueReporter.process(fieldQuery);
					b.append("<table>");
					b.append(writer.toString());
					b.append("</table>");
				}
			} catch (Exception x) {
				x.printStackTrace();
			} finally {
				try {valueReporter.setConnection(null); } catch (Exception x) {}
			}
		}
		*/
		String[][] s = new String[][] {
				{"feature",CDKConstants.CASRN,"CAS RN"},
				{"feature","EC","EINECS"},
				{"feature",CDKConstants.NAMES,"Chemical name(s)"},
				{"feature",null,"All available feature values"},
				{"tuple",null,"Feature values by groups"},
				{"feature_definition",null,"Feature definitions"}
				
		};
			for (String[] n:s)
			b.append(String.format("<a href=\"%s/%s/%s\" target=\"_blank\">%s</a><br>",w,n[0],n[1]==null?"":n[1],n[2]));
			//b.append("</div>");
			b.append("</div></div>");		
		return b.toString();
	}		

	@Override
	public void close() throws SQLException {
		valueReporter.close();
		super.close();
	}
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
	}
}
