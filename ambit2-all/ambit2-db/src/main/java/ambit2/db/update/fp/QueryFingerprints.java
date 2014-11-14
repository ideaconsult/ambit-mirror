package ambit2.db.update.fp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.StringCondition;
import ambit2.descriptors.processors.BitSetGenerator.FPTable;

/**
 * Expects 1024 bit fingerprint in the form of  "0-0-0-0-0-20000000-0-0-0-0-0-0-0-0-0-10000000" (16x 64 bits)
 * @author nina
 *
 * @param <T>
 */
public class QueryFingerprints   extends AbstractQuery<String, IFingerprint<FPTable,String>, StringCondition, IFingerprint<FPTable,String>>  implements IQueryRetrieval<IFingerprint<FPTable,String>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2637272084357419722L;
	protected _order order = _order.frequency;
	
	public _order getOrder() {
		return order;
	}

	public void setOrder(_order order) {
		this.order = order;
	}

	public enum _order {
		none,
		frequency {
			@Override
			public String getSQL() {
				return " order by c desc";
			}
		},
		fingerprints {
			@Override
			public String getSQL() {
				return " order by fp1,fp2,fp3,fp4,fp5,fp6,fp7,fp8,fp9,fp10,fp11,fp12,fp13,fp14,fp15,fp16";
			}
		};
		public String getSQL() {
			return "";
		}
	}
	protected String sql = 
	
		"SELECT \n"+
		"concat(\n"+
		"hex(fp1),'-',\n" +
		"hex(fp2),'-',\n"+
		"hex(fp3),'-',\n"+
		"hex(fp4),'-',\n"+
		"hex(fp5),'-',\n"+
		"hex(fp6),'-',\n"+
		"hex(fp7),'-',\n"+
		"hex(fp8),'-',\n"+
		"hex(fp9),'-',\n"+
		"hex(fp10),'-',\n"+
		"hex(fp11),'-',\n"+
		"hex(fp12),'-',\n"+
		"hex(fp13),'-',\n"+
		"hex(fp14),'-',\n"+
		"hex(fp15),'-',\n"+
		"hex(fp16)\n"+
		") x,\n"+
		"count(*) c\n"+
		"FROM %s f\n"+
		"group by\n"+
		"fp1,fp2,fp3,fp4,fp5,fp6,fp7,fp8,\n" +
		"fp9,fp10,fp11,fp12,fp13,fp14,fp15,fp16\n"+
		"%s";

	@Override
	public double calculateMetric(IFingerprint<FPTable, String> object) {
		return 1;
	}

	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		return null;
	}

	@Override
	public String getSQL() throws AmbitException {
		return String.format(sql,getValue().getType().getTable(),getOrder().getSQL());
	}

	@Override
	public IFingerprint<FPTable,String> getObject(ResultSet rs) throws AmbitException {
		IFingerprint<FPTable,String> f = new Fingerprint<FPTable,String>();
		try {
			f.setBits(rs.getString(1));
			f.setFrequency(rs.getInt(2));
			f.setType(getValue().getType());
			return f;
		} catch (SQLException x) {
			throw new AmbitException(x);
		}
	}

}
