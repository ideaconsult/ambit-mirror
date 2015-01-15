package ambit2.base.data.study;

import java.util.List;

public class ProtocolApplicationAnnotated<PROTOCOL, PARAMS, ENDPOINT, CONDITIONS, UNIT> extends ProtocolApplication<PROTOCOL, PARAMS, ENDPOINT, CONDITIONS, UNIT> {

    /**
     * 
     */
    private static final long serialVersionUID = -8229267088974835650L;
    protected List<ValueAnnotated> records_to_delete;
    public List<ValueAnnotated> getRecords_to_delete() {
        return records_to_delete;
    }
    public void setRecords_to_delete(List<ValueAnnotated> records_to_delete) {
        this.records_to_delete = records_to_delete;
    }
    public ProtocolApplicationAnnotated(PROTOCOL protocol) {
	super(protocol);
    }

}
