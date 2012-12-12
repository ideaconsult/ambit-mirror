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
	protected String jsonpCallback = null;
	protected PropertyJSONReporter propertyJSONReporter;
	protected String hilightPredictions = null;
	
	public String getHilightPredictions() {
		return hilightPredictions;
	}

	public void setHilightPredictions(String hilightPredictions) {
		this.hilightPredictions = hilightPredictions;
	}



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
	
	public CompoundJSONReporter(Template template, Request request,ResourceDoc doc, String urlPrefix,String jsonpCallback) {
		this(template,null,request,doc,urlPrefix,jsonpCallback);
	}
	
	public CompoundJSONReporter(Template template,Profile groupedProperties, Request request,ResourceDoc doc, String urlPrefix,String jsonpCallback) {
		super(template,groupedProperties,request,doc,urlPrefix);
		this.jsonpCallback = jsonpCallback;
		propertyJSONReporter = new PropertyJSONReporter(request);
		hilightPredictions = request.getResourceRef().getQueryAsForm().getFirstValue("model_uri");
	}

	@Override
	public void setOutput(Writer output) throws AmbitException {
		super.setOutput(output);
		propertyJSONReporter.setOutput(output);
	}
	@Override
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
			builder.append(String.format("\t\"%s\":{\n",jsonCompound.compound.jsonname()));
			builder.append(String.format("\t\t\"%s\":\"%s\",\n",jsonCompound.URI.jsonname(),uri));
			//similarity
			Object similarityValue = null;
			for (Property p : item.getProperties()) 
				if ("metric".equals(p.getName())) {
					similarityValue = item.getProperty(p);
					break;
				}
			builder.append(String.format("\t\t\"metric\":%s,",similarityValue));
			
			builder.append(String.format("\t\t\"%s\":\"\",","name")); //placeholders
			builder.append(String.format("\t\t\"%s\":\"\",","cas"));
			builder.append(String.format("\t\t\"%s\":\"\"","einecs"));
			

			builder.append("\n\t\t},\n");
			
			builder.append(String.format("\t\"%s\":{\n",jsonCompound.values.jsonname()));
			String comma1 = null;
			for (int j=0; j < header.size(); j++) {

				Property p = header.get(j);
				Object value = item.getProperty(p);
				String key = reporter.getURI(p);
				if (key.contains("cdk:Title") || key.contains("cdk:Formula")) continue;
				if (key.contains("SMARTSProp")) continue;
				if (value==null) {
					continue; //builder.append(String.format("\t\t\"%s\":null",key));
				} 
				if (comma1!=null) {
					builder.append(comma1);
					builder.append("\n");
				}
				if (value instanceof Double) 
					builder.append(String.format("\t\t\"%s\":%6.3f",key,(Double)value));
				else if (value instanceof Integer) 
					builder.append(String.format("\t\t\"%s\":%d",key,(Integer)value));
				else if (value instanceof Long) 
					builder.append(String.format("\t\t\"%s\":%l",key,(Long)value));
				else 
					builder.append(String.format("\t\t\"%s\":\"%s\"",key,value.toString().replace("\"","'").replace("\\","").replace("\n","|")));				
				i++;
				comma1 = ",";
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
			if (jsonpCallback!=null) {
				output.write(jsonpCallback);
				output.write("(");
			}
			output.write("{\n");
			output.write("\"query\": {");
			output.write("\n\t\"summary\":");
			output.write("\"");
			output.write(query==null?"":query.toString().replace("\n"," ").replace("\r", " "));
			output.write("\"");
			output.write("\n},");
			output.write("\n\"dataEntry\":[");
			
		} catch (Exception x) {
			x.printStackTrace();
		}
	};
	/**
	 * "{"f1":"feature1","f2":{"uri":"feature2","smth":"smb"}}"
	 */
	@Override
	public void footer(java.io.Writer output, Q query) {
		try {
			output.write("\n],");
		} catch (Exception x) {}
		
		try {
			if (hilightPredictions==null)
				output.write(String.format("\n\"%s\":null,","model_uri"));
			else
				output.write(String.format("\n\"%s\":\"%s\",","model_uri",hilightPredictions));
			output.write("\n\"feature\":{\n");
			if (header!=null)
			for (int j=0; j < header.size(); j++) 
				propertyJSONReporter.processItem(header.get(j));
			
		} catch (Exception x) {
			//x.printStackTrace();
		} finally {
			try {output.write("}\n");} catch (Exception x) {}
		}
		
		
		try {
			output.write("}\n");
			
			if (jsonpCallback!=null) {
				output.write(");");
			}
		} catch (Exception x) {}

	};
	

	
	@Override
	public String getFileExtension() {
		return null;//"json";
	}
	

	
}
