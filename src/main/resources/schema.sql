DROP ALL OBJECTS;

create table if not exists activitylog ( 
  id bigint NOT NULL AUTO_INCREMENT,
  process varchar(200) NOT NULL,
  processId varchar(200) NOT NULL,
  summary varchar(200) NULL,
  result  varchar(50)  NULL,
  error  varchar(max) NULL,
  clientip varchar(100) NULL,
  createdBy bigint,
  createdByGroup bigint,
  createdDate timestamp NOT NULL,
  lastModifiedBy bigint,
  lastModifiedDate timestamp NOT NULL,
  lastUpdatedByprocessId varchar(200) NULL,
  isdeleted bigint NOT NULL default 0,
  version bigint NOT NULL,
  PRIMARY KEY (Id)
);

create table if not exists activitylogDetails ( 
  id bigint NOT NULL AUTO_INCREMENT,
  entity varchar(100) NOT NULL,
  entityid bigint NOT NULL,
  entityrevision bigint NOT NULL,
  processid varchar(200) NULL,
  createdBy bigint,
  createdByGroup bigint,
  createdDate timestamp NOT NULL,
  lastModifiedBy bigint,
  lastModifiedDate timestamp NOT NULL,
  lastUpdatedByprocessId varchar(200) NULL,
  isdeleted bigint NOT NULL default 0,
  version bigint NOT NULL,
  PRIMARY KEY (Id)
);

create table if not exists persistent_logins ( 
  username varchar_ignorecase(100) not null, 
  series varchar(64) primary key, 
  token varchar(64) not null, 
  last_used timestamp not null
);

CREATE TABLE ARTICLES (
  id bigint NOT NULL AUTO_INCREMENT,
  title varchar(200) NOT NULL,
  category varchar(100) NULL,
  summary varchar(200) NULL,
  author varchar(100) NULL,
  createdBy bigint,
  createdByGroup bigint,
  createdDate timestamp NOT NULL,
  lastModifiedBy bigint,
  lastModifiedDate timestamp NOT NULL,
  lastUpdatedByprocessId varchar(200) NULL,
  isdeleted bigint NOT NULL default 0,
  version bigint NOT NULL,
  PRIMARY KEY (Id)
);

CREATE TABLE ARTICLES_AUD (
  id bigint NOT NULL,
  title varchar(200),
  title_mod boolean,
  category varchar(100),
  category_mod boolean,
  summary varchar(200),
  summary_mod boolean,
  author varchar(100),
  author_mod boolean,
  createdBy bigint,
  createdBy_mod boolean,
  createdByGroup bigint,
  createdByGroup_mod bigint,
  createdDate timestamp,
  createdDate_mod boolean,
  lastModifiedBy bigint,
  lastModifiedBy_mod boolean,
  lastModifiedDate timestamp,
  lastModifiedDate_mod boolean,
  version bigint,
  version_mod boolean,
  lastUpdatedByprocessId varchar(200),
  lastUpdatedByprocessId_mod boolean,
  isdeleted bigint,
  isdeleted_mod boolean,
  REV INTEGER NOT NULL,
  REVTYPE INTEGER NOT NULL,
  PRIMARY KEY (Id, REV)
);


CREATE TABLE CITATIONS (
  id bigint NOT NULL AUTO_INCREMENT,
  title varchar(200) NOT NULL,
  link varchar(100) NULL,
  citationtype varchar(200) NULL,
  createdBy bigint,
  createdByGroup bigint,
  createdDate timestamp NOT NULL,
  lastModifiedBy bigint,
  lastModifiedDate timestamp NOT NULL,
  lastUpdatedByprocessId varchar(200) NULL,
  isdeleted bigint NOT NULL default 0,
  version bigint NOT NULL,
  PRIMARY KEY (Id)
);

