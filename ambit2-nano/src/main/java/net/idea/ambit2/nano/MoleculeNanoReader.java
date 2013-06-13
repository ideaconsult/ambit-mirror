package net.idea.ambit2.nano;

import org.bitbucket.nanojava.data.Nanomaterial;
import org.bitbucket.nanojava.io.Deserializer;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.interfaces.IStructureRecord.MOL_TYPE;
import ambit2.core.data.MoleculeTools;
import ambit2.core.processors.structure.MoleculeReader;


public class MoleculeNanoReader extends MoleculeReader {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6188755441611061645L;

	@Override
	protected IAtomContainer handleFormat(MOL_TYPE format,IStructureRecord record) throws AmbitException{
		switch(format) {
		case NANO: try {
			return nm2atomcontainer(record);
		   } catch (Exception x) {
			   throw new AmbitException(x);
		   }
		default: return super.handleFormat(format,record);   
		}
	}
	
	public static IAtomContainer nm2atomcontainer(IStructureRecord record) throws Exception {
		   IAtomContainer ac = MoleculeTools.newAtomContainer(SilentChemObjectBuilder.getInstance());
		   Nanomaterial nanomaterial = Deserializer.fromCMLString(record.getContent()); 
		   if (nanomaterial.getChemicalComposition()!=null) {
			   IMolecularFormula formula = nanomaterial.getChemicalComposition();
			   record.setFormula(MolecularFormulaManipulator.getHillString(formula));
			   for (IIsotope isotope : formula.isotopes()) {
				   MoleculeTools.newAtom(ac.getBuilder(),isotope);
			   }
			   ac.setProperty("ChemicalComposition",record.getFormula());
		   }
		   ac.setProperty("MaterialType",nanomaterial.getType());
		   return ac;
	}
	
}
