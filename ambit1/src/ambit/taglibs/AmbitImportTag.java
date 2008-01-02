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

import ambit.data.literature.ReferenceFactory;
import ambit.data.molecule.SourceDataset;

/**
 * 
 * @author Nina Jeliazkova nina@acad.bg
 <pre>
xml is expected of the following format
<!ELEMENT molecules (property*)>
<!ATTLIST molecules embedded (Yes|No) #IMPLIED url CDATA #REQUIRED  filetype CDATA #IMPLIED description CDATA #IMPLIED>

<!ELEMENT property EMPTY>
<!ATTLIST property fieldname CDATA #REQUIRED fieldtype CDATA #REQUIRED newname CDATA #IMPLIED>
 
 </pre>
 
 */
public class AmbitImportTag extends AmbitFileTag {	
	protected String datasetname;
	protected String database="ambit";
	protected String host="localhost";
	protected String user="";
	protected String password="";
	protected String port="3306";
	protected String xml=""; 
	
	public String getDatasetname() {
		return datasetname;
	}
	public void setDatasetname(String datasetname) {
		this.datasetname = datasetname;
	}

	public AmbitImportTag() {

	}
	@Override
	public void doTag() throws JspException, IOException {
		try {
			if (!"".equals(getXml())) {
				DBImportBatch.import2DB_xml(getXml(), 
						host, port, database, user, password);
			} else
			DBImportBatch.import2DB(getFile(), 
					new SourceDataset(getDatasetname(),ReferenceFactory.createDatasetReference(getFilename(),getDatasetname())),	
					host, port, database, user, password);
		} catch (Exception x) {
			throw new JspException(x);
		}
	}


	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
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
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getXml() {
		return xml;
	}
	public void setXml(String xml) {
		this.xml = xml;
	}

}


