create table appluser (
    id int8 not null,
    password varchar(255),
    username varchar(255),
    language varchar(3),
    registration_date timestamp,
    primary key (id)
);

create unique index
    idx_appluser_username ON appluser(username);
