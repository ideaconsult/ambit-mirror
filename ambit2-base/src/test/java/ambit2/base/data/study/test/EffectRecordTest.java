package ambit2.base.data.study.test;

import junit.framework.Assert;

import org.junit.Test;

import ambit2.base.data.study.EffectRecord;
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
    }

}
