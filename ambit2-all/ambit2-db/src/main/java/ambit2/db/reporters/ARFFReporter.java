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
import ambit2.db.readers.RetrieveTemplateStructure;

public class ARFFReporter<Q extends IQueryRetrieval<IStructureRecord>> extends QueryHeaderReporter<IStructureRecord, Q, Writer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2931123688036795689L;


	public ARFFReporter() {
		this(null);
	}

	public ARFFReporter(Template template) {
		setTemplate(template==null?new Template(null):template);
		getProcessors().clear();
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
		/*
		@relation steroids_10mols_AlogP2

		@attribute MolName {aldosterone,androstanediol,19-nortestosterone,epicorticosterone,cortisolacetat,prednisolone,testosterone,17a-hydroxyprogesterone,progesterone,pregnenolone,etiocholanolone}
		@attribute MW numeric
		@attribute naAromAtom numeric
		@attribute topoShape numeric
		@attribute nHBDon numeric
		@attribute nHBAcc numeric
		@attribute Alogp2 numeric

		@data
		*/
		if (header == null) {
			header = template2Header(template);
		
			writer.write("@attribute URI string\n");
			for (Property p : header) 
				writer.write(String.format("@attribute %s %s\n", p.getName(), p.getClazz()==Number.class?"numeric":"string"));
			
			writer.write("\n@data\n");
		}
	}	

	public void header(Writer writer, Q query) {
		try {
			writer.write(String.format("@relation %s\n\n", "Dataset"));
		} catch (IOException x) {
			x.printStackTrace();
			//TODO throw exception
		}
	};

	@Override
	public void processItem(IStructureRecord item, Writer writer) {
		try {
			writeHeader(writer);
			int i = 0;
			writer.write(String.format("/compound/%d",item.getIdchemical()));
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
			writer.write('\n');
		} catch (Exception x) {
			logger.error(x);
		}
		
	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}


}