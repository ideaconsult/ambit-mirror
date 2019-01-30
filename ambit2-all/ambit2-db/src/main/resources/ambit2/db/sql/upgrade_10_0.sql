ALTER TABLE `substance_experiment` CHANGE COLUMN `resulttype` `resulttype` VARCHAR(32) NULL DEFAULT NULL  ;
ALTER TABLE `bundle_substance_experiment` ADD COLUMN `resulttype` VARCHAR(32) NULL DEFAULT NULL  AFTER `remarks` ;
ALTER TABLE `bundle_final_experiment` ADD COLUMN `resulttype` VARCHAR(32) NULL DEFAULT NULL  AFTER `remarks` ;

