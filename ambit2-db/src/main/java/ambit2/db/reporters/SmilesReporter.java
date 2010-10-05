package ambit2.db.reporters;

import java.io.IOException;
import java.io.Writer;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.core.config.AmbitCONSTANTS;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveGroupedValuesByAlias;
import ambit2.db.readers.RetrieveProfileValues;
import ambit2.db.readers.RetrieveProfileValues.SearchMode;
import ambit2.db.search.QuerySmilesByID;

public class SmilesReporter<Q extends IQueryRetrieval<IStructureRecord>> extends QueryStructureReporter<Q, Writer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3648376868814044783L;
	protected Template template;
	protected boolean writeProperties = false;
	protected Profile groupProperties;
	public Profile getGroupProperties() {
		return groupProperties;
	}
	public void setGroupProperties(Profile gp) {
		this.groupProperties = gp;
	}		
	protected Property key;
	public enum Mode {
		SMILES {
			@Override
			public String getTag() {
				return AmbitCONSTANTS.SMILES;
			}
		},
		InChI {
			@Override
			public String getTag() {
				return Property.opentox_InChI_std;
			}
			@Override
			public Property getProperty() {
				Property p = Property.getInstance(getTag(),"Default","http:///ambit.sourceforge.net");
				p.setLabel(getTag());
				return p;
			}
		};
		public abstract String getTag();
		public Property getProperty() {
			return Property.getInstance(getTag(),getTag());
		}
	}
	protected Mode mode;
	
	public Mode getMode() {
		return mode;
	}
	public void setMode(Mode mode) {
		this.mode = mode;
		key = mode.getProperty();
	}
	public SmilesReporter() {
		this(false,null);
	}
	public SmilesReporter(boolean writeProperties,Template template) {
		this(writeProperties,Mode.SMILES,template);
	}
	public SmilesReporter(boolean writeProperties,Mode mode,Template template) {
		super();
		setTemplate(template);
		setMode(mode);
		this.writeProperties = writeProperties;
		getProcessors().clear();
		
		switch (mode) {
		case SMILES: {
			getProcessors().add(new ProcessorStructureRetrieval(new QuerySmilesByID()));
			break;
			
		}
		case InChI: {
			if (getGroupProperties()==null) setGroupProperties(new Profile());
			getGroupProperties().add(mode.getProperty());
			getProcessors().add(new ProcessorStructureRetrieval(new RetrieveGroupedValuesByAlias(getGroupProperties())) {
				@Override
				public IStructureRecord process(IStructureRecord target)
						throws AmbitException {
					((RetrieveGroupedValuesByAlias)getQuery()).setRecord(target);
					return super.process(target);
				}
			});
			break;
		}
		}
		
		if ((template!=null) && getTemplate().size()>0) 
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
	public Object processItem(IStructureRecord item) throws AmbitException {
		try {
			Object smiles = item.getProperty(key);
			if (smiles == null)
				output.write("");
			else 
				output.write(smiles.toString());
			
			if (writeProperties && (item.getProperties() != null)) {
				for (Property key: item.getProperties()) {

				boolean ok = true;
				for (Mode m: mode.values())	
					if (key.getName().toLowerCase().equals(m.name().toString().toLowerCase())) { ok = false;break;}
					else if (key.getLabel().toLowerCase().equals(m.getTag().toString().toLowerCase())) { ok = false;break;}

				if (ok) {
					Object property = item.getProperty(key);
					String d = key.getName().indexOf(' ')>0?"\"":"";
					output.write(String.format(",%s%s%s",d,d,
						property==null?"":property.toString()
						));
				}
				}
			}
			output.write('\n');
			output.flush();
			
		} catch (IOException x) {
			x.printStackTrace();
		}
		return null;
		
	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
	public Template getTemplate() {
		return template;
	}
	public void setTemplate(Template template) {
		this.template = template;
	}
	public void footer(Writer output, Q query) {};
	public void header(Writer output, Q query) {};
}
