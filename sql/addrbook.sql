CREATE TABLE ADDRESSBOOK(
  userid varchar(255),
  nickname varchar(255),
  email varchar(255),
  phoneNumber varchar(255),
  primary key (userid,email),
  foreign key (userid) references users(username)
);