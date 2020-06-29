create table location (
    id int8 not null,
    name varchar(256),
    shortname varchar(64),
    street varchar(512),
    zip varchar(10),
    city varchar(512),
    country varchar(512),
    created_date timestamp,
    user_id int8 not null,
    primary key (id),
    foreign key (user_id) REFERENCES appluser(id)
);

create unique index
    idx_location_shortname ON location(shortname);
create index
    idx_location_user_id ON location(user_id);