CREATE TABLE CITATIONS_AUD (
  id bigint NOT NULL,
  title varchar(200),
  title_mod boolean,
  link varchar(100) NULL,
  link_mod boolean,
  citationtype varchar(200) NULL,
  citationtype_mod boolean,  
  createdBy bigint,
  createdBy_mod boolean,
  createdByGroup bigint,
  createdByGroup_mod bigint,
  createdDate timestamp,
  createdDate_mod boolean,
  lastModifiedBy bigint,
  lastModifiedBy_mod boolean,
  lastModifiedDate timestamp,
  lastModifiedDate_mod boolean,
  version bigint,
  version_mod boolean,
  lastUpdatedByprocessId varchar(200),
  lastUpdatedByprocessId_mod boolean,
  isdeleted bigint,
  isdeleted_mod boolean,
  REV INTEGER NOT NULL,
  REVTYPE INTEGER NOT NULL,
  PRIMARY KEY (Id, REV)
);


CREATE TABLE ARTICLE_CITATION (
  article_id bigint not null, 
  citation_id bigint not null,
  constraint fk_article_citation_article foreign key(article_id) references articles(id),
  constraint fk_article_citation_citation foreign key(citation_id) references citations(id)
);


CREATE TABLE ARTICLE_CITATION_AUD (
  article_id bigint not null, 
  citation_id bigint not null,
  REV INTEGER NOT NULL,
  REVTYPE INTEGER NOT NULL,
  constraint fk_article_citation_article2 foreign key(article_id) references articles(id),
  constraint fk_article_citation_citation2 foreign key(citation_id) references citations(id)
);


CREATE TABLE ARTICLECOMMENTS (
  id bigint NOT NULL AUTO_INCREMENT,
  comment varchar(max) NOT NULL,
  article_id bigint NULL,
  createdBy bigint,
  createdByGroup bigint,
  createdDate timestamp NOT NULL,
  lastModifiedBy bigint,
  lastModifiedDate timestamp NOT NULL,
  lastUpdatedByprocessId varchar(200) NULL,
  isdeleted bigint NOT NULL default 0,
  version bigint NOT NULL,
  PRIMARY KEY (Id)
);

CREATE TABLE ARTICLECOMMENTS_AUD (
  id bigint NOT NULL,
  comment varchar(max) NULL,
  comment_mod boolean,
  article_id bigint NULL,
  article_id_mod boolean,
  createdBy bigint,
  createdBy_mod boolean,
  createdByGroup bigint,
  createdByGroup_mod bigint,
  createdDate timestamp,
  createdDate_mod boolean,
  lastModifiedBy bigint,
  lastModifiedBy_mod boolean,
  lastModifiedDate timestamp,
  lastModifiedDate_mod boolean,
  version bigint,
  version_mod boolean,
  lastUpdatedByprocessId varchar(200),
  lastUpdatedByprocessId_mod boolean,
  isdeleted bigint,
  isdeleted_mod boolean,
  REV INTEGER NOT NULL,
  REVTYPE INTEGER NOT NULL,
  PRIMARY KEY (Id, REV)
);

CREATE TABLE REVINFO (
    REV INTEGER GENERATED BY DEFAULT AS IDENTITY,
    REVTSTMP BIGINT,
    PRIMARY KEY (REV)
);

create table if not exists OAUTH_USERS ( 
  id bigint NOT NULL AUTO_INCREMENT,
  O_AUTH_ID varchar(250) NOT NULL UNIQUE,
  global_User_Id bigint not null,
  PRIMARY KEY (id)
);
 
create table if not exists GLOBAL_USERS ( 
  id bigint NOT NULL AUTO_INCREMENT,
  created datetime not null,
  PRIMARY KEY (id)
);

create table if not exists users(
    id bigint NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) unique not null,
    password VARCHAR(100) not null,
    passwordExpirationDate timestamp not null,
    enabled boolean not null,
    createdBy bigint,
    createdByGroup bigint,
    createdDate timestamp NOT NULL,
    lastModifiedBy bigint,
    lastModifiedDate timestamp NOT NULL,
    lastUpdatedByprocessId varchar(200) NULL,
    isdeleted bigint NOT NULL default 0,
    version bigint NOT NULL,
    primary key (id)
);

