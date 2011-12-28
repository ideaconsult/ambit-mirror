package ambit2.db.reporters;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import ambit2.base.data.ISourceDataset;
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
	protected boolean writeCompoundURI = true;
	protected Property similarityColumn;
	protected String licenseColumn = null;
			
	public Property getSimilarityColumn() {
		return similarityColumn;
	}
	public void setSimilarityColumn(Property similarityColumn) {
		this.similarityColumn = similarityColumn;
	}
	public boolean isWriteCompoundURI() {
		return writeCompoundURI;
	}
	public void setWriteCompoundURI(boolean writeCompoundURI) {
		this.writeCompoundURI = writeCompoundURI;
	}
	public int getNumberofHeaderLines() {
		return numberofHeaderLines;
	}
	public void setNumberofHeaderLines(int numberofHeaderLines) {
		this.numberofHeaderLines = numberofHeaderLines;
	}

	protected String urlPrefix = "";
	protected String separator = ",";
	//http://www.rfc-editor.org/rfc/rfc4180.txt
	protected String lineseparator = "\r\n";
	public String getSeparator() {
		return separator;
	}
	public void setSeparator(String separator) {
		this.separator = separator;
	}
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
	@Override
	public void setLicenseURI(String licenseURI) {
		super.setLicenseURI(licenseURI);
		if (isIncludeLicenseInTextFiles()) {
			licenseColumn = "http://purl.org/dc/terms/rights";
			for (ISourceDataset.license l : ISourceDataset.license.values())
				if (l.getURI().equals(getLicenseURI())) {
					licenseColumn = "http://purl.org/dc/terms/license";
					break;
				}		
		} else licenseColumn = null;
	}
	public void footer(Writer output, Q query) {
	
		try { 
			if (header == null) {
				writeHeader(output);
			}				
			output.flush(); } catch (Exception x) {};
	};
	
	@Override
	protected List<Property> template2Header(Template template,
			boolean propertiesOnly) {
		List<Property> p = super.template2Header(template, propertiesOnly);
		if (similarityColumn!=null) 
			p.add(similarityColumn);
		return p;
	}
	protected void writeHeader(Writer writer) throws IOException {
		
		if (header == null) {
			header = template2Header(template,true);

			if (numberofHeaderLines <= 0) {
				//no header
			} else 	if (numberofHeaderLines == 1) {
				writer.write("Compound");
				for (Property p : header) 
					writer.write(String.format("%s\"%s %s\"", separator,p.getName()==null?"N?A":p.getName(),p.getUnits()==null?"":p.getUnits()));
				
				if (licenseColumn!=null)
					writer.write(String.format("%s%s",separator,licenseColumn));
				
				writer.write(lineseparator);
			} else {
				writer.write("");
				for (Property p : header) 
					writer.write(String.format("%s\"%s\"", separator,p.getUrl()));
				writer.write(lineseparator);
				writer.write("");
				for (Property p : header) 
					writer.write(String.format("%s\"%s\"", separator,p.getTitle()));
				
				writer.write(lineseparator);
				writer.write("URI");
				for (Property p : header) 
					writer.write(String.format("%s\"%s\"", separator,p.getName()));
				writer.write(lineseparator);
				
				writer.write("");
				for (Property p : header) 
					writer.write(String.format("%s\"%s\"", separator,p.getUnits()));

				if (licenseColumn!=null)
					writer.write(String.format("%s%s",separator,licenseColumn));
				
				writer.write(lineseparator);			
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
			
			if (writeCompoundURI) {
				writer.write(String.format("%s/compound/%d",urlPrefix,item.getIdchemical()));
				if (item.getIdstructure()>0)
					writer.write(String.format("/conformer/%d",item.getIdstructure()));
			}
			String delimiter = writeCompoundURI?separator:"";
			for (Property p : header) {
				
				Object value = item.getProperty(p);
			
				if (p.getClazz()==Number.class) 
					writer.write(String.format("%s%s",
							delimiter,
							value==null?"":value
							));
				else
					if ((value !=null)&& (value.toString().indexOf("<html>")>=0))
						writer.write(String.format("%s\" \"",delimiter));
					else
					writer.write(String.format("%s\"%s\"",
							delimiter,
							value==null?"":
							//value.toString().replace(lineseparator,"\n") 
							//would be nice to have Excel compatibility with multiline \n, but it breaks too many readers elsewhere ...
							value.toString().replace(lineseparator,"|").replace("\n", "|").replace("\r", "|")
							));					
				
				i++;
				delimiter = separator;
			}
			if (licenseColumn!=null)
				writer.write(String.format("%s%s",separator,getLicenseURI()));
			
		} catch (Exception x) {
			logger.error(x);
			x.printStackTrace();
		} finally {

			try { writer.write(lineseparator); } catch (Exception x) {}
		}
		return null;
	}

	public void open() throws DbAmbitException {
		
	}

	@Override
	public String getFileExtension() {
		return ",".equals(separator)?"csv":"txt";
	}

}