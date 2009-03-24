package ambit2.db.search;

import ambit2.base.data.Template;


public abstract  class QueryExperiment<T,C extends IQueryCondition,ResultType> 
				extends AbstractQuery<Template,IQueryObject,C,ResultType> {

}
