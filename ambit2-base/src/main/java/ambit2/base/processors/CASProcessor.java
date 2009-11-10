package ambit2.base.processors;

import ambit2.base.exceptions.AmbitException;

/**
 * Transform a string to CAS number format XXXXX-XX-X  
 * @author nina
 *
 */
public class CASProcessor extends DefaultAmbitProcessor<String, String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 144610103301504232L;
	protected static final String zero="0";
	protected static final String dash="-";
	public String process(String target) throws AmbitException {
		target = target.trim();
		StringBuilder b = new StringBuilder();
		int index = 0;
		for (int i=0; i < target.length();i++) {
			if (target.substring(index,index+1).equals(zero)) 
				index++;
			else break;
		}
		
		if (target.indexOf(dash)>0) return target.substring(index);
		try {
			b.append(target.substring(index,target.length()-3));
			b.append(dash);
			b.append(target.substring(target.length()-3,target.length()-1));
			b.append(dash);
			b.append(target.substring(target.length()-1));
			return b.toString();
		} catch (Exception x) {
			return target.substring(index);
		}
	}
}
