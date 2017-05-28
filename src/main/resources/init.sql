drop table datacourceconfig;
 
CREATE TABLE datacourceconfig
(
userid varchar(30),
dbmstype varchar(20),
dba char(1),
dsname varchar(30),
dburl varchar(250),
dbuser varchar(30),
dbpass varchar(20),
primary key(userid,dsname)
);
desc datacourceconfig;

drop table dataview;

CREATE TABLE dataview
(
dbmstype varchar(20),
viewtype varchar(5),
viewname varchar(30),
statement varchar(100),
filterdefault varchar(30),
charttype varchar(30),
primary key(dbmsType,viewtype,viewname)
);

desc dataview;


insert into datacourceconfig(userid,dbmstype,dba,dsname,dburl,dbuser,dbpass)
 values('lw','oracle-12c','T','test1','jdbc:oracle:thin:@//Dawn:1521/corcl','user1','oracle');
