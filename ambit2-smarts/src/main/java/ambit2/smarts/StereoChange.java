package ambit2.smarts;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;

public class StereoChange 
{
	public List<IAtom> addLigands1 = new ArrayList<IAtom>(); 
	public List<IAtom> addLigands2 = new ArrayList<IAtom>(); 
	public boolean ligand1Deleted = false;
	public boolean ligand2Deleted = false;
}
