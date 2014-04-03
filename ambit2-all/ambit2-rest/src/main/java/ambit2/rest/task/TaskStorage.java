package ambit2.rest.task;

import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import net.idea.restnet.i.task.ICallableTask;
import net.idea.restnet.i.task.ITask;
import net.idea.restnet.i.task.ITaskResult;
import net.idea.restnet.i.task.ITaskStorage;
import net.idea.restnet.i.task.TaskStatus;

import org.restlet.Context;
import org.restlet.data.Reference;

import ambit2.rest.SimpleTaskResource;

public class TaskStorage<USERID> implements ITaskStorage<USERID> {
	protected Logger logger;
	protected String name;
	protected TimeUnit taskCleanupUnit = TimeUnit.MINUTES;
	protected long taskCleanupRate = 120; //30 min
	protected double cpuutilisation = 0.5;
	protected double waittime=1;
	protected double cputime=1;
	
	protected ExecutorCompletionService<Reference> completionService_internal;
	protected ExecutorCompletionService<Reference> completionService_external;
	protected ExecutorService pool_internal;
	protected ExecutorService pool_external;
	protected ScheduledThreadPoolExecutor cleanupTimer;
	protected ScheduledThreadPoolExecutor cleanupCompletedTasks;

	protected ConcurrentMap<UUID,ITask<ITaskResult,USERID>> tasks;
	
	public TaskStorage(String name, Logger logger) {
		this.name = name;
		this.logger = logger;
		int nthreads = (int)Math.ceil(Runtime.getRuntime().availableProcessors()*cpuutilisation*(1+waittime/cputime));
		//nthreads = 8;
		nthreads = 64;
		pool_internal = createExecutorService("internal",nthreads);
		
		//		(int)Math.ceil(Runtime.getRuntime().availableProcessors()*cpuutilisation*(1+waittime/cputime))
			//	);
		pool_external = createExecutorService("external",nthreads);

		/**
		 * TODO for internal tasks
		 * https://www.securecoding.cert.org/confluence/display/java/TPS01-J.+Do+not+execute+interdependent+tasks+in+a+bounded+thread+pool
		 */
		completionService_internal = new ExecutorCompletionService<Reference>(pool_internal);
		completionService_external = new ExecutorCompletionService<Reference>(pool_external);

		tasks = new ConcurrentHashMap<UUID,ITask<ITaskResult,USERID>>();
		

		TimerTask cleanUpTasks  = new TimerTask() {
			
			@Override
			public void run() {
				cleanUpTasks();
				
			}
		};
		
		TimerTask completedTasks  = new TimerTask() {
			
			@Override
			public void run() {
				Future<Reference> f = null;
				while ((f = completionService_internal.poll()) != null) {
					if ((f instanceof ExecutableTask) && ((ExecutableTask)f).getTask()!=null) {
						((ExecutableTask)f).getTask().setStatus(TaskStatus.Cancelled);
						((ExecutableTask)f).getTask().setTimeCompleted(System.currentTimeMillis());
						((ExecutableTask)f).setTask(null);
					}					
					f= null;
				}
				while ((f = completionService_external.poll()) != null) {
					if ((f instanceof ExecutableTask) && ((ExecutableTask)f).getTask()!=null) {
						((ExecutableTask)f).getTask().setStatus(TaskStatus.Cancelled);
						((ExecutableTask)f).getTask().setTimeCompleted(System.currentTimeMillis());
						((ExecutableTask)f).setTask(null);
					}
					f= null;
				}
			}
		};
		

		cleanupTimer = new ScheduledThreadPoolExecutor(1);
		cleanupTimer.scheduleWithFixedDelay(cleanUpTasks, taskCleanupRate, taskCleanupRate,taskCleanupUnit);

		cleanupCompletedTasks = new ScheduledThreadPoolExecutor(1);
		cleanupCompletedTasks.scheduleWithFixedDelay(completedTasks, 100,100,TimeUnit.MILLISECONDS);
	}
	
