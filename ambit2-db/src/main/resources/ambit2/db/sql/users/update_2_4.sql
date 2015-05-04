ALTER TABLE `roles` CHANGE COLUMN `role_name` `role_name` VARCHAR(40) NOT NULL DEFAULT 'ambit_guest'  ;
ALTER TABLE `user_roles` 
  ADD CONSTRAINT `userfk`
  FOREIGN KEY (`user_name` )
  REFERENCES `users` (`user_name` )
  ON DELETE RESTRICT
  ON UPDATE CASCADE;
ALTER TABLE `policy` DROP FOREIGN KEY `fkrole1` ;
ALTER TABLE `policy` CHANGE COLUMN `role_name` `role_name` VARCHAR(40) NOT NULL DEFAULT 'ambit_guest'  , 
  ADD CONSTRAINT `fkrole1`
  FOREIGN KEY (`role_name` )
  REFERENCES `ambit_users`.`roles` (`role_name` )
  ON DELETE CASCADE
  ON UPDATE CASCADE;
  
ALTER TABLE `user_roles` DROP FOREIGN KEY `urolefk` ;
ALTER TABLE `user_roles` CHANGE COLUMN `role_name` `role_name` VARCHAR(40) NOT NULL  , 
  ADD CONSTRAINT `urolefk`
  FOREIGN KEY (`role_name` )
  REFERENCES `ambit_users`.`roles` (`role_name` )
  ON DELETE CASCADE
  ON UPDATE CASCADE;

insert into version_users (idmajor,idminor,comment) values (2,4,"AMBITDB users");

-- insert into roles select concat("B.",hex(bundle_number),".R") from cosing.bundle where bundle_number=unhex("26C7777DC80D11E4919080EE7350BFA7")
-- insert into policy select null,concat("B.",hex(bundle_number),".R"),"/ambit2",concat("/bundle/",hex(bundle_number)),2,1,1,1,1 from cosing.bundle where bundle_number=unhex("26C7777DC80D11E4919080EE7350BFA7")
-- insert into user_roles select "admin",concat("B.",hex(bundle_number),".R") from cosing.bundle where bundle_number=unhex("26C7777DC80D11E4919080EE7350BFA7")
-- insert into user_roles select "ngn",concat("B.",hex(bundle_number),".R") from cosing.bundle where bundle_number=unhex("26C7777DC80D11E4919080EE7350BFA7")
-- public
-- insert into policy values (null,"ambit_user","/ambit2","/bundle/26C7777DC80D11E4919080EE7350BFA7",2,1,0,0,0)

-- select prefix,resource,sum(mget) from policy join user_roles using(role_name) 
-- where user_name="ngn10" and prefix="/ambit2" and resource="/bundle/26C7777DC80D11E4919080EE7350BFA7" 
-- and mget=1 group by prefix,resource
