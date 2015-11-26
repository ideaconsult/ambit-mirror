/* QueryExactStructureBitSet.java
 * Author: nina
 * Date: Apr 10, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.db.search.structure;

import java.util.List;
import java.util.logging.Level;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;

import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.base.config.Preferences;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.groups.SuppleAtomContainer;
import ambit2.core.helper.CDKHueckelAromaticityDetector;
import ambit2.core.processors.structure.AtomConfigurator;
import ambit2.core.processors.structure.FingerprintGenerator;
import ambit2.core.processors.structure.MoleculeReader;
import ambit2.db.search.BooleanCondition;
import ambit2.db.search.NumberCondition;
import ambit2.smarts.CMLUtilities;
import ambit2.smarts.processors.SMARTSPropertiesReader;
import ambit2.smarts.processors.StructureKeysBitSetGenerator;


/**
 * This is a hack to cope with the failure of SmilesGenerator to produce canonical SMILES.
 * @author nina
 * @deprecated  Convert to InChI and use {@link QueryStructure}
 */
public class QueryExactStructure extends AbstractStructureQuery<String, IAtomContainer, BooleanCondition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4140784963337312377L;
	
	/**
	 * 
	 */
	protected transient StructureKeysBitSetGenerator skGenerator;
	protected transient FingerprintGenerator fpGenerator ;
	protected QueryPrescreenBitSet screening;
	protected transient MoleculeReader reader;
	protected transient AtomConfigurator configurator;
	protected transient SMARTSPropertiesReader bondPropertiesReader;
	protected Property smartsProperty = Property.getInstance(CMLUtilities.SMARTSProp, CMLUtilities.SMARTSProp);
	protected static final String EXACT = "EXACT";
	protected transient UniversalIsomorphismTester uit;
	
	public QueryExactStructure() throws Exception {
		super();
		screening = new QueryPrescreenBitSet();
		screening.setCondition(NumberCondition.getInstance("="));
		setChemicalsOnly(false);
		setValue(null);
		setFieldname(null);
		uit = new UniversalIsomorphismTester();
		reader = new MoleculeReader();
		configurator = new AtomConfigurator();
		bondPropertiesReader = new SMARTSPropertiesReader();
		fpGenerator = new FingerprintGenerator(new Fingerprinter());
		skGenerator = new StructureKeysBitSetGenerator();
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
			if ((screening.getValue()==null) || (screening.getFieldname()==null)) {
				screening.setPage(0);
				screening.setPageSize(0);
				IAtomContainer atomContainer = getValue();
				if (fpGenerator==null) fpGenerator = new FingerprintGenerator(new Fingerprinter());
				if (skGenerator == null) skGenerator = new StructureKeysBitSetGenerator();
				screening.setValue(fpGenerator.process(atomContainer));
				screening.setFieldname(skGenerator.process(atomContainer));
			}
		} catch (AmbitException x) {
			throw x;
		} catch (Exception x) {
			throw new AmbitException();
			//screening.setValue(null);
			//screening.setFieldname(null);
		}		
	}
	@Override
	public void setValue(IAtomContainer value) {
		
		try {
			if (value != null) {
				if (configurator==null) configurator = new AtomConfigurator();
				value = configurator.process(value);
				CDKHueckelAromaticityDetector.detectAromaticity(value);	

	            if (value.getBondCount()>1)
	            	value = AtomContainerManipulator.removeHydrogensPreserveMultiplyBonded(value);
			}
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
		} finally {
			super.setValue(value);
		}

		
		
		screening.setValue(null);
		screening.setFieldname(null);
	}

	@Override
	public String getKey() {
		return null;
	}
	@Override
	public String getCategory() {
		return EXACT;
	}
	@Override
	public boolean isPrescreen() {
		return true;
	}

	@Override
	public double calculateMetric(IStructureRecord object) {
		try {
			if (getValue() != null) {
				if (reader ==null) reader = new MoleculeReader();
				IAtomContainer mol = reader.process(object);
				// empty or markush
				if ((mol == null) || (mol.getAtomCount() == 0)
						|| (mol instanceof SuppleAtomContainer))
					return 0;

				if ("true".equals(Preferences
						.getProperty(Preferences.FASTSMARTS))) {
					Object smartsdata = object.getRecordProperty(smartsProperty);

					if (smartsdata != null) {
						mol.setProperty(smartsProperty, smartsdata);
						if (bondPropertiesReader == null) bondPropertiesReader = new SMARTSPropertiesReader();
						mol = bondPropertiesReader.process(mol);
					} else {
						mol.removeProperty(smartsProperty);
						if (configurator==null) configurator = new AtomConfigurator();
						mol = configurator.process(mol);
						
						CDKHueckelAromaticityDetector.detectAromaticity(mol);

					}
				} else {
					mol.removeProperty(smartsProperty);
					if (configurator==null) configurator = new AtomConfigurator();
					mol = configurator.process(mol);
					CDKHueckelAromaticityDetector.detectAromaticity(mol);

				}
				try {
	                if (((IAtomContainer) mol).getBondCount()>1)
	                	mol = AtomContainerManipulator.removeHydrogensPreserveMultiplyBonded(mol);
	                
				} catch (Exception x) {
					
				}
				if (uit == null) uit = new UniversalIsomorphismTester();
				int ok = (getValue().getAtomCount()!=mol.getAtomCount())?0:
						 (getValue().getBondCount()!=mol.getBondCount())?0:
							uit.isIsomorph(getValue(),mol)?1:0;
				return ok;

			} else
				return 0;
		} catch (Exception x) {
			logger.log(Level.WARNING,x.getMessage(),x);
			return -1;
		}

	}

	@Override
	public String toString() {
		return EXACT;
	}
}
