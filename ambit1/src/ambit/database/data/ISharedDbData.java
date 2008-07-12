/* ISharedDbData.java
 * Author: Nina Jeliazkova
 * Date: 2006-4-30 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2006  Ideaconsult Ltd.
 * 
 * Contact: nina@acad.bg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 */

package ambit.database.data;

import ambit.data.AmbitUser;
import ambit.data.ISharedData;
import ambit.database.DbConnection;
import ambit.exceptions.AmbitException;

/**
 * An extension of {@link ambit.data.ISharedData} for database applications.
 * @author Nina Jeliazkova nina@acad.bg
 * <b>Modified</b> 2006-4-30
 */
public interface ISharedDbData extends ISharedData {
	public static String[] SOURCE = {"Molecule Browser: Current set of structures","Molecule Browser: Current molecule only"};	
	public static String[] RESULTS_DESTINATION = {"Molecule Browser: New List","Molecule Browser: Replace current molecule","FILE: Write to user selected file","DATABASE: Save as a new dataset","DATABASE: Save as a query results","FILE: Temporary file"};
	public static int MEMORY_LIST = 0;
	public static int MEMORY_CURRENT = 1;
	public static int RESULTS_FILE = 2;
	public static int RESULTS_DATASET = 3;
	public static int RESULTS_QUERY = 4;
	public static int RESULTS_QUERYFILE = 5;
	
	public DbConnection getDbConnection();
	public void setDbConnection(DbConnection dbConnection);
	public void close() throws AmbitException ;
	public void open(String host, String port, String database, String user, String password) throws AmbitException ;
	public void open(String host, String port, String database, String user, String password, boolean verify) throws AmbitException ;
	public String getHost();
	public String getDatabase();
	public String getPort();
	public AmbitUser getUser();
	public int getPageSize();
	public void setPageSize(int pageSize);
	public int getPage();
	public void setPage(int page);
	public int getResultDestination();
	public void setResultDestination(int resultDestination);
	public void setSource(int source);
	public int getSource();
}
