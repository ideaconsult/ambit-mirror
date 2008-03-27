/**
 * Created on 2004-12-9
 *
 */
package ambit2.datastructures;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ambit2.database.DbConnection;
import ambit2.exceptions.AmbitException;

/**
 * This is a test and should not be here
 * 
 * @author Nina Jeliazkova <br>
 *         <b>Modified</b> 2005-4-7
 */
public class BigIntTest {
	public void readtest() {
		DbConnection dbc = new DbConnection("test");
		try {
			dbc.open();
			PreparedStatement ps = dbc.getConn().prepareStatement(
					"select fp from fingerprint where fp=?;"
			// "select fp from fingerprint where fp=18446744073709551614;"
					);
			BigDecimal bd = new BigDecimal("18446744073709551615");
			// "2049");

			// Long l = Long.decode("18446744073709551615");
			// BigInteger bi = new BigInteger("18446744073709551615");
			// System.out.println(Long.valueOf(bd.toString()));
			// ps.setObject(1,bi,Types.BIGINT,0);
			ps.setBigDecimal(1, bd);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				// BigInteger bi = (BigInteger) rs.getObject(1);
				// System.out.println(bi.toString(16));
				long n = rs.getLong(1);
				System.out.println(n + "\t0x" + Long.toHexString(n));
			}
			ps.close();
			dbc.close();
		} catch (AmbitException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void writetest() {
		DbConnection dbc = new DbConnection("test");
		try {
			dbc.open();
			PreparedStatement ps = dbc.getConn().prepareStatement(
					"insert into fingerprint set fp=?;");
			BigDecimal bi = new BigDecimal("18446744073709551614");

			ps.setBigDecimal(1, bi);
			ps.executeUpdate();
			ps.close();
			dbc.close();
		} catch (AmbitException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		BigIntTest t = new BigIntTest();
		// t.writetest();
		t.readtest();
	}
}
