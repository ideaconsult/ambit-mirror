package ambit2.db.update.fp;

import ambit2.descriptors.processors.BitSetGenerator.FPTable;
import ambit2.smarts.SmartsScreeningKeys;


public class Fingerprint<Type,Content> implements IFingerprint<Type,Content> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5550890711527303929L;
	protected static SmartsScreeningKeys keys;
	Type type;
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	Content bits;
	int frequency;

	public Fingerprint() {
		
	}
	public Fingerprint(Type type,Content bits) {
		this();
		setBits(bits);
	}
	@Override
	public Content getBits() {
		return bits;
	}
	@Override
	public int getFrequency() {
		return frequency;
	}
	@Override
	public synchronized String getInterpretation(int bitindex) throws Exception {
		if (FPTable.sk1024.equals(type)) {
			if (keys==null) keys = new SmartsScreeningKeys();
			try {
				return keys.getKeys().get(bitindex);
			} catch (Exception x) {
				throw x;
			}
		} else
			return null;
	}
	@Override
	public void setBits(Content bits) {
		this.bits = bits;
		
	}
	@Override
	public void setFrequency(int value) {
		this.frequency = value;
		
	}
	@Override
	public String toString() {
		return String.format("%s [%s]",getType().toString(),getBits()==null?"":getBits());
	}
	
}
