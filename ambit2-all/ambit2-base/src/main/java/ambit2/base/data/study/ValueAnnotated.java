package ambit2.base.data.study;

import ambit2.base.json.JSONUtils;

public class ValueAnnotated<VALUE> extends Value<VALUE> {
    protected int idresult;

    public int getIdresult() {
	return idresult;
    }

    public void setIdresult(int idresult) {
	this.idresult = idresult;
    }

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

    public String getRemark() {
	return remark;
    }

    public void setRemark(String remark) {
	this.remark = remark;
    }

    protected boolean copied = true;
    protected boolean deleted = false;
    protected String remark = null;
    
    @Override
    public String toJSON(StringBuilder b) {
        String comma = super.toJSON(b);
	if (getIdresult()>0) {
	    b.append(",");
	    b.append("\t\"idresult\":");
	    b.append(idresult);
	    comma = ", ";
	}
	if (!isCopied()) {
	    b.append(",");
	    b.append("\t\"newentry\":");
	    b.append(!isCopied());
	    comma = ", ";
	}
	if (isDeleted()) {
	    b.append(",");
	    b.append("\t\"deleted\":");
	    b.append(isDeleted());
	    comma = ", ";
	}	
	return comma;
    }
}
