ALTER TABLE `chem_relation` ADD COLUMN `metric` DOUBLE AFTER `relation`;

ALTER TABLE `qsasheader` 
  ADD CONSTRAINT `id_srcdataset`
  FOREIGN KEY (`id_srcdataset` )
  REFERENCES `src_dataset` (`id_srcdataset` )
  ON DELETE CASCADE
  ON UPDATE CASCADE, 
  ADD CONSTRAINT `idproperty`
  FOREIGN KEY (`idproperty` )
  REFERENCES `properties` (`idproperty` )
  ON DELETE CASCADE
  ON UPDATE CASCADE
, ADD INDEX `id_srcdataset_idx` (`id_srcdataset` ASC) 
, ADD INDEX `idproperty_idx` (`idproperty` ASC) ;

insert into version (idmajor,idminor,comment) values (6,8,"AMBIT Schema: structure relations table");
