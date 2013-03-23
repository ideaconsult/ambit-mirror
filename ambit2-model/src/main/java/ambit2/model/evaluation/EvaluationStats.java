package ambit2.model.evaluation;

import java.util.Hashtable;

import ambit2.core.data.model.IEvaluation;

public class EvaluationStats<Content> implements IEvaluation<Content> {
	
	private Hashtable<EVStatsType, Double> stats = new Hashtable<IEvaluation.EVStatsType, Double>();
	public Hashtable<EVStatsType, Double> getStats() {
		return stats;
	}
	private EVType type;
	private Content content;
	
	public double getPctInCorrect() {
		return stats.get(EVStatsType.pctincorrect);
	}
	public void setPctInCorrect(double pct) {
		stats.put(EVStatsType.pctincorrect,pct);
	}	
	public double getPctCorrect() {
		return stats.get(EVStatsType.pctcorrect);
	}
	public void setPctCorrect(double pct) {
		stats.put(EVStatsType.pctcorrect,pct);
	}
	public double getMAE() {
		return stats.get(EVStatsType.mae);
	}
	public void setMAE(double MAE) {
		stats.put(EVStatsType.mae,MAE);
	}
	
	public double getRMSE() {
		return stats.get(EVStatsType.rmse);
	}
	public void setRMSE(double rMSE) {
		stats.put(EVStatsType.rmse,rMSE);
	}
	public EvaluationStats(EVType type, Content content) {
		setType(type);
		setContent(content);
	}
	@Override
	public EVType getType() {
		return type;
	}

	@Override
	public void setType(EVType type) {
		this.type=type;
	}

	@Override
	public void setContent(Content content) {
		this.content = content;
	}

	@Override
	public Content getContent() {
		return content;
	}
	@Override
	public String toString() {
		return content==null?"":content.toString();
	}
}
