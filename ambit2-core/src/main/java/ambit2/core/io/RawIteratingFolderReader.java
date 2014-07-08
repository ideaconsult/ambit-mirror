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

import org.openscience.cdk.index.CASNumber;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.interfaces.ICiteable;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.base.processors.CASProcessor;
import ambit2.core.config.AmbitCONSTANTS;
import ambit2.core.data.EINECS;

/*
 * Raw reader for multiple files in a folder
 */
public class RawIteratingFolderReader extends IteratingFolderReader<IStructureRecord,IRawReader<IStructureRecord>> implements IRawReader<IStructureRecord> {
	protected enum FileNameMode {
		NONE,
		CAS,
		EC,
		Name
	}
	protected FileNameMode mode;
	public FileNameMode getMode() {
		return mode;
	}
	public void setMode(FileNameMode mode) {
		this.mode = mode;
	}
	protected CASProcessor casTransformer = new CASProcessor();
	protected Property einecsProperty = Property.getEINECSInstance();
	protected Property casProperty = Property.getInstance(AmbitCONSTANTS.CASRN,LiteratureEntry.getCASReference());
	protected Property nameProperty = Property.getNameInstance();
	
	public RawIteratingFolderReader(File[] files) {
		super(files);
		casProperty.setLabel(AmbitCONSTANTS.CASRN);
	}
	@Override
	public void setFiles(File[] files) {
		super.setFiles(files);
		if (files ==null) return;
		casTransformer = new CASProcessor();
		int[] count = new int[FileNameMode.values().length];
		mode = FileNameMode.Name;
		for (File file: files) {
			String name = file.getName().toLowerCase();
			int i5d = name.indexOf(".i5d");
			if (i5d>0) {
				mode = FileNameMode.NONE;
				return;
			}
			int dot = name.indexOf('.');
			if (dot >= 0) {
				String id = name.substring(0,dot);
				try {
					String cas = casTransformer.process(id);
					if (CASNumber.isValid(cas)) {
						count[FileNameMode.CAS.ordinal()]++;
						continue;
					}
				} catch (Exception x) {}
				try {
					if (EINECS.isValid(id)) { 
						count[FileNameMode.EC.ordinal()]++;
						continue;
					}
				} catch (Exception x) {}	
				if (FileNameMode.NONE.equals(mode))
					count[FileNameMode.NONE.ordinal()]++;
				else 
					count[FileNameMode.Name.ordinal()]++;
			}
		}
		mode = FileNameMode.NONE;
		for (FileNameMode m : FileNameMode.values()) 
			if (count[m.ordinal()]>count[mode.ordinal()])
				mode = m;
	}
	@Override
	public Object next() {
		Object o = super.next();
		if (o instanceof IStructureRecord)
			switch (mode) {
			case CAS: {assignCASRN((IStructureRecord)o);break;}
			case EC: {assignEINECS((IStructureRecord)o);break;}
			case Name: {assignName((IStructureRecord)o); break;}
			default: {
				
			}
			}		
		return o;
	}
	public IStructureRecord nextRecord() {
		if (reader == null) return null;
		IStructureRecord record = reader.nextRecord();
		switch (mode) {
		case CAS: {assignCASRN(record);break;}
		case EC: {assignEINECS(record);break;}
		case Name:{assignName(record); break;} 
		default: {
		}
		}
		return record;
	}
	//does file name contain CAS number? 
	protected void assignCASRN(IStructureRecord record) {
		if (record.getProperty(casProperty)!=null)
			record.removeProperty(casProperty);
		int dot = files[index].getName().indexOf('.');
		if (dot >= 0) {
			String cas = files[index].getName().substring(0,dot);
			try {
				cas = casTransformer.process(cas);
				if (CASProcessor.isValidFormat(cas)) 
					record.setProperty(casProperty, cas);
				else
					record.setProperty(nameProperty, files[index].getName().substring(0,dot));
			} catch (Exception x) {
				
			}
		}		
	}
	
	protected void assignEINECS(IStructureRecord record) {
		
		if (record.getProperty(einecsProperty)!=null)
			record.removeProperty(einecsProperty);
		int dot = files[index].getName().indexOf('.');
		if (dot >= 0) {
			String ec = files[index].getName().substring(0,dot);
			if (EINECS.isValidFormat(ec)) 
				record.setProperty(einecsProperty, ec);
			else 
				record.setProperty(nameProperty, ec);
		}		
	}	
	
	protected void assignName(IStructureRecord record) {
		
		if (record.getProperty(nameProperty)!=null)
			record.removeProperty(nameProperty);
		int dot = files[index].getName().indexOf('.');
		if (dot >= 0) {
			String ec = files[index].getName().substring(0,dot);
			record.setProperty(nameProperty, ec);
		} else record.setProperty(nameProperty,files[index].getName());
	}		
	protected IRawReader<IStructureRecord> getItemReader(int index) throws Exception {
		String name = files[index].getName().toLowerCase();
		if (name.endsWith(FileInputState.extensions[FileInputState.SDF_INDEX])) {
			RawIteratingSDFReader r = new RawIteratingSDFReader(new FileReader(files[index]));
			r.setReference(LiteratureEntry.getInstance(files[index].getName(),"file:///"+files[index].getAbsolutePath()));
			return (IRawReader<IStructureRecord>) r;
		} else if (name.endsWith(FileInputState.extensions[FileInputState.MOL_INDEX])) {
			RawIteratingMOLReader r = new RawIteratingMOLReader(new FileReader(files[index]));
			r.setReference(LiteratureEntry.getInstance(files[index].getName(),"file:///"+files[index].getAbsolutePath()));
			return (IRawReader<IStructureRecord>) r;
		} else if (name.endsWith(FileInputState.extensions[FileInputState.I5D_INDEX])) {
			IIteratingChemObjectReader r = FileInputState.getI5DReader(files[index]);
			if (r instanceof ICiteable) {
				((ICiteable)r).setReference(LiteratureEntry.getI5UUIDReference());
			}
			return (IRawReader<IStructureRecord>) r;
		}
			
		else throw new Exception("Unsupported format "+name); 
	}

}
