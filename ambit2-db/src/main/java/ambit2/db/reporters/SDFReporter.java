package ambit2.db.reporters;

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
import ambit2.db.readers.RetrieveStructure;
import ambit2.db.readers.RetrieveProfileValues.SearchMode;
import ambit2.smarts.CMLUtilities;

public class SDFReporter<Q extends IQueryRetrieval<IStructureRecord>> extends QueryStructureReporter<Q, Writer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2931123688036795689L;
	protected Template template;
	protected boolean MOLONLY = false;
	public boolean isMOLONLY() {
		return MOLONLY;
	}
	public void setMOLONLY(boolean molonly) {
		MOLONLY = molonly;
	}
	public Template getTemplate() {
		return template;
	}
	public void setTemplate(Template template) {
		this.template = template;
	}
	public SDFReporter() {
		this(new Template(null));
	}
	public SDFReporter(Template template) {
		this(template,false);
	}
	public SDFReporter(Template template,boolean molOnly) {
		setTemplate(template);
		setMOLONLY(molOnly);
		getProcessors().clear();
		RetrieveStructure r = new RetrieveStructure();
		r.setMaxRecords(1);
		getProcessors().add(new ProcessorStructureRetrieval(r));		

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
	public Object processItem(IStructureRecord item) throws AmbitException {
		try {
			output.write(item.getContent());
			if (isMOLONLY()) return null;
			for (Property p : item.getProperties()) {
				if (CMLUtilities.SMARTSProp.equals(p.getName())) continue;
				Object value = item.getProperty(p);
				if (value != null)
					output.write(String.format("\n> <%s>\n%s\n",p.getName().toString(),
							value.toString()));
			}
			output.write("\n$$$$\n");
		} catch (Exception x) {
			logger.error(x);
		}
		return null;
	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
	public void footer(Writer output, Q query) {};
	public void header(Writer output, Q query) {};

}
