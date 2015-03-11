package ambit2.db.search.structure.pairwise;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.IStoredProcStatement;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.ISourceDataset;
import ambit2.base.data.Property;
import ambit2.db.search.AbstractQuery;
import ambit2.db.search.BooleanCondition;

/**
 * 
 * @author nina
 * 
 */
public class ChemicalSpaceQuery extends AbstractQuery<ISourceDataset, Property, BooleanCondition, ChemSpaceCell>
	implements IQueryRetrieval<ChemSpaceCell>, IStoredProcStatement {
    protected ChemSpaceCell cell = new ChemSpaceCell();
    protected ChemSpaceMethod method = ChemSpaceMethod.g2cell;
    protected double threshold_similarity = 0.75;
    protected double threshold_dactivity = 5;

    public double getThreshold_dactivity() {
	return threshold_dactivity;
    }

    public void setThreshold_dactivity(double threshold_dactivity) {
	this.threshold_dactivity = threshold_dactivity;
    }

    public double getThreshold_similarity() {
	return threshold_similarity;
    }

    public void setThreshold_similarity(double threshold_similarity) {
	this.threshold_similarity = threshold_similarity;
    }

    public ChemSpaceMethod getMethod() {
	return method;
    }

    public void setMethod(ChemSpaceMethod method) {
	this.method = method;
    }

    public enum ChemSpaceMethod {
	maxcellsim, g2cell, fishercell, numsim, g2row, numcliff, sali
    }

    protected int maxPoints = 15;

    public int getMaxPoints() {
	return maxPoints;
    }

    public void setMaxPoints(int maxPoints) {
	if (maxPoints > 0)
	    this.maxPoints = maxPoints;
    }

    /**
	 * 
	 */
    private static final long serialVersionUID = 169838296668703784L;
    protected final String sql = "{call tanimotoBinnedSpace(?,?,?,?,?,?)}";

    @Override
    public String getSQL() throws AmbitException {
	return sql;
    }

    @Override
    public void registerOutParameters(CallableStatement statement) throws SQLException {

    }

    @Override
    public List<QueryParam> getParameters() throws AmbitException {

	List<QueryParam> params = new ArrayList<QueryParam>();
	params.add(new QueryParam<Integer>(Integer.class, getValue().getId()));
	params.add(new QueryParam<Integer>(Integer.class, getFieldname().getID()));
	params.add(new QueryParam<Integer>(Integer.class, getMaxPoints()));
	params.add(new QueryParam<Integer>(Integer.class, getMethod().ordinal()));
	params.add(new QueryParam<Double>(Double.class, getThreshold_similarity()));
	params.add(new QueryParam<Double>(Double.class, getThreshold_dactivity()));
	return params;
    }

    @Override
    public ChemSpaceCell getObject(ResultSet rs) throws AmbitException {
	try {
	    cell.setIndex(rs.getInt(1), rs.getInt(4));
	    cell.setLeft(rs.getDouble(2), rs.getDouble(3));
	    cell.setRight(rs.getDouble(5), rs.getDouble(6));
	    cell.setDistance(rs.getDouble(7));
	    return cell;
	} catch (SQLException x) {
	    throw new AmbitException(x);
	}
    }

    @Override
    public double calculateMetric(ChemSpaceCell object) {
	return 1;
    }

    @Override
    public boolean isPrescreen() {
	return false;
    }

    @Override
    public void getStoredProcedureOutVars(CallableStatement statement) throws SQLException {
	// do nothing
    }

    
    @Override
    public boolean supportsPaging() {
	return true;
    }

    @Override
    public String toString() {
	if ((getFieldname() == null) && (getValue() == null))
	    return getClass().getName();
	StringBuilder b = new StringBuilder();
	if (getFieldname() == null)
	    b.append("Property");
	else
	    b.append(getFieldname());
	b.append(' ');
	b.append(getCondition() == null ? "" : getCondition());
	b.append(' ');
	if (getValue() != null)
	    b.append(getValue());
	return b.toString();
    }
}
