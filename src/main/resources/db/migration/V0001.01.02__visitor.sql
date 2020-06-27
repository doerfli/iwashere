create table visitor (
    id int8 not null,
    firstname varchar(256),
    lastname varchar(256),
    email varchar(256),
    phone varchar(32),
    registration_date timestamp not null,
    primary key (id)
);
