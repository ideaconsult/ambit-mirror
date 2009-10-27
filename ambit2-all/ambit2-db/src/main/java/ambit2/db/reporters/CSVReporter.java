package ambit2.db.reporters;

import java.io.IOException;
import java.io.Writer;

import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveProfileValues;
import ambit2.db.readers.RetrieveTemplateStructure;
import ambit2.db.readers.RetrieveProfileValues.SearchMode;

public class CSVReporter<Q extends IQueryRetrieval<IStructureRecord>> extends QueryHeaderReporter<IStructureRecord, Q, Writer> {


	/**
	 * 
	 */
	private static final long serialVersionUID = -4569410787926615089L;

	public CSVReporter() {
		this(null);
	}

	public CSVReporter(Template template) {
		setTemplate(template==null?new Template(null):template);
		getProcessors().clear();

		if (getTemplate().size()>0) 
			getProcessors().add(new ProcessorStructureRetrieval(new RetrieveProfileValues(SearchMode.idproperty,getTemplate())) {
				@Override
				public IStructureRecord process(IStructureRecord target)
						throws AmbitException {
					((RetrieveProfileValues)getQuery()).setRecord(target);
					return super.process(target);
				}
			});
		else
		getProcessors().add(new ProcessorStructureRetrieval(new RetrieveTemplateStructure(getTemplate())) {
				@Override
				public IStructureRecord process(IStructureRecord target)
						throws AmbitException {
					((RetrieveTemplateStructure)getQuery()).setRecord(target);
					return super.process(target);
				}
			});
		getProcessors().add(new DefaultAmbitProcessor<IStructureRecord,IStructureRecord>() {
			public IStructureRecord process(IStructureRecord target) throws AmbitException {
				processItem(target,getOutput());
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
			header = template2Header(template);
		
			writer.write("URI");
			for (Property p : header) 
				writer.write(String.format(",%s", p.getName()));
			
			writer.write("\n");
		}
	}	

	public void header(Writer writer, Q query) {

	};

	@Override
	public void processItem(IStructureRecord item, Writer writer) {
		try {
			writeHeader(writer);
			int i = 0;
			writer.write(String.format("/compound/%d",item.getIdchemical()));
			if (item.getIdstructure()>0)
				writer.write(String.format("/conformer/%d",item.getIdstructure()));
			for (Property p : header) {
				Object value = item.getProperty(p);
				if (p.getClazz()==Number.class) 
					writer.write(String.format(",%s",
							value==null?"":value
							));
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
		
	}

	public void open() throws DbAmbitException {
		
	}


}