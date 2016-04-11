package ambit2.base.data.study;

public class ProtocolEffectRecordMatrix<ENDPOINT, CONDITIONS, UNIT> extends ProtocolEffectRecord<ENDPOINT, CONDITIONS, UNIT> {

    /**
     * 
     */
    private static final long serialVersionUID = 7153942724319574909L;
    private boolean record_copied = true;
    private boolean record_deleted = false;
    private String record_remarks = null;
    
    private boolean protocolapp_copied = true;
    public boolean isProtocolapp_copied() {
		return protocolapp_copied;
	}
	public void setProtocolapp_copied(boolean protocolapp_copied) {
		this.protocolapp_copied = protocolapp_copied;
	}
	public boolean isProtocolapp_deleted() {
		return protocolapp_deleted;
	}
	public void setProtocolapp_deleted(boolean protocolap_deleted) {
		this.protocolapp_deleted = protocolap_deleted;
	}
	public String getProtocolapp_remarks() {
		return protocolapp_remarks;
	}
	public void setProtocolapp_remarks(String protocolap_remarks) {
		this.protocolapp_remarks = protocolap_remarks;
	}
	private boolean protocolapp_deleted = false;
    private String protocolapp_remarks = null;
    
    
    public boolean isCopied() {
        return record_copied;
    }
    public void setCopied(boolean copied) {
        this.record_copied = copied;
    }
    public boolean isDeleted() {
        return record_deleted;
    }
    public void setDeleted(boolean deleted) {
        this.record_deleted = deleted;
    }
    public String getRemarks() {
        return record_remarks;
    }
    public void setRemarks(String remarks) {
        this.record_remarks = remarks;
    }

}
