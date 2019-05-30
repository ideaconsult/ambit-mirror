package net.idea.ambit.templates.db;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ambit2.db.search.SQLFileQueryParams;
import net.enanomapper.maker.TemplateMakerSettings;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.restnet.db.aalocal.user.IDBConfig;

public class ExperimentTemplateQuery extends SQLFileQueryParams implements IDBConfig {
	protected String dbname = null;
	protected ObjectMapper dx = new ObjectMapper();
	//private static String _params = "{\"params\" : {\":all\" : { \"type\" : \"boolean\", \"value\": %s}, \":s_prefix\" : { \"type\" : \"String\", \"value\": \"%s\"},\":s_uuid\" : { \"type\" : \"String\", \"value\":\"%s\"}	}}";
	private static String _params_i = "{\"params\" : {\":%s\" : { \"type\" : \"String\", \"value\":\"%s\"}	}}";

	public enum _QUERY_TYPE_TEMPLATE {
		all {
			@Override
			public String queryParams(String... params) {
				return null;
			}
			@Override	
			public String getSQL() {
				return "net/idea/ambit/templates/db/template_all.sql";
			}
			
		},
		byidtemplate {
			@Override
			public String getField() {
				return "_idtemplate";
			}

			@Override	
			public String getSQL() {
				return "net/idea/ambit/templates/db/template.sql";
			}
		},

		;
		public String getField() {
			return null;
		}

		public String queryParams(String... params) {
			if (params == null || params.length < 1)
				return null;
			else
				return String.format(_params_i, getField(), params[0]);
		}

		public String getSQL() {
			return "net/idea/ambit/templates/db/template.sql";
		}

		public boolean preprocessing() {
			return false;
		}

	};

	public ExperimentTemplateQuery(TemplateMakerSettings settings) throws IOException {
		this(settings,settings.getQueryTemplateId()==null?_QUERY_TYPE_TEMPLATE.all:_QUERY_TYPE_TEMPLATE.byidtemplate,settings.getQueryTemplateId()==null?null:new String[] {settings.getQueryTemplateId()});
	}
	public ExperimentTemplateQuery(TemplateMakerSettings settings, _QUERY_TYPE_TEMPLATE query,String[] params) throws IOException {
		this(query.getSQL(),query.queryParams(params));
	}

	public ExperimentTemplateQuery(_QUERY_TYPE_TEMPLATE queryType) throws IOException {
		this(queryType.getSQL(), queryType.queryParams());
	}	
	public ExperimentTemplateQuery(_QUERY_TYPE_TEMPLATE queryType, String... params) throws IOException {
		this(queryType.getSQL(), queryType.queryParams(params));
	}	
	public ExperimentTemplateQuery(String sqlResource, String json) throws IOException {
		this(sqlResource, (ObjectNode) null);
		JsonNode node = json2params(json);
		if (node != null && node instanceof ObjectNode)
			setValue((ObjectNode) node);
	}
	
	public JsonNode json2params(String json) throws JsonProcessingException, IOException {
		if (json == null)
			return null;
		JsonNode node = dx.readTree(json);
		return node.get("params");
	}

	
	public ExperimentTemplateQuery(String resourceName, ObjectNode params) throws IOException {
		super(resourceName, params);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7508405780091117532L;

	@Override
	public void setDatabaseName(String name) {
		this.dbname = name;
		
	}

	@Override
	public String getDatabaseName() {
		return dbname;
	}

	@Override
	public String getSQL() throws AmbitException {
		String sql =  String.format(super.getSQL(), getDatabaseName()==null?"":String.format(".%s",getDatabaseName()));
		return sql;
	}
}
