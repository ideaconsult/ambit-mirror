package ambit2.db.simiparity.space;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.conditions.StringCondition;
import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;
import ambit2.base.data.study.ReliabilityParams._r_flags;
import ambit2.base.data.substance.SubstanceProperty;
import ambit2.base.interfaces.IStructureRecord;
import ambit2.db.search.AbstractQuery;

/**
 * Reads {@link QMap}
 * 
 * @author nina
 * 
 */
public class QueryQMap extends
		AbstractQuery<IStructureRecord, QMap, StringCondition, QMap> implements
		IQueryRetrieval<QMap> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5648740520878436166L;
	private static String sql = "SELECT idsasmap,id_srcdataset,idproperty,threshold_dact,threshold_sim,"
			+ "d.name datasetname,d.idreference,d.created,d.idtemplate,d.licenseURI,d.rightsHolder,d.maintainer,d.stars,\n"
			+ "p.name propertyname,p.units,p.comments,topcategory,endpointcategory,guidance,reference\n"
			+ "FROM qsasheader\n"
			+ "left join src_dataset d using(id_srcdataset) left join properties p using(idproperty)\n"
			+ "%s\n" + "order by id_srcdataset,idproperty";

	protected StringBuilder condition = null;

	@Override
	public String getSQL() throws AmbitException {
		if (getValue() != null) {
			if ((getValue().getId() > 0)) {
				initCondition();
				condition.append("idsasmap=?");
			} else {
				if ((getValue().getDataset() != null)
						&& (getValue().getDataset().getID() > 0)) {
					if (!initCondition())
						condition.append(" and ");
					condition.append("id_srcdataset=?");
				}
				if ((getValue().getProperty() != null)
						&& (getValue().getProperty().getId() > 0)) {
					if (!initCondition())
						condition.append(" and ");
					condition.append("idproperty=?");
				}
			}
		}
		return String.format(sql, condition == null ? "" : condition);
	}

	protected boolean initCondition() {
		if (condition == null) {
			condition = new StringBuilder();
			condition.append(" where ");
			return true;
		} else
			return false;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		if (getValue() != null) {
			if ((getValue().getId() > 0)) {
				params.add(new QueryParam<Integer>(Integer.class, getValue()
						.getId()));
				return params;
			}
			if ((getValue().getDataset() != null)
					&& (getValue().getDataset().getID() > 0)) {
				params.add(new QueryParam<Integer>(Integer.class, getValue()
						.getDataset().getID()));
			}
			if ((getValue().getProperty() != null)
					&& (getValue().getProperty().getId() > 0)) {
				params.add(new QueryParam<Integer>(Integer.class, getValue()
						.getProperty().getId()));
			}
		}
		return params;
	}

	@Override
	public QMap getObject(ResultSet rs) throws AmbitException {
		QMap qmap = new QMap();
		try {
			qmap.setId(rs.getInt(1));
			if (rs.getInt(2) <= 0) {
				qmap.setDataset(new SourceDataset(rs.getString("reference")));
				qmap.getDataset().setID(-1);
			} else {
				qmap.setDataset(new SourceDataset(rs.getString("datasetname")));
				qmap.getDataset().setID(rs.getInt(2));
			}
			qmap.getDataset().setStars(rs.getInt("stars"));
			int idproperty = rs.getInt(3);
			if (idproperty > 0) {
				qmap.setProperty(new Property(rs.getString("propertyname")));
				qmap.getProperty().setId(rs.getInt(3));
			} else {
				SubstanceProperty prop = new SubstanceProperty(
						rs.getString("topcategory"),
						rs.getString("endpointCategory"),
						rs.getString("guidance"), rs.getString("reference"));
				prop.setStudyResultType(_r_flags.experimentalresult);
				prop.setEnabled(true);
				qmap.setProperty(prop);
			}
			qmap.setActivityThreshold(rs.getDouble(4));
			qmap.setSimilarityThreshold(rs.getDouble(5));
			return qmap;
		} catch (Exception x) {
			throw new AmbitException(x);
		}

	}

	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public double calculateMetric(QMap object) {
		return 1;
	}

}
