-- -------------------------
-- Ambit schema 6.5
-- Adjustments in the foreign key setup for structures
-- -------------------------
SELECT 'Update foreign keys in table struc_dataset' as 'Status';

ALTER TABLE `struc_dataset` DROP FOREIGN KEY `struc_dataset_ibfk_1` ;
ALTER TABLE `struc_dataset` 
  ADD CONSTRAINT `struc_dataset_ibfk_1`
  FOREIGN KEY (`idstructure` )
  REFERENCES `structure` (`idstructure` )
  ON DELETE RESTRICT
  ON UPDATE CASCADE;

SELECT 'Update foreign keys in table template_def'  as 'Status';
  
ALTER TABLE `template_def` DROP FOREIGN KEY `FK_template_def_2` ;
ALTER TABLE `template_def` 
  ADD CONSTRAINT `FK_template_def_2`
  FOREIGN KEY (`idproperty` )
  REFERENCES `properties` (`idproperty` )
  ON DELETE RESTRICT
  ON UPDATE CASCADE;
  
-- -----------------------------------------------------
-- Deletes a dataset and associated structures, if the structures are not in any other dataset
-- Delete is allowed only if the star field is <= maxstars!
-- -----------------------------------------------------

SELECT 'Create deleteDataset stored procedure'  as 'Status';

DROP PROCEDURE IF EXISTS `deleteDataset`;
DELIMITER $$

CREATE PROCEDURE deleteDataset(IN dataset INTEGER, IN maxstars INTEGER)
LANGUAGE SQL
READS SQL DATA 
CONTAINS SQL

BEGIN

DECLARE no_more_rows BOOLEAN;
DECLARE chemical INTEGER;
DECLARE propertyid INTEGER;

DECLARE thedataset CURSOR FOR
SELECT idstructure FROM struc_dataset join src_dataset using(id_srcdataset) where id_srcdataset=dataset and stars<=maxstars;

DECLARE theproperties CURSOR FOR
SELECT idproperty FROM src_dataset join template_def using(idtemplate) where id_srcdataset=dataset and stars<=maxstars;

DECLARE CONTINUE HANDLER FOR NOT FOUND     SET no_more_rows = TRUE;
--  Error: 1451 SQLSTATE: 23000 Foreign key constraint  
DECLARE CONTINUE HANDLER FOR SQLSTATE '23000' SET @x = @x + 1;

	SET @x = 0;
	 
	OPEN thedataset;
	
	the_loop: LOOP
	
		FETCH thedataset into chemical;
		IF no_more_rows THEN
			CLOSE thedataset;
			LEAVE the_loop;
		END IF;
	
		DELETE from struc_dataset where id_srcdataset=dataset and idstructure=chemical;
	-- this may fail because of foreign key constraints	
		DELETE from structure where idstructure=chemical;
	
	
	END LOOP the_loop;   
	
	-- now delete properties
	SET no_more_rows = FALSE;
	OPEN theproperties;
	
	prop_loop: LOOP
	
		FETCH theproperties into propertyid;
		IF no_more_rows THEN
			CLOSE theproperties;
			LEAVE prop_loop;
		END IF;
	
	-- this may fail because of foreign key constraints	
		DELETE from properties where idproperty=propertyid;
	
	END LOOP prop_loop;   
	
	-- finally delete the template and the src_dataset entry itself
	-- struc_dataset and template_def enjoy cascading delete
	DELETE d,t FROM src_dataset d, template t 
			WHERE d.idtemplate=t.idtemplate and id_srcdataset=dataset and stars<=maxstars;

END $$

DELIMITER ;

-- To be able to remove datasets
-- GRANT EXECUTE ON PROCEDURE `ambit2`.deleteDataset TO 'guest'@'localhost';
-- GRANT EXECUTE ON PROCEDURE `ambit2`.deleteDataset TO 'guest'@'127.0.0.1';
-- GRANT EXECUTE ON PROCEDURE `ambit2`.deleteDataset TO 'guest'@'::1';
  
insert into version (idmajor,idminor,comment) values (6,5,"AMBIT2 schema");   

SELECT 'DONE. Please run the following statements: '  as 'Status';

SELECT "GRANT EXECUTE ON PROCEDURE `ambit2`.deleteDataset TO 'guest'@'localhost'" as "TODO";
SELECT "GRANT EXECUTE ON PROCEDURE `ambit2`.deleteDataset TO 'guest'@'127.0.0.1'"  as "TODO";
SELECT "GRANT EXECUTE ON PROCEDURE `ambit2`.deleteDataset TO 'guest'@'::1'"  as "TODO";

