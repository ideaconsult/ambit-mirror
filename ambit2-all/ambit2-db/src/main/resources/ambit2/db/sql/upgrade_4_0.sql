-- -----------------------------------------------------
-- version 4.0 
-- duplicate string values with case insensitive ones.
-- Should speed up case insensitive queries
-- -----------------------------------------------------
insert ignore into version (idmajor,idminor,comment) values (4,0,"AMBIT2 schema");

DROP trigger IF_EXISTS copy_history;
DROP table IF EXISTS  history;

CREATE TABLE IF NOT EXISTS `property_ci` (
  `id_ci` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `value_ci` varchar(255) NOT NULL,
  PRIMARY KEY (`id_ci`),
  UNIQUE KEY `Index_3` (`value_ci`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `summary_property_chemicals` (
  `idchemical` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_ci` int(10) unsigned NOT NULL,
  `idproperty` int(10) unsigned NOT NULL,
  PRIMARY KEY (`idchemical`,`id_ci`,`idproperty`),
  KEY `FK_ppci_2` (`id_ci`),
  KEY `FK_ppci_3` (`idproperty`),
  CONSTRAINT `FK_ppci_1` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_ppci_2` FOREIGN KEY (`id_ci`) REFERENCES `property_ci` (`id_ci`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_ppci_3` FOREIGN KEY (`idproperty`) REFERENCES `properties` (`idproperty`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

DELIMITER ;;

/*!50003 CREATE*/ /*!50020 DEFINER=`root`@`localhost`*/ /*!50003 PROCEDURE `copy_ci`()
    READS SQL DATA
BEGIN  
DECLARE no_more_rows BOOLEAN;  
DECLARE struc_id INTEGER; 
DECLARE prop_id INTEGER; 
DECLARE  val VARCHAR(255);  
DECLARE props CURSOR FOR 
SELECT structure.idchemical,idproperty,value from structure join property_values using(idstructure) 
join property_string using(idvalue_string) where idvalue_string is not null;  

DECLARE CONTINUE HANDLER FOR NOT FOUND     SET no_more_rows = TRUE;  

SELECT "open";  

INSERT IGNORE INTO property_ci (value_ci) SELECT value from property_string;  

OPEN props;  

SELECT "start loop";  
the_loop: LOOP  

FETCH props into struc_id,prop_id,val;  
IF no_more_rows THEN         
	CLOSE props;         
	LEAVE the_loop; 
END IF;  

INSERT IGNORE INTO summary_property_chemicals (idchemical,id_ci,idproperty) 
	SELECT struc_id,id_ci,prop_id FROM property_ci where value_ci = val;   
	
END LOOP the_loop;   

END */;;


DROP TRIGGER IF EXISTS insert_string_ci;;

CREATE TRIGGER insert_string_ci AFTER INSERT ON property_string
 FOR EACH ROW BEGIN
    INSERT IGNORE INTO property_ci (value_ci) values (NEW.value);
 END ;;
 
CREATE TRIGGER update_string_ci AFTER UPDATE ON property_string
 FOR EACH ROW BEGIN
    UPDATE property_ci set value_ci=NEW.value where value_ci=OLD.value;
 END ;;
 
 CREATE TRIGGER summary_chemical_prop_insert AFTER INSERT ON property_values
 FOR EACH ROW BEGIN
    INSERT IGNORE INTO summary_property_chemicals (idchemical,id_ci,idproperty) 
    	SELECT idchemical,id_ci,idproperty from property_ci
		JOIN structure
		JOIN properties
		WHERE
		NEW.idvalue_string is not null 
		and value_ci = (select value from property_string where idvalue_string=NEW.idvalue_string)
		and idstructure=NEW.idstructure
		and idproperty=NEW.idproperty;   
 END ;;

 CREATE TRIGGER summary_chemical_prop_update AFTER UPDATE ON property_values
 FOR EACH ROW BEGIN
    INSERT IGNORE INTO summary_property_chemicals (idchemical,id_ci,idproperty) 
    	SELECT idchemical,id_ci,idproperty from property_ci
		JOIN structure
		JOIN properties
		WHERE
		NEW.idvalue_string is not null 
		and value_ci = (select value from property_string where idvalue_string=NEW.idvalue_string)
		and idstructure=NEW.idstructure
		and idproperty=NEW.idproperty;   
 END ;;

DELIMITER ;

insert ignore into version (idmajor,idminor,comment) values (4,1,"AMBIT2 schema");
-----------------------------------------------------
-- speeds up retrieval of all properties of a chemical
-----------------------------------------------------
ALTER TABLE `summary_property_chemicals` ADD INDEX `Index_4`(`idchemical`, `idproperty`);


-----------------------------------------------------
-- speeds up retrieval of all properties of a dataset
-----------------------------------------------------

-----------------------------------------------------
-- A template entry, linked to every dataset
-----------------------------------------------------
ALTER TABLE `src_dataset` ADD COLUMN `idtemplate` INTEGER UNSIGNED AFTER `created`,
 ADD CONSTRAINT `FK_src_dataset_3` FOREIGN KEY `FK_src_dataset_3` (`idtemplate`)
    REFERENCES `template` (`idtemplate`)
    ON DELETE CASCADE
    ON UPDATE CASCADE;
    
-----------------------------------------------------
-- properties type
-----------------------------------------------------    
ALTER TABLE `template_def` ADD COLUMN `ptype` SET('STRING','NUMERIC') NOT NULL AFTER `order`;

-- ------------------------------------------------------------------------------------------
-- Triggers to duplicate info of properties used in a dataset via template/template_def table
-- ------------------------------------------------------------------------------------------
DELIMITER ;;

-- -----------------------------------------------------
-- Trigger to create template entry for a dataset
-- -----------------------------------------------------
CREATE TRIGGER insert_dataset_template BEFORE INSERT ON src_dataset
 FOR EACH ROW BEGIN
    INSERT IGNORE INTO template (name) values (NEW.name);
    SET NEW.idtemplate = (SELECT idtemplate FROM template where template.name=NEW.name);
END ;;

-- -----------------------------------------------------
-- Trigger to add property entry to template_def 
-- -----------------------------------------------------
CREATE TRIGGER insert_property_tuple AFTER INSERT ON property_tuples
 FOR EACH ROW BEGIN
    INSERT INTO template_def (idtemplate,idproperty,`order`,ptype) (
    SELECT idtemplate,idproperty,idproperty,idtype FROM
      (SELECT idtemplate FROM src_dataset join tuples using(id_srcdataset) WHERE idtuple=NEW.idtuple) a
      JOIN
      (SELECT idproperty,idtype from property_values WHERE id=NEW.id) b
    ) ON DUPLICATE KEY UPDATE ptype=concat(ptype,',',values(ptype));
 END ;;
DELIMITER ;
 

--------------------------------------------------------------------------------------------------------
-- Procedure to create entries in template/template_def tables, storing features per dataset
-- This is redundant information, but necessary to speed up queries retrieving properties for a dataset
--------------------------------------------------------------------------------------------------------
DELIMITER ;;

/*!50003 CREATE*/ /*!50020 DEFINER=`root`@`localhost`*/ /*!50003 PROCEDURE `copy_dataset_features`()
    READS SQL DATA
BEGIN  
DECLARE no_more_rows BOOLEAN;
DECLARE dataset_id INTEGER;
DECLARE dataset_name VARCHAR(255);
DECLARE template_id INTEGER;

DECLARE datasets CURSOR FOR
SELECT id_srcdataset,name,idtemplate FROM src_dataset ;

DECLARE CONTINUE HANDLER FOR NOT FOUND     SET no_more_rows = TRUE;  

SELECT "open";

OPEN datasets;

SELECT "start loop";
the_loop: LOOP

FETCH datasets into dataset_id,dataset_name,template_id;
IF no_more_rows THEN
	CLOSE datasets;
	LEAVE the_loop;
END IF;

SELECT dataset_name,dataset_id,template_id;

IF template_id IS NULL THEN
  INSERT IGNORE INTO template (idtemplate,name) values (null,dataset_name);

  UPDATE src_dataset, template SET src_dataset.idtemplate=template.idtemplate
  WHERE id_srcdataset=dataset_id AND template.name=src_dataset.name;
ELSE
  DELETE FROM template_def WHERE idtemplate= template_id;
END IF;

INSERT IGNORE into template_def (idtemplate,idproperty,`order`,ptype)
SELECT idtemplate,idproperty,idproperty,group_concat(distinct(idtype))
FROM property_values
JOIN property_tuples using(id)
JOIN tuples using (idtuple)
JOIN src_dataset using(id_srcdataset)
WHERE id_srcdataset=dataset_id
GROUP by idproperty;

END LOOP the_loop;   

END */;;

DELIMITER ;


---

ALTER TABLE `structure` MODIFY COLUMN `format` ENUM('SDF','CML','MOL','INC') NOT NULL DEFAULT 'SDF';

--- 4.3 -> 4.5

ALTER TABLE `models` ADD COLUMN `creator` VARCHAR(45) NOT NULL default 'guest' AFTER `hidden`,
 ADD INDEX `Index_creator`(`creator`);

ALTER TABLE `models` ADD COLUMN `dataset` VARCHAR(255) COMMENT 'dataset uri' AFTER `creator`,
 ADD INDEX `Index_10`(`dataset`);
 
ALTER TABLE `bookmark` MODIFY COLUMN `hasTopic` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'b:hasTopic Associates the bookmark with a Topic ';

--- 4.5 -> 4.6
ALTER TABLE `catalog_references` MODIFY COLUMN `type` ENUM('Unknown','Dataset','Algorithm','Model','BibtexEntry','BibtexArticle','BibtexBook','Feature') NOT NULL DEFAULT 'Dataset';

--- 4.6 -> 4.7
ALTER TABLE `properties` ADD COLUMN `ptype` SET('STRING','NUMERIC') NOT NULL DEFAULT '' AFTER `islocal`;
insert into properties (idproperty,ptype)
(
select idproperty,group_concat(distinct(ptype)) from template_def where ptype != "" group by idproperty
) on duplicate key update ptype=values(ptype);
ALTER TABLE `template_def` DROP COLUMN `ptype`;
DROP TRIGGER insert_property_tuple;
DROP TRIGGER summary_chemical_prop_insert;
DROP TRIGGER summary_chemical_prop_update;
-- -----------------------------------------------------
-- Trigger to add property entry to template_def 
-- -----------------------------------------------------
DELIMITER $
CREATE TRIGGER insert_property_tuple AFTER INSERT ON property_tuples
 FOR EACH ROW BEGIN
    INSERT IGNORE INTO template_def (idtemplate,idproperty,`order`) (
    SELECT idtemplate,idproperty,idproperty FROM
      (SELECT idtemplate FROM src_dataset join tuples using(id_srcdataset) WHERE idtuple=NEW.idtuple) a
      JOIN
      (SELECT idproperty from property_values WHERE id=NEW.id) b
    ) ;
 END $
 
CREATE TRIGGER summary_chemical_prop_insert AFTER INSERT ON property_values
 FOR EACH ROW BEGIN
    UPDATE properties set ptype=CONCAT_WS(',',ptype,NEW.idtype)  where idproperty=NEW.idproperty;
    
    INSERT IGNORE INTO summary_property_chemicals (idchemical,id_ci,idproperty) 
    	SELECT idchemical,id_ci,idproperty from property_ci
		JOIN structure
		JOIN properties
		WHERE
		NEW.idvalue_string is not null 
		and value_ci = (select value from property_string where idvalue_string=NEW.idvalue_string)
		and idstructure=NEW.idstructure
		and idproperty=NEW.idproperty;   
END $
 
  CREATE TRIGGER summary_chemical_prop_update AFTER UPDATE ON property_values
 FOR EACH ROW BEGIN
 	UPDATE properties set ptype=CONCAT_WS(',',ptype,NEW.idtype)  where idproperty=NEW.idproperty;
 
    INSERT IGNORE INTO summary_property_chemicals (idchemical,id_ci,idproperty) 
    	SELECT idchemical,id_ci,idproperty from property_ci
		JOIN structure
		JOIN properties
		WHERE
		NEW.idvalue_string is not null 
		and value_ci = (select value from property_string where idvalue_string=NEW.idvalue_string)
		and idstructure=NEW.idstructure
		and idproperty=NEW.idproperty;   
 END $
DELIMITER ;
insert into version (idmajor,idminor,comment) values (4,7,"AMBIT2 schema");


---------------------
-- 4.9 
-- Longer properties name / sameas fields to work with ToxML flattened hierarchy
---------------------

ALTER TABLE `properties` MODIFY COLUMN `name` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
 MODIFY COLUMN `comments` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL;
 insert into version (idmajor,idminor,comment) values (4,9,"AMBIT2 schema");

---------------------
-- 4.10
-- Datasets license URI
---------------------
ALTER TABLE `src_dataset` ADD COLUMN `licenseURI` VARCHAR(128) NOT NULL DEFAULT 'Unknown' AFTER `idtemplate`,
 ADD INDEX `Index_license`(`licenseURI`);
insert into version (idmajor,idminor,comment) values (4,10,"AMBIT2 schema"); 

---------------------
-- 4.11
-- Bookmark table index (order by date)
---------------------
alter table bookmark drop key `Index_3`;
alter table bookmark drop key `Index_2`;
alter table bookmark add key `Index_4` (`creator`,`hasTopic`,`date`) using btree;
alter table bookmark add key `Index_3` (`hasTopic`) using btree;
alter table bookmark add key `Index_2` (`creator`,`hasTopic`,`title`) using btree;
insert into version (idmajor,idminor,comment) values (4,11,"AMBIT2 schema - added table bookmark index");

---------------------
-- 4.12
-- Datasets rights holder
---------------------
ALTER TABLE `src_dataset` ADD COLUMN `rightsHolder` VARCHAR(128) NOT NULL AFTER `licenseURI`;
insert into version (idmajor,idminor,comment) values (4,12,"AMBIT2 schema - Datasets rights holder added"); 

---------------------
-- 4.13
-- User name for models
---------------------
ALTER TABLE `models` ADD COLUMN `user_name` VARCHAR(16) NOT NULL DEFAULT 'guest' AFTER `dataset`,
 ADD CONSTRAINT `FK_models_users` FOREIGN KEY `FK_models_users` (`user_name`)
    REFERENCES `users` (`user_name`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT;

 ALTER TABLE `structure`
 DROP FOREIGN KEY `fk_idchemical`;

ALTER TABLE `structure` ADD CONSTRAINT `fk_idchemical` FOREIGN KEY `fk_idchemical` (`idchemical`)
    REFERENCES `chemicals` (`idchemical`)
    ON DELETE RESTRICT
    ON UPDATE CASCADE;

insert into version (idmajor,idminor,comment) values (4,13,"AMBIT2 schema - user_name for models"); 

---------------------
-- 4.14
-- Property annotations table
---------------------
CREATE TABLE  `property_annotation` (
  `idproperty` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'subject',
  `rdf_type` varchar(45) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'Feature',
  `predicate` varchar(45) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'property',
  `object` text CHARACTER SET utf8 NOT NULL COMMENT 'object',
  PRIMARY KEY (`idproperty`,`rdf_type`,`predicate`,`object`(250)) USING BTREE,
  KEY `Index_2` (`predicate`,`object`(250)) USING BTREE,
  CONSTRAINT `FK_property_annotation_1` FOREIGN KEY (`idproperty`) REFERENCES `properties` (`idproperty`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

insert into version (idmajor,idminor,comment) values (4,14,"AMBIT2 schema - Property annotations table added");


---------------------
-- 5.0
-- Chemicals table changed. Hashcode replaced by InChI key. Redundant index on InChI field is dropped.
---------------------
ALTER TABLE `chemicals` 
CHANGE COLUMN `hashcode` `inchikey` VARCHAR(27) DEFAULT 0
, DROP INDEX `sinchi`
, DROP INDEX `ssmiles`
, DROP INDEX `idchemical`
, DROP INDEX `inchi`
, DROP INDEX `formula`
, DROP INDEX `hashcode`
, DROP INDEX `Index_8`,
 ADD INDEX `index_smiles` USING BTREE(`smiles`(760)),
 ADD INDEX `index_idchemical` USING BTREE(`idchemical`),
 ADD INDEX `index_inchi` USING BTREE(`inchi`(767)),
 ADD INDEX `index_formula` USING BTREE(`formula`),
 ADD INDEX `index_inchikey` USING BTREE(`inchikey`),
 ADD INDEX `index_label` USING BTREE(`label`);
 
-- -----------------------------------------------------
-- placeholder for dataset creator name 
-- ----------------------------------------------------- 
 ALTER TABLE `src_dataset` ADD COLUMN `maintainer` VARCHAR(45) NOT NULL DEFAULT 'Unknown' AFTER `rightsHolder`,
 ADD INDEX `Index_6`(`maintainer`);
 
 -- -----------------------------------------------------
-- Table `chem_relation` 
-- -----------------------------------------------------
 CREATE TABLE `chem_relation` (
  `idchemical1` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `idchemical2` INTEGER UNSIGNED NOT NULL,
  `relation` VARCHAR(64) NOT NULL,
  PRIMARY KEY (`idchemical1`, `idchemical2`, `relation`),
  CONSTRAINT `FK_chem_relation_1` FOREIGN KEY `FK_chem_relation_1` (`idchemical1`)
    REFERENCES `chemicals` (`idchemical`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `FK_chem_relation_2` FOREIGN KEY `FK_chem_relation_2` (`idchemical2`)
    REFERENCES `chemicals` (`idchemical`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

 insert into version (idmajor,idminor,comment) values (5,0,"AMBIT2 schema - Chemicals table changed");

-- -----------------------------------------------------
-- Double 14:8 format for numerical values changed to Double (16 significant digits) 
-- -----------------------------------------------------  
ALTER TABLE `property_values` MODIFY COLUMN `value_num` DOUBLE DEFAULT NULL, DROP INDEX `Index_2`;
ALTER TABLE `property_values` ADD INDEX `Index_2`(`value_num`);
insert into version (idmajor,idminor,comment) values (5,1,"AMBIT2 schema -num values format changed"); 

-- -----------------------------------------------------
-- Nano materials support schema 5.2
-- -----------------------------------------------------  
ALTER TABLE `structure` MODIFY COLUMN `format` ENUM('SDF','CML','MOL','INC','NANO') NOT NULL DEFAULT 'SDF',
MODIFY COLUMN `type_structure` ENUM('NA','MARKUSH','SMILES','2D no H','2D with H','3D no H','3D with H','optimized','experimental','NANO') NOT NULL DEFAULT 'NA';
insert into version (idmajor,idminor,comment) values (5,2,"AMBIT2 schema - Nano materials support added");

----------------------------------- 
-- Drop FK to users table ; Ambit schema 5.3
-----------------------------------
ALTER TABLE `structure` DROP INDEX `FK_structure_2`, DROP FOREIGN KEY `FK_structure_2`;
ALTER TABLE `models` DROP FOREIGN KEY `FK_models_users`;
ALTER TABLE `property_values` DROP FOREIGN KEY `FK_property_values_1`;
ALTER TABLE `property_pairstruc` DROP FOREIGN KEY `FK_relationship_struc_4`;
ALTER TABLE `src_dataset` DROP FOREIGN KEY `FK_src_dataset_1`;
ALTER TABLE `funcgroups` DROP FOREIGN KEY `FK_funcgroups_1`;
ALTER TABLE `quality_structure` DROP FOREIGN KEY `FK_quality_struc_2`;
insert into version (idmajor,idminor,comment) values (5,3,"AMBIT2 schema - FK to users table dropped");

----------------------------------- 
-- Updated xtab procedures to accept property id instead of name ; Ambit schema 5.4
-----------------------------------
-- -----------------------------------------------------
-- Generates sql given numerical and nominal property for a histogram of a numerical property 
-- -----------------------------------------------------

DROP FUNCTION IF EXISTS `sql_xtab`;
DELIMITER $$

CREATE FUNCTION sql_xtab(property_num INT,property_nom INT, query INT,bins DOUBLE) RETURNS TEXT READS SQL 
DATA 
begin
   set @x="";
   set @@group_concat_max_len=100000;
   select   concat(
                 'select  a-mod(a,',bins,') as \"bins',property_num,'\"\n'
             ,   group_concat(distinct
                     concat(
                         ', sum(','\n'
                     ,   '     if(b=\"',value,'\"\n'
                     ,   '       ,  1','\n'
                     ,   '       ,   0','\n'
                     ,   '       )\n'
                     ,   '     )'
                     ,   ' "',value,'"\n'
                     )
                     order by value
                     separator ''
                 )
,',sum(if(b is null,  1,   0)) "N/A" '
,'from (','\n'
,'		select a,b from','\n'
,'		(','\n'
,'		select value_num as a,idchemical from query_results','\n'
,'		join property_values using(idstructure) \n'
,'		where idproperty = ',property_num,' and idquery=',query,' and value_num is not null\n'
,'		group by idchemical,value_num','\n'
,'		) as X','\n'
,'		left join','\n'
,'		(','\n'
,'		select value as b,idchemical from query_results','\n'
,'		join property_values using(idstructure) join property_string using(idvalue_string) \n'
,'		where idproperty = ',property_nom,' and idquery=',query,'\n'
,'		group by idchemical,value_num','\n'
,'		) as Y','\n'
,'		using(idchemical)','\n'
,') as p','\n'
,'group by a-mod(a,',bins,')','\n'
             ) s
       into @x
    from query_results join property_values using(idstructure) left join property_string using(idvalue_string) 
    where idproperty = property_nom and idquery=query;

    return @x;
end $$

DELIMITER ;

-- -----------------------------------------------------
-- Generates sql given numerical and nominal property for a histogram of a numerical property 
-- -----------------------------------------------------
DROP FUNCTION IF EXISTS `sql_dataset_xtab`;
DELIMITER $$

CREATE FUNCTION sql_dataset_xtab(property_num INT,property_nom INT, dataset INT,bins DOUBLE) RETURNS TEXT 
READS SQL DATA 
begin
   DECLARE x TEXT;
   set @@group_concat_max_len=100000;
   select   concat(
                 'select  a-mod(a,',bins,') as \"bins',property_num,'\"\n'
             ,   group_concat(distinct
                     concat(
                         ', sum(','\n'
                     ,   '     if(b=\"',ifnull(text,value),'\"\n'
                     ,   '       ,  1','\n'
                     ,   '       ,   0','\n'
                     ,   '       )\n'
                     ,   '     )'
                     ,   ' "',value,'"\n'
                     )
                     order by ifnull(text,value)
                     separator ''
                 )
,',sum(if(b is null,  1,   0)) "N/A" '
,'from (','\n'
,'		select a,b from','\n'
,'		(','\n'
,'		select value_num as a,idchemical from struc_dataset \n'
,'		join property_values using(idstructure) \n'
,'		where idproperty = ',property_num,' and id_srcdataset=',dataset,' and value_num is not null\n'
,'		group by idchemical,value_num','\n'
,'		) as X','\n'
,'		left join','\n'
,'		(','\n'
,'		select ifnull(text,value) as b,idchemical from struc_dataset \n'
,'		join property_values using(idstructure) join property_string using(idvalue_string) \n'
,'		where idproperty = ',property_nom,' and id_srcdataset=',dataset,'\n'
,'		group by idchemical,value_num','\n'
,'		) as Y','\n'
,'		using(idchemical)','\n'
,') as p','\n'
,'group by a-mod(a,',bins,')','\n'
             ) s
       into @x
    from struc_dataset join property_values using(idstructure) left join property_string using(idvalue_string)
    where idproperty = property_nom and id_srcdataset=dataset;

    return @x;
end $$

DELIMITER ;

DROP PROCEDURE IF EXISTS `p_dataset_xtab`;
DELIMITER $$

-- -----------------------------------------------------
-- Generates sql given numerical and nominal property for a histogram of a numerical property 
-- -----------------------------------------------------
CREATE PROCEDURE p_dataset_xtab(IN property_num INT,property_nom INT,q INT,bins DOUBLE)
LANGUAGE SQL
READS SQL DATA 
CONTAINS SQL
SQL SECURITY DEFINER
begin
   set @x="";
   select  sql_dataset_xtab(property_num,property_nom,q,bins) into @x;
   prepare  xtab  from     @x;
   execute  xtab;
   deallocate prepare  xtab;
end $$

DELIMITER ;
-- -----------------------------------------------------
-- Generates cross tab view  given numerical and nominal property
-- -----------------------------------------------------
DROP PROCEDURE IF EXISTS `p_xtab`;
DELIMITER $$

CREATE PROCEDURE p_xtab(IN property_num INT,property_nom INT,q INT,bins DOUBLE)
LANGUAGE SQL
READS SQL DATA 
CONTAINS SQL
SQL SECURITY DEFINER
begin
   set @x="";
   select  sql_xtab(property_num,property_nom,q,bins) into @x;
   prepare  xtab  from     @x;
   execute  xtab;
   deallocate prepare  xtab;
end $$

DELIMITER ;

-- -----------------------------------------------------
-- Ambit schema 6.0
-- This procedure is now used on import - the import will fail if findByProperty() is not available!
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Find chemical by identifier or inchi. 
-- Search mode -- 0 : name ; 1 : alias; 2: idproperty ; 3: any ; 4: inchi ; 5: inchikey
-- Example call findByProperty(0,3,"http://www.opentox.org/api/1.1#CASRN","",0,"",0,"InChI=1/C9H14N2O3/c1-4-9(5-2)6(12)10-8(14)11(3)7(9)13/h4-5H2,1-3H3,(H,10,12,14)",0);
-- -----------------------------------------------------

DROP PROCEDURE IF EXISTS `findByProperty`;
DELIMITER $$
CREATE DEFINER=`root`@`localhost` PROCEDURE `findByProperty`(
                IN chemical_id INT,
                IN search_mode INT,  -- 0 : name ; 1 : alias; 2: idproperty ; 3: any ; 4: inchi ; 5: inchikey
                IN property_name VARCHAR(255),
                IN property_alias VARCHAR(255),
                IN property_id INT,
                IN query_value TEXT,  -- string value or inchi
                IN query_value_num DOUBLE,
                IN query_inchi TEXT,
                IN maxrows INT)
READS SQL DATA                
BEGIN
    DECLARE property_id INT DEFAULT -1;

    SET @found = null;
    IF (chemical_id<=0) THEN
      SET chemical_id = 0;
      IF (!isnull(query_value)) THEN
      -- text
          CASE search_mode
          WHEN 0 THEN
          select idchemical,idproperty,value_ci into chemical_id,property_id,@found  from properties join summary_property_chemicals using(idproperty)
            join property_ci p using(id_ci) where name = property_name and value_ci = query_value limit 1;
          WHEN 1 THEN
            select idchemical,idproperty,value_ci into chemical_id,property_id,@found  from properties join summary_property_chemicals using(idproperty)
            join property_ci using(id_ci) where comments = property_alias and value_ci = query_value limit 1;
          WHEN 2 THEN
            select idchemical,idproperty,value_ci into chemical_id,property_id,@found  from summary_property_chemicals
            join property_ci using(id_ci) where idproperty = property_id and value_ci = query_value limit 1;
          WHEN 3 THEN
            select idchemical,idproperty,value_ci into chemical_id,property_id,@found  from summary_property_chemicals
            join property_ci using(id_ci)  where value_ci = query_value limit 1;
          WHEN 5 THEN
            select idchemical,null,inchi into chemical_id,property_id,@found from chemicals where inchikey = query_inchi;
          ELSE BEGIN
            select idchemical,null,inchi into chemical_id,property_id,@found from chemicals where inchi = query_inchi;
          END;
          END CASE;
      ELSE
      -- numeric
          CASE search_mode
          WHEN 0 THEN
          select idchemical,idproperty,value_num into chemical_id,property_id,@found from properties join property_values using(idproperty)
            where name = property_name and value_num = query_value_num limit 1;
          WHEN 1 THEN
            select idchemical,idproperty,value_num into chemical_id,property_id,@found from properties join property_values using(idproperty)
            where comments = property_alias and value_num = query_value_num limit 1;
          WHEN 2 THEN
            select idchemical,idproperty,value_num into chemical_id,property_id,@found from property_values
            where idproperty = property_id and value_num = query_value_num limit 1;
          ELSE BEGIN
            select  idchemical,idproperty,value_num into chemical_id,property_id,@found from property_values where value_num = query_value_num limit 1;
          END;
          END CASE;
      END IF;
    END IF;

    -- fallback
    if ((chemical_id<=0) and !isnull(query_inchi)) THEN
      select idchemical,null,inchi into chemical_id,property_id,@found from chemicals where inchi = query_inchi;
    END IF;

    -- get the preferred structure
    IF (maxrows<=0) THEN
  	  	select 1,idchemical,idstructure,if(type_structure='NA',0,1) as selected, preference as metric,@found as text,property_id
        from structure where idchemical = chemical_id order by idchemical,preference,idstructure;
    ELSE
      	select 1,idchemical,idstructure,if(type_structure='NA',0,1) as selected, preference as metric,@found as text,property_id
        from structure where idchemical = chemical_id order by idchemical,preference,idstructure limit maxrows;
    END IF;

END $$

DELIMITER ;
 
-- -------------------------
-- Ambit schema 6.2
-- This should work under MySQL 5.1.
-- -------------------------

ALTER TABLE `chemicals` ADD COLUMN `lastmodified` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `label`;


DROP PROCEDURE IF EXISTS `findByProperty`;
DELIMITER $$
CREATE PROCEDURE `findByProperty`(
                IN chemical_id INT,
                IN search_mode INT,  -- 0 : name ; 1 : alias; 2: idproperty ; 3: any ; 4: inchi ; 5: inchikey
                IN property_name VARCHAR(255),
                IN property_alias VARCHAR(255),
                IN property_id INT,
                IN query_value TEXT,  -- string value or inchi
                IN query_value_num DOUBLE,
                IN query_inchi TEXT,
                IN maxrows INT)
    READS SQL DATA
BEGIN
    DECLARE property_id INT DEFAULT -1;
    SET @found = null;
    IF (chemical_id<=0) THEN
      SET chemical_id = 0;
      IF (!isnull(query_value)) THEN
      -- text
          CASE search_mode
          WHEN 0 THEN
          select idchemical,idproperty,value_ci into chemical_id,property_id,@found  from properties join summary_property_chemicals using(idproperty)
            join property_ci p using(id_ci) where name = property_name and value_ci = query_value limit 1;
          WHEN 1 THEN
            select idchemical,idproperty,value_ci into chemical_id,property_id,@found  from properties join summary_property_chemicals using(idproperty)
            join property_ci using(id_ci) where comments = property_alias and value_ci = query_value limit 1;
          WHEN 2 THEN
            select idchemical,idproperty,value_ci into chemical_id,property_id,@found  from summary_property_chemicals
            join property_ci using(id_ci) where idproperty = property_id and value_ci = query_value limit 1;
          WHEN 3 THEN
            select idchemical,idproperty,value_ci into chemical_id,property_id,@found  from summary_property_chemicals
            join property_ci using(id_ci)  where value_ci = query_value limit 1;
          WHEN 5 THEN
            select idchemical,null,inchi into chemical_id,property_id,@found from chemicals where inchikey = query_inchi limit 1;
          ELSE BEGIN
            select idchemical,null,inchi into chemical_id,property_id,@found from chemicals where inchi = query_inchi limit 1;
          END;
          END CASE;
      ELSE
      -- numeric
          CASE search_mode
          WHEN 0 THEN
          select idchemical,idproperty,value_num into chemical_id,property_id,@found from properties join property_values using(idproperty)
            where name = property_name and value_num = query_value_num limit 1;
          WHEN 1 THEN
            select idchemical,idproperty,value_num into chemical_id,property_id,@found from properties join property_values using(idproperty)
            where comments = property_alias and value_num = query_value_num limit 1;
          WHEN 2 THEN
            select idchemical,idproperty,value_num into chemical_id,property_id,@found from property_values
            where idproperty = property_id and value_num = query_value_num limit 1;
          ELSE BEGIN
            select  idchemical,idproperty,value_num into chemical_id,property_id,@found from property_values where value_num = query_value_num limit 1;
          END;
          END CASE;
      END IF;
    END IF;
    -- fallback
    if ((chemical_id<=0) and !isnull(query_inchi)) THEN
      select idchemical,null,inchi into chemical_id,property_id,@found from chemicals where inchi = query_inchi limit 1;
    END IF;
    -- get the preferred structure
    IF (maxrows<=0) THEN
  	  	select 1,idchemical,idstructure,if(type_structure='NA',0,1) as selected, preference as metric,@found as text,property_id
        from structure where idchemical = chemical_id order by idchemical,preference,idstructure;
    ELSE
      	select 1,idchemical,idstructure,if(type_structure='NA',0,1) as selected, preference as metric,@found as text,property_id
        from structure where idchemical = chemical_id order by idchemical,preference,idstructure limit 1;
    END IF;
END $$

DELIMITER ;
insert into version (idmajor,idminor,comment) values (6,2,"AMBIT2 schema");
 