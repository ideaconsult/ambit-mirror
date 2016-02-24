#AMBIT web application configuration guide 

[Download](./downloads.html) | [Installation guide](install_ambitrest.html) | [Configuration](configure.html) | [Authentication and authorisation](./configureaa.html)

##Authentication and authorisation	

AMBIT supports three modes for defining users and assigning access rights.

* OpenTox AA

* Local users database

* Read only, one admin user

The configuration file `config.prop` is found at 

````
{tomcat-dir}/webapps/ambit2/WEB-INF/classes/ambit2/rest/config/config.prop
````

An example [configuration file](./txt/config.prop) 

### OpenTox AA
Set `aa.enabled=true` to enable [OpenTox Authentication and Authorisation](./opentox_aa.html).
In this mode the user management and access control is handled by OpenTox Authentication and Authorization 
[service](http://opentox.org/dev/apis/api-1.2/AA)
which relies on a customized [OpenAM](https://en.wikipedia.org/wiki/OpenAM).

This mode enables federated authentication and authorisation.

### Read only, one admin user

Set `aa.local.enabled=true` to switch to this mode.

All resources are publicly readable, an user name and password is required
for importing data or running calculations (HTTP POST, PUT, DELETE are
protected via HTTP BASIC scheme). The user and password are defined in the
`aa.local.admin.name` and `aa.local.admin.pass` properties.

This mode is mostly useful for testing or quickly setting up a read only AMBIT instance.

### Local users database

Set `aa.db.enabled` to true to enable users from the AMBIT local users database.
This feature is introduced since AMBIT 2.5.5.

* IMPORTANT: `ambit_users` database should exist at `ambit.db.host` 

There is an user management user interface.

The eMail notification settings are introduced since AMBIT 2.5.5. 
The eMail server is used to send confirmation emails to newly registered users,
as well as confirmations for password resets. Please define a valid SMTP server.

The enableEmailVerification option is introduced since  
AMBIT 2.5.8. With email verification disabled anybody could register a new
user; however the user status have to be changed to `confirmed` by an admin
through the user management page. 

Since `ambit2` schema 8.6 and `ambit_users` schema 2.3 the tables may be imported 
into one common database.

