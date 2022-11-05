package ambit2.vega;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ambit2.base.config.AMBITConfigProperties;
import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.Property;
import ambit2.base.data.PropertyAnnotation;
import ambit2.base.data.PropertyAnnotations;
import ambit2.base.external.ShellException;
import net.idea.modbcum.i.exceptions.AmbitException;

public class VegaRemote implements VegaWrapper  {
    protected Logger logger = Logger.getLogger(getClass().getName());
    public static final String VEGA_REMOTE = "VEGA_REMOTE";
    public static final String VEGA_REMOTE_USER = "VEGA_REMOTE_USER";
    public static final String VEGA_REMOTE_PASS = "VEGA_REMOTE_PASS";
    protected String config;
    protected String URL;

    public synchronized String getURLFromConfig(String propertiesResource, String propertyHome) throws ShellException {
        try {
            AMBITConfigProperties properties = new AMBITConfigProperties();
            
            String wheredragonlives = properties.getPropertyWithDefault(propertyHome,propertiesResource,null);
            if (wheredragonlives == null) {
                logger.log(Level.SEVERE, String.format("Can't find where %s is located. No property %s in %s",
                        toString(), propertyHome, propertiesResource));
                throw new ShellException(null, String.format("Can't find where %s is located.", toString()));
            }
            return wheredragonlives;
        } catch (ShellException x) {
            throw x;
        } catch (Exception x) {
            throw new ShellException(null, x);
        }
    }    
    protected String getParam(String key) throws ShellException {
        String home = System.getenv(key);
        if (home == null && config != null)
            home = getURLFromConfig(config, key);
        return home;
    }
    
    protected String getConfig() {
        return config;
    }

    protected void setConfig(String config) {
        this.config = config;
    }
    
    public VegaRemote() throws ShellException {
        this(AMBITConfigProperties.ambitProperties);
    }

    public VegaRemote(String config) throws ShellException {
        super();
        this.config = config;   
        this.URL = getParam(VEGA_REMOTE);
    }    

    protected Property getProperty(String model, String version) {
        ILiteratureEntry ref = LiteratureEntry.getInstance(model, version);
        Property property = new Property("result", "units", ref);
        property.setLabel("sameas");
        property.setEnabled(true);
        PropertyAnnotations pa = new PropertyAnnotations();
        property.setAnnotations(pa);

        PropertyAnnotation a = new PropertyAnnotation();
        a.setObject("NON-Mutagenic");
        a.setPredicate("acceptValue");
        a.setType("^^\"NontoxicCategory\"");
        pa.add(a);
        a = new PropertyAnnotation();
        a.setObject("Mutagenic");
        a.setPredicate("acceptValue");
        a.setType("^^\"ToxicCategory\"");
        pa.add(a);
        return property;

    }