	public void cleanUpTasks() {
		Iterator<UUID> keys = tasks.keySet().iterator();
		while (keys.hasNext()) {
			UUID key = keys.next();
			ITask<ITaskResult,USERID> task = tasks.get(key);
			try {
				//task.update();
				if (task.isDone() && (task.isExpired(taskCleanupRate))) tasks.remove(key);
			} catch (Exception x) {Context.getCurrentLogger().warning(x.getMessage());}
		}
	}	
	protected ExecutorService createExecutorService(final String name, int maxThreads) {
		ThreadFactory tf =
        new ThreadFactory() {
    			public Thread newThread(Runnable r) {
    				Thread thread = new Thread(r);
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
    			
    			
    		};
        RejectedExecutionHandler rjh = new RejectedExecutionHandler() {
        	@Override
        	public void rejectedExecution(Runnable r,
        			ThreadPoolExecutor executor) {

        		
        		if (r instanceof FutureTask) {
        			((FutureTask)r).cancel(true);
        		}

        		
        	}
        };
        
        //SynchronousQueue<Runnable> taskQueue = new SynchronousQueue<Runnable>(true);        
        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>(1000);
       // BlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<Runnable>(5,true);
        
        ExecutorService xs =
				new ThreadPoolExecutor(maxThreads, maxThreads,
                0L, TimeUnit.MILLISECONDS,
                taskQueue,
                tf,rjh) {
        	
        	@Override
        	protected <T> RunnableFuture<T> newTaskFor(
        			Callable<T> callable) {
          		if (callable instanceof ExecutableTask) {
        			((ExecutableTask)callable).getTask().setStatus(TaskStatus.Queued);
        			return (RunnableFuture<T>)callable;
          		} else if (callable instanceof RunnableFuture)
        			return (RunnableFuture<T>)callable;
        		else
        			return super.newTaskFor(callable);
        	}
        	protected <T extends Object> java.util.concurrent.RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        		if (runnable instanceof ExecutableTask) {
        			((ExecutableTask)runnable).getTask().setStatus(TaskStatus.Queued);
        			return (RunnableFuture<T>)runnable;
        		} else if (runnable instanceof RunnableFuture)
        			return (RunnableFuture<T>)runnable;
        		else
        			return super.newTaskFor(runnable, value);
        		
        	};
        
        };
        
		
		return xs;
	}
	
	protected ITask<ITaskResult,USERID> createTask(USERID user,ICallableTask callable) {
		return new Task<ITaskResult,USERID>(user) {
			@Override
			public synchronized float getPercentCompleted() {
				return getUri()==null?0:getUri().getPercentCompleted();
			}
			
		};
	}
	
	
	public ITask<ITaskResult,USERID> addTask(String taskName, 
			ICallableTask callable, 
			Reference baseReference,
			USERID user,boolean internal) {
		if (callable == null) return null;
		
		ITask<ITaskResult,USERID> task = createTask(user,callable);
		task.setName(taskName);
		task.setInternal(internal);
		callable.setUuid(task.getUuid());
		
		ExecutableTask<USERID> xtask = new ExecutableTask<USERID>(callable,task);
		
		
		TaskResult ref =	new TaskResult(
				String.format("%s%s/%s", 
						baseReference==null?"":baseReference.toString(),
						SimpleTaskResource.resource,
						Reference.encode(task.getUuid().toString())));
		task.setUri(ref);

		if (tasks.get(task.getUuid())==null) {
			ITask<ITaskResult,USERID> theTask = tasks.putIfAbsent(task.getUuid(),task);
	
			if (theTask==null) {
				theTask = task;
			} else {

			}

			try {
				Future future = task.isInternal()?
						completionService_internal.submit(xtask,null):
						completionService_external.submit(xtask,null);

						return theTask;
			} catch (RejectedExecutionException x) {
				return null;
			} catch (Exception x) {
				return null;
			}

		}
		else return null;
	}	
	public synchronized ITask<ITaskResult,USERID> findTask(String id) {
		try {
			return tasks.get(UUID.fromString(id));
		} catch (Exception x) {
			return null;
		}
	}
	public synchronized ITask<ITaskResult,USERID> findTask(UUID id) {
		try {
			return tasks.get(id);
		} catch (Exception x) {

			return null;
		}
	}	
	public synchronized void removeTask(String id) {
		try {
			tasks.remove(UUID.fromString(id));
		} catch (Exception x) {
			return;
		}
	}
	@Override
	public Iterator<UUID> getTasks() {
		return tasks.keySet().iterator();
	}
	/*
	public Iterator<UUID> getTasks() {
		ArrayList<UUID> lists = new ArrayList<UUID>();
		Iterator<UUID> i = tasks.keySet().iterator();
		while (i.hasNext())
			lists.add(i.next());
		return lists.iterator();
	} 
	 */
	
	@Override
	public void removeTasks() {
		cancelTasks();
		tasks.clear();
		
	}
	public void cancelTasks() {
		Iterator<UUID> keys = tasks.keySet().iterator();
		while (keys.hasNext()) {
			UUID key = keys.next();
			ITask<ITaskResult,USERID> task = tasks.get(key);
			try {
				if (!task.isDone()) task.setStatus(TaskStatus.Cancelled);
				} catch (Exception x) {logger.warning(x.getMessage());}
		}
	}
	public synchronized void shutdown(long timeout,TimeUnit unit) throws Exception {
		
		if (!pool_internal.isShutdown()) {

			pool_internal.awaitTermination(timeout, unit);
			pool_internal.shutdown();
		}
		if (!pool_external.isShutdown()) {
			pool_external.awaitTermination(timeout, unit);
			pool_external.shutdown();
		}
		if (!cleanupTimer.isShutdown()) {
			cleanupTimer.awaitTermination(timeout, unit);
			cleanupTimer.shutdown();
		}
		if (!cleanupCompletedTasks.isShutdown()) {
			cleanupCompletedTasks.awaitTermination(timeout, unit);
			cleanupCompletedTasks.shutdown();
		}		
	}	
	public Iterator<ITask<Reference,USERID>> filterTasks() {
		return null;
	}
}
