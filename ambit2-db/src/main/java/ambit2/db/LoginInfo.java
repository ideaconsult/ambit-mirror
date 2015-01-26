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

package ambit2.db;

import java.net.URI;
import java.sql.Connection;

import ambit2.base.config.Preferences;

public class LoginInfo {
    protected String scheme;
    protected String host;
    protected String port;
    protected String database;
    protected String user;
    protected String password;

    // TODO derive generic class from QMRFAttributes
    public LoginInfo() {
	setScheme(Preferences.getProperty(Preferences.SCHEME));
	setHostname(Preferences.getProperty(Preferences.HOST));
	setDatabase(Preferences.getProperty(Preferences.DATABASE));
	setPort(Preferences.getProperty(Preferences.PORT));
	setUser(Preferences.getProperty(Preferences.USER));
    }

    public void setURI(URI uri) throws Exception {
	String driver = uri.getScheme();

	URI u1 = new URI(uri.getSchemeSpecificPart());
	setScheme(driver + ":" + u1.getScheme());

	setHostname(u1.getHost());
	setPort(Integer.toString(u1.getPort()));
	setDatabase(u1.getPath().substring(1));
    }

    public void setURI(Connection connection) throws Exception {
	setURI(new URI(connection.getMetaData().getURL()));
	String username = connection.getMetaData().getUserName();
	setUser(username.substring(0, username.indexOf("@")));
    }

    public String getDatabase() {
	return database;
    }

    public void setDatabase(String database) {
	if ("${ambit.db}".equals(database))
	    this.host = "ambit2";
	this.database = database;
    }

    public String getHostname() {
	return host;
    }

    public void setHostname(String hostname) {
	if ("${ambit.db.host}".equals(hostname))
	    this.host = hostname;
	else
	    this.host = hostname;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    public String getPort() {
	return port;
    }

    public void setPort(String port) {
	this.port = port;
    }

    public String getScheme() {
	return scheme;
    }

    public void setScheme(String scheme) {
	this.scheme = scheme;
    }

    public String getUser() {
	return user;
    }

    public void setUser(String user) {
	this.user = user;
    }

    @Override
    public String toString() {
	return user;
    }
}
