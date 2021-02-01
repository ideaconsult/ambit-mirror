package ambit2.groupcontribution.nmr.nmr_1h;

import java.text.DecimalFormat;

public class HShift 
{
	public double value = 0.0;
	public int binNumber = 0;
	public int atomIndex = 0; //It might be explicit H atom index or a heavy atom index with implicit H atoms
	public int imlicitHAtomsNumbers = 0;
	public SpinSplitting spinSplit = null;
	public String explanationInfo = null;
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		DecimalFormat df = new DecimalFormat("###.###");
		sb.append(df.format(value));
		sb.append(" H" + imlicitHAtomsNumbers);
		sb.append(" atom " + (atomIndex + 1));
		
		if (spinSplit != null)
			sb.append(" " + spinSplit.toString());	
		
		sb.append("  " + explanationInfo);
		return sb.toString();
	}
	
	public String toString(boolean explanation)
	{
		StringBuffer sb = new StringBuffer();
		DecimalFormat df = new DecimalFormat("###.###");
		sb.append(df.format(value));
		sb.append(" H" + imlicitHAtomsNumbers);
		sb.append(" atom " + (atomIndex + 1));
		
		if (spinSplit != null)
			sb.append(" " + spinSplit.toString());	
		
		
		if (explanation)
			sb.append("  " + explanationInfo);
		return sb.toString();
	}
	
}
