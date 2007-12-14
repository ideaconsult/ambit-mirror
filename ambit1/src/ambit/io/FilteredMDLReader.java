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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.MDLReader;

public class FilteredMDLReader extends MDLReader {

	public FilteredMDLReader() {
		super();
	}

	public FilteredMDLReader(InputStream arg0) {
		this(new InputStreamReader(arg0));
	}

	public FilteredMDLReader(Reader arg0) {
		super(new MultiNewlineFilterReader(arg0));
	}
	@Override
	public void setReader(InputStream arg0) throws CDKException {
		super.setReader(new MultiNewlineFilterReader(new InputStreamReader(arg0)));
	}
	@Override
	public void setReader(Reader arg0) throws CDKException {
		super.setReader(new MultiNewlineFilterReader(arg0));
	}
}


