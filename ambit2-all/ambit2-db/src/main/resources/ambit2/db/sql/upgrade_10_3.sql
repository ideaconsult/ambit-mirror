DROP TABLE IF EXISTS `assay_template`;
CREATE TABLE `assay_template` (
  `endpoint` varchar(64) DEFAULT NULL,
  `assay` varchar(45) DEFAULT NULL,
  `row` int(11) DEFAULT NULL,
  `col` int(11) DEFAULT NULL,
  `idtemplate` varchar(45) DEFAULT NULL,
  `module` varchar(16) DEFAULT NULL,
  `level1` varchar(32) DEFAULT NULL,
  `level2` varchar(32) DEFAULT NULL,
  `level3` varchar(32) DEFAULT NULL,
  `value` varchar(192) DEFAULT NULL,
  `value_clean` varchar(192) DEFAULT NULL,
  `header1` varchar(80) DEFAULT NULL,
  `hint` varchar(160) DEFAULT NULL,
  `unit` varchar(32) DEFAULT NULL,
  `annotation` varchar(64) DEFAULT NULL,
  `file` varchar(32) DEFAULT NULL,
  `folder` varchar(32) DEFAULT NULL,
  `sheet` varchar(32) DEFAULT NULL,
  `visible` tinyint(4) DEFAULT '1',
  KEY `primary_index` (`idtemplate`,`row`,`col`),
  KEY `endpointx` (`endpoint`,`assay`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into version (idmajor,idminor,comment) values (10,3,"Assay templates");