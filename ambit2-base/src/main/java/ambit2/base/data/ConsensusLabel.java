package ambit2.base.data;

import ambit2.base.data.ConsensusLabel.CONSENSUS_LABELS;

public class ConsensusLabel extends AbstractLabel<CONSENSUS_LABELS> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1008781131664292956L;

	public static enum CONSENSUS_LABELS {Consensus,Majority,Unconfirmed,Ambiguous,Unknown};
	
	public ConsensusLabel() {
		
	}
	public ConsensusLabel(CONSENSUS_LABELS label) {
		setLabel(label);
	}	
}
