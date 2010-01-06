/* EINECS.java
 * Author: nina
 * Date: Mar 21, 2009
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2009  Ideaconsult Ltd.
 * 
 * Contact: nina
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit2.core.data;

public class EINECS {
	protected static final int[] index = {0,1,2,4,5,6,8};
	protected static final int[] w = 	 {1,2,3,4,5,6};
	protected EINECS() {
		
	}
	/**
	 * 200-002-3
	 * 200-006-5
	 * EINECS:abc-def-g
	 * <br>
	 * g=(a+2b+3c+4d+5e+6f)mod11 
	 * @param einecs
	 * @return
	 */
	public static boolean isValid(String einecs) {
		try {
			if (einecs.indexOf("-")<=0) return false;
			if (einecs.length()==9) {
				int g = 0;
				for (int i=0; i < w.length; i++) {
					g += Integer.parseInt(einecs.substring(index[i],index[i]+1))*w[i];
				}
				
				g = g % 11;
				if (g==10) return false;
				return Integer.parseInt(einecs.substring(8,9)) == g;
			} else return false;
		} catch (Exception x) {
			return false;
		}
	}
	public static boolean isValidFormat(String einecs) {
		try {
			if (einecs.indexOf("-")<=0) return false;
			if (einecs.length()==9) {
				for (int i=0; i < w.length; i++) 
					Integer.parseInt(einecs.substring(index[i],index[i]+1));
				
				return
				einecs.substring(3,4).equals("-") && einecs.substring(7,8).equals("-");

			} else return false;
		} catch (Exception x) {
			return false;
		}
	}
}
