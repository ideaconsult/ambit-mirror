package ambit2.db.update.dictionary;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import ambit2.base.data.Property;
import ambit2.base.data.SourceDataset;

public class DatasetTemplateAddProperty  extends AbstractUpdate<SourceDataset,Property> {
	
	private static final String[] create_sql = 
	{"INSERT IGNORE INTO template_def (idtemplate,idproperty,`order`) SELECT idtemplate,?,? FROM src_dataset where id_srcdataset=?" };


	public DatasetTemplateAddProperty(SourceDataset template,Property property) {
		super();
		setGroup(template);
		setObject(property);
	}
	
	public DatasetTemplateAddProperty() {
		this(null,null);
	}

	public List<QueryParam> getParameters(int index) throws AmbitException {
			List<QueryParam> params1 = new ArrayList<QueryParam>();
			params1.add(new QueryParam<Integer>(Integer.class, getObject().getId()));
			params1.add(new QueryParam<Integer>(Integer.class, getObject().getOrder()));
			params1.add(new QueryParam<Integer>(Integer.class, getGroup().getId()));
			return params1;
	}

	

	public String[] getSQL() throws AmbitException {
		return create_sql;
	}
	@Override
	public boolean returnKeys(int index) {

		return false;
	}

	@Override
	public void setID(int arg0, int arg1) {
	}

}
