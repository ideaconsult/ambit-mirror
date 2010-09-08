package ambit2.some;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.jchempaint.renderer.selection.IChemObjectSelection;
import org.openscience.jchempaint.renderer.selection.SingleSelection;

import ambit2.base.exceptions.AmbitException;
import ambit2.base.interfaces.IProcessor;
import ambit2.core.data.IStructureDiagramHighlights;
import ambit2.core.data.MoleculeTools;
import ambit2.jchempaint.CompoundImageTools;
import ambit2.some.SOMERawReader.someindex;

public class SOMEVisualizer extends SOMEResultsParser implements IStructureDiagramHighlights, IProcessor<IAtomContainer, IChemObjectSelection>  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2863301348491350336L;
	protected IAtomContainer selected;
	protected Dimension imageSize = new Dimension(150,150);
	protected String ruleid = null;
	protected IAtomContainer mol;
	
	public SOMEVisualizer() {
		super();
		selected = MoleculeTools.newAtomContainer(NoNotificationChemObjectBuilder.getInstance());
	}
	@Override
	public BufferedImage getImage(IAtomContainer mol, String ruleID, int width,
			int height, boolean atomnumbers) throws AmbitException {
		this.ruleid = ruleID;
		this.mol = mol;
		Object some = mol.getProperty(SOMEShell.SOME_RESULT);
		if (some != null) 
			try {parseRecord(some.toString());} catch (Exception x) {x.printStackTrace();}
    	CompoundImageTools tools = new CompoundImageTools(new Dimension(width,height));
    	return tools.getImage(mol, this, true, atomnumbers);
	}

	@Override
	public BufferedImage getImage(IAtomContainer mol) throws AmbitException {
		return  getImage(mol,null,150,150,false);
	}
	/*
	@Override
	protected void process(int atomNum, String atomSymbol, someindex index,
			double value, boolean star) {
		double[] size = new double[] {1.0,1.33,1.66};
		
		mol.getAtom(atomNum-1).setProperty(SOMEShell.SOME_RESULT, value);
		mol.getAtom(atomNum-1).setProperty(CompoundImageTools.SELECTED_ATOM_COLOR,index.getColor(0.75));
		mol.getAtom(atomNum-1).setProperty(CompoundImageTools.SELECTED_ATOM_SIZE,size[atomNum % 3]);
		mol.getAtom(atomNum-1).setProperty(CompoundImageTools.ATOM_ANNOTATION,String.format("%d",index.ordinal()-1));
		
			//selected.addAtom(mol.getAtom(atomNum));
	}
	*/

	@Override
	protected void process(int atomNum, String atomSymbol, someindex index,
			double value, boolean star) {

		if (!star) return;
		if ((ruleid==null) || ruleid.equals(index.name())) {
			mol.getAtom(atomNum-1).setProperty(SOMEShell.SOME_RESULT, value);
			mol.getAtom(atomNum-1).setProperty(CompoundImageTools.SELECTED_ATOM_SIZE,1+value);
			mol.getAtom(atomNum-1).setProperty(CompoundImageTools.SELECTED_ATOM_COLOR,index.getColor(0.75));
			mol.getAtom(atomNum-1).setProperty(CompoundImageTools.ATOM_ANNOTATION,
					String.format("(%d)",index.ordinal()-1));
		}
			
	}
	
    public Dimension getImageSize() {
		return imageSize;
	}
	public void setImageSize(Dimension imageSize) {
		this.imageSize = imageSize;
	}
	@Override
	public long getID() {
		return 0;
	}
	@Override
	public boolean isEnabled() {
		return true;
	}
	@Override
	public IChemObjectSelection process(IAtomContainer mol)
			throws AmbitException {

		try {
			for (IAtom atom: mol.atoms()) {
				if (atom.getProperty(SOMEShell.SOME_RESULT)!=null)
					selected.addAtom(atom);
			}
			if (selected.getAtomCount()==0) return null;
			else return new SingleSelection<IAtomContainer>(selected);

		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	@Override
	public void setEnabled(boolean value) {
		
	}

}
