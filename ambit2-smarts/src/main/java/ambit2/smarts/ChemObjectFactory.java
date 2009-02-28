package ambit2.smarts;

import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.io.MyIteratingMDLReader;
import ambit2.hashcode.MoleculeAndAtomsHashing;

/** Utilities for generation of different types of 
 * chemical objects: molecules, fragments, queries */
public class ChemObjectFactory 
{	
	public Vector<SequenceElement> sequence = new Vector<SequenceElement>();
	Vector<IAtom> sequencedAtoms = new Vector<IAtom>();
	Vector<IAtom> sequencedBondAt1 = new Vector<IAtom>();
	Vector<IAtom> sequencedBondAt2 = new Vector<IAtom>();
	MoleculeAndAtomsHashing molHash = new MoleculeAndAtomsHashing();
	SmartsManager man = new SmartsManager();
	SmartsParser parser = new SmartsParser();
	SmartsToChemObject stco = new SmartsToChemObject(); 
	IsomorphismTester isoTester = new IsomorphismTester();
	
	
	
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
		
		//Calculating the first topological layer of each atom
		if (target.getAtom(0).getProperty(TopLayer.TLProp) == null)
			TopLayer.setAtomTopLayers(target, TopLayer.TLProp);
		
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
		Stack<SequenceElement> stack = new Stack<SequenceElement>();
		stack.push(seqEl);
		while (!stack.empty())
		{
			//curAddedAtoms.clear();
			SequenceElement curSeqAt = stack.pop();
			for (int i = 0; i < curSeqAt.atoms.length; i++)
			{
				topLayer = (TopLayer)curSeqAt.atoms[i].getProperty(TopLayer.TLProp);
				if (topLayer.atoms.size() == 1)
					continue; // it is terminal atom and no further sequencing should be done
				int a[] = getSeqAtomsInLayer(topLayer);
				
				n = 0;
				for (int k = 0; k<a.length; k++)
					if (a[k] == 0)
						n++;
				
				if (n > 0)
				{	
					seqEl = new SequenceElement();
					seqEl.center = curSeqAt.atoms[i];
					seqEl.atoms = new IAtom[n];
					seqEl.bonds = new IBond[n];
					sequence.add(seqEl);
					stack.add(seqEl);
				}	
				
				int j = 0;				
				for (int k = 0; k < a.length; k++)
				{
					if (a[k] == 0)
					{	
						seqEl.atoms[j] = topLayer.atoms.get(k);
						seqEl.bonds[j] = topLayer.bonds.get(k);
						addSeqBond(seqEl.center,seqEl.atoms[j]);
						sequencedAtoms.add(seqEl.atoms[j]);
						//curAddedAtoms.add(seqEl.atoms[j]);
						j++;
					}
					else
					{	
						if (curSeqAt.center == topLayer.atoms.get(k))
							continue;
						//Check whether  bond(curSeqAt.atoms[i]-topLayer.atoms.get(k))
						//is already sequenced
						if (getSeqBond(curSeqAt.atoms[i],topLayer.atoms.get(k)) != -1)
							continue;						
						//topLayer.atoms.get(k) atom is already sequenced.
						//Therefore sequnce element of 'bond' type is registered.						
						//newSeqEl is not added in the stack (this is not needed for this bond)
						SequenceElement newSeqEl = new SequenceElement();						
						newSeqEl.center = null;
						newSeqEl.atoms = new IAtom[2];
						newSeqEl.bonds = new IBond[1];
						newSeqEl.atoms[0] = curSeqAt.atoms[i];
						newSeqEl.atoms[1] = topLayer.atoms.get(k);
						addSeqBond(newSeqEl.atoms[0],newSeqEl.atoms[1]);
						newSeqEl.bonds[0] = topLayer.bonds.get(k);
						sequence.add(newSeqEl);						
					}
				}
			}			
		}
		
