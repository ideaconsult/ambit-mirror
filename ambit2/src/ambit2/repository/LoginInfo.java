/*
Copyright (C) 2005-2008  

Contact: nina@acad.bg

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public License
as published by the Free Software Foundation; either version 2.1
of the License, or (at your option) any later version.
All we ask is that proper credit is given for our work, which includes
- but is not limited to - adding the above copyright notice to the beginning
of your source code files, and to any copyright notice that you may distribute
with programs based on this work.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA
*/

package ambit2.repository;

import ambit2.config.Preferences;
import ambit2.data.qmrf.QMRFAttributes;

public class LoginInfo extends QMRFAttributes {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6766389941828875205L;
	//TODO derive generic class from QMRFAttributes
	protected static final String[] names={
		Preferences.HOST,Preferences.PORT,Preferences.USER,"Password",Preferences.DATABASE};
	public LoginInfo() {
		setNames(names);
		setHostname(Preferences.getProperty(Preferences.HOST));
		setDatabase(Preferences.getProperty(Preferences.DATABASE));
		setPort(Preferences.getProperty(Preferences.PORT));
		setUser(Preferences.getProperty(Preferences.USER));
	}
	public String getDatabase() {
		return get(Preferences.DATABASE);
	}
	public void setDatabase(String database) {
		put(Preferences.DATABASE,database);
	}
	public String getHostname() {
		return get(Preferences.HOST);
	}
	public void setHostname(String hostname) {
		put(Preferences.HOST,hostname);
	}
	public String getPassword() {
		return get("Password");
	}
	public void setPassword(String password) {
		put("Password",password);
	}
	public String getPort() {
		return get(Preferences.PORT);
	}
	public void setPort(String port) {
		put(Preferences.PORT,port);
	}
	public String getScheme() {
		return get(Preferences.SCHEME);
	}
	public void setScheme(String scheme) {
		put(Preferences.SCHEME,scheme);
	}
	public String getUser() {
		return get(Preferences.USER);
	}
	public void setUser(String user) {
		put(Preferences.USER,user);
	}
}


