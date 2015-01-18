package ambit2.base.data.study;

import java.io.Serializable;

import ambit2.base.json.JSONUtils;

public class ValueAnnotated<VALUE> extends Value<VALUE> implements Serializable {
    public static enum _fields {
	textValue,idresult, deleted, newentry,remarks;
	public String toJSONField() {
	    return "\t\"" + name() + "\":";
	}
    }

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
    protected String textValue = null;

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }


    @Override
    public String toJSON(StringBuilder b) {
	String comma = super.toJSON(b);
	if (getTextValue()!=null) {
	    b.append(comma);
	    b.append(_fields.textValue.toJSONField());
	    b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(getTextValue())));
	    comma = ", ";
	}	
	if (getIdresult() > 0) {
	    b.append(comma);
	    b.append(_fields.idresult.toJSONField());
	    b.append(idresult);
	    comma = ", ";
	}
	if (!isCopied()) {
	    b.append(comma);
	    b.append(_fields.newentry.toJSONField());
	    b.append(!isCopied());
	    comma = ", ";
	}
	if (isDeleted()) {
	    b.append(comma);
	    b.append(_fields.deleted.toJSONField());
	    b.append(isDeleted());
	    comma = ", ";
	}
	if (getRemark()!=null) {
	    b.append(comma);
	    b.append(_fields.remarks.toJSONField());
	    b.append(JSONUtils.jsonQuote(JSONUtils.jsonEscape(getRemark())));
	    comma = ", ";
	}
	return comma;
    }
    @Override
    public String toHumanReadable() {
	if (getTextValue()!=null) return getTextValue();
	else return super.toHumanReadable();
    }
}
