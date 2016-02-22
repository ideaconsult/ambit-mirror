-- timestamp for substance
ALTER TABLE `substance` ADD COLUMN `timestamp` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP  AFTER `owner_name` ;

-- allow to hide of the composition
ALTER TABLE `substance_relation` ADD COLUMN `hidden` TINYINT NULL DEFAULT 0  AFTER `name` ;
ALTER TABLE `substance_relation` ADD INDEX `cmp-hidden` (`hidden` ASC) ;

-- hide compositions with the same names, leave the first only
insert into substance_relation (cmp_prefix,cmp_uuid,idsubstance,idchemical,relation,function)
select cmp_prefix,cmp_uuid,idsubstance,idchemical,relation,function
from substance_relation s1
join (
SELECT cmp_prefix as vcmp_prefix,cmp_uuid as vcmp_uuid,count(distinct(cmp_uuid)) c,idsubstance,name from substance_relation where name is not null and (name!="") group by idsubstance,name
) s2 using(idsubstance,name)
where c>1 and (cmp_uuid != vcmp_uuid)
on duplicate key update hidden=1;

insert into version (idmajor,idminor,comment) values (8,14,"AMBIT2 schema");