-- larger maintainer field http://sourceforge.net/p/ambit/feature-requests/92/
ALTER TABLE `bundle` CHANGE COLUMN `maintainer` `maintainer` VARCHAR(128) CHARACTER SET 'utf8' COLLATE 'utf8_bin' NOT NULL DEFAULT 'Unknown'  ;

ALTER TABLE `ontobucket` CHANGE COLUMN `relation` `relation` ENUM('label','subclass','db','endpoint','endpointhash','hash','protocol','target','reference','substancetype','compound') CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT 'subclass'  ;

insert into version (idmajor,idminor,comment) values (8,13,"AMBIT2 schema");

