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
 