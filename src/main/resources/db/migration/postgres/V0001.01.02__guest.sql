create table guest (
    id int8 not null,
    name varchar(256),
    email varchar(256),
    phone varchar(32),
    created_date timestamp not null,
    primary key (id)
);
