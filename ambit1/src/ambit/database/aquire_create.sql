-- Should be run from the folder where AQUIRE data files reside
CREATE DATABASE IF NOT EXISTS aquire;
USE AQUIRE;

DROP TABLE IF EXISTS aquire;
DROP TABLE IF EXISTS chemicalinfo;
DROP TABLE IF EXISTS endpoints;
DROP TABLE IF EXISTS chemicals;
DROP TABLE IF EXISTS concentrationtypecodes;
DROP TABLE IF EXISTS effects;
DROP TABLE IF EXISTS endpoints;
DROP TABLE IF EXISTS exposuretypecodes;
DROP TABLE IF EXISTS reference;
DROP TABLE IF EXISTS species_common_names;
DROP TABLE IF EXISTS species_data;


--Reference
CREATE TABLE `reference` (
  `ReferenceNumber` int(11) unsigned NOT NULL,  
  `ReferenceDB` varchar(3) character set latin1 collate latin1_bin NOT NULL default '', 
  `ReferenceType` varchar(10) character set latin1 collate latin1_bin NOT NULL default '',
  `Author` varchar(120) character set latin1 collate latin1_bin NOT NULL default '',
  `Title` varchar(220) character set latin1 collate latin1_bin NOT NULL default '',
  `Source` varchar(255) character set latin1 collate latin1_bin NOT NULL default '',
  `PublicationYear` varchar(4) character set latin1 collate latin1_bin NOT NULL default '',
  PRIMARY KEY  (`ReferenceNumber`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

load data local infile "common_validation/reference.txt" into table reference fields terminated by "|"  IGNORE 1 LINES;

--exposuretypecodes.txt
CREATE TABLE `exposuretypecodes` (
  `code` char(4) NOT NULL,
  `description` varchar(64) character set latin1 collate latin1_bin NOT NULL default '',
  PRIMARY KEY  (`code`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

load data local infile "validation/exposuretypecodes.txt" into table exposuretypecodes fields terminated by "|"  IGNORE 1 LINES;


--ConcentrationTypeCodes.txt
CREATE TABLE `ConcentrationTypeCodes` (
  `code` char(2) NOT NULL,
  `description` varchar(64) character set latin1 collate latin1_bin NOT NULL default '',
  PRIMARY KEY  (`code`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

load data local infile "validation/ConcentrationTypeCodes.txt" into table ConcentrationTypeCodes fields terminated by "|"  IGNORE 1 LINES;


--species_commonname
CREATE TABLE  species_common_names (
  SpeciesID int(11) NOT NULL default '0',
  CommonName varchar(60) character set latin1 collate latin1_bin NOT NULL default '',
  PRIMARY KEY  (`SpeciesID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

load data local  infile "common_validation/species_common_names.txt" IGNORE into table species_common_names fields terminated by "|" IGNORE 1 LINES; 
--species_data
CREATE TABLE  species_data (
  `SpeciesID` int(11) NOT NULL default '0',
  `CommonName` varchar(60) collate latin1_bin NOT NULL default '',
  `LatinName` varchar(117) collate latin1_bin NOT NULL default '',
  `Kingdom` char(1) collate latin1_bin NOT NULL default '',
  PRIMARY KEY  (`SpeciesID`),
  KEY `CommonName` (`CommonName`),
  KEY `LatinName` (`LatinName`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1 COLLATE=latin1_bin;

load data local infile "common_validation/species_data_file.txt" IGNORE into table species_data fields terminated by "|" IGNORE 1 LINES;

--effect
CREATE TABLE `effects` (
  `code` char(10) NOT NULL,
  `definition` varchar(64) character set latin1 collate latin1_bin NOT NULL default '',
  PRIMARY KEY  (`code`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

load data local infile "common_validation/effects.txt" into table effects fields terminated by "|"  IGNORE 1 LINES;

--endpoints
CREATE TABLE `endpoints` (
  `code` char(10) NOT NULL,
  `definition` varchar(64) character set latin1 collate latin1_bin NOT NULL default '',
  PRIMARY KEY  (`code`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

load data local  infile "common_validation/endpoints.txt" IGNORE into table endpoints fields terminated by "|" IGNORE 1 LINES; 

--chemicals
CREATE TABLE `chemicals` (
  `cas` char(11) NOT NULL,
  `Chemical_Name` text character set latin1 collate latin1_bin NOT NULL default '',
  PRIMARY KEY  (`cas`),
  KEY chemname (`Chemical_Name`(760))
) ENGINE=MyISAM DEFAULT CHARSET=latin1;


load data local infile "common_validation/chemicals.txt" into table chemicals fields terminated by "|"  IGNORE 1 LINES;

--chemicalinfo
CREATE TABLE `chemicalinfo` (
  `AquireLocation` int(11) unsigned NOT NULL auto_increment,
  `RecordNumber` int(11) unsigned NOT NULL,
  `RecordStatus` int(6) unsigned NOT NULL,  
  `CASNumber` varchar(11) NOT NULL,
  `ChemicalGrade` varchar(10) NOT NULL,
  `Formulation` varchar(10) NOT NULL,    
  `RadioLabel` varchar(7) NOT NULL,    
  `Characteristics` varchar(31) NOT NULL, 
  `Purity` varchar(12) NOT NULL,        
  PRIMARY KEY  (`AquireLocation`,`RecordNumber`),
  KEY cas (`CASNumber`),
  KEY recordno (`RecordNumber`)  
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

load data local infile "chemicalinfo.txt" into table chemicalinfo fields terminated by "|"  IGNORE 1 LINES;


--aquire
CREATE TABLE `aquire` (
  `AquireLocation` int(11) unsigned NOT NULL auto_increment,
  `RecordStatus` int(6) unsigned NOT NULL,  
  `ReferenceNumber` int(11) unsigned NOT NULL,  
  `TestCAS` char(11) NOT NULL,
  `TestGrade` varchar(10) NOT NULL,
  `TestFormulation` varchar(10) NOT NULL,    
  `TestRadioLabel` varchar(7) NOT NULL,    
  `TestCharacteristics` varchar(31) NOT NULL, 
  `TestPurity` varchar(12) NOT NULL,        
  `SpeciesNumber` int(11) NOT NULL,        
  `DocumentationCode` int(11) NOT NULL,        
  `SpeciesLifestage` varchar(35) NOT NULL,    
  `LifestageCode` char(4) NOT NULL,         
  `Endpoint` char(8) NOT NULL,           
  `EndpointAssigned` char(1) NOT NULL,           
  `Effect` char(5) NOT NULL,           
  `Measurement` char(5) NOT NULL,           
  `EffectPercentMean` char(10) NOT NULL,             
  `EffectPercentMin` char(10) NOT NULL,             
  `EffectPercentMax` char(10) NOT NULL,                 
  `Tissue` char(5) NOT NULL,                   
  `StudyType` char(10) NOT NULL,                   
  `TestDurationOp` char(2) NOT NULL,                   
  `TestDuration` char(7) NOT NULL,                     
  `MinDurationOp` char(2) NOT NULL,                     
  `MinDuration` char(7) NOT NULL,                     
  `MaxDurationOp` char(2) NOT NULL,                         
  `MaxDuration` char(7) NOT NULL,                       
  `DurationUnits` char(3) NOT NULL,                         
  `Reviewer` char(3) NOT NULL,                           
  `TestLocation` char(7) NOT NULL,                           
  `WaterType` char(3) NOT NULL,                               
  `StudyControl` char(5) NOT NULL,                           
  `Trend` char(3) NOT NULL,                           
  `Concentration1Type` char(2) NOT NULL,                           
  `Ion1` char(10) NOT NULL,                           
  `Concentration1MeanOp` char(2) NOT NULL,                           
  `Concentration1Mean` char(10) NOT NULL,                               
  `Concentration1MinOp` char(2) NOT NULL,    
  `Concentration1Min` char(10) NOT NULL,    
  `Concentration1MaxOp` char(2) NOT NULL,    
  `Concentration1Max` char(10) NOT NULL,            
  `Concentration2Type` char(2) NOT NULL,            
  `Ion2` char(10) NOT NULL,            
  `Concentration2MeanOp` char(2) NOT NULL,            
  `Concentration2Mean` char(10) NOT NULL,                    
  `Concentration2MinOp` char(2) NOT NULL,   
  `Concentration2Min` char(10) NOT NULL,   
  `Concentration2MaxOp` char(2) NOT NULL,   
  `Concentration2Max` char(10) NOT NULL,           
  `ConcentrationUnits` char(15) NOT NULL,    
  `BCF1MeanOp` char(2) NOT NULL,    
  `BCF1Mean` char(10) NOT NULL,          
  `BCF1MinOp` char(2) NOT NULL,           
  `BCF1Min` char(10) NOT NULL,    
  `BCF1MaxOp` char(2) NOT NULL,    
  `BCF1Max` char(10) NOT NULL,    
  `BCF2MeanOp` char(2) NOT NULL,    
  `BCF2Mean` char(10) NOT NULL, 
  `BCF2MinOp` char(2) NOT NULL,           
  `BCF2Min` char(10) NOT NULL,    
  `BCF2MaxOp` char(2) NOT NULL,    
  `BCF2Max` char(10) NOT NULL,       
  `Significant` char(7) NOT NULL,  
  `StatisticalSignificance` char(20) NOT NULL,  
  `ExposureType` char(3) NOT NULL,  
  `TestMethod` char(3) NOT NULL,  
  `OrganicCarbonType` char(1) NOT NULL,  
  `Suitability` char(1) NOT NULL,  
  `CreationDate` char(30) NOT NULL,  
  `ModifyDate` char(30) NOT NULL,  
  `SignificantModifyDate` char(30) NOT NULL,  
  `DataPublishedDate` char(30) NOT NULL,  
  `valid` bool NOT NULL,    
  PRIMARY KEY  (`AquireLocation`),
  KEY  (`TestCAS`),  
  KEY (`Endpoint`),  
  KEY (`SpeciesNumber`),
  KEY (`Effect`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

load data local  infile "aquire.txt" into table aquire fields terminated by "|"  IGNORE 1 LINES;

