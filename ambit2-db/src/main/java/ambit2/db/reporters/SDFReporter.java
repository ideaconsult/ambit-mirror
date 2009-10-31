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
import ambit2.db.readers.RetrieveTemplateStructure;
import ambit2.db.readers.RetrieveProfileValues.SearchMode;

public class SDFReporter<Q extends IQueryRetrieval<IStructureRecord>> extends QueryReporter<IStructureRecord, Q, Writer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2931123688036795689L;
	protected Template template;
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
		setTemplate(template);
		getProcessors().clear();
		getProcessors().add(new ProcessorStructureRetrieval(new RetrieveStructure()));

		if (getTemplate().size()>0) 
			getProcessors().add(new ProcessorStructureRetrieval(new RetrieveProfileValues(SearchMode.idproperty,getTemplate(),true)) {
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
				processItem(target);
				return target;
			};
		});	
	}


	@Override
	public void processItem(IStructureRecord item) throws AmbitException {
		try {
			output.write(item.getContent());
			for (Property p : item.getProperties()) {
				Object value = item.getProperty(p);
				if (value != null)
					output.write(String.format("\n> <%s>\n%s\n",p.getName().toString(),
							value.toString()));
			}
			output.write("\n$$$$\n");
		} catch (Exception x) {
			logger.error(x);
		}
		
	}

	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
	public void footer(Writer output, Q query) {};
	public void header(Writer output, Q query) {};

}
