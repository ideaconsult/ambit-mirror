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
package ambit2.base.processors.batch;

import java.util.Iterator;

import net.idea.modbcum.i.batch.DefaultBatchStatistics;
import net.idea.modbcum.i.batch.IBatchStatistics;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.processors.ProcessorsChain;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import ambit2.base.interfaces.IBatchProcessor;

public abstract class BatchProcessor<INPUT,Target> extends DefaultAmbitProcessor<INPUT,IBatchStatistics> 
					implements IBatchProcessor<INPUT,Target,IBatchStatistics> {
	public static String PROPERTY_BATCHSTATS="ambit2.core.processors.batch.IBatchStatistics";
	protected ProcessorsChain<Target,IBatchStatistics,IProcessor> processor;
	protected boolean cancelled = false;
	protected long timeout = 0;
	/**
	 * 
	 */
	private static final long serialVersionUID = -5659435501205598414L;
	public BatchProcessor() {
		this(null);
	}
	public BatchProcessor(ProcessorsChain<Target,IBatchStatistics,IProcessor> processor) {
		super();
		setProcessorChain(processor);
	}	
	public ProcessorsChain<Target, IBatchStatistics, IProcessor> getProcessorChain() {
		return processor;
	}
	public void setProcessorChain(
			ProcessorsChain<Target, IBatchStatistics, IProcessor> processor) {
		this.processor = processor;
		
	}
	protected abstract void closeIterator(Iterator iterator) throws AmbitException;
/*
	protected Iterator getIterator(IInputState target) throws AmbitException {
		return target.getReader(); 
	}
	
	protected void closeIterator(Iterator iterator) throws AmbitException {
		iterator.close();
	}
	*/	
	public IBatchStatistics process(INPUT target) throws AmbitException {
		try {
			DefaultBatchStatistics stats = new DefaultBatchStatistics();
			stats.setResultCaption("Read");
			stats.setFrequency(1);

			Iterator reader = getIterator(target);
			ProcessorsChain<Target,IBatchStatistics,IProcessor> processor = getProcessorChain();
			if (processor == null)
				throw new AmbitException("Processor not defined");
			long started = System.currentTimeMillis();
			while (reader.hasNext() && !cancelled) {
				if ((stats.getRecords(IBatchStatistics.RECORDS_STATS.RECORDS_READ) % stats.getFrequency())==0)
					propertyChangeSupport.firePropertyChange(PROPERTY_BATCHSTATS,null,stats);				
				Object object= null;
				long ms = System.currentTimeMillis();
				try {
					object = reader.next();
					stats.increment(IBatchStatistics.RECORDS_STATS.RECORDS_READ);
					stats.incrementTimeElapsed(IBatchStatistics.RECORDS_STATS.RECORDS_READ, System.currentTimeMillis()-ms);
				} catch (Exception x) {
					stats.increment(IBatchStatistics.RECORDS_STATS.RECORDS_ERROR);
					stats.incrementTimeElapsed(IBatchStatistics.RECORDS_STATS.RECORDS_ERROR, System.currentTimeMillis()-ms);					
					continue;					
				}
				ms = System.currentTimeMillis();
				try {
					processor.process((Target)object);
					stats.increment(IBatchStatistics.RECORDS_STATS.RECORDS_PROCESSED);
					stats.incrementTimeElapsed(IBatchStatistics.RECORDS_STATS.RECORDS_PROCESSED, System.currentTimeMillis()-ms);
				} catch (Exception x) {
					stats.increment(IBatchStatistics.RECORDS_STATS.RECORDS_ERROR);
					stats.incrementTimeElapsed(IBatchStatistics.RECORDS_STATS.RECORDS_ERROR, System.currentTimeMillis()-ms);					
					continue;
				}				
				long elapsed = System.currentTimeMillis()-started;
				if ((timeout>0)&&(elapsed>timeout)) {
					stats.increment(IBatchStatistics.RECORDS_STATS.RECORDS_ERROR);
					stats.incrementTimeElapsed(IBatchStatistics.RECORDS_STATS.RECORDS_ERROR, System.currentTimeMillis()-ms);
					break;
					
				}
			}
			
			propertyChangeSupport.firePropertyChange(PROPERTY_BATCHSTATS,null,stats);
			closeIterator(reader);
			
			return stats;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}
	public void cancel() {
		this.cancelled = true;
		
	}
	public void setTimeout(long timeout) {
		this.timeout = timeout;
		
	}
	public long getTimeout() {
		return timeout;
	}

}
