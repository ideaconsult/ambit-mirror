SET unique_checks=0;
SET foreign_key_checks=0;
SET autocommit=0;


-- create a temporary table
DROP TABLE IF EXISTS `substance_relation_chemicals_tmp`;

CREATE TABLE `substance_relation_chemicals_tmp` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `cmp_prefix` varchar(6) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'Composition UUID prefix',
  `cmp_uuid` varbinary(16) NOT NULL COMMENT 'Composition UUID',
  `idsubstance` int(11) unsigned NOT NULL,
  `idchemical` int(11) unsigned NOT NULL,
  `relation` varchar(45) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '',
  `function` varchar(45) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `proportion_real_lower` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `proportion_real_lower_value` double DEFAULT NULL,
  `proportion_real_upper` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `proportion_real_upper_value` double DEFAULT NULL,
  `proportion_real_unit` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `proportion_typical` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `proportion_typical_value` double DEFAULT NULL,
  `proportion_typical_unit` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `rs_prefix` varchar(6) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT 'ReferenceSubstance UUID (prefix)',
  `rs_uuid` varbinary(16) DEFAULT NULL COMMENT 'ReferenceSubstance UUID',
  `name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT 'composition name',
  `hidden` tinyint(4) DEFAULT '0',
  `substance_prefix` varchar(6) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT 'ECB5 in I5 UUIDs like ECB5-2c94e32c-3662-4dea-ba00-43787b8a6fd3',
  `substance_uuid` varbinary(16) DEFAULT NULL COMMENT 'The UUID part of  I5 UUIDs in binary format',
  `inchi` text CHARACTER SET latin1 COLLATE latin1_bin,
  `smiles` text CHARACTER SET latin1 COLLATE latin1_bin,
  `formula` varchar(64) DEFAULT NULL,
  `inchikey` varchar(27) DEFAULT NULL,
  `label` enum('OK','UNKNOWN','ERROR') NOT NULL DEFAULT 'UNKNOWN',
  `lastmodified` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `idchemicalnew` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `chemicalold_x` (`idchemical`),
  KEY `inchikey_x` (`inchikey`),
  KEY `suuid_x` (`substance_uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Dossier to chemicals relation';

COMMIT;


-- import into a temporary table
SELECT "import into a temporary table";

LOAD DATA INFILE "{TMP}substance_relation_chemicals_table.tsv"
into table substance_relation_chemicals_tmp
FIELDS TERMINATED BY '\t' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
(@id,cmp_prefix,@var_cmp_uuid,
@idsubstance,idchemical,
relation,function,
proportion_real_lower,proportion_real_lower_value,
proportion_real_upper,proportion_real_upper_value,
proportion_real_unit,
proportion_typical,proportion_typical_value,proportion_typical_unit,
rs_prefix,@var_rs_uuid,name,hidden,
substance_prefix,@var_uuid,
inchi,smiles,formula,inchikey,label,lastmodified)
SET cmp_uuid=UNHEX(@var_cmp_uuid),rs_uuid=unhex(@var_rs_uuid),substance_uuid=unhex(@var_uuid);

COMMIT;


-- add the missing chemicals
SELECT "add the missing chemicals";
insert into chemicals
SELECT null,inchi,smiles,formula,inchikey,label,lastmodified FROM substance_relation_chemicals_tmp
where inchikey not in 
(select inchikey from chemicals)
group by inchikey;

COMMIT;

SELECT "and the chemicals without structure";
-- and the chemicals without structure
-- temporarily using the inchi field to link with substance_uuid
insert into chemicals
SELECT null,hex(substance_uuid),smiles,formula,inchikey,"ERROR",lastmodified FROM substance_relation_chemicals_tmp
where inchi is null
group by substance_uuid;

COMMIT;


-- join to get idsubstance
-- TODO fields


SELECT "join with chemicals having a structure and insert into substance_relation";
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
select "join with chemicals WITHOUT a structure and insert into substance_relation";

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


-- fix the inchis

-- Write the new chemical id into tmp table, will be used for properties migration
-- chemicals with valid inchi 88712 
insert into substance_relation_chemicals_tmp
SELECT 
id,
cmp_prefix,cmp_uuid,idsubstance,t.idchemical,
relation,function,
proportion_real_lower,proportion_real_lower_value,
proportion_real_upper,proportion_real_upper_value,
proportion_real_unit,
proportion_typical,proportion_typical_value,proportion_typical_unit,
rs_prefix,rs_uuid,name,0,substance_prefix,substance_uuid,
t.inchi,t.smiles,t.formula,t.inchikey,t.label,t.lastmodified,c.idchemical as idchemicalnew
FROM substance_relation_chemicals_tmp t
join chemicals c using(inchikey)
where inchikey is not null
on duplicate key update idchemicalnew=values(idchemicalnew);

-- Write the new chemical id into tmp table, will be used for properties migration
-- chemicals without valid inchi  27440 
insert into substance_relation_chemicals_tmp
SELECT 
id,
cmp_prefix,cmp_uuid,idsubstance,t.idchemical,
relation,function,
proportion_real_lower,proportion_real_lower_value,
proportion_real_upper,proportion_real_upper_value,
proportion_real_unit,
proportion_typical,proportion_typical_value,proportion_typical_unit,
rs_prefix,rs_uuid,name,0,substance_prefix,substance_uuid,
t.inchi,t.smiles,t.formula,t.inchikey,t.label,t.lastmodified,c.idchemical as idchemicalnew
FROM substance_relation_chemicals_tmp t
join chemicals c on unhex(c.inchi)=substance_uuid
where !(c.inchi like  "InChI%") and t.inchikey is null
on duplicate key update idchemicalnew=values(idchemicalnew);

-- create structures based on smiles/inchis
insert into structure 
SELECT 
null,
idchemical,
if(smiles is null,
 if(inchi is null,compress(""),
 if (chemicals.inchi regexp "^InChI",compress(inchi),compress(""))
 ),
compress(smiles)),
"INC",
now(),
"guest",
if(inchi is null,'NA','SMILES'),
"UNKNOWN",
null,
9999,
null
FROM chemicals 
where idchemical not in (select idchemical from structure);

COMMIT;

SET unique_checks=1;
SET foreign_key_checks=1;
SET autocommit=1;