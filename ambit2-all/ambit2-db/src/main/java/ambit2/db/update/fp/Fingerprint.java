package ambit2.db.update.fp;


public class Fingerprint<Type,Content> implements IFingerprint<Type,Content> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5550890711527303929L;
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
	public String getInterpretation(int bitindex) {
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
