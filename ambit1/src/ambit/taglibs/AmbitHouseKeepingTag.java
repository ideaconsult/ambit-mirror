/*
Copyright (C) 2005-2006  

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

package ambit.taglibs;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import ambit.data.literature.ReferenceFactory;
import ambit.data.molecule.SourceDataset;
import ambit.database.DbConnection;
import ambit.ui.actions.dbadmin.DBGenerator;

public class AmbitHouseKeepingTag extends SimpleTagSupport {
	protected String datasetname;
	protected String database="ambit";
	protected String host="localhost";
	protected String user="";
	protected String password="";
	protected String port="3306";
	protected String action = "Fingerprints";
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
	public String getDatasetname() {
		return datasetname;
	}
	public void setDatasetname(String datasetname) {
		this.datasetname = datasetname;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
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
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	@Override
	public void doTag() throws JspException, IOException {
		try {
			DbConnection dbConnection = new DbConnection(host,port,database,user,password);
			dbConnection.open(false);
			DBImportBatch.housekeeping(getSelectedAction(action), 
					new SourceDataset(getDatasetname(),ReferenceFactory.createDatasetReference(getDatasetname(),getDatasetname())),	
					dbConnection);
			dbConnection.close();
		} catch (Exception x) {
			throw new JspException(x);
		}
	}
	protected int getSelectedAction(String a) throws Exception {
		for (int i=0; i < DBGenerator.possibilities.length;i++) {
			if (DBGenerator.possibilities[i].toString().toLowerCase().equals(a.toLowerCase())) 
				return i;
		}
		throw new Exception("Undefined action "+a);
	}
}


