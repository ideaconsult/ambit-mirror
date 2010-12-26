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

