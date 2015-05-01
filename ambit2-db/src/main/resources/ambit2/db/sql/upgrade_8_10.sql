-- enable case insensitive search by substance name
ALTER TABLE `substance` CHANGE COLUMN `publicname` `publicname` TEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL  ;
ALTER TABLE `substance` CHANGE COLUMN `name` `name` TEXT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL COMMENT 'Human readable name of the entry'  ;

-- add indices, will speed up LIKE value% queries
ALTER TABLE `substance` ADD INDEX `name-x` (`name`(128) ASC) , ADD INDEX `publicname-x` (`publicname`(128) ASC);

-- enable fulltext search by substance name - requires MySQL 5.6.4 or higher
-- ALTER TABLE `substance` ADD FULLTEXT INDEX `name-fulltext` (`name` ASC) ;
-- ALTER TABLE `substance` ADD FULLTEXT INDEX `publicname-fulltext` (`publicname` ASC) ;

insert into version (idmajor,idminor,comment) values (8,10,"AMBIT2 schema");
  