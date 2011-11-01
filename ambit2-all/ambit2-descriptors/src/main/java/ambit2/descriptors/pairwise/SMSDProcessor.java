package ambit2.descriptors.pairwise;

import java.util.Map;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smsd.Isomorphism;
import org.openscience.cdk.smsd.interfaces.Algorithm;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.core.data.MoleculeTools;
import ambit2.core.processors.structure.MoleculePairProcessor;

public class SMSDProcessor extends MoleculePairProcessor {
	protected SmilesGenerator smigen = new SmilesGenerator();
	protected LiteratureEntry prediction;
	protected Property property = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = -5786249572631916554L;


	public SMSDProcessor(LiteratureEntry prediction) {
		super();
		this.prediction = prediction;
		smigen.setUseAromaticityFlag(true);

	}
	@Override
	public IStructureRecord[] process(IStructureRecord[] target,
			IAtomContainer[] molecules) {
       // A1 = (IMolecule) AtomContainerManipulator.removeHydrogens(A1);
        //A2 = (IMolecule) AtomContainerManipulator.removeHydrogens(A2);
		try {
	        boolean bondSensitive = true;
	        boolean removeHydrogen = true;
	        boolean stereoMatch = true;
	        boolean fragmentMinimization = true;
	        boolean energyMinimization = true;
	        
	        Isomorphism comparison = new Isomorphism(Algorithm.DEFAULT, bondSensitive);
	        comparison.init(molecules[0],molecules[1], removeHydrogen,true);
	        comparison.setChemFilters(stereoMatch, fragmentMinimization, energyMinimization);
	        
	        //IAtomContainer Query = comparison.getReactantMolecule();
	        //IAtomContainer Target = comparison.getProductMolecule();
	
	        //comparison.getTanimotoSimilarity();
	        
	        IAtomContainer mcss = MoleculeTools.newAtomContainer(SilentChemObjectBuilder.getInstance());
	         try {
	        	 Map<IBond,IBond> mapping = comparison.getFirstBondMap();
	        	 if (mapping != null) {
	                 for (Map.Entry<IBond,IBond> mappings : mapping.entrySet()) {
	                     //Get the mapped atom number in Query Molecule
	                     IBond bond = mappings.getKey();
	                     mcss.addBond(bond);
	                     for (int b=0;b< bond.getAtomCount(); b++)
	                    	 mcss.addAtom(bond.getAtom(b));
	                     
	                 }
	                 //TODO perhaps replace by InChI, or make sure SMILES are unique
	                 String smiles= smigen.createSMILES(mcss);
	                 if ((smiles!=null) && !"".equals(smiles)) {
	                	 if ((property==null) || !property.getName().equals(smiles)) {
	                		 property = new Property(smiles,prediction);
	                		 property.setLabel(String.format("http://opentox.org/api/1.2/substructures.owl#%s",smiles));
	                		 property.setNominal(true);
	                		 property.setClazz(Number.class);
	                	 }
		                 target[0].setProperty(property,1);
		                 target[1].setProperty(property,1);
	                 }
	        	 }
             } catch (Exception ex) {
                 ex.printStackTrace();
             }

		} catch (Exception x) {
			//TODO
		}
		
		return target;
	}

}
