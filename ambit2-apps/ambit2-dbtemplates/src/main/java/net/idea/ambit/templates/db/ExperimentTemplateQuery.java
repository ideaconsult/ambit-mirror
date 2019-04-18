package net.idea.ambit.templates.db;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ambit2.db.search.SQLFileQueryParams;
import net.enanomapper.maker.TemplateMakerSettings;

public class ExperimentTemplateQuery extends SQLFileQueryParams {

	protected ObjectMapper dx = new ObjectMapper();
	//private static String _params = "{\"params\" : {\":all\" : { \"type\" : \"boolean\", \"value\": %s}, \":s_prefix\" : { \"type\" : \"String\", \"value\": \"%s\"},\":s_uuid\" : { \"type\" : \"String\", \"value\":\"%s\"}	}}";
	private static String _params_i = "{\"params\" : {\":%s\" : { \"type\" : \"String\", \"value\":\"%s\"}	}}";

	public enum _QUERY_TYPE_TEMPLATE {
		byendpoint {
			@Override
			public String getField() {
				return "_endpoint";
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
		this(_QUERY_TYPE_TEMPLATE.byendpoint.getSQL(),_QUERY_TYPE_TEMPLATE.byendpoint.queryParams(new String[] {settings.getEndpointname()}));
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
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7508405780091117532L;

}
