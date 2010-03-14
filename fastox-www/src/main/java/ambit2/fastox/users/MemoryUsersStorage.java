package ambit2.fastox.users;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.Context;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;


/**
 * Quick and dirty implementation of pseudo users
 * @author nina
 *
 */
public class MemoryUsersStorage {
	protected ConcurrentHashMap<IToxPredictUser,IToxPredictSession> users = null;
	protected TimerTask cleanUpSessions = null; 
	protected Timer timer;
	protected long taskCleanupRate = 15L*60L*1000L; //15 min
	protected long maxInactiveTimeRange = 30L*60L*1000L; //45 min;
	
	protected static MemoryUsersStorage instance;
	
	protected MemoryUsersStorage() {
		super();
		users = new ConcurrentHashMap<IToxPredictUser,IToxPredictSession>();
		cleanUpSessions = new TimerTask() {
				@Override
				public void run() {
					cleanUpSessions();
					
				}
		};
		timer = new Timer();
		timer.scheduleAtFixedRate(cleanUpSessions,5L*60L*1000L,taskCleanupRate);
		
		
	}
	public static synchronized Enumeration<IToxPredictUser> users() {
		if (instance==null) return null;
		else return instance.users.keys();
	}
	public static synchronized MemoryUsersStorage getInstance() {
		if (instance==null) {
			instance = new MemoryUsersStorage();
			instance.addSession(new ToxPredictUser("admin"));  //for testing only
		}
		return instance;
	}
	public synchronized void cleanUpSessions() {
		
		Iterator<IToxPredictUser> keys = users.keySet().iterator();
		while (keys.hasNext()) {
			IToxPredictUser user = keys.next();
			try {
				if (user.getId().equals("admin")) continue;
				if (!user.isActive(maxInactiveTimeRange)) {
					IToxPredictSession sessionData = users.get(user);
					if (sessionData!=null) sessionData.clear();
					keys.remove();
				}
			} catch (Exception x) {Context.getCurrentLogger().warning(x.getMessage());}
		}
	}	
	public static synchronized IToxPredictSession addSession(IToxPredictUser user) throws ResourceException {
		if (user == null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Unknown user");
		ToxPredictSession session = new ToxPredictSession(user);
		getInstance().users.put(user, session);
		return session;
	}	
	public static synchronized IToxPredictSession getSession(IToxPredictUser user) throws ResourceException {
		if (user == null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Unknown user");
		return getInstance().users.get(user);
	}
	public static synchronized IToxPredictSession getSession(String id) throws ResourceException {
		try {
			if (id == null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Invalid user");
			IToxPredictUser user = new ToxPredictUser(id);
			IToxPredictSession session = getInstance().getSession(user);
			if (session != null) return session; 
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Unknown user");
		} catch (IllegalArgumentException x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Unknown user");	
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,x.getMessage(),x);
		}
	}
}
