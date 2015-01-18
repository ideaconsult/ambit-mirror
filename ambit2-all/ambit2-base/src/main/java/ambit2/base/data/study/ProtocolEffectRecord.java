package ambit2.base.data.study;

/**
 * Extension of {@link EffectRecord} to be able to attach the protocol
 * 
 * @author nina
 * 
 * @param <ENDPOINT>
 * @param <CONDITIONS>
 * @param <UNIT>
 * 
 *            TypicalUse: new {@link ProtocolEffectRecord<String,Params,String>}
 */
public class ProtocolEffectRecord<ENDPOINT, CONDITIONS, UNIT> extends EffectRecord<ENDPOINT, CONDITIONS, UNIT> {
    /**
	 * 
	 */
    private static final long serialVersionUID = 7123248041424144730L;
    protected Protocol protocol;
    protected String documentUUID;
    protected int idresult = -1;
    protected String interpretationResult;
    public String getInterpretationResult() {
        return interpretationResult;
    }

    public void setInterpretationResult(String interpretationResult) {
        this.interpretationResult = interpretationResult;
    }

    public int getIdresult() {
	return idresult;
    }

    public void setIdresult(int idresult) {
	this.idresult = idresult;
    }

    public String getDocumentUUID() {
	return documentUUID;
    }

    public void setDocumentUUID(String documentUUID) {
	this.documentUUID = documentUUID;
    }

    public Protocol getProtocol() {
	return protocol;
    }

    public void setProtocol(Protocol protocol) {
	this.protocol = protocol;
    }

}
