/*
Copyright (C) 2005-2006  

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

package ambit.io;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

public class MultiNewlineFilterReader extends FilterReader {

	public MultiNewlineFilterReader(Reader in) {
		super(in);
	}
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		//will be transformed to '\r'
		final char[][] lookup = {
				{'\r','\r','\n'},{'\r','\n','\r'}
				};
		
		int r = super.read(cbuf, off, len);
		
		if (r == -1) return r; //EOF

		int pos = 0;
		while (pos<r) {
			
			int shift = 2;
			for (int i=0; i < lookup.length;i++) {
				boolean found = false;
				for (int j=0; j < lookup[i].length; j++) 
					if (((off+pos+j)<r) && (lookup[i][j]==cbuf[off+pos+j])) { 
						found = true;
						shift = lookup[i].length-1;
					} else {found = false; break;}
				
				if (found) {
					int next = pos+shift;
					System.arraycopy(cbuf, next, cbuf, pos, r-next);  
					r = r -shift;
					break;
				}
				
			}	
			pos++;
		}	
		return r;
	}
}



