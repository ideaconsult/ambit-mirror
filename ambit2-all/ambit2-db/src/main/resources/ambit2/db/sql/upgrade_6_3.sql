-- -------------------------
-- Ambit schema 6.3
-- Star rating for models and datasets
-- -------------------------
ALTER TABLE `src_dataset` ADD COLUMN `stars` INTEGER UNSIGNED NOT NULL DEFAULT 5 AFTER `maintainer`;
ALTER TABLE `src_dataset` ADD INDEX `Index_7`(`stars`);
ALTER TABLE `models` ADD COLUMN `stars` INTEGER UNSIGNED NOT NULL DEFAULT 5 COMMENT 'stars rating' AFTER `user_name`,
	ADD INDEX `Index_12`(`stars`);
insert into version (idmajor,idminor,comment) values (6,3,"AMBIT2 schema");