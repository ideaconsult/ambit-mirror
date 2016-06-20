package ambit2.smarts;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.stereo.DoubleBondStereochemistry;
import org.openscience.cdk.stereo.ExtendedTetrahedral;
import org.openscience.cdk.stereo.TetrahedralChirality;

public class StereoChange 
{
	public static enum StereoElementType {
		STEREO_DB, TETHAHEDRAL_CHIRALITY, EXTENDED_CHIRALITY, UNKNOWN 
	}
	
	public StereoElementType elementType = StereoElementType.UNKNOWN;
	public List<IBond> addLigands0 = new ArrayList<IBond>(); 
	public List<IBond> addLigands1 = new ArrayList<IBond>(); 
	public boolean ligand0Deleted = false;
	public boolean ligand1Deleted = false;
	
	public void setStereoElementType(IStereoElement element)
	{
		if (element instanceof DoubleBondStereochemistry)
			elementType = StereoElementType.STEREO_DB;
		else
			if (element instanceof TetrahedralChirality)
				elementType = StereoElementType.TETHAHEDRAL_CHIRALITY;
			else
				if (element instanceof ExtendedTetrahedral)
					elementType = StereoElementType.EXTENDED_CHIRALITY;
				else
					elementType = StereoElementType.UNKNOWN;
	}
	
	public boolean isValidStereoElement()
	{
		if (ligand0Deleted)
			return false;
		if (ligand1Deleted)
			return false;
		
		return true;
	}
	
}
