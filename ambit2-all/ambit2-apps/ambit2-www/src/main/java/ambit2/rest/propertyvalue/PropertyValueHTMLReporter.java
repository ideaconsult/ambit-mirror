package ambit2.rest.propertyvalue;

import java.io.Writer;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.db.QueryURIReporter;

import org.restlet.Request;
import org.restlet.data.Reference;

import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.PropertyValue;
import ambit2.db.search.AbstractQuery;
import ambit2.rest.DisplayMode;
import ambit2.rest.QueryHTMLReporter;
import ambit2.rest.property.PropertyResource;
import ambit2.rest.property.PropertyURIReporter;
import ambit2.rest.structure.CompoundHTMLReporter;

/**
 * Generates html out of proeprty/value pairs
 * @author nina
 *
 * @param <T>
 */
public class PropertyValueHTMLReporter<T> extends QueryHTMLReporter<T,IQueryRetrieval<T>> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1971719947783649465L;
	protected CompoundHTMLReporter<IQueryRetrieval<IStructureRecord>> cmp_reporter;
	protected PropertyURIReporter propertyURIReporter;
	protected IStructureRecord record;
	protected boolean editable = false;
	
	public boolean isEditable() {
		return editable;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	public PropertyValueHTMLReporter(Request baseRef,ResourceDoc doc) {
		this(baseRef,DisplayMode.singleitem,doc);
	}
	public PropertyValueHTMLReporter(Request baseRef, DisplayMode _dmode,ResourceDoc doc) {
	
		super(baseRef,DisplayMode.table,doc);
		cmp_reporter = new CompoundHTMLReporter<IQueryRetrieval<IStructureRecord>>(baseRef,doc,_dmode,true);
		propertyURIReporter = new PropertyURIReporter(baseRef);
		this.editable = editable;
	}
	@Override
	protected QueryURIReporter createURIReporter(Request request, ResourceDoc doc) {
		return new PropertyValueURIReporter(request,doc);
	}
	

	@Override
	public Object processItem(T item) throws AmbitException  {
		try {
			
			if (item instanceof IStructureRecord) {
				IStructureRecord v = (IStructureRecord) item;
				for (Property p : v.getProperties())
					output.write(String.format("%s = %s<br>",p,v.getProperty(p)));
			} else if (item instanceof PropertyValue) {
				PropertyValue v = (PropertyValue) item;
				
				output.write(String.format("<form action='%s' method='POST'>",
						getUpdateAction(v)
						));
				output.write("<tr>");
				output.write("<th align='right'>");
				output.write(String.format("<a href='%s' target='_blank'>%s</a>&nbsp;%s</th>",
						propertyURIReporter.getURI(v.getProperty()),
						v.getProperty().getLabel(),
						v.getProperty().getUnits()
						));

				if (editable) {
					output.write("<td>");
					output.write(String.format("<input type='hidden' name='%s' value='%d'>",PropertyResource.idfeaturedef,v.getProperty().getId()));
					output.write(String.format("<input type='text' name='value' size='40' value='%s'>",v.getValue().toString()));
					output.write("</td>");
	
					output.write("<td>");
					output.write("<input type='submit' value='Update'>");
					output.write("</td>");
				} else
					output.write(String.format("<td>%s</td>",
							v.getValue().toString().replace("\n", "<br>")));					
				output.write("<td>");				
				if (v.getValue().toString().startsWith("InChI"))
					output.write(String.format("&nbsp;<a href='http://rdf.openmolecules.net/?%s' target='_blank'><img src='%s/images/search.png' border='0' alt='Look up at OpenMolecules.net' title='openmolecules.net'></a>",
							Reference.encode(v.getValue().toString()),uriReporter.getBaseReference().toString()));						
				output.write("</td>");	
				output.write("</tr>");
				
				output.write("</form>");
			} else	output.write(item.toString());
			
		} catch (Exception x) {
			
		}
		return null;
		
	}
	protected String getUpdateAction(PropertyValue v) {
		return String.format("%s%s/compound/%d/conformer/%d%s%d",
				getUriReporter().getBaseReference(),
				PropertyValueResource.featureKey,
				record.getIdchemical(),
				record.getIdstructure(),
				PropertyResource.featuredef,
				v.getProperty().getId()
				);
	}
	@Override
	public void header(Writer w, IQueryRetrieval<T> query) {
		super.header(w, query);
		try {
			if (query instanceof AbstractQuery) {
				if (((AbstractQuery)query).getValue() instanceof IStructureRecord) {
					record = (IStructureRecord)((AbstractQuery)query).getValue();
					cmp_reporter.setOutput(output);
					cmp_reporter.processItem(record);
				}
			}			
			w.write("<table>");
		} catch (Exception x) {}
	}
	@Override
	public void footer(Writer w, IQueryRetrieval<T> query) {
		try {
			w.write("</table>");
		} catch (Exception x) {}
		super.footer(output, query);
	}
	
}
