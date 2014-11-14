package ambit2.core.processors;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;

import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.layout.StructureDiagramGenerator;

import ambit2.core.data.MoleculeTools;

public class Structure2DBuilder extends DefaultAmbitProcessor<IAtomContainer, IAtomContainer>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -711201344724786569L;
	protected   StructureDiagramGenerator sdg = new StructureDiagramGenerator();
	@Override
	public IAtomContainer process(IAtomContainer molecule) throws AmbitException {
			if (molecule==null) return null;
			if (molecule.getAtomCount()==1) {
				molecule.getAtom(0).setPoint2d(new Point2d(new double[]{0.0001,0.0001}));
				return molecule;
			} else {			
        	IMoleculeSet molecules =  ConnectivityChecker.partitionIntoMolecules(molecule);
        	IAtomContainer newmol = MoleculeTools.newAtomContainer(molecules.getBuilder());
	        for (int i=0; i < molecules.getAtomContainerCount();i++) {
	        	IAtomContainer c = molecules.getAtomContainer(i);                	
	            sdg.setMolecule((IMolecule)c,false);
	            try {
	            	sdg.generateCoordinates(new Vector2d(0,1));
	            	newmol.add(sdg.getMolecule());
	            } catch (Exception x) {
	            	for (IAtom atom : c.atoms()) atom.setPoint2d(new Point2d(new double[]{0.0001,0.0001}));
	            	newmol.add(c);
	            }
	        }
	        return newmol;
	    }
	}

	
}
