package ambit2.tautomers.ranking;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtomContainer;

import ambit2.tautomers.CACTVSRanking;
import ambit2.tautomers.TautomerConst;
import ambit2.tautomers.TautomerConst.CanonicTautomerMethod;
import ambit2.tautomers.TautomerManager;

public class CanonicTautomer 
{
	protected TautomerManager tman = null;
	protected boolean FlagStoreCACTVSRank = false;
	
	public CanonicTautomer(TautomerManager tman){
		this.tman = tman;
	}
	
	public TautomerManager getTman() {
		return tman;
	}

	public void setTman(TautomerManager tman) {
		this.tman = tman;
	}
	
	public boolean isFlagStoreCACTVSRank() {
		return FlagStoreCACTVSRank;
	}

	public void setFlagStoreCACTVSRank(boolean flagStoreCACTVSRank) {
		FlagStoreCACTVSRank = flagStoreCACTVSRank;
	}
		
	public IAtomContainer getCanonicTautomer(List<IAtomContainer> tautomers) throws Exception
	{	
		if (tautomers.isEmpty())
			return null;
		
		if (tautomers.size() == 1)
			return tautomers.get(0);
		
		List<IAtomContainer> topTautomers = new ArrayList<IAtomContainer>();
		double topRank = 0.0;
			
		for (IAtomContainer t : tautomers)
		{	
			Double rank;

			if (tman.getCanonicTautomerMethod() == CanonicTautomerMethod.ENERGY_RANK_INCHI_KEY)				
				rank = (Double)t.getProperty(TautomerConst.TAUTOMER_RANK);
			else
			{	
				rank = CACTVSRanking.getEnergyRank(t);
				if (FlagStoreCACTVSRank)
					t.setProperty(TautomerConst.CACTVS_ENERGY_RANK, rank);
			}	

			if (rank == null)
				return null; 

			if (topTautomers.isEmpty())
			{
				topRank = rank;
				topTautomers.add(t);  //filling the topTautomers for the first time
				continue;
			}

			if (rank == topRank)
			{
				//another top rank tautomer 
				topTautomers.add(t); 
				continue;
			}

			if (rank < topRank)
			{
				//a new better top rank tautomer is found
				topTautomers.clear();
				topTautomers.add(t); 
				topRank = rank;
				continue;
			}

			//This is the case rank > topRank. Hence nothing is done
		}

		if (topTautomers.size() == 1)
			return (topTautomers.get(0));
		else
			return getMinInchiTautomer(topTautomers);
	}
	
	public IAtomContainer getMinInchiTautomer(List<IAtomContainer> tautomers) throws Exception
	{	
		if (tautomers.isEmpty())
			return null;
		
		if (tautomers.size() == 1)
			return tautomers.get(0);
		
		InChIGeneratorFactory igf = InChIGeneratorFactory.getInstance();
		
		IAtomContainer minTaut = tautomers.get(0);
		InChIGenerator ig0 = igf.getInChIGenerator(minTaut, tman.tautomerFilter.getInchiOptions());
		String minInchiKey = ig0.getInchiKey();
		
		for (int i = 1; i < tautomers.size(); i++)
		{
			InChIGenerator ig = igf.getInChIGenerator(tautomers.get(i), tman.tautomerFilter.getInchiOptions());
			String inchiKey = ig.getInchiKey();
			if (minInchiKey.compareTo(inchiKey) > 0)
				minInchiKey = inchiKey;
		}
		
		return minTaut;
	}
	
}
