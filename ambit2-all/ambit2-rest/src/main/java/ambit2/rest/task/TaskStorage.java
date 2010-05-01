package ambit2.rest.task;

import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;

import org.restlet.Context;
import org.restlet.data.Reference;

import ambit2.rest.SimpleTaskResource;

public class TaskStorage<USERID> implements ITaskStorage<USERID> {
	protected Logger logger;
	protected String name;
	
	protected ConcurrentMap<UUID,Task<Reference,USERID>> tasks;
	public enum pool_task  { INTERNAL, EXTERNAL};
	protected long taskCleanupRate = 2L*60L*60L*1000L; //2h
	protected Hashtable<pool_task,ExecutorService> pools;
	
	public TaskStorage(String name, Logger logger) {
		this.name = name;
		this.logger = logger;
		pools = new Hashtable<pool_task, ExecutorService>();
		ExecutorService x= createExecutorService(5);
		pools.put(pool_task.INTERNAL,x);
		x = createExecutorService(1);
		pools.put(pool_task.EXTERNAL,x);


		tasks = new ConcurrentHashMap<UUID,Task<Reference,USERID>>();		
		

		TimerTask cleanUpTasks  = new TimerTask() {
			
			@Override
			public void run() {
				cleanUpTasks();
				
			}
		};

	    Timer timer = new Timer();

	    timer.scheduleAtFixedRate(cleanUpTasks,taskCleanupRate,taskCleanupRate);	
	}
	
	protected ExecutorService createExecutorService(int maxThreads) {
		return Executors.newFixedThreadPool(maxThreads,new ThreadFactory() {
		//return Executors.newCachedThreadPool(new ThreadFactory() {
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setPriority(Thread.MIN_PRIORITY);
				thread.setDaemon(true);
				thread.setName(String.format("%s task executor",name));
				thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					public void uncaughtException(Thread t, Throwable e) {
			            java.io.StringWriter stackTraceWriter = new java.io.StringWriter();
			            e.printStackTrace(new PrintWriter(stackTraceWriter));
						logger.severe(stackTraceWriter.toString());
					}
				});
				return thread;
			}
		});
	}
	
	public void cleanUpTasks() {
		Iterator<UUID> keys = tasks.keySet().iterator();
		while (keys.hasNext()) {
			UUID key = keys.next();
			Task<Reference,USERID> task = tasks.get(key);
			try {
				task.update();
				if (task.isDone() && (task.isExpired(taskCleanupRate))) tasks.remove(key);
			} catch (Exception x) {Context.getCurrentLogger().warning(x.getMessage());}
		}
	}	
	public void cancelTasks() {
		Iterator<Task<Reference,USERID>> i = getTasks();
		while (i.hasNext()) {
			Task<Reference,USERID> task = i.next();
			try {
			if (!task.isDone()) task.cancel(true);
			} catch (Exception x) {logger.warning(x.getMessage());}
		}
	}

	public void removeTasks() {
		cancelTasks();
		tasks.clear();
	}	
	
	
	public Iterator<Task<Reference,USERID>> getTasks() {
		return tasks.values().iterator();
	}
	public synchronized Task<Reference,USERID> findTask(String id) {
		try {
			return tasks.get(UUID.fromString(id));
		} catch (Exception x) {
			System.out.println(x);
			return null;
		}
	}
	public synchronized void removeTask(String id) {
		try {
			tasks.remove(UUID.fromString(id));
		} catch (Exception x) {
			System.out.println(x);
			return;
		}
	}
	
	public synchronized Task<Reference,USERID> addTask(String taskName, 
			Callable<Reference> callable, 
			Reference baseReference,
			USERID user,boolean internal) {
		if (callable == null) return null;
		Task<Reference,USERID> task = new Task<Reference,USERID>(callable,user);
		task.setName(taskName);
		
		
		Reference ref =	new Reference(
				String.format("%s%s/%s", baseReference.toString(),SimpleTaskResource.resource,Reference.encode(task.getUuid().toString())));
		task.setUri(ref);
		tasks.put(task.getUuid(),task);

		Future future = internal?
			pools.get(pool_task.INTERNAL).submit(task.getFuture()):
			pools.get(pool_task.EXTERNAL).submit(task.getFuture());

		return task;
	}	
	
	public Iterator<Task<Reference,USERID>> filterTasks() {
		return null;
	}
	
	
}

