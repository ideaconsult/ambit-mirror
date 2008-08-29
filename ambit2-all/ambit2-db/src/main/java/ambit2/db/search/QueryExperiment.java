package ambit2.db.search;

import ambit2.core.data.experiment.StudyTemplate;


public abstract  class QueryExperiment<T,C extends IQueryCondition> 
				extends AbstractQuery<StudyTemplate,TemplateFieldQuery<T>,C> {

}
