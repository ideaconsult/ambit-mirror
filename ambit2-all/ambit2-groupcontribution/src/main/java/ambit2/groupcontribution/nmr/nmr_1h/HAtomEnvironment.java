package ambit2.groupcontribution.nmr.nmr_1h;

import java.util.ArrayList;
import java.util.List;

import ambit2.groupcontribution.nmr.Substituent;
import ambit2.smarts.groups.GroupMatch;

public class HAtomEnvironment 
{
	public String name = null;
	public String smarts = null;
	public String info = "";
	public double chemShift0 = 0.0;
	public String substituentPosDesignations[] = null;
	public int substituentPosAtomIndex[] = null;
		
	public List<Substituent> substituents = new ArrayList<Substituent>(); 
	
	public GroupMatch groupMatch = null;
}
