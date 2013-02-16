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
