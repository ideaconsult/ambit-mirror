-- substance table
LOAD DATA INFILE "{TMP}substance_table.tsv"
into table substance
FIELDS TERMINATED BY '\t' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
(@idsubstance,prefix,@var_uuid,documentType,format,name,
publicname,content,substanceType,rs_prefix,@var_rs_uuid,owner_prefix,@var_owner_uuid,owner_name)
SET uuid = UNHEX(@var_uuid), owner_uuid=unhex(@var_owner_uuid),rs_uuid=unhex(@var_rs_uuid);

-- substance_protocolapplications table
LOAD DATA INFILE "{TMP}substance_protocolapplication_table.tsv"
into table substance_protocolapplication
FIELDS TERMINATED BY '\t' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
(document_prefix,@var_document_uuid,topcategory,endpointcategory,endpoint,guidance,substance_prefix,@var_substance_uuid,
params,interpretation_result,interpretation_criteria,reference,reference_year,reference_owner,updated,
reliability,isRobustStudy,isUsedforClassification,isUsedforMSDS,purposeFlag,studyResultType)
SET document_uuid = UNHEX(@var_document_uuid), substance_uuid=unhex(@var_substance_uuid);

-- substance_experiments table
LOAD DATA INFILE "{TMP}substance_experiment_table.tsv"
into table substance_experiment
FIELDS TERMINATED BY '\t' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
(@idresult,document_prefix,@var_document_uuid,topcategory,endpointcategory,@toberecalculated,endpoint,conditions,unit,
loQualifier,loValue,upQualifier,upValue,textValue,errQualifier,err,substance_prefix,@var_substance_uuid)
SET document_uuid = UNHEX(@var_document_uuid), substance_uuid=unhex(@var_substance_uuid);

-- recalculating the endpoint hash
update substance_experiment set endpointhash= unhex(sha1(concat(ifnull(endpoint,""),ifnull(unit,""),ifnull(conditions,""))));

-- substance_ids table
LOAD DATA INFILE "{TMP}substance_ids_table.tsv"
into table substance_ids
FIELDS TERMINATED BY '\t' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
(prefix,@var_uuid,type,id)
SET uuid = UNHEX(@var_uuid);

-- create a temporary table
CREATE TABLE `substance_relation_chemicals_tmp` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `cmp_prefix` varchar(6) COLLATE utf8_bin NOT NULL COMMENT 'Composition UUID prefix',
  `cmp_uuid` varbinary(16) NOT NULL COMMENT 'Composition UUID',
  `idsubstance` int(11) unsigned NOT NULL,
  `idchemical` int(11) unsigned NOT NULL,  
  `relation` varchar(45) COLLATE utf8_bin NOT NULL DEFAULT '',
  `function` varchar(45) COLLATE utf8_bin DEFAULT NULL,
  `proportion_real_lower` varchar(16) COLLATE utf8_bin DEFAULT NULL,
  `proportion_real_lower_value` double DEFAULT NULL,
  `proportion_real_upper` varchar(16) COLLATE utf8_bin DEFAULT NULL,
  `proportion_real_upper_value` double DEFAULT NULL,
  `proportion_real_unit` varchar(16) COLLATE utf8_bin DEFAULT NULL,
  `proportion_typical` varchar(16) COLLATE utf8_bin DEFAULT NULL,
  `proportion_typical_value` double DEFAULT NULL,
  `proportion_typical_unit` varchar(16) COLLATE utf8_bin DEFAULT NULL,
  `rs_prefix` varchar(6) COLLATE utf8_bin DEFAULT NULL COMMENT 'ReferenceSubstance UUID (prefix)',
  `rs_uuid` varbinary(16) DEFAULT NULL COMMENT 'ReferenceSubstance UUID',
  `name` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT 'composition name',
  `hidden` tinyint(4) DEFAULT '0',
  `substance_prefix` varchar(6) COLLATE utf8_bin DEFAULT NULL COMMENT 'ECB5 in I5 UUIDs like ECB5-2c94e32c-3662-4dea-ba00-43787b8a6fd3',
  `substance_uuid` varbinary(16) DEFAULT NULL COMMENT 'The UUID part of  I5 UUIDs in binary format',  
  `inchi` text CHARACTER SET latin1 COLLATE latin1_bin,
  `smiles` text CHARACTER SET latin1 COLLATE latin1_bin,
  `formula` varchar(64) DEFAULT NULL,
  `inchikey` varchar(27) DEFAULT NULL,
  `label` enum('OK','UNKNOWN','ERROR') NOT NULL DEFAULT 'UNKNOWN',
  `lastmodified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,  
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Dossier to chemicals relation';

-- import into a temporary table
LOAD DATA INFILE "{TMP}substance_relation_chemicals_table.tsv"
into table substance_relation_chemicals_tmp
FIELDS TERMINATED BY '\t' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
(@id,cmp_prefix,@var_cmp_uuid,
@idsubstance,@idchemical,
relation,function,
proportion_real_lower,proportion_real_lower_value,
proportion_real_upper,proportion_real_upper_value,
proportion_real_unit,
proportion_typical,proportion_typical_value,proportion_typical_unit,
rs_prefix,@var_rs_uuid,name,hidden,
substance_prefix,@var_uuid,
inchi,smiles,formula,inchikey,label,lastmodified)
SET cmp_uuid=UNHEX(@var_cmp_uuid),rs_uuid=unhex(@var_rs_uuid),substance_uuid=unhex(@var_uuid);

-- add the missing chemicals
insert into chemicals
SELECT null,inchi,smiles,formula,inchikey,label,lastmodified FROM substance_relation_chemicals_tmp
where inchikey not in 
(select inchikey from chemicals)
group by inchikey;

-- and the chemicals without structure
-- temporarily using the inchi field to link with substance_uuid
insert into chemicals
SELECT null,hex(substance_uuid),smiles,formula,inchikey,label,lastmodified FROM substance_relation_chemicals_tmp
where inchi is null
group by substance_uuid;

-- join to get idsubstance
-- TODO fields
SELECT * FROM substance_relation_chemicals_tmp
join substance on prefix=substance_prefix and uuid=substance_uuid;

-- then join with chemicals having a structure and insert into substance_relation
insert ignore into substance_relation
SELECT 
cmp_prefix,cmp_uuid,s.idsubstance,c.idchemical,
relation,function,
proportion_real_lower,proportion_real_lower_value,
proportion_real_upper,proportion_real_upper_value,
proportion_real_unit,
proportion_typical,proportion_typical_value,proportion_typical_unit,
t.rs_prefix,t.rs_uuid,t.name,0
FROM substance_relation_chemicals_tmp t
join chemicals c using(inchikey)
join substance s on prefix=substance_prefix and uuid=substance_uuid
where inchikey is not null;

-- then join the chemicals WITHOUT structure and insert into substance_relation
insert ignore into substance_relation
SELECT 
cmp_prefix,cmp_uuid,s.idsubstance,c.idchemical,
relation,function,
proportion_real_lower,proportion_real_lower_value,
proportion_real_upper,proportion_real_upper_value,
proportion_real_unit,
proportion_typical,proportion_typical_value,proportion_typical_unit,
t.rs_prefix,t.rs_uuid,t.name,0
FROM substance_relation_chemicals_tmp t
join chemicals c on unhex(c.inchi)=substance_uuid
join substance s on prefix=substance_prefix and uuid=substance_uuid
where !(c.inchi regexp "^InChI") and t.inchikey is null;
