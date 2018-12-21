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
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import org.apache.commons.csv.CSVFormat;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.exceptions.AmbitIOException;
import ambit2.core.io.FileInputState;
import ambit2.core.io.FileState._FILE_TYPE;
import ambit2.core.io.IInputState;
import ambit2.core.io.RawIteratingCSVReader;
import ambit2.core.io.RawIteratingFolderReader;
import ambit2.core.io.RawIteratingMOLReader;
import ambit2.core.io.RawIteratingSDFReader;
import ambit2.core.io.RawIteratingWrapper;
import net.idea.modbcum.i.IDBProcessor;
import net.idea.modbcum.i.batch.IBatchStatistics;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.processors.ProcessorsChain;
import net.idea.modbcum.p.batch.AbstractBatchProcessor;

/**
 * Reads file
 * 
 * @author nina
 * 
 * @param <ItemOutput>
 */
public class BatchDBProcessor<ITEMTYPE> extends AbstractBatchProcessor<IInputState, ITEMTYPE> {

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

	public BatchDBProcessor(ProcessorsChain<ITEMTYPE, IBatchStatistics, IProcessor> processor) {
		super(processor);

	}

	public Iterator<ITEMTYPE> getIterator(IInputState target) throws AmbitException {
		if (target instanceof FileInputState)
			try {
				File _file = ((FileInputState) target).getFile();
				if (_file.isDirectory()) {
					FilenameFilter filter = new FilenameFilter() {
						public boolean accept(File dir, String name) {
							return !name.startsWith(".");
						}
					};
					return new RawIteratingFolderReader(_file.listFiles(filter));
				} else {
					InputStream stream = null;
					String filename=_file.getName();
					if (filename.endsWith(_FILE_TYPE.GZ_INDEX.getExtension())) {
						String uncompressed = filename.replaceAll(_FILE_TYPE.GZ_INDEX.getExtension(), "");
						try {
							stream = new GZIPInputStream(new FileInputStream(_file));
							filename = uncompressed;
						} catch (IOException x) {
							throw new AmbitIOException(x);
						}
					} else 					
						stream = new FileInputStream(_file);
					
					if (FileInputState._FILE_TYPE.SDF_INDEX.hasExtension(filename)) {
						RawIteratingSDFReader reader = new RawIteratingSDFReader(new InputStreamReader(stream));
						if (getReference() == null)
							reader.setReference(LiteratureEntry.getInstance(filename, _file.getAbsolutePath()));
						else
							reader.setReference(getReference());
						return reader;
					} else if (FileInputState._FILE_TYPE.MOL_INDEX.hasExtension(filename)) {
						RawIteratingMOLReader reader = new RawIteratingMOLReader(new InputStreamReader(stream));
						if (getReference() == null)
							reader.setReference(LiteratureEntry.getInstance(filename, _file.getAbsolutePath()));
						else
							reader.setReference(getReference());
						return reader;
						/* TEST and replace the wrapper with this */
					} else if (FileInputState._FILE_TYPE.CSV_INDEX.hasExtension(filename)) {
						RawIteratingCSVReader reader = new RawIteratingCSVReader(new InputStreamReader(stream), CSVFormat.EXCEL);
						configureReader(reader, target, _file);
						return reader;
					} else if (FileInputState._FILE_TYPE.TXT_INDEX.hasExtension(filename)) {
						RawIteratingCSVReader reader = new RawIteratingCSVReader(new InputStreamReader(stream),
								CSVFormat.TDF.withCommentMarker('#'));
						configureReader(reader, target, _file);
						return reader;
					} else {
						IIteratingChemObjectReader ir = FileInputState.getReader(stream,
								filename);
						if (ir == null)
							throw new AmbitException("Unsupported format " + filename);
						else {

							if (ir instanceof RawIteratingCSVReader) {
								configureReader(((RawIteratingCSVReader)ir), target, _file);
							}
							RawIteratingWrapper reader = new RawIteratingWrapper(ir);
							
							if (getReference() == null)
								reader.setReference(
										LiteratureEntry.getInstance(filename, _file.getAbsolutePath()));
							else
								reader.setReference(getReference());
							return reader;
						}
					}
				}
			} catch (IOException x) {
				throw new AmbitIOException(x);
			} catch (Exception x) {
				throw new AmbitException(x);
			}
		else
			throw new AmbitException("Not a file");
	}

	protected void configureReader(RawIteratingCSVReader reader, IInputState target, File file) {
		reader.setKeeprawrecord(false);
		reader.setOptionalSMILESHeader(((FileInputState) target).getOptionalSMILESHeader());
		reader.setOptionalInChIHeader(((FileInputState) target).getOptionalInChIHeader());
		reader.setOptionalInChIKeyHeader(((FileInputState) target).getOptionalInChIKeyHeader());
		if (getReference() == null)
			reader.setReference(LiteratureEntry.getInstance(file.getName(), file.getAbsolutePath()));
		else
			reader.setReference(getReference());
	}
	@Override
	public void afterProcessing(IInputState target, Iterator<ITEMTYPE> iterator) throws AmbitException {

		try {
			if (iterator instanceof IIteratingChemObjectReader)
				((IIteratingChemObjectReader) iterator).close();
			else if (iterator instanceof IDBProcessor)
				((IDBProcessor) iterator).close();
		} catch (Exception x) {
			throw new AmbitException(x);
		} finally {
			super.afterProcessing(target, iterator);
		}

	}

}