create table if not exists users_aud (
  id bigint NOT NULL,
  username VARCHAR(50) not null,
  username_mod boolean,
  password VARCHAR(100) not null,
  password_mod boolean,
  passwordExpirationDate timestamp not null,
  passwordExpirationDate_mod boolean,
  enabled boolean not null,
  enabled_mod boolean,
  createdBy bigint,
  createdBy_mod boolean,
  createdByGroup bigint,
  createdByGroup_mod bigint,
  createdDate timestamp,
  createdDate_mod boolean,
  lastModifiedBy bigint,
  lastModifiedBy_mod boolean,
  lastModifiedDate timestamp,
  lastModifiedDate_mod boolean,
  version bigint,
  version_mod boolean,
  lastUpdatedByprocessId varchar(200),
  lastUpdatedByprocessId_mod boolean,
  isdeleted bigint,
  isdeleted_mod boolean,
  REV INTEGER NOT NULL,
  REVTYPE INTEGER NOT NULL,
  PRIMARY KEY (Id, REV)
);

create table if not exists failedLoginAttempts(
    id bigint NOT NULL,
    username VARCHAR(50) not null,
    timestamp timestamp not null,
    primary key (id)
);

create table if not exists authorities (
    username VARCHAR(50) not null,
    authority VARCHAR(50) not null,
    constraint fk_authorities_users foreign key(username) references users(username)
);
create unique index ix_auth_username on authorities (username,authority);

create table if not exists persistent_logins ( 
  username varchar_ignorecase(100) not null, 
  series varchar(64) primary key, 
  token varchar(64) not null, 
  last_used timestamp not null 
);

create table if not exists roles ( 
  id bigint not null AUTO_INCREMENT primary key, 
  name varchar(64),
  hierarchyOrder int not null,
  createdBy bigint,
  createdByGroup bigint,
  createdDate timestamp NOT NULL,
  lastModifiedBy bigint,
  lastModifiedDate timestamp NOT NULL,
  lastUpdatedByprocessId varchar(200) NULL,
  isdeleted bigint NOT NULL default 0,
  version bigint NOT NULL
);

create table if not exists roles_aud ( 
  id bigint not null, 
  name varchar(64),
  name_mod boolean,
  hierarchyOrder int,
  hierarchyOrder_mod boolean,
  createdBy bigint,
  createdBy_mod boolean,
  createdByGroup bigint,
  createdByGroup_mod bigint,
  createdDate timestamp,
  createdDate_mod boolean,
  lastModifiedBy bigint,
  lastModifiedBy_mod boolean,
  lastModifiedDate timestamp,
  lastModifiedDate_mod boolean,
  version bigint,
  version_mod boolean,
  lastUpdatedByprocessId varchar(200),
  lastUpdatedByprocessId_mod boolean,
  isdeleted bigint,
  isdeleted_mod boolean,
  REV INTEGER NOT NULL,
  REVTYPE INTEGER NOT NULL,
  PRIMARY KEY (Id, REV)
);

create table if not exists user_role ( 
  user_id bigint not null, 
  role_id bigint not null,
  constraint fk_user_role_users foreign key(user_id) references users(id),
  constraint fk_user_role_roles foreign key(role_id) references roles(id)
);

CREATE TABLE user_role_AUD (
  user_id bigint not null, 
  role_id bigint not null,
  REV INTEGER NOT NULL,
  REVTYPE INTEGER NOT NULL,
  constraint fk_user_role_users2 foreign key(user_id) references users(id),
  constraint fk_user_role_roles2 foreign key(role_id) references roles(id)
);

create table if not exists groups ( 
  id bigint not null AUTO_INCREMENT primary key, 
  name varchar(64) ,
  createdBy bigint,
  createdByGroup bigint,
  createdDate timestamp NOT NULL,
  lastModifiedBy bigint,
  lastModifiedDate timestamp NOT NULL,
  lastUpdatedByprocessId varchar(200) NULL,
  isdeleted bigint NOT NULL default 0,
  version bigint NOT NULL
);