		for(int i = 0; i < sequence.size(); i++)
			sequence.get(i).setAtomNums(target);
	}
	
	void addSeqBond(IAtom at1, IAtom at2)
	{
		sequencedBondAt1.add(at1);
		sequencedBondAt2.add(at2);
	}
	
	int[] getSeqAtomsInLayer(TopLayer topLayer)
	{
		int a[] = new int[topLayer.atoms.size()];
		for (int i = 0; i <topLayer.atoms.size(); i++)
		{	
			if (containsAtom(sequencedAtoms, topLayer.atoms.get(i)))
			{	
				a[i] = 1;
			}	
			else
				a[i] = 0;
		}	
		return(a);
	}
	
	
	boolean containsAtom(Vector<IAtom> v, IAtom atom)
	{
		for(int i = 0; i < v.size(); i++)
			if (v.get(i) == atom)
				return(true);
		return(false);
	}
	
	int getSeqBond(IAtom at1, IAtom at2)
	{
		for (int i = 0; i < sequencedBondAt1.size(); i++)
		{
			if (sequencedBondAt1.get(i)==at1)
			{
				if (sequencedBondAt2.get(i)==at2)
					return(i);
			}
			else
				if (sequencedBondAt1.get(i)==at2)
				{
					if (sequencedBondAt2.get(i)==at1)
						return(i);
				}
		}
		return(-1);		
	}
	
	public IAtomContainer getFragmentFromSequence(int numSteps)
	{
		AtomContainer mol = new AtomContainer();		
		HashMap<IAtom,IAtom> m = new HashMap<IAtom,IAtom>();	//Mapping original --> copy	
		SequenceElement el;
		
		//Processing first sequence element (The first center is always different from null
		el = sequence.get(0);
		IAtom a0 = getAtomCopy(el.center);		
		mol.addAtom(a0);
		m.put(el.center, a0);
				
		for (int k = 0; k < el.atoms.length; k++)
		{
			IAtom a = getAtomCopy(el.atoms[k]);
			mol.addAtom(a);
			m.put(el.atoms[k],a);
			addBond(mol, m.get(el.center), a, el.bonds[k].getOrder(), el.bonds[k].getFlag(CDKConstants.ISAROMATIC) );
		}
		
		for (int i = 1; i <= numSteps; i++)
		{
			el = sequence.get(i);
			if (el.center == null) //Handling a closing bond
			{	 
				addBond(mol,m.get(el.atoms[0]),m.get(el.atoms[1]), 
						el.bonds[0].getOrder(), el.bonds[0].getFlag(CDKConstants.ISAROMATIC));
			}
			else
			{
				for (int k = 0; k < el.atoms.length; k++)
				{
					IAtom a = getAtomCopy(el.atoms[k]);
					mol.addAtom(a);
					m.put(el.atoms[k],a);
					addBond(mol, m.get(el.center), a, el.bonds[k].getOrder(), el.bonds[k].getFlag(CDKConstants.ISAROMATIC) );
				}
			}
		}
		return(mol);
	}
	
	IAtom getAtomCopy(IAtom atom)
	{
		IAtom copyAtom = new Atom(atom.getSymbol());
		
		if (atom.getFlag(CDKConstants.ISAROMATIC))
			copyAtom.setFlag(CDKConstants.ISAROMATIC,true);
		
		int charge = atom.getFormalCharge().intValue(); 
		if (charge != 0)
			copyAtom.setFormalCharge(charge);
		
		return(copyAtom);
	}
	
	void addBond(IAtomContainer mol, IAtom at1, IAtom at2, IBond.Order order, boolean isAromatic)
	{
		IAtom[] atoms = new IAtom[2];
		atoms[0] = at1;
		atoms[1] = at2;		
		Bond b = new Bond();
		b.setAtoms(atoms);
		b.setOrder(order);
		if (isAromatic)
			b.setFlag(CDKConstants.ISAROMATIC, true);
		mol.addBond(b);
	}
	
	
	//This function generates a Carbon skeleton from a query atom sequence
	//It is used mainly for testing purposes
	public static IAtomContainer getCarbonSkelleton(Vector<QuerySequenceElement> sequence)
	{
		AtomContainer mol = new AtomContainer();		
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
		//TODO
	}
	
	void connectFragmentToMoleculeSpiro(IAtomContainer base, IAtomContainer fragment, 
							int basePos, int fragPos) 
	{
		//TODO	
	}
	
	void condenseFragmentToMolecule(IAtomContainer base, IAtomContainer fragment, 
			int bondType, int basePos, int fragPos) 
	{
		//TODO
	}
	
	
	//--------------------------------------------------------------------------------------
	
		
	
	public void produceStructuresExhaustively (IAtomContainer mol, Vector<StructInfo> vStr, int maxNumSeqSteps)
	{	
		ChemObjectToSmiles cots = new ChemObjectToSmiles();
		for (int k = 0; k < mol.getAtomCount(); k++)
		{
			setAtomSequence(mol, mol.getAtom(k));
			int n = sequence.size();
			if (n > maxNumSeqSteps)
				n = maxNumSeqSteps;
			for (int i = 0; i < n; i++)
			{
				IAtomContainer struct = getFragmentFromSequence(i);
				String smiles = cots.getSMILES(struct);
				//Long hash = molHash.getMoleculeHash(struct);
				
				if (!checkForDuplication(smiles, vStr))
				{	
					StructInfo strInfo = new StructInfo();
					strInfo.smiles = smiles;
					//strInfo.hash = hash;
					strInfo.atomCount = struct.getAtomCount();
					strInfo.bondCount = struct.getBondCount();
					vStr.add(strInfo);
				}	
			}
		}
	}
	
	
	boolean checkForDuplication(String smarts, Vector<StructInfo> vStr)
	{	
		man.setQuery(smarts);
		for (int i = 0; i < vStr.size(); i++)
		{
			StructInfo s = vStr.get(i);
			if (man.getQueryContaner().getAtomCount() == s.atomCount)
				if (man.getQueryContaner().getBondCount() == s.bondCount)
				{					
					if (smarts.equals(s.smiles))
						return(true);
					
					QueryAtomContainer q = parser.parse(s.smiles);
					IAtomContainer ac = stco.extractAtomContainer(q,null);
					boolean res = man.searchIn(ac);
					if (res)
						return(true);
					
					//	if (hash.equals(s.hash))
					//		return(true);
				}
		}
		return(false);
	}
	
	public void produceStructsFromMDL(String mdlFile, int maxNumSeqSteps, int maxNumRecord, 
				Vector<StructInfo> vStr, String outFile)
	{	
		try
		{
			DefaultChemObjectBuilder b = DefaultChemObjectBuilder.getInstance();
			MyIteratingMDLReader reader = new MyIteratingMDLReader(new FileReader(mdlFile),b);
			int record=0;

			while (reader.hasNext()) 
			{	
				record++;
				if (record % 50 == 0)
					saveStructs(vStr,outFile);
				
				if (record > maxNumRecord)
					break;
				Object o = reader.next();
				if (o instanceof IAtomContainer) 
				{
					IAtomContainer mol = (IAtomContainer)o;
					if (mol.getAtomCount() == 0) continue;
					AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
					CDKHueckelAromaticityDetector.detectAromaticity(mol);
					produceStructuresExhaustively(mol, vStr, maxNumSeqSteps);
					System.out.println("record " + record+ "  " + vStr.size());
				}
			}	
		}
		catch(Exception e){
			System.out.println(e.toString());
		}
		
		saveStructs(vStr,outFile);
	}
	
	void saveStructs(Vector<StructInfo> vStr, String fName)
	{
		try
		{	
			File file = new File(fName);
			RandomAccessFile f = new RandomAccessFile(file,"rw");
			f.setLength(0);
			for (int i = 0; i<vStr.size(); i++)
				f.write((vStr.get(i).smiles + "\r\n").getBytes());
			f.close();
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
	}
	
	void saveStructStatistics(Vector<String> smiles, Vector<Integer> stat, String fName)
	{
		try
		{	
			File file = new File(fName);
			RandomAccessFile f = new RandomAccessFile(file,"rw");
			f.setLength(0);
			for (int i = 0; i<smiles.size(); i++)
				f.write((stat.get(i).toString()+"  "+smiles.get(i) + "\r\n").getBytes());
			f.close();
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
	}
	
	
	public void performStructureStatistics(String smilesFile, String mdlFile, 
				int portionSize, int nUsedStr, String outFile)
	{
		//Each structure from smilesFile is searched against mdlFile
		//The process is done in portions.
		try
		{	
			File file = new File(smilesFile);
			RandomAccessFile f = new RandomAccessFile(file,"r");			
			long length = f.length();
			
			Vector<String> smiles = new Vector<String>();
			Vector<String> allSmiles = new Vector<String>();
			Vector<Integer> allStat = new Vector<Integer>();
			int n = 0;
			while (f.getFilePointer() < length)
			{	
				n++;
				//System.out.print(" " + n);
				String line = f.readLine();
				smiles.add(line);
				if (smiles.size() % portionSize == 0)
				{
					System.out.println("Stattistics " + n);
					Vector<Integer> stat = doStatisticsForStructs(smiles, mdlFile, nUsedStr);
					for (int i = 0; i < smiles.size(); i++)
					{
						allSmiles.add(smiles.get(i));
						allStat.add(stat.get(i));
					}
					smiles.clear();
					saveStructStatistics(allSmiles, allStat, outFile);
				}
			}
			
			//What left from smiles vector is processed as well
			if (!smiles.isEmpty())
			{	
				Vector<Integer> stat = doStatisticsForStructs(smiles, mdlFile, nUsedStr);
				for (int i = 0; i < smiles.size(); i++)
				{
					allSmiles.add(smiles.get(i));
					allStat.add(stat.get(i));
				}			
				saveStructStatistics(allSmiles, allStat, outFile);
			}
			f.close();
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
	}
	
	Vector<Integer> doStatisticsForStructs(Vector<String> smiles, String mdlFile, int nUsedStr)
	{	
		System.out.print(" queries ..." );
		Vector<QueryAtomContainer> queries = new Vector<QueryAtomContainer> ();
		int n = smiles.size();
		int frequency[] = new int[n];
		for (int i = 0; i < n; i++)
		{
			QueryAtomContainer q = parser.parse(smiles.get(i));
			queries.add(q);
			frequency[i] = 0;
		}
		System.out.println(" done" );
		
		try
		{
			DefaultChemObjectBuilder b = DefaultChemObjectBuilder.getInstance();
			MyIteratingMDLReader reader = new MyIteratingMDLReader(new FileReader(mdlFile),b);
			int record=0;
			boolean res;

			while (reader.hasNext()) 
			{	
				record++;
				if (record % 10 == 0)
					System.out.println("  searched records " + record);
				
				if (record > nUsedStr)
					break;
				Object o = reader.next();
				if (o instanceof IAtomContainer) 
				{
					IAtomContainer mol = (IAtomContainer)o;
					if (mol.getAtomCount() == 0) continue;
					AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
					CDKHueckelAromaticityDetector.detectAromaticity(mol);
					//The target is not "prepared" since the queries are not complicated expressions
					//parser.prepareTargetForSMARTSSearch
					//System.out.println("  $ record " + record+ "  " + SmartsHelper.moleculeToSMILES(mol));
										
					for (int i = 0; i < n; i++)
					{						
						res = UniversalIsomorphismTester.isSubgraph(mol, queries.get(i));						
						//isoTester.setQuery(queries.get(i));						
						//res = isoTester.hasIsomorphism(mol);						
						if (res)
							frequency[i]++;
					}
					
				}
			}	
		}
		catch(Exception e){
			System.out.println("**** " + e.toString());
		}
		
		
		Vector<Integer> stat = new Vector<Integer>();
		for (int i = 0; i < n; i++)
			stat.add(new Integer(frequency[i]));
		
		return(stat);
	}
	
}
