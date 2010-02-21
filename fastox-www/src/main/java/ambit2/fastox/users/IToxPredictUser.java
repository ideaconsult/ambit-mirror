package ambit2.fastox.users;

public interface IToxPredictUser {
	public String getId();
	public String getName();
	public long getTimeStampLastActive();
	/**
	 * Sets the current time stamp as last being active, with the activity description
	 * @param description
	 * @return
	 */
	public long setTimeStamp(String description);
	public boolean isActive(long maxInactiveTimeRange);
	public void setClientinfo(String clientinfo);
	public String getClientinfo();
}
