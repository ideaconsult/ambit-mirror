package ambit2.some;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
	public void parseRecord(String some) throws Exception {
		super.parseRecord(some);
		double[] size = new double[] {1.66,1.33,1.0};
		//assign ranks
		List<IAtom> atoms = new ArrayList<IAtom>();
		Comparator<IAtom> cmp = null;
		for (int i = someindex.aliphaticHydroxylation.ordinal() ; i <= someindex.SOxidation.ordinal(); i ++) {
			atoms.clear();
			for (IAtom atom : mol.atoms()) {
				Object value = atom.getProperty(someindex.values()[i].name());
				if (value !=null) {
					atoms.add(atom);
				}
			}
			if (atoms.size()>0) {
				 cmp = new SOMERank(someindex.values()[i]);
				 Collections.sort(atoms,cmp);
				 
				 int rank = 1;
				 Double prevValue = null;
				 for (int r=0; r < atoms.size();r++ ) {
					 Object value = atoms.get(r).getProperty(someindex.values()[i].name());
					 atoms.get(r).setProperty(SOMEShell.SOME_RESULT, r+1);
					 //atoms.get(r).setProperty(CompoundImageTools.ATOM_ANNOTATION,String.format("[%s]",someindex.values()[i].name()));
					 atoms.get(r).setProperty(CompoundImageTools.SELECTED_ATOM_SIZE,rank>3?0.66:size[rank-1]);
					 
					 if ((prevValue==null) || (prevValue<((Double)value))) rank++;
					 prevValue = (Double) value; 	 
					 
				 }
			}
					
		}

	}
	@Override
	protected void process(int atomNum, String atomSymbol, someindex index,
			double value, boolean star) {

		if (!star) return;
		if ((ruleid==null) || ruleid.equals(index.name())) {
			mol.getAtom(atomNum-1).setProperty(index.name(), value);
			mol.getAtom(atomNum-1).setProperty(CompoundImageTools.SELECTED_ATOM_COLOR,index.getColor(0.75));

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


class SOMERank implements Comparator<IAtom> {
	protected someindex index;
	public SOMERank(someindex index) {
		this.index = index;
	}
	@Override
	public int compare(IAtom o1, IAtom o2) {
		Object value1 = o1.getProperty(index.name());
		Object value2 = o2.getProperty(index.name());
		if ((value1==null) && (value2==null)) return 0;
		
		if (value1==null) 
			return - ((int) (100 * ((Double)value2).doubleValue()));
		if (value2==null) 
			return (int) (100 * ((Double)value1).doubleValue());
		
		return (((Double)value2).doubleValue()-((Double)value1).doubleValue())>0?1:-1;


	}
	
}