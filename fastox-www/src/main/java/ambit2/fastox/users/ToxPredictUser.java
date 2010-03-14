package ambit2.fastox.users;

import java.util.Date;
import java.util.UUID;

public class ToxPredictUser implements IToxPredictUser ,Comparable<IToxPredictUser>{
	protected String name;
	protected String id;
	protected long timestamp;
	protected String activity;
	protected String clientinfo;
	
	public String getClientinfo() {
		return clientinfo;
	}
	public void setClientinfo(String clientinfo) {
		this.clientinfo = clientinfo;
	}
	public synchronized long setTimeStamp(String description) {
		timestamp = System.currentTimeMillis();
		this.activity = description;
		return timestamp;
	}
	public synchronized boolean isActive(long maxInactiveTimeRange) {
		return (System.currentTimeMillis()-timestamp) <= maxInactiveTimeRange;
	}
	public synchronized long getTimeStampLastActive() {
		return timestamp;
	}
	public ToxPredictUser() {
		this(UUID.randomUUID().toString());
	}
	public ToxPredictUser(String id) throws IllegalArgumentException  {
		this(id,"guest");
	}
	public ToxPredictUser(String id,String name) throws IllegalArgumentException {
		super();
		
		this.id = "admin".equals(id)?id:UUID.fromString(id).toString();
		this.name = "admin".equals(id)?id:name;
		this.timestamp = System.currentTimeMillis();
	}
	

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return String.format("%s [%s] %s %s at %s (%s)",
				name,
				id,
				activity==null?"":"Last activity ",
				activity==null?"":activity,
				new Date(timestamp),
				getClientinfo()==null?"":getClientinfo()
				);
	}
    public int hashCode() {
    	int hash = 7;
    	hash = 31 * hash + getId().hashCode();
    	//hash = 31 * hash + (null == data ? 0 : data.hashCode());
    	return hash;

    }		
	public int compareTo(IToxPredictUser o) {
		return getId().compareTo(o.getId());
	}
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof IToxPredictUser)?getId().equals(((IToxPredictUser)obj).getId()):false;
	}

}
