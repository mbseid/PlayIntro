# --- First database schema

# --- !Ups

create table account (
  id                	varchar(255) not null primary key,
  balance               decimal(10,2) not null,
  email                 varchar(255) not null
);

create table transaction (
  id                    bigint not null primary key,
  account               varchar(255) not null,
  amount                decimal(10,2) not null,
  dateDone                  timestamp,
  success				boolean,
  foreign key(account)  references account(id) on delete cascade
);

create sequence transaction_seq start with 1000;


# --- !Downs
drop sequence if exists transaction_seq;
drop table if exists transaction;
drop table if exists account;


