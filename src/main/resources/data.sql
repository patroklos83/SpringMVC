

INSERT INTO ARTICLES (Id, title, category, author, summary, createdBy, createdDate, lastModifiedBy, lastModifiedDate, version) 
VALUES (1, 'Java Concurrency', 'Java', NULL, 'Sample article summary text bla bla bla ....', 2, CURRENT_TIMESTAMP(), NULL, CURRENT_TIMESTAMP(), 0);

/*
INSERT INTO ARTICLES (Id, title, category, author, summary, createdBy, createdDate, lastModifiedBy, lastModifiedDate, version) 
VALUES (2, 'Java Concurrency', 'Java', NULL, 'Sample article summary text bla bla bla ....', 2, CURRENT_TIMESTAMP(), NULL, CURRENT_TIMESTAMP(), 0);

INSERT INTO ARTICLES (Id, title, category, author, summary, createdBy, createdDate, lastModifiedBy, lastModifiedDate, version) 
VALUES (3, 'Java Concurrency', 'Java', NULL, 'Sample article summary text bla bla bla ....', 2, CURRENT_TIMESTAMP(), NULL, CURRENT_TIMESTAMP(), 0);

INSERT INTO ARTICLES (Id, title, category, author, summary, createdBy, createdDate, lastModifiedBy, lastModifiedDate, version) 
VALUES (4, 'Java Concurrency', 'Java', NULL, 'Sample article summary text bla bla bla ....', 2, CURRENT_TIMESTAMP(), NULL, CURRENT_TIMESTAMP(), 0);

INSERT INTO ARTICLES (Id, title, category, author, summary, createdBy, createdDate, lastModifiedBy, lastModifiedDate, version) 
VALUES (5, 'Java Concurrency', 'Java', NULL, 'Sample article summary text bla bla bla ....', 2, CURRENT_TIMESTAMP(), NULL, CURRENT_TIMESTAMP(), 0);

INSERT INTO ARTICLES (Id, title, category, author, summary, createdBy, createdDate, lastModifiedBy, lastModifiedDate, version) 
VALUES (6, 'Java Concurrency', 'Java', NULL, 'Sample article summary text bla bla bla ....', 2, CURRENT_TIMESTAMP(), NULL, CURRENT_TIMESTAMP(), 0);

INSERT INTO ARTICLES (Id, title, category, author, summary, createdBy, createdDate, lastModifiedBy, lastModifiedDate, version) 
VALUES (7, 'Java Concurrency', 'Java', NULL, 'Sample article summary text bla bla bla ....', 2, CURRENT_TIMESTAMP(), NULL, CURRENT_TIMESTAMP(), 0);

INSERT INTO ARTICLES (Id, title, category, author, summary, createdBy, createdDate, lastModifiedBy, lastModifiedDate, version) 
VALUES (8, 'Java Concurrency', 'Java', NULL, 'Sample article summary text bla bla bla ....', 2, CURRENT_TIMESTAMP(), NULL, CURRENT_TIMESTAMP(), 0);

INSERT INTO ARTICLES (Id, title, category, author, summary, createdBy, createdDate, lastModifiedBy, lastModifiedDate, version) 
VALUES (9, 'Java Concurrency', 'Java', NULL, 'Sample article summary text bla bla bla ....', 2, CURRENT_TIMESTAMP(), NULL, CURRENT_TIMESTAMP(), 0);

INSERT INTO ARTICLES (Id, title, category, author, summary, createdBy, createdDate, lastModifiedBy, lastModifiedDate, version) 
VALUES (10, 'Java Concurrency', 'Java', NULL, 'Sample article summary text bla bla bla ....', 2, CURRENT_TIMESTAMP(), NULL, CURRENT_TIMESTAMP(), 0);
*/
--INSERT INTO GLOBAL_USERS (created)
--values (SYSDATE);

-- Anonymous user 
INSERT INTO users (id, username, password, passwordExpirationDate, enabled, createdDate, lastModifiedDate, version)
  values (1,'anonymousUser',
    '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a',
    DATEADD(DAY, 1000, current_timestamp()), 1, current_timestamp(), current_timestamp(), 0);

-- User user/pass
INSERT INTO users (id, username, password, passwordExpirationDate, enabled, createdDate, lastModifiedDate, version)
  values (2,'user',
    '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a',
    DATEADD(DAY, 1, current_timestamp()), 1, current_timestamp(), current_timestamp(), 0);

/*    
INSERT INTO authorities (username, authority)
  values ('user', 'ROLE_USER');
INSERT INTO authorities (username, authority)
  values ('anonymousUser', 'ROLE_ANONYMOUS');
  */
  
 INSERT INTO roles (id, name, createdDate, lastModifiedDate, version) 
 VALUES 
(1, 'ROLE_ADMIN', current_timestamp(), current_timestamp(), 0),
(2, 'ROLE_ANONYMOUS', current_timestamp(), current_timestamp(), 0),
(3, 'ROLE_USER', current_timestamp(), current_timestamp(), 0);

insert into user_role(user_id, role_id) values
(1,2),
(2,1),
(2,3);

insert into privilege(id,name)
values
(1, 'CREATE'),
(2, 'READ'),
(3, 'UPDATE'),
(4, 'DELETE'),
(5, 'CANCEL'),
(6, 'SIGNUP'),
(7, 'RETRIEVE REVISION CHANGES');

--insert ROLE_USER CRUD PRIVILEGES
insert into roles_privileges(role_id, privilege_id)
values
(3, 1),
(3, 2),
(3, 3),
(3, 4),
(3, 5),
(2, 6),
(3, 7);

  