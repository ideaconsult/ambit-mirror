/*
Copyright (C) 2005-2008

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.smarts.query;

import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 * Creates any of SMARTS pattern classes.
 * @author Nina Jeliazkova nina@acad.bg
 *
 */
public class SmartsPatternFactory implements ISmartsPatternFactory {

	public static enum SmartsParser {
			smarts_joelib,
			smarts_cdk,
			smarts_nk,
			smarts_fast
	};
	protected SmartsParser parserType;

	public SmartsPatternFactory() {
		setParserType(SmartsParser.smarts_joelib);
	}
	
	public ISmartsPattern createSmartsPattern(String smarts, boolean negate) throws SMARTSException {
		return createSmartsPattern(parserType, smarts, negate);
	}

	public static ISmartsPattern createSmartsPattern(SmartsParser parser, String smarts,boolean negate) throws SMARTSException {

		switch (parser) {
		case smarts_cdk: return new SmartsPatternCDK(smarts,negate);
		case smarts_fast: return new FastSmartsMatcher(smarts,negate);
		default: return new SmartsPatternAmbit(smarts,negate,SilentChemObjectBuilder.getInstance()); 

		}
	}
	
	public SmartsParser getParserType() {
		return parserType;
	}
	public void setParserType(SmartsParser parserType) {
		this.parserType = parserType;
	}
	
}


