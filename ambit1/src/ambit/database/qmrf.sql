CREATE DATABASE `qmrf` /*!40100 DEFAULT CHARACTER SET latin1 */;

DROP TABLE IF EXISTS `qmrf`.`qmrfalgorithms`;
CREATE TABLE  `qmrf`.`qmrfalgorithms` (
  `QmrfAlgorithmID` int(10) unsigned NOT NULL auto_increment,
  `QmrfAlgorithmName` varchar(255) default NULL,
  `QmrfPublicationID` int(10) unsigned default NULL,
  PRIMARY KEY  (`QmrfAlgorithmID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `qmrf`.`qmrfalgorithmtodoc`;
CREATE TABLE  `qmrf`.`qmrfalgorithmtodoc` (
  `QmrfAlgorithmID` int(10) unsigned NOT NULL,
  `QmrfDocID` int(10) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `qmrf`.`qmrfappdomainsoftware`;
CREATE TABLE  `qmrf`.`qmrfappdomainsoftware` (
  `SoftwareID` int(10) unsigned NOT NULL,
  `QmrfDocID` int(10) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `qmrf`.`qmrfauthors`;
CREATE TABLE  `qmrf`.`qmrfauthors` (
  `QmrfAuthorID` int(10) unsigned NOT NULL auto_increment,
  `QmrfAuthorName` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`QmrfAuthorID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `qmrf`.`qmrfauthortodoc`;
CREATE TABLE  `qmrf`.`qmrfauthortodoc` (
  `QmrfAuthorID` int(10) unsigned NOT NULL,
  `QmrfDocID` int(10) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `qmrf`.`qmrfbibliographypublicationtodoc`;
CREATE TABLE  `qmrf`.`qmrfbibliographypublicationtodoc` (
  `QmrfPublicationID` int(10) unsigned NOT NULL,
  `QmrfDocID` int(10) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `qmrf`.`qmrfdescriptors`;
CREATE TABLE  `qmrf`.`qmrfdescriptors` (
  `QmrfDescriptorID` int(10) unsigned NOT NULL auto_increment,
  `QmrfDescriptorName` varchar(255) default NULL,
  `QmrfDescriptorDescription` varchar(255) default NULL,
  `QmrfDescriptorSymbol` varchar(10) default NULL,
  `QmrfPublicationID` int(10) unsigned default NULL,
  PRIMARY KEY  (`QmrfDescriptorID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `qmrf`.`qmrfdescriptorsgenerationsoftwaretodoc`;
CREATE TABLE  `qmrf`.`qmrfdescriptorsgenerationsoftwaretodoc` (
  `SoftwareID` int(10) unsigned NOT NULL,
  `QmrfDocID` int(10) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `qmrf`.`qmrfdescriptortodoc`;
CREATE TABLE  `qmrf`.`qmrfdescriptortodoc` (
  `QmrfDescriptorID` int(10) unsigned NOT NULL,
  `QmrfDocID` int(10) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `qmrf`.`qmrfdocs`;
CREATE TABLE  `qmrf`.`qmrfdocs` (
  `QmrfDocID` int(10) unsigned NOT NULL auto_increment,
  `QmrfDocXML` text,
  `QSAR_title` text,
  `QSAR_models` text,
  `qmrf_date` text,
  `qmrf_date_revision` text,
  `qmrf_revision` text,
  `model_date` text,
  `info_availability` text,
  `related_models` text,
  `model_species` text,
  `endpoint_comments` text,
  `endpoint_units` text,
  `endpoint_variable` text,
  `endpoint_protocol` text,
  `endpoint_data_quality` text,
  `algorithm_type` text,
  `algorithm_comments` text,
  `algorithms_descriptors` text,
  `descriptors_selection` text,
  `descriptors_generation` text,
  `descriptors_chemicals_ratio` text,
  `app_domain_description` text,
  `app_domain_method` text,
  `applicability_limits` text,
  `other_info` text,
  `preprocessing` text,
  `goodness_of_fit` text,
  `loo` text,
  `lmo` text,
  `yscrambling` text,
  `bootstrap` text,
  `other_statistics` text,
  `validation_set_availability` text,
  `validation_set_data` text,
  `validation_set_descriptors` text,
  `validation_dependent_var_availability` text,
  `validation_other_info` text,
  `experimental_design` text,
  `validation_predictivity` text,
  `validation_assessment` text,
  `validation_comments` text,
  `mechanistic_basis` text,
  `mechanistic_basis_comments` text,
  `mechanistic_basis_info` text,
  `comments` text,
  `QMRF_number` text,
  `date_publication` text,
  `keywords` text,
  `summary_comments` text,
  PRIMARY KEY  (`QmrfDocID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `qmrf`.`qmrfendpoints`;
CREATE TABLE  `qmrf`.`qmrfendpoints` (
  `QmrfEndpointID` int(10) unsigned NOT NULL auto_increment,
  `QmrfEndpointName` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`QmrfEndpointID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `qmrf`.`qmrfendpointtodoc`;
CREATE TABLE  `qmrf`.`qmrfendpointtodoc` (
  `QmrfEndpointID` int(10) unsigned NOT NULL,
  `QmrfDocID` int(10) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `qmrf`.`qmrfmodelauthortodoc`;
CREATE TABLE  `qmrf`.`qmrfmodelauthortodoc` (
  `QmrfAuthorID` int(10) unsigned NOT NULL,
  `QrmfDocID` int(10) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `qmrf`.`qmrfpublications`;
CREATE TABLE  `qmrf`.`qmrfpublications` (
  `QmrfPublicationID` int(10) unsigned NOT NULL auto_increment,
  `QmrfPublicationTitle` varchar(255) NOT NULL default '',
  `QmrfPublicationUrl` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`QmrfPublicationID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `qmrf`.`qmrfpublicationtodoc`;
CREATE TABLE  `qmrf`.`qmrfpublicationtodoc` (
  `QmrfPublicationID` int(10) unsigned NOT NULL,
  `QmrfDocID` int(10) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `qmrf`.`qmrfroles`;
CREATE TABLE  `qmrf`.`qmrfroles` (
  `QmrfRoleID` int(10) unsigned NOT NULL auto_increment,
  `QmrfRole` varchar(45) NOT NULL default '',
  PRIMARY KEY  (`QmrfRoleID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `qmrf`.`qmrfsoftware`;
CREATE TABLE  `qmrf`.`qmrfsoftware` (
  `SoftwareID` int(10) unsigned NOT NULL auto_increment,
  `SoftwareName` varchar(255) NOT NULL default '',
  `SoftwareDescription` varchar(255) default NULL,
  `SoftwareUrl` varchar(255) default NULL,
  `SoftwareContact` varchar(255) default NULL,
  `SoftwareNumber` varchar(45) default NULL,
  PRIMARY KEY  (`SoftwareID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `qmrf`.`qmrfsoftwaretodoc`;
CREATE TABLE  `qmrf`.`qmrfsoftwaretodoc` (
  `SoftwareID` int(10) unsigned NOT NULL,
  `QmrfDocID` int(10) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `qmrf`.`qmrfusers`;
CREATE TABLE  `qmrf`.`qmrfusers` (
  `UserID` int(10) unsigned NOT NULL auto_increment,
  `UserPass` varchar(45) default NULL,
  `UserName` varchar(45) default NULL,
  `UserRole` varchar(45) default NULL,
  PRIMARY KEY  (`UserID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DELIMITER $$

DROP PROCEDURE IF EXISTS `qmrf`.`add_document`$$
 $$

DELIMITER ;

DELIMITER $$

DROP PROCEDURE IF EXISTS `qmrf`.`split_string`$$
 $$

DELIMITER ;


