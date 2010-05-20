package ambit2.db.reporters;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveGroupedValuesByAlias;
import ambit2.db.readers.RetrieveProfileValues;
import ambit2.db.readers.RetrieveProfileValues.SearchMode;

public class CSVReporter<Q extends IQueryRetrieval<IStructureRecord>> extends QueryHeaderReporter<Q, Writer> {


	/**
	 * 
	 */
	private static final long serialVersionUID = -4569410787926615089L;
	protected int numberofHeaderLines  = 1;
	protected String urlPrefix = "";
	public String getUrlPrefix() {
		return urlPrefix;
	}
	public void setUrlPrefix(String urlPrefix) {
		this.urlPrefix = urlPrefix;
	}
	public CSVReporter() {
		this(null);
	}
	public CSVReporter(Template template) {
		this(template,null,"");
	}
	public CSVReporter(Template template, Profile groupedProperties, String urlPrefix) {
		setUrlPrefix(urlPrefix);
		setGroupProperties(groupedProperties);
		setTemplate(template==null?new Template(null):template);
		getProcessors().clear();


		if ((getGroupProperties()!=null) && (getGroupProperties().size()>0))
			getProcessors().add(new ProcessorStructureRetrieval(new RetrieveGroupedValuesByAlias(getGroupProperties())) {
				@Override
				public IStructureRecord process(IStructureRecord target)
						throws AmbitException {
					((RetrieveGroupedValuesByAlias)getQuery()).setRecord(target);
					return super.process(target);
				}
			});
		if (getTemplate().size()>0) 
			getProcessors().add(new ProcessorStructureRetrieval(new RetrieveProfileValues(SearchMode.idproperty,getTemplate(),true)) {
				@Override
				public IStructureRecord process(IStructureRecord target)
						throws AmbitException {
					((RetrieveProfileValues)getQuery()).setRecord(target);
					return super.process(target);
				}
			});		
		getProcessors().add(new DefaultAmbitProcessor<IStructureRecord,IStructureRecord>() {
			public IStructureRecord process(IStructureRecord target) throws AmbitException {
				processItem(target);
				return target;
			};
		});	
	}
	public void footer(Writer output, Q query) {
	
		try { 
			if (header == null) {
				writeHeader(output);
			}				
			output.flush(); } catch (Exception x) {};
	};
	
	protected void writeHeader(Writer writer) throws IOException {
		if (header == null) {
			header = template2Header(template,true);

			
			if (numberofHeaderLines == 1) {
				writer.write("");
				for (Property p : header) 
					writer.write(String.format(",\"%s %s\"", p.getName()==null?"N?A":p.getName(),p.getUnits()==null?"":p.getUnits()));	
				writer.write("\n");
			} else {
				writer.write("");
				for (Property p : header) 
					writer.write(String.format(",\"%s\"", p.getUrl()));
				writer.write("\n");
				writer.write("");
				for (Property p : header) 
					writer.write(String.format(",\"%s\"", p.getTitle()));
				
				writer.write("\n");
				writer.write("URI");
				for (Property p : header) 
					writer.write(String.format(",\"%s\"", p.getName()));
				writer.write("\n");
				
				writer.write("");
				for (Property p : header) 
					writer.write(String.format(",\"%s\"", p.getUnits()));
				writer.write("\n");			
			}
		}
	}	

	public void header(Writer writer, Q query) {

	};

	@Override
	public Object processItem(IStructureRecord item) throws AmbitException {
		Writer writer = getOutput();
		try {
			
			writeHeader(writer);
			int i = 0;
			writer.write(String.format("%s/compound/%d",urlPrefix,item.getIdchemical()));
			if (item.getIdstructure()>0)
				writer.write(String.format("/conformer/%d",item.getIdstructure()));
			for (Property p : header) {
				Object value = item.getProperty(p);
				if (p.getClazz()==Number.class) 
					writer.write(String.format(",%s",
							value==null?"":value
							));
				else
					if ((value !=null)&& (value.toString().indexOf("<html>")>=0))
						writer.write(",\" \"");
					else
					writer.write(String.format(",\"%s\"",
							value==null?"":
							value.toString().replace("\n", "").replace("\r","")
							));					
				i++;
			}
			
		} catch (Exception x) {
			logger.error(x);
			x.printStackTrace();
		} finally {
			try { writer.write('\n'); } catch (Exception x) {}
		}
		return null;
	}

	public void open() throws DbAmbitException {
		
	}


}