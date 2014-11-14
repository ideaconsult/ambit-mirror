package ambit2.db.reporters;

import java.io.StringWriter;
import java.io.Writer;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.SDFWriter;

import ambit2.base.data.ISourceDataset;
import ambit2.base.data.Profile;
import ambit2.base.data.Property;
import ambit2.base.data.Template;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.db.processors.ProcessorStructureRetrieval;
import ambit2.db.readers.RetrieveGroupedValuesByAlias;
import ambit2.db.readers.RetrieveProfileValues;
import ambit2.db.readers.RetrieveProfileValues.SearchMode;
import ambit2.db.readers.RetrieveStructure;
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
	protected boolean first = true;
	private static final String ux = "(?<!\r)\n";
	private static final String wlinesep = "\r\n";
	private static final String sdfrecord = "$$$$";
	protected boolean changeLineSeparators = false;
	
	public boolean isChangeLineSeparators() {
		return changeLineSeparators;
	}
	public void setChangeLineSeparators(boolean changeLineSeparators) {
		this.changeLineSeparators = changeLineSeparators;
	}

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
		this(new Template(null),null,false);
	}
	public SDFReporter(Template template,Profile groupedProperties,boolean changeLineSeparator) {
		this(template,groupedProperties,false,changeLineSeparator);
	}
	public SDFReporter(Template template,Profile groupedProperties,boolean molOnly,boolean changeLineSeparator) {
		setTemplate(template);
		setGroupProperties(groupedProperties);
		setChangeLineSeparators(changeLineSeparator);
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
		"\r\n  EMPTY     1224111917\r\n"+
		"\r\n"+  
		"  0  0  0  0  0  0  0  0  0  0999 V2000\r\n"+
		"M  END";
	
	@Override
	public Object processItem(IStructureRecord item) throws AmbitException {
		try {
			//some software doesn't like $$$$\n at the end of the file, so writing the \n at the beginning of a record
			if (!first) output.write(wlinesep);
			first = false;
			String content = null;
			try {
				content = getSDFContent(item);
			} catch (Exception x) {
				content = null;
				logger.log(java.util.logging.Level.SEVERE,
						String.format("Error reading structure at /compound/%d/structure/%d : %s",
									item.getIdchemical(),item.getIdstructure(),x.getMessage()));
			}
			if (content==null) return null;
			int pi = content.indexOf(sdfrecord);
			content = pi>0?content.substring(0,pi-1):content;
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
						output.write(String.format("\r\n> <%s>\r\n%s\r\n",p.getName().toString(),
								value.toString()));
				}
			output.write(wlinesep);
			output.write(sdfrecord);
			
		} catch (Exception x) {
			logger.log(java.util.logging.Level.SEVERE,x.getMessage(),x);
		}
		return null;
	}

	protected String getSDFContent(IStructureRecord item) throws AmbitException {
		//most common case
		if (MOL_TYPE.SDF.toString().equals(item.getFormat())) {
			if (isChangeLineSeparators())
				return item.getContent().replaceAll(ux,wlinesep);
			else	
				return item.getContent();
		}	
		//otherwise
		if (reader==null) reader = new MoleculeReader();
		try {
			StringWriter w = new StringWriter();
			SDFWriter sdfwriter = new SDFWriter(w); 
			IAtomContainer ac = reader.process(item);
			ac.getProperties().clear();
			sdfwriter.write(ac);
			sdfwriter.close();
			if (isChangeLineSeparators())
				return w.toString().replaceAll(ux,wlinesep);
			else	
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
