package ambit2.core.data.model;

public interface IEvaluation<Content> {
	public enum EVType {evaluation,evaluation_training,evaluation_test,crossvalidation}
	public enum EVStatsType {rmse,mae,pctcorrect,pctincorrect}
	EVType getType();
	void setType(EVType type);
	void setContent(Content content);
	Content getContent();
}
