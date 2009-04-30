package ambit2.smarts;

import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.interfaces.IAtomContainer;
import java.util.Vector;


public class StructureSetAnalyzer 
{
	public class CharStructInfo 
	{
		public String smiles;		
		public int frequency;
		public double QI; 
	};
	
	
	public Vector<IAtomContainer> structures = new Vector<IAtomContainer>();
	public double factor = 0.50; //This is the weight of the frequency
	public Vector<CharStructInfo> charStructInfo = new Vector<CharStructInfo>();
	//public Vector<String> charStruct = new Vector<String>();
	
	
	
	public int maxStructSize;
	
	
	
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
		charStructInfo.clear();
		for (int i = 0; i < structures.size(); i++)
			processStructureStochasticalty(structures.get(i));
	}
	
	
	void processStructureStochasticalty(IAtomContainer container)
	{
		
	}
	
	
	
	
	
}
