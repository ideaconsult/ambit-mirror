/**
 * Filename:AmbitUser.java 
 * @author Nina Jeliazkova
 * Created 2005-3-27
 *
 * @see AMBIT project http://luna.acad.bg/nina/projects
 */

package ambit2.core.data;

import com.jgoodies.binding.beans.Model;

/**
 * Contains all information about an AMBIT database user. Used in {@link ambit2.database.DbConnection}.
 * @author Nina Jeliazkova <br>
 * <b>Modified</b> 2005-4-7
 */
public class AmbitUser extends Model {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2225165862194930280L;
	public enum USER_TYPE {
	    Guest,Admin,Pro 
	}
	
	
	protected String name = "";	
	protected String title = "";
	protected String firstName = "";
	protected String lastName = "";
	protected String affiliation = "";
	protected String address = "";
	protected String city = "";	
	protected String country = "";
	protected String email = "";
	protected String www = "";	
	//protected String loginAllowedFromHost = "%";
	protected String loggedFromHost = "";	
	protected String regStatus = "commenced";	
	protected String password = "guest";
	protected USER_TYPE type = USER_TYPE.Guest;
	
	public USER_TYPE getType() {
		return type;
	}
	public void setType(USER_TYPE type) {
		this.type = type;
	}
	/**
	 * 
	 */
	public AmbitUser() {
		this("guest");
	}
	public AmbitUser(String name) {
		this(name,USER_TYPE.Guest);
	}
	public AmbitUser(String name, USER_TYPE type) {
		setName(name);
		setType(type);
		
	}	
	/**

	public AmbitUser(String name) {
		super(name);
		
	}


	public AmbitUser(String name, int id) {
		super(name, id);
		
	}
	*/

	/**
	 * @return Returns the address.
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param address The address to set.
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * @return Returns the affiliation.
	 */
	public String getAffiliation() {
		return affiliation;
	}
	/**
	 * @param affiliation The affiliation to set.
	 */
	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}
	/**
	 * @return Returns the city.
	 */
	public String getCity() {
		return city;
	}
	/**
	 * @param city The city to set.
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/**
	 * @return Returns the country.
	 */
	public String getCountry() {
		return country;
	}
	/**
	 * @param country The country to set.
	 */
	public void setCountry(String country) {
		this.country = country;
	}
	/**
	 * @return Returns the email.
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email The email to set.
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return Returns the firstName.
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName The firstName to set.
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return Returns the lastName.
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * @param lastName The lastName to set.
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	/**
	 * @return Returns the title.
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title The title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return Returns the www.
	 */
	public String getWww() {
		return www;
	}
	/**
	 * @param www The www to set.
	 */
	public void setWww(String www) {
		this.www = www;
	}
	public void updateConnectionData(String user, String password, String host) {
		name = user;
		this.password = password;
		loggedFromHost = host;
	}
	/*
	public String mysqlHostAllowed() {
		return loginAllowedFromHost;
	}
	public void updatemysqlHostAllowed(String host) {
		loginAllowedFromHost = host;
	}
	*/
	public String getLoggedFromHost() {
		return loggedFromHost;
	}
	public String mysqlPassword() {
		return password;
	}
	/*
	public boolean isLoginAllowed() {
		if (loginAllowedFromHost.equals("%")) return true;
		else return (loginAllowedFromHost.equals(loggedFromHost));
	}
	*/
	public Object clone() throws CloneNotSupportedException {
		AmbitUser au = (AmbitUser) super.clone();
		au.address = address;
		au.affiliation = affiliation;
		au.city = city;
		au.country = country;
		//au.editable = editable;
		au.email = email;
		au.firstName = firstName;
		au.lastName = lastName;
		au.loggedFromHost = loggedFromHost;
		//au.loginAllowedFromHost = loginAllowedFromHost;
		au.password = password;
		au.regStatus = regStatus;
		au.title = title;
		au.setType(type);
		au.www = www;
		return au;
	}
	/*
	public String getLoginAllowedFromHost() {
		return loginAllowedFromHost;
	}

	public void setLoginAllowedFromHost(String loginAllowedFromHost) {
		this.loginAllowedFromHost = loginAllowedFromHost;
	}
	*/

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}

