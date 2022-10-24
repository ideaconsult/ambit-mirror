package ambit2.dragon.test;

import java.io.InputStream;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ambit2.vega.VegaRemote;
import ambit2.vega.VegaShell;

public class VegaRemoteTest {

    @Test
    public void test() throws Exception {
        try (InputStream in = VegaShell.class.getClassLoader()
                .getResourceAsStream("ambit2/vega/test/predictions.json")) {
            ObjectMapper m = new ObjectMapper();
            JsonNode root = m.readTree(in);
            
            VegaRemote.parse_remotevega(null, root);
        } catch (Exception x) {
            throw(x);
        }
    }

}
