package ambit2.base.data.study.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Params;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;
import ambit2.base.json.JSONUtils;
import junit.framework.Assert;

public class ProtocolApplicationTestFactory {

	public static ProtocolApplication initpa() {
		Protocol protocol = new Protocol("Short-term toxicity to fish, IUC4#53/Ch.4.1");
		protocol.setCategory("EC_FISHTOX_SECTION");
		protocol.addGuideline("Method: other: acute toxicity test; \"static bioassay\"");
		ProtocolApplication papp = new ProtocolApplication<Protocol, IParams, String, IParams, String>(protocol);
		IParams params = new Params();
		params.put("Test organism", "Lepomis cyanellus");
		papp.setParameters(params);
		papp.setReference("reference");
		papp.setDocumentUUID("IUC4-7adb0d03-f69b-32a9-9efe-86b4a8577893");
		EffectRecord record = new EffectRecord<String, IParams, String>();
		params = new Params();
		params.put("Exposure", "96");
		record.setConditions(params);
		record.setEndpoint("LC50");
		record.setUnit("mg/L");
		record.setLoValue(83.1);
		papp.addEffect(record);
		papp.setUpdated(getTimeStamp());
		return papp;
	}

	// s - timestamp in format yyyy-[m]m-[d]d hh:mm:ss[.f...]. The fractional
	// seconds may be omitted. The leading zero for mm and dd may also be
	// omitted.
	protected static Date getTimeStamp() {
		try {
			SimpleDateFormat formatter = ProtocolApplication.dateformatter;
			// String s = formatter.format(new Date());
			return formatter.parse("2017-11-1 0:0:0");
		} catch (ParseException x) {
			return null;
		}
	}

	@Test
	public void testTimeStamp() {
		Date t = getTimeStamp();
		Assert.assertNotNull(t);
	}

	public static ProtocolApplication initpc() {
		Protocol protocol = new Protocol("Partition coefficient, IUC4#5/Ch.2.5");
		protocol.setCategory("PC_PARTITION_SECTION");
		protocol.addGuideline("Method: other (measured)");
		ProtocolApplication papp = new ProtocolApplication<Protocol, IParams, String, IParams, String>(protocol);
		IParams params = new Params();
		papp.setParameters(params);
		papp.setReference("reference");
		papp.setDocumentUUID("IUC4-2f64ab27-d2be-352e-b9d8-4f0274fd6633");
		EffectRecord record = new EffectRecord<String, IParams, String>();
		params = new Params();
		params.put("Temperature", "25 \u2103C");
		record.setConditions(params);
		record.setEndpoint("log Pow");
		record.setLoValue(0.35);
		papp.addEffect(record);
		papp.setInvestigationUUID("2f64ab27-abcd-abcd-abcd-4f0274fd6633");
		papp.setUpdated(getTimeStamp());

		return papp;
	}

	public static ProtocolApplication initacutetox() {
		Protocol protocol = new Protocol("Acute toxicity: oral, IUC4#2/Ch.5.1.1");
		protocol.setCategory("TO_ACUTE_ORAL_SECTION");
		protocol.addGuideline("Method: other: no data");
		ProtocolApplication papp = new ProtocolApplication<Protocol, IParams, String, IParams, String>(protocol);
		IParams params = new Params();
		papp.setParameters(params);
		papp.setReference(
				"Smyth, H. F. Seaton J., and Fischer L. (1941).|The single dose toxicity of some glycols and derivatives.|J. Ind. Hyg. Toxicol. 23, 259-268");
		params.put("Species", "rat");
		params.put("Sex", "male/female");
		papp.setDocumentUUID("IUC4-ae64fc3b-22a4-3173-9362-9cce1ff622ae");
		EffectRecord record = new EffectRecord<String, IParams, String>();
		params = new Params();
		params.put("Temperature", "25 \u2103C");
		params.put("Sex", "male");
		record.setConditions(params);
		record.setEndpoint("LD50");
		record.setLoValue(260);
		record.setUpValue(320);
		record.setUnit("mg/kg bw");
		papp.addEffect(record);
		papp.setUpdated(getTimeStamp());
		return papp;
	}

	public static ProtocolApplication initbiodeg() {
		Protocol protocol = new Protocol("Biodegradation in water: screening tests, IUC4#1/Ch.3.5");
		protocol.setCategory("TO_BIODEG_WATER_SCREEN_SECTION");
		protocol.addGuideline("OECD Guideline 301 D (Ready Biodegradability: Closed Bottle Test)");
		ProtocolApplication papp = new ProtocolApplication<Protocol, IParams, String, IParams, String>(protocol);
		IParams params = new Params();
		papp.setParameters(params);
		papp.setReference("reference");
		papp.setDocumentUUID("IUC4-1d75f01c-3b2b-35f5-84f1-ce23e22b6c73");
		EffectRecord record = new EffectRecord<String, Params, String>();
		params = new Params();
		params.put("Time point", "28 d");
		record.setConditions(params);
		record.setEndpoint("% Degradation");
		record.setLoValue(90);
		record.setUnit("%");
		papp.addEffect(record);
		papp.setUpdated(getTimeStamp());
		return papp;
	}

	@Test
	public void test_categories() throws Exception {
		StringBuilder b = null;

		for (Protocol._categories c : Protocol._categories.values()) {
			if (b == null) {
				b = new StringBuilder();
				b.append("[\n");
			} else
				b.append(",");

			b.append("{\n");
			b.append(String.format("%s:%s", JSONUtils.jsonQuote("id"), JSONUtils.jsonQuote(c.name())));
			b.append(",\n");
			b.append(String.format("%s:[%s]", JSONUtils.jsonQuote("name"), JSONUtils.jsonQuote(c.toString())));
			b.append(",\n");
			b.append(String.format("%s:\"%s\"", JSONUtils.jsonQuote("ontology"), "IUCLID_DERIVED"));
			b.append(",\n");
			b.append(String.format("%s:[%s]", JSONUtils.jsonQuote("definition"),
					JSONUtils.jsonQuote(String.format("%s. %s", c.getNumber(), c.name()))));
			b.append(",\n");
			b.append(String.format("%s:[%s]", JSONUtils.jsonQuote("URI"), JSONUtils.jsonQuote(c.getOntologyURI())));
			b.append(",\n");
			b.append(String.format("%s:[%s]", JSONUtils.jsonQuote("topcategory"),
					JSONUtils.jsonQuote(c.getTopCategory())));

			b.append("}\n");

		}
		b.append("\n]");
		System.out.println(b.toString());
	}
}
