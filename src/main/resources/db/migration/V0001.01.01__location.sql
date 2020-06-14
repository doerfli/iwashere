create table location (
    id int8 not null,
    name varchar(256),
    shortname varchar(64),
    street varchar(512),
    zip varchar(10),
    city varchar(512),
    country varchar(512),
    user_id int8 not null
);

create unique index
    idx_location_shortname ON location(shortname);
