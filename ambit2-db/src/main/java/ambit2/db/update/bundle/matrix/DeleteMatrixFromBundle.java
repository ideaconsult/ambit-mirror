package ambit2.db.update.bundle.matrix;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.update.AbstractObjectUpdate;

public class DeleteMatrixFromBundle  extends AbstractObjectUpdate<SubstanceEndpointsBundle> {
	
	private final static String[] sql = new String[] {
	"delete from bundle_substance_protocolapplication where idbundle=?\n",
	"delete from bundle_substance_experiment where idbundle=?\n"

	};
	
	public DeleteMatrixFromBundle(SubstanceEndpointsBundle bundle) {
		super();
		setObject(bundle);
	}
	
	@Override
	public String[] getSQL() throws AmbitException {
		return sql;
	}

	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		if (getObject()==null || getObject().getID()<=0) throw new AmbitException("Bundle not defined");
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<Integer>(Integer.class, getObject().getID()));
		return params;
	}

	@Override
	public void setID(int index, int id) {
	}

}
