-- ---------------------------------------------------------------------------
-- Import substances
-- ---------------------------------------------------------------------------

SET unique_checks=0;
SET foreign_key_checks=0;
SET autocommit=0;
SET sql_log_bin=0;

-- substance table
LOAD DATA INFILE "{TMP}substance_table.tsv"
into table substance
FIELDS TERMINATED BY '\t' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
(@idsubstance,prefix,@var_uuid,documentType,format,name,
publicname,content,substanceType,rs_prefix,@var_rs_uuid,owner_prefix,@var_owner_uuid,owner_name)
SET uuid = UNHEX(@var_uuid), owner_uuid=unhex(@var_owner_uuid),rs_uuid=unhex(@var_rs_uuid);

COMMIT;

-- substance_protocolapplications table
LOAD DATA INFILE "{TMP}substance_protocolapplication_table.tsv"
into table substance_protocolapplication
FIELDS TERMINATED BY '\t' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
(document_prefix,@var_document_uuid,topcategory,endpointcategory,endpoint,guidance,substance_prefix,@var_substance_uuid,
params,interpretation_result,interpretation_criteria,reference,reference_year,reference_owner,updated,
reliability,isRobustStudy,isUsedforClassification,isUsedforMSDS,purposeFlag,studyResultType)
SET document_uuid = UNHEX(@var_document_uuid), substance_uuid=unhex(@var_substance_uuid);

COMMIT;

-- substance_ids table
LOAD DATA INFILE "{TMP}substance_ids_table.tsv"
into table substance_ids
FIELDS TERMINATED BY '\t' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
(prefix,@var_uuid,type,id)
SET uuid = UNHEX(@var_uuid);

COMMIT;



SET unique_checks=1;
SET foreign_key_checks=1;
SET autocommit=1;
-- INFO   Completed in 1,029,777 msec