    public synchronized IAtomContainer process(IAtomContainer mol) throws AmbitException {
        
        String smiles = null;
        try {
            SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.Default);
            smiles = smigen.create(mol);
        } catch (Exception x) {
            throw new AmbitException(x);
        }
        try (CloseableHttpClient httpclient = HttpClientBuilder.create().build()) {
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials(getParam(VegaRemote.VEGA_REMOTE_USER),getParam(VegaRemote.VEGA_REMOTE_PASS));
            URI uri = new URIBuilder(String.format("%s/vega;id=PREFERRED", this.URL)).build();
            HttpPost httprequest = new HttpPost(String.format("%s", uri));
            httprequest.addHeader(new BasicScheme().authenticate(creds, httprequest, null));
            httprequest.setEntity(new StringEntity(String.format("[\"%s\"]", smiles)));
            httprequest.setHeader("Content-type", "application/json");
            try (CloseableHttpResponse response = httpclient.execute(httprequest)) {
                if (200 == response.getStatusLine().getStatusCode()) {
                    String res = EntityUtils.toString(response.getEntity());
                    ObjectMapper m = new ObjectMapper();
                    JsonNode root = m.readTree(res);
                    VegaRemote.parse_remotevega(mol, root);
                    return mol;
                } else
                    throw new AmbitException(response.getStatusLine().toString());
            } catch (Exception x) {
                x.printStackTrace();
                throw x;
            }
        } catch (AmbitException x) {
            throw x;
        } catch (Exception x) {
            throw new AmbitException(x);
        }
    };

    public List<Property> createProperties() throws AmbitException {
        
        try (CloseableHttpClient httpclient = HttpClientBuilder.create().build()) {
            URI uri = new URIBuilder(this.URL).build();
            HttpGet httprequest = new HttpGet(String.format("%s;id=PREFERRED", uri));
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials(getParam(VegaRemote.VEGA_REMOTE_USER),getParam(VegaRemote.VEGA_REMOTE_PASS));
            httprequest.addHeader(new BasicScheme().authenticate(creds, httprequest, null));            
            httprequest.setHeader("Content-type", "application/json");
            try (CloseableHttpResponse response = httpclient.execute(httprequest)) {
                if (200 == response.getStatusLine().getStatusCode()) {
                    String res = EntityUtils.toString(response.getEntity());
                    ObjectMapper m = new ObjectMapper();
                    JsonNode root = m.readTree(res);
                    Map<String, Property> features = parse_metadata(root.get("models").fields());
                    List<Property> properties = new ArrayList<Property>();
                    for (Property p:  features.values()) 
                        properties.add(p);
                    return properties;
                } else
                    throw new AmbitException();
            } catch (Exception x) {
                throw x;
            }
        } catch (Exception x) {
            throw new AmbitException(x);
        }
    }

    public static IAtomContainer parse_remotevega(IAtomContainer mol, JsonNode root) throws Exception {
        Map<String, Property> features = parse_metadata(root.get("metadata").get("models").fields());
        parse_predictions(root.get("predictions").fields(), features, mol);
        return mol;
    }

    public static void parse_predictions(Iterator<Map.Entry<String, JsonNode>> pi, Map<String, Property> features,
            IAtomContainer mol) {
        while (pi.hasNext()) {
            
            Map.Entry<String, JsonNode> p = pi.next();
            Iterator<Map.Entry<String, JsonNode>> values = p.getValue().fields();
            while (values.hasNext()) {
                try {
                    Map.Entry<String, JsonNode> v = values.next();
                    if (mol != null)
                        mol.setProperty(features.get(v.getKey()), v.getValue().asText());
                } catch (Exception x) {
                    System.err.println(x);
                }
            }
        }
    }

    public static Map<String, Property> parse_metadata(Iterator<Map.Entry<String, JsonNode>> pi) {
        Map<String, Property> properties = new HashMap<String, Property>();
        while (pi.hasNext()) {
            Map.Entry<String, JsonNode> p = pi.next();
            ILiteratureEntry ref = LiteratureEntry.getInstance(p.getKey(),
                    String.format("%s (%s)",
                    p.getValue().get("name").asText(),
                    p.getValue().get("version").asText()));
            Map<String, Property> tmp = parse_features(p.getValue().get("features").fields(), ref);
            if (tmp != null)
                properties.putAll(tmp);
        }
        return properties;

    }

    public static Map<String, Property> parse_features(Iterator<Map.Entry<String, JsonNode>> pi, ILiteratureEntry ref) {
        Map<String, Property> properties = new HashMap<String, Property>();
        while (pi.hasNext()) {
            Map.Entry<String, JsonNode> p = pi.next();
            String name = p.getKey();
            String units = p.getValue().get("units").asText();
            String label = p.getValue().get("sameAs").asText();
            Property property = new Property(name, (units==null)?"":units, ref);
            property.setLabel(label);
            property.setNominal(p.getValue().get("isNominal").asBoolean());
            property.setEnabled(true);
            JsonNode annotation = p.getValue().get("annotations");
            if (annotation != null) {
                PropertyAnnotations pa = new PropertyAnnotations();
                property.setAnnotations(pa);
                Iterator<String> keys = annotation.fieldNames();
                while (keys.hasNext()) {
                    String key = keys.next();
                    PropertyAnnotation a = new PropertyAnnotation();
                    a.setObject(key);
                    a.setPredicate("acceptValue");
                    a.setType("^^" + annotation.get(key).asText());
                    pa.add(a);
                }
            }

            properties.put(p.getKey(), property);

        }
        return properties;
    }

}
