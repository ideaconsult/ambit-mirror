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
import ambit2.smarts.query.FastSmartsMatcher;

/**
 * Select structures by querying fungroups table by smarts
 * @author Nina Jeliazkova nina@acad.bg
 *
 */
public class QuerySMARTS extends AbstractStructureQuery<String,FunctionalGroup,BooleanCondition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4539445262832597492L;
	protected SmartsToChemObject smartsToChemObject = new SmartsToChemObject();
	protected StructureKeysBitSetGenerator skGenerator = null;
	protected FingerprintGenerator fpGenerator = new FingerprintGenerator();
	protected QueryPrescreenBitSet screening = new QueryPrescreenBitSet();
	//protected CMLUtilities util = new CMLUtilities();
	protected MoleculeReader reader = new MoleculeReader();
	protected AtomConfigurator configurator = new AtomConfigurator();
	protected Property smartsProperty = Property.getInstance(CMLUtilities.SMARTSProp, CMLUtilities.SMARTSProp);
	
	public QuerySMARTS() {
		setMaxRecords(1000);
	}
	public String getSQL() throws AmbitException {
		return screening.getSQL();
	}

	public List<QueryParam> getParameters() throws AmbitException {
		return screening.getParameters();
	}
	@Override
	public void setValue(FunctionalGroup value) {
		super.setValue(value);
		FastSmartsMatcher matcher = new FastSmartsMatcher();
		value.setQuery(matcher);
		//if (screen == null) screen = new StructureKeysBitSetGenerator();
		try {
			QueryAtomContainer container = matcher.getQuery();
			IAtomContainer atomContainer = smartsToChemObject.process(container);
			screening.setValue(fpGenerator.process(atomContainer));
		} catch (AmbitException x) {
			screening.setValue(null);
		}
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
				//empty or markush
				if ((mol==null) || (mol.getAtomCount()==0) || (mol instanceof SuppleAtomContainer)) return 0;
				
				if ("true".equals(Preferences.getProperty(Preferences.FASTSMARTS))) { 
	 				Object smartsdata = object.getProperty(smartsProperty);
					
					if (smartsdata!= null) {
						mol.setProperty(CMLUtilities.SMARTSProp, smartsdata);
	
					} else mol.removeProperty(CMLUtilities.SMARTSProp);
				} else mol.removeProperty(CMLUtilities.SMARTSProp);
				
				
				mol = configurator.process(mol);
				
				CDKHueckelAromaticityDetector.detectAromaticity(mol);
				VerboseDescriptorResult<String,IntegerResult> result = getValue().process(mol);
				return result.getResult().intValue();
			} else return 0;
		} catch (Exception x) {
			return -1;
		}
	}
	@Override
	public String toString() {
		if (getValue() != null) return getValue().toString();
		return "SMARTS";
	}
}
