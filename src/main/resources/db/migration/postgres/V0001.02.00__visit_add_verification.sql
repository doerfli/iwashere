alter table visit
    add column verified_email boolean not null,
    add column verified_phone boolean not null;
