package ambit2.rest.structure;

import java.io.IOException;
import java.io.Writer;

import org.restlet.Request;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.rest.ResourceDoc;
import ambit2.rest.dataset.ARFFResourceReporter;
import ambit2.rest.property.PropertyJSONReporter;

import com.lowagie.text.html.HtmlEncoder;

/**
 * JSON
 * @author nina
 *
 * @param <Q>
 */
public class CompoundJSONReporter<Q extends IQueryRetrieval<IStructureRecord>> extends ARFFResourceReporter<Q> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 410930501401847402L;
	protected String comma = null;
	protected PropertyJSONReporter propertyJSONReporter;
	enum jsonCompound {
		URI,
		compound,
		dataset,
		dataEntry,
		values;
		
		public String jsonname() {
			return name();
		}
	}
	
	public CompoundJSONReporter(Template template, Request request,ResourceDoc doc, String urlPrefix) {
		this(template,null,request,doc,urlPrefix);
	}
	
	public CompoundJSONReporter(Template template,Profile groupedProperties, Request request,ResourceDoc doc, String urlPrefix) {
		super(template,groupedProperties,request,doc,urlPrefix);
		propertyJSONReporter = new PropertyJSONReporter(request);
	}

	@Override
	public void setOutput(Writer output) throws AmbitException {
		super.setOutput(output);
		propertyJSONReporter.setOutput(output);
	}
	protected void writeHeader(Writer writer) throws IOException {
		if (header == null) {
			header = template2Header(template,true);
			/*
			writer.write("@attribute URI string\n");
			for (Property p : header) {
				writer.write(getPropertyHeader(p));
			}
			
			writer.write("\n@data\n");
			*/
		}
	}	
	@Override
	public Object processItem(IStructureRecord item) throws AmbitException {
		try {
			Writer writer = getOutput();
			writeHeader(writer);
			int i = 0;
			String uri = String.format("%s/compound/%d",urlPrefix,item.getIdchemical());
			if (item.getIdstructure()>0)
				uri = String.format("%s/conformer/%d",uri,item.getIdstructure());
			
			StringBuilder builder = new StringBuilder();
			if (comma!=null) builder.append(comma);
			
			builder.append("\n\t{\n");
			builder.append(String.format("\t%s:{\n",jsonCompound.compound.jsonname()));
			builder.append(String.format("\t\t%s:\"%s\",\n",jsonCompound.URI.jsonname(),uri));
			builder.append(String.format("\t\t%s:\"%s\"","type","Compound"));
			builder.append("\n\t\t},\n");
			
			builder.append(String.format("\t\"%s\":{",jsonCompound.values.jsonname()));
			//builder.append(String.format("\t\"%s\":[",jsonCompound.values.jsonname()));
			for (int j=0; j < header.size(); j++) {
				if (j>0) builder.append(",");
				Property p = header.get(j);
				//builder.append("\n\t\t{\n");
				builder.append("\n");
				Object value = item.getProperty(p);
				//builder.append(String.format("\t\t\"%s\":\"%s\",\n","feature",reporter.getURI(p)));
				//String key = "value";
				String key = reporter.getURI(p);
				if (value==null) {
					builder.append(String.format("\t\t\"%s\":null",key));
				} else if (p.getClazz().equals(String.class))
					builder.append(String.format("\t\t\"%s\":\"%s\"",key,HtmlEncoder.encode(value.toString())));
				else if (value instanceof Double) 
					builder.append(String.format("\t\t\"%s\":%6.3f",key,(Double)value));
				else if (value instanceof Integer) 
					builder.append(String.format("\t\t\"%s\":%d",key,(Integer)value));
				else if (value instanceof Long) 
					builder.append(String.format("\t\t\"%s\":%l",key,(Long)value));
				else 
					builder.append(String.format("\t\t\"%s\":\"%s\"",key,HtmlEncoder.encode(value.toString())));				
				i++;
			}
			builder.append("\n\t\t}");
			//builder.append("\n\t\t]");
			
			builder.append("\n\t}");
			writer.write(builder.toString());
			comma = ",";
		} catch (Exception x) {
			logger.error(x);
		}
		return item;
		
	}
	
	@Override
	public void header(java.io.Writer output, Q query) {
		try {
			output.write("{\n");
			output.write("\"dataEntry\":[");
			
		} catch (Exception x) {}
	};
	/**
	 * "{"f1":"feature1","f2":{"uri":"feature2","smth":"smb"}}"
	 */
	@Override
	public void footer(java.io.Writer output, Q query) {
		try {
			output.write("\n],");
			output.write("\nfeature:{\n");
			for (int j=0; j < header.size(); j++) 
				propertyJSONReporter.processItem(header.get(j));
			output.write("}\n");

			output.write("}\n");
		} catch (Exception x) {}
	};
	

	
	@Override
	public String getFileExtension() {
		return null;//"json";
	}
	

	
}
