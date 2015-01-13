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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.smarts.AliphaticAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyOrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.AromaticAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AromaticQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.OrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSBond;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.processors.structure.HydrogenAdderProcessor;


public class SmartsHelper 
{
	SmilesParser smilesparser;
	static SmilesGenerator smiGen = new SmilesGenerator();
	
	int curIndex;
	HashMap<IAtom,TopLayer> firstSphere = new HashMap<IAtom,TopLayer>();	
	//Work container - list with the processed atom nodes
	HashMap<IAtom,AtomSmartsNode> nodes = new HashMap<IAtom,AtomSmartsNode>();
	HashMap<IAtom,String> atomIndexes = new HashMap<IAtom,String>();
	List<IBond> ringClosures = new ArrayList<IBond>();
	List<IAtom> traversedAtoms = new ArrayList<IAtom>(); 
	List<IAtom> nonTraversedAtoms = new ArrayList<IAtom>(); 
	
	
	int nAtom;
	int nBond;
	
	public SmartsHelper(IChemObjectBuilder builder) {
		super();
		smilesparser = new SmilesParser(builder);
	}
	static public String getAtomsString(IQueryAtomContainer query)
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
		
		Object prop = expression.getProperty("SmirksMapIndex");
		if (prop != null)
			sb.append("Map "+prop+" ");
		
