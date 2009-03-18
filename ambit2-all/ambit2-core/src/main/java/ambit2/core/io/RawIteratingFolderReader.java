/* RawIteratingFolderReader.java
 * Author: nina
 * Date: Mar 14, 2009
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

package ambit2.core.io;

import java.io.File;
import java.io.FileReader;
import java.util.Hashtable;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.index.CASNumber;

import ambit2.core.data.IStructureRecord;

/*
 * Raw reader for multiple files in a folder
 */
public class RawIteratingFolderReader extends IteratingFolderReader<IStructureRecord,IRawReader<IStructureRecord>> implements IRawReader<IStructureRecord> {

	public RawIteratingFolderReader(File[] files) {
		super(files);
	}

	@Override
	public Object next() {
		Object o = super.next();
		if (o instanceof IStructureRecord) {
			assignCASRN((IStructureRecord)o);
		}
		return o;
	}
	public IStructureRecord nextRecord() {
		if (reader == null) return null;
		IStructureRecord record = reader.nextRecord();
		assignCASRN(record);
		return record;
	}
	//does file name contain CAS number? 
	protected void assignCASRN(IStructureRecord record) {
		int dot = files[index].getName().indexOf('.');
		if (dot >= 0) {
			String cas = files[index].getName().substring(0,dot);
			if (CASNumber.isValid(cas)) {
				if (record.getProperties()==null)
					record.setProperties(new Hashtable());
				record.getProperties().put(CDKConstants.CASRN, cas);
			}
		}		
	}
	protected IRawReader<IStructureRecord> getItemReader(int index) throws Exception {
		String name = files[index].getName().toLowerCase();
		//System.out.println(index+ "\t"+name);
		if (name.endsWith(FileInputState.extensions[FileInputState.SDF_INDEX])) 
			return (IRawReader<IStructureRecord>)new RawIteratingSDFReader(new FileReader(files[index]));
		else throw new Exception("Unsupported format "+name); 
	}
}
