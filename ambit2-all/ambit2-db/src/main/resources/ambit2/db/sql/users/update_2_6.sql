DROP PROCEDURE IF EXISTS `deleteUser`;
DELIMITER $$

CREATE PROCEDURE deleteUser(IN uname VARCHAR(16))
LANGUAGE SQL
READS SQL DATA 
CONTAINS SQL

BEGIN

delete FROM user_registration where user_name=uname;
delete FROM user_roles where user_name=uname;
delete FROM users where user_name=uname;
delete o FROM user_organisation o, user u where o.iduser=u.iduser and username=uname;
delete o FROM user_project o, user u where o.iduser=u.iduser and username=uname;
delete FROM user where username=uname;

END $$

DELIMITER ; $$

drop view ulist;
create view  ulist as
select `u`.`username` AS `username`,`u`.`firstname` AS `firstname`,`u`.`lastname` AS `lastname`,`u`.`institute` AS `institute`,`u`.`email` AS `email`,`u`.`keywords` AS `keywords`,group_concat(`r`.`role_name` separator ',') AS `role`,`n`.`created` AS `created`,`n`.`confirmed` AS `confirmed`,`n`.`status` AS `status` from ((`user` `u` join `user_roles` `r` on((`u`.`username` = `r`.`user_name`))) join `user_registration` `n` on((`u`.`username` = `n`.`user_name`))) 
where role_name != "ambit_user"
group by `u`.`username` order by `n`.`created` desc,`u`.`institute`,`u`.`username`;

insert into version_users (idmajor,idminor,comment) values (2,6,"AMBITDB users");