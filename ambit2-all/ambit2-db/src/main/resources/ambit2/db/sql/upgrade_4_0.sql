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
