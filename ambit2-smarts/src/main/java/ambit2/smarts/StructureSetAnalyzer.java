package ambit2.smarts;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;


public class StructureSetAnalyzer 
{
	public class CharStructInfo 
	{
		public int atomCount;
		public int bondCount;
		public String smiles;		
		public int frequency;
		public double SQI;   //Structure Quality Index
	};
	
	
	
	public List<IAtomContainer> structures = new ArrayList<IAtomContainer>();
	public double factor = 0.50; //This is the weight of the frequency
	public List<CharStructInfo> charStructInfo = new ArrayList<CharStructInfo>();
	public int hitListSize = 100;
	public int maxSizeOfSequence = 0; //by default no restriction
	public int maxHitStructSize = 0;  //by default no restriction
	
	//Utility objects
	SmartsParser sp = new SmartsParser();
	IsomorphismTester isoTester = new IsomorphismTester();
	ChemObjectToSmiles cots = new ChemObjectToSmiles();
	ChemObjectFactory cof ;
	SmartsToChemObject stco ;
		
	int maxStructSize;
	double minSQI;
	int minSQIPos;
	
	public StructureSetAnalyzer(IChemObjectBuilder builder) {
		super();
		stco = new SmartsToChemObject(builder);
		cof = new ChemObjectFactory(builder);
	}
	void setMaxStructSize()
	{
		maxStructSize = 0;
		for (int i = 0; i < structures.size(); i++)
		{	
			int curSize = structures.get(i).getAtomCount();
			if (maxStructSize < curSize)
				maxStructSize = curSize;
		}
	}
	
	
	public void mcsAnalysis()
	{
		charStructInfo.clear();
		for (int i = 0; i < structures.size()-1; i++)
			for (int j = i+1; j < structures.size(); j++)
			{
				//TODO
				//UniversalIsomorphismTester.
			}
	}
	
	public void stochasticAnalysis()
	{	
		minSQI = 0;
		minSQIPos = -1;
		charStructInfo.clear();
		for (int i = 0; i < structures.size(); i++)
		{	
			System.out.println("Processing str. " + (i+1) + 
					"       nAtoms = " + structures.get(i).getAtomCount());
			processStructureStochasticalty(structures.get(i));
		}	
	}
	
	
	void processStructureStochasticalty(IAtomContainer mol)
	{	
		for (int k = 0; k < mol.getAtomCount(); k++)
		{	
			cof.setAtomSequence(mol, mol.getAtom(k));
			int n = cof.sequence.size();
			
			if (maxSizeOfSequence > 0)  //Restriction according to the sequence size
			{	
				if (n > maxSizeOfSequence)
					n = maxSizeOfSequence;
			}	
			
			for (int i = 0; i < n; i++)
			{
				IAtomContainer struct = cof.getFragmentFromSequence(i);
				
				if(maxHitStructSize > 0) //Restriction according to the Hit Str. size
				{
					if (struct.getAtomCount() > maxHitStructSize)
						break;
				}
				
				String smiles = cots.getSMILES(struct);
				
				if (!checkForDuplication(smiles))
					registerNewStruct(struct,smiles);
			}
		}
	}
	
	boolean checkForDuplication(String smarts)
	{	
		IQueryAtomContainer query  = sp.parse(smarts);
		sp.setNeededDataFlags();
		isoTester.setQuery(query);						
				
		for (int i = 0; i < charStructInfo.size(); i++)
		{
			CharStructInfo s = charStructInfo.get(i);
			if (query.getAtomCount() == s.atomCount)
				if (query.getBondCount() == s.bondCount)
				{					
					if (smarts.equals(s.smiles))
						return(true);
					
					IQueryAtomContainer q = sp.parse(s.smiles);
					IAtomContainer ac = stco.extractAtomContainer(q,null);
					
					boolean isoRes = isoTester.hasIsomorphism(ac);
					if (isoRes)
						return(true);
				}
		}
		
		return(false);
	}
	
	
	void registerNewStruct(IAtomContainer struct, String smiles)
	{
		int freq = getFrequency(smiles);
		double SQI = getSQI(freq, struct.getAtomCount());
		
		
		if (charStructInfo.size() < hitListSize)
		{
			CharStructInfo csi = new CharStructInfo();
			csi.atomCount = struct.getAtomCount();
			csi.bondCount = struct.getBondCount();
			csi.frequency = freq;
			csi.smiles = smiles;
			csi.SQI = SQI;
			charStructInfo.add(csi);
			
			if (minSQIPos == -1)  //This is only for the first added struct.
			{	
				minSQIPos = 0;
				minSQI = SQI;
			}
			else
			{
				if (minSQI > SQI)
				{
					minSQIPos = charStructInfo.size()-1;
					minSQI = SQI;
				}
			}
				
		}
		else  //Structure set is full. The worst SQI could be replaced.
		{
			if (minSQI < SQI)  
			{	
				//Removing the worst struct
				charStructInfo.remove(minSQIPos);
				
				//Adding the new struct
				CharStructInfo csi = new CharStructInfo();
				csi.atomCount = struct.getAtomCount();
				csi.bondCount = struct.getBondCount();
				csi.frequency = freq;
				csi.smiles = smiles;
				csi.SQI = SQI;
				charStructInfo.add(csi);
				
				setMinSQI();
			}
		}
	}
	
	double getSQI(int frequency, int strSize)
	{
		return( factor*frequency/structures.size() + (1-factor)*strSize/maxStructSize);
	}
	
	
	int getFrequency(String smiles)
	{
		int freq = 0;
		IQueryAtomContainer query  = sp.parse(smiles);
		sp.setNeededDataFlags();
		isoTester.setQuery(query);
		
		for (int i = 0; i < structures.size(); i++)
		{
			IAtomContainer ac = structures.get(i); 
			boolean isoRes = isoTester.hasIsomorphism(ac);
			if (isoRes)
				freq++;
		}
		
		return (freq);
	}
	
	
	void setMinSQI()
	{
		minSQI = charStructInfo.get(0).SQI;
		minSQIPos = 0;
		for (int i = 1; i < charStructInfo.size(); i++)
			if (minSQI > charStructInfo.get(i).SQI)
			{
				minSQI = charStructInfo.get(i).SQI;
				minSQIPos = i;
			}
	}
	
	
}
