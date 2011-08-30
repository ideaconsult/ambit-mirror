package ambit2.db.reporters;

import java.io.IOException;
import java.io.Writer;

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

public class ARFFReporter<Q extends IQueryRetrieval<IStructureRecord>> extends QueryHeaderReporter< Q, Writer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2931123688036795689L;

	protected String urlPrefix = "";
	public String getUrlPrefix() {
		return urlPrefix;
	}
	public void setUrlPrefix(String urlPrefix) {
		this.urlPrefix = urlPrefix;
	}
	
	public ARFFReporter() {
		this(null,null);
	}
	public ARFFReporter(Template template) {
		this(template,null);
	}
	public ARFFReporter(Template template,Profile groupedProperties) {
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
					IStructureRecord record = super.process(target);
					return record;
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
			header = template2Header(template,true);
		
			writer.write("@attribute URI string\n");
			for (Property p : header) {
				writer.write(getPropertyHeader(p));
			}
			
			writer.write("\n@data\n");
		}
	}	
	protected String getPropertyHeader(Property p) {
		String d = p.getName().indexOf(" ")>=0?"\"":"";
		return 
		String.format("@attribute %s%s%s %s\n", 
				d,
				p.getName(),
				d,
				p.getClazz()==Number.class?"numeric":"string");
	}
	public void header(Writer writer, Q query) {
		try {
			if (getLicenseURI()==null)
				writer.write(String.format("@relation %s\n\n", getRelationName()));
			else
				writer.write(String.format("@relation %s_License:_%s\n\n", 
						getRelationName(), 
						getLicenseURI().trim().replace(" ", "_")));
		} catch (IOException x) {
			x.printStackTrace();
			//TODO throw exception
		}
	};
	protected String getRelationName() {
		return "Dataset";
	}
	@Override
	public Object processItem(IStructureRecord item) throws AmbitException {
		try {
			Writer writer = getOutput();
			writeHeader(writer);
			int i = 0;
			writer.write(String.format("%s/compound/%d",urlPrefix,item.getIdchemical()));
			if (item.getIdstructure()>0)
				writer.write(String.format("/conformer/%d",item.getIdstructure()));
			for (Property p : header) {
				Object value = item.getProperty(p);
				if (p.getClazz()==Number.class) { 
					writer.write(String.format(",%s",
							(value==null)||(IQueryRetrieval.NaN.equals(value.toString()))?"?":value
							));
				} else
					writer.write(String.format(",%s%s%s",
							value==null?"":"\"",
							value==null?"?":
							value.toString().replace("\n", "").replace("\r",""),
							value==null?"":"\""
							));					
				i++;
			}
			writer.write('\n');
		} catch (Exception x) {
			logger.error(x);
		}
		return null;
		
	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}


}