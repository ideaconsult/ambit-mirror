package ambit2.db.update.fp;

import java.io.Serializable;

public interface IFingerprint<Type,Content> extends Serializable {
	Type getType();
	void setType(Type type);
	Content getBits();
	void setBits(Content bits);
	int getFrequency();
	void setFrequency(int value);
	String getInterpretation(int bitindex) throws Exception;
	
}
