/* DbCreateDatabase.java
 * Author: Nina Jeliazkova
 * Date: May 6, 2008 
 * Revision: 0.1 
 * 
 * Copyright (C) 2005-2008  Nina Jeliazkova
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

package ambit2.db.processors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import ambit2.base.config.Preferences;
import ambit2.base.data.StringBean;

public class DbCreateDatabase extends
		AbstractRepositoryWriter<StringBean, String> {
	boolean useExistingDatabase = false;

	public boolean isUseExistingDatabase() {
		return useExistingDatabase;
	}

	public void setUseExistingDatabase(boolean useExistingDatabase) {
		this.useExistingDatabase = useExistingDatabase;
	}

	protected String adminPass = null;

	public String getAdminPass() {
		return adminPass;
	}

	public void setAdminPass(String adminPass) {
		this.adminPass = adminPass;
	}

	protected String userPass = null;

	public String getUserPass() {
		return userPass;
	}

	public void setUserPass(String userPass) {
		this.userPass = userPass;
	}

	private static final long serialVersionUID = -335737998721944578L;
	protected String SQLFile = "ambit2/db/sql/create_tables.sql";

	public DbCreateDatabase() {
		this("guest", "guest");
	}

	public DbCreateDatabase(String guestpass, String adminpass) {
		this("guest", guestpass, "admin", adminpass);
	}

	public DbCreateDatabase(String guest, String guestpass, String admin,
			String adminpass) {
		setOperation(OP.CREATE);

		this.adminPass = adminpass;
		this.userPass = guestpass;

	}


	@Override
	public String create(StringBean database) throws SQLException {
		createDatabase(database.toString());
		createTables(database.toString());
		/*
		String[] users = {
				String.format("insert into ausers (user_name,email,lastname,keywords,homepage) values (\"guest\",\"guest\",\"Default guest user\",\"guest\",\"http://ambit.sourceforge.net\");"),
				String.format("insert into ausers (user_name,email,lastname,keywords,homepage) values (\"admin\",\"admin\",\"Default admin user\",\"admin\",\"http://ambit.sourceforge.net\");"),
				String.format("insert into ausers (user_name,email,lastname,keywords,homepage) values (\"quality\",\"quality\",\"Automatic quality verifier\",\"quality\",\"http://ambit.sourceforge.net\");"), };
		Statement st = connection.createStatement();
		try {
			for (String user : users)
				try {
					st.executeUpdate(user);
				} catch (Exception x) {
					logger.log(java.util.logging.Level.WARNING, user, x);
				}
		} finally {
			if (st != null)
				st.close();
		}
		*/
		try {
			Preferences.setProperty(Preferences.DATABASE, database.toString());
			Preferences.saveProperties(getClass().getName());
		} catch (Exception x) {
		}
		return database.toString();
	}

	public void createDatabase(String newDb) throws SQLException {
		Statement t = null;
		if (!useExistingDatabase)
			try {
				t = connection.createStatement();
				t.addBatch("DROP DATABASE IF EXISTS `" + newDb + "`");
				t.addBatch("CREATE SCHEMA IF NOT EXISTS `" + newDb
						+ "` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin ");
				t.addBatch("USE `" + newDb + "`");
				t.executeBatch();

			} finally {
				if (t != null)
					t.close();
			}
	}

	/*
	 * public void createTables(String newDB) throws SQLException { InputStream
	 * in = this.getClass().getClassLoader().getResourceAsStream( getSQLFile());
	 * try { ScriptRunner script = new ScriptRunner(connection,false,false);
	 * 
	 * script.runScript(new BufferedReader(new InputStreamReader(in))); } catch
	 * (IOException x) { x.printStackTrace(); throw new
	 * SQLException(x.getMessage()); } finally { try {in.close(); } catch
	 * (Exception x) {x.printStackTrace();} }
	 * 
	 * }
	 */

	public void createTables(String newDB) throws SQLException {
		try {
			// if (this.getUser().getTitle().equals(AmbitUser.USERTYPE_ADMIN)) {
			InputStream in = this.getClass().getClassLoader()
					.getResourceAsStream(getSQLFile());

			if (in == null)
				throw new SQLException("Can't find " + getSQLFile());

			Statement t = connection.createStatement();
			t.execute("USE `" + newDB + "`;");
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			String line = null;
			StringBuffer table = new StringBuffer();
			String delimiter = ";";
			while (true) {

				line = reader.readLine();
				if (line == null)
					break;
				if (line.toUpperCase().startsWith("DELIMITER")) {
					delimiter = line.substring(line.indexOf("DELIMITER") + 10)
							.trim();
					// t.execute(table.toString());
					table = new StringBuffer();
					continue;
				}

				if (line.trim().toUpperCase().startsWith("END " + delimiter)) {
					table.append("END");
					int ok = t.executeUpdate(table.toString());
					table = new StringBuffer();
					continue;
				}
				if (line == null)
					break;
				if (line.trim().equals(""))
					continue;
				if (line.indexOf("--") == 0)
					continue;
				table.append(line);
				table.append("\n");
				if (line.indexOf(delimiter) >= 0) {
					// System.out.println(table.toString());
					// t.addBatch(table.toString());
					t.executeUpdate(table.toString());
					table = new StringBuffer();
				}

			}
			// t.executeBatch();
			in.close();
			reader.close();
		} catch (IOException x) {
			x.printStackTrace();
			throw new SQLException(x.getMessage());
		}
	}

	public synchronized String getSQLFile() {
		return SQLFile;
	}

	public synchronized void setSQLFile(String file) {
		SQLFile = file;
	}

	public static boolean dbExists(Connection connection, String dbname)
			throws Exception {
		boolean ok = false;
		ResultSet rs = null;
		Statement st = null;
		try {

			st = connection.createStatement();
			rs = st.executeQuery("show databases");
			while (rs.next()) {
				if (dbname.equals(rs.getString(1))) {
					ok = true;
					// break; there was smth wrong with not scrolling through
					// all records
				}
			}

		} catch (Exception x) {
			throw x;
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception x) {
			}
			try {
				if (st != null)
					st.close();
			} catch (Exception x) {
			}
		}
		return ok;
	}

	public static String getDbVersion(Connection connection, String dbname)
			throws Exception {
		String version = null;
		ResultSet rs = null;
		Statement st = null;

		try {
			st = connection.createStatement();
			rs = st.executeQuery(String.format("Use `%s`", dbname)); // just in
																		// case
		} catch (Exception x) {
			throw x;
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception x) {
			}
			try {
				if (st != null)
					st.close();
			} catch (Exception x) {
			}
		}
		try {
			st = connection.createStatement();
			rs = st.executeQuery("select concat(idmajor,'.',idminor) from version");
			while (rs.next()) {
				version = rs.getString(1);
			}

		} catch (Exception x) {
			throw x;
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception x) {
			}
			try {
				if (st != null)
					st.close();
			} catch (Exception x) {
			}
		}
		return version;
	}

	public static void dropTables(Connection connection, String dbname,
			List<String> table_names) throws Exception {
		dropTables(connection, dbname,
				table_names.toArray(new String[table_names.size()]));
	}

	public static void dropTables(Connection connection, String dbname,
			String[] table_names) throws Exception {
		Statement st = null;
		try {
			st = connection.createStatement();
			st.addBatch(String.format("Use `%s`", dbname)); // just in case
			st.addBatch("SET FOREIGN_KEY_CHECKS = 0");
			for (String table : table_names) {
				String sql = String.format("drop table if exists `%s`", table);
				st.addBatch(sql);
			}
			st.addBatch("SET FOREIGN_KEY_CHECKS = 1");
			st.executeBatch();
		} catch (Exception x) {
			throw x;
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (Exception x) {
			}
		}

	}

	public static List<String> tablesExists(Connection connection, String dbname)
			throws Exception {
		int tables = 0;
		ResultSet rs = null;
		Statement st = null;
		List<String> table_names = new ArrayList<String>();
		try {
			st = connection.createStatement();
			rs = st.executeQuery(String.format("Use `%s`", dbname)); // just in
																		// case
		} catch (Exception x) {
			throw x;
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception x) {
			}
			try {
				if (st != null)
					st.close();
			} catch (Exception x) {
			}
		}
		try {
			st = connection.createStatement();
			rs = st.executeQuery("show tables");
			while (rs.next()) {
				tables++;
				table_names.add(rs.getString(1));
			}

		} catch (Exception x) {
			throw x;
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception x) {
			}
			try {
				if (st != null)
					st.close();
			} catch (Exception x) {
			}
		}
		return table_names;
	}
}