		return(sb.toString());
	}
	
	static public String getAtomsExpressionTokens(IQueryAtomContainer query)
	{
		StringBuffer sb = new StringBuffer();	
		
		for (int i = 0; i < query.getAtomCount(); i++)
		{	
			if (query.getAtom(i) instanceof SmartsAtomExpression)
				sb.append("Expr["+getAtomExpressionTokens((SmartsAtomExpression)query.getAtom(i)) + "] ");
			else
				sb.append(atomToString((SMARTSAtom)query.getAtom(i)) + " ");			
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
			Integer implH = at.getImplicitHydrogenCount();
			if (implH == null)
			{	
				sb.append(" implH = null ");
				implH = new Integer(0);
			}	
			
			sb.append(" NumH=" + ( implH.intValue() + explHAt));
			
			if (at.getFlag(CDKConstants.ISAROMATIC)) 
				sb.append(" aromatic");
			
			sb.append("   " + at.getAtomTypeName());
			
			//Integer stereo = at.getStereoParity();			
			//sb.append(" stereo = " + stereo);
						
			
			sb.append("\n");
		}
		
		String stereoInfo = stereoInfoToString(container);	
		if (!stereoInfo.isEmpty())
		{
			sb.append("Stereo information:\n");
			sb.append(stereoInfo);
		}
		
		return(sb.toString());
	}
	
	static public String stereoInfoToString(IAtomContainer container)
	{
		StringBuffer sb = new StringBuffer();
		for (IStereoElement stereoEl : container.stereoElements())
		{
			if (stereoEl instanceof ITetrahedralChirality)
			{	
				sb.append(stereoCenterToString(container, (ITetrahedralChirality) stereoEl) + "\n");
				continue;
			}
			sb.append(stereoEl.toString());
		}
		
		return sb.toString();
	}
	
	static public String stereoCenterToString(IAtomContainer container, ITetrahedralChirality chiral)
	{
		StringBuffer sb = new StringBuffer();
		IAtom at = chiral.getChiralAtom();
		sb.append(at.getSymbol()+ container.getAtomNumber(at)+ "  ");
		sb.append(chiral.getStereo()+ "  ligands:");
		IAtom ats[] = chiral.getLigands();
		for (int i = 0; i < ats.length; i++)
			sb.append(" "+ats[i].getSymbol()+ container.getAtomNumber(ats[i]));
		
		return sb.toString();
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
			sb.append("  #" + i + " Atoms (" + at0_num + "," + at1_num + ")   Order = " + bondOrderToIntValue(bo));
			
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
	
	static public String bondToStringExhaustive(IQueryAtomContainer query, IBond bond)
	{
		StringBuffer sb = new StringBuffer();			
		sb.append(bondToString(bond)+ " "+
				bondAtomNumbersToString(query,bond)+ "  "+
				atomToString(bond.getAtom(0))+ " "+atomToString(bond.getAtom(1))+"\n");			
		return(sb.toString());
	}
	
	static public String getBondsString(IQueryAtomContainer query)
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
	
	static public IQueryAtomContainer getQueryAtomContainer(IAtomContainer ac, boolean HandleAromaticity)
	{
		IQueryAtomContainer query = new QueryAtomContainer();
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
			
			IAtom[] atoms = new IAtom[2];
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
		//System.out.println(b.getClass().getSimpleName());
		
		if (a instanceof SmartsAtomExpression)
			return(a.toString());	
		
		if (a instanceof AliphaticSymbolQueryAtom)
			return(a.getSymbol());
		
		if (a instanceof AromaticSymbolQueryAtom)
			return(a.getSymbol().toLowerCase());
		
		if (a instanceof AliphaticAtom)
			return("A");
		
		if (a instanceof AromaticAtom)
			return("a");
		
		if (a instanceof AnyAtom)
			return("*");
		
		
		//This is a default exit. Generally it should not happen. 
		//Class SymbolQueryAtomAromaticityNotSpecified would be process here as well
		return(a.getSymbol());		
	}
	
	static public String bondToString(IBond b)
	{
		
		//TODO handle the cis/trans information
		
		if (b instanceof SmartsBondExpression)
			return(b.toString());
		
		if (b instanceof SingleOrAromaticBond)
			return("");
		
		if (b instanceof SingleNonAromaticBond)
			return("-");
		
		if (b instanceof DoubleNonAromaticBond)
			return("=");
		
		if (b instanceof DoubleStereoBond)
			return("=");
		
		if (b instanceof RingQueryBond)
			return("@");
		
		if (b instanceof AnyOrderQueryBond)
			return("~");
		
		if (b instanceof AromaticQueryBond)
			return(":");
		
		if (b instanceof OrderQueryBond)
		{	
			if (b.getOrder() == IBond.Order.SINGLE)
				return("-");
			if (b.getOrder() == IBond.Order.DOUBLE)
				return("=");
			if (b.getOrder() == IBond.Order.TRIPLE)
				return("#");
		}
		
		//These are quite specific cases which are due to Ambit specific flags 
		//such as mSupportDoubleBondAromaticityNotSpecified flag		
		if (b instanceof DoubleBondAromaticityNotSpecified)  
			return("=");
		if (b instanceof SingleBondAromaticityNotSpecified)
			return("-");
		if (b instanceof TripleBondAromaticityNotSpecified)
			return("#");
		
		//These is a default exit. Generally this should not happen.
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

	
	
	void determineFirstSheres(IQueryAtomContainer query)
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
	/**
	 * @param query
	 * @return SMARTS string 
	 */
	public String toSmarts(IQueryAtomContainer query)
	{
		determineFirstSheres(query);
		traversedAtoms.clear();  //The list of atoms which were traversed (included into Smarts string) 
		nonTraversedAtoms.clear();
		for (int i = 0; i < query.getAtomCount(); i++)
			nonTraversedAtoms.add(query.getAtom(i));
		
		nodes.clear();
		atomIndexes.clear();
		ringClosures.clear();
		curIndex = 1;
		
		StringBuffer resBuf = new StringBuffer();
		
		while (traversedAtoms.size() < nAtom)
		{
			if (!traversedAtoms.isEmpty())
				resBuf.append(".");
			AtomSmartsNode node = new AtomSmartsNode();
			node.parent = null;
			//node.atom = query.getAtom(0);
			node.atom = nonTraversedAtoms.get(0);
			traverseAtom(node.atom);
			nodes.put(node.atom, node);
			resBuf.append(nodeToString(node.atom));
		}
		
		return(resBuf.toString() );
	}
	
	void traverseAtom(IAtom at)
	{
		nonTraversedAtoms.remove(at);
		traversedAtoms.add(at);
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
		List<String> branches = new ArrayList<String>();
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
				traverseAtom(newNode.atom);
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
					String ind = ((curIndex>9)?"%":"") + curIndex;
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
		
		sb.append(branches.get(branches.size() - 1).toString());
		return(sb.toString());
	}
	
	public static String moleculeToSMILES(IAtomContainer mol, boolean useAromaticityFlag) throws Exception
	{	 
		//SmilesGenerator(true) - is needed for  aromatic structures to obtain incorrect SMILES 
		
		smiGen.setUseAromaticityFlag(useAromaticityFlag); 
		return (smiGen.createSMILES(mol));
		
		
		/*
		java.io.StringWriter result =  new java.io.StringWriter();
		SMILESWriter writer = new SMILESWriter(result);
		
		writer.write(mol);
		writer.close();

		return(result.toString());
		*/
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
			res[i] =  SmartsHelper.moleculeToSMILES(frag,true);
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
	
	
	
	
	static public List<Integer> getSmartsPositions(String smartsQuery, IAtomContainer target, 
					boolean FlagSupportDoubleBondAromaticityNotSpecified) throws Exception
	{	
		SmartsParser sp = new SmartsParser();
		sp.mSupportDoubleBondAromaticityNotSpecified = FlagSupportDoubleBondAromaticityNotSpecified;
		IsomorphismTester isoTester = new IsomorphismTester();
		
		IQueryAtomContainer query  = sp.parse(smartsQuery);
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
	
	public static void setAromaticAtomsFromBondFlagInfo(IAtomContainer mol)
	{
		for (IBond bond : mol.bonds()) 
			if (bond.getFlag(CDKConstants.ISAROMATIC))
			{
				bond.getAtom(0).setFlag(CDKConstants.ISAROMATIC, true);
				bond.getAtom(1).setFlag(CDKConstants.ISAROMATIC, true);
			}
	}
	
	public static void preProcessStructure(IAtomContainer mol) throws Exception
	{
		preProcessStructure(mol, true, true);
	}
	
	public static void preProcessStructure(IAtomContainer mol, boolean HandleAromaticity, boolean AddImplicitHAtoms) throws Exception
	{
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		
		if (AddImplicitHAtoms)
		{	
			CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
			adder.addImplicitHydrogens(mol);
		}	
		
		if (HandleAromaticity)
			CDKHueckelAromaticityDetector.detectAromaticity(mol);
	}
	
	public static void preProcessStructure(IAtomContainer mol, boolean HandleAromaticity, boolean AddImplicitHAtoms, boolean ImplicitHToExplicit) throws Exception
	{
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
		
		if (AddImplicitHAtoms)
		{	
			CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance());
			adder.addImplicitHydrogens(mol);
			
			if (ImplicitHToExplicit)
				AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
		}	
		
		if (HandleAromaticity)
			CDKHueckelAromaticityDetector.detectAromaticity(mol);
	}
	
	public static void convertExcplicitHAtomsToImplicit(IAtomContainer mol) throws Exception
	{	
		List<IBond> removeBonds = new ArrayList<IBond>();
		
		for (IBond bond : mol.bonds())
		{	
			if (bond.getAtom(0).getSymbol().equals("H"))
			{
				if (bond.getAtom(1).getSymbol().equals("H"))
					removeBonds.add(bond);
				else
				{
					add1IplicitHAtom(bond.getAtom(1));
					removeBonds.add(bond);
				}
			}
			else
			{
				if (bond.getAtom(1).getSymbol().equals("H"))
				{	
					add1IplicitHAtom(bond.getAtom(0));
					removeBonds.add(bond);
				}	
			}
			
			//System.out.println(bond.getAtom(0).getSymbol() + " ~ " + bond.getAtom(1).getSymbol() + "  " + implicitHAtomsVector(mol));
		}
		
		List<IAtom> removeAtoms = new ArrayList<IAtom>();
		
		for (IAtom atom : mol.atoms())
		{
			if (atom.getSymbol().equals("H"))
				removeAtoms.add(atom);
		}
		
		for (IBond bond : removeBonds)
			mol.removeBond(bond);
		
		for (IAtom atom : removeAtoms)
			mol.removeAtom(atom);
	}
	
	public static void add1IplicitHAtom(IAtom atom)
	{
		if (atom.getImplicitHydrogenCount() == CDKConstants.UNSET)
			atom.setImplicitHydrogenCount(new Integer(1));
		else
			atom.setImplicitHydrogenCount(atom.getImplicitHydrogenCount() + 1);
	}
	
	public static String implicitHAtomsVector(IAtomContainer mol)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i< mol.getAtomCount(); i++)
		{
			Integer ihc = mol.getAtom(i).getImplicitHydrogenCount();
			if (ihc == null)
				sb.append("  null");
			else
				sb.append("  " + ihc);
		}
		return sb.toString();
	}
	
	
}