create table if not exists groups_aud ( 
  id bigint not null, 
  name varchar(64),
  name_mod boolean,
  createdBy bigint,
  createdBy_mod boolean,
  createdByGroup bigint,
  createdByGroup_mod bigint,
  createdDate timestamp,
  createdDate_mod boolean,
  lastModifiedBy bigint,
  lastModifiedBy_mod boolean,
  lastModifiedDate timestamp,
  lastModifiedDate_mod boolean,
  version bigint,
  version_mod boolean,
  lastUpdatedByprocessId varchar(200),
  lastUpdatedByprocessId_mod boolean,
  isdeleted bigint,
  isdeleted_mod boolean,
  REV INTEGER NOT NULL,
  REVTYPE INTEGER NOT NULL,
  PRIMARY KEY (Id, REV)
);

create table if not exists user_group ( 
  user_id bigint not null, 
  group_id bigint not null,
  constraint fk_user_group_users foreign key(user_id) references users(id),
  constraint fk_user_group_groups foreign key(group_id) references groups(id)
);

create table if not exists user_group_AUD ( 
  user_id bigint not null, 
  group_id bigint not null,
  REV INTEGER NOT NULL,
  REVTYPE INTEGER NOT NULL,
  constraint fk_user_group_users2 foreign key(user_id) references users(id),
  constraint fk_user_group_groups2 foreign key(group_id) references groups(id)
);

create table if not exists entityAccess ( 
  id bigint not null AUTO_INCREMENT primary key, 
  name varchar(64) unique not null,
  createdBy bigint,
  createdByGroup bigint,
  createdDate timestamp NOT NULL,
  lastModifiedBy bigint,
  lastModifiedDate timestamp NOT NULL,
  lastUpdatedByprocessId varchar(200) NULL,
  isdeleted bigint NOT NULL default 0,
  version bigint NOT NULL
);

create table if not exists entityAccess_aud ( 
  id bigint not null, 
  name varchar(64),
  name_mod boolean,
  createdBy bigint,
  createdBy_mod boolean,
  createdByGroup bigint,
  createdByGroup_mod bigint,
  createdDate timestamp,
  createdDate_mod boolean,
  lastModifiedBy bigint,
  lastModifiedBy_mod boolean,
  lastModifiedDate timestamp,
  lastModifiedDate_mod boolean,
  version bigint,
  version_mod boolean,
  lastUpdatedByprocessId varchar(200),
  lastUpdatedByprocessId_mod boolean,
  isdeleted bigint,
  isdeleted_mod boolean,
  REV INTEGER NOT NULL,
  REVTYPE INTEGER NOT NULL,
  PRIMARY KEY (Id, REV)
);

create table if not exists role_entityAccess (  
  id bigint not null, 
  role_id bigint not null,
  entityAccess_id bigint not null,
  readAccess boolean,
  createAccess boolean,
  updateAccess boolean,
  deleteAccess boolean,
  createdBy bigint,
  createdBy_mod boolean,
  createdByGroup bigint,
  createdByGroup_mod bigint,
  createdDate timestamp,
  createdDate_mod boolean,
  lastModifiedBy bigint,
  lastModifiedBy_mod boolean,
  lastModifiedDate timestamp,
  lastModifiedDate_mod boolean,
  version bigint,
  version_mod boolean,
  lastUpdatedByprocessId varchar(200),
  lastUpdatedByprocessId_mod boolean,
  isdeleted bigint,
  isdeleted_mod boolean,
  constraint fk_role_entityAccess_roles foreign key(role_id) references roles(id),
  constraint fk_role_entityAccess_entities foreign key(entityAccess_id) references entityAccess(id)
);

CREATE TABLE role_entityAccess_aud (
  id bigint not null,  
  role_id bigint null,
  role_id_mod boolean,
  entityAccess_id bigint null,
  entityAccess_id_mod boolean,
  readAccess boolean,
  readAccess_mod boolean,
  createAccess boolean,
  createAccess_mod boolean,
  updateAccess boolean,
  updateAccess_mod boolean,
  deleteAccess boolean,
  deleteAccess_mod boolean,
  createdBy bigint,
  createdBy_mod boolean,
  createdByGroup bigint,
  createdByGroup_mod bigint,
  createdDate timestamp,
  createdDate_mod boolean,
  lastModifiedBy bigint,
  lastModifiedBy_mod boolean,
  lastModifiedDate timestamp,
  lastModifiedDate_mod boolean,
  version bigint,
  version_mod boolean,
  lastUpdatedByprocessId varchar(200),
  lastUpdatedByprocessId_mod boolean,
  isdeleted bigint,
  isdeleted_mod boolean,
  REV INTEGER NOT NULL,
  REVTYPE INTEGER NOT NULL,
  constraint fk_role_entityAccess_roles2 foreign key(role_id) references roles(id),
  constraint fk_role_entityAccess_entities2 foreign key(entityAccess_id) references entityAccess(id)
);

