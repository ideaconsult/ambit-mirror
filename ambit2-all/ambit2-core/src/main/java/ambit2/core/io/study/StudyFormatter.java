package ambit2.core.io.study;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import ambit2.base.data.PropertyAnnotation;
import ambit2.base.data.PropertyAnnotations;
import ambit2.base.data.study.IValue;
import ambit2.base.data.study.MultiValue;
import ambit2.base.data.study.Protocol._categories;
import ambit2.base.data.substance.SubstanceProperty;

public class StudyFormatter {
	protected Logger logger = Logger.getLogger(getClass().getName());
	private ObjectMapper dx;
	protected JsonNode columns;

	public StudyFormatter() {
		super();
		dx = new ObjectMapper();
		InputStream in = null;
		try {
			in = this
					.getClass()
					.getClassLoader()
					.getResourceAsStream(
							"ambit2/base/data/study/config-study.js");
			JsonNode dxRoot = dx.readTree(in);
			columns = dxRoot.get("columns");
		} catch (Exception x) {
			logger.log(Level.WARNING, x.getMessage());
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception x) {
			}
		}
	}

	public String format(SubstanceProperty property, Object value)
			throws Exception {
		if (value == null)
			return null;
		if (value instanceof MultiValue)
			return format(property, (MultiValue) value);
		else if (value instanceof IValue)
			return format(property, (IValue) value);
		else
			return format(property, value.toString());
	}

	public String format(SubstanceProperty property, String value)
			throws Exception {

		ObjectNode column = (ObjectNode) columns.get(property
				.getEndpointcategory());
		if (column == null)
			column = (ObjectNode) columns.get("SUPPORTING_INFO_SECTION");

		StringBuilder b = new StringBuilder();

		if ("GI_GENERAL_INFORM_SECTION".equals(property.getEndpointcategory())) {
			ObjectNode config = getEndpointConfig(column);
			if (inMatrix(config))
				b.append(property.getName());
			config = getInterpretationResultConfig(column);
			if (inMatrix(config)) {
				b.append(" ");
				b.append(value);
			}
		} else {

			if ("TO_GENETIC_IN_VITRO_SECTION".equals(property
					.getEndpointcategory())
					|| "TO_GENETIC_IN_VIVO_SECTION".equals(property
							.getEndpointcategory())) {
				ObjectNode config = getResultConfig(column);
				ObjectNode tconfig = getTextResultConfig(column);
				ObjectNode iconfig = getInterpretationResultConfig(column);
				if (inMatrix(config) || inMatrix(tconfig) || inMatrix(iconfig))
					b.append(value);
			} else if ("TO_BIODEG_WATER_SCREEN_SECTION".equals(property
					.getEndpointcategory())) {
				if (!"interpretation_result".equals(property.getName())) {
					b.append(property.getName());
					b.append(" =");
					b.append(value);
					if (property.getUnits() != null) {
						b.append(" ");
						b.append(property.getUnits());
					}
				} else
					b.append(value);

			} else {
				ObjectNode config = getEndpointConfig(column);
				if (inMatrix(config)) {
					JsonNode node = config.get("sTitle");
					if (node == null)
						b.append(property.getName());
					else
						b.append(node.asText());
					b.append(" =");

				}

				if ("interpretation_result".equals(property.getName())) {
					config = getInterpretationResultConfig(column);
					if (inMatrix(config)) {
						b.append(value);
					} else {
						throw new Exception(
								String.format(
										"%s: Found property name 'interpretation_result' , configured as not 'inMatrix'",
										property.getEndpointcategory()));
					}
				} else {
					config = getResultConfig(column);
					ObjectNode tconfig = getTextResultConfig(column);
					if (inMatrix(config) || inMatrix(tconfig)) {
						b.append(value);
						if (property.getUnits() != null) {
							b.append(" ");
							b.append(property.getUnits());
						}
					}
				}
			}

			ObjectNode config = getGuidelineConfig(column);
			if (inMatrix(config)) {
				if (property.getReference().getURL() != null) {
					b.append(" [");
					b.append(property.getReference().getURL());
					b.append("]");
				} else
					logger.log(Level.WARNING, "Protocol guidance missing");
			}
			config = getConditionsConfig(column);
			formatParams(b, config, property.getAnnotations());
			config = getParametersConfig(column);
			formatParams(b, config, property.getAnnotations());
		}
		return b.toString();
	}

	protected void formatParams(StringBuilder b, ObjectNode conditions,
			PropertyAnnotations pannotations) {
		boolean hasConditions = false;
		if (conditions != null)
			for (PropertyAnnotation pa : pannotations) {
				ObjectNode condition = conditions == null ? null
						: (ObjectNode) conditions.get(pa.getPredicate()
								.toString().toLowerCase());
				JsonNode conditionInMatrix = condition == null ? null
						: condition.get("inMatrix");
				if (conditionInMatrix != null
						&& conditionInMatrix.asBoolean(false)) {
					if (!hasConditions)
						b.append(" (");
					else
						b.append(" ,");
					b.append(pa.getPredicate());
					b.append("=");
					b.append(pa.getObject());
					hasConditions = true;
				}
				// System.out.println(pa.getType());
			}
		if (hasConditions)
			b.append(")");
	}

	public ObjectNode getCategoryConfig(_categories category) {
		return (ObjectNode) columns.get(category.name());
	}

	public ObjectNode getConditionsConfig(ObjectNode column) {
		return column == null ? null : (ObjectNode) column.get("conditions");
	}

	public ObjectNode getParametersConfig(ObjectNode column) {
		return column == null ? null : (ObjectNode) column.get("parameters");
	}

	public ObjectNode getProtocolConfig(ObjectNode column) {
		return column == null ? null : (ObjectNode) column.get("protocol");
	}

	public ObjectNode getEffectsConfig(ObjectNode column) {
		return column == null ? null : (ObjectNode) column.get("effects");
	}

	public ObjectNode getInterpretationConfig(ObjectNode column) {
		return column == null ? null : (ObjectNode) column
				.get("interpretation");
	}

	public ObjectNode getInterpretationResultConfig(ObjectNode column) {
		ObjectNode config = getInterpretationConfig(column);
		return config == null ? null : (ObjectNode) config.get("result");
	}

	public ObjectNode getInterpretationResultCriteria(ObjectNode column) {
		ObjectNode config = getInterpretationConfig(column);
		return config == null ? null : (ObjectNode) config.get("criteria");
	}

	public ObjectNode getGuidelineConfig(ObjectNode column) {
		ObjectNode protocolConfig = getProtocolConfig(column);
		return protocolConfig == null ? null : (ObjectNode) protocolConfig
				.get("guideline");
	}

	public ObjectNode getEndpointConfig(ObjectNode column) {
		ObjectNode effectConfig = getEffectsConfig(column);
		return effectConfig == null ? null : (ObjectNode) effectConfig
				.get("endpoint");
	}

	public ObjectNode getResultConfig(ObjectNode column) {
		ObjectNode effectConfig = getEffectsConfig(column);
		return effectConfig == null ? null : (ObjectNode) effectConfig
				.get("result");
	}

	public ObjectNode getTextResultConfig(ObjectNode column) {
		ObjectNode effectConfig = getEffectsConfig(column);
		return effectConfig == null ? null : (ObjectNode) effectConfig
				.get("text");
	}

	public boolean inMatrix(ObjectNode config) {
		if (config == null)
			return false;
		JsonNode inMatrix = config.get("inMatrix");
		return inMatrix == null ? false : inMatrix.asBoolean(false);
	}

	public String format(SubstanceProperty property, IValue value)
			throws Exception {
		return format(property, value.toHumanReadable());
	}

	public String format(SubstanceProperty property, MultiValue<IValue> value)
			throws Exception {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < ((MultiValue) value).size(); i++) {
			if (i > 0)
				b.append("\n");
			b.append(format(property, (IValue) ((MultiValue) value).get(i)));
		}
		return b.toString();
	}
}
