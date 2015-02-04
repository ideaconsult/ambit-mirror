package ambit2.base.data.study.test;

import junit.framework.Assert;

import org.junit.Test;

import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Params;
import ambit2.base.data.study.Value;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public class EffectRecordTest {

    @Test
    public void testStdDev() throws Exception {
	EffectRecord<String, Params, String> effectRecord = new EffectRecord<String, Params, String>();
	effectRecord.setConditions(new Params());
	effectRecord.setStdDev(1, "mm");
	Assert.assertEquals(1.0, effectRecord.getStdDev().getLoValue());
	Assert.assertEquals("mm", effectRecord.getStdDev().getUnits());
    }

    @Test
    public void testStdDev_notinitialized() throws Exception {
	EffectRecord<String, Params, String> effectRecord = new EffectRecord<String, Params, String>();
	try {
	    effectRecord.setStdDev(1, "mm");
	    Assert.fail("Should throw exception instead!");
	} catch (UnsupportedOperationException x) {
	}
    }

    @Test
    public void testValue() throws Exception {
	Value value = new Value();
	value.setUnits("mg");
	value.setLoValue(3.14);
	value.setLoQualifier("ca.");
	value.setUpQualifier("<=");
	value.setUpValue("6.28");
	System.out.println(value.toString());
    }

    @Test
    public void testEndpointHash() throws Exception {
	HashFunction hf = Hashing.sha1();
	HashCode hc = hf.newHasher()
	       .putString("Polydispersity index", Charsets.UTF_8)
	       .putString("nm", Charsets.UTF_8)
	       .putString("{\"MEDIUM\":\" \"}", Charsets.UTF_8)
	       .hash();
	Assert.assertEquals("3502f924548105d27b60d6e55373aae4eb8802de", hc.toString());
	hc = hf.newHasher()
		       .putString("Polydispersity index", Charsets.UTF_8)
		       .putString("nm", Charsets.UTF_8)
		       .putString("{\"MEDIUM\":\"Human serum  Sigma #H4522 \"}", Charsets.UTF_8)
		       .hash();
	Assert.assertEquals("e342fd5e5d524e6f234ed6da934e9225af5a3a09", hc.toString());
	
	hc = hf.newHasher()
		       .putString("% Degradation", Charsets.UTF_8)
		       .putString("%", Charsets.UTF_8)
		       .putString("{\"Sampling time\":{	\"unit\":\"d\", 	\"loValue\":7.0}}", Charsets.UTF_8)
		       .hash();
	
	Assert.assertEquals("79d261f21100358502adc85703f0a13f31dad619", hc.toString());
	hc = hf.newHasher()
		       .putString("% Degradation", Charsets.UTF_8)
		       .putString("%", Charsets.UTF_8)
		       .putString("{\"Sampling time\":{\"loValue\":7.0,\"unit\":\"d\"}}", Charsets.UTF_8)
		       .hash();
	Assert.assertEquals("3cf77d9efe4e74744e691bf4880b4ef728b16fae", hc.toString());
	hc = hf.newHasher()
		       .putString("% Degradation%{\"Sampling time\":{	\"unit\":\"d\", 	\"loValue\":7.0}}", Charsets.UTF_8)
		       .hash();
	Assert.assertEquals("79D261F21100358502ADC85703F0A13F31DAD619", hc.toString().toUpperCase());
	
	IParams p = new Params();
	p.setLoValue(7.0);
	p.setUnits("d");
	p.setLoQualifier(">=");
	p.setUpQualifier("<");
	p.setUpValue(10.0);

	Value v = new Value();
	v.setUnits(p.getUnits().toString());
	v.setLoValue((Number)p.getLoValue());
	v.setLoQualifier(p.getLoQualifier().toString());
	v.setUpQualifier(p.getUpQualifier().toString());
	v.setUpValue((Number)p.getUpValue());
	
	Assert.assertEquals(p.toString(), v.toString());
	
	EffectRecord<String,IParams,String> effect = new EffectRecord<String,IParams,String>();
	effect.setConditions(new Params());
	effect.setEndpoint("% Degradation");
	effect.setUnit("%");
	v = new Value();
	v.setUnits("d");
	v.setLoValue(7.0);
	effect.getConditions().put("Sampling time",v);
	//System.out.println(effect);
	hc = hf.newHasher()
		       .putString(effect.getEndpoint(), Charsets.UTF_8)
		       .putString(effect.getUnit(), Charsets.UTF_8)
		       .putString(effect.getConditions().toString(), Charsets.UTF_8)
		       .hash();
	Assert.assertEquals("3cf77d9efe4e74744e691bf4880b4ef728b16fae", hc.toString().toLowerCase());
    }

}
