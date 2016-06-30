SET unique_checks=0;
SET foreign_key_checks=0;
SET autocommit=0;
SET sql_log_bin=0;

-- substance_experiments table
LOAD DATA INFILE "{TMP}substance_experiment_table.tsv"
into table substance_experiment
FIELDS TERMINATED BY '\t' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
(@idresult,document_prefix,@var_document_uuid,topcategory,endpointcategory,@ehash,endpoint,conditions,unit,
loQualifier,loValue,upQualifier,upValue,textValue,errQualifier,err,substance_prefix,@var_substance_uuid)
SET document_uuid = UNHEX(@var_document_uuid),endpointhash=unhex(@ehash),substance_uuid=unhex(@var_substance_uuid) ;

COMMIT;

-- recalculating the endpoint hash
-- update substance_experiment set endpointhash= unhex(sha1(concat(ifnull(endpoint,""),ifnull(unit,""),ifnull(conditions,""))));

SET unique_checks=1;
SET foreign_key_checks=1;
SET autocommit=1;