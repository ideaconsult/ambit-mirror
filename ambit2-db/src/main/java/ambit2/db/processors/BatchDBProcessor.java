/*
Copyright (C) 2007-2008  

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
package ambit2.db.processors;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Iterator;

import net.idea.modbcum.i.IDBProcessor;
import net.idea.modbcum.i.batch.IBatchStatistics;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.processors.ProcessorsChain;
import net.idea.modbcum.p.batch.AbstractBatchProcessor;

import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.exceptions.AmbitIOException;
import ambit2.core.io.FileInputState;
import ambit2.core.io.IInputState;
import ambit2.core.io.RawIteratingFolderReader;
import ambit2.core.io.RawIteratingMOLReader;
import ambit2.core.io.RawIteratingSDFReader;
import ambit2.core.io.RawIteratingWrapper;

/**
 * Reads file 
 * @author nina
 *
 * @param <ItemOutput>
 */
public class BatchDBProcessor<ITEMTYPE> extends AbstractBatchProcessor<IInputState,ITEMTYPE> 	{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5659435501205598414L;
	protected ILiteratureEntry reference;
	
	public ILiteratureEntry getReference() {
		return reference;
	}
	public void setReference(ILiteratureEntry reference) {
		this.reference = reference;
	}
	public BatchDBProcessor() {
	}
	public BatchDBProcessor(ProcessorsChain<ITEMTYPE,IBatchStatistics,IProcessor> processor) {
		super(processor);

	}	
	public Iterator<ITEMTYPE> getIterator(IInputState target)
			throws AmbitException {
		if (target instanceof FileInputState)
			try {
				File file = ((FileInputState)target).getFile();
				if (file.isDirectory()) {
					FilenameFilter filter = new FilenameFilter() {
					        public boolean accept(File dir, String name) {
					            return !name.startsWith(".");
					        }
					};					
					return new RawIteratingFolderReader(file.listFiles(filter));
				} else {
					if (file.getName().endsWith(FileInputState.extensions[FileInputState.SDF_INDEX])) {
						RawIteratingSDFReader reader = new RawIteratingSDFReader(
								new FileReader(file));
						if (getReference()==null)
							reader.setReference(LiteratureEntry.getInstance(file.getName(),file.getAbsolutePath()));
						else reader.setReference(getReference());
						return reader;
					} else if (file.getName().endsWith(FileInputState.extensions[FileInputState.MOL_INDEX])) {
						RawIteratingMOLReader reader = new RawIteratingMOLReader(new FileReader(file));
						if (getReference()==null)
							reader.setReference(LiteratureEntry.getInstance(file.getName(),file.getAbsolutePath()));
						else reader.setReference(getReference());
						return reader;
					} else {
						IIteratingChemObjectReader ir = FileInputState.getReader(file,((FileInputState)target).getFileFormat());
						if (ir == null) throw new AmbitException("Unsupported format "+file.getName());
						else {
							RawIteratingWrapper reader = new RawIteratingWrapper(ir);
							if (getReference()==null)
								reader.setReference(LiteratureEntry.getInstance(file.getName(),file.getAbsolutePath()));
							else reader.setReference(getReference());
							return reader;
						}
					} 
				}
			} catch (IOException x) {
				throw new AmbitIOException(x);
			} catch (Exception x) {
				throw new AmbitException(x);
			}
		else throw new AmbitException("Not a file");
	}
	@Override
	public void afterProcessing(IInputState target,
			Iterator<ITEMTYPE> iterator) throws AmbitException {
		
		try {
			if (iterator instanceof IIteratingChemObjectReader)
				((IIteratingChemObjectReader)iterator).close();
			else if(iterator instanceof IDBProcessor) 
				((IDBProcessor)iterator).close();
		} catch (Exception x) {
			throw new AmbitException(x);
		} finally {
			super.afterProcessing(target, iterator);
		}
		
	}
	
}
