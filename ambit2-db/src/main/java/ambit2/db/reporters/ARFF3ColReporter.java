package ambit2.db.reporters;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;

public class ARFF3ColReporter<Q extends IQueryRetrieval<IStructureRecord>> extends ARFFReporter<Q> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2931123688036795689L;
	protected List<Integer> compounds = new ArrayList<Integer>();
	
	public ARFF3ColReporter() throws IOException  {
		this(null,null);
	}
	public ARFF3ColReporter(Template template) {
		this(template,null);
	}
	public ARFF3ColReporter(Template template,Profile groupedProperties) {
		super(template,groupedProperties);
	}
	

	/*
	public void header(Writer writer, Q query) {
		try {
			if (getLicenseURI()==null)
				writer.write(String.format("@relation %s\n\n", getRelationName()));
			else
				writer.write(String.format("@relation %s_License:_%s\n\n", 
						getRelationName().trim().replace(" ", "_").replace("?", "_").replace("&", "_"), 
						getLicenseURI().trim().replace(" ", "_")));
			
			output.write("@attribute Compound int\r\n");
			output.write("@attribute Feature int\r\n");
			output.write("@attribute Value real\r\n");
			output.write("@DATA\r\n");
		} catch (IOException x) {
			x.printStackTrace();
			//TODO throw exception
		}
	};
	*/
	protected void completeTheHeader() {
		try { 
			//complete the URI attribute
			output.write("}\r\n");
			output.write("@ATTRIBUTE Feature {");
			delimiter="";
			for (Property p : header) {
				output.write(delimiter);
				output.write(getItemIdentifier(p));
				delimiter=",";
			}
			output.write("}\r\n");
			output.write("@ATTRIBUTE Value real\r\n");
			output.write("@DATA\r\n");
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		} finally {
		}
	}
	protected String getRelationName() {
		return "Dataset";
	}

	
	@Override
	public Object processItem(IStructureRecord item) throws AmbitException {
		try {
			Writer writer = tmpWriter;
			writeHeader(writer);
			String uri = String.format("%d",item.getIdchemical());

			output.append(delimiter);
			output.append(uri);
			delimiter = ",";
			
			for (Property p : header) {
				Object value = item.getProperty(p);
				if (value==null) continue;
				if (IQueryRetrieval.NaN.equals(value.toString())) continue;
				//write numbers only
				if (p.getClazz()==Number.class) {
					writer.append(uri);
					writer.append(",");
					writer.append(getItemIdentifier(p));
					writer.append(",");
					writer.append(value.toString());
					writer.write("\r\n");
				}			
			}
		} catch (Exception x) {
			logger.log(java.util.logging.Level.SEVERE,x.getMessage(),x);
		}
		return item;
		
	}	

	protected String getItemIdentifier(Property p) {
		String d = p.getName().indexOf(" ")>=0?"\"":"";
		return String.format("%s%s%s",d,p.getName(),d);
	}
	
	public void open() throws DbAmbitException {
	}

	@Override
	public String getFileExtension() {
		return "arff";
	}

}