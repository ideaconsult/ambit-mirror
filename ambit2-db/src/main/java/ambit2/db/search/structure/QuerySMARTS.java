package ambit2.db.search.structure;

import java.util.List;

import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.qsar.result.IntegerResult;

import ambit2.base.config.Preferences;
import ambit2.base.data.Property;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.groups.SuppleAtomContainer;
import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.db.search.BooleanCondition;
import ambit2.db.search.QueryParam;
import ambit2.descriptors.FunctionalGroup;
import ambit2.descriptors.VerboseDescriptorResult;
import ambit2.smarts.CMLUtilities;
import ambit2.smarts.SmartsToChemObject;
import ambit2.smarts.processors.StructureKeysBitSetGenerator;
import ambit2.smarts.query.SmartsPatternAmbit;

/**
 * Select structures by querying fungroups table by smarts
 * 
 * @author Nina Jeliazkova nina@acad.bg
 * 
 */
public class QuerySMARTS extends
		AbstractStructureQuery<String, FunctionalGroup, BooleanCondition> {
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -4539445262832597492L;
	protected SmartsToChemObject smartsToChemObject = new SmartsToChemObject();
	protected transient StructureKeysBitSetGenerator skGenerator = new StructureKeysBitSetGenerator();
	protected transient FingerprintGenerator fpGenerator = new FingerprintGenerator();
	protected QueryPrescreenBitSet screening;
	protected transient MoleculeReader reader = new MoleculeReader();
	protected transient AtomConfigurator configurator = new AtomConfigurator();
	protected Property smartsProperty = Property.getInstance(
			CMLUtilities.SMARTSProp, CMLUtilities.SMARTSProp);
	protected static final String SMARTS = "SMARTS";
	
	protected boolean fp1024_screening = true;
	protected boolean sk1024_screening = true;
	protected boolean usePrecalculatedAtomProperties = true;
	
	public boolean isFp1024_screening() {
		return fp1024_screening;
	}

	public void setFp1024_screening(boolean fp1024_screening) {
		this.fp1024_screening = fp1024_screening;
	}

	public boolean isSk1024_screening() {
		return sk1024_screening;
	}

	public void setSk1024_screening(boolean sk1024_screening) {
		this.sk1024_screening = sk1024_screening;
	}

	public boolean isUsePrecalculatedAtomProperties() {
		return usePrecalculatedAtomProperties;
	}

	public void setUsePrecalculatedAtomProperties(
			boolean usePrecalculatedAtomProperties) {
		this.usePrecalculatedAtomProperties = usePrecalculatedAtomProperties;
	}

	
	public QuerySMARTS() {
		super();
		screening = new QueryPrescreenBitSet();
		setChemicalsOnly(false);
		setValue(null);
		setFieldname(null);
	}

	public String getSQL() throws AmbitException {
		prepareScreening();
		return screening.getSQL();
	}

	@Override
	public void setChemicalsOnly(boolean chemicalsOnly) {
		super.setChemicalsOnly(chemicalsOnly);
		screening.setChemicalsOnly(chemicalsOnly);
	}

	@Override
	public void setId(Integer id) {
		super.setId(id);
		screening.setId(id);
	}

	public List<QueryParam> getParameters() throws AmbitException {
		prepareScreening();
		return screening.getParameters();
	}
	
	public void prepareScreening() throws AmbitException {
		try {
			if (((screening.getValue()==null) && isFp1024_screening()) || 
				((screening.getFieldname()==null)) && isSk1024_screening()) {
			//if ((screening.getValue()==null) || (screening.getFieldname()==null)) {
				screening.setPageSize(0);
				screening.setPage(0);
				SmartsPatternAmbit matcher = new SmartsPatternAmbit();
				matcher.setUseCDKIsomorphism(false);
				value.setQuery(matcher);
				QueryAtomContainer container = matcher.getQuery();
				IAtomContainer atomContainer = smartsToChemObject.process(container);
				try {
					AtomConfigurator cfg = new AtomConfigurator();
					cfg.process(atomContainer);
				} catch (Exception x) { x.printStackTrace();}
				try { CDKHueckelAromaticityDetector.detectAromaticity(atomContainer); } catch (Exception x) { x.printStackTrace();}

				if ((atomContainer == null) || (atomContainer.getAtomCount()==0)) {
					screening.setValue(null);
					screening.setFieldname(null);
				} else {
					try {
						screening.setValue(fp1024_screening?fpGenerator.process(atomContainer):null);
					} catch (Exception x) {
						screening.setValue(null);
					}		
					try {
						screening.setFieldname(sk1024_screening?skGenerator.process(atomContainer):null);
					} catch (Exception x) {
						screening.setFieldname(null);
					}						
				}

			}
		} catch (AmbitException x) {
			throw x;
			//screening.setValue(null);
			//screening.setFieldname(null);
		}		
	}
	@Override
	public void setValue(FunctionalGroup value) {
		super.setValue(value);
		screening.setValue(null);
		screening.setFieldname(null);
	}

	@Override
	public String getKey() {
		return super.getValue().getSmarts();
	}
	@Override
	public String getCategory() {
		return SMARTS;
	}
	@Override
	public boolean isPrescreen() {
		return true;
	}

	@Override
	public double calculateMetric(IStructureRecord object) {
		try {
			if (getValue() != null) {
				getValue().setVerboseMatch(false);
				IAtomContainer mol = reader.process(object);
				// empty or markush
				if ((mol == null) || (mol.getAtomCount() == 0)
						|| (mol instanceof SuppleAtomContainer))
					return 0;

				if ("true".equals(Preferences.getProperty(Preferences.FASTSMARTS)) && isUsePrecalculatedAtomProperties()) {
					Object smartsdata = object.getProperty(smartsProperty);

					if (smartsdata != null) {
						mol.setProperty(smartsProperty, smartsdata);

					} else {
						mol.removeProperty(smartsProperty);
						mol = configurator.process(mol);
						CDKHueckelAromaticityDetector.detectAromaticity(mol);

					}
				} else {
					mol.removeProperty(smartsProperty);
					mol = configurator.process(mol);
					CDKHueckelAromaticityDetector.detectAromaticity(mol);

				}
				int aromatic = 0;
				VerboseDescriptorResult<String, IntegerResult> result = getValue()
						.process(mol);
				return result.getResult().intValue();
			} else
				return 0;
		} catch (Exception x) {
			return -1;
		}

	}

	@Override
	public String toString() {
		return String.format("SMARTS %s %s %s %s", 
				getValue() == null ? "" : getValue().toString(),
				isFp1024_screening()?"":",No fp screening",
				isSk1024_screening()?"":",No sk screening",
				isUsePrecalculatedAtomProperties()?"":",No precalculated properties"
				);
	}
}