create table if not exists privilege ( 
  id bigint not null primary key,
  name varchar(120) not null,
  createdBy bigint,
  createdByGroup bigint,
  createdDate timestamp NOT NULL,
  lastModifiedBy bigint,
  lastModifiedDate timestamp NOT NULL,
  lastUpdatedByprocessId varchar(200) NULL,
  isdeleted bigint NOT NULL default 0,
  version bigint NOT NULL
);

create table if not exists privilege_aud ( 
  id bigint not null primary key,
  name varchar(120) not null,
  name_mod boolean,
  createdBy bigint,
  createdBy_mod boolean,
  createdByGroup bigint,
  createdByGroup_mod bigint,
  createdDate timestamp,
  createdDate_mod boolean,
  lastModifiedBy bigint,
  lastModifiedBy_mod boolean,
  lastModifiedDate timestamp,
  lastModifiedDate_mod boolean,
  version bigint,
  version_mod boolean,
  lastUpdatedByprocessId varchar(200),
  lastUpdatedByprocessId_mod boolean,
  isdeleted bigint,
  isdeleted_mod boolean,
  REV INTEGER NOT NULL,
  REVTYPE INTEGER NOT NULL
);

create table if not exists roles_privileges ( 
  role_id bigint not null,
  privilege_id bigint not null, 
  constraint fk_user_role_privilege foreign key(privilege_id) references privilege(id),
  constraint fk_user_role_role foreign key(role_id) references roles(id)
);

create table if not exists roles_privileges_aud ( 
  role_id bigint not null,
  privilege_id bigint not null, 
  REV INTEGER NOT NULL,
  REVTYPE INTEGER NOT NULL,
  constraint fk_user_role_privilege2 foreign key(privilege_id) references privilege(id),
  constraint fk_user_role_role2 foreign key(role_id) references roles(id)
);

 CREATE SEQUENCE if not exists privileges_sequence
  START WITH 1
  INCREMENT BY 1;

 CREATE SEQUENCE if not exists roles_sequence
  START WITH 10
  INCREMENT BY 1;
  
 CREATE SEQUENCE if not exists groups_sequence
  START WITH 4
  INCREMENT BY 1;

 CREATE SEQUENCE if not exists activitylog_sequence
  START WITH 1
  INCREMENT BY 1;
  
 CREATE SEQUENCE if not exists activitylogDetails_sequence
  START WITH 1
  INCREMENT BY 1;
  
  CREATE SEQUENCE if not exists articles_sequence
  START WITH 17
  INCREMENT BY 1;
  
  CREATE SEQUENCE if not exists articles_citations_sequence
  START WITH 30
  INCREMENT BY 1;
  
  CREATE SEQUENCE if not exists articlecomments_sequence
  START WITH 30
  INCREMENT BY 1;

  CREATE SEQUENCE if not exists users_sequence
  START WITH 5
  INCREMENT BY 1;
  
  CREATE SEQUENCE if not exists failedLoginAttempts_sequence
  START WITH 1
  INCREMENT BY 1;

  CREATE SEQUENCE if not exists oauthusers_sequence
  START WITH 1
  INCREMENT BY 1;
  
  CREATE SEQUENCE if not exists globalusers_sequence
  START WITH 2
  INCREMENT BY 1;
  
  CREATE SEQUENCE if not exists entityAccess_sequence
  START WITH 2
  INCREMENT BY 1;
  
  CREATE SEQUENCE if not exists role_entityAccess_sequence
  START WITH 20
  INCREMENT BY 1;