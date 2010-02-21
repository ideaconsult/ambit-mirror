package ambit2.rest;

import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.data.Reference;

import ambit2.rest.task.Task;

public class TaskApplication<USERID> extends Application {
	protected ConcurrentMap<UUID,Task<Reference,USERID>> tasks;

	protected long taskCleanupRate = 2L*60L*60L*1000L; //2h
	protected ExecutorService pool;
	public TaskApplication() {
		super();
		//pool = Executors.newFixedThreadPool(5);
		pool = Executors.newSingleThreadExecutor(new ThreadFactory() {
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setPriority(Thread.MIN_PRIORITY);
				thread.setDaemon(true);
				thread.setName(String.format("%s task executor",getName()));
				thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					public void uncaughtException(Thread t, Throwable e) {
			            java.io.StringWriter stackTraceWriter = new java.io.StringWriter();
			            e.printStackTrace(new PrintWriter(stackTraceWriter));
						getLogger().severe(stackTraceWriter.toString());
					}
				});
				return thread;
			}
		});

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
	
	@Override
	protected void finalize() throws Throwable {
		removeTasks();
		super.finalize();
	}
	
	public void cleanUpTasks() {
		Iterator<UUID> keys = tasks.keySet().iterator();
		while (keys.hasNext()) {
			UUID key = keys.next();
			Task<Reference,USERID> task = tasks.get(key);
			try {
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
			} catch (Exception x) {getLogger().warning(x.getMessage());}
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
	public synchronized Reference addTask(String taskName, Callable<Reference> callable, Reference baseReference) {
		if (callable == null) return null;
		FutureTask<Reference> futureTask = new FutureTask<Reference>(callable) {
			@Override
			protected void done() {
				super.done();
				//((AmbitApplication)getApplication()).getTasks().remove(this);
			}
			
			
		};		
		UUID uuid = UUID.randomUUID();
		Task<Reference,USERID> task = new Task<Reference,USERID>(futureTask);
		task.setName(taskName);
		Reference ref =	new Reference(
				String.format("%s%s/%s", baseReference.toString(),SimpleTaskResource.resource,Reference.encode(uuid.toString())));
		task.setUri(ref);
		tasks.put(uuid,task);
		//getTaskService().submit(futureTask);
		pool.submit(futureTask);
		return ref;
	}
	
}
