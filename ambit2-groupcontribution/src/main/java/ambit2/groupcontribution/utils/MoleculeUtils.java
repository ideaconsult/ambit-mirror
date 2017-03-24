package ambit2.groupcontribution.utils;

import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

public class MoleculeUtils {

	public static int getExplicitHCount (IAtom at, IAtomContainer ac){
		int explicitHCount = 0;
			List<IAtom> neighbors = ac.getConnectedAtomsList(at);
			for (int i = 0; i < neighbors.size(); i++){
				if(neighbors.get(i).getSymbol().equals("H")){
					explicitHCount++;
				}
			}			
		return explicitHCount;
	}

	public static int getHCount (IAtom at, IAtomContainer ac){
		int totalHCount = at.getImplicitHydrogenCount() + getExplicitHCount(at, ac);
		return totalHCount;
	}

}
