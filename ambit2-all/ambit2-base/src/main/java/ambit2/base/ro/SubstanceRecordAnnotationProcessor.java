package ambit2.base.ro;

import java.io.File;
import java.util.Map;

import ambit2.base.data.SubstanceRecord;
import ambit2.base.data.study.EffectRecord;
import ambit2.base.data.study.IParams;
import ambit2.base.data.study.Protocol;
import ambit2.base.data.study.ProtocolApplication;

public class SubstanceRecordAnnotationProcessor extends AbstractAnnotator<SubstanceRecord, SubstanceRecord> {

    /**
     * 
     */
    private static final long serialVersionUID = 5447660472718234256L;

    public SubstanceRecordAnnotationProcessor(File lookupfolder, boolean fulllinks) {
        super(lookupfolder, new String[] { _dictionaries.endpoint.name(), _dictionaries.guideline.name(),
                _dictionaries.params.name(), _dictionaries.conditions.name(), _dictionaries.substance.name() },
                fulllinks);
    }

    @Override
    public SubstanceRecord process(SubstanceRecord record) {
        if (record != null && record.getMeasurements() != null)
            for (ProtocolApplication<Protocol, IParams, String, IParams, String> papp : record.getMeasurements()) {
                for (EffectRecord<String, IParams, String> effect : papp.getEffects())
                    try {
                        String[] terms = annotateEndpoint(effect.getEndpoint());
                        if (terms != null)
                            for (String term : terms) {
                                effect.addEndpointSynonym(term);
                            }
                    } catch (Exception x) {

                    }
            }
        return record;
    }
    
    protected void collectAnnotations( Map<String,String[]> annotations, String key, String[] values) {
        if ((key != null) && (values!=null) && (values.length>0))
                annotations.put(key,values);        
    }
    
    public void annotate_all(SubstanceRecord record, Map<String,String[]> annotations) {
        try {
    	    if (annotations.get(((SubstanceRecord)record).getPublicName())!=null) {
    	        String name = ((SubstanceRecord)record).getPublicName();
    	        collectAnnotations(annotations,name,annotateGuideline(name));
    	    }
    	    if (record.getMeasurements() != null)
                for (ProtocolApplication<Protocol, IParams, String, IParams, String> papp : record.getMeasurements()) {
                    if (papp.getProtocol().getGuideline()!=null)
                        for (String g : papp.getProtocol().getGuideline()) 
                            collectAnnotations(annotations,g,annotateGuideline(g));
                      
                    Object method = papp.getParameters().get("E.method");
                    if (method!=null)
                        collectAnnotations(annotations,method.toString(),annotateParam(method.toString()));
                    
                    for (EffectRecord<String, IParams, String> effect : papp.getEffects())
                        try {
                            collectAnnotations(annotations,effect.getEndpoint(),annotateEndpoint(effect.getEndpoint()));
                            collectAnnotations(annotations,effect.getUnit(),annotateUnits(effect.getUnit()));
                        } catch (Exception x) {
                        }
                        
                }    	        
        } catch (Exception err) {
            
        }
        //annotator.annotateConditions(delimiter)
        //annotator.annotateUnits(delimiter)
	}

    public String[] annotateConditions(String condition) {
        try {
            return annotate(lookup.get(_dictionaries.conditions.name()), condition);
        } catch (Exception x) {
        }
        return null;
    }

    public String[] annotateUnits(String unit) throws Exception {
        try {
            return annotate(lookup.get(_dictionaries.units.name()), unit);
        } catch (Exception x) {
        }
        return null;
    }

    public String[] annotateEndpoint(String endpoint) {
        try {
            return annotate(lookup.get(_dictionaries.endpoint.name()), endpoint);
        } catch (Exception x) {
            return null;
        }

    }

    public String[] annotateGuideline(String guideline) {
        try {
            return annotate(lookup.get(_dictionaries.guideline.name()), guideline);
        } catch (Exception x) {
        }
        return null;
    }

    public String[] annotateParam(String param) {
        try {
            return annotate(lookup.get(_dictionaries.params.name()), param);
        } catch (Exception x) {
        }
        return null;
    }

    public String[] annotateSubstance(SubstanceRecord record) {
        try {
            return annotate(lookup.get(_dictionaries.substance.name()), record.getPublicName());
        } catch (Exception x) {
        }
        return null;
    }
}
