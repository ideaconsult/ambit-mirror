package ambit2.core.io.study;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import ambit2.base.data.PropertyAnnotation;
import ambit2.base.data.PropertyAnnotations;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.IValue;
import ambit2.base.data.study.MultiValue;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.Protocol._categories;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.data.study.ReliabilityParams._r_flags;
import ambit2.base.data.substance.SubstanceProperty;
import ambit2.core.io.json.SubstanceStudyParser;

public class StudyFormatter {
	protected Logger logger = Logger.getLogger(getClass().getName());
	private ObjectMapper dx;
	protected JsonNode columns;
	public static final int _MAX_COL = 16383;
	protected ObjectMapper mapper = new ObjectMapper();

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
					if (node == null) {
						if (!"interpretation_result".equals(property.getName())) {
							b.append(property.getName());
							b.append(" =");
						}
					} else {
						b.append(node.asText());
						b.append(" =");
					}

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

				JsonNode titleInMatrix = condition == null ? null : condition
						.get("titleInMatrix");

				if (conditionInMatrix != null
						&& conditionInMatrix.asBoolean(false)) {
					if (!hasConditions)
						b.append(" (");
					else
						b.append(" ,");

					if (titleInMatrix == null || titleInMatrix.asBoolean(true)) {
						b.append(pa.getPredicate());
						b.append("=");
					}
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

	public ObjectNode getCitationConfig(ObjectNode column) {
		ObjectNode protocolConfig = getProtocolConfig(column);
		return protocolConfig == null ? null : (ObjectNode) protocolConfig
				.get("citation");
	}

	public ObjectNode getOwnerConfig(ObjectNode column) {
		ObjectNode protocolConfig = getProtocolConfig(column);
		return protocolConfig == null ? null : (ObjectNode) protocolConfig
				.get("owner");
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

	public boolean isVisible(ObjectNode config) {
		if (config == null)
			return false;
		JsonNode bVisible = config.get("bVisible");
		return bVisible == null ? true : bVisible.asBoolean(false);
	}

	public int getOrder(ObjectNode config) {
		if (config == null)
			return _MAX_COL;
		JsonNode order = config.get("iOrder");
		if (order != null)
			return order.asInt();
		else
			return _MAX_COL;
	}

	private int getColumn(ObjectNode config) {
		if (config == null)
			return _MAX_COL;
		JsonNode order = config.get("iColumn");
		if (order != null)
			return order.asInt();
		else
			return _MAX_COL;
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

	protected void assignTitle(ObjectNode config, ObjectNode defaultConfig,
			String value) {
		if (config.get("sTitle") == null) {
			JsonNode title = defaultConfig == null ? null : defaultConfig
					.get("sTitle");
			config.put("sTitle", title == null ? value : title.asText());
		}
	}

	public void formatCategoryHeader(String category, IStudyPrinter printer)
			throws Exception {
		ObjectNode defaultColumn = (ObjectNode) columns.get("_");

		ObjectNode column = (ObjectNode) columns.get(category);
		List<ObjectNode> ordered = new ArrayList<ObjectNode>();

		if (column == null)
			column = defaultColumn;

		ObjectNode config = getGuidelineConfig(column);
		if (isVisible(config)) {
			ordered.add(config);
			assignTitle(config, getGuidelineConfig(defaultColumn), "Guideline");
		}

		config = getCitationConfig(column);
		if (isVisible(config)) {
			ordered.add(config);
			assignTitle(config, getCitationConfig(defaultColumn), "Reference");
		}

		config = getInterpretationResultConfig(column);
		if (isVisible(config)) {
			ordered.add(config);
			assignTitle(config, getInterpretationResultConfig(defaultColumn),
					"Interpretation result");
		}

		config = getInterpretationResultCriteria(column);
		if (isVisible(config)) {
			ordered.add(config);
			assignTitle(config, getInterpretationResultCriteria(defaultColumn),
					"Interpretation criteria");
		}

		config = getOwnerConfig(column);
		if (config == null) {
			config = getOwnerConfig(defaultColumn);
			ObjectNode pConfig = getProtocolConfig(column);
			if (pConfig == null) {
				try {
					pConfig = getProtocolConfig(defaultColumn);
					column.put("protocol", pConfig);
				} catch (Exception x) {
					x.printStackTrace();
				}
			} else
				config.put("owner", config);
		}
		if (isVisible(config))
			ordered.add(config);
		/**
		 * Parameters
		 */
		config = getParametersConfig(column);
		if (config == null)
			config = getParametersConfig(defaultColumn);
		Iterator<String> iFields = config.getFieldNames();
		while (iFields.hasNext())
			try {
				String field = iFields.next();
				ObjectNode param = (ObjectNode) config.get(field);
				if (isVisible(param)) {
					ordered.add(param);
					assignTitle(param, null, field);
				}
			} catch (Exception x) {
			} finally {

			}
		/**
		 * Conditions
		 */
		config = getConditionsConfig(column);
		if (config == null)
			config = getConditionsConfig(defaultColumn);
		iFields = config.getFieldNames();
		while (iFields.hasNext())
			try {
				String field = iFields.next();
				ObjectNode param = (ObjectNode) config.get(field);
				if (isVisible(param)) {
					ordered.add(param);
					assignTitle(param, null, field);
				}
			} catch (Exception x) {
			} finally {

			}
		/**
		 * Results
		 */
		config = getEndpointConfig(column);
		if (config == null)
			config = getEndpointConfig(defaultColumn);
		if (isVisible(config)) {
			ordered.add(config);
			assignTitle(config, getEndpointConfig(defaultColumn), "Endpoint");
		}
		config = getResultConfig(column);
		if (config == null)
			config = getResultConfig(defaultColumn);
		if (isVisible(config)) {
			ordered.add(config);
			assignTitle(config, getResultConfig(defaultColumn), "Value");
		}
		config = getTextResultConfig(column);
		if (config == null)
			config = getTextResultConfig(defaultColumn);
		if (isVisible(config)) {
			ordered.add(config);
			assignTitle(config, getTextResultConfig(defaultColumn),
					"Text value");
		}
		/**
		 * Now sort all these
		 */
		Collections.sort(ordered, new Comparator<ObjectNode>() {
			@Override
			public int compare(ObjectNode o1, ObjectNode o2) {
				return getOrder(o1) - getOrder(o2);
			}
		});

		for (int i = 0; i < ordered.size(); i++) {
			ordered.get(i).put("iColumn", i + 1);
			if (printer != null) {
				JsonNode title = ordered.get(i).get("sTitle");
				printer.printHeader(0, (i + 1), (i + 1), title == null ? "??"
						: title.asText());
			}
		}

	}

	public void format(
			final ProtocolApplication<Protocol, String, String, IParams, String> pa,
			IStudyPrinter printer) throws Exception {

		_r_flags flag = null;
		try {
			for (_r_flags rf : _r_flags.values())
				if (rf.toString().equals(
						pa.getReliability().getStudyResultType().toString())) {
					flag = rf;
					break;
				}
			;
		} catch (Exception x) {
		}

		ObjectNode column = (ObjectNode) columns.get(pa.getProtocol()
				.getCategory());
		// defaultColumn
		// TODO default column

		ObjectNode config = getGuidelineConfig(column);
		if (isVisible(config))
			try {
				printer.print(0, getColumn(config), getOrder(config), pa
						.getProtocol().getGuideline().get(0), false, flag);
			} catch (Exception x) {
				logger.log(Level.WARNING, x.getMessage());
			}

		config = getCitationConfig(column);

		if (isVisible(config))
			try {
				printer.print(0, getColumn(config), getOrder(config),
						pa.getReferenceYear(), false, flag);
			} catch (Exception x) {
				logger.log(Level.WARNING, x.getMessage());
			}

		config = getOwnerConfig(column);
		if (isVisible(config))
			printer.print(0, getColumn(config), getOrder(config),
					pa.getReferenceOwner(), false, flag);

		config = getInterpretationResultConfig(column);
		if (isVisible(config)) {
			printer.print(0, getColumn(config), getOrder(config),
					pa.getInterpretationResult(), true, flag);
		}
		config = getInterpretationResultCriteria(column);
		if (isVisible(config)) {
			printer.print(0, getColumn(config), getOrder(config),
					pa.getInterpretationCriteria(), false, flag);
		}

		/**
		 * Parameters
		 */

		try {

			JsonNode conditions = mapper.readTree(new StringReader(pa
					.getParameters()));
			if (conditions instanceof ObjectNode) {
				IParams params = SubstanceStudyParser
						.parseParams((ObjectNode) conditions);

				config = getParametersConfig(column);

				Iterator<Map.Entry> iFields = params.entrySet().iterator();
				while (iFields.hasNext())
					try {
						Map.Entry entry = iFields.next();
						Object field = entry.getKey();
						ObjectNode param = (ObjectNode) config.get(field
								.toString().toLowerCase());
						if (isVisible(param)) {
							String value = entry.getValue().toString();
							printer.print(0, getColumn(param), getOrder(param),
									value, false, flag);
						}
					} catch (Exception x) {
					} finally {

					}
			}

		} catch (Exception x) {
		}
		/**
		 * Effects
		 */
		int row = 0;
		if (pa.getEffects() != null)
			for (EffectRecord<String, IParams, String> effect : pa.getEffects()) {
				config = getEndpointConfig(column);
				if (isVisible(config))
					printer.print(row, getColumn(config), getOrder(config),
							effect.getEndpoint(), false, flag);

				config = getResultConfig(column);
				if (isVisible(config))
					printer.print(row, getColumn(config), getOrder(config),
							String.format(
									"%s%s %s%s %s",
									effect.getLoQualifier() == null ? ""
											: effect.getLoQualifier(),
									effect.getLoValue() == null ? "" : effect
											.getLoValue(),
									effect.getUpQualifier() == null ? ""
											: effect.getUpQualifier(),
									effect.getUpValue() == null ? "" : effect
											.getUpValue(),
									effect.getUnit() == null ? "" : effect
											.getUnit()), true, flag);

				config = getTextResultConfig(column);
				if ((effect.getTextValue() != null) && isVisible(config))
					printer.print(row, getColumn(config), getOrder(config),
							effect.getTextValue().toString(), true, flag);
				row++;
			}

	}
}
