package ambit2.groupcontribution.nmr.nmr_1h;

import java.util.ArrayList;
import java.util.List;

import ambit2.groupcontribution.nmr.Substituent;

public class HAtomEnvironment 
{
	public String name = null;
	public String info = "";
	public double chemShift0 = 0.0;
	public String substituentPositions[] = null;
	
	public List<Substituent> substituents = new ArrayList<Substituent>(); 
	
}
