package ambit2.sln;

public class SLNHelper 
{
	
	static public String getAtomsAttributes(SLNContainer container)
	{
		StringBuffer sb = new StringBuffer();	
		
		for (int i = 0; i < container.getAtomCount(); i++)
		{	
			SLNAtom at = (SLNAtom)container.getAtom(i); 
			sb.append("  #" + i + "  ");
			sb.append(at.atomType);
			sb.append("\n");
		}	
		return(sb.toString());
	}
	
	static public String getBondsAttributes(SLNContainer container)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < container.getBondCount(); i++)
		{
			SLNBond bo = (SLNBond)container.getBond(i);  
			SLNAtom at0 = (SLNAtom)bo.getAtom(0);
			SLNAtom at1 = (SLNAtom)bo.getAtom(1);
			int at0_num = container.getAtomNumber(at0);
			int at1_num = container.getAtomNumber(at1);
			sb.append("  #" + i + " Atoms (" + at0_num + "," + at1_num + ")  Order = "  /*+ bo.bondType(bo)*/);

			sb.append("\n");
		}
		return(sb.toString());
	}
	
	public String toSLN(SLNContainer container)
	{
		return "";
	}
	
	
}
