package ambit2.db.reporters;

import java.io.IOException;
import java.io.Writer;

import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.core.config.AmbitCONSTANTS;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveGroupedValuesByAlias;
import ambit2.db.search.QuerySmilesByID;

public class SmilesReporter<Q extends IQueryRetrieval<IStructureRecord>> extends QueryStructureReporter<Q, Writer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3648376868814044783L;
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
		this(false);
	}
	public SmilesReporter(boolean writeProperties) {
		this(writeProperties,Mode.SMILES);
	}
	public SmilesReporter(boolean writeProperties,Mode mode) {
		super();
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

				if (key.getName().equals(mode.getTag())) continue;
					Object property = item.getProperty(key);
					String d = key.getName().indexOf(' ')>0?"\"":"";
					output.write(String.format("\t%s%s%s=%s",d,key.toString(),d,
							property==null?"":property.toString()
							));

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

	public void footer(Writer output, Q query) {};
	public void header(Writer output, Q query) {};
}
