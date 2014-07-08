package ambit2.db.search.property;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import ambit2.core.data.model.ModelQueryResults;
import ambit2.db.search.StringCondition;

public class ModelTemplates extends AbstractPropertyRetrieval<ModelQueryResults, String, StringCondition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4888110865139617053L;
	
	protected String sql = String.format("%s\n%s%s",
			base_sql,
			"join template_def using(idproperty)\n",
			"join models on models.%s=template_def.idtemplate\n%s"
			);
	protected String where = "where idmodel=?";
	protected String whereName = "where models.name %s ?";
	
	protected enum _ModelTemplates {
		predicted {
			@Override
			public String getTableColumn() {
				return "predicted";
			}
		},
		independent{
			@Override
			public String getTableColumn() {
				return "predictors";
			}
		},
		predictors{
			@Override
			public String getTableColumn() {
				return "predictors";
			}
		},		
		target {
			@Override
			public String getTableColumn() {
				return "dependent";
			}
		},		
		dependent {
			@Override
			public String getTableColumn() {
				return "dependent";
			}
		};
		public abstract String getTableColumn();
	};
	
	public ModelTemplates() {
		super();
		setCondition(StringCondition.getInstance("regexp"));
	}
	@Override
	public void setValue(String value) {
		try {
			super.setValue(_ModelTemplates.valueOf(value).getTableColumn());
		} catch (Exception x) {
			super.setValue(_ModelTemplates.predicted.getTableColumn());
		}
	}
	public List<QueryParam> getParameters() throws AmbitException {
		if (getFieldname()!= null) {
			List<QueryParam> param = new ArrayList<QueryParam>();
			if (getFieldname().getId()>0)
				param.add(new QueryParam<Integer>(Integer.class,getFieldname().getId()));
			else
				param.add(new QueryParam<String>(String.class,getFieldname().getName()));
			return param;
		} else return null;
	}

	public String getSQL() throws AmbitException {
		return String.format(sql, 
				_ModelTemplates.valueOf(getValue()).getTableColumn(),
				getFieldname()==null?"":
					getFieldname().getId()>0?where:String.format(whereName,getCondition().getSQL())
				);
	}

}
