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
DELIMITER; 
;

insert into version_users (idmajor,idminor,comment) values (2,6,"AMBITDB users");