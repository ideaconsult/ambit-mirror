-- ---------------------------------------------------------------------------
-- Import chemicals
-- ---------------------------------------------------------------------------
SET unique_checks=0;
SET foreign_key_checks=0;
SET autocommit=0;
SET sql_log_bin=0;

DROP table if exists property_values_tmp;

CREATE TABLE `property_values_tmp` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `idchemical` int(10) unsigned NOT NULL,
  `name` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `units` varchar(16) COLLATE utf8_bin NOT NULL DEFAULT '',
  `comments` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `text` text COLLATE utf8_bin,
  `value` varchar(255) COLLATE utf8_bin NOT NULL,
  `title` varchar(255) COLLATE utf8_bin NOT NULL,
  `url` varchar(255) COLLATE utf8_bin NOT NULL,
  `type` enum('Unknown','Dataset','Algorithm','Model','BibtexEntry','BibtexArticle','BibtexBook','Feature') COLLATE utf8_bin NOT NULL DEFAULT 'Dataset',
  `idreference` int(10) DEFAULT NULL,
  `idproperty` int(10) DEFAULT NULL,
  `idchemicalnew` int(10) DEFAULT NULL,
  `idvalue` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `tmp_value_x` (`value`),
  KEY `tmp_title_x` (`title`),
  KEY `tmp_name_x` (`idreference`,`name`),
  KEY `tmp_idchemicalold_x` (`idchemical`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


LOAD DATA INFILE "{TMP}property_names.tsv"
into table property_values_tmp
FIELDS TERMINATED BY '\t' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
(@id,idchemical,name,units,comments,text,value,title,url,type) ;

LOAD DATA INFILE "{TMP}property_tradename.tsv"
into table property_values_tmp
FIELDS TERMINATED BY '\t' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
(@id,idchemical,name,units,comments,text,value,title,url,type) ;

LOAD DATA INFILE "{TMP}property_i5uuid.tsv"
into table property_values_tmp
FIELDS TERMINATED BY '\t' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
(@id,idchemical,name,units,comments,text,value,title,url,type) ;

LOAD DATA INFILE "{TMP}property_ec.tsv"
into table property_values_tmp
FIELDS TERMINATED BY '\t' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
(@id,idchemical,name,units,comments,text,value,title,url,type) ;

LOAD DATA INFILE "{TMP}property_casrn.tsv"
into table property_values_tmp
FIELDS TERMINATED BY '\t' OPTIONALLY ENCLOSED BY '"'
LINES TERMINATED BY '\n'
(@id,idchemical,name,units,comments,text,value,title,url,type) ;

COMMIT;

-- strings
insert ignore into property_string
SELECT null,value FROM property_values_tmp
group by value;

COMMIT;

-- idvalue_string
-- 1329456 
insert into property_values_tmp
SELECT id,idchemical,name,units,comments,text,t.value,title,url,type,
idreference,idproperty,idchemicalnew,s.idvalue_string FROM property_values_tmp t
join property_string s on s.value=t.value
on duplicate key update idvalue=values(idvalue);

COMMIT;


-- catalog_references
-- 75053 
insert ignore into catalog_references
SELECT null,title,url,type FROM property_values_tmp
group by title;

COMMIT;

-- idreferences
insert into property_values_tmp
SELECT id,idchemical,name,units,comments,text,t.value,t.title,t.url,t.type,
s.idreference,idproperty,idchemicalnew,idvalue FROM property_values_tmp t
join catalog_references s on s.title=t.title
on duplicate key update idreference=values(idreference);

COMMIT;

-- properties
-- 134922 
insert ignore into properties
SELECT null,idreference,name,units,comments,0,"STRING" FROM property_values_tmp
group by idreference,name;

COMMIT;

-- idproperty
-- 1329456 
insert into property_values_tmp
SELECT id,idchemical,t.name,t.units,t.comments,t.text,value,title,url,type,
t.idreference,s.idproperty,idchemicalnew,idvalue FROM property_values_tmp t
join properties s on s.idreference=t.idreference and s.name=t.name
on duplicate key update idproperty=values(idproperty);

COMMIT;

-- Import without experiments  Completed in 1,812,075 msec ~ 30 min

-- update the idchemicalnew 
insert into property_values_tmp
SELECT t.id,t.idchemical,t.name,t.units,t.comments,t.text,value,title,url,type,
t.idreference,t.idproperty,s.idchemicalnew as idchemicalnew,idvalue FROM property_values_tmp t
join substance_relation_chemicals_tmp s on s.idchemical=t.idchemical
on duplicate key update idchemicalnew=values(idchemicalnew);

COMMIT;

-- and finally the values
insert ignore into property_values
SELECT null,idproperty,idstructure,idchemical,"guest","OK",text,idvalue,null,"STRING" FROM 
property_values_tmp
join structure using(idchemical);

COMMIT;

SET unique_checks=1;
SET foreign_key_checks=1;
SET autocommit=1;