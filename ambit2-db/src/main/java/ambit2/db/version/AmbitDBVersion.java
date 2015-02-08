package ambit2.db.version;

import java.io.Serializable;

public class AmbitDBVersion implements Serializable {
	/**
     * 
     */
    private static final long serialVersionUID = -810148258205916153L;
	protected String dbname;
	public String getDbname() {
		return dbname;
	}
	public void setDbname(String dbname) {
		this.dbname = dbname;
	}
	public AmbitDBVersion() {

	}
	protected int major;
	public int getMajor() {
		return major;
	}
	public void setMajor(int major) {
		this.major = major;
	}
	public int getMinor() {
		return minor;
	}
	public void setMinor(int minor) {
		this.minor = minor;
	}
	public long getCreated() {
		return created;
	}
	public void setCreated(long created) {
		this.created = created;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	protected int minor;
	long created;
	String comments;
}
