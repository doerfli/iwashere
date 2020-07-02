create table appluser (
    id int8 not null,
    username varchar(255) not null,
    password varchar(255) not null,
    password_changed_date timestamp not null,
    state varchar(32) not null,
    language varchar(3) not null,
    token varchar(64),
    created_date timestamp not null,
    primary key (id)
);

create unique index
    idx_appluser_username ON appluser(username);
create unique index
    idx_appluser_token ON appluser(token);
