package ambit2.smarts;

import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import ambit2.core.data.MoleculeTools;
import ambit2.core.io.MyIteratingMDLReader;

/** Utilities for generation of different types of 
 * chemical objects: molecules, fragments, queries */
public class ChemObjectFactory 
{	
	public List<SequenceElement> sequence = new ArrayList<SequenceElement>();
	List<IAtom> sequencedAtoms = new ArrayList<IAtom>();
	List<IAtom> sequencedBondAt1 = new ArrayList<IAtom>();
	List<IAtom> sequencedBondAt2 = new ArrayList<IAtom>();
	//MoleculeAndAtomsHashing molHash = new MoleculeAndAtomsHashing();
	SmartsManager man;
	SmartsParser parser = new SmartsParser();
	SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
	SmartsToChemObject stco ; 
	IsomorphismTester isoTester = new IsomorphismTester();
	IChemObjectBuilder builder;
	
	public ChemObjectFactory(IChemObjectBuilder builder) {
		super();
		setBuilder(builder);
		stco = new SmartsToChemObject(builder); 
		man = new SmartsManager(builder);
	}
	
	public IChemObjectBuilder getBuilder() {
		return builder;
	}

	public void setBuilder(IChemObjectBuilder builder) {
		this.builder = builder;
	}

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
	
	
	boolean containsAtom(List<IAtom> v, IAtom atom)
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
		IAtomContainer mol = builder.newInstance(IAtomContainer.class);
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
		IAtom copyAtom = builder.newInstance(IAtom.class,atom.getSymbol());
		
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
		IBond b = MoleculeTools.newBond(mol.getBuilder());
		b.setAtoms(atoms);
		b.setOrder(order);
		if (isAromatic)
			b.setFlag(CDKConstants.ISAROMATIC, true);
		mol.addBond(b);
	}
	
	
	//This function generates a Carbon skeleton from a query atom sequence
	//It is used mainly for testing purposes
	public IAtomContainer getCarbonSkelleton(List<QuerySequenceElement> sequence)
	{
		IAtomContainer mol = builder.newInstance(IAtomContainer.class);
		HashMap<IAtom,IAtom> m = new HashMap<IAtom,IAtom>();		
		QuerySequenceElement el;
		
		//Processing first sequence element
		el = sequence.get(0);
		IAtom a0 = builder.newInstance(IAtom.class,"C");		
		mol.addAtom(a0);
		m.put(el.center, a0);
		
		for (int k = 0; k < el.atoms.length; k++)
		{
			IAtom a = builder.newInstance(IAtom.class,"C");
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
					IAtom a = builder.newInstance(IAtom.class,"C");
					mol.addAtom(a);
					m.put(el.atoms[k],a);
					addSkelletonBond(mol,m.get(el.center),a);
				}
			}
		}
		
		return(mol);
	}
	
	
	void addSkelletonBond(IAtomContainer mol, IAtom at1, IAtom at2)
	{
		IAtom[] atoms = new IAtom[2];
		atoms[0] = at1;
		atoms[1] = at2;		
		IBond b = builder.newInstance(IBond.class);
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
	
		
	
	public void produceStructuresExhaustively (IAtomContainer mol, List<StructInfo> vStr, 
				int maxNumSeqSteps, int maxStrSize) throws Exception
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
				if (struct.getAtomCount() > maxStrSize)
					break;
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
	
	public void produceStructuresRandomly(IAtomContainer mol, List<StructInfo> vStr, 
			int maxNumSeqSteps, int numStructs, int minGenStrSize)
	{	
		Random random = new Random();
		
		ChemObjectToSmiles cots = new ChemObjectToSmiles();
		for (int k = 0; k < numStructs; k++)
		{
			int atNum = random.nextInt(mol.getAtomCount());
			setAtomSequence(mol, mol.getAtom(atNum));
			int n = sequence.size()-1;
			if (n > maxNumSeqSteps)
				n = maxNumSeqSteps;			
			
			int numSteps = n;
			if (n > 1)
				numSteps = 1 + random.nextInt(n-1);			
			
			IAtomContainer struct = getFragmentFromSequence(numSteps);				
			if (struct.getAtomCount() < minGenStrSize)
				continue;
			
			String smiles = cots.getSMILES(struct);
			
			StructInfo strInfo = new StructInfo();
			strInfo.smiles = smiles;					
			strInfo.atomCount = struct.getAtomCount();
			strInfo.bondCount = struct.getBondCount();
			vStr.add(strInfo);
			
			if (mol.getAtomCount() < 15)
				break; //For smaller molecules only one structure is produced 
		}	
			
	}
	
	
	boolean checkForDuplication(String smarts, List<StructInfo> vStr) throws Exception
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
					
					IQueryAtomContainer q = parser.parse(s.smiles);
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
	
	public void produceStructsFromMDL(String mdlFile, int maxNumSeqSteps, int maxNumRecord, int maxStrSize,
				List<StructInfo> vStr, String outFile) throws Exception
	{	

			MyIteratingMDLReader reader = new MyIteratingMDLReader(new FileReader(mdlFile),builder);
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
					produceStructuresExhaustively(mol, vStr, maxNumSeqSteps, maxStrSize);
					System.out.println("record " + record+ "  " + vStr.size());
				}
			}	

		
		saveStructs(vStr,outFile);
	}
	
	public void produceRandomStructsFromMDL(String mdlFile, int maxNumSeqSteps, int minGenStrSize,  int maxNumRecord,
			List<StructInfo> vStr, String outFile) throws Exception
	{	

			MyIteratingMDLReader reader = new MyIteratingMDLReader(new FileReader(mdlFile),builder);
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
					
					produceStructuresRandomly(mol, vStr, maxNumSeqSteps, 4, minGenStrSize);					
					System.out.println("record " + record+ "  " + vStr.size());
				}
			}	


		saveStructs(vStr,outFile);
	}
	
	void saveStructs(List<StructInfo> vStr, String fName) throws Exception
	{
			File file = new File(fName);
			RandomAccessFile f = new RandomAccessFile(file,"rw");
			f.setLength(0);
			for (int i = 0; i<vStr.size(); i++)
				f.write((vStr.get(i).smiles + "\r\n").getBytes());
			f.close();

	}
	
	void saveStructStatistics(List<String> smiles, List<Integer> stat, String fName) throws Exception
	{
			File file = new File(fName);
			RandomAccessFile f = new RandomAccessFile(file,"rw");
			f.setLength(0);
			for (int i = 0; i<smiles.size(); i++)
				f.write((stat.get(i).toString()+"  "+smiles.get(i) + "\r\n").getBytes());
			f.close();

	}
	
	
	public void performStructureStatistics(String smilesFile, String mdlFile, 
				int portionSize, int nUsedStr, String outFile) throws Exception
	{
		//Each structure from smilesFile is searched against mdlFile
		//The process is done in portions.
			File file = new File(smilesFile);
			RandomAccessFile f = new RandomAccessFile(file,"r");			
			long length = f.length();
			
			List<String> smiles = new ArrayList<String>();
			List<String> allSmiles = new ArrayList<String>();
			List<Integer> allStat = new ArrayList<Integer>();
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
					List<Integer> stat = doStatisticsForStructs(smiles, mdlFile, nUsedStr);
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
				List<Integer> stat = doStatisticsForStructs(smiles, mdlFile, nUsedStr);
				for (int i = 0; i < smiles.size(); i++)
				{
					allSmiles.add(smiles.get(i));
					allStat.add(stat.get(i));
				}			
				saveStructStatistics(allSmiles, allStat, outFile);
			}
			f.close();

	}
	
	List<Integer> doStatisticsForStructs(List<String> smiles, String mdlFile, int nUsedStr) throws Exception
	{	
		System.out.print(" queries ..." );
		List<IQueryAtomContainer> queries = new ArrayList<IQueryAtomContainer> ();
		int n = smiles.size();
		int frequency[] = new int[n];
		for (int i = 0; i < n; i++)
		{
			IQueryAtomContainer q = parser.parse(smiles.get(i));
			queries.add(q);
			frequency[i] = 0;
		}
		System.out.println(" done" );
		
		MyIteratingMDLReader reader = null;
		try
		{
			reader = new MyIteratingMDLReader(new FileReader(mdlFile),builder);
			int record=0;
			boolean res;

			while (reader.hasNext()) 
			{	
				record++;
				if (record % 200 == 0)
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
			throw e;
			
		} finally {
			try { reader.close();} catch (Exception x) {}
		}	
		
		
		List<Integer> stat = new ArrayList<Integer>();
		for (int i = 0; i < n; i++)
			stat.add(new Integer(frequency[i]));
		
		return(stat);
	}
	
	//----------------- Utilities for creation of artificial molecules ------------------------------
	
	public IAtomContainer connectStructures(IAtomContainer str1,int numAt1, 
			IAtomContainer str2, int numAt2, IBond.Order order) throws Exception
	{	
		IAtom at1 = null;
		IAtom at2 = null;
		IAtomContainer mol = new AtomContainer();
		
		//clone str1
		for(int i = 0; i < str1.getAtomCount(); i++)
		{
			IAtom at = (IAtom)str1.getAtom(i).clone();
			mol.addAtom(at);
			if (numAt1 == i)
				at1 = at;
		}
		
		for(int i = 0; i < str1.getBondCount(); i++)
		{
			IBond bo = str1.getBond(i);
			int at0Num = str1.getAtomNumber(bo.getAtom(0));
			int at1Num = str1.getAtomNumber(bo.getAtom(1));
			IBond newBo = new Bond();
			newBo.setOrder(bo.getOrder());
			newBo.setAtom(mol.getAtom(at0Num), 0);
			newBo.setAtom(mol.getAtom(at1Num), 1);
			mol.addBond(newBo);
		}
		
		//clone str2
		for(int i = 0; i < str2.getAtomCount(); i++)
		{
			IAtom at = (IAtom)str2.getAtom(i).clone();
			mol.addAtom(at);
			if (numAt2 == i)
				at2 = at;
		}
		for(int i = 0; i < str2.getBondCount(); i++)
		{
			IBond bo = str2.getBond(i);
			int at0Num = str2.getAtomNumber(bo.getAtom(0));
			int at1Num = str2.getAtomNumber(bo.getAtom(1));
			IBond newBo = new Bond();
			newBo.setOrder(bo.getOrder());
			newBo.setAtom(mol.getAtom(at0Num + str1.getAtomCount()), 0);
			newBo.setAtom(mol.getAtom(at1Num + str1.getAtomCount()), 1);
			mol.addBond(newBo);
		}
		
		//Adding a new bond connecting bond
		IBond newBo = new Bond();
		newBo.setOrder(order);
		newBo.setAtom(at1, 0);
		newBo.setAtom(at2, 1);
		mol.addBond(newBo);

		return mol;
	}

	public IAtomContainer connectStructures(String smiles1, int numAt1, String smiles2, int numAt2, IBond.Order order) throws Exception 
	{
		IAtomContainer mol1 = smilesParser.parseSmiles(smiles1);
		IAtomContainer mol2 = smilesParser.parseSmiles(smiles2);

		return connectStructures(mol1, numAt1, mol2, numAt2, order);
	}


	public IAtomContainer connectStructures_Spiro(IAtomContainer str1,int numAt1, 
			IAtomContainer str2, int numAt2, IBond.Order order) throws Exception
	{
		//TODO
		return null;
	}
	
	
	public IAtomContainer condenseStructures(String smiles1,int str1At0, int str1At1, 
			String smiles2, int str2At0, int str2At1) throws Exception
	{
		IAtomContainer mol1 = smilesParser.parseSmiles(smiles1);
		IAtomContainer mol2 = smilesParser.parseSmiles(smiles2);

		return condenseStructures(mol1, str1At0, str1At1, mol2, str2At0, str2At1);
	}
	
	
	
	public IAtomContainer condenseStructures(IAtomContainer str1,int str1At0, int str1At1, 
			IAtomContainer str2, int str2At0, int str2At1) throws Exception
	{
		IAtom at0 = null;
		IAtom at1 = null;
		
		IAtomContainer mol = new AtomContainer();
		
		//clone str1
		for(int i = 0; i < str1.getAtomCount(); i++)
		{
			IAtom at = (IAtom)str1.getAtom(i).clone();
			mol.addAtom(at);
			if (str1At0 == i)
				at0 = at;

			if (str1At1 == i)
				at1 = at;
		}
		
		for(int i = 0; i < str1.getBondCount(); i++)
		{
			IBond bo = str1.getBond(i);
			int at0Num = str1.getAtomNumber(bo.getAtom(0));
			int at1Num = str1.getAtomNumber(bo.getAtom(1));
			IBond newBo = new Bond();
			newBo.setOrder(bo.getOrder());
			newBo.setAtom(mol.getAtom(at0Num), 0);
			newBo.setAtom(mol.getAtom(at1Num), 1);
			mol.addBond(newBo);
		}
		
		
		HashMap<IAtom,IAtom> map = new HashMap<IAtom,IAtom>(); 
		
		//clone str2
		for(int i = 0; i < str2.getAtomCount(); i++)
		{			
			IAtom at = str2.getAtom(i);
			if (str2At0 == i)
				map.put(at, at0);
			else
				if (str2At1 == i)
					map.put(at, at1);
				else
				{
					IAtom new_at = (IAtom)str2.getAtom(i).clone();
					mol.addAtom(new_at);
					map.put(at, new_at);
				}
		}
		
		for(int i = 0; i < str2.getBondCount(); i++)
		{
			IBond bo = str2.getBond(i);
			IAtom boAt0 = bo.getAtom(0);
			IAtom boAt1 = bo.getAtom(1);
			int at0Num = str2.getAtomNumber(boAt0);
			int at1Num = str2.getAtomNumber(boAt1);
			
			if ( ((at0Num == str2At0) && (at1Num == str2At1)) || 
				 ((at0Num == str2At1) && (at1Num == str2At0))    ) 
			{
				//This is the condensation bond
				continue;	
			}
			else
			{
				IBond newBo = new Bond();
				newBo.setOrder(bo.getOrder());
				newBo.setAtom(map.get(boAt0), 0);
				newBo.setAtom(map.get(boAt1), 1);
				mol.addBond(newBo);
			}
		}

		return mol;
	}
	
	//----------------- Some File Utilities  ------------------------------
	
	
	public ArrayList<String> loadSmilesStringsFromFile(String smilesFile) throws Exception
	{
		ArrayList<String> smiles = new ArrayList<String>();
		
		File file = new File(smilesFile);
		RandomAccessFile f = new RandomAccessFile(file,"r");			
		long length = f.length();
		int n = 0;
		
		while (f.getFilePointer() < length)
		{	
			n++;
			String line = f.readLine();
			String sm = line.trim();
			if (!sm.isEmpty())
				smiles.add(line);
		}
		
		f.close();
		
		return smiles;
	}
	
	public void saveSmilesToFile(ArrayList<String> smiles, String fName) throws Exception
	{
			File file = new File(fName);
			RandomAccessFile f = new RandomAccessFile(file,"rw");
			f.setLength(0);
			for (int i = 0; i < smiles.size(); i++)
				f.write((smiles.get(i) + "\r\n").getBytes());
			f.close();
	}
	
	
	
	public ArrayList<ArrayList<String>> loadSmilesTuplesFromFile(String smilesFile) throws Exception
	{
		//Each line gives a smiles set
		ArrayList<ArrayList<String>> smilesSets = new ArrayList<ArrayList<String>>();
		
		File file = new File(smilesFile);
		RandomAccessFile f = new RandomAccessFile(file,"r");			
		long length = f.length();
		int n = 0;
		
		while (f.getFilePointer() < length)
		{	
			n++;
			String line = f.readLine();
			String line1 = line.trim();
			ArrayList<String> tupels = getTuples(line1, " ");
			ArrayList<String> smiSet = new ArrayList<String>(); 
			
			for (int i = 0; i < tupels.size(); i++)
				smiSet.add(tupels.get(i));
			
			smilesSets.add(smiSet);
		}
		
		f.close();
		
		return smilesSets;
	}
	
	public void saveSmilesTuplesToFile(ArrayList<ArrayList<String>> smilesTuples, String fName) throws Exception
	{
			File file = new File(fName);
			RandomAccessFile f = new RandomAccessFile(file,"rw");
			f.setLength(0);
			
			for (int i = 0; i < smilesTuples.size(); i++)
			{
				ArrayList<String> tuple = smilesTuples.get(i);
				for (int k = 0; k < tuple.size(); k++)
				{
					f.write(tuple.get(k).getBytes());
					if (k < tuple.size()-1)
						f.write(" ".getBytes());
				}
				
				if (i < smilesTuples.size()-1)
					f.write("\r\n".getBytes());
			}	
			
			f.close();
	}
	
	ArrayList<String> getTuples(String line, String splitter)
	{
		ArrayList<String> tuples = new ArrayList<String>();
		String tokens[] = line.split(splitter);
		for (int i = 0; i < tokens.length; i++)
			if(!tokens[i].isEmpty())
				tuples.add(tokens[i]);
		return tuples;
	}
	
	
	public ArrayList<String> attachGroupToAllStructures(String groupSmiles, int groupAttachPos, 
					ArrayList<String> inputSmiles, int strDefaultPos) throws Exception
	{
		ArrayList<String> outSmiles = new ArrayList<String>();
		for (int i = 0; i < inputSmiles.size(); i++)
		{
			IAtomContainer ac = connectStructures(groupSmiles, groupAttachPos, inputSmiles.get(i), strDefaultPos, IBond.Order.SINGLE);
			String resSmiles = SmartsHelper.moleculeToSMILES(ac,true);  
			outSmiles.add(resSmiles);
		}
		
		return outSmiles;
	}
	
}
