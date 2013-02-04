package ambit2.db.reporters;

import java.io.StringWriter;
import java.io.Writer;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.io.SDFWriter;
import org.openscience.cdk.renderer.color.IAtomColorer;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.base.processors.DefaultAmbitProcessor;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.db.exceptions.DbAmbitException;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.IQueryRetrieval;
import ambit2.db.readers.RetrieveGroupedValuesByAlias;
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
	//in case the native format is not SDF
	protected MoleculeReader reader = null;
	
	protected Profile groupProperties;
	public Profile getGroupProperties() {
		return groupProperties;
	}
	public void setGroupProperties(Profile gp) {
		this.groupProperties = gp;
	}	
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
		this(new Template(null),null);
	}
	public SDFReporter(Template template,Profile groupedProperties) {
		this(template,groupedProperties,false);
	}
	public SDFReporter(Template template,Profile groupedProperties,boolean molOnly) {
		setTemplate(template);
		setGroupProperties(groupedProperties);
		setMOLONLY(molOnly);
		getProcessors().clear();
		RetrieveStructure r = new RetrieveStructure();
		r.setPage(0);
		r.setPageSize(1);
		getProcessors().add(new ProcessorStructureRetrieval(r));		

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

	private final static String emptySDF = 
		"\n  EMPTY     1224111917\n"+
		"\n"+  
		"  0  0  0  0  0  0  0  0  0  0999 V2000\n"+
		"M  END";
	
	@Override
	public Object processItem(IStructureRecord item) throws AmbitException {
		try {
			String content = getSDFContent(item);
			int pi = content.indexOf("$$$$");
			content = pi>=0?content.substring(0,pi-1):content;
			if ("".equals(content.trim())) content = emptySDF;

			String licenseURI = getLicenseURI();
			
			if ((licenseURI!=null) && !ISourceDataset.license.Unknown.equals(licenseURI)) {
				int n = content.indexOf('\n');
				int r = content.indexOf('\r');
				int index = -1;
				if ((n>=0) && (r>=0)) index = n<r?n:r;
				else if (n>=0) index = n;
				else if (r>=0) index = r;
				content = String.format("%s%s",licenseURI,content.substring(index));

			}
			output.write(content);
			if (!isMOLONLY())
				for (Property p : item.getProperties()) {
					if (CMLUtilities.SMARTSProp.equals(p.getName())) continue;
					Object value = item.getProperty(p);
					if (value != null)
						output.write(String.format("\n> <%s>\n%s\n",p.getName().toString(),
								value.toString()));
				}
			output.write("\n$$$$\n");
			
		} catch (Exception x) {
			logger.log(java.util.logging.Level.SEVERE,x.getMessage(),x);
		}
		return null;
	}

	protected String getSDFContent(IStructureRecord item) throws AmbitException {
		//most common case
		if (MOL_TYPE.SDF.toString().equals(item.getFormat())) return item.getContent();
		//otherwise
		if (reader==null) reader = new MoleculeReader();
		try {
			StringWriter w = new StringWriter();
			SDFWriter sdfwriter = new SDFWriter(w); 
			IAtomContainer ac = reader.process(item);
			ac.getProperties().clear();
			sdfwriter.write(ac);
			sdfwriter.close();
			return w.toString();
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	public void open() throws DbAmbitException {
		// TODO Auto-generated method stub
		
	}
	public void footer(Writer output, Q query) {};
	public void header(Writer output, Q query) {};

	@Override
	public String getFileExtension() {
		return MOLONLY?"mol":"sdf";
	}
}
