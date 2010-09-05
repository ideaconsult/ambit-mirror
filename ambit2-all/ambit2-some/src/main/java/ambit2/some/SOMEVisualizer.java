package ambit2.some;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

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
    	CompoundImageTools tools = new CompoundImageTools(new Dimension(width,height));
    	return tools.getImage(mol, this, true, atomnumbers);
	}

	@Override
	public BufferedImage getImage(IAtomContainer mol) throws AmbitException {
		return  getImage(mol,null,150,150,false);
	}
	

	@Override
	protected void process(int atomNum, String atomSymbol, someindex index,
			double value, boolean star) {
		if ((ruleid==null) || ruleid.equals(index.toString()))
			selected.addAtom(mol.getAtom(atomNum));
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
		Object some = mol.getProperty(SOMEShell.SOME_RESULT);
		if (some == null) return null;
		try {
			parseRecord(some.toString());
			if (selected.getAtomCount()==0) return null;
			else return new SingleSelection<IAtomContainer>(selected);
		} catch (AmbitException x) {
			throw x;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	@Override
	public void setEnabled(boolean value) {
		
	}

}
