-- -------------------------
-- Ambit schema 6.4
-- Additional fields in the query table
-- -------------------------
ALTER TABLE `query` ADD COLUMN `created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `idtemplate`;
ALTER TABLE `sessions` DROP FOREIGN KEY `FK_sessions_1`;
insert into version (idmajor,idminor,comment) values (6,4,"AMBIT2 schema");

