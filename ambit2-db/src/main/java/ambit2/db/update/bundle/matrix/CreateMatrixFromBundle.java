package ambit2.db.update.bundle.matrix;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.base.data.substance.SubstanceEndpointsBundle;
import ambit2.db.update.AbstractObjectUpdate;

public class CreateMatrixFromBundle extends AbstractObjectUpdate<SubstanceEndpointsBundle> {
	protected boolean deleteMatrix = false;
	public boolean isDeleteMatrix() {
		return deleteMatrix;
	}

	public void setDeleteMatrix(boolean deleteMatrix) {
		this.deleteMatrix = deleteMatrix;
	}
	
	private static String _insert_papp = 
	"insert into bundle_substance_protocolapplication SELECT e.idbundle,document_prefix,document_uuid,\n"+
	"p.topcategory,p.endpointcategory,endpoint,guidance,p.substance_prefix,p.substance_uuid,params,\n"+
	"interpretation_result,interpretation_criteria,reference,reference_year,reference_owner,updated,\n"+
	"reliability,isRobustStudy,isUsedforClassification,isUsedforMSDS,purposeFlag,studyResultType \n"+
	"FROM substance_protocolapplication p join bundle_endpoints e join bundle_substance s\n"+ 
	"where e.idbundle=s.idbundle and e.idbundle=?\n"+ 
	"and s.substance_prefix = p.substance_prefix and p.substance_uuid=s.substance_uuid\n"+
	"and e.topcategory=p.topcategory and p.endpointcategory=e.endpointcategory";

	private static String _insert_exp = 
	"insert into bundle_substance_experiment SELECT e.idbundle,null,document_prefix,document_uuid,p.topcategory,p.endpointcategory,p.endpointhash,endpoint,\n"+
	"conditions,	unit,	loQualifier,loValue,upQualifier,upvalue,textvalue,errQualifier,err,	p.substance_prefix,	p.substance_uuid\n"+
	"FROM substance_experiment p join bundle_endpoints e join bundle_substance s\n"+
	"where e.idbundle=s.idbundle and e.idbundle=? and s.substance_prefix = p.substance_prefix and p.substance_uuid=s.substance_uuid\n"+
	"and e.topcategory=p.topcategory and p.endpointcategory=e.endpointcategory \n";

	private static String _delete_papp = "delete from bundle_substance_protocolapplication where idbundle=?";
	private static String _delete_exp =  "delete from bundle_substance_experiment where idbundle=?";
	
	private final static String[] sql_insert = new String[] {
		_insert_papp,_insert_exp
	};
	
	private final static String[] sql_delete = new String[] {
		_delete_exp,_delete_papp,_insert_papp,_insert_exp
	};
	public CreateMatrixFromBundle(SubstanceEndpointsBundle bundle) {
		this(bundle,false);
	}
	public CreateMatrixFromBundle(SubstanceEndpointsBundle bundle,boolean deleteMatrix) {
		super();
		setObject(bundle);
		setDeleteMatrix(deleteMatrix);
	}
	
	@Override
	public String[] getSQL() throws AmbitException {
		return deleteMatrix?sql_delete:sql_insert;
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
