/* DBReader.java
 * Author: nina
 * Date: Feb 8, 2009
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

package ambit2.db.processors;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;

import ambit2.core.exceptions.AmbitException;
import ambit2.core.processors.IProcessor;
import ambit2.core.processors.ProcessorsChain;
import ambit2.core.processors.batch.DefaultBatchStatistics;
import ambit2.core.processors.batch.IBatchProcessor;
import ambit2.core.processors.batch.IBatchStatistics;
import ambit2.db.AbstractDBProcessor;
import ambit2.db.IDBProcessor;
import ambit2.db.SessionID;
import ambit2.db.exceptions.DbAmbitException;

/**
 * 
 * @author nina
 *
 */
public abstract class AbstractBatchProcessor<Target, ItemInput> extends 
					AbstractDBProcessor<Target, IBatchStatistics> 
					implements IBatchProcessor<Target, ItemInput,  IBatchStatistics> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6221299717393378599L;
	protected IBatchStatistics batchStatistics;
	public static String PROPERTY_BATCHSTATS="ambit2.core.processors.batch.IBatchStatistics";	
	protected long now = System.currentTimeMillis(); //ms
	protected ProcessorsChain<ItemInput,IBatchStatistics,IProcessor> processor;	

	public AbstractBatchProcessor() {
		// TODO Auto-generated constructor stub
	}
	public AbstractBatchProcessor(ProcessorsChain<ItemInput,IBatchStatistics,IProcessor> processor) {
		super();
		setProcessorChain(processor);
	}	
	public ProcessorsChain<ItemInput, IBatchStatistics, IProcessor> getProcessorChain() {
		return processor;
	}
	public void setProcessorChain(
			ProcessorsChain<ItemInput, IBatchStatistics, IProcessor> processor) {
		this.processor = processor;
		
	}
	public IBatchStatistics process(Target target) throws AmbitException {

		beforeProcessing(target);
		IBatchStatistics result = getResult(target);		
		ProcessorsChain<ItemInput, IBatchStatistics,IProcessor> processor = getProcessorChain(); 
		Iterator<ItemInput> i = getIterator(target);
		while (i.hasNext()) {
			
			ItemInput input = null;

			try {
				input = i.next();
				onItemRead(input,  result);
			} catch (Exception x) {
				onError(input, null, result, x);
				continue;					
			}
			
			try {
				if (processor != null) {
					Object output = processor.process(input);
					onItemProcessed(input, output, result);
				} 
			} catch (Exception x) {
				onError(input, null, result, x);			
				continue;
			}				
										
		}

		afterProcessing(target,i);
		return result;
	}
	public void afterProcessing(Target target, java.util.Iterator<ItemInput> iterator) throws AmbitException {
		propertyChangeSupport.firePropertyChange(PROPERTY_BATCHSTATS,null,batchStatistics);
	}
	public void beforeProcessing(Target target)	throws AmbitException {
		now = System.currentTimeMillis();
	
	}
	public IBatchStatistics getResult(Target target) {
		DefaultBatchStatistics stats = new DefaultBatchStatistics();
		stats.setResultCaption("Read");
		long freq = 1;
		stats.setFrequency(freq);
		return (IBatchStatistics) stats;
	}
	public void onItemProcessing(ItemInput input, Object output,
			IBatchStatistics stats) {
		long freq = stats.getFrequency();
		if ((stats.getRecords(IBatchStatistics.RECORDS_READ) % freq)==0)
			propertyChangeSupport.firePropertyChange(PROPERTY_BATCHSTATS,null,stats);
		
	}
	public void open() throws DbAmbitException {
		for (IProcessor p : getProcessorChain())
			if (p instanceof IDBProcessor)
				((IDBProcessor)p).open();
		
	}
	public void onError(ItemInput input, Object output,
			IBatchStatistics stats, Exception x) {
		stats.increment(IBatchStatistics.RECORDS_ERROR);
		stats.incrementTimeElapsed(IBatchStatistics.RECORDS_ERROR, System.currentTimeMillis()-now);
		now = System.currentTimeMillis();		
	}
	public void onItemProcessed(ItemInput input, Object output,
			IBatchStatistics stats) {
		stats.increment(IBatchStatistics.RECORDS_PROCESSED);
		stats.incrementTimeElapsed(IBatchStatistics.RECORDS_PROCESSED, System.currentTimeMillis()-now);
		propertyChangeSupport.firePropertyChange(PROPERTY_BATCHSTATS,null,stats);
		now = System.currentTimeMillis();
		
	}
	public void onItemRead(ItemInput input, IBatchStatistics stats) {
		stats.increment(IBatchStatistics.RECORDS_READ);
		stats.incrementTimeElapsed(IBatchStatistics.RECORDS_READ, System.currentTimeMillis()-now);
		now = System.currentTimeMillis();
		
	}	
	@Override
	public void setSession(SessionID sessionID) {
		super.setSession(sessionID);
		for (IProcessor p : getProcessorChain())
			if (p instanceof IDBProcessor)
				((IDBProcessor)p).setSession(sessionID);
	}
	@Override
	public void setConnection(Connection connection) throws DbAmbitException {
		super.setConnection(connection);
		for (IProcessor p : getProcessorChain())
			if (p instanceof IDBProcessor)
				((IDBProcessor)p).setConnection(connection);		

		
	}
	@Override
	public void close() throws SQLException {
		processor.close();
		super.close();
	}	
}
