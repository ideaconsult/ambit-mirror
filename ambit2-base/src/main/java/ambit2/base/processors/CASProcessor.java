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

	public String process(String target) throws AmbitException {
		target = target.trim();
		StringBuilder b = new StringBuilder();
		int index = 0;
		while (target.substring(index,index+1).equals("0")) {
			index++;
		}
		b.append(target.substring(index,target.length()-3));
		b.append('-');
		b.append(target.substring(target.length()-3,target.length()-1));
		b.append('-');
		b.append(target.substring(target.length()-1));
		return b.toString();
	}
}
