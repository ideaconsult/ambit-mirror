package ambit2.db.search;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import net.idea.modbcum.i.IJSONQueryParams;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.query.SQLFileQuery;

public class SQLFileQueryParams extends SQLFileQuery<ObjectNode> implements
		IJSONQueryParams {
	public enum _fields {
		name, connection, params, sql, order, value, processor
	};

	private enum _supportedprmtypes {
		STRING, INTEGER, INT, LONG, DOUBLE, FLOAT, BOOLEAN
	};

	protected List<QueryParam> queryParams;
	protected String psql;
	protected String jsonParams;

	public String getJsonParams() {
		return jsonParams;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -2420284940751695905L;

	public SQLFileQueryParams(File sqlFile, ObjectNode params)
			throws IOException {
		super(sqlFile);
		setValue(params);
	}

	public SQLFileQueryParams(String resourceName, ObjectNode params)
			throws IOException {
		super(resourceName);
		setValue(params);
	}

	@Override
	public void setValue(ObjectNode params) {
		if (params != null) {
			jsonParams = params.toString();
			queryParams = parseParams(params);
		}

	}

	protected List<QueryParam> parseParams(ObjectNode params) {
		List<Entry<String, JsonNode>> pnames = new ArrayList<Entry<String, JsonNode>>();
		psql = sql;
		if (params == null)
			return null;

		Iterator<Entry<String, JsonNode>> i = params.fields();
		while (i.hasNext()) {
			Entry<String, JsonNode> param = i.next();
			pnames.add(param);
			int index = sql.indexOf(param.getKey());
			if (index > 0) // ignore parameters not in sql
				((ObjectNode) param.getValue())
						.put(_fields.order.name(), index);
			psql = psql.replace(param.getKey(), "?");
		}
		Collections.sort(pnames, new Comparator<Entry<String, JsonNode>>() {
			@Override
			public int compare(Entry<String, JsonNode> o1,
					Entry<String, JsonNode> o2) {
				return ((ObjectNode) o1.getValue()).get(_fields.order.name())
						.asInt()
						- ((ObjectNode) o2.getValue())
								.get(_fields.order.name()).asInt();
			}
		});
		List<QueryParam> queryParams = null;
		for (Entry<String, JsonNode> param : pnames) {
			if (queryParams == null)
				queryParams = new ArrayList<QueryParam>();
			ObjectNode qparam = ((ObjectNode) param.getValue());
			_supportedprmtypes type = _supportedprmtypes.STRING;
			try {
				type = _supportedprmtypes.valueOf(qparam.get("type").asText()
						.toUpperCase());
			} catch (Exception x) {
				x.printStackTrace();
			}
			switch (type) {
			case STRING: {
				queryParams.add(new QueryParam<String>(String.class, qparam
						.get("value").asText().trim()));
				break;
			}
			case INTEGER: {
				queryParams.add(new QueryParam<Integer>(Integer.class, qparam
						.get("value").asInt()));
				break;
			}
			case LONG: {
				queryParams.add(new QueryParam<Long>(Long.class, qparam.get(
						"value").asLong()));
				break;
			}
			case DOUBLE: {
				queryParams.add(new QueryParam<Double>(Double.class, qparam
						.get("value").asDouble()));
				break;
			}
			case BOOLEAN: {
				queryParams.add(new QueryParam<Boolean>(Boolean.class, qparam
						.get("value").asBoolean()));
				break;
			}
			default: {
				queryParams.add(new QueryParam<String>(String.class, qparam
						.get("value").asText()));
			}
			}

		}
		return queryParams;
	}

	@Override
	protected String loadSQL(InputStream stream) throws IOException {
		String sql = super.loadSQL(stream);
		return sql;
	}

	@Override
	public String getSQL() throws AmbitException {
		return psql == null ? sql : psql;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		return queryParams;
	}
}
