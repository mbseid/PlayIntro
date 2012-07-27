# --- First database schema

# --- !Ups

create table account (
  id                	varchar(255) not null primary key,
  balance               number not null,
  email                 varchar(255) not null
);

create table transaction (
  id                    bigint not null primary key,
  account               varchar(255) not null,
  amount                number not null,
  date                  timestamp,
  foreign key(account)  references account(id) on delete cascade
);

create sequence transaction_seq start with 1000;


# --- !Downs

drop table if exists account;
drop table if exists transaction;
drop sequence if exists transaction_seq;
