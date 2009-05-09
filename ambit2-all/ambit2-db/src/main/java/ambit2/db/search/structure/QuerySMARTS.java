package ambit2.db.search.structure;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.result.IntegerResult;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.db.search.BooleanCondition;
import ambit2.db.search.QueryParam;
import ambit2.descriptors.FunctionalGroup;
import ambit2.descriptors.VerboseDescriptorResult;

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
	public final static String sqlField = 
//		"select ? as idquery,idchemical,-1,1 as selected,1 as metric from funcgroups join struc_fgroups as f using (idfuncgroup) where smarts = ?";
		"select ? as idquery,idchemical,idstructure,1 as selected,1 as metric from structure";	
	protected MoleculeReader reader = new MoleculeReader();
	protected AtomConfigurator configurator = new AtomConfigurator();
	public String getSQL() throws AmbitException {
		return sqlField;
	}

	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getId()));
		return params;
		/*
		params.add(new QueryParam<String>(String.class, getValue().getSmarts()));
		return params;
		*/
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
				IAtomContainer mol = configurator.process(reader.process(object));
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
