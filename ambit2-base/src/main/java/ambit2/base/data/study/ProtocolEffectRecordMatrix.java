package ambit2.base.data.study;

public class ProtocolEffectRecordMatrix<ENDPOINT, CONDITIONS, UNIT> extends ProtocolEffectRecord<ENDPOINT, CONDITIONS, UNIT> {

    /**
     * 
     */
    private static final long serialVersionUID = 7153942724319574909L;
    private boolean copied = true;
    public boolean isCopied() {
        return copied;
    }
    public void setCopied(boolean copied) {
        this.copied = copied;
    }
    public boolean isDeleted() {
        return deleted;
    }
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    public String getRemarks() {
        return remarks;
    }
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    private boolean deleted = false;
    private String remarks = null;
}
