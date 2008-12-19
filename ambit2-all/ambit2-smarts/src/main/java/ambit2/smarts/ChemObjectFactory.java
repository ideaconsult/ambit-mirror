package ambit2.smarts;

import java.util.HashMap;
import java.util.Vector;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;


/** Utilities for generation of different types of 
 * chemical objects: molecules, fragments, queries */
public class ChemObjectFactory 
{
	
	Vector<SequenceElement> sequence = new Vector<SequenceElement>();
	Vector<IAtom> sequencedAtoms = new Vector<IAtom>();
	Vector<IAtom> sequencedBondAt1 = new Vector<IAtom>();
	Vector<IAtom> sequencedBondAt2 = new Vector<IAtom>();
	
	public void setAtomSequence(IAtomContainer target, IAtom startAtom)
	{	
		//This function is implemented analogously to IsomorphismTester.setQueryAtomSequence
		
		IAtom firstAtom;
		SequenceElement seqEl;
		TopLayer topLayer;
		int n;
		
		if (startAtom == null)
			firstAtom = target.getFirstAtom();
		else
			firstAtom = startAtom;
		
		sequence.clear();
		sequencedAtoms.clear();
		sequencedBondAt1.clear();
		sequencedBondAt2.clear();
		
		//Setting the first sequence atom
		sequencedAtoms.add(firstAtom);		
		seqEl = new SequenceElement();
		seqEl.center = firstAtom;
		topLayer = (TopLayer)firstAtom.getProperty(TopLayer.TLProp);
		n = topLayer.atoms.size();
		seqEl.atoms = new IAtom[n];
		seqEl.bonds = new IBond[n];
		for (int i = 0; i < n; i++)
		{
			sequencedAtoms.add(topLayer.atoms.get(i));
			seqEl.atoms[i] = topLayer.atoms.get(i);
			seqEl.bonds[i] = topLayer.bonds.get(i);
			addSeqBond(seqEl.center,seqEl.atoms[i]);
		}
		sequence.add(seqEl);
		
		
		//Sequencing the entire query structure
		//TODO
	}
	
	void addSeqBond(IAtom at1, IAtom at2)
	{
		sequencedBondAt1.add(at1);
		sequencedBondAt2.add(at2);
	}
	
	
	//This function generates a Carbon skeleton from a query atom sequence
	//It is used mainly for testing purposes
	public static IAtomContainer getCarbonSkelleton(Vector<QuerySequenceElement> sequence)
	{
		AtomContainer mol = new AtomContainer();
		Vector<IAtom> v = new Vector<IAtom>();
		HashMap<IAtom,IAtom> m = new HashMap<IAtom,IAtom>();		
		QuerySequenceElement el;
		
		//Processing first sequence element
		el = sequence.get(0);
		IAtom a0 = new Atom("C");		
		mol.addAtom(a0);
		m.put(el.center, a0);
		
		for (int k = 0; k < el.atoms.length; k++)
		{
			IAtom a = new Atom("C");
			mol.addAtom(a);
			//System.out.println("## --> " +  SmartsHelper.atomToString(el.atoms[k]));
			m.put(el.atoms[k],a);			
			addSkelletonBond(mol,m.get(el.center),a);
		}
		
		//Processing all other elements
		for (int i = 1; i < sequence.size(); i++)
		{
			el = sequence.get(i);
			if (el.center == null)
			{
				//It describes a bond
				addSkelletonBond(mol, m.get(el.atoms[0]), m.get(el.atoms[1]));
			}
			else
			{
				el = sequence.get(i);
				for (int k = 0; k < el.atoms.length; k++)
				{
					IAtom a = new Atom("C");
					mol.addAtom(a);
					m.put(el.atoms[k],a);
					addSkelletonBond(mol,m.get(el.center),a);
				}
			}
		}
		
		return(mol);
	}
	
	
	static void addSkelletonBond(IAtomContainer mol, IAtom at1, IAtom at2)
	{
		IAtom[] atoms = new IAtom[2];
		atoms[0] = at1;
		atoms[1] = at2;		
		Bond b = new Bond();
		b.setAtoms(atoms);
		b.setOrder(IBond.Order.SINGLE);
		mol.addBond(b);
	}
	
	
	void connectFragmentToMolecule(IAtomContainer base, IAtomContainer fragment, 
							int bondType, int basePos, int fragPos) 
	{
		
	}
	
	void condenseFragmentToMolecule(IAtomContainer base, IAtomContainer fragment, 
			int bondType, int basePos, int fragPos) 
	{

	}
	
	
	
}
