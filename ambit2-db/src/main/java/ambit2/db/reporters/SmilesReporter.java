package ambit2.db.reporters;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.config.AmbitCONSTANTS;
import ambit2.db.processors.ProcessorStructureRetrieval;
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
			@Override
			public String getFileExtension() {
				return "smi";
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
			@Override
			public String getFileExtension() {
				return "inchi";
			}
		};
		public abstract String getTag();
		public abstract String getFileExtension();
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
			Template inchis = new Template();
			inchis.add(Property.getInChIInstance());
			inchis.add(Property.getInChIStdInstance());
			getProcessors().add(new ProcessorStructureRetrieval(new RetrieveProfileValues(SearchMode.alias,inchis,false)) {
				@Override
				public IStructureRecord process(IStructureRecord target)
						throws AmbitException {
					((RetrieveProfileValues)getQuery()).setRecord(target);
					
					//((RetrieveProfileValues)getQuery()).setChemicalsOnly(target.usePreferedStructure() || target.getIdstructure()<=0);
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
			Object smiles = null;
			switch (mode) {
			case InChI : {
				for (Property p : item.getProperties()) 
					if (p.getLabel().equals(Property.opentox_InChI_std)) {
						smiles = item.getProperty(p);
						break;
					} else if (p.getLabel().equals(Property.opentox_InChI)) {
						smiles = item.getProperty(p);
						break;
					}
				break;
			}
			default: {
				smiles = item.getProperty(key);
			}
			}
			if (smiles == null)
				output.write("");
			else 
				output.write(smiles.toString());
			
			if (writeProperties && (item.getProperties() != null)) {
				for (Property key: item.getProperties()) {

				boolean ok = true;
				for (Mode m: Mode.values())	
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
			logger.log(Level.WARNING,x.getMessage(),x);
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
	@Override
	public String getFileExtension() {
		return mode.getFileExtension();
	}
}
