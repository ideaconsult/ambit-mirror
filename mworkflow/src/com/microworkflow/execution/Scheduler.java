/*
 * 	 
 *  Copyright (c) 2002 Dragos Manolescu (dam@micro-workflow.com)
 * 
 *  See the LICENSE file for licensing information.
 */

package com.microworkflow.execution;

import java.util.logging.Logger;

import EDU.oswego.cs.dl.util.concurrent.*;

public class Scheduler {
	public static final int THREADS=3;
	public static final int THREAD_TIMEOUT=3000;
	protected PooledExecutor threadPool;
	Logger logger=Logger.getLogger("com.microworkflow.execution.Scheduler");
	
	public Scheduler() {
		initialize();
	}
	protected void initialize() {
		threadPool=new PooledExecutor(new LinkedQueue());
		threadPool.setKeepAliveTime(THREAD_TIMEOUT);
		threadPool.createThreads(THREADS);
	}
	public void shutdown() {
		threadPool.shutdownNow();
	}
	public void scheduleCommand(Closure command) {
		try {
			threadPool.execute(new CommandWrapper(command));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	public void waitForCompletion() {
		threadPool.shutdownAfterProcessingCurrentlyQueuedTasks();
		logger.info("Waiting on queued threads to complete");
		try {
			threadPool.awaitTerminationAfterShutdown();			
		} catch (IllegalStateException e) {
			logger.severe("Invalid pool state");			
		} catch (InterruptedException e) {
			logger.info("Thread interrupted");
		}
	}
}
