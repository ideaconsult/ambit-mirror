package ambit2.tautomers;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.smarts.SmartsHelper;

public class TautomerRelationGraph 
{
	public static enum StructureFormat {
		SMILES, INCHI
	}
	
	public List<IAtomContainer> tautomers = new ArrayList<IAtomContainer>();
	public List<TautomerRelation> relations = new ArrayList<TautomerRelation>();
	
	
	public String getRelationGraphAsString(boolean flagAddTautomerList) throws Exception
	{
		return getRelationGraphAsString(flagAddTautomerList, StructureFormat.SMILES);
	}
	
	public String getRelationGraphAsString(boolean flagAddTautomerList, StructureFormat strFormat) throws Exception
	{	
		StringBuffer sb = new StringBuffer();
		if (flagAddTautomerList)
		{	
			sb.append("Tautomers:\n");
			for (int i = 0; i < tautomers.size(); i++)
			{
				switch (strFormat)
				{
				case SMILES:
					sb.append("  " + SmartsHelper.
							moleculeToSMILES(tautomers.get(i),false) + "\n");
					break;
				}
			}
		}
		
		sb.append("Relations:\n");
		for (int i = 0; i < relations.size(); i++)
			sb.append("  " + relations.get(i).toString() + "\n");
		
		return sb.toString();
	}
	
}
