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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;

import ambit2.core.exceptions.AmbitException;
import ambit2.core.io.IInputState;
import ambit2.core.processors.IProcessor;
import ambit2.core.processors.batch.DefaultBatchStatistics;
import ambit2.core.processors.batch.IBatchProcessor;
import ambit2.core.processors.batch.IBatchStatistics;
import ambit2.db.AbstractDBProcessor;
import ambit2.db.IDBProcessor;
import ambit2.db.SessionID;
import ambit2.db.exceptions.DbAmbitException;

public abstract class BatchDBProcessor<Target,Result> extends AbstractDBProcessor<IInputState,IBatchStatistics> implements IBatchProcessor<Target,Result> {
	public static String PROPERTY_BATCHSTATS="ambit2.core.processors.batch.IBatchStatistics";
	protected IDBProcessor<Target,Result> processor;
	/**
	 * 
	 */
	private static final long serialVersionUID = -5659435501205598414L;
	public BatchDBProcessor() {
		this(null);
	}
	public BatchDBProcessor(IProcessor<Target,Result> processor) {
		super();
		setProcessor(processor);
	}	
	public void open() throws DbAmbitException {
		processor.open();
		
	}
	public IProcessor<Target,Result> getProcessor() {
		return processor;
	}
	public void setProcessor(IProcessor<Target,Result> processor) {
		if (processor instanceof IDBProcessor)
			this.processor = (IDBProcessor<Target, Result>)processor;
	}
	protected abstract Iterator getIterator(IInputState target) throws AmbitException ;	
	protected abstract void closeIterator(Iterator iterator) throws AmbitException;

	public IBatchStatistics process(IInputState target) throws AmbitException {
		try {
			DefaultBatchStatistics stats = new DefaultBatchStatistics();
			stats.setResultCaption("Read");
			long freq = 1;
			stats.setFrequency(freq);

			Iterator reader = getIterator(target);
			IProcessor<Target,Result> processor = getProcessor();
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
	@Override
	public void setSession(SessionID sessionID) {
		super.setSession(sessionID);
		processor.setSession(sessionID);
	}
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		processor.setConnection(connection);
		
	}
	@Override
	public void close() throws SQLException {
		processor.close();
		super.close();
	}

}
