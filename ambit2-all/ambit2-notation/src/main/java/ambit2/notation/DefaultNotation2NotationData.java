package ambit2.notation;

public class DefaultNotation2NotationData 
{
	public DefaultNotationGenerator generator = null; 
	
	
	public DefaultNotation2NotationData() {
		generator = new DefaultNotationGenerator(); 
	}
	
	public DefaultNotation2NotationData(DefaultNotationGenerator generator) {
		this.generator = generator;; 
	}
	
	
	public NotationData parseDefaultNotation(String notation)
	{
		//TODO
		return null;
	}
	
}
