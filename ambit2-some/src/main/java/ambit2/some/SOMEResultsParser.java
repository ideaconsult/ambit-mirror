package ambit2.some;

import java.io.BufferedReader;
import java.io.StringReader;

import ambit2.some.SOMERawReader.someindex;

/**
 * Parses SOME text results. Override process method to do something more sensible.
 * @author nina
 *
 */
public class SOMEResultsParser {

	public void parseRecord(String some) throws Exception {
		BufferedReader reader = new BufferedReader(new StringReader(some));
		String line = null;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("@SOME")) {
				line = reader.readLine(); //skip next line as well
				continue;
			}
			String[] tokens = line.trim().split(" +");
			int atomNum = Integer.parseInt(tokens[someindex.atomIndex.ordinal()]);
			String atomSymbol = tokens[someindex.atomType.ordinal()];

			//if (!structure.getAtom(atomNum).getSymbol().equals(atomSymbol)) throw new Exception("Mismatch of atoms in some and structure"); 
				
			for (int i = someindex.aliphaticHydroxylation.ordinal() ; i <= someindex.SOxidation.ordinal(); i ++) {
				if (tokens[i].endsWith("*")) {
					double value = Double.parseDouble(tokens[i].substring(0,tokens[i].length()-1));
					process(atomNum,atomSymbol,someindex.values()[i],value,true);
				} else {
					double value = Double.parseDouble(tokens[i]);
					if (value > 0)
						process(atomNum,atomSymbol,someindex.values()[i],value,false);
				}
			}

		}
	}
	/**
	 * 
	 * @param atomNum
	 * @param atomSymbol
	 * @param index
	 * @param value
	 */
	protected void process(int atomNum,String atomSymbol, someindex index, double value,boolean star) {
		System.out.println(atomNum + " " + atomSymbol + " " + value);
	}
}
