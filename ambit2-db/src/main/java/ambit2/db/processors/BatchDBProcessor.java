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

import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

import ambit2.base.data.LiteratureEntry;
import ambit2.base.exceptions.AmbitException;
import ambit2.base.exceptions.AmbitIOException;
import ambit2.base.interfaces.IBatchStatistics;
import ambit2.base.interfaces.IProcessor;
import ambit2.base.processors.ProcessorsChain;
import ambit2.core.io.FileInputState;
import ambit2.core.io.IInputState;
import ambit2.core.io.RawIteratingFolderReader;
import ambit2.core.io.RawIteratingSDFReader;

/**
 * Reads file 
 * @author nina
 *
 * @param <ItemOutput>
 */
public class BatchDBProcessor extends AbstractBatchProcessor<IInputState,String> 
						{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5659435501205598414L;
	public BatchDBProcessor() {
		// TODO Auto-generated constructor stub
	}
	public BatchDBProcessor(ProcessorsChain<String,IBatchStatistics,IProcessor> processor) {
		super(processor);

	}	
	public Iterator<String> getIterator(IInputState target)
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
					RawIteratingSDFReader reader = new RawIteratingSDFReader(
							new FileReader(file));
					reader.setReference(LiteratureEntry.getInstance(file.getName(),file.getAbsolutePath()));
					return reader;
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
			Iterator<String> iterator) throws AmbitException {
		
		try {
				if (iterator instanceof IIteratingChemObjectReader)
			((IIteratingChemObjectReader)iterator).close();
		} catch (Exception x) {
			throw new AmbitException(x);
		} finally {
			super.afterProcessing(target, iterator);
		}
		
	}
/*
	public IBatchStatistics process(IInputState target) throws AmbitException {
		try {
			DefaultBatchStatistics stats = new DefaultBatchStatistics();
			stats.setResultCaption("Read");
			long freq = 1;
			stats.setFrequency(freq);

			Iterator reader = getIterator(target);
			ProcessorsChain<Target,Result,IProcessor> processor = getProcessorChain();
			if (processor == null)
				throw new AmbitException("Processor not defined");
			while (reader.hasNext()) {
				freq = stats.getFrequency();
				if ((stats.getRecords(IBatchStatistics.RECORDS_READ) % freq)==0)
					propertyChangeSupport.firePropertyChange(PROPERTY_BATCHSTATS,null,stats);				
				Object object= null;
				long ms = System.currentTimeMillis();
				try {
					object = reader.next();
					stats.increment(IBatchStatistics.RECORDS_READ);
					stats.incrementTimeElapsed(IBatchStatistics.RECORDS_READ, System.currentTimeMillis()-ms);
				} catch (Exception x) {
					stats.increment(IBatchStatistics.RECORDS_ERROR);
					stats.incrementTimeElapsed(IBatchStatistics.RECORDS_ERROR, System.currentTimeMillis()-ms);					
					continue;					
				}
				ms = System.currentTimeMillis();
				try {
					processor.process((Target)object);
					stats.increment(IBatchStatistics.RECORDS_PROCESSED);
					stats.incrementTimeElapsed(IBatchStatistics.RECORDS_PROCESSED, System.currentTimeMillis()-ms);
				} catch (Exception x) {
					stats.increment(IBatchStatistics.RECORDS_ERROR);
					stats.incrementTimeElapsed(IBatchStatistics.RECORDS_ERROR, System.currentTimeMillis()-ms);					
					continue;
				}				
								
			}
			propertyChangeSupport.firePropertyChange(PROPERTY_BATCHSTATS,null,stats);
			closeIterator(reader);
			
			return stats;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	*/
	
}
