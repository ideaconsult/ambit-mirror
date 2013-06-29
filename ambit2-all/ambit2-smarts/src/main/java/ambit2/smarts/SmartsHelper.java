/*
Copyright (C) 2007-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.smarts;

import java.util.HashMap;
import java.util.Vector;

import org.openscience.cdk.Atom;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.SMILESWriter;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSBond;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.processors.structure.HydrogenAdderProcessor;


public class SmartsHelper 
{
	static SmilesParser smilesparser;
	int curIndex;
	HashMap<IAtom,TopLayer> firstSphere = new HashMap<IAtom,TopLayer>();	
	//Work container - list with the processed atom nodes
	HashMap<IAtom,AtomSmartsNode> nodes = new HashMap<IAtom,AtomSmartsNode>();
	HashMap<IAtom,String> atomIndexes = new HashMap<IAtom,String>();
	Vector<IBond> ringClosures = new Vector<IBond>();
	
	int nAtom;
	int nBond;
	
	public SmartsHelper(IChemObjectBuilder builder) {
		super();
		smilesparser = new SmilesParser(builder);
	}
	static public String getAtomsString(QueryAtomContainer query)
	{
		StringBuffer sb = new StringBuffer();	
		
		for (int i = 0; i < query.getAtomCount(); i++)
		{	
			sb.append(atomToString((SMARTSAtom)query.getAtom(i)) + " ");			
		}	
		return(sb.toString());
	}
	
		
	static public String getAtomExpressionTokens(SmartsAtomExpression expression)
	{
		StringBuffer sb = new StringBuffer();	
		
		for (int k = 0; k < expression.tokens.size(); k++)
		{
			sb.append("tok("+expression.tokens.get(k).type+","+expression.tokens.get(k).param+") ");
		}
		return(sb.toString());
	}
	
	static public String getAtomsString(IAtomContainer container)
	{
		StringBuffer sb = new StringBuffer();	
		
		for (int i = 0; i < container.getAtomCount(); i++) 
			sb.append(container.getAtom(i).getSymbol() + " ");
		return(sb.toString());
	}
	
	static public String getAtomsAttributes(IAtomContainer container)
	{
		StringBuffer sb = new StringBuffer();	
		
		for (int i = 0; i < container.getAtomCount(); i++)
		{	
			IAtom at = container.getAtom(i); 
			sb.append("  #" + i + "  ");
			sb.append(at.getSymbol());
			Integer explHInt = (Integer)at.getProperty(CMLUtilities.ExplicitH);
			int explHAt = 0;
			if (explHInt != null)
				explHAt = explHInt.intValue();
        	/*
        	https://sourceforge.net/tracker/?func=detail&aid=3020065&group_id=20024&atid=120024
			sb.append(" NumH=" + (at.getHydrogenCount() + explHAt));
			*/			
			sb.append(" NumH=" + (at.getImplicitHydrogenCount() + explHAt));
			if (at.getFlag(CDKConstants.ISAROMATIC)) 
				sb.append(" aromatic");
			
			Integer stereo = at.getStereoParity();			
			sb.append(" stereo = " + stereo);
						
			
			sb.append("\n");
		}	
		return(sb.toString());
	}
	
	static public String getBondAttributes(IAtomContainer container)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < container.getBondCount(); i++)
		{
			IBond bo = container.getBond(i); 
			IAtom at0 = bo.getAtom(0);
			IAtom at1 = bo.getAtom(1);
			int at0_num = container.getAtomNumber(at0);
			int at1_num = container.getAtomNumber(at1);
			sb.append("  #" + i + "  (" + at0_num + "," + at1_num + ")");
			
			if (bo.getFlag(CDKConstants.ISAROMATIC)) 
				sb.append(" aromatic");
			
			
			sb.append("\n");
		}
		
		return(sb.toString());
	}	
	
	static public String getBondsString(IAtomContainer query)
	{
		StringBuffer sb = new StringBuffer();	
		
		for (int i = 0; i < query.getBondCount(); i++)
		{	
			sb.append(query.getBond(i).getOrder() + " ");			
		}	
		return(sb.toString());
	}
	
	static public String bondToStringExhaustive(QueryAtomContainer query, IBond bond)
	{
		StringBuffer sb = new StringBuffer();			
		sb.append(bondToString(bond)+ " "+
				bondAtomNumbersToString(query,bond)+ "  "+
				atomToString(bond.getAtom(0))+ " "+atomToString(bond.getAtom(1))+"\n");			
		return(sb.toString());
	}
	
	static public String getBondsString(QueryAtomContainer query)
	{
		StringBuffer sb = new StringBuffer();			
		for (int i = 0; i < query.getBondCount(); i++)
		{	
			sb.append(bondToString(query.getBond(i))+ " "+
					bondAtomNumbersToString(query,query.getBond(i) )+ "  "+
					atomToString(query.getBond(i).getAtom(0))+ " "+
					atomToString(query.getBond(i).getAtom(1))+"\n");
		}	
		return(sb.toString());
	}
	
	static public QueryAtomContainer getQueryAtomContainer(IAtomContainer ac, boolean HandleAromaticity)
	{
		QueryAtomContainer query = new QueryAtomContainer();
		for (int i = 0; i < ac.getAtomCount(); i++)
		{
			IAtom a = ac.getAtom(i);
			if (HandleAromaticity)
			{
				if (a.getFlag(CDKConstants.ISAROMATIC))
				{
					AromaticSymbolQueryAtom newAt = new AromaticSymbolQueryAtom();
					newAt.setSymbol(a.getSymbol());
					query.addAtom(newAt);
				}
				else
				{
					AliphaticSymbolQueryAtom newAt = new AliphaticSymbolQueryAtom();
					newAt.setSymbol(a.getSymbol());
					query.addAtom(newAt);
				}	
			}
			else
			{	
				SymbolQueryAtomAromaticityNotSpecified newAt = new SymbolQueryAtomAromaticityNotSpecified();
				newAt.setSymbol(a.getSymbol());
				query.addAtom(newAt);
			}
		}
		
		for (int i = 0; i < ac.getBondCount(); i++)
		{
			IBond b = ac.getBond(i);
			IAtom at0 = b.getAtom(0);
			IAtom at1 = b.getAtom(1);
			int index0 = ac.getAtomNumber(at0);
			int index1 = ac.getAtomNumber(at1);
			
			SMARTSBond newBo;
			
			if (b.getOrder()== IBond.Order.TRIPLE)
				newBo = new TripleBondAromaticityNotSpecified();
			else
			{	
				if (b.getOrder()== IBond.Order.DOUBLE)
					newBo = new DoubleBondAromaticityNotSpecified();
				else
				{
					if (HandleAromaticity)
					{
						boolean isArom = b.getFlag(CDKConstants.ISAROMATIC);
						if (isArom)
							newBo = new SingleOrAromaticBond();
						else
							newBo = new SingleNonAromaticBond();
					}
					else
						newBo = new SingleBondAromaticityNotSpecified();
				}
			}
			
			IAtom[] atoms = new Atom[2];
		    atoms[0] = query.getAtom(index0);
		    atoms[1] = query.getAtom(index1);
		    newBo.setAtoms(atoms);
		    query.addBond(newBo);
		}
		
		return query;
	}
	
	static public int bondOrderToIntValue(IBond b)
	{
		if (b.getOrder() == IBond.Order.SINGLE)
			return(1);
		if (b.getOrder() == IBond.Order.DOUBLE)
			return(2);
		if (b.getOrder() == IBond.Order.TRIPLE)
			return(3);
		if (b.getOrder() == IBond.Order.QUADRUPLE)
			return(4);
		
		
		return 0;
	}

	static public String atomToString(IAtom a)
	{
		if (a instanceof SmartsAtomExpression)
			return(a.toString());		
		if (a instanceof AliphaticSymbolQueryAtom)
			return(a.getSymbol());
		if (a instanceof AromaticSymbolQueryAtom)
			return("Ar-"+a.getSymbol());
		
		return(a.getSymbol());		
	}
	
	static public String bondToString(IBond b)
	{
		//TODO - to improve it ???
		
		if (b instanceof SmartsBondExpression)
			return(b.toString());		
		if (b instanceof SingleOrAromaticBond)
			return("");
				
		if (b.getOrder() == IBond.Order.SINGLE)
			return("-");
		if (b.getOrder() == IBond.Order.DOUBLE)
			return("=");
		if (b.getOrder() == IBond.Order.TRIPLE)
			return("#");
		
		return("-");
	}
	
	static public String smilesBondToString(IBond b, boolean aromaticity)
	{			
		if (aromaticity)
			if (b.getFlag(CDKConstants.ISAROMATIC))
				return("");
		
		if (b.getOrder() == IBond.Order.SINGLE)
			return("");
		if (b.getOrder() == IBond.Order.DOUBLE)
			return("=");
		if (b.getOrder() == IBond.Order.TRIPLE)
			return("#");
		
		return("");
	}
	
	static public String bondAtomNumbersToString(IAtomContainer container, IBond b)
	{
		return(" "+ container.getAtomNumber(b.getAtom(0))+ " "+container.getAtomNumber(b.getAtom(1)));				
	}

	
	
	void determineFirstSheres(QueryAtomContainer query)
	{
		firstSphere.clear();
		nAtom =  query.getAtomCount();
		nBond =  query.getBondCount();
		
		for (int i = 0; i < nAtom; i++)
		{				
			firstSphere.put(query.getAtom(i), new TopLayer());
		}	
			
		for (int i = 0; i < nBond; i++)
		{
			IBond bond = query.getBond(i);
			IAtom at0 = bond.getAtom(0);
			IAtom at1 = bond.getAtom(1);
			firstSphere.get(at0).atoms.add(at1);
			firstSphere.get(at0).bonds.add(bond);
			firstSphere.get(at1).atoms.add(at0);
			firstSphere.get(at1).bonds.add(bond);			
		}
	}
	
	public String toSmarts(QueryAtomContainer query)
	{
		determineFirstSheres(query);
		nodes.clear();
		atomIndexes.clear();
		ringClosures.clear();
		curIndex = 1;
		AtomSmartsNode node = new AtomSmartsNode();
		node.parent = null;
		node.atom = query.getAtom(0);
		nodes.put(node.atom, node);
		return(nodeToString(node.atom));
	}
	
	void addIndexToAtom(String ind, IAtom atom)
	{	
		//System.out.println("Set index "+ind);
		
		if (atomIndexes.containsKey(atom))
		{
			String old_ind = atomIndexes.get(atom);
			atomIndexes.remove(atom);
			atomIndexes.put(atom,old_ind+ind);
		}
		else 
			atomIndexes.put(atom,ind);
	}
	
	String nodeToString(IAtom atom)
	{
		StringBuffer sb = new StringBuffer();
		TopLayer afs = firstSphere.get(atom);
		AtomSmartsNode curNode = nodes.get(atom);
		Vector<String> branches = new Vector<String>();
		for (int i=0; i<afs.atoms.size(); i++)
		{
			IAtom neighborAt = afs.atoms.get(i);
			if (neighborAt == curNode.parent)
				continue;
			
			AtomSmartsNode neighborNode = nodes.get(neighborAt);
			if (neighborNode == null) // This node has not been registered yet
			{
				//Registering a new Node and a new branch
				AtomSmartsNode newNode = new AtomSmartsNode();
				newNode.atom = neighborAt;
				newNode.parent = atom;
				nodes.put(newNode.atom, newNode); 
				
				String bond_str = bondToString(afs.bonds.get(i));				
				String newBranch = bond_str + nodeToString(neighborAt);
				branches.add(newBranch);
			}
			else
			{
				//Handle ring closure: adding indexes to both atoms
				IBond neighborBo = afs.bonds.get(i);
				if (!ringClosures.contains(neighborBo))
				{	
					ringClosures.add(neighborBo);
					String ind = (curIndex>9)?"%":"" + curIndex;
					addIndexToAtom(bondToString(neighborBo) + ind, atom);	
					addIndexToAtom(ind, neighborAt);
					curIndex++;
				}
			}
		}
		//Add atom from the current node
		sb.append(atomToString((SMARTSAtom) atom));
				
		//Add indexes
		if (atomIndexes.containsKey(atom))		
			sb.append(atomIndexes.get(atom));
		
		//Add branches
		if (branches.size() == 0)
			return(sb.toString());
		
		for(int i = 0; i < branches.size()-1; i++)
			sb.append("("+branches.get(i).toString()+")");
		sb.append(branches.lastElement().toString());
		return(sb.toString());
	}
	
	public static String moleculeToSMILES(IAtomContainer mol) throws Exception
	{	 
		//TODO use SmilesGenerator(true)
		java.io.StringWriter result =  new java.io.StringWriter();
		SMILESWriter writer = new SMILESWriter(result);
		
		writer.write(mol);
		writer.close();

		return(result.toString());
	}
	
	public static void convertToCarbonSkelleton(IAtomContainer mol)
	{
		//All atoms are made C and all bond are made single
		for (int i = 0; i < mol.getAtomCount(); i++)
		{
			IAtom at = mol.getAtom(i);
			at.setSymbol("C");
			at.setFormalCharge(0);
			at.setMassNumber(0); 
		}
		
		for (int i = 0; i < mol.getBondCount(); i++)
		{
			IBond bo = mol.getBond(i);
			bo.setOrder(IBond.Order.SINGLE);
		}
	}
	
	public static  IMolecule getMoleculeFromSmiles(String smi) throws Exception {
		IMolecule mol = null;
		SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());			
		mol = sp.parseSmiles(smi);
		return mol;
	}
	
	public static  IMolecule getMoleculeFromSmiles(String smi, boolean FlagExplicitHatoms) throws Exception {
		IMolecule mol = null;
		SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());			
		mol = sp.parseSmiles(smi);
		
		//TODO
		//!!!! aromaticity might be lost in the preprocessing phase
				
		//some pre-processing is done 
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
		adder.addImplicitHydrogens(mol);
		if (FlagExplicitHatoms)
			HydrogenAdderProcessor.convertImplicitToExplicitHydrogens(mol);
		
		return mol;
	}
	
	public static String[] getCarbonSkelletonsFromString(String smiles) throws Exception
	{	
		IMolecule mol = getMoleculeFromSmiles(smiles);
		IMoleculeSet ms =  ConnectivityChecker.partitionIntoMolecules(mol);
		int n = ms.getAtomContainerCount();
		String res[] = new String[n];
		for(int i =0; i < n; i++)
		{	
			IAtomContainer frag = ms.getAtomContainer(i);
			SmartsHelper.convertToCarbonSkelleton(frag);
			res[i] =  SmartsHelper.moleculeToSMILES(frag);
		}
		return(res);
	}
	
	
	static public void printIntArray(int c[])
	{
		if (c == null)
		{	
			System.out.println("null");
			return;
		}	
		for (int i = 0; i < c.length; i++)			
			System.out.print(c[i]+" ");
		System.out.println();
	}
	
	static public String toString(int c[])
	{
		StringBuffer sb = new StringBuffer();
		if (c == null)
			sb.append("null");
		else
			for (int i = 0; i < c.length; i++)			
				sb.append(" "+c[i]);
		
		return (sb.toString());
	}
	
	static public String atomPropertiesToString(IAtom atom)
	{
		StringBuffer sb = new StringBuffer();
		if (atom.getProperties() == null)
			return("");
		
		Object keys[] = atom.getProperties().keySet().toArray();
		for (int i = 0; i < keys.length; i++)
		{				
			if (keys[i].toString().toString().equals(CMLUtilities.RingData) || keys[i].toString().toString().equals(CMLUtilities.RingData2))
				sb.append(keys[i].toString()+" = "+ toString((int[])atom.getProperties().get(keys[i]))+"\n");	
			else
				sb.append(keys[i].toString()+" = "+ atom.getProperties().get(keys[i])+"\n");
		}	
		return(sb.toString());
	}
	
	
	
	
	static public Vector<Integer> getSmartsPositions(String smartsQuery, IAtomContainer target, 
					boolean FlagSupportDoubleBondAromaticityNotSpecified) throws Exception
	{	
		SmartsParser sp = new SmartsParser();
		sp.mSupportDoubleBondAromaticityNotSpecified = FlagSupportDoubleBondAromaticityNotSpecified;
		IsomorphismTester isoTester = new IsomorphismTester();
		
		QueryAtomContainer query  = sp.parse(smartsQuery);
		sp.setNeededDataFlags();
		String errorMsg = sp.getErrorMessages();
		if (!errorMsg.equals(""))
		{
			System.out.println("Smarts Parser errors:\n" + errorMsg);			
			return null;
		}
		
		isoTester.setQuery(query);
		sp.setSMARTSData(target);
		
		return isoTester.getIsomorphismPositions(target);
	}
	
	
	
	
}
