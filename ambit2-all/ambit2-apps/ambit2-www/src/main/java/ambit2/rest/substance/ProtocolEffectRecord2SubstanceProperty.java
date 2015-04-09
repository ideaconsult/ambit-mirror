package ambit2.rest.substance;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import net.idea.modbcum.i.processors.IProcessor;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;

import ambit2.base.data.ILiteratureEntry;
import ambit2.base.data.LiteratureEntry;
import ambit2.base.data.PropertyAnnotation;
import ambit2.base.data.PropertyAnnotations;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.ProtocolEffectRecord;
import ambit2.base.data.study.Value;
import ambit2.base.data.study.ReliabilityParams._r_flags;
import ambit2.base.data.substance.SubstanceProperty;

public class ProtocolEffectRecord2SubstanceProperty implements
	IProcessor<ProtocolEffectRecord<String, IParams, String>, SubstanceProperty> {
    /**
     * 
     */
    private static final long serialVersionUID = 5597788664120240759L;

    public ProtocolEffectRecord2SubstanceProperty() {
	super();
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public long getID() {
	return 0;
    }

    @Override
    public boolean isEnabled() {
	return true;
    }

    @Override
    public void open() throws Exception {
    }

    public ILiteratureEntry getReference(ProtocolEffectRecord<String, IParams, String> detail) {
	List<String> guideline = detail.getProtocol().getGuideline();
	ILiteratureEntry ref = LiteratureEntry.getInstance(guideline == null ? null : guideline.size() == 0 ? null
		: guideline.get(0), guideline == null ? null : guideline.size() == 0 ? null : guideline.get(0));
	return ref;
    }

    public PropertyAnnotations conditions2annotations(ProtocolEffectRecord<String, IParams, String> detail)
	    throws JsonProcessingException, IOException {

	PropertyAnnotations ann = new PropertyAnnotations();
	/* works but is a hack
	if (detail.getStudyResultType() != null) {
	    PropertyAnnotation a = new PropertyAnnotation();
	    a.setType("reliability");
	    a.setPredicate("studyResultType");
	    a.setObject(detail.getStudyResultType());
	    ann.add(a);
	}
	*/
	if (detail.getConditions() == null)
	    return ann;
	Iterator<String> keys = detail.getConditions().keySet().iterator();
	while (keys.hasNext()) {
	    String key = keys.next();
	    Object o = detail.getConditions().get(key);
	    if (o == null)
		continue;
	    else if (o instanceof Value) {
		Value value = (Value) o;
		PropertyAnnotation a = new PropertyAnnotation();
		a.setType("condition");
		a.setPredicate(key);
		if (value.getLoValue() instanceof String) {
		    a.setObject(value.getLoValue());
		} else {
		    a.setObject(String.format("%s%s %s%s %s",
			    value.getLoQualifier() == null ? "" : value.getLoQualifier(),
			    value.getLoValue() == null ? "" : value.getLoValue(), value.getUpQualifier() == null ? ""
				    : value.getUpQualifier(), value.getUpValue() == null ? "" : value.getUpValue(),
			    value.getUnits() == null ? "" : value.getUnits()));
		}
		ann.add(a);
	    } else {
		PropertyAnnotation a = new PropertyAnnotation();
		a.setType("condition");
		a.setPredicate(key);
		a.setObject(o.toString());
		ann.add(a);
	    }
	}
	return ann;
    }

    @Override
    public SubstanceProperty process(ProtocolEffectRecord<String, IParams, String> detail) throws Exception {

	SubstanceProperty key = new SubstanceProperty(detail.getProtocol().getTopCategory(), detail.getProtocol()
		.getCategory(), detail.getEndpoint(), detail.getUnit(), getReference(detail));
	key.setExtendedURI(true);
	key.setIdentifier(detail.getSampleID());
	key.setAnnotations(conditions2annotations(detail));
	
	try {
	    key.setStudyResultType(_r_flags.valueOf(detail.getStudyResultType().replace(":","").replace("_","").replace(" ","").replace("-","").replace(")","").replace("(","")));
	} catch (Exception x) {
	    key.setStudyResultType(null);
	}
	return key;
    }

    @Override
    public void setEnabled(boolean arg0) {
    }

}
